/*
 * Copyright 2011 Research Studios Austria Forschungsgesellschaft mBH
 *
 * This file is part of easyrec.
 *
 * easyrec is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * easyrec is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with easyrec.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.easyrec.plugin.slopeone.store.dao.impl;

import com.google.common.collect.Sets;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.set.TIntSet;
import org.easyrec.plugin.slopeone.model.Deviation;
import org.easyrec.plugin.slopeone.model.TenantItem;
import org.easyrec.plugin.slopeone.store.dao.DeviationDAO;
import org.easyrec.utils.spring.store.dao.DaoUtils;
import org.easyrec.utils.spring.store.dao.impl.AbstractTableCreatingDroppingDAOImpl;
import org.easyrec.utils.spring.store.service.sqlscript.SqlScriptService;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.object.SqlFunction;
import org.springframework.jdbc.object.SqlUpdate;

import javax.annotation.Nonnull;
import javax.sql.DataSource;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Blazing fast inserts with this implementation of DeviationDAO.
 * <p/>
 * This class merges the inserted deviations directly inside the database (therefore use only with non-merging
 * calculation strategy!).
 * <p/>
 * Works by inserting in java -> temp_table(mem) -> cache_table(mem) -> real table(InnoDB/MyISAM)
 * <p/>
 * The intermediate step from java->temp_table is needed because the data is inserted with LOAD DATA INFILE which
 * doesn't support merging (no ON DUPLICATE KEY UPDATE).
 * From the temp_table the data is then INSERT INTO cache_table SELECT ... ON DUPLICATE KEY UPDATE ... merged.
 * When the cache overflows 50% of the cache are written to the real table (again INSERT INTO ... SELECT ... ON
 * DUPLICATE KEY UPDATE.) (this is the slowest step!)
 * <p/>
 * <p/>
 * <p><b>Company:&nbsp;</b> SAT, Research Studios Austria</p>
 * <p><b>Copyright:&nbsp;</b> (c) 2007</p>
 * <p><b>last modified:</b><br/> $Author$<br/> $Date$<br/> $Revision$</p>
 *
 * @author Patrick Marschik
 */
public class DeviationDAOCachedMergingMySQL extends AbstractTableCreatingDroppingDAOImpl implements DeviationDAO {
    public static final int[] NO_TYPES = new int[0];
    private static final AtomicInteger estimatedCacheSize = new AtomicInteger(0);
    private static final int maxCacheSize = 7000000;

    private final DeviationQuery QUERY_GET_CACHED_DEVIATION;
    private final DeviationQuery QUERY_GET_DEVIATION;
    private final DeviationQuery QUERY_ORDER_DEVIATION;
    private final DeviationQuery QUERY_ORDER_DEVIATION_DENOM;
    private final DeviationQuery QUERY_ORDER_DEVIATION_LIMIT;
    private final DeviationQuery QUERY_ORDER_DEVIATION_DENOMLIMIT;
    private final MappingSqlQuery<Integer> QUERY_GET_ITEMIDS;
    private final SqlUpdate QUERY_INSERT_CACHED_DEVIATION;
    private final InsertTempDeviationsQuery QUERY_INSERT_TEMP_DEVIATIONS;
    private final FlushCacheQuery QUERY_FLUSH_CACHE;
    private final SqlFunction QUERY_GET_CACHE_SIZE;

    private static void writeDeviation(final Writer writer, final Deviation deviation) throws IOException {
        writer.write(String.valueOf(deviation.getTenantId()));
        writer.append('\t');
        writer.append(String.valueOf(deviation.getItem1Id()));
        writer.append('\t');
        writer.append(String.valueOf(deviation.getItem2Id()));
        writer.append('\t');
        writer.append(String.valueOf(deviation.getItem1TypeId()));
        writer.append('\t');
        writer.append(String.valueOf(deviation.getItem2TypeId()));
        writer.append('\t');
        writer.append(String.valueOf(deviation.getNumerator()));
        writer.append('\t');
        writer.append(String.valueOf(deviation.getDenominator()));
        writer.append('\n');
    }

