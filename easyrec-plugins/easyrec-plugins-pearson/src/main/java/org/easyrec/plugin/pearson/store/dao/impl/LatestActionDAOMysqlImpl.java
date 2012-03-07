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

package org.easyrec.plugin.pearson.store.dao.impl;

import com.google.common.collect.ObjectArrays;
import com.google.common.primitives.Ints;
import org.easyrec.model.core.ItemVO;
import org.easyrec.model.core.RatingVO;
import org.easyrec.plugin.pearson.model.User;
import org.easyrec.plugin.pearson.store.dao.LatestActionDAO;
import org.easyrec.store.dao.core.ActionDAO;
import org.easyrec.utils.spring.cache.annotation.ShortCacheable;
import org.easyrec.utils.spring.store.dao.DaoUtils;
import org.easyrec.utils.spring.store.dao.annotation.DAO;
import org.easyrec.utils.spring.store.dao.impl.AbstractTableCreatingDAOImpl;
import org.easyrec.utils.spring.store.service.sqlscript.SqlScriptService;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.List;


/**
 * DOCUMENT ME!<p><b>Company:&nbsp;</b> SAT, Research Studios Austria</p>
 * <p><b>Copyright:&nbsp;</b> (c) 2009</p>
 * <p><b>last modified:</b><br/> $Author$<br/> $Date$<br/> $Revision$</p>
 *
 * @author Patrick Marschik
 */
@DAO
public class LatestActionDAOMysqlImpl extends AbstractTableCreatingDAOImpl implements LatestActionDAO {
    //~ Static fields/initializers /////////////////////////////////////////////////////////////////////////////////////

    private static final int PAGE_SIZE = 50000;
    private static final RowMapper defaultPreviousRatingMapper = new RatingRowMapper(
            DEFAULT_PREVIOUS_RATING_VALUE_COLUMN_NAME, DEFAULT_PREVIOUS_ACTION_TIME_COLUMN_NAME);
    private static RowMapper defaultRatedTogetherMapper = new RatedTogetherRowMapper();
    private static final RowMapper defaultRatingMapper = new RatingRowMapper(DEFAULT_RATING_VALUE_COLUMN_NAME,
            DEFAULT_ACTION_TIME_COLUMN_NAME);
    private static final String MAX_ACTION_TIME = "maxActionTime";
    private static final String TABLE_CREATING_SCRIPT_NAME = "classpath:sql/content/LatestAction.sql";

    //~ Constructors ///////////////////////////////////////////////////////////////////////////////////////////////////

    public LatestActionDAOMysqlImpl(final DataSource dataSource, final SqlScriptService sqlScriptService) {
        super(sqlScriptService);
        setDataSource(dataSource);
    }

    //~ Methods ////////////////////////////////////////////////////////////////////////////////////////////////////////

    @SuppressWarnings("unchecked")
    public List<ItemVO<Integer, Integer>> getAvailableItemsForTenant(final Integer tenantId,
                                                                              final Integer itemTypeId) {
        final StringBuilder query = new StringBuilder("SELECT DISTINCT ");
        query.append(DEFAULT_ITEM_COLUMN_NAME);
        query.append(" FROM ").append(DEFAULT_TABLE_NAME);
        query.append(" WHERE ");
        query.append(DEFAULT_TENANT_COLUMN_NAME).append("=? AND ");
        query.append(DEFAULT_ITEM_TYPE_COLUMN_NAME).append("=?");

        final Object[] args = new Object[]{tenantId, itemTypeId};
        final int[] argt = new int[]{Types.INTEGER, Types.INTEGER};

        final RowMapper mapper = new ItemRowMapper(tenantId, itemTypeId);

        return getJdbcTemplate().query(query.toString(), args, argt, mapper);
    }

