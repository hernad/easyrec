/*
 * Copyright 2010 Research Studios Austria Forschungsgesellschaft mBH
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

package org.easyrec.plugin.itemitem.store.dao.impl;

import com.google.common.primitives.Ints;
import org.easyrec.model.core.ActionVO;
import org.easyrec.model.core.ItemVO;
import org.easyrec.model.core.RatingVO;
import org.easyrec.model.core.transfer.TimeConstraintVO;
import org.easyrec.plugin.itemitem.store.dao.ActionDAO;
import org.easyrec.utils.spring.cache.annotation.ShortCacheable;
import org.easyrec.utils.spring.store.dao.DaoUtils;
import org.easyrec.utils.spring.store.dao.annotation.DAO;
import org.easyrec.utils.spring.store.dao.impl.AbstractTableCreatingDroppingDAOImpl;
import org.easyrec.utils.spring.store.service.sqlscript.SqlScriptService;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;


/**
 * MySQL implementation of {@link org.easyrec.plugin.itemitem.store.dao.ActionDAO}<p><b>Company:&nbsp;</b> SAT, Research
 * Studios Austria</p> <p><b>Copyright:&nbsp;</b> (c) 2009</p> <p><b>last modified:</b><br/> $Author$<br/> $Date$<br/>
 * $Revision$</p>
 *
 * @author Patrick Marschik
 */
@DAO
public class ActionDAOMysqlImpl extends AbstractTableCreatingDroppingDAOImpl implements ActionDAO {
    public static final String QUERY_USERS;
    public static final int[] ARGT_USERS;

    public static final String QUERY_RATED;
    public static final int[] ARGT_RATED;

    public static final String QUERY_GENERATE;
    public static final int[] ARGT_GENERATE;
    public static final String QUERY_GENERATE_SINCE;
    public static final int[] ARGT_GENERATE_SINCE;

    public static final String QUERY_ITEMS;
    public static final int[] ARGT_ITEMS;

    public static final String QUERY_ITEMRATINGS;
    public static final int[] ARGT_ITEMRATINGS;

    public static final String QUERY_USERRATINGS;
    public static final int[] ARGT_USERRATINGS;

    public static final String QUERY_RATEDTOGETHER;
    public static final int[] ARGT_RATEDTOGETHER;

    public static final String QUERY_INSERT;
    public static final int[] ARGT_INSERT;

    private static RowMapper<RatedTogether<Integer, Integer>> defaultRatedTogetherMapper = new RatedTogetherRowMapper();
    private static final RowMapper<RatingVO<Integer, Integer>> defaultRatingMapper = new RatingRowMapper(
            COLUMN_RATINGVALUE, COLUMN_ACTIONTIME);
    private static final String MAX_ACTION_TIME = "maxActionTime";
    private static final String TABLE_CREATING_SCRIPT_NAME = "classpath:sql/plugins/itemitem/ItemItemAction.sql";

    private org.easyrec.store.dao.core.ActionDAO actionDAO;

