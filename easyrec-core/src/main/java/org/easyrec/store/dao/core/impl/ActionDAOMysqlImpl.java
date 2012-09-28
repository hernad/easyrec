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
package org.easyrec.store.dao.core.impl;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import org.easyrec.model.core.ActionVO;
import org.easyrec.model.core.ItemVO;
import org.easyrec.model.core.RankedItemVO;
import org.easyrec.model.core.RatingVO;
import org.easyrec.model.core.transfer.TimeConstraintVO;
import org.easyrec.service.core.TenantService;
import org.easyrec.store.dao.core.ActionDAO;
import org.easyrec.store.dao.impl.AbstractBaseActionDAOMysqlImpl;
import org.easyrec.utils.spring.store.ResultSetIteratorMysql;
import org.easyrec.utils.spring.store.dao.DaoUtils;
import org.easyrec.utils.spring.store.dao.annotation.DAO;
import org.easyrec.utils.spring.store.service.sqlscript.SqlScriptService;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.annotation.Nullable;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * This class provides a Mysql implementation of the {@link org.easyrec.store.dao.core.ActionDAO} interface.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: sat-rsa $<br/>
 * $Date: 2012-02-23 16:01:50 +0100 (Thu, 23 Feb 2012) $<br/>
 * $Revision: 140 $</p>
 *
 * @author Roman Cerny
 */

