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

package org.easyrec.plugin.slopeone.store.dao.impl;

import com.google.common.base.Preconditions;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.TIntSet;
import org.easyrec.model.core.ActionVO;
import org.easyrec.model.core.ItemVO;
import org.easyrec.model.core.RatingVO;
import org.easyrec.model.core.transfer.TimeConstraintVO;
import org.easyrec.plugin.slopeone.store.dao.ActionDAO;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


/**
 * MySQL implementation of {@link ActionDAO}.<p><b>Company:&nbsp;</b> SAT, Research Studios Austria</p>
 * <p><b>Copyright:&nbsp;</b> (c) 2007</p> <p><b>last modified:</b><br/> $Author: dmann $<br/> $Date: 2011-12-20 15:22:22 +0100 (Di, 20 Dez 2011) $<br/> $Revision: 18685 $</p>
 *
 * @author Patrick Marschik
 */
@DAO
public class ActionDAOMysqlImpl extends AbstractTableCreatingDroppingDAOImpl implements ActionDAO {
    private static final String QUERY_GENERATE;
    private static final int[] ARGT_GENERATE = new int[]{Types.INTEGER, Types.INTEGER, Types.TIMESTAMP};

    private static final String QUERY_INSERT;
    private static final String QUERY_INSERT_VALUE;
    private static final int[] ARGT_INSERT = new int[]{Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER,
            Types.INTEGER, Types.TIMESTAMP};

    private static final String QUERY_USER;
    private static final int[] ARGT_USER = new int[]{Types.INTEGER};

    private static final String QUERY_USER_ACTIONTIME;
    private static final int ARGT_USER_ACTIONTIME = Types.TIMESTAMP;

    private static final String QUERY_RATINGS;
    private static final int[] ARGT_RATINGS = new int[]{Types.INTEGER, Types.INTEGER};

    private static final String QUERY_NEWEST;
    public static final int[] ARGT_NEWEST = new int[]{Types.INTEGER};

    private static final RowMapper<RatingVO<Integer, Integer>> ROWMAPPER_RATING =
            new RatingRowMapper();

    private org.easyrec.store.dao.core.ActionDAO actionDAO;

