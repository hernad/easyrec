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
package org.easyrec.service.domain;

import org.easyrec.model.core.*;
import org.easyrec.model.core.transfer.IAConstraintVO;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Provides methods to convert a typed (string based) VO into an integer based VO.
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

public interface TypeMappingService {
    // constants for all possible types (well known types; convenience constants)
    public final static String ACTION_TYPE_ADD_TO_PLAYLIST = "ADD_TO_PLAYLIST";
    public final static String ACTION_TYPE_BUY = "BUY";
    public final static String ACTION_TYPE_PREVIEW = "PREVIEW";
    public final static String ACTION_TYPE_RATE = "RATE";
    public final static String ACTION_TYPE_SEARCH = "SEARCH";
    public final static String ACTION_TYPE_VIEW = "VIEW";
    public final static String ACTION_TYPE_CHOOSE_TOPIC = "CHOOSE_TOPIC";

    public final static String AGGREGATE_TYPE_AVERAGE = "AVERAGE";
    public final static String AGGREGATE_TYPE_FIRST = "FIRST";
    public final static String AGGREGATE_TYPE_MAXIMUM = "MAXIMUM";
    public final static String AGGREGATE_TYPE_MOST_FREQUENT = "MOST_FREQUENT";
    public final static String AGGREGATE_TYPE_NEWEST = "NEWEST";
    public final static String AGGREGATE_TYPE_OLDEST = "OLDEST";

    public final static String ASSOC_TYPE_BOUGHT_TOGETHER = "BOUGHT_TOGETHER";
    public final static String ASSOC_TYPE_COLL_TOGETHER = "COLL_TOGETHER";
    public final static String ASSOC_TYPE_IS_ELEMENT_OF = "IS_ELEMENT_OF";
    public final static String ASSOC_TYPE_IS_SIMILAR_TO = "IS_SIMILAR_TO";
    public final static String ASSOC_TYPE_LIKES = "LIKES";
    public final static String ASSOC_TYPE_RATED_TOGETHER = "RATED_TOGETHER";
    public final static String ASSOC_TYPE_SEARCHED_TOGETHER = "SEARCHED_TOGETHER";
    public final static String ASSOC_TYPE_SOUNDS_SIMILAR = "SOUNDS_SIMILAR";
    public final static String ASSOC_TYPE_VIEWED_TOGETHER = "VIEWED_TOGETHER";
    public final static String ASSOC_TYPE_GOOD_RATED_TOGETHER = "GOOD_RATED_TOGETHER";
    public final static String ASSOC_TYPE_USER_TO_ITEM = "USER_TO_ITEM";

    public final static String ITEM_TYPE_ALBUM = "ALBUM";
    public final static String ITEM_TYPE_ARTIST = "ARTIST";
    public final static String ITEM_TYPE_ASSET = "ASSET";
    public final static String ITEM_TYPE_CLUSTER = "CLUSTER";
    public final static String ITEM_TYPE_GENRE_CLUSTER = "GENRE_CLUSTER";
    public final static String ITEM_TYPE_PLAYLIST = "PLAYLIST";
    public final static String ITEM_TYPE_PROTOTYPE_TRACK = "PROTOTYPE_TRACK";
    public final static String ITEM_TYPE_SOUND_CLUSTER = "SOUND_CLUSTER";
    public final static String ITEM_TYPE_TRACK = "TRACK";
    public final static String ITEM_TYPE_USER = "USER";

    public final static String SOURCE_TYPE_AMG = "AMG";
    public final static String SOURCE_TYPE_FE = "FE";
    public final static String SOURCE_TYPE_RMG = "RMG";
    public final static String SOURCE_TYPE_ARM = "ARM";

    public final static String VIEW_TYPE_ADMIN = "ADMIN";
    public final static String VIEW_TYPE_COMMUNITY = "COMMUNITY";
    public final static String VIEW_TYPE_SYSTEM = "SYSTEM";

    // convert single model objects
    public ActionVO<Integer, String> convertActionVO(Integer tenantId,
                                                                               ActionVO<Integer, Integer> action);

    public ActionVO<Integer, Integer> convertTypedActionVO(Integer tenantId,
                                                                                      ActionVO<Integer, String> typedAction);

