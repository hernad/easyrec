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

import org.easyrec.model.core.ItemVO;
import org.easyrec.model.core.RecommendedItemVO;
import org.easyrec.model.core.transfer.TimeConstraintVO;
import org.easyrec.store.dao.core.RecommendationDAO;
import org.easyrec.store.dao.core.RecommendedItemDAO;
import org.easyrec.store.dao.impl.AbstractBaseRecommendedItemDAOMysqlImpl;
import org.easyrec.utils.spring.store.ResultSetIteratorMysql;
import org.easyrec.utils.spring.store.dao.DaoUtils;
import org.easyrec.utils.spring.store.dao.annotation.DAO;
import org.easyrec.utils.spring.store.service.sqlscript.SqlScriptService;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Iterator;
import java.util.List;

/**
 * This class provides a Mysql implementation of the {@link org.easyrec.store.dao.core.RecommendedItemDAO} interface.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: sat-rsa $<br/>
 * $Date: 2012-02-23 16:01:50 +0100 (Do, 23 Feb 2012) $<br/>
 * $Revision: 140 $</p>
 *
 * @author Roman Cerny
 */

@DAO
public class RecommendedItemDAOMysqlImpl
        extends AbstractBaseRecommendedItemDAOMysqlImpl<RecommendedItemVO<Integer, Integer>, Integer>
        implements RecommendedItemDAO {
    // members
    private RecommendedItemVORowMapper recommendedItemVORowMapper = new RecommendedItemVORowMapper();

    // constructor 
    public RecommendedItemDAOMysqlImpl(DataSource dataSource, SqlScriptService sqlScriptService) {
        super(sqlScriptService);
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

    // abstract (generic) method implementation of 'AbstractBaseActionDAOMysqlImpl<RecommendedItemVO>'
    @Override
    public int insertRecommendedItem(RecommendedItemVO<Integer, Integer> recommendedItem) {
        if (logger.isTraceEnabled()) {
            logger.trace("inserting recommendedItem=" + recommendedItem);
        }

        // validate unique key
        validateUniqueKey(recommendedItem);

        StringBuilder query = new StringBuilder("INSERT INTO ");
        query.append(DEFAULT_TABLE_NAME);
        query.append(" SET ");
        query.append(DEFAULT_ITEM_COLUMN_NAME);
        query.append("=?, ");
        query.append(DEFAULT_ITEM_TYPE_COLUMN_NAME);
        query.append("=?, ");
        query.append(DEFAULT_RECOMMENDATION_COLUMN_NAME);
        query.append("=?, ");
        query.append(DEFAULT_PREDICTION_VALUE_COLUMN_NAME);
        query.append("=?, ");
        query.append(DEFAULT_ITEM_ASSOC_COLUMN_NAME);
        query.append("=?, ");
        query.append(DEFAULT_EXPLANATION_COLUMN_NAME);
        query.append("=?");

        Object[] args = {recommendedItem.getItem().getItem(), recommendedItem.getItem().getType(),
                recommendedItem.getRecommendationId(), recommendedItem.getPredictionValue(),
                recommendedItem.getItemAssocId(), recommendedItem.getExplanation()};
        int[] argTypes = {Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.DOUBLE, Types.INTEGER, Types.VARCHAR};

        PreparedStatementCreatorFactory factory = new PreparedStatementCreatorFactory(query.toString(), argTypes);
        factory.setReturnGeneratedKeys(true);
        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsAffected = getJdbcTemplate().update(factory.newPreparedStatementCreator(args), keyHolder);

        // retrieve auto increment id, and set to VO
        recommendedItem.setId(keyHolder.getKey().intValue());

        return rowsAffected;
    }

    @Override
    public RecommendedItemVO<Integer, Integer> loadRecommendedItem(Integer recommendedItemId) {
        // validate input
        if (recommendedItemId == null) {
            throw new IllegalArgumentException("missing 'recommendedItemId'");
        }
        if (logger.isTraceEnabled()) {
            logger.trace("rertrieving recommendedItem with id=" + recommendedItemId);
        }

        Object[] args = {recommendedItemId};
        int[] argTypes = {Types.INTEGER};

        return getJdbcTemplate()
                .queryForObject(getRecommendedItemQueryString(), args, argTypes, recommendedItemVORowMapper);
    }

    @Override
    public Iterator<RecommendedItemVO<Integer, Integer>> getRecommendedItemIterator(int bulkSize) {
        return new ResultSetIteratorMysql<RecommendedItemVO<Integer, Integer>>(getDataSource(), bulkSize,
                getRecommendedItemIteratorQueryString(), recommendedItemVORowMapper);
    }

    @Override
    public Iterator<RecommendedItemVO<Integer, Integer>> getRecommendedItemIterator(int bulkSize,
                                                                                             TimeConstraintVO timeConstraints) {
        if (timeConstraints == null || timeConstraints.getDateFrom() == null && timeConstraints.getDateTo() == null) {
            return getRecommendedItemIterator(bulkSize);
        }
        Object[] args = new Object[1];
        int[] argTypes = {Types.TIMESTAMP};

        DaoUtils.ArgsAndTypesHolder holder = new DaoUtils.ArgsAndTypesHolder(args, argTypes);
        String s = getRecommendedItemIteratorQueryString(timeConstraints, holder);

        return new ResultSetIteratorMysql<RecommendedItemVO<Integer, Integer>>(getDataSource(), bulkSize, s,
                holder.getArgs(), holder.getArgTypes(), recommendedItemVORowMapper);
    }

    @Override
    public List<RecommendedItemVO<Integer, Integer>> getRecommendedItems(TimeConstraintVO timeConstraints) {
        if (timeConstraints == null || timeConstraints.getDateFrom() == null && timeConstraints.getDateTo() == null) {
            throw new IllegalArgumentException(
                    "missing 'timeConstraints', if you prefer to retrieve ALL recommended items (without specifying any time constraints) use method 'getRecommendedItemIterator()' instead");
        }
        Object[] args = new Object[1];
        int[] argTypes = {Types.TIMESTAMP};

        DaoUtils.ArgsAndTypesHolder holder = new DaoUtils.ArgsAndTypesHolder(args, argTypes);
        String s = getRecommendedItemIteratorQueryString(timeConstraints, holder);

        return getJdbcTemplate().query(s, holder.getArgs(), holder.getArgTypes(), recommendedItemVORowMapper);
    }

    @Override
    public List<RecommendedItemVO<Integer, Integer>> getRecommendedItemsOfRecommendation(
            Integer recommendationId) {
        // validate input parameters
        if (recommendationId == null) {
            throw new IllegalArgumentException("missing 'recommendationId'");
        }

        Object[] args = new Object[]{recommendationId};
        int[] argTypes = {Types.INTEGER};
        return getJdbcTemplate()
                .query(getRecommendedItemsOfRecommendationQueryStringJoinRecommendation(), args, argTypes,
                        recommendedItemVORowMapper);
    }

    @Override
    public List<RecommendedItemVO<Integer, Integer>> getRecommendedItemsOfRecommendation(
            Integer recommendationId, Integer tenantId) {
        // validate input parameters
        if (tenantId == null) {
            getRecommendedItemsOfRecommendation(recommendationId);
        }

        if (recommendationId == null) {
            throw new IllegalArgumentException("missing 'recommendationId'");
        }

        Object[] args = new Object[]{recommendationId};
        int[] argTypes = {Types.INTEGER};
        return getJdbcTemplate().query(getRecommendedItemsOfRecommendationQueryString(), args, argTypes,
                new TenantRecommendedItemVORowMapper(tenantId));
    }

    //////////////////////////////////////////////////////////////////////////////
    // private methods
    private void validateUniqueKey(RecommendedItemVO<Integer, Integer> recommendedItem) {
        if (recommendedItem.getItem() == null) {
            throw new IllegalArgumentException(
                    "missing constraints, unique key (itemId, itemTypeId, recommendationId) must be set, missing 'item'");
        }
        if (recommendedItem.getItem().getItem() == null) {
            throw new IllegalArgumentException(
                    "missing constraints, unique key (itemId, itemTypeId, recommendationId) must be set, missing 'itemId'");
        }
        if (recommendedItem.getItem().getType() == null) {
            throw new IllegalArgumentException(
                    "missing constraints, unique key (itemId, itemTypeId, recommendationId) must be set, missing 'itemTypeId'");
        }
        if (recommendedItem.getRecommendationId() == null) {
            throw new IllegalArgumentException(
                    "missing constraints, unique key (itemId, itemTypeId, recommendationId) must be set, missing 'recommendationId'");
        }
    }

    private String getRecommendedItemQueryString() {
        String recAlias = "rec";
        String recItemAlias = "recItem";

        // join with recommendation (to retrieve tenantId)
        StringBuilder sqlString = new StringBuilder("SELECT ");
        sqlString.append(recItemAlias);
        sqlString.append(".*, ");
        sqlString.append(recAlias);
        sqlString.append(".");
        sqlString.append(RecommendationDAO.DEFAULT_TENANT_COLUMN_NAME);
        sqlString.append(" FROM ");
        sqlString.append(DEFAULT_TABLE_NAME);
        sqlString.append(" AS ");
        sqlString.append(recItemAlias);
        sqlString.append(", ");
        sqlString.append(RecommendationDAO.DEFAULT_TABLE_NAME);
        sqlString.append(" AS ");
        sqlString.append(recAlias);
        sqlString.append(" WHERE ");
        sqlString.append(recItemAlias);
        sqlString.append(".");
        sqlString.append(DEFAULT_RECOMMENDATION_COLUMN_NAME);
        sqlString.append("=");
        sqlString.append(recAlias);
        sqlString.append(".");
        sqlString.append(RecommendationDAO.DEFAULT_ID_COLUMN_NAME);
        sqlString.append(" AND ");
        sqlString.append(recItemAlias);
        sqlString.append(".");
        sqlString.append(DEFAULT_ID_COLUMN_NAME);
        sqlString.append("=?");
        return sqlString.toString();
    }

    private String getRecommendedItemsOfRecommendationQueryString() {
        StringBuilder sqlString = new StringBuilder("SELECT * FROM ");
        sqlString.append(DEFAULT_TABLE_NAME);
        sqlString.append(" WHERE ");
        sqlString.append(DEFAULT_RECOMMENDATION_COLUMN_NAME);
        sqlString.append("=?");
        return sqlString.toString();
    }

    private String getRecommendedItemsOfRecommendationQueryStringJoinRecommendation() {
        String recAlias = "rec";
        String recItemAlias = "recItem";

        StringBuilder sqlString = new StringBuilder("SELECT ");
        sqlString.append(recItemAlias);
        sqlString.append(".*, ");
        sqlString.append(recAlias);
        sqlString.append(".");
        sqlString.append(RecommendationDAO.DEFAULT_TENANT_COLUMN_NAME);
        sqlString.append(" FROM ");
        sqlString.append(DEFAULT_TABLE_NAME);
        sqlString.append(" AS ");
        sqlString.append(recItemAlias);
        sqlString.append(", ");
        sqlString.append(RecommendationDAO.DEFAULT_TABLE_NAME);
        sqlString.append(" AS ");
        sqlString.append(recAlias);
        sqlString.append(" WHERE ");
        sqlString.append(recItemAlias);
        sqlString.append(".");
        sqlString.append(DEFAULT_RECOMMENDATION_COLUMN_NAME);
        sqlString.append("=");
        sqlString.append(recAlias);
        sqlString.append(".");
        sqlString.append(RecommendationDAO.DEFAULT_ID_COLUMN_NAME);
        sqlString.append(" AND ");
        sqlString.append(DEFAULT_RECOMMENDATION_COLUMN_NAME);
        sqlString.append("=?");
        return sqlString.toString();
    }

    //////////////////////////////////////////////////////////////////////////////
    // private inner classes
    private class RecommendedItemVORowMapper implements RowMapper<RecommendedItemVO<Integer, Integer>> {
        public RecommendedItemVO<Integer, Integer> mapRow(ResultSet rs, int rowNum) throws SQLException {
            RecommendedItemVO<Integer, Integer> recommendedItem = new RecommendedItemVO<Integer, Integer>(
                    DaoUtils.getInteger(rs, DEFAULT_ID_COLUMN_NAME), new ItemVO<Integer, Integer>(
                            DaoUtils.getInteger(rs, RecommendationDAO.DEFAULT_TENANT_COLUMN_NAME),
                            DaoUtils.getInteger(rs, DEFAULT_ITEM_COLUMN_NAME),
                            DaoUtils.getInteger(rs, DEFAULT_ITEM_TYPE_COLUMN_NAME)),
                    DaoUtils.getDouble(rs, DEFAULT_PREDICTION_VALUE_COLUMN_NAME),
                    DaoUtils.getInteger(rs, DEFAULT_RECOMMENDATION_COLUMN_NAME),
                    DaoUtils.getInteger(rs, DEFAULT_ITEM_ASSOC_COLUMN_NAME),
                    DaoUtils.getStringIfPresent(rs, DEFAULT_EXPLANATION_COLUMN_NAME));
            return recommendedItem;
        }
    }

    private class TenantRecommendedItemVORowMapper implements RowMapper<RecommendedItemVO<Integer, Integer>> {
        private Integer tenantId;

        public TenantRecommendedItemVORowMapper(Integer tenantId) {
            this.tenantId = tenantId;
        }

        public RecommendedItemVO<Integer, Integer> mapRow(ResultSet rs, int rowNum) throws SQLException {
            RecommendedItemVO<Integer, Integer> recommendedItem = new RecommendedItemVO<Integer, Integer>(
                    DaoUtils.getInteger(rs, DEFAULT_ID_COLUMN_NAME),
                    new ItemVO<Integer, Integer>(tenantId, DaoUtils.getInteger(rs, DEFAULT_ITEM_COLUMN_NAME),
                            DaoUtils.getInteger(rs, DEFAULT_ITEM_TYPE_COLUMN_NAME)),
                    DaoUtils.getDouble(rs, DEFAULT_PREDICTION_VALUE_COLUMN_NAME),
                    DaoUtils.getInteger(rs, DEFAULT_RECOMMENDATION_COLUMN_NAME),
                    DaoUtils.getInteger(rs, DEFAULT_ITEM_ASSOC_COLUMN_NAME),
                    DaoUtils.getStringIfPresent(rs, DEFAULT_EXPLANATION_COLUMN_NAME));
            return recommendedItem;
        }
    }
}
