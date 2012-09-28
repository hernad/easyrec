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
package org.easyrec.service.domain.impl;

import org.easyrec.model.core.ActionVO;
import org.easyrec.model.core.ItemVO;
import org.easyrec.model.core.RankedItemVO;
import org.easyrec.model.core.RatingVO;
import org.easyrec.model.core.transfer.TimeConstraintVO;
import org.easyrec.service.core.ActionService;
import org.easyrec.service.domain.DomainActionService;
import org.easyrec.service.domain.TypeMappingService;
import org.easyrec.store.dao.domain.TypedActionDAO;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Implementation of the {@link org.easyrec.service.domain.DomainActionService} interface.
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
public class DomainActionServiceImpl implements DomainActionService {
    // members
    protected ActionService actionService;
    protected TypedActionDAO typedActionDAO;
    protected TypeMappingService typeMappingService;

    public DomainActionServiceImpl(ActionService actionService, TypedActionDAO typedActionDAO,
                                   TypeMappingService typeMappingService) {
        this.actionService = actionService;
        this.typedActionDAO = typedActionDAO;
        this.typeMappingService = typeMappingService;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // interface "ActionService" implementation

    public Iterator<ActionVO<Integer, String>> getActionIterator(int bulkSize) {
        return typedActionDAO.getActionIterator(bulkSize);
    }

    public Iterator<ActionVO<Integer, String>> getActionIterator(int bulkSize,
                                                                                           TimeConstraintVO timeConstraints) {
        return typedActionDAO.getActionIterator(bulkSize, timeConstraints);
    }

    public List<ActionVO<Integer, String>> getActionsFromUser(Integer tenantId,
                                                                                        Integer userId,
                                                                                        String sessionId) {
        return typedActionDAO.getActionsFromUser(tenantId, userId, sessionId);
    }

    public int insertAction(ActionVO<Integer, String> rating) {
        return typedActionDAO.insertAction(rating, false);
    }

    public int insertAction(ActionVO<Integer, String> rating, boolean usedateFromVO) {
        return typedActionDAO.insertAction(rating, usedateFromVO);
    }

    public int removeActionsByTenant(Integer tenant) {
        return typedActionDAO.removeActionsByTenant(tenant);
    }

    public void importActionsFromCSV(String fileName) {
        importActionsFromCSV(fileName, null);
    }

    public void importActionsFromCSV(String fileName, ActionVO<Integer, String> defaults) {
        actionService.importActionsFromCSV(fileName,
                typeMappingService.convertTypedActionVO(defaults.getTenant(), defaults));
    }

    public List<ItemVO<Integer, String>> getItemsOfTenant(final Integer tenant,
                                                                   final String consideredItemType) {
        return typeMappingService.convertListOfItemVOs(tenant,
                actionService.getItemsOfTenant(tenant, typeMappingService.getIdOfItemType(tenant, consideredItemType)));
    }

    public List<ItemVO<Integer, String>> getItemsByUserActionAndType(Integer tenant, Integer user,
                                                                              String sessionId,
                                                                              String consideredActionType,
                                                                              String consideredItemType,
                                                                              Double ratingThreshold,
                                                                              Integer numberOfLastActionsConsidered) {
        return typeMappingService.convertListOfItemVOs(tenant, actionService
                .getItemsByUserActionAndType(tenant, user, sessionId,
                        typeMappingService.getIdOfActionType(tenant, consideredActionType),
                        typeMappingService.getIdOfItemType(tenant, consideredItemType), ratingThreshold, numberOfLastActionsConsidered));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // interface "DomainActionService" implementation

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Actions
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public void purchaseItem(Integer tenant, Integer user, String sessionId, String ip,
                             ItemVO<Integer, String> item, String description) {
        typedActionDAO.insertAction(
                new ActionVO<Integer, String>(tenant, user, sessionId, ip, item,
                        TypeMappingService.ACTION_TYPE_BUY, null, null, null, description), false);
    }

    public void purchaseItem(Integer tenant, Integer user, String sessionId, String ip,
                             ItemVO<Integer, String> item, String description, Date actionTime) {
        typedActionDAO.insertAction(
                new ActionVO<Integer, String>(tenant, user, sessionId, ip, item,
                        TypeMappingService.ACTION_TYPE_BUY, null, null, null, description, actionTime), true);
    }

    public void viewItem(Integer tenant, Integer user, String sessionId, String ip,
                         ItemVO<Integer, String> item, String description) {
        typedActionDAO.insertAction(
                new ActionVO<Integer, String>(tenant, user, sessionId, ip, item,
                        TypeMappingService.ACTION_TYPE_VIEW, null, null, null, description), false);
    }

    public void viewItem(Integer tenant, Integer user, String sessionId, String ip,
                         ItemVO<Integer, String> item, String description, Date actionTime) {
        typedActionDAO.insertAction(
                new ActionVO<Integer, String>(tenant, user, sessionId, ip, item,
                        TypeMappingService.ACTION_TYPE_VIEW, null, null, null, description, actionTime), true);
    }

    public void rateItem(Integer tenant, Integer user, String sessionId, String ip,
                         ItemVO<Integer, String> item, Integer ratingValue, String description) {
        typedActionDAO.insertAction(
                new ActionVO<Integer, String>(tenant, user, sessionId, ip, item,
                        TypeMappingService.ACTION_TYPE_RATE, ratingValue, null, null, description), false);
    }

    public void rateItem(Integer tenant, Integer user, String sessionId, String ip,
                         ItemVO<Integer, String> item, Integer ratingValue, String description,
                         Date actionTime) {
        typedActionDAO.insertAction(
                new ActionVO<Integer, String>(tenant, user, sessionId, ip, item,
                        TypeMappingService.ACTION_TYPE_RATE, ratingValue, null, null, description, actionTime), true);
    }
    
    public void insertAction(Integer tenant, Integer user, String sessionId, String ip,
                         ItemVO<Integer, String> item, String actionType, Integer actionValue, String description) {
        typedActionDAO.insertAction(
                new ActionVO<Integer, String>(tenant, user, sessionId, ip, item,
                        actionType, actionValue, null, null, description), false);
    }
    
    
    public void insertAction(Integer tenant, Integer user, String sessionId, String ip,
                         ItemVO<Integer, String> item, String actionType, Integer actionValue, String description,
                         Date actionTime) {
        typedActionDAO.insertAction(
                new ActionVO<Integer, String>(tenant, user, sessionId, ip, item,
                        actionType, actionValue, null, null, description, actionTime), true);
    }

    
    
    public void searchItem(Integer tenant, Integer user, String sessionId, String ip,
                           ItemVO<Integer, String> item, Boolean searchSucceeded, Integer numberOfFoundItems,
                           String description) {
        typedActionDAO.insertAction(
                new ActionVO<Integer, String>(tenant, user, sessionId, ip, item,
                        TypeMappingService.ACTION_TYPE_SEARCH, null, searchSucceeded, numberOfFoundItems, description),
                false);
    }

    public void searchItem(Integer tenant, Integer user, String sessionId, String ip,
                           ItemVO<Integer, String> item, Boolean searchSucceeded, Integer numberOfFoundItems,
                           String description, Date actionTime) {
        typedActionDAO.insertAction(
                new ActionVO<Integer, String>(tenant, user, sessionId, ip, item,
                        TypeMappingService.ACTION_TYPE_SEARCH, null, searchSucceeded, numberOfFoundItems, description,
                        actionTime), true);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Rankings
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public List<RankedItemVO<Integer, String>> mostBoughtItems(Integer tenant, String itemType,
                                                                                Integer numberOfResults,
                                                                                TimeConstraintVO timeRange,
                                                                                Boolean sortDescending) {
        return typedActionDAO
                .getRankedItemsByActionType(tenant, TypeMappingService.ACTION_TYPE_BUY, itemType, numberOfResults,
                        timeRange, sortDescending);
    }

    public List<RankedItemVO<Integer, String>> mostViewedItems(Integer tenant, String itemType,
                                                                                Integer numberOfResults,
                                                                                TimeConstraintVO timeRange,
                                                                                Boolean sortDescending) {
        return typedActionDAO
                .getRankedItemsByActionType(tenant, TypeMappingService.ACTION_TYPE_VIEW, itemType, numberOfResults,
                        timeRange, sortDescending);
    }

    public List<RankedItemVO<Integer, String>> mostRatedItems(Integer tenant, String itemType,
                                                                               Integer numberOfResults,
                                                                               TimeConstraintVO timeRange,
                                                                               Boolean sortDescending) {
        return typedActionDAO
                .getRankedItemsByActionType(tenant, TypeMappingService.ACTION_TYPE_RATE, itemType, numberOfResults,
                        timeRange, sortDescending);
    }

    public List<RankedItemVO<Integer, String>> mostSearchedItems(Integer tenant, String itemType,
                                                                                  Integer numberOfResults,
                                                                                  TimeConstraintVO timeRange,
                                                                                  Boolean sortDescending) {
        return typedActionDAO
                .getRankedItemsByActionType(tenant, TypeMappingService.ACTION_TYPE_SEARCH, itemType, numberOfResults,
                        timeRange, sortDescending);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Ratings
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public List<RatingVO<Integer, String>> itemRatings(Integer tenant, Integer user, String sessionId,
                                                                         String itemType, Integer numberOfResults,
                                                                         TimeConstraintVO timeRange) {
        return typedActionDAO
                .getDirectItemRatings(tenant, user, sessionId, itemType, numberOfResults, timeRange, true, null,
                        typeMappingService.getIdOfActionType(tenant, TypeMappingService.ACTION_TYPE_RATE));
    }

    public List<RatingVO<Integer, String>> badItemRatings(Integer tenant, Integer user,
                                                                            String sessionId, String itemType,
                                                                            Integer numberOfResults,
                                                                            TimeConstraintVO timeRange) {
        return typedActionDAO
                .getDirectItemRatings(tenant, user, sessionId, itemType, numberOfResults, timeRange, true, false,
                        typeMappingService.getIdOfActionType(tenant, TypeMappingService.ACTION_TYPE_RATE));
    }

    public List<RatingVO<Integer, String>> goodItemRatings(Integer tenant, Integer user,
                                                                             String sessionId, String itemType,
                                                                             Integer numberOfResults,
                                                                             TimeConstraintVO timeRange) {
        return typedActionDAO
                .getDirectItemRatings(tenant, user, sessionId, itemType, numberOfResults, timeRange, true, true,
                        typeMappingService.getIdOfActionType(tenant, TypeMappingService.ACTION_TYPE_RATE));
    }

    public List<RatingVO<Integer, String>> lastGoodItemRatings(Integer tenant, Integer user,
                                                                                 String sessionId, String itemType,
                                                                                 Integer numberOfResults) {
        return typedActionDAO
                .getDirectItemRatings(tenant, user, sessionId, itemType, numberOfResults, null, false, true,
                        typeMappingService.getIdOfActionType(tenant, TypeMappingService.ACTION_TYPE_RATE));
    }

    // getter/setter
    public TypedActionDAO getTypedActionDAO() {
        return typedActionDAO;
    }

    public void setTypedActionDAO(TypedActionDAO typedActionDAO) {
        this.typedActionDAO = typedActionDAO;
    }

    public ActionService getActionService() {
        return actionService;
    }

    public void setActionService(ActionService actionService) {
        this.actionService = actionService;
    }

    public TypeMappingService getTypeMappingService() {
        return typeMappingService;
    }

    public void setTypeMappingService(TypeMappingService typeMappingService) {
        this.typeMappingService = typeMappingService;
    }
}