    public DeviationDAOCachedMergingMySQL(DataSource dataSource, SqlScriptService sqlScriptService) {
        super(sqlScriptService);
        setDataSource(dataSource);

        QUERY_GET_CACHED_DEVIATION = new DeviationQuery(getDataSource(),
                "SELECT * FROM so_deviation_cache WHERE tenantId = ? AND item1TypeId = ? AND item2TypeId = ? AND item1Id = ? and item2Id = ? LIMIT 1");
        QUERY_GET_CACHED_DEVIATION.declareParameter(new SqlParameter("tenantId", Types.INTEGER));
        QUERY_GET_CACHED_DEVIATION.declareParameter(new SqlParameter("item1TypeId", Types.INTEGER));
        QUERY_GET_CACHED_DEVIATION.declareParameter(new SqlParameter("item2TypeId", Types.INTEGER));
        QUERY_GET_CACHED_DEVIATION.declareParameter(new SqlParameter("item1Id", Types.INTEGER));
        QUERY_GET_CACHED_DEVIATION.declareParameter(new SqlParameter("item2Id", Types.INTEGER));
        QUERY_GET_CACHED_DEVIATION.compile();

        QUERY_GET_DEVIATION = new DeviationQuery(getDataSource(),
                "SELECT * FROM so_deviation WHERE tenantId = ? AND item1TypeId = ? AND item2TypeId = ? AND item1Id = ? and item2Id = ? LIMIT 1");
        QUERY_GET_DEVIATION.declareParameter(new SqlParameter("tenantId", Types.INTEGER));
        QUERY_GET_DEVIATION.declareParameter(new SqlParameter("item1TypeId", Types.INTEGER));
        QUERY_GET_DEVIATION.declareParameter(new SqlParameter("item2TypeId", Types.INTEGER));
        QUERY_GET_DEVIATION.declareParameter(new SqlParameter("item1Id", Types.INTEGER));
        QUERY_GET_DEVIATION.declareParameter(new SqlParameter("item2Id", Types.INTEGER));
        QUERY_GET_DEVIATION.compile();

        QUERY_ORDER_DEVIATION = makeOrderedDeviationsQuery(getDataSource(), false, false);
        QUERY_ORDER_DEVIATION.compile();

        QUERY_ORDER_DEVIATION_DENOM = makeOrderedDeviationsQuery(getDataSource(), true, false);
        QUERY_ORDER_DEVIATION_DENOM.compile();

        QUERY_ORDER_DEVIATION_LIMIT = makeOrderedDeviationsQuery(getDataSource(), false, true);
        QUERY_ORDER_DEVIATION_LIMIT.compile();

        QUERY_ORDER_DEVIATION_DENOMLIMIT = makeOrderedDeviationsQuery(getDataSource(), true, true);
        QUERY_ORDER_DEVIATION_DENOMLIMIT.compile();

        QUERY_GET_ITEMIDS = new MappingSqlQuery<Integer>(getDataSource(),
                "SELECT item1Id AS item FROM so_deviation WHERE tenantId = ? AND item1TypeId = ?\n" +
                        "UNION SELECT item2Id AS item FROM so_deviation WHERE tenantId = ? AND item2TypeId = ?") {
            @Override
            protected Integer mapRow(final ResultSet rs, final int rowNum) throws SQLException {
                return rs.getInt("item");
            }
        };
        QUERY_GET_ITEMIDS.declareParameter(new SqlParameter("tenantId1", Types.INTEGER));
        QUERY_GET_ITEMIDS.declareParameter(new SqlParameter("item1TypeId", Types.INTEGER));
        QUERY_GET_ITEMIDS.declareParameter(new SqlParameter("tenantId2", Types.INTEGER));
        QUERY_GET_ITEMIDS.declareParameter(new SqlParameter("item2TypeId", Types.INTEGER));
        QUERY_GET_ITEMIDS.compile();

        QUERY_INSERT_CACHED_DEVIATION = new SqlUpdate(getDataSource(),
                "INSERT INTO so_deviation_cache(tenantId, item1Id, item2Id, item1TypeId, item2TypeId, numerator, denominator, written)\n" +
                        "        VALUES (?, ?, ?, ?, ?, ?, ?, b'0')\n" +
                        "ON DUPLICATE KEY UPDATE\n" +
                        "        numerator = numerator + VALUES(numerator),\n" +
                        "        denominator = denominator + VALUES(denominator),\n" +
                        "        written = b'0'",
                new int[]{Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.DOUBLE,
                        Types.BIGINT});
        QUERY_INSERT_CACHED_DEVIATION.compile();

        QUERY_INSERT_TEMP_DEVIATIONS = new InsertTempDeviationsQuery(getDataSource());

        QUERY_FLUSH_CACHE = new FlushCacheQuery(getDataSource());

        QUERY_GET_CACHE_SIZE = new SqlFunction(getDataSource(),
                "SELECT count(*) AS count FROM so_deviation_cache",
                NO_TYPES);
    }