    static {
        StringBuilder query;
        query = new StringBuilder("SELECT DISTINCT ");
        query.append(COLUMN_USERID).append(" FROM ").append(TABLE_NAME).append("\n");
        query.append("WHERE ").append(COLUMN_TENANTID).append(" = ?");
        QUERY_USERS = query.toString();
        ARGT_USERS = new int[]{Types.INTEGER};
        query = new StringBuilder("SELECT * FROM ");
        query.append(TABLE_NAME);
        query.append(" WHERE ");
        query.append(COLUMN_TENANTID).append("=? AND ");
        query.append(COLUMN_USERID).append("=? AND ");
        query.append(COLUMN_ITEMID).append("=? AND ");
        query.append(COLUMN_ITEMTYPEID).append("=? AND ");
        query.append(COLUMN_ACTIONTYPEID).append("=? LIMIT 1");
        QUERY_RATED = query.toString();
        ARGT_RATED = new int[]{Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER};

        QUERY_GENERATE = makeQueryGenerate(false);
        QUERY_GENERATE_SINCE = makeQueryGenerate(true);
        ARGT_GENERATE = new int[]{Types.INTEGER};
        ARGT_GENERATE_SINCE = new int[]{Types.INTEGER, Types.TIMESTAMP};

        query = new StringBuilder("SELECT DISTINCT ");
        query.append(COLUMN_ITEMID);
        query.append(" FROM ").append(TABLE_NAME);
        query.append(" WHERE ");
        query.append(COLUMN_TENANTID).append("=? AND ");
        query.append(COLUMN_ITEMTYPEID).append("=?");

        QUERY_ITEMS = query.toString();
        ARGT_ITEMS = new int[]{Types.INTEGER, Types.INTEGER};

        QUERY_ITEMRATINGS = makeAverageRatingQueryString(COLUMN_ITEMID);
        ARGT_ITEMRATINGS = new int[]{Types.INTEGER, Types.INTEGER};

        QUERY_USERRATINGS = makeAverageRatingQueryString(COLUMN_USERID);
        ARGT_USERRATINGS = new int[]{Types.INTEGER, Types.INTEGER};

        query = new StringBuilder("SELECT\n");
        query.append("    a1.").append(COLUMN_TENANTID);
        query.append(", a1.").append(COLUMN_ITEMID);
        query.append(" AS '").append(COLUMN_ITEMID);
        query.append("1', a2.").append(COLUMN_ITEMID);
        query.append(" AS '").append(COLUMN_ITEMID);
        query.append("2', a1.").append(COLUMN_ITEMTYPEID);
        query.append(" AS '").append(COLUMN_ITEMTYPEID);
        query.append("1', a2.").append(COLUMN_ITEMTYPEID);
        query.append(" AS '").append(COLUMN_ITEMTYPEID);
        query.append("2', a1.").append(COLUMN_RATINGVALUE);
        query.append(" AS '").append(COLUMN_RATINGVALUE);
        query.append("1', a2.").append(COLUMN_RATINGVALUE);
        query.append(" AS '").append(COLUMN_RATINGVALUE);
        query.append("2', a1.").append(COLUMN_USERID);
        query.append(" AS '").append(COLUMN_USERID);
        query.append("1', a2.").append(COLUMN_USERID);
        query.append(" AS '").append(COLUMN_USERID);
        query.append("2'\nFROM ").append(TABLE_NAME).append(" AS a1\n");
        query.append("LEFT JOIN ").append(TABLE_NAME).append(" AS a2 ON\n");
        query.append("    a1.").append(COLUMN_USERID);
        query.append(" = a2.").append(COLUMN_USERID).append(" AND\n");
        query.append("    a1.").append(COLUMN_TENANTID);
        query.append(" = a2.").append(COLUMN_TENANTID).append(" AND\n");
        query.append("    a1.").append(COLUMN_ITEMTYPEID);
        query.append(" = a2.").append(COLUMN_ITEMTYPEID).append(" AND\n");
        query.append("    a1.").append(COLUMN_ACTIONTYPEID);
        query.append(" = a2.").append(COLUMN_ACTIONTYPEID);
        query.append("\nWHERE\n");
        query.append("    a1.").append(COLUMN_TENANTID);
        query.append(" = ? AND a1.").append(COLUMN_ITEMTYPEID);
        query.append(" = ? AND a1.").append(COLUMN_ACTIONTYPEID);
        query.append(" = ? AND a1.").append(COLUMN_ITEMID);
        query.append(" = ? AND a2.").append(COLUMN_ITEMID);
        query.append(" = ?");

        QUERY_RATEDTOGETHER = query.toString();
        ARGT_RATEDTOGETHER = new int[]{Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER};

        query = new StringBuilder();
        query.append("INSERT IGNORE INTO ").append(TABLE_NAME).append("(\n");
        query.append("  ").append(COLUMN_TENANTID).append("\n,");
        query.append("  ").append(COLUMN_USERID).append("\n,");
        query.append("  ").append(COLUMN_ITEMID).append("\n,");
        query.append("  ").append(COLUMN_ITEMTYPEID).append("\n,");
        query.append("  ").append(COLUMN_ACTIONTYPEID).append("\n,");
        query.append("  ").append(COLUMN_RATINGVALUE).append("\n,");
        query.append("  ").append(COLUMN_ACTIONTIME).append(")\n");
        query.append("VALUES (?, ?, ?, ?, ?, ?, ?)\n");

        QUERY_INSERT = query.toString();
        ARGT_INSERT = new int[]{Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.DOUBLE,
                Types.TIMESTAMP};
    }