    public AssociatedItemVO<Integer, String> convertAssociatedItemVO(Integer tenantId,
                                                                                      AssociatedItemVO<Integer, Integer> associatedItem);

    public AssociatedItemVO<Integer, Integer> convertTypedAssociatedItemVO(Integer tenantId,
                                                                                             AssociatedItemVO<Integer, String> typedAssociatedItem);

    public IAConstraintVO<Integer, String> convertIAConstraintVO(Integer tenantId,
                                                                         IAConstraintVO<Integer, Integer> constraint);

    public IAConstraintVO<Integer, Integer> convertTypedIAConstraintVO(Integer tenantId,
                                                                                IAConstraintVO<Integer, String> typedConstraint);

    public ItemAssocVO<Integer, String> convertItemAssocVO(Integer tenantId,
                                                                                            ItemAssocVO<Integer,Integer> itemAssoc);

    public ItemAssocVO<Integer,Integer> convertTypedItemAssocVO(Integer tenantId,
                                                                                                     ItemAssocVO<Integer, String> typedItemAssoc);

    public ItemVO<Integer, String> convertItemVO(Integer tenantId, ItemVO<Integer, Integer> item);

    public ItemVO<Integer, Integer> convertTypedItemVO(Integer tenantId,
                                                                ItemVO<Integer, String> typedItem);

    public RankedItemVO<Integer, String> convertRankedItemVO(Integer tenantId,
                                                                              RankedItemVO<Integer, Integer> rankedItem);

    public RankedItemVO<Integer, Integer> convertTypedRankedItemVO(Integer tenantId,
                                                                                     RankedItemVO<Integer, String> typedRankedItem);

    public RatingVO<Integer, String> convertRatingVO(Integer tenantId,
                                                                       RatingVO<Integer, Integer> rating);

    public RatingVO<Integer, Integer> convertTypedRatingVO(Integer tenantId,
                                                                             RatingVO<Integer, String> typedRating);

    public RecommendationVO<Integer, String> convertRecommendationVO(Integer tenantId,
                                                                                                       RecommendationVO<Integer, Integer> recommendation);

    public RecommendationVO<Integer, Integer> convertTypedRecommendationVO(
            Integer tenantId, RecommendationVO<Integer, String> typedRecommendation);

    public RecommendedItemVO<Integer, String> convertRecommendedItemVO(Integer tenantId,
                                                                                RecommendedItemVO<Integer, Integer> recommendedItem);

    public RecommendedItemVO<Integer, Integer> convertTypedRecommendedItemVO(Integer tenantId,
                                                                                      RecommendedItemVO<Integer, String> typedRecommendedItem);

    // convert lists of model objects
    public List<ActionVO<Integer, String>> convertListOfActionVOs(Integer tenantId,
                                                                                            List<ActionVO<Integer, Integer>> inList);

    public List<ActionVO<Integer, Integer>> convertListOfTypedActionVOs(Integer tenantId,
                                                                                                   List<ActionVO<Integer, String>> inList);

    public List<AssociatedItemVO<Integer, String>> convertListOfAssociatedItemVOs(Integer tenantId,
                                                                                                   List<AssociatedItemVO<Integer, Integer>> inList);

    public List<AssociatedItemVO<Integer, Integer>> convertListOfTypedAssociatedItemVOs(
            Integer tenantId, List<AssociatedItemVO<Integer, String>> inList);

    public List<IAConstraintVO<Integer, String>> convertListOfIAConstraintVOs(Integer tenantId,
                                                                                      List<IAConstraintVO<Integer, Integer>> inList);

    public List<IAConstraintVO<Integer, Integer>> convertListOfTypedIAConstraintVOs(Integer tenantId,
                                                                                             List<IAConstraintVO<Integer, String>> inList);

    public List<ItemAssocVO<Integer, String>> convertListOfItemAssocVOs(
            Integer tenantId, List<ItemAssocVO<Integer,Integer>> inList);

    public List<ItemAssocVO<Integer,Integer>> convertListOfTypedItemAssocVOs(
            Integer tenantId, List<ItemAssocVO<Integer, String>> inList);

    public List<ItemVO<Integer, String>> convertListOfItemVOs(Integer tenantId,
                                                                       List<ItemVO<Integer, Integer>> inList);