    @SuppressWarnings("unchecked")
    public List<RatingVO<Integer, Integer>> getAverageRatingsForItem(final Integer tenantId,
                                                                                       final Integer itemTypeId) {
        final String query = averageRatingQueryString(DEFAULT_ITEM_COLUMN_NAME);

        final Object[] args = new Object[]{tenantId, itemTypeId};
        final int[] argt = new int[]{Types.INTEGER, Types.INTEGER};

        final RowMapper mapper = new RowMapper() {
            public Object mapRow(final ResultSet rs, final int rowNum) throws SQLException {
                final Integer itemId = DaoUtils.getInteger(rs, DEFAULT_ITEM_COLUMN_NAME);
                final Double averageRatingValue = DaoUtils.getDouble(rs, DEFAULT_RATING_VALUE_COLUMN_NAME);
                final Integer count = DaoUtils.getInteger(rs, "count");

                final ItemVO<Integer, Integer> item = new ItemVO<Integer, Integer>(tenantId, itemId,
                        itemTypeId);

                final RatingVO<Integer, Integer> rating =
                        new RatingVO<Integer, Integer>(
                                item, averageRatingValue, count, null);

                return rating;
            }
        };

        final List<RatingVO<Integer, Integer>> ratings = getJdbcTemplate()
                .query(query, args, argt, mapper);

        return ratings;
    }

    @SuppressWarnings("unchecked")
    public List<RatingVO<Integer, Integer>> getAverageRatingsForUser(final Integer tenantId,
                                                                                       final Integer itemTypeId) {
        final String query = averageRatingQueryString(DEFAULT_USER_COLUMN_NAME);

        final Object[] args = new Object[]{tenantId, itemTypeId};
        final int[] argt = new int[]{Types.INTEGER, Types.INTEGER};

        final RowMapper mapper = new RowMapper() {
            public Object mapRow(final ResultSet rs, final int rowNum) throws SQLException {
                final Integer userId = DaoUtils.getInteger(rs, DEFAULT_USER_COLUMN_NAME);
                final Double averageRatingValue = DaoUtils.getDouble(rs, DEFAULT_RATING_VALUE_COLUMN_NAME);
                final Integer count = DaoUtils.getInteger(rs, "count");

                final RatingVO<Integer, Integer> rating =
                        new RatingVO<Integer, Integer>(
                                null, averageRatingValue, count, null, userId);

                return rating;
            }
        };

        final List<RatingVO<Integer, Integer>> ratings = getJdbcTemplate()
                .query(query.toString(), args, argt, mapper);

        return ratings;
    }

    @Override
    public String getDefaultTableName() {
        return DEFAULT_TABLE_NAME;
    }

    @SuppressWarnings("unchecked")
    public List<ItemVO<Integer, Integer>> getItemsNotRatedByUser(final Integer tenantId, final Integer userId,
                                                                          final Integer itemTypeId) {
        final StringBuilder query = new StringBuilder("SELECT DISTINCT ");
        query.append(DEFAULT_ITEM_COLUMN_NAME).append("\n");
        query.append("FROM\n");
        query.append("    ").append(DEFAULT_TABLE_NAME).append("\n");
        query.append("WHERE\n");
        query.append("    ").append(DEFAULT_TENANT_COLUMN_NAME).append(" = ? AND\n");
        query.append("    ").append(DEFAULT_ITEM_TYPE_COLUMN_NAME).append(" = ? AND\n");
        query.append("    ").append(DEFAULT_ITEM_COLUMN_NAME);
        query.append(" NOT IN (SELECT DISTINCT ");
        query.append(DEFAULT_ITEM_COLUMN_NAME);
        query.append(" FROM ").append(DEFAULT_TABLE_NAME);
        query.append(" WHERE ");
        query.append(DEFAULT_TENANT_COLUMN_NAME).append(" = ? AND ");
        query.append(DEFAULT_USER_COLUMN_NAME).append(" = ? AND ");
        query.append(DEFAULT_ITEM_TYPE_COLUMN_NAME).append(" = ?)");

        final Object[] args = new Object[]{tenantId, itemTypeId, tenantId, itemTypeId, userId};
        final int[] argt = new int[]{Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER};

        final List<ItemVO<Integer, Integer>> items = getJdbcTemplate()
                .query(query.toString(), args, argt, new ItemRowMapper(tenantId, itemTypeId));

        return items;
    }

    @SuppressWarnings("unchecked")
    public List<RatedTogether<Integer, Integer>> getItemsRatedTogether(final Integer tenantId,
                                                                                         final Integer itemTypeId,
                                                                                         final Integer item1Id,
                                                                                         final Integer item2Id,
                                                                                         final Integer actionTypeId) {
        final StringBuilder query = createRatedTogetherQuery(DEFAULT_USER_COLUMN_NAME, DEFAULT_ITEM_COLUMN_NAME);

        final Object[] args = new Object[]{tenantId, itemTypeId, actionTypeId, item1Id, item2Id};
        final int[] argt = new int[]{Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER};

        final List<RatedTogether<Integer, Integer>> ratedTogether = getJdbcTemplate()
                .query(query.toString(), args, argt, defaultRatedTogetherMapper);

        return ratedTogether;
    }

