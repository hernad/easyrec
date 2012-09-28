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

import java.util.List;
import org.easyrec.model.core.ItemVO;
import org.easyrec.model.core.RecommendationVO;
import org.easyrec.service.core.RecommenderService;
import org.easyrec.service.domain.DomainRecommenderService;
import org.easyrec.service.domain.TypeMappingService;

/**
 * Implementation of the {@link org.easyrec.service.domain.DomainRecommenderService} interface.
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
public class DomainRecommenderServiceImpl implements DomainRecommenderService {
    // members
    protected RecommenderService recommenderService;
    protected TypeMappingService typeMappingService;

    // constructor
    public DomainRecommenderServiceImpl(RecommenderService recommenderService, TypeMappingService typeMappingService) {
        this.recommenderService = recommenderService;
        this.typeMappingService = typeMappingService;
    }

    // interface "RecommenderService" implementation 

    @Override
    public List<ItemVO<Integer, String>> getActionHistory(Integer tenantId, 
                                                          Integer userId, 
                                                          String sessionId, 
                                                          String consideredActionTypeId, 
                                                          String consideredItemTypeId, 
                                                          Double actionValueThreshold, 
                                                          Integer numberOfLastActionsConsidered) {
        return typeMappingService.convertListOfItemVOs(tenantId, 
                                                       recommenderService.getActionHistory(tenantId, 
                                                                                           userId, 
                                                                                           sessionId, 
                                                                                           typeMappingService.getIdOfActionType(tenantId, consideredActionTypeId), 
                                                                                           typeMappingService.getIdOfItemType(tenantId, consideredItemTypeId), 
                                                                                           actionValueThreshold, 
                                                                                           numberOfLastActionsConsidered));
    }
    
    
    
    @Override
    public RecommendationVO<Integer, String> getItemsBasedOnActionHistory(
            Integer tenant, Integer user, String sessionId, String consideredActionType, String consideredItemType,
            Double ratingThreshold, Integer numberOfLastActionsConsidered, String assocType, String requestedItemType) {
        return typeMappingService.convertRecommendationVO(tenant, recommenderService
                .getItemsBasedOnActionHistory(tenant, user, sessionId,
                        typeMappingService.getIdOfActionType(tenant, consideredActionType),
                        typeMappingService.getIdOfItemType(tenant, consideredItemType), ratingThreshold, numberOfLastActionsConsidered,
                        typeMappingService.getIdOfAssocType(tenant, assocType),
                        typeMappingService.getIdOfItemType(tenant, requestedItemType)));
    }

    @Override
    public RecommendationVO<Integer, String> getAlsoActedItems(Integer tenant,
                                                                                                 Integer user,
                                                                                                 String sessionId,
                                                                                                 String assocType,
                                                                                                 ItemVO<Integer, String> item,
                                                                                                 String filteredActionType,
                                                                                                 String requestedItemType) {
        return typeMappingService.convertRecommendationVO(tenant, recommenderService
                .getAlsoActedItems(tenant, user, sessionId, typeMappingService.getIdOfAssocType(tenant, assocType),
                        typeMappingService.convertTypedItemVO(tenant, item),
                        typeMappingService.getIdOfActionType(tenant, filteredActionType),
                        typeMappingService.getIdOfItemType(tenant, requestedItemType)));
    }

    @Override
    public RecommendationVO<Integer, String> recommend(Integer tenant,
                                                                                         ItemVO<Integer, String> item,
                                                                                         String assocType) {
        return typeMappingService.convertRecommendationVO(tenant, recommenderService
                .recommend(tenant, typeMappingService.convertTypedItemVO(tenant, item),
                        typeMappingService.getIdOfAssocType(tenant, assocType)));
    }

    //////////////////////////////////////////////////////////////////////////////    
    // interface "DomainRecommenderService" implementation      
    @Override
    public RecommendationVO<Integer, String> itemsBasedOnPurchaseHistory(
            Integer tenant, Integer user, String sessionId, String consideredItemType,
            Integer numberOfLastActionsConsidered, String assocType, String requestedItemType) {
        return getItemsBasedOnActionHistory(tenant, user, sessionId, TypeMappingService.ACTION_TYPE_BUY,
                consideredItemType, null, numberOfLastActionsConsidered, assocType, requestedItemType);
    }

    @Override
    public RecommendationVO<Integer, String> itemsBasedOnViewingHistory(
            Integer tenant, Integer user, String sessionId, String consideredItemType,
            Integer numberOfLastActionsConsidered, String assocType, String requestedItemType) {
        return getItemsBasedOnActionHistory(tenant, user, sessionId, TypeMappingService.ACTION_TYPE_VIEW,
                consideredItemType, null, numberOfLastActionsConsidered, assocType, requestedItemType);
    }

    @Override
    public RecommendationVO<Integer, String> itemsBasedOnSearchingHistory(
            Integer tenant, Integer user, String sessionId, String consideredItemType,
            Integer numberOfLastActionsConsidered, String assocType, String requestedItemType) {
        return getItemsBasedOnActionHistory(tenant, user, sessionId, TypeMappingService.ACTION_TYPE_SEARCH,
                consideredItemType, null, numberOfLastActionsConsidered, assocType, requestedItemType);
    }

    @Override
    public RecommendationVO<Integer, String> alsoBoughtItems(Integer tenant,
                                                                                               Integer user,
                                                                                               String sessionId,
                                                                                               ItemVO<Integer, String> item,
                                                                                               String requestedItemType) {
        return getAlsoActedItems(tenant, user, sessionId, TypeMappingService.ASSOC_TYPE_BOUGHT_TOGETHER, item, null,
                //PH: also filter items that have been bought, rated,... TypeMappingService.ACTION_TYPE_BUY,
                requestedItemType);
    }

    @Override
    public RecommendationVO<Integer, String> alsoViewedItems(Integer tenant,
                                                                                               Integer user,
                                                                                               String sessionId,
                                                                                               ItemVO<Integer, String> item,
                                                                                               String requestedItemType) {
        return getAlsoActedItems(tenant, user, sessionId, TypeMappingService.ASSOC_TYPE_VIEWED_TOGETHER, item, null,
                //PH: also filter items that have been bought, rated,... TypeMappingService.ACTION_TYPE_VIEW,
                requestedItemType);
    }

    @Override
    public RecommendationVO<Integer, String> alsoSearchedItems(Integer tenant,
                                                                                                 Integer user,
                                                                                                 String sessionId,
                                                                                                 ItemVO<Integer, String> item,
                                                                                                 String requestedItemType) {
        return getAlsoActedItems(tenant, user, sessionId, TypeMappingService.ASSOC_TYPE_SEARCHED_TOGETHER, item, null,
                //PH: also filter items that have been bought, rated,... TypeMappingService.ACTION_TYPE_SEARCH,
                requestedItemType);
    }

    @Override
    public RecommendationVO<Integer, String> alsoGoodRatedItems(Integer tenant,
                                                                                                  Integer user,
                                                                                                  String sessionId,
                                                                                                  ItemVO<Integer, String> item,
                                                                                                  String requestedItemType) {
        return getAlsoActedItems(tenant, user, sessionId, TypeMappingService.ASSOC_TYPE_GOOD_RATED_TOGETHER, item, null,
                //PH: also filter items that have been bought, rated,... TypeMappingService.ACTION_TYPE_RATE,
                requestedItemType);
    }
       
}
