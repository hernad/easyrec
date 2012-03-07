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
package org.easyrec.service.web;

import org.easyrec.model.core.*;
import org.easyrec.model.web.*;

import java.util.List;

/**
 * <DESCRIPTION>
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: szavrel $<br/>
 * $Date: 2012-02-02 18:49:38 +0100 (Do, 02 Feb 2012) $<br/>
 * $Revision: 18703 $</p>
 *
 * @author Stephan Zavrel
 */
public interface IDMappingService {
    public ItemVO<Integer, Integer> convertItem(Item item);

    public RankedItem convertRankedItemVO(/*Integer tenantId,*/
                                          RankedItemVO<Integer, String> rankedItemVO);

    public RankedItemVO<Integer, String> convertRankedItem(/*Integer tenantId,*/
                                                                            RankedItem rankedItem);

    public List<RankedItem> convertListOfRankedItemVOs(/*Integer tenantId,*/
                                                       List<RankedItemVO<Integer, String>> inList);

    public List<RankedItemVO<Integer, String>> convertListOfRankedItems(/*Integer tenantId,*/
                                                                                         List<RankedItem> inList);

    public Rating convertRatingVO(RatingVO<Integer, String> ratingVO);

    public RatingVO<Integer, String> convertRating(Rating rating);

    public List<Rating> convertListOfRatingVOs(List<RatingVO<Integer, String>> inList);

    public List<RatingVO<Integer, String>> convertListOfRatings(List<Rating> inList);

    public RecommendedItem convertRecommendedItemVO(RecommendedItemVO<Integer, String> recommendedItemVO);

    public RecommendedItemVO<Integer, String> convertRecommendedItem(RecommendedItem recommendedItem);

    public List<RecommendedItem> convertListOfRecommendedItemVOs(
            List<RecommendedItemVO<Integer, String>> inList);

    public List<RecommendedItemVO<Integer, String>> convertListOfRecommendedItems(
            List<RecommendedItem> inList);

    public List<Item> mapListOfItemVOs(List<ItemVO<Integer, Integer>> inList, RemoteTenant remoteTenant);
    
    public List<Item> mapListOfItemVOs(List<ItemVO<Integer, String>> inList,
            RemoteTenant remoteTenant, Integer userId, Session session, Integer numberOfRecommendations);

    public List<Item> mapRecommendedItems(
            RecommendationVO<Integer, String> recommendation,
            RemoteTenant remoteTenant, Integer userId, Session session, Integer numberOfRecommendations);

    public List<Item> mapRankedItems(List<RankedItemVO<Integer, String>> rankedItems,
                                     RemoteTenant remoteTenant, Session session, Integer numberOfRecommendations);

    public List<Item> mapRatedItems(List<RatingVO<Integer, String>> ratedItems,
                                    RemoteTenant remoteTenant, Session session, Integer numberOfRecommendations);


    public List<ItemAssocVO<String, String>> mapItemAssocs(
            List<ItemAssocVO<Integer, String>> itemAssocs,
            boolean filterInactiveRules);

    public List<Item> mapClusterItems(List<ItemVO<Integer, Integer>> clusterItems,
                                     RemoteTenant remoteTenant, Session session, Integer numberOfRecommendations);

}
