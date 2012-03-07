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
package org.easyrec.service.core.impl;

import org.easyrec.model.core.AssociatedItemVO;
import org.easyrec.model.core.ItemVO;
import org.easyrec.model.core.RecommendationVO;
import org.easyrec.model.core.RecommendedItemVO;
import org.easyrec.model.core.transfer.IAConstraintVO;
import org.easyrec.service.core.ActionService;
import org.easyrec.service.core.ItemAssocService;
import org.easyrec.service.core.RecommendationHistoryService;
import org.easyrec.service.core.RecommenderService;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.List;

import static org.easyrec.util.core.RecommenderUtils.filterAlreadyActedOn;
import static org.easyrec.util.core.RecommenderUtils.filterDuplicates;

/**
 * Implementation of the {@link org.easyrec.service.core.RecommenderService} interface.
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
public class RecommenderServiceImpl implements RecommenderService, InitializingBean {
    // constants
    // HINT: move these constants to a RecommenderConfig class/bean (Mantis Issue: #724)
    private final static Integer DEFAULT_MAXIMUM_NUMBER_OF_RELATED_ITEMS_PER_ITEM = 100;
    private final static boolean DEFAULT_USE_AVERAGE_PREDICTION_VALUES_FOR_DUPLICATES = true;

    // HINT: introduce language dependent .property file (eg: messages.en.properties) for the following messages (Mantis Issue: #723)
    private final static String RECOMMENDATION_STRATEGY_ITEMS_BASED_ON_ACTION_HISTORY = "itemsBasedOnActionHistory";
    private final static String RECOMMENDATION_STRATEGY_ITEMS_ALSO_ACTED_ON = "itemsAlsoActedOn";

    private final static String EXPLANATION_ITEMS_BASED_ON_ACTION_HISTORY = "items that are related with those items the user acted on lately";
    private final static String EXPLANATION_ITEMS_ALSO_ACTED_ON = "items that are often acted on together with the given item";

    private final static String EXPLANATION_RELATED_1 = "this item is related to the currently acted on item '";
    private final static String EXPLANATION_RELATED_2 = "' via the assoc type '";

    //////////////////////////////////////////////////////////////////////////////
    // members
    private ActionService actionService;
    private ItemAssocService itemAssocService;
    private RecommendationHistoryService recommendationHistoryService;
    private Integer maximumNumberOfRelatedItemsPerItem = null;
    private boolean filterResults = true;

    //////////////////////////////////////////////////////////////////////////////
    // interface "RecommenderService" implementation
    @Override
    public List<ItemVO<Integer, Integer>> getActionHistory(Integer tenantId, 
                                                               Integer userId, 
                                                               String sessionId, 
                                                               Integer consideredActionTypeId,
                                                               Integer consideredItemTypeId,
                                                               Double ratingThreshold, 
                                                               Integer numberOfLastActionsConsidered) {
        List<ItemVO<Integer, Integer>> itemsActedOn = actionService
                .getItemsByUserActionAndType(tenantId, userId, sessionId, consideredActionTypeId, consideredItemTypeId, ratingThreshold,
                        numberOfLastActionsConsidered);
        
        
        return itemsActedOn;
    }
    
    
    @Override
    public RecommendationVO<Integer, Integer> getItemsBasedOnActionHistory(
            Integer tenantId, Integer userId, String sessionId, Integer consideredActionTypeId,
            Integer consideredItemTypeId, Double ratingThreshold, Integer numberOfLastActionsConsidered, Integer assocTypeId,
            Integer requestedItemTypeId) {
        List<ItemVO<Integer, Integer>> itemsActedOn = actionService
                .getItemsByUserActionAndType(tenantId, userId, sessionId, consideredActionTypeId, consideredItemTypeId, ratingThreshold,
                        numberOfLastActionsConsidered);
        List<AssociatedItemVO<Integer, Integer>> currentAssociatedItems;
        List<RecommendedItemVO<Integer, Integer>> allRecommendedItems = null;

        for (ItemVO<Integer, Integer> currentItem : itemsActedOn) {
            currentAssociatedItems = itemAssocService.getItemsTo(currentItem, assocTypeId, requestedItemTypeId,
                    new IAConstraintVO<Integer, Integer>(maximumNumberOfRelatedItemsPerItem, null, tenantId,
                            true));
            if (currentAssociatedItems != null) {
                if (allRecommendedItems == null) {
                    allRecommendedItems = convertAssociatedItems(currentAssociatedItems, currentItem);
                } else {
                    allRecommendedItems.addAll(convertAssociatedItems(currentAssociatedItems, currentItem));
                }
            }
        }

        // filter duplicates and history
        if (filterResults) {
            allRecommendedItems = doFiltering(tenantId, userId, sessionId, consideredActionTypeId, requestedItemTypeId,
                    allRecommendedItems, DEFAULT_USE_AVERAGE_PREDICTION_VALUES_FOR_DUPLICATES);
        }

        // create recommendation object
        RecommendationVO<Integer, Integer> recommendation = new RecommendationVO<Integer, Integer>(
                tenantId, userId, null, requestedItemTypeId, assocTypeId, consideredActionTypeId,
                RECOMMENDATION_STRATEGY_ITEMS_BASED_ON_ACTION_HISTORY, EXPLANATION_ITEMS_BASED_ON_ACTION_HISTORY,
                allRecommendedItems);

        // HINT: implement an aspect (instead of directly using this) (Mantis Issue: #650)
        if (recommendationHistoryService != null) recommendationHistoryService.insertRecommendation(recommendation);


        return recommendation;
    }

    @Override
    public RecommendationVO<Integer, Integer> getAlsoActedItems(Integer tenantId,
                                                                                                    Integer userId,
                                                                                                    String sessionId,
                                                                                                    Integer assocTypeId,
                                                                                                    ItemVO<Integer, Integer> item,
                                                                                                    Integer filteredActionTypeId,
                                                                                                    Integer requestedItemTypeId) {
        List<AssociatedItemVO<Integer, Integer>> associatedItems = itemAssocService
                .getItemsTo(item, assocTypeId, requestedItemTypeId,
                        new IAConstraintVO<Integer, Integer>(maximumNumberOfRelatedItemsPerItem, null, null,
                                null, tenantId, true, false));
        List<RecommendedItemVO<Integer, Integer>> recommendedItems = convertAssociatedItems(associatedItems,
                item);

        // filter duplicates and history
        if (filterResults) {
            recommendedItems = doFiltering(tenantId, userId, sessionId, filteredActionTypeId, requestedItemTypeId,
                    recommendedItems, DEFAULT_USE_AVERAGE_PREDICTION_VALUES_FOR_DUPLICATES);
        }

        RecommendationVO<Integer, Integer> recommendation = new RecommendationVO<Integer, Integer>(
                tenantId, userId, item.getItem(), item.getType(), assocTypeId, null,
                RECOMMENDATION_STRATEGY_ITEMS_ALSO_ACTED_ON, EXPLANATION_ITEMS_ALSO_ACTED_ON, recommendedItems);

        // HINT: implement an aspect (instead of directly using this) (Mantis Issue: #650)
        if (recommendationHistoryService != null) recommendationHistoryService.insertRecommendation(recommendation);

        return recommendation;
    }

    // HINT: implement this method (as soon as we support online recommendations)
    @Override
    public RecommendationVO<Integer, Integer> recommend(Integer tenant,
                                                        ItemVO<Integer, Integer> item,
                                                        Integer assocTypeId) {
        return null;
    }

    // getter/setter
    public void setActionService(ActionService actionService) {
        this.actionService = actionService;
    }

    public ActionService getActionService() {
        return actionService;
    }

    public void setItemAssocService(ItemAssocService itemAssocService) {
        this.itemAssocService = itemAssocService;
    }

    public ItemAssocService getItemAssocService() {
        return itemAssocService;
    }

    public void setRecommendationHistoryService(RecommendationHistoryService recommendationHistoryService) {
        this.recommendationHistoryService = recommendationHistoryService;
    }

    public RecommendationHistoryService getRecommendationHistoryService() {
        return recommendationHistoryService;
    }

    public Integer getMaximumNumberOfRelatedItemsPerItem() {
        return maximumNumberOfRelatedItemsPerItem;
    }

    public void setMaximumNumberOfRelatedItemsPerItem(Integer maximumNumberOfRelatedItemsPerItem) {
        this.maximumNumberOfRelatedItemsPerItem = maximumNumberOfRelatedItemsPerItem;
    }

    public boolean isFilterResults() {
        return filterResults;
    }

    public void setFilterResults(boolean filterResults) {
        this.filterResults = filterResults;
    }


    //////////////////////////////////////////////////////////////////////////////
    // private methods
    private List<RecommendedItemVO<Integer, Integer>> convertAssociatedItems(
            List<AssociatedItemVO<Integer, Integer>> associatedItems,
            ItemVO<Integer, Integer> currentItem) {
        List<RecommendedItemVO<Integer, Integer>> recommendedItems = new ArrayList<RecommendedItemVO<Integer, Integer>>();
        for (AssociatedItemVO<Integer, Integer> currentAssociatedItem : associatedItems) {
            StringBuilder explanation = new StringBuilder(EXPLANATION_RELATED_1);
            explanation.append(currentItem.getItem());
            explanation.append(EXPLANATION_RELATED_2);
            explanation.append(currentAssociatedItem.getAssocType());
            explanation.append("'");
            recommendedItems.add(new RecommendedItemVO<Integer, Integer>(currentAssociatedItem.getItem(),
                    currentAssociatedItem.getAssocValue(), currentAssociatedItem.getItemAssocId(),
                    explanation.toString()));
        }
        return recommendedItems;
    }

    private List<RecommendedItemVO<Integer, Integer>> doFiltering(Integer tenantId, Integer userId,
                                                                           String sessionId, Integer actionTypeId,
                                                                           Integer itemTypeId,
                                                                           List<RecommendedItemVO<Integer, Integer>> recommendedItems,
                                                                           boolean useAveragePredictionValues) {
        // filter duplicates 
        recommendedItems = filterDuplicates(recommendedItems, useAveragePredictionValues);

        // filter out shortly acted on items
        List<ItemVO<Integer, Integer>> itemsActedOn = null;
        if (userId != null || sessionId != null) {
            itemsActedOn = actionService
                    .getItemsByUserActionAndType(tenantId, userId, sessionId, actionTypeId, itemTypeId, null, null);
        }

        if (itemsActedOn != null && itemsActedOn.size() > 0) {
            filterAlreadyActedOn(recommendedItems, itemsActedOn);
        }
        return recommendedItems;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (maximumNumberOfRelatedItemsPerItem == null)
            maximumNumberOfRelatedItemsPerItem = DEFAULT_MAXIMUM_NUMBER_OF_RELATED_ITEMS_PER_ITEM;
    }


}