    static {
        StringBuilder query = new StringBuilder();
        query.append("INSERT IGNORE INTO ").append(TABLE_NAME).append("(\n");
        query.append("  ").append(COLUMN_TENANTID).append(",\n");
        query.append("  ").append(COLUMN_USERID).append(",\n");
        query.append("  ").append(COLUMN_ITEMID).append(",\n");
        query.append("  ").append(COLUMN_ITEMTYPEID).append(",\n");
        query.append("  ").append(COLUMN_RATINGVALUE).append(",\n");
        query.append("  ").append(COLUMN_ACTIONTIME).append(")\n");
        query.append("SELECT\n");
        query.append("  ").append(org.easyrec.store.dao.core.ActionDAO.DEFAULT_TENANT_COLUMN_NAME).append(",\n");
        query.append("  ").append(org.easyrec.store.dao.core.ActionDAO.DEFAULT_USER_COLUMN_NAME).append(",\n");
        query.append("  ").append(org.easyrec.store.dao.core.ActionDAO.DEFAULT_ITEM_COLUMN_NAME).append(",\n");
        query.append("  ").append(org.easyrec.store.dao.core.ActionDAO.DEFAULT_ITEM_TYPE_COLUMN_NAME).append(",\n");
        query.append("  ").append(org.easyrec.store.dao.core.ActionDAO.DEFAULT_RATING_VALUE_COLUMN_NAME).append(",\n");
        query.append("  ").append(org.easyrec.store.dao.core.ActionDAO.DEFAULT_ACTION_TIME_COLUMN_NAME).append("\n");
        query.append("FROM\n");
        query.append("  ").append(org.easyrec.store.dao.core.ActionDAO.DEFAULT_TABLE_NAME).append("\n");
        query.append("WHERE\n");
        query.append("  ").append(org.easyrec.store.dao.core.ActionDAO.DEFAULT_TENANT_COLUMN_NAME).append(" = ? AND\n");
        query.append("  ").append(org.easyrec.store.dao.core.ActionDAO.DEFAULT_ACTION_TYPE_COLUMN_NAME).append(
                " = ? AND\n");
        query.append("  ").append(org.easyrec.store.dao.core.ActionDAO.DEFAULT_ACTION_TIME_COLUMN_NAME).append(
                " >= ? AND\n");
        query.append("  ").append(org.easyrec.store.dao.core.ActionDAO.DEFAULT_ITEM_TYPE_COLUMN_NAME).append(
                " IN (@@@)\n");
        QUERY_GENERATE = query.toString();

        query = new StringBuilder();
        query.append("INSERT IGNORE INTO ").append(TABLE_NAME).append("(\n");
        query.append("  ").append(COLUMN_TENANTID).append("\n,");
        query.append("  ").append(COLUMN_USERID).append("\n,");
        query.append("  ").append(COLUMN_ITEMID).append("\n,");
        query.append("  ").append(COLUMN_ITEMTYPEID).append("\n,");
        query.append("  ").append(COLUMN_RATINGVALUE).append("\n,");
        query.append("  ").append(COLUMN_ACTIONTIME).append(")\n");
        query.append("VALUES\n");

        QUERY_INSERT = query.toString();
        QUERY_INSERT_VALUE = "  (?, ?, ?, ?, ?, ?),\n";

        query = new StringBuilder();
        query.append("SELECT DISTINCT\n");
        query.append("  ").append(COLUMN_USERID).append('\n');
        query.append("FROM ").append(TABLE_NAME).append('\n');
        query.append("WHERE\n");
        query.append("  ").append(COLUMN_TENANTID).append(" = ? AND\n");
        query.append("  ").append(COLUMN_ITEMTYPEID).append(" IN (@@@)");

        QUERY_USER = query.toString();
        QUERY_USER_ACTIONTIME = " AND\n  " + COLUMN_ACTIONTIME + " >= ?";

        query = new StringBuilder();
        query.append("SELECT\n");
        query.append("  ").append(COLUMN_ID).append(",\n");
        query.append("  ").append(COLUMN_TENANTID).append(",\n");
        query.append("  ").append(COLUMN_USERID).append(",\n");
        query.append("  ").append(COLUMN_ITEMID).append(",\n");
        query.append("  ").append(COLUMN_ITEMTYPEID).append(",\n");
        query.append("  ").append(COLUMN_RATINGVALUE).append(",\n");
        query.append("  ").append(COLUMN_ACTIONTIME).append("\n");
        query.append("FROM ").append(TABLE_NAME).append("\n");
        query.append("WHERE\n");
        query.append("  ").append(COLUMN_TENANTID).append(" = ? AND\n");
        query.append("  ").append(COLUMN_USERID).append(" = ? AND\n");
        query.append("  ").append(COLUMN_ITEMTYPEID).append(" IN (@@@)\n");
        query.append("ORDER BY ").append(COLUMN_ITEMID).append(" ASC");

        QUERY_RATINGS = query.toString();

        query = new StringBuilder();
        query.append("SELECT\n");
        query.append("  max(").append(COLUMN_ACTIONTIME).append(")\n");
        query.append("FROM ").append(TABLE_NAME).append("\n");
        query.append("WHERE\n");
        query.append("  ").append(COLUMN_TENANTID).append(" = ? AND\n");
        query.append("  ").append(COLUMN_ITEMTYPEID).append(" IN (@@@)");

        QUERY_NEWEST = query.toString();
    }

    public ActionDAOMysqlImpl(DataSource dataSource, SqlScriptService sqlScriptService) {
        super(sqlScriptService);
        setDataSource(dataSource);
    }

    public void setActionDAO(final org.easyrec.store.dao.core.ActionDAO actionDAO) { this.actionDAO = actionDAO; }

    private String generateItemTypeInClause(TIntSet itemTypeIds) {
        StringBuilder inClause = new StringBuilder();

        TIntIterator iterator = itemTypeIds.iterator();

        while (iterator.hasNext()) {
            inClause.append(iterator.next());

            if (iterator.hasNext())
                inClause.append(", ");
        }

        return inClause.toString();
    }

    private Date getNewestActionDate(int tenantId, TIntSet itemTypeIds) {
        Object[] args = new Object[]{tenantId};

        String query = QUERY_NEWEST.replace("@@@", generateItemTypeInClause(itemTypeIds));

        return getJdbcTemplate().queryForObject(query, args, ARGT_NEWEST, Date.class);
    }

    public int generateActions(int tenantId, TIntSet itemTypeIds, int actionTypeId, Date since) {
        Preconditions.checkNotNull(itemTypeIds);
        Preconditions.checkArgument(itemTypeIds.size() > 0, "at least one itemtype must be given");

        if (since == null) since = getNewestActionDate(tenantId, itemTypeIds);

        if (isOnSameDataSourceAsEasyrec()) {
            Object[] args = new Object[]{tenantId, actionTypeId, since};

            String query = QUERY_GENERATE.replace("@@@", generateItemTypeInClause(itemTypeIds));

            return getJdbcTemplate().update(query, args, ARGT_GENERATE);
        }

        // when not on same datasource the tenantId is ignored and all actions are copied

        Iterator<ActionVO<Integer, Integer>> actions = actionDAO
                .getActionIterator(5000, new TimeConstraintVO(since, null));
        int result = 0;

        while (actions.hasNext()) {
            ActionVO<Integer, Integer> actionVO = actions.next();

            if (actionVO.getTenant() != tenantId) continue;
            if (actionVO.getActionType() != actionTypeId) continue;
            if (!itemTypeIds.contains(actionVO.getItem().getType())) continue;

            result += insertAction(actionVO);
        }

        return result;
    }