    private static DeviationQuery makeOrderedDeviationsQuery(final DataSource dataSource,
                                                             final boolean checkForDenominator, final boolean limit) {
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT id, tenantId, item1Id, item2Id, item1TypeId, item2TypeId, numerator, denominator\n");
        sql.append("FROM ((\n");
        sql.append(
                "    SELECT id, tenantId, item1Id, item2Id, item1TypeId, item2TypeId, numerator, denominator, deviation AS dev\n");
        sql.append("    FROM so_deviation\n");
        sql.append("    WHERE tenantId = ? AND item1TypeId = ? AND item1Id = ?\n");
        if (checkForDenominator) sql.append("      AND denominator >= ?\n");
        sql.append("    ORDER BY deviation DESC\n");
        if (limit) sql.append("    LIMIT ?\n");
        sql.append("  ) UNION (\n");
        sql.append(
                "    SELECT id, tenantId, item2Id AS item1Id, item1Id AS item2Id, item1TypeId, item2TypeId, numerator * -1 AS numerator, denominator, deviation * -1 AS dev\n");
        sql.append("    FROM so_deviation USE INDEX (key_deviation_reverse)\n");
        sql.append("    WHERE tenantId = ? AND item2TypeId = ? AND item2Id = ?\n");
        if (checkForDenominator) sql.append("      AND denominator >= ?\n");
        sql.append("    ORDER BY deviation ASC\n");
        if (limit) sql.append("    LIMIT ?\n");
        sql.append("  )) AS so_deviation\n");
        sql.append("ORDER BY dev DESC\n");
        if (limit) sql.append("LIMIT ?");

        DeviationQuery query = new DeviationQuery(dataSource, sql.toString());

        query.declareParameter(new SqlParameter("tenantId1", Types.INTEGER));
        query.declareParameter(new SqlParameter("item1TypeId", Types.INTEGER));
        query.declareParameter(new SqlParameter("item1Id", Types.INTEGER));

        if (checkForDenominator) query.declareParameter(new SqlParameter("denominator1", Types.INTEGER));
        if (limit) query.declareParameter(new SqlParameter("limit1", Types.INTEGER));

        query.declareParameter(new SqlParameter("tenantId2", Types.INTEGER));
        query.declareParameter(new SqlParameter("item2TypeId", Types.INTEGER));
        query.declareParameter(new SqlParameter("item2Id", Types.INTEGER));
        if (checkForDenominator) query.declareParameter(new SqlParameter("denominator2", Types.INTEGER));
        if (limit) query.declareParameter(new SqlParameter("limit2", Types.INTEGER));
        if (limit) query.declareParameter(new SqlParameter("limit3", Types.INTEGER));

        return query;
    }

    private void createTable(String sqlFile, String tableName) {
        List<String> sqlCommands = getSqlScriptService().parseSqlScript("classpath:sql/plugins/slopeone/" + sqlFile);

        for (String sqlCommand : sqlCommands)
            getJdbcTemplate().execute(sqlCommand);

        if (logger.isInfoEnabled()) logger.info("successfully created table \"" + tableName + "\"");
    }