    private static String makeQueryGenerate(boolean withSince) {
        final StringBuilder query = new StringBuilder("INSERT INTO ");
        query.append(TABLE_NAME).append("(");
        appendAllColumns(query);
        query.append(")\n");
        query.append("SELECT a1.").append(org.easyrec.store.dao.core.ActionDAO.DEFAULT_ID_COLUMN_NAME);
        query.append(", a1.").append(org.easyrec.store.dao.core.ActionDAO.DEFAULT_TENANT_COLUMN_NAME);
        query.append(", a1.").append(org.easyrec.store.dao.core.ActionDAO.DEFAULT_USER_COLUMN_NAME);
        query.append(", a1.").append(org.easyrec.store.dao.core.ActionDAO.DEFAULT_ITEM_COLUMN_NAME);
        query.append(", a1.").append(org.easyrec.store.dao.core.ActionDAO.DEFAULT_ITEM_TYPE_COLUMN_NAME);
        query.append(", a1.").append(org.easyrec.store.dao.core.ActionDAO.DEFAULT_ACTION_TYPE_COLUMN_NAME);
        query.append(", a1.").append(org.easyrec.store.dao.core.ActionDAO.DEFAULT_RATING_VALUE_COLUMN_NAME);
        query.append(", a1.").append(org.easyrec.store.dao.core.ActionDAO.DEFAULT_ACTION_TIME_COLUMN_NAME).append("\n");
        query.append("FROM\n");
        query.append("    (SELECT ").append(org.easyrec.store.dao.core.ActionDAO.DEFAULT_TENANT_COLUMN_NAME)
                .append(", ");
        query.append(org.easyrec.store.dao.core.ActionDAO.DEFAULT_USER_COLUMN_NAME).append(", ");
        query.append(org.easyrec.store.dao.core.ActionDAO.DEFAULT_ITEM_COLUMN_NAME).append(", ");
        query.append(org.easyrec.store.dao.core.ActionDAO.DEFAULT_ITEM_TYPE_COLUMN_NAME).append(", ");
        query.append(org.easyrec.store.dao.core.ActionDAO.DEFAULT_ACTION_TYPE_COLUMN_NAME).append(", max(");
        query.append(org.easyrec.store.dao.core.ActionDAO.DEFAULT_ACTION_TIME_COLUMN_NAME);
        query.append(") AS ").append(MAX_ACTION_TIME);
        query.append(" FROM ");
        query.append(org.easyrec.store.dao.core.ActionDAO.DEFAULT_TABLE_NAME).append("\n");
        query.append("     WHERE ");
        query.append(org.easyrec.store.dao.core.ActionDAO.DEFAULT_TENANT_COLUMN_NAME).append(" = ?");

        if (withSince)
            query.append(" AND ").append(org.easyrec.store.dao.core.ActionDAO.DEFAULT_ACTION_TIME_COLUMN_NAME)
                    .append(" > ?");

        query.append("\n");
        query.append("     GROUP BY ");
        query.append(org.easyrec.store.dao.core.ActionDAO.DEFAULT_USER_COLUMN_NAME).append(", ");
        query.append(org.easyrec.store.dao.core.ActionDAO.DEFAULT_ITEM_COLUMN_NAME).append(", ");
        query.append(org.easyrec.store.dao.core.ActionDAO.DEFAULT_ITEM_TYPE_COLUMN_NAME).append(", ");
        query.append(org.easyrec.store.dao.core.ActionDAO.DEFAULT_ACTION_TYPE_COLUMN_NAME).append("\n");
        query.append("    ) AS a2\n");
        query.append("INNER JOIN ");
        query.append(org.easyrec.store.dao.core.ActionDAO.DEFAULT_TABLE_NAME).append(" AS a1 ON\n");
        query.append("    a1.").append(org.easyrec.store.dao.core.ActionDAO.DEFAULT_USER_COLUMN_NAME);
        query.append(" = a2.").append(org.easyrec.store.dao.core.ActionDAO.DEFAULT_USER_COLUMN_NAME).append(" AND\n");
        query.append("    a1.").append(org.easyrec.store.dao.core.ActionDAO.DEFAULT_ITEM_COLUMN_NAME);
        query.append(" = a2.").append(org.easyrec.store.dao.core.ActionDAO.DEFAULT_ITEM_COLUMN_NAME).append(" AND\n");
        query.append("    a1.").append(org.easyrec.store.dao.core.ActionDAO.DEFAULT_ITEM_TYPE_COLUMN_NAME);
        query.append(" = a2.").append(org.easyrec.store.dao.core.ActionDAO.DEFAULT_ITEM_TYPE_COLUMN_NAME)
                .append(" AND\n");
        query.append("    a1.").append(org.easyrec.store.dao.core.ActionDAO.DEFAULT_ACTION_TYPE_COLUMN_NAME);
        query.append(" = a2.").append(org.easyrec.store.dao.core.ActionDAO.DEFAULT_ACTION_TYPE_COLUMN_NAME)
                .append(" AND\n");
        query.append("    a1.").append(org.easyrec.store.dao.core.ActionDAO.DEFAULT_ACTION_TIME_COLUMN_NAME);
        query.append(" = a2.").append(MAX_ACTION_TIME).append(" AND\n");
        query.append("    a1.").append(org.easyrec.store.dao.core.ActionDAO.DEFAULT_TENANT_COLUMN_NAME);
        query.append(" = a2.").append(org.easyrec.store.dao.core.ActionDAO.DEFAULT_TENANT_COLUMN_NAME).append("\n");
        query.append("WHERE a1.");
        query.append(org.easyrec.store.dao.core.ActionDAO.DEFAULT_RATING_VALUE_COLUMN_NAME).append(" IS NOT NULL\n");
        query.append("ON DUPLICATE KEY UPDATE\n");
        query.append("    ").append(COLUMN_ID);
        query.append(" = VALUES(").append(COLUMN_ID).append(")\n");

        return query.toString();
    }

