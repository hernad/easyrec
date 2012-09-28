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

import org.easyrec.model.core.ItemVO;
import org.easyrec.model.core.RecommendedItemVO;
import org.easyrec.model.core.transfer.TimeConstraintVO;
import org.easyrec.service.domain.TypeMappingService;
import org.easyrec.store.dao.core.RecommendationDAO;
import org.easyrec.store.dao.core.RecommendedItemDAO;
import org.easyrec.store.dao.domain.TypedRecommendedItemDAO;
import org.easyrec.store.dao.impl.AbstractBaseRecommendedItemDAOMysqlImpl;
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
import java.util.List;

/**
 * This class provides a typed implementation of the {@link org.easyrec.store.dao.domain.TypedRecommendedItemDAO} interface.
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
public class TypedRecommendedItemDAOMysqlImpl
        extends AbstractBaseRecommendedItemDAOMysqlImpl<RecommendedItemVO<Integer, String>, Integer>
        implements TypedRecommendedItemDAO {
    // members
    private TypedRecommendedItemVORowMapper recommendedItemVORowMapper = new TypedRecommendedItemVORowMapper();

    private RecommendedItemDAO recommendedItemDAO;
    private TypeMappingService typeMappingService;

    // constructor 
    public TypedRecommendedItemDAOMysqlImpl(DataSource dataSource, RecommendedItemDAO recommendedItemDAO,
                                            TypeMappingService typeMappingService, SqlScriptService sqlScriptService) {
        super(sqlScriptService);
        setDataSource(dataSource);
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

    // abstract (generic) method implementation of 'AbstractBaseActionDAOMysqlImpl<TypedRecommendedItemVO>'
    @Override
    public int insertRecommendedItem(RecommendedItemVO<Integer, String> recommendedItem) {
        if (recommendedItem.getItem() == null) {
            throw new IllegalArgumentException("item was null, inserting was stopped");
        }
        Integer tenantId = recommendedItem.getItem().getTenant();
        if (tenantId == null) {
            throw new IllegalArgumentException("tenant not specified, can not retrieve type mapping without tenant");
        }
        return recommendedItemDAO
                .insertRecommendedItem(typeMappingService.convertTypedRecommendedItemVO(tenantId, recommendedItem));
    }

    @Override
    public RecommendedItemVO<Integer, String> loadRecommendedItem(Integer recommendedItemId) {
        RecommendedItemVO<Integer, Integer> loadedRecommendedItem = recommendedItemDAO
                .loadRecommendedItem(recommendedItemId);
        if (loadedRecommendedItem.getItem() == null) {
            throw new IllegalArgumentException("item was null, loading was stopped");
        }
        Integer tenantId = loadedRecommendedItem.getItem().getTenant();
        if (tenantId == null) {
            throw new IllegalArgumentException("tenant not specified, can not retrieve type mapping without tenant");
        }
        return typeMappingService.convertRecommendedItemVO(tenantId, loadedRecommendedItem);
    }

    @Override
    public Iterator<RecommendedItemVO<Integer, String>> getRecommendedItemIterator(int bulkSize) {
        return new ResultSetIteratorMysql<RecommendedItemVO<Integer, String>>(getDataSource(), bulkSize,
                getRecommendedItemIteratorQueryString(), recommendedItemVORowMapper);
    }

    @Override
    public Iterator<RecommendedItemVO<Integer, String>> getRecommendedItemIterator(int bulkSize,
                                                                                            TimeConstraintVO timeConstraints) {
        if (timeConstraints == null || timeConstraints.getDateFrom() == null && timeConstraints.getDateTo() == null) {
            return getRecommendedItemIterator(bulkSize);
        }
        Object[] args = new Object[1];
        int[] argTypes = {Types.TIMESTAMP};

        DaoUtils.ArgsAndTypesHolder holder = new DaoUtils.ArgsAndTypesHolder(args, argTypes);
        String s = getRecommendedItemIteratorQueryString(timeConstraints, holder);

        return new ResultSetIteratorMysql<RecommendedItemVO<Integer, String>>(getDataSource(), bulkSize, s,
                holder.getArgs(), holder.getArgTypes(), recommendedItemVORowMapper);
    }

    @Override
    public List<RecommendedItemVO<Integer, String>> getRecommendedItems(TimeConstraintVO timeConstraints) {
        return typeMappingService
                .convertListOfRecommendedItemVOs(recommendedItemDAO.getRecommendedItems(timeConstraints));
    }

    @Override
    public List<RecommendedItemVO<Integer, String>> getRecommendedItemsOfRecommendation(
            Integer recommendationId) {
        return typeMappingService.convertListOfRecommendedItemVOs(
                recommendedItemDAO.getRecommendedItemsOfRecommendation(recommendationId));
    }

    @Override
    public List<RecommendedItemVO<Integer, String>> getRecommendedItemsOfRecommendation(
            Integer recommendationId, Integer tenantId) {
        if (tenantId == null) {
            throw new IllegalArgumentException("tenant not specified, can not retrieve type mapping without tenant");
        }
        return typeMappingService.convertListOfRecommendedItemVOs(tenantId,
                recommendedItemDAO.getRecommendedItemsOfRecommendation(recommendationId, tenantId));
    }

    //////////////////////////////////////////////////////////////////////////////
    // private inner classes
    private class TypedRecommendedItemVORowMapper implements RowMapper<RecommendedItemVO<Integer, String>> {
        public RecommendedItemVO<Integer, String> mapRow(ResultSet rs, int rowNum) throws SQLException {
            Integer tenantId = DaoUtils.getInteger(rs, RecommendationDAO.DEFAULT_TENANT_COLUMN_NAME);
            if (tenantId == null) {
                throw new IllegalArgumentException(
                        "tenant not specified, can not retrieve type mapping without tenant");
            }
            return new RecommendedItemVO<Integer, String>(
                    DaoUtils.getInteger(rs, DEFAULT_ID_COLUMN_NAME),
                    new ItemVO<Integer, String>(tenantId, DaoUtils.getInteger(rs, DEFAULT_ITEM_COLUMN_NAME),
                            typeMappingService
                                    .getItemTypeById(tenantId, DaoUtils.getInteger(rs, DEFAULT_ITEM_TYPE_COLUMN_NAME))),
                    DaoUtils.getDouble(rs, DEFAULT_PREDICTION_VALUE_COLUMN_NAME),
                    DaoUtils.getInteger(rs, DEFAULT_RECOMMENDATION_COLUMN_NAME),
                    DaoUtils.getInteger(rs, DEFAULT_ITEM_ASSOC_COLUMN_NAME),
                    DaoUtils.getStringIfPresent(rs, DEFAULT_EXPLANATION_COLUMN_NAME));
        }
    }
}