    public void starting() {
        dropTableIfExists("so_deviation_cache");
        dropTableIfExists("so_deviation_temp");

        createTable("DeviationCache.sql", "so_deviation_cache");
        createTable("DeviationTemp.sql", "so_deviation_temp");

        // sometimes the tables contain rows when they are recreated. therefore truncate them
        getJdbcTemplate().update("TRUNCATE so_deviation_cache");
        getJdbcTemplate().update("TRUNCATE so_deviation_temp");
    }

    public void endUpdate() {
        flushCache(getFlushSize());
    }

    public void finished(final int tenantId, final int itemTypeId) {
        dropTableIfExists("so_deviation_cache");
        dropTableIfExists("so_deviation_temp");
    }

    private void dropTableIfExists(final String tableName) {
        if (DaoUtils.existsTable(getDataSource(), tableName)) getJdbcTemplate().update("DROP TABLE " + tableName);
    }

    public synchronized Deviation getDeviation(final int tenantId, final int item1Id, final int item1TypeId,
                                               final int item2Id, int item2TypeId) {
        Object[] args = new Object[]{tenantId, item1TypeId, item2TypeId, item1Id, item2Id};

        List<Deviation> deviations = QUERY_GET_CACHED_DEVIATION.execute(args);

        if (deviations.size() > 0) return deviations.get(0);

        deviations = QUERY_GET_DEVIATION.execute(args);

        if (deviations.size() == 0) return null;

        Deviation result = deviations.get(0);
        insertCachedDeviation(result);

        return result;
    }

    public List<Deviation> getDeviationsOrdered(final int tenantId, final int itemTypeId, final int itemId,
                                                final Long minCountConstraint,
                                                final Integer maxNumberOfDeviationsConstraint) {
        Object[] args;
        DeviationQuery query;

        if (minCountConstraint != null && maxNumberOfDeviationsConstraint != null) {
            query = QUERY_ORDER_DEVIATION_DENOMLIMIT;
            args = new Object[]{tenantId, itemTypeId, itemId, minCountConstraint, maxNumberOfDeviationsConstraint,
                    tenantId, itemTypeId, itemId, minCountConstraint, maxNumberOfDeviationsConstraint,
                    maxNumberOfDeviationsConstraint};
        } else if (minCountConstraint != null) {
            query = QUERY_ORDER_DEVIATION_DENOM;
            args = new Object[]{tenantId, itemTypeId, itemId, minCountConstraint, tenantId, itemTypeId, itemId,
                    minCountConstraint};
        } else if (maxNumberOfDeviationsConstraint != null) {
            query = QUERY_ORDER_DEVIATION_LIMIT;
            args = new Object[]{tenantId, itemTypeId, itemId, maxNumberOfDeviationsConstraint, tenantId, itemTypeId,
                    itemId, maxNumberOfDeviationsConstraint, maxNumberOfDeviationsConstraint};
        } else {
            query = QUERY_ORDER_DEVIATION;
            args = new Object[]{tenantId, itemTypeId, itemId, tenantId, itemTypeId, itemId};
        }

        return query.execute(args);
    }

    @Nonnull
    public Set<TenantItem> getItemIds(final int tenantId, final TIntSet itemTypeIds) {
        TIntIterator iterator = itemTypeIds.iterator();
        Set<TenantItem> result = Sets.newHashSet();

        while (iterator.hasNext()) {
            int itemTypeId = iterator.next();
            Object[] args = new Object[]{tenantId, itemTypeId, tenantId, itemTypeId};
            List<Integer> itemIds = QUERY_GET_ITEMIDS.execute(args);

            for (Integer itemId : itemIds)
                result.add(new TenantItem(itemId, itemTypeId));
        }

        return result;
    }

    public synchronized int insertDeviation(final Deviation deviation) {
        return insertCachedDeviation(deviation);
    }