    private static void appendAllColumns(final StringBuilder stringBuilder) {
        stringBuilder.append(COLUMN_ID).append(", ");
        stringBuilder.append(COLUMN_TENANTID).append(", ");
        stringBuilder.append(COLUMN_USERID).append(", ");
        stringBuilder.append(COLUMN_ITEMID).append(", ");
        stringBuilder.append(COLUMN_ITEMTYPEID).append(", ");
        stringBuilder.append(COLUMN_ACTIONTYPEID).append(", ");
        stringBuilder.append(COLUMN_RATINGVALUE).append(", ");
        stringBuilder.append(COLUMN_ACTIONTIME);
    }

    public static String makeAverageRatingQueryString(final String groupByColumn) {
        final StringBuilder query = new StringBuilder("SELECT ");
        query.append(groupByColumn);
        query.append(", AVG(").append(COLUMN_RATINGVALUE);
        query.append(") AS ").append(COLUMN_RATINGVALUE);
        query.append(", COUNT(").append(COLUMN_RATINGVALUE);
        query.append(") AS count FROM ").append(TABLE_NAME);
        query.append(" WHERE ");
        query.append(COLUMN_TENANTID).append(" = ? AND ");
        query.append(COLUMN_ITEMTYPEID).append(" = ? GROUP BY ");
        query.append(groupByColumn);

        return query.toString();
    }

