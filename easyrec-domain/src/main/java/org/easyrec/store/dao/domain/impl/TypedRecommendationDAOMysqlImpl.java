/**Copyright 2010 Research Studios Austria Forschungsgesellschaft mBH
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
package org.easyrec.store.dao.domain.impl;

import org.easyrec.model.core.RecommendationVO;
import org.easyrec.model.core.transfer.TimeConstraintVO;
import org.easyrec.service.domain.TypeMappingService;
import org.easyrec.store.dao.core.RecommendationDAO;
import org.easyrec.store.dao.core.RecommendedItemDAO;
import org.easyrec.store.dao.domain.TypedRecommendationDAO;
import org.easyrec.store.dao.impl.AbstractBaseRecommendationDAOMysqlImpl;
import org.easyrec.utils.spring.store.ResultSetIteratorMysql;
import org.easyrec.utils.spring.store.dao.DaoUtils;
import org.easyrec.utils.spring.store.dao.annotation.DAO;
import org.easyrec.utils.spring.store.service.sqlscript.SqlScriptService;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Iterator;

/**
 * This class provides a typed implementation of the {@link org.easyrec.store.dao.domain.TypedRecommendationDAO} interface.
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
public class TypedRecommendationDAOMysqlImpl extends
        AbstractBaseRecommendationDAOMysqlImpl<RecommendationVO<Integer, String>>
        implements TypedRecommendationDAO {
    // members
    private TypedRecommendationVORowMapper recommendationVORowMapper = new TypedRecommendationVORowMapper();

    private RecommendationDAO recommendationDAO;
    private RecommendedItemDAO recommendedItemDAO;
    private TypeMappingService typeMappingService;

    // constructor 
    public TypedRecommendationDAOMysqlImpl(DataSource dataSource, RecommendationDAO recommendationDAO,
                                           RecommendedItemDAO recommendedItemDAO, TypeMappingService typeMappingService,
                                           SqlScriptService sqlScriptService) {
        super(sqlScriptService);
        setDataSource(dataSource);
        this.recommendationDAO = recommendationDAO;
        this.recommendedItemDAO = recommendedItemDAO;
        this.typeMappingService = typeMappingService;

        // output connection information
        if (logger.isInfoEnabled()) {
            try {
                logger.info(DaoUtils.getDatabaseURLAndUserName(dataSource));
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    // abstract (generic) method implementation of 'AbstractBaseRecommendationDAOMysqlImpl<TypedRecommendationVO>'
    @Override
    public int insertRecommendation(
            RecommendationVO<Integer, String> typedRecommendation) {
        Integer tenantId = typedRecommendation.getTenant();
        if (tenantId == null) {
            throw new IllegalArgumentException("tenant not specified, can not retrieve type mapping without tenant");
        }
        return recommendationDAO
                .insertRecommendation(typeMappingService.convertTypedRecommendationVO(tenantId, typedRecommendation));
    }

    @Override
    public RecommendationVO<Integer, String> loadRecommendation(
            Integer recommendationId) {
        RecommendationVO<Integer, Integer> loadedRecommendation = recommendationDAO
                .loadRecommendation(recommendationId);
        Integer tenantId = loadedRecommendation.getTenant();
        if (tenantId == null) {
            throw new IllegalArgumentException("tenant not specified, can not retrieve type mapping without tenant");
        }
        return typeMappingService.convertRecommendationVO(tenantId, loadedRecommendation);
    }

    @Override
    public Iterator<RecommendationVO<Integer, String>> getRecommendationIterator(
            int bulkSize) {
        return new ResultSetIteratorMysql<RecommendationVO<Integer, String>>(
                getDataSource(), bulkSize, getRecommendationIteratorQueryString(), recommendationVORowMapper);
    }

    @Override
    public Iterator<RecommendationVO<Integer, String>> getRecommendationIterator(
            int bulkSize, TimeConstraintVO timeConstraints) {
        if (timeConstraints == null || timeConstraints.getDateFrom() == null && timeConstraints.getDateTo() == null) {
            return getRecommendationIterator(bulkSize);
        }
        Object[] args = new Object[1];
        int[] argTypes = {Types.TIMESTAMP};

        DaoUtils.ArgsAndTypesHolder holder = new DaoUtils.ArgsAndTypesHolder(args, argTypes);
        String s = getRecommendationIteratorQueryString(timeConstraints, holder);

        return new ResultSetIteratorMysql<RecommendationVO<Integer, String>>(
                getDataSource(), bulkSize, s, holder.getArgs(), holder.getArgTypes(), recommendationVORowMapper);
    }

    //////////////////////////////////////////////////////////////////////////////
    // private inner classes
    private class TypedRecommendationVORowMapper
            implements RowMapper<RecommendationVO<Integer, String>> {
        public RecommendationVO<Integer, String> mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            Integer tenantId = DaoUtils.getInteger(rs, DEFAULT_TENANT_COLUMN_NAME);
            if (tenantId == null) {
                throw new IllegalArgumentException(
                        "tenant not specified, can not retrieve type mapping without tenant");
            }
            return new RecommendationVO<Integer, String>(
                    DaoUtils.getInteger(rs, DEFAULT_ID_COLUMN_NAME), tenantId,
                    DaoUtils.getInteger(rs, DEFAULT_USER_COLUMN_NAME),
                    DaoUtils.getInteger(rs, DEFAULT_QUERIED_ITEM_COLUMN_NAME), typeMappingService
                    .getItemTypeById(tenantId, DaoUtils.getInteger(rs, DEFAULT_QUERIED_ITEM_TYPE_COLUMN_NAME)),
                    typeMappingService.getAssocTypeById(tenantId,
                            DaoUtils.getInteger(rs, DEFAULT_QUERIED_ASSOC_TYPE_COLUMN_NAME)), typeMappingService
                    .getActionTypeById(tenantId, DaoUtils.getInteger(rs, DEFAULT_RELATED_ACTION_TYPE_COLUMN_NAME)),
                    DaoUtils.getStringIfPresent(rs, DEFAULT_RECOMMENDATION_STRATEGY_COLUMN_NAME),
                    DaoUtils.getStringIfPresent(rs, DEFAULT_EXPLANATION_COLUMN_NAME),
                    DaoUtils.getDate(rs, DEFAULT_RECOMMENDATION_TIME_COLUMN_NAME), typeMappingService
                    .convertListOfRecommendedItemVOs(tenantId, recommendedItemDAO
                            .getRecommendedItemsOfRecommendation(DaoUtils.getInteger(rs, DEFAULT_ID_COLUMN_NAME))));
        }
    }
}