    public synchronized int insertDeviations(final List<Deviation> deviations) {
        File tempFile = null;
        BufferedWriter writer;

        try {
            tempFile = File.createTempFile("insert_temp_deviations", ".tmp");
            writer = new BufferedWriter(new FileWriter(tempFile));

            for (Deviation deviation : deviations)
                writeDeviation(writer, deviation);

            writer.close();

            return QUERY_INSERT_TEMP_DEVIATIONS.update(tempFile.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (tempFile != null) {
                if (!tempFile.delete() && logger.isWarnEnabled())
                    logger.warn("couldn't delete temp file: " + tempFile.getAbsolutePath());
            }
        }
    }

    @Override
    public String getDefaultTableName() { return "so_deviation"; }

    @Override
    public String getTableCreatingSQLScriptName() { return "classpath:sql/plugins/slopeone/Deviation.sql"; }

    @Override
    public void dropTable() {
        super.dropTable();

        dropTableIfExists("so_deviation_cache");

        if (DaoUtils.existsTable(getDataSource(), "so_deviation_temp"))
            getJdbcTemplate().update("DROP TABLE so_deviation_temp");
    }

    private int insertCachedDeviation(final Deviation deviation) {
        requestInsert(1);

        Object[] args = new Object[]{deviation.getTenantId(), deviation.getItem1Id(), deviation.getItem2Id(),
                deviation.getItem1TypeId(), deviation.getItem2TypeId(), deviation.getNumerator(),
                deviation.getDenominator()};

        estimatedCacheSize.incrementAndGet();

        return QUERY_INSERT_CACHED_DEVIATION.update(args);
    }

    private synchronized void requestInsert(int numberOfDeviations) {
        if (canInsert(numberOfDeviations)) return;

        updateEstimatedCacheSize();

        if (canInsert(numberOfDeviations)) return;

        flushCache(getFlushSize());
    }

    private void updateEstimatedCacheSize() {
        estimatedCacheSize.set(QUERY_GET_CACHE_SIZE.run());
    }

    private boolean canInsert(int numberOfDeviations) {
        return estimatedCacheSize.get() + numberOfDeviations < maxCacheSize;
    }

    private void flushCache(int numberOfItemsToRemove) {
        if (logger.isInfoEnabled()) logger.info("Flushing cache: " + numberOfItemsToRemove + " deviations");

        QUERY_FLUSH_CACHE.update(numberOfItemsToRemove);

        updateEstimatedCacheSize();
    }

    private int getFlushSize() {
        return Math.max(0, (int) (estimatedCacheSize.get() - (maxCacheSize * 0.5)));
    }

    private static class DeviationQuery extends MappingSqlQuery<Deviation> {
        public DeviationQuery(final DataSource ds, final String sql) {
            super(ds, sql);
        }

        @Override
        protected Deviation mapRow(final ResultSet rs, final int rowNum) throws SQLException {
            Integer id = null;

            try {
                id = DaoUtils.getInteger(rs, "id");
            } catch (SQLException ex) {
                logger.info("no id column found, assuming null since this row was in the cache table", ex);
            }

            int tenantId = rs.getInt("tenantId");
            int item1Id = rs.getInt("item1Id");
            int item2Id = rs.getInt("item2Id");
            int item1TypeId = rs.getInt("item1TypeId");
            int item2TypeId = rs.getInt("item2TypeId");
            double numerator = rs.getDouble("numerator");
            long denominator = rs.getLong("denominator");

            return new Deviation(id, tenantId, item1Id, item1TypeId, item2Id, item2TypeId, numerator, denominator);
        }
    }

    /**
     * Groups the operations to flush the cache table
     * 1. write rows from cache to 'real' table (only those not already written=1)
     * 2. reset all items where written=1 to written=0, denominator=0, numerator=0
     * 3. delete {numberOfItemsToRemove} items from cache table
     */
    private static class FlushCacheQuery {
        private final SqlUpdate moveCacheToTableQuery;
        private final SqlUpdate updateCacheWrittenQuery;
        private final SqlUpdate reduceCacheQuery;
        private final SqlUpdate disableKeysQuery;
        private final SqlUpdate enableKeysQuery;

        public FlushCacheQuery(final DataSource ds) {
            moveCacheToTableQuery = new SqlUpdate(ds,
                    "INSERT INTO so_deviation(tenantId, item1Id, item2Id, item1TypeId, item2TypeId, numerator, denominator, deviation)\n" +
                            "    SELECT tenantId, item1Id, item2Id, item1TypeId, item2TypeId, numerator, denominator, (numerator/denominator)\n" +
                            "        FROM so_deviation_cache\n" +
                            "        WHERE written=b'0'\n" +
                            "ON DUPLICATE KEY UPDATE\n" +
                            "        so_deviation.numerator = so_deviation.numerator + VALUES(so_deviation.numerator),\n" +
                            "        so_deviation.denominator = so_deviation.denominator + VALUES(so_deviation.denominator),\n" +
                            "        so_deviation.deviation = (so_deviation.numerator+VALUES(so_deviation.numerator))/(so_deviation.denominator + VALUES(so_deviation.denominator))",
                    NO_TYPES);
            moveCacheToTableQuery.compile();

            updateCacheWrittenQuery = new SqlUpdate(ds,
                    "UPDATE so_deviation_cache SET numerator = 0, denominator = 0, written = b'1' WHERE written = b'0'",
                    NO_TYPES);
            updateCacheWrittenQuery.compile();

            reduceCacheQuery = new SqlUpdate(ds,
                    "DELETE FROM so_deviation_cache WHERE written=b'1' LIMIT ?",
                    new int[]{Types.INTEGER});
            reduceCacheQuery.compile();

            disableKeysQuery = new SqlUpdate(ds,
                    "ALTER TABLE so_deviation DISABLE KEYS",
                    NO_TYPES);
            disableKeysQuery.compile();

            enableKeysQuery = new SqlUpdate(ds,
                    "ALTER TABLE so_deviation ENABLE KEYS",
                    NO_TYPES);
            enableKeysQuery.compile();
        }

        public int update(int numberOfItemsToRemove) {
            try {
                disableKeysQuery.update();
                moveCacheToTableQuery.update();
                updateCacheWrittenQuery.update();
                return reduceCacheQuery.update(numberOfItemsToRemove);
            } finally {
                enableKeysQuery.update();
            }
        }
    }

    /**
     * Inserts data with LOAD DATA INFILE (speed!)
     * But since LOAD DATA INFILE doesn't support ON DUPLICATE KEY UPDATE we need to load the data into a temporary
     * table from which we then can INSERT INTO SELECT FROM {temp table}
     */
    private static class InsertTempDeviationsQuery {
        private final SqlUpdate queryTruncateTempTable;
        private final SqlUpdate queryLoadDataInTempTable;
        private final SqlUpdate queryMoveDataFromTempToCache;

        public InsertTempDeviationsQuery(final DataSource ds) {
            queryTruncateTempTable = new SqlUpdate(ds,
                    "TRUNCATE so_deviation_temp",
                    NO_TYPES);
            queryLoadDataInTempTable = new SqlUpdate(ds,
                    "LOAD DATA LOCAL INFILE ? INTO TABLE so_deviation_temp(tenantId, item1Id, item2Id, item1TypeId, item2TypeId, numerator, denominator)",
                    new int[]{Types.VARCHAR});
            queryMoveDataFromTempToCache = new SqlUpdate(ds,
                    "INSERT INTO so_deviation_cache(tenantId, item1Id, item2Id, item1TypeId, item2TypeId, numerator, denominator, written)\n" +
                            "    SELECT tenantId, item1Id, item2Id, item1TypeId, item2TypeId, numerator, denominator, b'0'\n" +
                            "        FROM so_deviation_temp\n" +
                            "ON DUPLICATE KEY UPDATE\n" +
                            "        so_deviation_cache.numerator = so_deviation_cache.numerator + VALUES(so_deviation_cache.numerator),\n" +
                            "        so_deviation_cache.denominator = so_deviation_cache.denominator + VALUES" +
                            "(so_deviation_cache.denominator)",
                    NO_TYPES);
        }

        public int update(String file) {
            queryTruncateTempTable.update();

            int result = queryLoadDataInTempTable.update(file);

            queryMoveDataFromTempToCache.update();

            return result;
        }
    }
}