@DAO
public class ActionDAOMysqlImpl extends
        AbstractBaseActionDAOMysqlImpl<ActionVO<Integer, Integer>, RankedItemVO<Integer, Integer>, Integer, Integer, ItemVO<Integer, Integer>, RatingVO<Integer, Integer>, Integer, Integer>
        implements ActionDAO {
    // constants
    private final static String DEFAULT_COUNT_ALIAS_NAME = "itemCounter";
    private final static String DEFAULT_RATING_ALIAS_NAME = "rating";
    private final static String DEFAULT_LAST_ACTION_TIME_ALIAS_NAME = "lastActionTime";

    // Prepared DAO Statment for Insert Action
    private static final int[] ARG_TYPES_INSERT;
    private static final String SQL_INSERT_ACTION;
    private static final String SQL_REMOVE_ACTIONS;
    private static final PreparedStatementCreatorFactory PS_INSERT_ACTION;

    // members
    private TenantService tenantService;

    private ActionVORowMapper actionVORowMapper = new ActionVORowMapper();
    private RankedItemVORowMapper rankedItemVORowMapper = new RankedItemVORowMapper();
    private ItemVORowMapper itemVORowMapper = new ItemVORowMapper();
    private RatingVORowMapper ratingVORowMapper = new RatingVORowMapper();

    static {

        ARG_TYPES_INSERT = new int[]{Types.INTEGER, Types.INTEGER, Types.VARCHAR, Types.VARCHAR, Types.INTEGER,
                Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.BOOLEAN, Types.INTEGER, Types.VARCHAR,
                Types.TIMESTAMP};

        SQL_INSERT_ACTION = new StringBuilder("INSERT INTO ").append(DEFAULT_TABLE_NAME).append(" SET ")
                .append(DEFAULT_TENANT_COLUMN_NAME).append("=?, ").append(DEFAULT_USER_COLUMN_NAME).append("=?, ")
                .append(DEFAULT_SESSION_COLUMN_NAME).append("=?, ").append(DEFAULT_IP_COLUMN_NAME).append("=?, ")
                .append(DEFAULT_ITEM_COLUMN_NAME).append("=?, ").append(DEFAULT_ITEM_TYPE_COLUMN_NAME).append("=?, ")
                .append(DEFAULT_ACTION_TYPE_COLUMN_NAME).append("=?, ").append(DEFAULT_RATING_VALUE_COLUMN_NAME)
                .append("=?, ").append(DEFAULT_SEARCH_SUCCEEDED_COLUMN_NAME).append("=?, ")
                .append(DEFAULT_NUMBER_OF_FOUND_ITEMS).append("=?, ").append(DEFAULT_DESCRIPTION_COLUMN_NAME)
                .append("=?, ").append(DEFAULT_ACTION_TIME_COLUMN_NAME).append("=? ").toString();

        PS_INSERT_ACTION = new PreparedStatementCreatorFactory(SQL_INSERT_ACTION, ARG_TYPES_INSERT);
        PS_INSERT_ACTION.setReturnGeneratedKeys(true);

        SQL_REMOVE_ACTIONS = new StringBuilder("DELETE FROM ").append(DEFAULT_TABLE_NAME).append(" WHERE ")
                .append(DEFAULT_TENANT_COLUMN_NAME).append("=? ").toString();

    }

    // constructor
    public ActionDAOMysqlImpl(DataSource dataSource, TenantService tenantService, SqlScriptService sqlScriptService) {
        super(sqlScriptService);
        this.tenantService = tenantService;
        setDataSource(dataSource);

        // output connection information
        if (logger.isInfoEnabled()) {
            try {
                logger.info(DaoUtils.getDatabaseURLAndUserName(dataSource));
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    // abstract (generic) method implementation of 'AbstractBaseActionDAOMysqlImpl<ActionVO>'
    @Override
    public Date getNewestActionDate(Integer tenantId, Integer userId, String sessionId) {
        // validate tenantId
        if (tenantId == null) {
            throw new IllegalArgumentException("missing constraints, missing 'tenantId'");
        }

        StringBuilder query = new StringBuilder("SELECT ");
        query.append(DEFAULT_ACTION_TIME_COLUMN_NAME);
        query.append(" FROM ");
        query.append(DEFAULT_TABLE_NAME);
        query.append(" WHERE ");
        query.append(DEFAULT_TENANT_COLUMN_NAME);
        query.append("=?");

        List<Object> args = Lists.newArrayList((Object) tenantId);
        List<Integer> argt = Lists.newArrayList(Types.INTEGER);

        if (userId != null) {
            query.append(" AND ");
            query.append(DEFAULT_USER_COLUMN_NAME);
            query.append("=?");

            args.add(userId);
            argt.add(Types.INTEGER);
        }
        if (sessionId != null) {
            query.append(" AND ");
            query.append(DEFAULT_SESSION_COLUMN_NAME);
            query.append(" LIKE ?");

            args.add(sessionId);
            argt.add(Types.VARCHAR);
        }

        query.append(" ORDER BY ");
        query.append(DEFAULT_ACTION_TIME_COLUMN_NAME);
        query.append(" DESC");

        return getJdbcTemplate().query(query.toString(), args.toArray(), Ints.toArray(argt),
                dateResultSetExtractor);
    }

    @Override
    public int insertAction(ActionVO<Integer, Integer> action, boolean useDateFromVO) {
        if (logger.isTraceEnabled()) {
            logger.trace("inserting action=" + action);
        }

        // validate non-empty fields (NOT NULL)
        validateNonEmptyFields(action, useDateFromVO);

        Object[] args = {action.getTenant(), action.getUser(), action.getSessionId(), action.getIp(),
                ((action.getItem() != null) ? action.getItem().getItem() : null),
                ((action.getItem() != null) ? action.getItem().getType() : null), action.getActionType(),
                action.getRatingValue(), action.getSearchSucceeded(), action.getNumberOfFoundItems(),
                action.getDescription(), ((useDateFromVO && action.getActionTime() != null) ? action.getActionTime() :
                                          new Date(System.currentTimeMillis()))};

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsAffected = getJdbcTemplate().update(PS_INSERT_ACTION.newPreparedStatementCreator(args), keyHolder);

        // retrieve auto increment id, and set to VO
        action.setId(keyHolder.getKey().intValue());

        return rowsAffected;
    }

    @Override
    public int removeActionsByTenant(Integer tenantId) {

        if (tenantId == null) {
            throw new IllegalArgumentException("'tenantId' must not be 'null'!");
        }

        Object[] args = {tenantId};
        int[] argTypes = {Types.INTEGER};

        int rowsAffected = getJdbcTemplate().update(SQL_REMOVE_ACTIONS, args, argTypes);

        return rowsAffected;
    }

    @Override
    public Iterator<ActionVO<Integer, Integer>> getActionIterator(int bulkSize) {
        return new ResultSetIteratorMysql<ActionVO<Integer, Integer>>(getDataSource(),
                bulkSize, getActionIteratorQueryString(), actionVORowMapper);
    }

    @Override
    public Iterator<ActionVO<Integer, Integer>> getActionIterator(int bulkSize,
                                                                                             TimeConstraintVO timeConstraints) {
        if (timeConstraints == null || timeConstraints.getDateFrom() == null && timeConstraints.getDateTo() == null) {
            return getActionIterator(bulkSize);
        }
        Object[] args = new Object[1];
        int[] argTypes = {Types.TIMESTAMP};

        DaoUtils.ArgsAndTypesHolder holder = new DaoUtils.ArgsAndTypesHolder(args, argTypes);
        String s = getActionIteratorQueryString(timeConstraints, holder);

        return new ResultSetIteratorMysql<ActionVO<Integer, Integer>>(getDataSource(),
                bulkSize, s, holder.getArgs(), holder.getArgTypes(), actionVORowMapper);
    }

    @Override
    public List<ActionVO<Integer, Integer>> getActionsFromUser(Integer tenantId,
                                                                                          Integer userId,
                                                                                          String sessionId) {
        if (logger.isTraceEnabled()) {
            logger.trace("retrieving actions from user (tenantId=" + tenantId + ", userId=" + userId + ", sessionId=" +
                    sessionId);
        }

        // validate
        if (tenantId == null) {
            throw new IllegalArgumentException("missing constraints, missing 'tenantId'");
        }

        StringBuilder query = new StringBuilder("SELECT * FROM ");
        query.append(DEFAULT_TABLE_NAME);
        query.append(" WHERE ");
        query.append(DEFAULT_TENANT_COLUMN_NAME);
        query.append("=?");

        List<Object> args = Lists.newArrayList((Object) tenantId);
        List<Integer> argt = Lists.newArrayList(Types.INTEGER);

        if (userId != null) {
            query.append(" AND ");
            query.append(DEFAULT_USER_COLUMN_NAME);
            query.append("=?");

            args.add(userId);
            argt.add(Types.INTEGER);
        }

        if (sessionId != null) {
            query.append(" AND ");
            query.append(DEFAULT_SESSION_COLUMN_NAME);
            query.append(" LIKE ?");

            args.add(sessionId);
            argt.add(Types.VARCHAR);
        }

        return getJdbcTemplate().query(query.toString(), args.toArray(), Ints.toArray(argt), actionVORowMapper);
    }

    @Override
    public List<RankedItemVO<Integer, Integer>> getRankedItemsByActionType(Integer tenantId,
                                                                                             Integer actionTypeId,
                                                                                             @Nullable Integer itemTypeId,
                                                                                             @Nullable Integer numberOfResults,
                                                                                             @Nullable TimeConstraintVO timeConstraints,
                                                                                             @Nullable Boolean sortDesc) {
        if (logger.isTraceEnabled()) {
            logger.trace("retrieving rankedItemsByActionType(tenantId=" + tenantId + ", actionTypeId=" + actionTypeId +
                    ", itemTypeId=" + itemTypeId + ", numberOfResults=" + numberOfResults + ", timeConstraints=" +
                    timeConstraints + ", sortDesc=" + sortDesc);
        }

        // validate
        if (tenantId == null) {
            throw new IllegalArgumentException("missing constraints, missing 'tenantId'");
        }
        if (actionTypeId == null) {
            throw new IllegalArgumentException("missing constraints, missing 'actionTypeId'");
        }

        StringBuilder interestingColumns = new StringBuilder(DEFAULT_TENANT_COLUMN_NAME);
        interestingColumns.append(", ");
        interestingColumns.append(DEFAULT_ACTION_TYPE_COLUMN_NAME);
        interestingColumns.append(", ");
        interestingColumns.append(DEFAULT_ITEM_TYPE_COLUMN_NAME);
        interestingColumns.append(", ");
        interestingColumns.append(DEFAULT_ITEM_COLUMN_NAME);

        StringBuilder query = new StringBuilder("SELECT ");
        query.append(interestingColumns);
        query.append(", COUNT(*) AS ");
        query.append(DEFAULT_COUNT_ALIAS_NAME);
        query.append(" FROM ");
        query.append(DEFAULT_TABLE_NAME);
        query.append(" WHERE ");
        query.append(DEFAULT_TENANT_COLUMN_NAME);
        query.append("=? AND ");
        query.append(DEFAULT_ACTION_TYPE_COLUMN_NAME);
        query.append("=?");

        List<Object> args = Lists.newArrayList((Object) tenantId, actionTypeId);
        List<Integer> argt = Lists.newArrayList(Types.INTEGER, Types.INTEGER);

        if (itemTypeId != null) {
            query.append(" AND ");
            query.append(DEFAULT_ITEM_TYPE_COLUMN_NAME);
            query.append("=?");

            args.add(itemTypeId);
            argt.add(Types.INTEGER);
        }

        if (timeConstraints != null && (timeConstraints.getDateFrom() != null || timeConstraints.getDateTo() != null)) {
            if (timeConstraints.getDateFrom() != null) {
                query.append(" AND ");
                query.append(DEFAULT_ACTION_TIME_COLUMN_NAME);
                query.append(" >= ?");

                args.add(timeConstraints.getDateFrom());
                argt.add(Types.TIMESTAMP);

                if (timeConstraints.getDateTo() != null) {
                    query.append(" AND ");
                    query.append(DEFAULT_ACTION_TIME_COLUMN_NAME);
                    query.append(" <= ?");

                    args.add(timeConstraints.getDateTo());
                    argt.add(Types.TIMESTAMP);
                }
            } else {
                query.append(" AND ");
                query.append(DEFAULT_ACTION_TIME_COLUMN_NAME);
                query.append(" <= ?");

                args.add(timeConstraints.getDateTo());
                argt.add(Types.TIMESTAMP);
            }
        }

        query.append(" GROUP BY ");
        query.append(interestingColumns);

        if (sortDesc != null) {
            query.append(" ORDER BY ");
            query.append(DEFAULT_COUNT_ALIAS_NAME);
            query.append(" ");
            if (sortDesc) {
                query.append(DaoUtils.ORDER_DESC);
            } else {
                query.append(DaoUtils.ORDER_ASC);
            }
        }

        // Note: for a non-mysql implementation this is need to be changed
        if (numberOfResults != null && numberOfResults > 0) {
            query.append(" LIMIT ?");

            args.add(numberOfResults);
            argt.add(Types.INTEGER);
        }

        return getJdbcTemplate().query(query.toString(), args.toArray(), Ints.toArray(argt),
                rankedItemVORowMapper);
    }

    public List<ItemVO<Integer, Integer>> getItemsOfTenant(final Integer tenantId,
                                                                    final Integer consideredItemTypeId) {
        if (logger.isTraceEnabled()) {
            logger.trace(
                    "retrieving itemsOfTenant(tenantId=" + tenantId + ", consideredItemTypeId=" + consideredItemTypeId);
        }

        // validate
        if (tenantId == null || consideredItemTypeId == null) {
            throw new IllegalArgumentException("missing constraints: missing 'tenantId' or 'consideredItemTypeId'");
        }

        StringBuilder query = new StringBuilder("SELECT DISTINCT ");
        query.append(DEFAULT_TENANT_COLUMN_NAME);
        query.append(", ");
        query.append(DEFAULT_ITEM_COLUMN_NAME);
        query.append(", ");
        query.append(DEFAULT_ITEM_TYPE_COLUMN_NAME);
        query.append(" FROM ");
        query.append(DEFAULT_TABLE_NAME);
        query.append(" WHERE ");
        query.append(DEFAULT_TENANT_COLUMN_NAME);
        query.append(" = ? AND ");
        query.append(DEFAULT_ITEM_TYPE_COLUMN_NAME);
        query.append(" = ?");

        Object[] args = {tenantId, consideredItemTypeId};
        int[] argTypes = {Types.INTEGER, Types.INTEGER};

        return getJdbcTemplate().query(query.toString(), args, argTypes, itemVORowMapper);
    }

    @Override
    public List<ItemVO<Integer, Integer>> getItemsByUserActionAndType(Integer tenantId, Integer userId,
                                                                               String sessionId,
                                                                               Integer consideredActionTypeId,
                                                                               Integer consideredItemTypeId,
                                                                               Integer numberOfLastActionsConsidered) {
        return getItemsByUserActionAndType(tenantId, userId, sessionId, consideredActionTypeId, consideredItemTypeId, null, numberOfLastActionsConsidered);

    }

    @Override
    public List<ItemVO<Integer, Integer>> getItemsByUserActionAndType(Integer tenantId, Integer userId,
                                                                               String sessionId,
                                                                               Integer consideredActionTypeId,
                                                                               Integer consideredItemTypeId,
                                                                               Double ratingThreshold,
                                                                               Integer numberOfLastActionsConsidered) {
        if (logger.isTraceEnabled()) {
            logger.trace(
                    "retrieving itemsByUserActionAndType(tenantId=" + tenantId + ", userId=" + userId + ", sessionId=" +
                            sessionId + ", consideredActionTypeId=" + consideredActionTypeId +
                            ", consideredItemTypeId=" + consideredItemTypeId + ", numberOfLastActionsConsidered=" +
                            numberOfLastActionsConsidered);
        }

        // validate
        if (tenantId == null) {
            throw new IllegalArgumentException("missing constraints: missing 'tenantId'");
        }
        if (userId == null && sessionId == null) {
            throw new IllegalArgumentException("missing constraints: missing 'userId' or 'sessionId'");
        }

        StringBuilder query = new StringBuilder("SELECT ");
        query.append(DEFAULT_TENANT_COLUMN_NAME);
        query.append(", ");
        query.append(DEFAULT_ITEM_COLUMN_NAME);
        query.append(", ");
        query.append(DEFAULT_ITEM_TYPE_COLUMN_NAME);
        query.append(" FROM ");
        query.append(DEFAULT_TABLE_NAME);
        query.append(" WHERE ");

        query.append(DEFAULT_TENANT_COLUMN_NAME);
        query.append("=? ");

        List<Object> args = Lists.newArrayList((Object) tenantId);
        List<Integer> argt = Lists.newArrayList(Types.INTEGER);

        if (userId != null) {
            query.append(" AND ");
            query.append(DEFAULT_USER_COLUMN_NAME);
            query.append("=? ");

            args.add(userId);
            argt.add(Types.INTEGER);
        }

        if (sessionId != null) {
            query.append(" AND ");
            query.append(DEFAULT_SESSION_COLUMN_NAME);
            query.append(" LIKE ? ");

            args.add(sessionId);
            argt.add(Types.VARCHAR);
        }

        if (consideredActionTypeId != null) {
            query.append(" AND ");
            query.append(DEFAULT_ACTION_TYPE_COLUMN_NAME);
            query.append("=? ");

            args.add(consideredActionTypeId);
            argt.add(Types.INTEGER);

            if (ratingThreshold != null) {
                query.append(" AND ");
                query.append(DEFAULT_RATING_VALUE_COLUMN_NAME);
                query.append(">? ");

                args.add(ratingThreshold);
                argt.add(Types.DOUBLE);
            }
        }

        if (consideredItemTypeId != null) {
            query.append(" AND ");
            query.append(DEFAULT_ITEM_TYPE_COLUMN_NAME);
            query.append("=? ");

            args.add(consideredItemTypeId);
            argt.add(Types.INTEGER);
        }

        query.append(" GROUP BY " + DEFAULT_ITEM_COLUMN_NAME);

        // Note: for a non-mysql implementation this needs to be changed
        if (numberOfLastActionsConsidered != null && numberOfLastActionsConsidered > 0) {
            query.append(" LIMIT ?");

            args.add(numberOfLastActionsConsidered);
            argt.add(Types.INTEGER);
        }

        return getJdbcTemplate().query(query.toString(), args.toArray(), Ints.toArray(argt), itemVORowMapper);
    }

    @Override
    public List<RatingVO<Integer, Integer>> getDirectItemRatings(Integer tenantId, Integer userId,
                                                                                   String sessionId, Integer itemTypeId,
                                                                                   Integer numberOfResults,
                                                                                   TimeConstraintVO timeRange,
                                                                                   Boolean sortByRatingInsteadOfActionTime,
                                                                                   Boolean goodRatingsOnly,
                                                                                   Integer tenantSpecificIdForRatingAction) {
        // query:
        // SELECT tenantId, (userId, sessionId,) itemId, itemTypeId, AVG(ratingValue) AS rating, COUNT(ratingValue) AS count
        // FROM action WHERE tenantId=0 AND actionType=RATING
        // GROUP BY tenantId, itemId, itemTypeId (, userId, sessionId)
        // HAVING rating <= 5.5 --optionally
        // ORDER BY rating DESC;

        // validate tenantId
        if (tenantId == null) {
            throw new IllegalArgumentException("missing constraints: missing 'tenantId'");
        }

        StringBuilder interestingColumns = new StringBuilder(DEFAULT_TENANT_COLUMN_NAME);
        if (userId != null) {
            interestingColumns.append(", ");
            interestingColumns.append(DEFAULT_USER_COLUMN_NAME);
        }
        if (sessionId != null) {
            interestingColumns.append(", ");
            interestingColumns.append(DEFAULT_SESSION_COLUMN_NAME);
        }
        interestingColumns.append(", ");
        interestingColumns.append(DEFAULT_ITEM_COLUMN_NAME);
        interestingColumns.append(", ");
        interestingColumns.append(DEFAULT_ITEM_TYPE_COLUMN_NAME);

        StringBuilder query = new StringBuilder("SELECT ");
        query.append(interestingColumns);
        query.append(", AVG(");
        query.append(DEFAULT_RATING_VALUE_COLUMN_NAME);
        query.append(") AS ");
        query.append(DEFAULT_RATING_ALIAS_NAME);
        query.append(", COUNT(");
        query.append(DEFAULT_RATING_VALUE_COLUMN_NAME);
        query.append(") AS ");
        query.append(DEFAULT_COUNT_ALIAS_NAME);
        query.append(", MAX(");
        query.append(DEFAULT_ACTION_TIME_COLUMN_NAME);
        query.append(") AS ");
        query.append(DEFAULT_LAST_ACTION_TIME_ALIAS_NAME);
        query.append(" FROM ");
        query.append(DEFAULT_TABLE_NAME);
        query.append(" WHERE ");
        query.append(DEFAULT_TENANT_COLUMN_NAME);
        query.append("=? AND ");
        query.append(DEFAULT_ACTION_TYPE_COLUMN_NAME);
        query.append("=?");

        List<Object> args = Lists.newArrayList((Object) tenantId, (Object) tenantSpecificIdForRatingAction);
        List<Integer> argt = Lists.newArrayList(Types.INTEGER, Types.INTEGER);

        if (itemTypeId != null) {
            query.append(" AND ");
            query.append(DEFAULT_ITEM_TYPE_COLUMN_NAME);
            query.append("=?");

            args.add(itemTypeId);
            argt.add(Types.INTEGER);
        }
        if (userId != null) {
            query.append(" AND ");
            query.append(DEFAULT_USER_COLUMN_NAME);
            query.append("=?");

            args.add(userId);
            argt.add(Types.INTEGER);
        }
        if (sessionId != null) {
            query.append(" AND ");
            query.append(DEFAULT_SESSION_COLUMN_NAME);
            query.append(" LIKE ? ");

            args.add(sessionId);
            argt.add(Types.VARCHAR);
        }

        if (timeRange != null && (timeRange.getDateFrom() != null || timeRange.getDateTo() != null)) {
            if (timeRange.getDateFrom() != null) {
                query.append(" AND ");
                query.append(DEFAULT_ACTION_TIME_COLUMN_NAME);
                query.append(" >= ?");

                args.add(timeRange.getDateFrom());
                argt.add(Types.TIMESTAMP);

                if (timeRange.getDateTo() != null) {
                    query.append(" AND ");
                    query.append(DEFAULT_ACTION_TIME_COLUMN_NAME);
                    query.append(" <= ?");

                    args.add(timeRange.getDateTo());
                    argt.add(Types.TIMESTAMP);
                }
            } else {
                query.append(" AND ");
                query.append(DEFAULT_ACTION_TIME_COLUMN_NAME);
                query.append(" <= ?");

                args.add(timeRange.getDateTo());
                argt.add(Types.TIMESTAMP);
            }
        }

        query.append(" GROUP BY ");
        query.append(interestingColumns);

        // add having clause if 'good rating only' flag is set
        if (goodRatingsOnly != null) {
            query.append(" HAVING  ");
            query.append(DEFAULT_RATING_ALIAS_NAME);
            if (goodRatingsOnly) {
                query.append(" > ");
            } else {
                query.append(" <= ");
            }
            query.append("?");

            args.add(tenantService.getTenantById(tenantId).getRatingRangeNeutral());
            argt.add(Types.DOUBLE);
        }

        query.append(" ORDER BY ");
        if (sortByRatingInsteadOfActionTime == null || sortByRatingInsteadOfActionTime) {
            query.append(DEFAULT_RATING_ALIAS_NAME);
            query.append(" DESC");
            query.append(", ");
            query.append(DEFAULT_LAST_ACTION_TIME_ALIAS_NAME);
        } else {
            query.append(DEFAULT_LAST_ACTION_TIME_ALIAS_NAME);
            query.append(" DESC");
            query.append(", ");
            query.append(DEFAULT_RATING_ALIAS_NAME);
        }
        query.append(" DESC");

        // Note: for a non-mysql implementation this is need to be changed
        if (numberOfResults != null && numberOfResults > 0) {
            query.append(" LIMIT ?");

            args.add(numberOfResults);
            argt.add(Types.INTEGER);
        }

        return getJdbcTemplate().query(query.toString(), args.toArray(), Ints.toArray(argt), ratingVORowMapper);
    }


    ///////////////////////////////////////////////////////////////////////////
    // private methods
    private void validateNonEmptyFields(ActionVO<Integer, Integer> action,
                                        boolean useDateFromVO) {
        if (action.getTenant() == null) {
            throw new IllegalArgumentException(
                    "missing constraints, unique key (tenantId, itemTypeId, actionTypeId, actionTime) must be set, missing 'tenantId'");
        }
        if (action.getItem() == null) {
            throw new IllegalArgumentException(
                    "missing constraints, unique key (tenantId, itemTypeId, actionTypeId, actionTime) must be set, missing 'item'");
        }
        if (action.getItem().getType() == null) {
            throw new IllegalArgumentException(
                    "missing constraints, unique key (tenantId, itemTypeId, actionTypeId, actionTime) must be set, missing 'itemTypeId'");
        }
        if (action.getActionType() == null) {
            throw new IllegalArgumentException(
                    "missing constraints, unique key (tenantId, itemTypeId, actionTypeId, actionTime) must be set, missing 'actionTypeId'");
        }
        // in case of automatically generated 'actionTime' on database level leave out check for null
        if (useDateFromVO && action.getActionTime() == null) {
            throw new IllegalArgumentException(
                    "missing constraints, unique key (tenantId, itemTypeId, actionTypeId, actionTime) must be set, missing 'actionTime'");
        }
    }

    //////////////////////////////////////////////////////////////////////////////
    // private inner classes
    private class ActionVORowMapper implements RowMapper<ActionVO<Integer, Integer>> {
        public ActionVO<Integer, Integer> mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            ActionVO<Integer, Integer> actionVO =
                    new ActionVO<Integer, Integer>(
                            DaoUtils.getInteger(rs, DEFAULT_ID_COLUMN_NAME),
                            DaoUtils.getInteger(rs, DEFAULT_TENANT_COLUMN_NAME),
                            DaoUtils.getInteger(rs, DEFAULT_USER_COLUMN_NAME),
                            DaoUtils.getStringIfPresent(rs, DEFAULT_SESSION_COLUMN_NAME),
                            DaoUtils.getStringIfPresent(rs, DEFAULT_IP_COLUMN_NAME),
                            new ItemVO<Integer, Integer>(DaoUtils.getInteger(rs, DEFAULT_TENANT_COLUMN_NAME),
                                    DaoUtils.getInteger(rs, DEFAULT_ITEM_COLUMN_NAME),
                                    DaoUtils.getInteger(rs, DEFAULT_ITEM_TYPE_COLUMN_NAME)),
                            DaoUtils.getInteger(rs, DEFAULT_ACTION_TYPE_COLUMN_NAME),
                            DaoUtils.getInteger(rs, DEFAULT_RATING_VALUE_COLUMN_NAME),
                            DaoUtils.getBoolean(rs, DEFAULT_SEARCH_SUCCEEDED_COLUMN_NAME),
                            DaoUtils.getInteger(rs, DEFAULT_NUMBER_OF_FOUND_ITEMS),
                            DaoUtils.getStringIfPresent(rs, DEFAULT_DESCRIPTION_COLUMN_NAME),
                            DaoUtils.getDate(rs, DEFAULT_ACTION_TIME_COLUMN_NAME));
            return actionVO;
        }
    }

    private class RankedItemVORowMapper implements RowMapper<RankedItemVO<Integer, Integer>> {
        public RankedItemVO<Integer, Integer> mapRow(ResultSet rs, int rowNum) throws SQLException {
            RankedItemVO<Integer, Integer> rankedItem =
                    new RankedItemVO<Integer, Integer>(
                            new ItemVO<Integer, Integer>(DaoUtils.getInteger(rs, DEFAULT_TENANT_COLUMN_NAME),
                                    DaoUtils.getInteger(rs, DEFAULT_ITEM_COLUMN_NAME),
                                    DaoUtils.getInteger(rs, DEFAULT_ITEM_TYPE_COLUMN_NAME)),
                            DaoUtils.getInteger(rs, DEFAULT_ACTION_TYPE_COLUMN_NAME), rowNum + 1,
                            DaoUtils.getInteger(rs, DEFAULT_COUNT_ALIAS_NAME));
            return rankedItem;
        }
    }

    private class ItemVORowMapper implements RowMapper<ItemVO<Integer, Integer>> {
        public ItemVO<Integer, Integer> mapRow(ResultSet rs, int rowNum) throws SQLException {
            ItemVO<Integer, Integer> item = new ItemVO<Integer, Integer>(
                    DaoUtils.getInteger(rs, DEFAULT_TENANT_COLUMN_NAME),
                    DaoUtils.getInteger(rs, DEFAULT_ITEM_COLUMN_NAME),
                    DaoUtils.getInteger(rs, DEFAULT_ITEM_TYPE_COLUMN_NAME));
            return item;
        }
    }

    private class RatingVORowMapper implements RowMapper<RatingVO<Integer, Integer>> {
        public RatingVO<Integer, Integer> mapRow(ResultSet rs, int rowNum) throws SQLException {
            RatingVO<Integer, Integer> rating = new RatingVO<Integer, Integer>(
                    new ItemVO<Integer, Integer>(DaoUtils.getInteger(rs, DEFAULT_TENANT_COLUMN_NAME),
                            DaoUtils.getInteger(rs, DEFAULT_ITEM_COLUMN_NAME),
                            DaoUtils.getInteger(rs, DEFAULT_ITEM_TYPE_COLUMN_NAME)),
                    DaoUtils.getDouble(rs, DEFAULT_RATING_ALIAS_NAME),
                    DaoUtils.getInteger(rs, DEFAULT_COUNT_ALIAS_NAME),
                    DaoUtils.getDate(rs, DEFAULT_LAST_ACTION_TIME_ALIAS_NAME),
                    DaoUtils.getIntegerIfPresent(rs, DEFAULT_USER_COLUMN_NAME),
                    DaoUtils.getStringIfPresent(rs, DEFAULT_SESSION_COLUMN_NAME));
            return rating;
        }
    }
}