    public ActionDAOMysqlImpl(final DataSource dataSource, final SqlScriptService sqlScriptService) {
        super(sqlScriptService);
        setDataSource(dataSource);
    }

    public void setActionDAO(final org.easyrec.store.dao.core.ActionDAO actionDAO) {
        this.actionDAO = actionDAO;
    }

    public boolean didUserRateItem(final Integer userId, final ItemVO<Integer, Integer> item,
                                   final Integer actionTypeId) {
        final Object[] args = new Object[]{item.getTenant(), userId, item.getItem(), item.getType(), actionTypeId};

        final int count = getJdbcTemplate().queryForList(QUERY_RATED, args, ARGT_RATED).size();

        return count > 0;
    }

    public int generateActions(final Integer tenantId, final Date since) {
        if (isOnSameDataSourceAsEasyrec()) {
            String query;
            int[] argt;
            Object[] args;

            if (since == null) {
                query = QUERY_GENERATE;
                argt = ARGT_GENERATE;
                args = new Object[]{tenantId};
            } else {
                query = QUERY_GENERATE_SINCE;
                argt = ARGT_GENERATE_SINCE;
                args = new Object[]{tenantId, since};
            }

            return getJdbcTemplate().update(query, args, argt);
        }

        Iterator<ActionVO<Integer, Integer>> actions = actionDAO
                .getActionIterator(5000, new TimeConstraintVO(since, null));
        int result = 0;

        while (actions.hasNext()) {
            ActionVO<Integer, Integer> actionVO = actions.next();

            Object[] args = new Object[]{actionVO.getTenant(), actionVO.getUser(), actionVO.getItem().getItem(),
                    actionVO.getItem().getType(), actionVO.getActionType(), actionVO.getRatingValue(),
                    actionVO.getActionTime()};

            result += getJdbcTemplate().update(QUERY_INSERT, args, ARGT_INSERT);
        }

        return result;
    }

    public List<ItemVO<Integer, Integer>> getAvailableItemsForTenant(final Integer tenantId,
                                                                              final Integer itemTypeId) {
        final Object[] args = new Object[]{tenantId, itemTypeId};

        final RowMapper<ItemVO<Integer, Integer>> mapper = new ItemRowMapper(tenantId, itemTypeId);

        return getJdbcTemplate().query(QUERY_ITEMS, args, ARGT_ITEMS, mapper);
    }

    public List<RatingVO<Integer, Integer>> getAverageRatingsForItem(final Integer tenantId,
                                                                                       final Integer itemTypeId) {
        final Object[] args = new Object[]{tenantId, itemTypeId};

        final RowMapper<RatingVO<Integer, Integer>> mapper = new RowMapper<RatingVO<Integer, Integer>>() {
            public RatingVO<Integer, Integer> mapRow(final ResultSet rs, final int rowNum)
                    throws SQLException {
                final Integer itemId = DaoUtils.getInteger(rs, COLUMN_ITEMID);
                final Double averageRatingValue = DaoUtils.getDouble(rs, COLUMN_RATINGVALUE);
                final Integer count = DaoUtils.getInteger(rs, "count");

                final ItemVO<Integer, Integer> item = new ItemVO<Integer, Integer>(tenantId, itemId,
                        itemTypeId);

                return new RatingVO<Integer, Integer>(item, averageRatingValue, count, null);
            }
        };

        final List<RatingVO<Integer, Integer>> ratings = getJdbcTemplate()
                .query(QUERY_ITEMRATINGS, args, ARGT_ITEMRATINGS, mapper);

        return ratings;
    }