    @SuppressWarnings("unchecked")
    public List<RatedTogether<Integer, Integer>> getItemsRatedTogetherByUsers(final Integer tenantId,
                                                                                                final Integer itemTypeId,
                                                                                                final Integer user1Id,
                                                                                                final Integer user2Id,
                                                                                                final Integer actionTypeId) {
        final StringBuilder query = createRatedTogetherQuery(DEFAULT_ITEM_COLUMN_NAME, DEFAULT_USER_COLUMN_NAME);

        final Object[] args = new Object[]{tenantId, itemTypeId, actionTypeId, user1Id, user2Id};
        final int[] argt = new int[]{Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER};

        final List<RatedTogether<Integer, Integer>> ratedTogether = getJdbcTemplate()
                .query(query.toString(), args, argt, defaultRatedTogetherMapper);

        return ratedTogether;
    }

    @SuppressWarnings("unchecked")
    public List<RatingVO<Integer, Integer>> getLatestRatingPage(int page, int tenantId,
                                                                                  int itemTypeId, Date since) {
        final StringBuilder query = new StringBuilder("SELECT ");
        appendAllColumns(query);
        query.append("\n");
        query.append("FROM ").append(DEFAULT_TABLE_NAME).append("\n");
        query.append("WHERE ");
        query.append(DEFAULT_TENANT_COLUMN_NAME).append(" = ? AND ");
        query.append(DEFAULT_ITEM_TYPE_COLUMN_NAME).append(" = ?");

        Object[] args = new Object[]{tenantId, itemTypeId};
        int[] argt = new int[]{Types.INTEGER, Types.INTEGER};

        if (since != null) {
            query.append(" AND ").append(DEFAULT_ACTION_TIME_COLUMN_NAME).append(" > ?");

            args = ObjectArrays.concat(args, since);
            argt = Ints.concat(argt, new int[]{Types.TIMESTAMP});
        }

        query.append("ORDER BY ").append(DEFAULT_ACTION_TIME_COLUMN_NAME).append(" ASC\n");
        query.append("LIMIT ?, ?");

        args = ObjectArrays.concat(args, new Object[]{page * PAGE_SIZE, PAGE_SIZE}, Object.class);
        argt = Ints.concat(argt, new int[]{Types.INTEGER, Types.INTEGER});

        return (List<RatingVO<Integer, Integer>>) getJdbcTemplate()
                .query(query.toString(), args, argt, defaultRatingMapper);
    }

    public int getLatestRatingPageCount(int tenantId, int itemTypeId, Date since) {
        final StringBuilder query = new StringBuilder("SELECT CEIL(count(*) / ?)");
        query.append("\n");
        query.append("FROM ").append(DEFAULT_TABLE_NAME).append("\n");
        query.append("WHERE ");
        query.append(DEFAULT_TENANT_COLUMN_NAME).append(" = ? AND ");
        query.append(DEFAULT_ITEM_TYPE_COLUMN_NAME).append(" = ?");

        Object[] args = new Object[]{PAGE_SIZE, tenantId, itemTypeId};
        int[] argt = new int[]{Types.INTEGER, Types.INTEGER, Types.INTEGER};

        if (since != null) {
            query.append(" AND ").append(DEFAULT_ACTION_TIME_COLUMN_NAME).append(" > ?");

            args = ObjectArrays.concat(args, since);
            argt = Ints.concat(argt, new int[]{Types.TIMESTAMP});
        }

        int count = getJdbcTemplate().queryForInt(query.toString(), args, argt);

        return count;
    }

    public static String averageRatingQueryString(final String groupByColumn) {
        final StringBuilder query = new StringBuilder("SELECT ");
        query.append(groupByColumn);
        query.append(", AVG(").append(DEFAULT_RATING_VALUE_COLUMN_NAME);
        query.append(") AS ").append(DEFAULT_RATING_VALUE_COLUMN_NAME);
        query.append(", COUNT(").append(DEFAULT_RATING_VALUE_COLUMN_NAME);
        query.append(") AS count FROM ").append(DEFAULT_TABLE_NAME);
        query.append(" WHERE ");
        query.append(DEFAULT_TENANT_COLUMN_NAME).append(" = ? AND ");
        query.append(DEFAULT_ITEM_TYPE_COLUMN_NAME).append(" = ? GROUP BY ");
        query.append(groupByColumn);

        return query.toString();
    }