    public List<ItemVO<Integer, Integer>> convertListOfTypedItemVOs(Integer tenantId,
                                                                             List<ItemVO<Integer, String>> inList);

    public List<RankedItemVO<Integer, String>> convertListOfRankedItemVOs(Integer tenantId,
                                                                                           List<RankedItemVO<Integer, Integer>> inList);

    public List<RankedItemVO<Integer, Integer>> convertListOfTypedRankedItemVOs(Integer tenantId,
                                                                                                  List<RankedItemVO<Integer, String>> inList);

    public List<RatingVO<Integer, String>> convertListOfRatingVOs(Integer tenantId,
                                                                                    List<RatingVO<Integer, Integer>> inList);

    public List<RatingVO<Integer, Integer>> convertListOfTypedRatingVOs(Integer tenantId,
                                                                                          List<RatingVO<Integer, String>> inList);

    public List<RecommendationVO<Integer, String>> convertListOfRecommendationVO(
            Integer tenantId, List<RecommendationVO<Integer, Integer>> inList);

    public List<RecommendationVO<Integer, Integer>> convertListOfTypedRecommendationVO(
            Integer tenantId, List<RecommendationVO<Integer, String>> inList);

    public List<RecommendedItemVO<Integer, String>> convertListOfRecommendedItemVOs(Integer tenantId,
                                                                                             List<RecommendedItemVO<Integer, Integer>> inList);

    public List<RecommendedItemVO<Integer, Integer>> convertListOfTypedRecommendedItemVOs(Integer tenantId,
                                                                                                   List<RecommendedItemVO<Integer, String>> inList);

    public List<RecommendedItemVO<Integer, String>> convertListOfRecommendedItemVOs(
            List<RecommendedItemVO<Integer, Integer>> inList);

    public List<RecommendedItemVO<Integer, Integer>> convertListOfTypedRecommendedItemVOs(
            List<RecommendedItemVO<Integer, String>> inList);

    // get integer based id of type
    public Integer getIdOfActionType(Integer tenantId, String actionType);

    public Integer getIdOfAggregateType(Integer tenantId, String aggregateType);

    public Integer getIdOfAssocType(Integer tenantId, String assocType);

    public Integer getIdOfAssocType(Integer tenantId, String assocType, Boolean visible);

    public Integer getIdOfItemType(Integer tenantId, String itemType);

    public Integer getIdOfItemType(Integer tenantId, String itemType, Boolean visible);

    public Integer getIdOfSourceType(Integer tenantId, String sourceType);

    public Integer getIdOfViewType(Integer tenantId, String viewType);

    // get string based id of type
    public String getActionTypeById(Integer tenantId, Integer actionTypeId);

    public String getAggregateTypeById(Integer tenantId, Integer aggregateTypeId);

    public String getAssocTypeById(Integer tenantId, Integer assocTypeId);

    public String getItemTypeById(Integer tenantId, Integer itemTypeId);

    public String getSourceTypeById(Integer tenantId, Integer sourceTypeId);

    public String getViewTypeById(Integer tenantId, Integer viewTypeId);

    // get mappings of types
    public HashMap<String, Integer> getActionTypeMapping(Integer tenantId);

    public HashMap<String, Integer> getAggregateTypeMapping(Integer tenantId);

    public HashMap<String, Integer> getAssocTypeMapping(Integer tenantId);

    public HashMap<String, Integer> getItemTypeMapping(Integer tenantId);

    public HashMap<String, Integer> getSourceTypeMapping(Integer tenantId);

    public HashMap<String, Integer> getViewTypeMapping(Integer tenantId);

    // get set of types
    public Set<String> getActionTypes(Integer tenantId);

    public Set<String> getAggregateTypes(Integer tenantId);

    public Set<String> getAssocTypes(Integer tenantId);

    public Set<String> getAssocTypes(Integer tenantId, Boolean visible);

    public Set<String> getItemTypes(Integer tenantId);

    public Set<String> getItemTypes(Integer tenantId, Boolean visible);

    public Set<String> getSourceTypes(Integer tenantId);

    public Set<String> getViewTypes(Integer tenantId);
}