    public List<RatingVO<Integer, Integer>> getAverageRatingsForUser(final Integer tenantId,
                                                                                       final Integer itemTypeId) {
        final Object[] args = new Object[]{tenantId, itemTypeId};

        final RowMapper<RatingVO<Integer, Integer>> mapper = new RowMapper<RatingVO<Integer, Integer>>() {
            public RatingVO<Integer, Integer> mapRow(final ResultSet rs, final int rowNum)
                    throws SQLException {
                final Integer userId = DaoUtils.getInteger(rs, COLUMN_USERID);
                final Double averageRatingValue = DaoUtils.getDouble(rs, COLUMN_RATINGVALUE);
                final Integer count = DaoUtils.getInteger(rs, "count");

                final RatingVO<Integer, Integer> rating = new RatingVO<Integer, Integer>(
                        null, averageRatingValue, count, null, userId);

                return rating;
            }
        };

        return getJdbcTemplate().query(QUERY_USERRATINGS, args, ARGT_USERRATINGS, mapper);
    }

    public List<RatedTogether<Integer, Integer>> getItemsRatedTogether(final Integer tenantId,
                                                                                         final Integer itemTypeId,
                                                                                         final Integer item1Id,
                                                                                         final Integer item2Id,
                                                                                         final Integer actionTypeId) {
        final Object[] args = new Object[]{tenantId, itemTypeId, actionTypeId, item1Id, item2Id};

        return getJdbcTemplate().query(QUERY_RATEDTOGETHER, args, ARGT_RATEDTOGETHER, defaultRatedTogetherMapper);
    }

    @ShortCacheable
    public List<RatingVO<Integer, Integer>> getLatestRatingsForTenant(final Integer tenantId,
                                                                                        final Integer itemTypeId,
                                                                                        final Integer itemId,
                                                                                        final Integer userId,
                                                                                        final Date since) {
        final StringBuilder query = new StringBuilder("SELECT ");
        appendAllColumns(query);
        query.append("\n");
        query.append("FROM ").append(TABLE_NAME).append("\n");
        query.append("WHERE ");
        query.append(COLUMN_TENANTID).append(" = ? AND ");
        query.append(COLUMN_ITEMTYPEID).append(" = ?");

        List<Object> args = newArrayList((Object)tenantId, itemTypeId);
        List<Integer> argt = newArrayList(Types.INTEGER, Types.INTEGER);

        if (itemId != null) {
            query.append(" AND ").append(COLUMN_ITEMID).append(" = ?");

            args.add(itemId);
            argt.add(Types.INTEGER);
        }

        if (userId != null) {
            query.append(" AND ").append(COLUMN_USERID).append(" = ?");

            args.add(userId);
            argt.add(Types.INTEGER);
        }

        if (since != null) {
            query.append(" AND ").append(COLUMN_ACTIONTIME).append(" > ?");

            args.add(since);
            argt.add(Types.TIMESTAMP);
        }

        return getJdbcTemplate().query(query.toString(), args.toArray(), Ints.toArray(argt), defaultRatingMapper);
    }

    public List<Integer> getUsersForTenant(Integer tenantId) {
        Object[] args = new Object[]{tenantId};

        return getJdbcTemplate().queryForList(QUERY_USERS, args, ARGT_USERS, Integer.class);
    }

    @Override
    public String getDefaultTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getTableCreatingSQLScriptName() {
        return TABLE_CREATING_SCRIPT_NAME;
    }

    private boolean isOnSameDataSourceAsEasyrec() {
        if (actionDAO == null) return false;

        String thisURL;
        String easyrecURL;

        Connection myConnection = null;
        Connection actionConnection = null;

        try {
            myConnection = DataSourceUtils.getConnection(getDataSource());
            actionConnection = DataSourceUtils.getConnection(actionDAO.getDataSource());

            thisURL = myConnection.getMetaData().getURL();
            easyrecURL = actionConnection.getMetaData().getURL();
        } catch (SQLException e) {
            String message = "Couldn't get datasource's URL.";
            logger.error(message, e);

            throw new RuntimeException(message, e);
        } finally {
            try {
                try {
                    if (myConnection != null) myConnection.close();
                } finally {
                    if (actionConnection != null) actionConnection.close();
                }
            } catch (SQLException e) {
                String message = "Couldn't get datasource's URL.";
                logger.error(message, e);
            }
        }

        return thisURL.equals(easyrecURL);
    }