    public Date getLatestRatingTimeForTenant(final Integer tenantId) {
        final StringBuilder query = new StringBuilder("SELECT max(");
        query.append(DEFAULT_ACTION_TIME_COLUMN_NAME);
        query.append(") FROM ").append(DEFAULT_TABLE_NAME);
        query.append(" WHERE ").append(DEFAULT_TENANT_COLUMN_NAME).append(" = ?");

        final Object[] args = new Object[]{tenantId};
        final int[] argt = new int[]{Types.INTEGER};

        return (Date) getJdbcTemplate().queryForObject(query.toString(), args, argt, Date.class);
    }

    @SuppressWarnings("unchecked")
    @ShortCacheable
    public List<RatingVO<Integer, Integer>> getLatestRatingsForTenant(final Integer tenantId,
                                                                                        final Integer itemTypeId,
                                                                                        final Integer itemId,
                                                                                        final Integer userId,
                                                                                        final Date since) {
        final StringBuilder query = new StringBuilder("SELECT ");
        appendAllColumns(query);
        query.append("\n");
        query.append("FROM ").append(DEFAULT_TABLE_NAME).append("\n");
        query.append("WHERE ");
        query.append(DEFAULT_TENANT_COLUMN_NAME).append(" = ? AND ");
        query.append(DEFAULT_ITEM_TYPE_COLUMN_NAME).append(" = ?");

        Object[] args = new Object[]{tenantId, itemTypeId};
        int[] argt = new int[]{Types.INTEGER, Types.INTEGER};

        if (itemId != null) {
            query.append(" AND ").append(DEFAULT_ITEM_COLUMN_NAME).append(" = ?");

            args = ObjectArrays.concat(args, itemId);
            argt = Ints.concat(argt, new int[]{Types.INTEGER});
        }

        if (userId != null) {
            query.append(" AND ").append(DEFAULT_USER_COLUMN_NAME).append(" = ?");

            args = ObjectArrays.concat(args, userId);
            argt = Ints.concat(argt, new int[]{Types.INTEGER});
        }

        if (since != null) {
            query.append(" AND ").append(DEFAULT_ACTION_TIME_COLUMN_NAME).append(" > ?");

            args = ObjectArrays.concat(args, since);
            argt = Ints.concat(argt, new int[]{Types.TIMESTAMP});
        }

        return getJdbcTemplate().query(query.toString(), args, argt, defaultRatingMapper);
    }

    @Override
    public String getTableCreatingSQLScriptName() {
        return TABLE_CREATING_SCRIPT_NAME;
    }

    @SuppressWarnings("unchecked")
    public List<RatingVO<Integer, Integer>> getUpdatedRatingsForTenant(final Integer tenantId,
                                                                                         final Integer itemTypeId,
                                                                                         final Integer actionTypeId,
                                                                                         final Date since) {
        final StringBuilder query = new StringBuilder("SELECT ");
        query.append(DEFAULT_ID_COLUMN_NAME).append(", ");
        query.append(DEFAULT_TENANT_COLUMN_NAME).append(", ");
        query.append(DEFAULT_USER_COLUMN_NAME).append(", ");
        query.append(DEFAULT_ITEM_COLUMN_NAME).append(", ");
        query.append(DEFAULT_ITEM_TYPE_COLUMN_NAME).append(", ");
        query.append(DEFAULT_ACTION_TYPE_COLUMN_NAME).append(", ");
        query.append(DEFAULT_PREVIOUS_RATING_VALUE_COLUMN_NAME).append(", ");
        query.append(DEFAULT_PREVIOUS_ACTION_TIME_COLUMN_NAME).append("\n");
        query.append("FROM ").append(DEFAULT_TABLE_NAME).append("\n");
        query.append("WHERE\n");
        query.append("    ").append(DEFAULT_TENANT_COLUMN_NAME).append(" = ? AND\n");
        query.append("    ").append(DEFAULT_ITEM_TYPE_COLUMN_NAME).append(" = ? AND\n");
        query.append("    ").append(DEFAULT_ACTION_TYPE_COLUMN_NAME).append(" = ? AND\n");
        query.append("    ").append(DEFAULT_ACTION_TIME_COLUMN_NAME).append(" > ?");

        final Object[] args = new Object[]{tenantId, itemTypeId, actionTypeId, since};
        final int[] argt = new int[]{Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.TIMESTAMP};

        return getJdbcTemplate().query(query.toString(), args, argt, defaultPreviousRatingMapper);
    }