    public List<RatingVO<Integer, Integer>> getRatings(int tenantId, TIntSet itemTypeIds,
                                                                         int userId) {
        Object[] args = new Object[]{tenantId, userId};

        String query = QUERY_RATINGS.replace("@@@", generateItemTypeInClause(itemTypeIds));

        return getJdbcTemplate().query(query, args, ARGT_RATINGS, ROWMAPPER_RATING);
    }

    public List<Integer> getUsers(int tenantId, TIntSet itemTypeIds, Date since) {
        String query = QUERY_USER;
        TIntList argt = new TIntArrayList(ARGT_USER);
        List<Object> args = new ArrayList<Object>(2);
        args.add(tenantId);

        if (since != null) {
            query += QUERY_USER_ACTIONTIME;
            argt.add(ARGT_USER_ACTIONTIME);
            args.add(since);
        }

        query = query.replace("@@@", generateItemTypeInClause(itemTypeIds));

        return getJdbcTemplate().queryForList(query, args.toArray(), argt.toArray(), Integer.class);
    }

    public int insertAction(ActionVO<Integer, Integer> action) {
        String query = QUERY_INSERT + QUERY_INSERT_VALUE;
        query = query.substring(0, query.length() - 2);

        Object[] args = new Object[]{action.getTenant(), action.getUser(), action.getItem().getItem(),
                action.getItem().getType(), action.getRatingValue(), action.getActionTime()};

        return getJdbcTemplate().update(query, args, ARGT_INSERT);
    }

    public int insertActions(List<ActionVO<Integer, Integer>> actions) {
        final int BULK_SIZE = 100;
        final int BULK_COUNT = ((actions.size() + BULK_SIZE) - 1) / BULK_SIZE;

        int result = 0;

        for (int bulk = 0; bulk < BULK_COUNT; bulk++) {
            int fromIdx = bulk * BULK_SIZE;
            int toIdx = (bulk + 1) * BULK_SIZE;
            toIdx = Math.min(toIdx, actions.size());

            List<ActionVO<Integer, Integer>> thisBulk = actions.subList(fromIdx, toIdx);
            result = insertActionsBulk(BULK_SIZE, thisBulk, result);
        }

        return result;
    }

    @Override
    public String getDefaultTableName() { return TABLE_NAME; }

    @Override
    public String getTableCreatingSQLScriptName() { return "classpath:sql/plugins/slopeone/SlopeOneAction.sql"; }

    private int insertActionsBulk(final int bulkSize,
                                  final List<ActionVO<Integer, Integer>> thisBulk,
                                  int currentResult) {
        List<Object> args = new ArrayList<Object>(thisBulk.size() * ARGT_INSERT.length);

        StringBuilder query = new StringBuilder(QUERY_INSERT);
        TIntArrayList argt = new TIntArrayList(bulkSize * ARGT_INSERT.length);

        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < thisBulk.size(); i++) {
            query.append(QUERY_INSERT_VALUE);
            argt.addAll(ARGT_INSERT);
        }

        query.replace(query.length() - 2, query.length(), "");

        for (ActionVO<Integer, Integer> action : thisBulk) {
            args.add(action.getTenant());
            args.add(action.getUser());
            args.add(action.getItem().getItem());
            args.add(action.getItem().getType());
            args.add(action.getRatingValue());
            args.add(action.getActionTime());
        }

        try {
            currentResult += getJdbcTemplate().update(query.toString(), args.toArray(), argt.toArray());
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return currentResult;
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

    private static class RatingRowMapper implements RowMapper<RatingVO<Integer, Integer>> {
        public RatingVO<Integer, Integer> mapRow(ResultSet rs, int rowNum) throws SQLException {
            int tenantId = rs.getInt(COLUMN_TENANTID);
            int userId = rs.getInt(COLUMN_USERID);
            int itemId = rs.getInt(COLUMN_ITEMID);
            int itemTypeId = rs.getInt(COLUMN_ITEMTYPEID);
            double ratingValue = rs.getDouble(COLUMN_RATINGVALUE);
            Date actionTime = DaoUtils.getDate(rs, COLUMN_ACTIONTIME);
            ItemVO<Integer, Integer> item = new ItemVO<Integer, Integer>(tenantId, itemId,
                    itemTypeId);

            return new RatingVO<Integer, Integer>(item, ratingValue, 0, actionTime, userId);
        }
    }
}
