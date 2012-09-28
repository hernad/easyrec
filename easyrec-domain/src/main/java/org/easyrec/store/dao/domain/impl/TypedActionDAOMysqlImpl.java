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

import org.easyrec.model.core.ActionVO;
import org.easyrec.model.core.ItemVO;
import org.easyrec.model.core.RankedItemVO;
import org.easyrec.model.core.RatingVO;
import org.easyrec.model.core.transfer.TimeConstraintVO;
import org.easyrec.service.domain.TypeMappingService;
import org.easyrec.store.dao.core.ActionDAO;
import org.easyrec.store.dao.domain.TypedActionDAO;
import org.easyrec.store.dao.impl.AbstractBaseActionDAOMysqlImpl;
import org.easyrec.utils.spring.store.ResultSetIteratorMysql;
import org.easyrec.utils.spring.store.dao.DaoUtils;
import org.easyrec.utils.spring.store.dao.annotation.DAO;
import org.easyrec.utils.spring.store.service.sqlscript.SqlScriptService;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * This class provides a typed implementation of the {@link org.easyrec.store.dao.domain.TypedActionDAO} interface.
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
public class TypedActionDAOMysqlImpl extends
        AbstractBaseActionDAOMysqlImpl<ActionVO<Integer, String>, RankedItemVO<Integer, String>, String, String, ItemVO<Integer, String>, RatingVO<Integer, String>, Integer, Integer>
        implements TypedActionDAO {
    // members
    private TypedActionVORowMapper actionVORowMapper = new TypedActionVORowMapper();

    private ActionDAO actionDAO;
    private TypeMappingService typeMappingService;

    // constructor
    public TypedActionDAOMysqlImpl(DataSource dataSource, ActionDAO actionDAO, TypeMappingService typeMappingService,
                                   SqlScriptService sqlScriptService) {
        super(sqlScriptService);
        setDataSource(dataSource);
        this.actionDAO = actionDAO;
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

    // abstract (generic) method implementation of 'AbstractBaseActionDAOMysqlImpl<TypedActionVO>'
    @Override
    public int insertAction(ActionVO<Integer, String> typedAction, boolean useDateFromVO) {
        Integer tenantId = typedAction.getTenant();
        if (tenantId == null) {
            throw new IllegalArgumentException("tenant not specified, can not retrieve type mapping without tenant");
        }
        return actionDAO.insertAction(typeMappingService.convertTypedActionVO(tenantId, typedAction), useDateFromVO);
    }

    @Override
    public int removeActionsByTenant(Integer tenantId) {
        return actionDAO.removeActionsByTenant(tenantId);
    }

    @Override
    public Iterator<ActionVO<Integer, String>> getActionIterator(int bulkSize) {
        return new ResultSetIteratorMysql<ActionVO<Integer, String>>(getDataSource(),
                bulkSize, getActionIteratorQueryString(), actionVORowMapper);
    }

    @Override
    public Iterator<ActionVO<Integer, String>> getActionIterator(int bulkSize,
                                                                                           TimeConstraintVO timeConstraints) {
        if (timeConstraints == null || timeConstraints.getDateFrom() == null && timeConstraints.getDateTo() == null) {
            return getActionIterator(bulkSize);
        }
        Object[] args = new Object[1];
        int[] argTypes = {Types.TIMESTAMP};

        DaoUtils.ArgsAndTypesHolder holder = new DaoUtils.ArgsAndTypesHolder(args, argTypes);
        String s = getActionIteratorQueryString(timeConstraints, holder);

        return new ResultSetIteratorMysql<ActionVO<Integer, String>>(getDataSource(),
                bulkSize, s, holder.getArgs(), holder.getArgTypes(), actionVORowMapper);
    }

    @Override
    public List<ActionVO<Integer, String>> getActionsFromUser(Integer tenant, Integer user,
                                                                                        String sessionId) {
        return typeMappingService.convertListOfActionVOs(tenant, actionDAO.getActionsFromUser(tenant, user, sessionId));
    }

    @Override
    public List<RankedItemVO<Integer, String>> getRankedItemsByActionType(Integer tenant,
                                                                                           String actionType,
                                                                                           String itemType,
                                                                                           Integer numberOfResults,
                                                                                           TimeConstraintVO timeConstraints,
                                                                                           Boolean sortDesc) {
        return typeMappingService.convertListOfRankedItemVOs(tenant, actionDAO
                .getRankedItemsByActionType(tenant, typeMappingService.getIdOfActionType(tenant, actionType),
                        typeMappingService.getIdOfItemType(tenant, itemType), numberOfResults, timeConstraints,
                        sortDesc));
    }


    public List<ItemVO<Integer, String>> getItemsOfTenant(final Integer tenant,
                                                                   final String consideredItemType) {
        return typeMappingService.convertListOfItemVOs(tenant,
                actionDAO.getItemsOfTenant(tenant, typeMappingService.getIdOfItemType(tenant, consideredItemType)));
    }

    @Override
    public List<ItemVO<Integer, String>> getItemsByUserActionAndType(Integer tenant, Integer user,
                                                                              String sessionId,
                                                                              String consideredActionType,
                                                                              String consideredItemType,
                                                                              Integer numberOfLastActionsConsidered) {
        return typeMappingService.convertListOfItemVOs(tenant, actionDAO
                .getItemsByUserActionAndType(tenant, user, sessionId,
                        typeMappingService.getIdOfActionType(tenant, consideredActionType),
                        typeMappingService.getIdOfItemType(tenant, consideredItemType), numberOfLastActionsConsidered));
    }

    @Override
    public List<ItemVO<Integer, String>> getItemsByUserActionAndType(Integer tenant, Integer user,
                                                                              String sessionId,
                                                                              String consideredActionType,
                                                                              String consideredItemType,
                                                                              Double ratingThreshold,
                                                                              Integer numberOfLastActionsConsidered) {
        return typeMappingService.convertListOfItemVOs(tenant, actionDAO
                .getItemsByUserActionAndType(tenant, user, sessionId,
                        typeMappingService.getIdOfActionType(tenant, consideredActionType),
                        typeMappingService.getIdOfItemType(tenant, consideredItemType), ratingThreshold, numberOfLastActionsConsidered));
    }

    @Override
    public List<RatingVO<Integer, String>> getDirectItemRatings(Integer tenant, Integer user,
                                                                                  String sessionId, String itemType,
                                                                                  Integer numberOfResults,
                                                                                  TimeConstraintVO timeRange,
                                                                                  Boolean sortByRatingInsteadOfActionTime,
                                                                                  Boolean goodRatingsOnly,
                                                                                  Integer tenantSpecificIdForRatingAction) {
        return typeMappingService.convertListOfRatingVOs(tenant, actionDAO
                .getDirectItemRatings(tenant, user, sessionId, typeMappingService.getIdOfItemType(tenant, itemType),
                        numberOfResults, timeRange, sortByRatingInsteadOfActionTime, goodRatingsOnly,
                        tenantSpecificIdForRatingAction));
    }

    @Override
    public Date getNewestActionDate(Integer tenant, Integer user, String sessionId) {
        return actionDAO.getNewestActionDate(tenant, user, sessionId);
    }

    //////////////////////////////////////////////////////////////////////////////
    // private inner classes
    private class TypedActionVORowMapper implements RowMapper<ActionVO<Integer, String>> {
        public ActionVO<Integer, String> mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            Integer tenantId = DaoUtils.getInteger(rs, DEFAULT_TENANT_COLUMN_NAME);

            return new ActionVO<Integer, String>(
                    DaoUtils.getInteger(rs, DEFAULT_ID_COLUMN_NAME), tenantId,
                    DaoUtils.getInteger(rs, DEFAULT_USER_COLUMN_NAME),
                    DaoUtils.getStringIfPresent(rs, DEFAULT_SESSION_COLUMN_NAME),
                    DaoUtils.getStringIfPresent(rs, DEFAULT_IP_COLUMN_NAME),
                    new ItemVO<Integer, String>(DaoUtils.getInteger(rs, DEFAULT_TENANT_COLUMN_NAME),
                            DaoUtils.getInteger(rs, DEFAULT_ITEM_COLUMN_NAME), typeMappingService
                            .getItemTypeById(tenantId, DaoUtils.getInteger(rs, DEFAULT_ITEM_TYPE_COLUMN_NAME))),
                    typeMappingService
                            .getActionTypeById(tenantId,
                                    DaoUtils.getInteger(rs, DEFAULT_ACTION_TYPE_COLUMN_NAME)),
                    DaoUtils.getInteger(rs, DEFAULT_RATING_VALUE_COLUMN_NAME),
                    DaoUtils.getBoolean(rs, DEFAULT_SEARCH_SUCCEEDED_COLUMN_NAME),
                    DaoUtils.getInteger(rs, DEFAULT_NUMBER_OF_FOUND_ITEMS),
                    DaoUtils.getStringIfPresent(rs, DEFAULT_DESCRIPTION_COLUMN_NAME),
                    DaoUtils.getDate(rs, DEFAULT_ACTION_TIME_COLUMN_NAME));
        }
    }
}