    @SuppressWarnings("unchecked")
    public List<User> getUsersThatRatedItem(final Integer tenantId, final Integer itemId, final Integer itemTypeId) {
        final StringBuilder query = new StringBuilder("SELECT DISTINCT ");
        query.append(DEFAULT_USER_COLUMN_NAME);
        query.append(" FROM ").append(DEFAULT_TABLE_NAME);
        query.append(" WHERE ");
        query.append(DEFAULT_TENANT_COLUMN_NAME).append(" = ? AND ");
        query.append(DEFAULT_ITEM_COLUMN_NAME).append(" = ? AND ");
        query.append(DEFAULT_ITEM_TYPE_COLUMN_NAME).append(" = ?");

        final Object[] args = new Object[]{tenantId, itemId, itemTypeId};
        final int[] argt = new int[]{Types.INTEGER, Types.INTEGER, Types.INTEGER};
        final RowMapper mapper = new UserMapper(tenantId);

        final List<User> result = getJdbcTemplate().query(query.toString(), args, argt, mapper);

        return result;
    }

    public boolean didUserRateItem(final Integer userId, final ItemVO<Integer, Integer> item,
                                   final Integer actionTypeId) {
        final StringBuilder query = new StringBuilder("SELECT * FROM ");
        query.append(DEFAULT_TABLE_NAME);
        query.append(" WHERE ");
        query.append(DEFAULT_TENANT_COLUMN_NAME).append("=? AND ");
        query.append(DEFAULT_USER_COLUMN_NAME).append("=? AND ");
        query.append(DEFAULT_ITEM_COLUMN_NAME).append("=? AND ");
        query.append(DEFAULT_ITEM_TYPE_COLUMN_NAME).append("=? AND ");
        query.append(DEFAULT_ACTION_TYPE_COLUMN_NAME).append("=? LIMIT 1");

        final Object[] args = new Object[]{item.getTenant(), userId, item.getItem(), item.getType(), actionTypeId};
        final int[] argt = new int[]{Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER};

        final int count = getJdbcTemplate().queryForList(query.toString(), args, argt).size();

        return count > 0;
    }

