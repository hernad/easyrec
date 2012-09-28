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

import org.easyrec.model.core.RecommendationVO;
import org.easyrec.model.core.RecommendedItemVO;
import org.easyrec.model.core.transfer.TimeConstraintVO;
import org.easyrec.store.dao.core.RecommendationDAO;
import org.easyrec.store.dao.core.RecommendedItemDAO;
import org.easyrec.store.dao.impl.AbstractBaseRecommendationDAOMysqlImpl;
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

/**
 * This class provides a Mysql implementation of the {@link org.easyrec.store.dao.core.RecommendationDAO} interface.
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
public class RecommendationDAOMysqlImpl extends
        AbstractBaseRecommendationDAOMysqlImpl<RecommendationVO<Integer, Integer>>
        implements RecommendationDAO {
    // members
    private RecommendationVORowMapper recommendationVORowMapper = new RecommendationVORowMapper();

    private RecommendedItemDAO recommendedItemDAO;

    // constructor 
    public RecommendationDAOMysqlImpl(DataSource dataSource, RecommendedItemDAO recommendedItemDAO,
                                      SqlScriptService sqlScriptService) {
        super(sqlScriptService);
        setDataSource(dataSource);
        this.recommendedItemDAO = recommendedItemDAO;

        // output connection information
        if (logger.isInfoEnabled()) {
            try {
                logger.info(DaoUtils.getDatabaseURLAndUserName(dataSource));
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    // abstract (generic) method implementation of 'AbstractBaseRecommendationDAOMysqlImpl<RecommendationVO>'
    @Override
    public int insertRecommendation(
            RecommendationVO<Integer, Integer> recommendation) {
        if (logger.isTraceEnabled()) {
            logger.trace("inserting recommendation=" + recommendation);
        }

        // validate unique key
        validateUniqueKey(recommendation);

        StringBuilder query = new StringBuilder("INSERT INTO ");
        query.append(DEFAULT_TABLE_NAME);
        query.append(" SET ");
        query.append(DEFAULT_TENANT_COLUMN_NAME);
        query.append("=?, ");
        query.append(DEFAULT_USER_COLUMN_NAME);
        query.append("=?, ");
        query.append(DEFAULT_QUERIED_ITEM_COLUMN_NAME);
        query.append("=?, ");
        query.append(DEFAULT_QUERIED_ITEM_TYPE_COLUMN_NAME);
        query.append("=?, ");
        query.append(DEFAULT_QUERIED_ASSOC_TYPE_COLUMN_NAME);
        query.append("=?, ");
        query.append(DEFAULT_RELATED_ACTION_TYPE_COLUMN_NAME);
        query.append("=?, ");
        query.append(DEFAULT_RECOMMENDATION_STRATEGY_COLUMN_NAME);
        query.append("=?, ");
        query.append(DEFAULT_EXPLANATION_COLUMN_NAME);
        query.append("=?, ");
        query.append(DEFAULT_RECOMMENDATION_TIME_COLUMN_NAME);
        query.append("=NOW()");

        Object[] args = {recommendation.getTenant(), recommendation.getUser(), recommendation.getQueriedItem(),
                recommendation.getQueriedItemType(), recommendation.getQueriedAssocType(),
                recommendation.getRelatedActionType(), recommendation.getRecommendationStrategy(),
                recommendation.getExplanation()};
        int[] argTypes = {Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER,
                Types.VARCHAR, Types.VARCHAR};

        PreparedStatementCreatorFactory factory = new PreparedStatementCreatorFactory(query.toString(), argTypes);
        factory.setReturnGeneratedKeys(true);
        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsAffected = getJdbcTemplate().update(factory.newPreparedStatementCreator(args), keyHolder);

        // retrieve auto increment id, and set to VO
        recommendation.setId(keyHolder.getKey().intValue());

        // inserting all RecommendedItemVOs (if recommended items exist)
        if (recommendation.getRecommendedItems() != null && recommendation.getRecommendedItems().size() > 0) {
            for (RecommendedItemVO<Integer, Integer> recommendedItem : recommendation.getRecommendedItems()) {
                // store auto increment id to all RecommendedItemVOs
                recommendedItem.setRecommendationId(recommendation.getId());
                recommendedItemDAO.insertRecommendedItem(recommendedItem);
            }
        }
        return rowsAffected;
    }

    @Override
    public RecommendationVO<Integer, Integer> loadRecommendation(
            Integer recommendationId) {
        // validate input
        if (recommendationId == null) {
            throw new IllegalArgumentException("missing 'recommendationId'");
        }
        if (logger.isTraceEnabled()) {
            logger.trace("retrieving recommendation with id=" + recommendationId);
        }

        Object[] args = {recommendationId};
        int[] argTypes = {Types.INTEGER};

        return getJdbcTemplate()
                .queryForObject(getRecommendationQueryString(), args, argTypes, recommendationVORowMapper);
    }

    @Override
    public Iterator<RecommendationVO<Integer, Integer>> getRecommendationIterator(
            int bulkSize) {
        return new ResultSetIteratorMysql<RecommendationVO<Integer, Integer>>(
                getDataSource(), bulkSize, getRecommendationIteratorQueryString(), recommendationVORowMapper);
    }

    @Override
    public Iterator<RecommendationVO<Integer, Integer>> getRecommendationIterator(
            int bulkSize, TimeConstraintVO timeConstraints) {
        if (timeConstraints == null || timeConstraints.getDateFrom() == null && timeConstraints.getDateTo() == null) {
            return getRecommendationIterator(bulkSize);
        }
        Object[] args = new Object[1];
        int[] argTypes = {Types.TIMESTAMP};

        DaoUtils.ArgsAndTypesHolder holder = new DaoUtils.ArgsAndTypesHolder(args, argTypes);
        String s = getRecommendationIteratorQueryString(timeConstraints, holder);

        return new ResultSetIteratorMysql<RecommendationVO<Integer, Integer>>(
                getDataSource(), bulkSize, s, holder.getArgs(), holder.getArgTypes(), recommendationVORowMapper);
    }

    //////////////////////////////////////////////////////////////////////////////
    // private methods
    private void validateUniqueKey(
            RecommendationVO<Integer, Integer> recommendation) {
        // NOTE: leave out check for 'recommendationTime' since it is automatically generated on database level
        //       do not check for recommended items either, since each recommendation should be stored for future evaluations,
        //       store recommendations even if no items could be recommended.
        if (recommendation.getTenant() == null) {
            throw new IllegalArgumentException(
                    "missing constraints, unique key (tenantId, recommendationTime) must be set, missing 'itemId'");
        }
    }

    //////////////////////////////////////////////////////////////////////////////
    // public inner classes
    public class RecommendationVORowMapper
            implements RowMapper<RecommendationVO<Integer, Integer>> {
        public RecommendationVO<Integer, Integer> mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            RecommendationVO<Integer, Integer> recommendation = new RecommendationVO<Integer, Integer>(
                    DaoUtils.getInteger(rs, DEFAULT_ID_COLUMN_NAME),
                    DaoUtils.getInteger(rs, DEFAULT_TENANT_COLUMN_NAME),
                    DaoUtils.getInteger(rs, DEFAULT_USER_COLUMN_NAME),
                    DaoUtils.getInteger(rs, DEFAULT_QUERIED_ITEM_COLUMN_NAME),
                    DaoUtils.getInteger(rs, DEFAULT_QUERIED_ITEM_TYPE_COLUMN_NAME),
                    DaoUtils.getInteger(rs, DEFAULT_QUERIED_ASSOC_TYPE_COLUMN_NAME),
                    DaoUtils.getInteger(rs, DEFAULT_RELATED_ACTION_TYPE_COLUMN_NAME),
                    DaoUtils.getStringIfPresent(rs, DEFAULT_RECOMMENDATION_STRATEGY_COLUMN_NAME),
                    DaoUtils.getStringIfPresent(rs, DEFAULT_EXPLANATION_COLUMN_NAME),
                    DaoUtils.getDate(rs, DEFAULT_RECOMMENDATION_TIME_COLUMN_NAME), recommendedItemDAO
                    .getRecommendedItemsOfRecommendation(DaoUtils.getInteger(rs, DEFAULT_ID_COLUMN_NAME)));
            return recommendation;
        }
    }
}