    private static final class ItemRowMapper implements RowMapper<ItemVO<Integer, Integer>> {
        private final Integer itemTypeId;
        private final Integer tenantId;

        private ItemRowMapper(final Integer tenantId, final Integer itemTypeId) {
            this.tenantId = tenantId;
            this.itemTypeId = itemTypeId;
        }

        public ItemVO<Integer, Integer> mapRow(final ResultSet rs, final int rowNum) throws SQLException {
            final Integer itemId = DaoUtils.getInteger(rs, COLUMN_ITEMID);

            return new ItemVO<Integer, Integer>(tenantId, itemId, itemTypeId);
        }
    }

    private static final class RatedTogetherRowMapper
            implements RowMapper<RatedTogether<Integer, Integer>> {
        public RatedTogether<Integer, Integer> mapRow(final ResultSet rs, final int rowNum)
                throws SQLException {
            final Integer itemId1 = DaoUtils.getInteger(rs, COLUMN_ITEMID + "1");
            final Integer itemId2 = DaoUtils.getInteger(rs, COLUMN_ITEMID + "2");

            final Integer itemTypeId1 = DaoUtils.getInteger(rs, COLUMN_ITEMTYPEID + "1");
            final Integer itemTypeId2 = DaoUtils.getInteger(rs, COLUMN_ITEMTYPEID + "2");

            final Integer tenantId = DaoUtils.getInteger(rs, COLUMN_TENANTID);

            final ItemVO<Integer, Integer> item1 = new ItemVO<Integer, Integer>(tenantId, itemId1,
                    itemTypeId1);
            final ItemVO<Integer, Integer> item2 = new ItemVO<Integer, Integer>(tenantId, itemId2,
                    itemTypeId2);

            final Double ratingValue1 = DaoUtils.getDouble(rs, COLUMN_RATINGVALUE + "1");
            final Double ratingValue2 = DaoUtils.getDouble(rs, COLUMN_RATINGVALUE + "2");

            final Integer userId1 = DaoUtils.getInteger(rs, COLUMN_USERID + "1");
            final Integer userId2 = DaoUtils.getInteger(rs, COLUMN_USERID + "2");

            final RatingVO<Integer, Integer> rating1 = new RatingVO<Integer, Integer>(
                    item1, ratingValue1, null, null, userId1);
            final RatingVO<Integer, Integer> rating2 = new RatingVO<Integer, Integer>(
                    item2, ratingValue2, null, null, userId2);

            final RatedTogether<Integer, Integer> ratedTogether = new RatedTogether<Integer, Integer>(
                    rating1, rating2);

            return ratedTogether;
        }
    }

    private static final class RatingRowMapper implements RowMapper<RatingVO<Integer, Integer>> {
        private final String actionTimeColName;
        private final String ratingValueColName;

        public RatingRowMapper(final String ratingValueColName, final String actionTimeColName) {
            this.ratingValueColName = ratingValueColName;
            this.actionTimeColName = actionTimeColName;
        }

        public RatingVO<Integer, Integer> mapRow(final ResultSet rs, final int rowNum)
                throws SQLException {
            final Integer tenantId = DaoUtils.getInteger(rs, COLUMN_TENANTID);
            final Integer userId = DaoUtils.getInteger(rs, COLUMN_USERID);
            final Integer itemId = DaoUtils.getInteger(rs, COLUMN_ITEMID);
            final Integer itemTypeId = DaoUtils.getInteger(rs, COLUMN_ITEMTYPEID);
            final Integer ratingValue = DaoUtils.getInteger(rs, ratingValueColName);
            final Date actionTime = DaoUtils.getDate(rs, actionTimeColName);

            final ItemVO<Integer, Integer> item = new ItemVO<Integer, Integer>(tenantId, itemId,
                    itemTypeId);
            final RatingVO<Integer, Integer> rating = new RatingVO<Integer, Integer>(
                    item, (double) ratingValue, null, actionTime, userId);

            return rating;
        }
    }
}