    public int generateLatestActionForTenant(final Integer tenantId, final Date sinceLastAction) {
        final StringBuilder query = new StringBuilder("INSERT INTO ");
        query.append(DEFAULT_TABLE_NAME).append("(");
        appendAllColumns(query);
        query.append(")\n");
        query.append("SELECT a1.").append(ActionDAO.DEFAULT_ID_COLUMN_NAME);
        query.append(", a1.").append(ActionDAO.DEFAULT_TENANT_COLUMN_NAME);
        query.append(", a1.").append(ActionDAO.DEFAULT_USER_COLUMN_NAME);
        query.append(", a1.").append(ActionDAO.DEFAULT_ITEM_COLUMN_NAME);
        query.append(", a1.").append(ActionDAO.DEFAULT_ITEM_TYPE_COLUMN_NAME);
        query.append(", a1.").append(ActionDAO.DEFAULT_ACTION_TYPE_COLUMN_NAME);
        query.append(", a1.").append(ActionDAO.DEFAULT_RATING_VALUE_COLUMN_NAME);
        query.append(", a1.").append(ActionDAO.DEFAULT_ACTION_TIME_COLUMN_NAME).append("\n");
        query.append("FROM\n");
        query.append("    (SELECT ").append(ActionDAO.DEFAULT_TENANT_COLUMN_NAME).append(", ");
        query.append(ActionDAO.DEFAULT_USER_COLUMN_NAME).append(", ");
        query.append(ActionDAO.DEFAULT_ITEM_COLUMN_NAME).append(", ");
        query.append(ActionDAO.DEFAULT_ITEM_TYPE_COLUMN_NAME).append(", ");
        query.append(ActionDAO.DEFAULT_ACTION_TYPE_COLUMN_NAME).append(", max(");
        query.append(ActionDAO.DEFAULT_ACTION_TIME_COLUMN_NAME);
        query.append(") AS ").append(MAX_ACTION_TIME);
        query.append(" FROM ");
        query.append(ActionDAO.DEFAULT_TABLE_NAME).append("\n");
        query.append("     WHERE ");
        query.append(ActionDAO.DEFAULT_TENANT_COLUMN_NAME).append(" = ?");

        if (sinceLastAction != null)
            query.append(" AND ").append(ActionDAO.DEFAULT_ACTION_TIME_COLUMN_NAME).append(" > ?");

        query.append("\n");
        query.append("     GROUP BY ");
        query.append(ActionDAO.DEFAULT_USER_COLUMN_NAME).append(", ");
        query.append(ActionDAO.DEFAULT_ITEM_COLUMN_NAME).append(", ");
        query.append(ActionDAO.DEFAULT_ITEM_TYPE_COLUMN_NAME).append(", ");
        query.append(ActionDAO.DEFAULT_ACTION_TYPE_COLUMN_NAME).append("\n");
        query.append("    ) AS a2\n");
        query.append("INNER JOIN ");
        query.append(ActionDAO.DEFAULT_TABLE_NAME).append(" AS a1 ON\n");
        query.append("    a1.").append(ActionDAO.DEFAULT_USER_COLUMN_NAME);
        query.append(" = a2.").append(ActionDAO.DEFAULT_USER_COLUMN_NAME).append(" AND\n");
        query.append("    a1.").append(ActionDAO.DEFAULT_ITEM_COLUMN_NAME);
        query.append(" = a2.").append(ActionDAO.DEFAULT_ITEM_COLUMN_NAME).append(" AND\n");
        query.append("    a1.").append(ActionDAO.DEFAULT_ITEM_TYPE_COLUMN_NAME);
        query.append(" = a2.").append(ActionDAO.DEFAULT_ITEM_TYPE_COLUMN_NAME).append(" AND\n");
        query.append("    a1.").append(ActionDAO.DEFAULT_ACTION_TYPE_COLUMN_NAME);
        query.append(" = a2.").append(ActionDAO.DEFAULT_ACTION_TYPE_COLUMN_NAME).append(" AND\n");
        query.append("    a1.").append(ActionDAO.DEFAULT_ACTION_TIME_COLUMN_NAME);
        query.append(" = a2.").append(MAX_ACTION_TIME).append(" AND\n");
        query.append("    a1.").append(ActionDAO.DEFAULT_TENANT_COLUMN_NAME);
        query.append(" = a2.").append(ActionDAO.DEFAULT_TENANT_COLUMN_NAME).append("\n");
        query.append("WHERE a1.");
        query.append(ActionDAO.DEFAULT_RATING_VALUE_COLUMN_NAME).append(" IS NOT NULL\n");
        query.append("ON DUPLICATE KEY UPDATE\n");
        query.append("    ").append(DEFAULT_ID_COLUMN_NAME);
        query.append(" = VALUES(").append(DEFAULT_ID_COLUMN_NAME).append(")\n");

        /*
           query.append("ON DUPLICATE KEY UPDATE\n");
           query.append("    ").append(DEFAULT_ID_COLUMN_NAME);
           query.append(" = VALUES(").append(DEFAULT_ID_COLUMN_NAME).append("),\n");
           query.append("    ").append(DEFAULT_PREVIOUS_RATING_VALUE_COLUMN_NAME);
           query.append(" = ").append(DEFAULT_TABLE_NAME).append(".").append(DEFAULT_RATING_VALUE_COLUMN_NAME).append(",\n");
           query.append("    ").append(DEFAULT_PREVIOUS_ACTION_TIME_COLUMN_NAME);
           query.append(" = ").append(DEFAULT_TABLE_NAME).append(".").append(DEFAULT_ACTION_TIME_COLUMN_NAME).append(",\n");
           query.append("    ").append(DEFAULT_RATING_VALUE_COLUMN_NAME);
           query.append(" = VALUES(").append(DEFAULT_RATING_VALUE_COLUMN_NAME).append("),\n");
           query.append("    ").append(DEFAULT_ACTION_TIME_COLUMN_NAME);
           query.append(" = VALUES(").append(DEFAULT_ACTION_TIME_COLUMN_NAME).append(")");
         */
        Object[] args = new Object[]{tenantId};
        int[] argt = new int[]{Types.INTEGER};

        if (sinceLastAction != null) {
            args = ObjectArrays.concat(args, sinceLastAction);
            argt = Ints.concat(argt, new int[]{Types.TIMESTAMP});
        }

        return getJdbcTemplate().update(query.toString(), args, argt);
    }

    private static void appendAllColumns(final StringBuilder stringBuilder) {
        stringBuilder.append(DEFAULT_ID_COLUMN_NAME).append(", ");
        stringBuilder.append(DEFAULT_TENANT_COLUMN_NAME).append(", ");
        stringBuilder.append(DEFAULT_USER_COLUMN_NAME).append(", ");
        stringBuilder.append(DEFAULT_ITEM_COLUMN_NAME).append(", ");
        stringBuilder.append(DEFAULT_ITEM_TYPE_COLUMN_NAME).append(", ");
        stringBuilder.append(DEFAULT_ACTION_TYPE_COLUMN_NAME).append(", ");
        stringBuilder.append(DEFAULT_RATING_VALUE_COLUMN_NAME).append(", ");
        stringBuilder.append(DEFAULT_ACTION_TIME_COLUMN_NAME);
    }

    private StringBuilder createRatedTogetherQuery(final String same, final String lookFor) {
        final StringBuilder query = new StringBuilder("SELECT\n");
        query.append("    a1.").append(DEFAULT_TENANT_COLUMN_NAME);
        query.append(", a1.").append(DEFAULT_ITEM_COLUMN_NAME);
        query.append(" AS '").append(DEFAULT_ITEM_COLUMN_NAME);
        query.append("1', a2.").append(DEFAULT_ITEM_COLUMN_NAME);
        query.append(" AS '").append(DEFAULT_ITEM_COLUMN_NAME);
        query.append("2', a1.").append(DEFAULT_ITEM_TYPE_COLUMN_NAME);
        query.append(" AS '").append(DEFAULT_ITEM_TYPE_COLUMN_NAME);
        query.append("1', a2.").append(DEFAULT_ITEM_TYPE_COLUMN_NAME);
        query.append(" AS '").append(DEFAULT_ITEM_TYPE_COLUMN_NAME);
        query.append("2', a1.").append(DEFAULT_RATING_VALUE_COLUMN_NAME);
        query.append(" AS '").append(DEFAULT_RATING_VALUE_COLUMN_NAME);
        query.append("1', a2.").append(DEFAULT_RATING_VALUE_COLUMN_NAME);
        query.append(" AS '").append(DEFAULT_RATING_VALUE_COLUMN_NAME);
        query.append("2', a1.").append(DEFAULT_USER_COLUMN_NAME);
        query.append(" AS '").append(DEFAULT_USER_COLUMN_NAME);
        query.append("1', a2.").append(DEFAULT_USER_COLUMN_NAME);
        query.append(" AS '").append(DEFAULT_USER_COLUMN_NAME);
        query.append("2'\nFROM ").append(DEFAULT_TABLE_NAME).append(" AS a1\n");
        query.append("LEFT JOIN ").append(DEFAULT_TABLE_NAME).append(" AS a2 ON\n");
        query.append("    a1.").append(same);
        query.append(" = a2.").append(same).append(" AND\n");
        query.append("    a1.").append(DEFAULT_TENANT_COLUMN_NAME);
        query.append(" = a2.").append(DEFAULT_TENANT_COLUMN_NAME).append(" AND\n");
        query.append("    a1.").append(DEFAULT_ITEM_TYPE_COLUMN_NAME);
        query.append(" = a2.").append(DEFAULT_ITEM_TYPE_COLUMN_NAME).append(" AND\n");
        query.append("    a1.").append(DEFAULT_ACTION_TYPE_COLUMN_NAME);
        query.append(" = a2.").append(DEFAULT_ACTION_TYPE_COLUMN_NAME);
        query.append("\nWHERE\n");
        query.append("    a1.").append(DEFAULT_TENANT_COLUMN_NAME);
        query.append(" = ? AND a1.").append(DEFAULT_ITEM_TYPE_COLUMN_NAME);
        query.append(" = ? AND a1.").append(DEFAULT_ACTION_TYPE_COLUMN_NAME);
        query.append(" = ? AND a1.").append(lookFor);
        query.append(" = ? AND a2.").append(lookFor);
        query.append(" = ?");

        return query;
    }

    //~ Inner Classes //////////////////////////////////////////////////////////////////////////////////////////////////

    private static final class ItemRowMapper implements RowMapper {
        private final Integer itemTypeId;
        private final Integer tenantId;

        private ItemRowMapper(final Integer tenantId, final Integer itemTypeId) {
            this.tenantId = tenantId;
            this.itemTypeId = itemTypeId;
        }

        public Object mapRow(final ResultSet rs, final int rowNum) throws SQLException {
            final Integer itemId = DaoUtils.getInteger(rs, DEFAULT_ITEM_COLUMN_NAME);

            return new ItemVO<Integer, Integer>(tenantId, itemId, itemTypeId);
        }
    }

    private static final class RatedTogetherRowMapper implements RowMapper {
        public Object mapRow(final ResultSet rs, final int rowNum) throws SQLException {
            final Integer itemId1 = DaoUtils.getInteger(rs, DEFAULT_ITEM_COLUMN_NAME + "1");
            final Integer itemId2 = DaoUtils.getInteger(rs, DEFAULT_ITEM_COLUMN_NAME + "2");

            final Integer itemTypeId1 = DaoUtils.getInteger(rs, DEFAULT_ITEM_TYPE_COLUMN_NAME + "1");
            final Integer itemTypeId2 = DaoUtils.getInteger(rs, DEFAULT_ITEM_TYPE_COLUMN_NAME + "2");

            final Integer tenantId = DaoUtils.getInteger(rs, DEFAULT_TENANT_COLUMN_NAME);

            final ItemVO<Integer, Integer> item1 = new ItemVO<Integer, Integer>(tenantId, itemId1,
                    itemTypeId1);
            final ItemVO<Integer, Integer> item2 = new ItemVO<Integer, Integer>(tenantId, itemId2,
                    itemTypeId2);

            final Double ratingValue1 = DaoUtils.getDouble(rs, DEFAULT_RATING_VALUE_COLUMN_NAME + "1");
            final Double ratingValue2 = DaoUtils.getDouble(rs, DEFAULT_RATING_VALUE_COLUMN_NAME + "2");

            final Integer userId1 = DaoUtils.getInteger(rs, DEFAULT_USER_COLUMN_NAME + "1");
            final Integer userId2 = DaoUtils.getInteger(rs, DEFAULT_USER_COLUMN_NAME + "2");

            final RatingVO<Integer, Integer> rating1 =
                    new RatingVO<Integer, Integer>(
                            item1, ratingValue1, null, null, userId1);
            final RatingVO<Integer, Integer> rating2 =
                    new RatingVO<Integer, Integer>(
                            item2, ratingValue2, null, null, userId2);

            final RatedTogether<Integer, Integer> ratedTogether =
                    new RatedTogether<Integer, Integer>(
                            rating1, rating2);

            return ratedTogether;
        }
    }

    private static final class RatingRowMapper implements RowMapper {
        private final String actionTimeColName;
        private final String ratingValueColName;

        public RatingRowMapper(final String ratingValueColName, final String actionTimeColName) {
            this.ratingValueColName = ratingValueColName;
            this.actionTimeColName = actionTimeColName;
        }

        public Object mapRow(final ResultSet rs, final int rowNum) throws SQLException {
            final Integer tenantId = DaoUtils.getInteger(rs, DEFAULT_TENANT_COLUMN_NAME);
            final Integer userId = DaoUtils.getInteger(rs, DEFAULT_USER_COLUMN_NAME);
            final Integer itemId = DaoUtils.getInteger(rs, DEFAULT_ITEM_COLUMN_NAME);
            final Integer itemTypeId = DaoUtils.getInteger(rs, DEFAULT_ITEM_TYPE_COLUMN_NAME);
            final Integer ratingValue = DaoUtils.getInteger(rs, ratingValueColName);
            final Date actionTime = DaoUtils.getDate(rs, actionTimeColName);

            final ItemVO<Integer, Integer> item = new ItemVO<Integer, Integer>(tenantId, itemId,
                    itemTypeId);
            final RatingVO<Integer, Integer> rating =
                    new RatingVO<Integer, Integer>(
                            item, (double) ratingValue, null, actionTime, userId);

            return rating;
        }
    }

    private static final class UserMapper implements RowMapper {
        private final Integer tenantId;

        private UserMapper(final Integer tenantId) {
            this.tenantId = tenantId;
        }

        public Object mapRow(final ResultSet rs, final int rowNum) throws SQLException {
            final Integer userId = DaoUtils.getInteger(rs, DEFAULT_USER_COLUMN_NAME);

            final User user = new User(null, null, tenantId, userId);

            return user;
        }
    }
}
