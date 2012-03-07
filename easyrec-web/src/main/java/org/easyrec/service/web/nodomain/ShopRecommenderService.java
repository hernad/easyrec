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
package org.easyrec.service.web.nodomain;

import org.easyrec.model.core.ClusterVO;
import org.easyrec.model.core.ItemAssocVO;
import org.easyrec.model.core.transfer.TimeConstraintVO;
import org.easyrec.model.web.*;
import org.easyrec.soap.nodomain.exception.EasyRecSoapException;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Recommender Webservice wrapper interface
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
public interface ShopRecommenderService {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Actions
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * storing a 'purchase item' action in the recommender storage
     *
     * @param remoteTenant    tenant identifier
     * @param userId          optional user id
     * @param itemId          the id of the item that has been bought
     * @param itemType        the item type of the bought item
     * @param itemDescription any description that might help (e.g. a human readable form of this action)
     * @param itemUrl         TODO
     * @param itemImageUrl    TODO
     * @param actionTime      TODO
     * @param session         optional session id
     * @return TODO
     */
    public Item purchaseItem(RemoteTenant remoteTenant, String userId, String itemId, String itemType,
                             String itemDescription, String itemUrl, String itemImageUrl, Date actionTime,
                             Session session);

    /**
     * storing a 'view item' action in the recommender storage
     *
     * @param remoteTenant    tenant identifier
     * @param userId          optional user id
     * @param itemId          the id of the item that has been viewed
     * @param itemType        the item type of the viewed item
     * @param itemDescription any description that might help (e.g. a human readable form of this action)
     * @param itemUrl         TODO
     * @param itemImageUrl    TODO
     * @param actionTime      TODO
     * @param session         optional session id
     * @return TODO
     */
    public Item viewItem(RemoteTenant remoteTenant, String userId, String itemId, String itemType,
                         String itemDescription, String itemUrl, String itemImageUrl, Date actionTime, Session session);

    /**
     * storing a 'rate item' action in the recommender storage
     *
     * @param remoteTenant    tenant identifier
     * @param userId          optional user id
     * @param itemId          the id of the item that has been rated
     * @param itemType        the item type of the rated item
     * @param itemDescription any description that might help (e.g. a human readable form of this action)
     * @param itemUrl         TODO
     * @param itemImageUrl    TODO
     * @param ratingValue     TODO
     * @param actionTime      TODO
     * @param session         optional session id
     * @return TODO
     */
    public Item rateItem(RemoteTenant remoteTenant, String userId, String itemId, String itemType,
                         String itemDescription, String itemUrl, String itemImageUrl, Integer ratingValue,
                         Date actionTime, Session session);
    /**
     * storing a 'search item' action in the recommender storage
     *
     * @param tenant tenant identifier
     * @param userId optional user id
     * @param sessionId optional session id
     * @param ip optional ip of the client host
     * @param itemId  the id of the item that has been searched
     * @param itemType the item type of the searched item
     * @param description any description that might help (e.g. a human readable form of this action)
     * @throws ShopRecommenderException
     */
    //public void searchItem(Integer tenant, String userId, String sessionId, String ip, String itemId, String itemType, Boolean searchSucceeded, Integer numberOfFoundItems, String description) throws ShopRecommenderException;

     /**
     * storing an action of the given actiontype in the recommender storage
     *
     * @param remoteTenant    tenant identifier
     * @param userId          optional user id
     * @param itemId          the id of the item that has been rated
     * @param itemType        the item type of the rated item
     * @param itemDescription any description that might help (e.g. a human readable form of this action)
     * @param itemUrl         TODO
     * @param itemImageUrl    TODO
     * @param actionType
     * @param actionValue
     * @param actionTime      TODO
     * @param session         required session id
     * @return TODO
     */
    public Item sendAction(RemoteTenant remoteTenant, String userId, String itemId, String itemType,
                         String itemDescription, String itemUrl, String itemImageUrl, String actionType, Integer actionValue,
                         Date actionTime, Session session);
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Rankings
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * returns 'bought items' (of a given item type for a given tenant) ranked by the frequency of 'buy' actions
     *
     * @param tenant          tenant identifier
     * @param itemType        the item type of interest
     * @param numberOfResults maximum number of {@link RankedItem}s in the result
     * @param timeRange       an optional time range (fromTime and toTime), only buys within that range are taken into the rankings computation
     * @param constraint      TODO
     * @param session         TODO
     * @return an array of {@link RankedItem}s, sorted by rank
     */
    public List<Item> mostBoughtItems(Integer tenant, @Nullable String itemType, Integer numberOfResults,
                                      String timeRange, @Nullable TimeConstraintVO constraint, Session session);

    /**
     * returns 'viewed items' (of a given item type for a given tenant) ranked by the frequency of 'view' actions
     *
     * @param tenant          tenant identifier
     * @param itemType        the item type of interest
     * @param numberOfResults maximum number of {@link RankedItem}s in the result
     * @param timeRange       an optional time range (fromTime and toTime), only buys within that range are taken into the rankings computation
     * @param constraint      TODO
     * @param session         TODO
     * @return an array of {@link RankedItem}s, sorted by rank
     */
    public List<Item> mostViewedItems(Integer tenant, @Nullable String itemType, Integer numberOfResults,
                                      String timeRange, @Nullable TimeConstraintVO constraint, Session session);

    /**
     * returns 'rated items' (of a given item type for a given tenant) ranked by the frequency of 'rate' actions
     * rating values are NOT taken into consideration, thus simply showing how often an item was rated at all
     *
     * @param tenant          tenant identifier
     * @param itemType        the item type of interest
     * @param numberOfResults maximum number of {@link RankedItem}s in the result
     * @param timeRange       an optional time range (fromTime and toTime), only buys within that range are taken into the rankings computation
     * @param constraint      TODO
     * @param session         TODO
     * @return an array of {@link RankedItem}s, sorted by rank
     */
    public List<Item> mostRatedItems(Integer tenant, @Nullable String itemType, Integer numberOfResults,
                                     String timeRange, @Nullable TimeConstraintVO constraint, Session session);

    /**
     * returns items in a cluster ranked and selected by the strategy parameter.
     * if less than {@code numberOfResult} items are found and {@code useFallback} is true also items from other
     * clusters near the origin cluster are considered.
     *
     * @param tenant          tenant identifier
     * @param clusterName     name of the cluster
     * @param numberOfResults maximum number of {@link RankedItem}s in the result
     * @param strategy        the strategy to use for retrieving items.
     * @param useFallback     if {@code true} then items from neighboring clusters are taken as long as the number of
     *                        items is small than {@code numberOfResults}
     * @param itemType        TODO
     * @param session         TODO
     * @return an array of {@link Item}s, sorted by {@code strategy}
     * @throws org.easyrec.soap.nodomain.exception.EasyRecSoapException
     *
     */
    public List<Item> itemsOfCluster(Integer tenant, String clusterName, Integer numberOfResults, String strategy,
                                     Boolean useFallback, Integer itemType, Session session)
            throws EasyRecSoapException;

    /**
     * returns 'bad rated items' (of a given item type for a given tenant) ranked by the frequency of 'rate' actions
     * only ratings with a value LOWER or EQUAL than the tenant specific threshold 'ratingRangeNeutral' are counted
     *
     * @param tenant tenant identifier
     * @param itemType the item type of interest
     * @param numberOfResults maximum number of {@link RankedItem}s in the result
     * @param timeRange an optional time range (fromTime and toTime), only buys within that range are taken into the rankings computation
     * @param sortDescending true for sorting descending (highest ranked item is the first), false for ascending sorting (lowest ranked item first)
     * @return an array of {@link RankedItem}s, sorted by rank
     * @throws ShopRecommenderException
     */
    // HINT: use tenant specific threshold 'ratingRangeNeutral' to decide between good and bad ratings (Mantis Issue: #666)
    //public RankedItem[] mostBadRatedItems(Integer tenant, String itemType, Integer numberOfResults, TimeConstraintVO timeRange, Boolean sortDescending) throws ShopRecommenderException;

    /**
     * returns 'good rated items' (of a given item type for a given tenant) ranked by the frequency of 'rate' actions
     * only ratings with a value HIGHER than the tenant specific threshold 'ratingRangeNeutral' are counted
     *
     * @param tenant tenant identifier
     * @param itemType the item type of interest
     * @param numberOfResults maximum number of {@link RankedItem}s in the result
     * @param timeRange an optional time range (fromTime and toTime), only buys within that range are taken into the rankings computation
     * @param sortDescending true for sorting descending (highest ranked item is the first), false for ascending sorting (lowest ranked item first)
     * @return an array of {@link RankedItem}s, sorted by rank
     * @throws ShopRecommenderException
     */
    //public RankedItem[] mostGoodRatedItems(Integer tenant, String itemType, Integer numberOfResults, TimeConstraintVO timeRange, Boolean sortDescending) throws ShopRecommenderException;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Ratings
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * returns 'ratings' (of a given item type for a given tenant, optionally user and session) ordered by the 'ratingValue'
     *
     * @param tenant tenant identifier
     * @param userId optional user id
     * @param sessionId optional session id
     * @param itemType the item type of interest
     * @param numberOfResults maximum number of {@link Rating}s in the result
     * @param timeRange
     * @return an array of {@link Rating}s, sorted by the rating value
     * @throws ShopRecommenderException
     */
    // public Rating[] itemRatings(Integer tenant, String userId, String sessionId, String itemType, Integer numberOfResults, TimeConstraintVO timeRange) throws ShopRecommenderException;

    /**
     * returns 'bad ratings' (of a given item type for a given tenant, optionally user and session) ordered by the 'ratingValue'
     * only returns ratings with a value LOWER or EQUAL than the tenant specific threshold 'ratingRangeNeutral'
     *
     * @param tenant          tenant identifier
     * @param userId          optional user id
     * @param itemType        the item type of interest
     * @param numberOfResults maximum number of {@link Rating}s in the result
     * @param timeRange       TODO
     * @param constraint      TODO
     * @param session         optional session id
     * @return an array of {@link Rating}s, sorted by the rating value
     */
    public List<Item> worstRatedItems(Integer tenant, @Nullable String userId, @Nullable String itemType,
                                      Integer numberOfResults, String timeRange, @Nullable TimeConstraintVO constraint,
                                      Session session);

    /**
     * returns 'good ratings' (of a given item type for a given tenant, optionally user and session) ordered by the 'ratingValue'
     * only returns ratings with a value HIGHER than the tenant specific threshold 'ratingRangeNeutral'
     *
     * @param tenant          tenant identifier
     * @param userId          optional user id
     * @param itemType        the item type of interest
     * @param numberOfResults maximum number of {@link Rating}s in the result
     * @param timeRange       TODO
     * @param constraint      TODO
     * @param session         optional session id
     * @return an array of {@link Rating}s, sorted by the rating value
     */
    public List<Item> bestRatedItems(Integer tenant, @Nullable String userId, @Nullable String itemType,
                                     Integer numberOfResults, String timeRange, @Nullable TimeConstraintVO constraint,
                                     Session session);

    /**
     * returns the 'last good ratings' (of a given item type for a given tenant, optionally user and session) ordered by the 'actionTime'
     * only returns ratings with a value HIGHER than the tenant specific threshold 'ratingRangeNeutral'
     *
     * @param tenant tenant identifier
     * @param userId optional user id
     * @param sessionId optional session id
     * @param itemType the item type of interest
     * @param numberOfResults maximum number of {@link Rating}s in the result
     * @param timeRange
     * @return an array of {@link Rating}s, sorted by the rating value
     * @throws ShopRecommenderException
     */
    //public Rating[] lastGoodItemRatings(Integer tenant, String userId, String sessionId, String itemType, Integer numberOfResults) throws ShopRecommenderException;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Recommendations
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * returns {@link RecommendedItem}s, based on the 'purchase' history of items with the given 'consideredItemType',
     * taking 'numberOfLastActionsConsidered' actions into consideration,
     * looking for business rules (item assocs) with the given 'assocType' and 'requestedItemType' for items in the result
     *
     * @param tenant                        tenant identifier
     * @param userId                        optional user id (note: either user id or session id must be set)
     * @param sessionId                     optional session id
     * @param consideredItemType            optional, if set, only the purchased items of that type are used from the history
     * @param numberOfLastActionsConsidered optional, if set only the given number of considered items are used
     * @param assocType                     optional, if set, only associations of the given type are used to compute related items
     * @param requestedItemType             optional, if set, only items of the requested type are used in the result
     * @throws EasyRecSoapException
     * @returns an array of {@link RecommendedItem}s, based on the 'purchase' history of items with the given 'consideredItemType',
     */
    public RecommendedItem[] itemsBasedOnPurchaseHistory(Integer tenant, String userId, String sessionId,
                                                         String consideredItemType,
                                                         Integer numberOfLastActionsConsidered, String assocType,
                                                         String requestedItemType) throws EasyRecSoapException;

    /**
     * returns {@link RecommendedItem}s, based on the 'viewing' history of items with the given 'consideredItemType',
     * taking 'numberOfLastActionsConsidered' actions into consideration,
     * looking for business rules (item assocs) with the given 'assocType' and 'requestedItemType' for items in the result
     *
     * @param tenant                        tenant identifier
     * @param userId                        optional user id (note: either user id or session id must be set)
     * @param session                       current session
     * @param consideredItemType            optional, if set, only the purchased items of that type are used from the history
     * @param numberOfLastActionsConsidered optional, if set only the given number of considered items are used
     * @param assocType                     optional, if set, only associations of the given type are used to compute related items
     * @param requestedItemType             optional, if set, only items of the requested type are used in the result
     * @throws EasyRecSoapException
     * @returns an array of {@link RecommendedItem}s, based on the 'viewing' history of items with the given 'consideredItemType',
     */
    public Recommendation itemsBasedOnViewingHistory(Integer tenant, String userId, Session session,
                                                     String consideredItemType, Integer numberOfLastActionsConsidered,
                                                     String assocType, String requestedItemType,
                                                     Integer numberOfRecommendations) throws EasyRecSoapException;

    /**
     * returns {@link RecommendedItem}s, based on the 'searching' history of items with the given 'consideredItemType',
     * taking 'numberOfLastActionsConsidered' actions into consideration,
     * looking for business rules (item assocs) with the given 'assocType' and 'requestedItemType' for items in the result
     *
     * @param tenant                        tenant identifier
     * @param userId                        optional user id (note: either user id or session id must be set)
     * @param sessionId                     optional session id
     * @param consideredItemType            optional, if set, only the purchased items of that type are used from the history
     * @param numberOfLastActionsConsidered optional, if set only the given number of considered items are used
     * @param assocType                     optional, if set, only associations of the given type are used to compute related items
     * @param requestedItemType             optional, if set, only items of the requested type are used in the result
     * @throws EasyRecSoapException
     * @returns an array of {@link RecommendedItem}s, based on the 'searching' history of items with the given 'consideredItemType',
     */
    public RecommendedItem[] itemsBasedOnSearchingHistory(Integer tenant, String userId, String sessionId,
                                                          String consideredItemType,
                                                          Integer numberOfLastActionsConsidered, String assocType,
                                                          String requestedItemType) throws EasyRecSoapException;
    // HINT: use tenant specific threshold 'ratingRangeNeutral' to decide between good and bad ratings (Mantis Issue: #666)
    //public RecommendedItem[] itemsBasedOnRatingHistory(Integer tenant, String userId, String sessionId, String consideredItemType, Integer numberOfLastActionsConsidered, String assocType, String requestedItemType) throws ShopRecommenderException;
    //public RecommendedItem[] itemsBasedOnGoodRatingHistory(Integer tenant, String userId, String sessionId, String consideredItemType, Integer numberOfLastActionsConsidered, String assocType, String requestedItemType) throws ShopRecommenderException;
    //public RecommendedItem[] itemsBasedOnBadRatingHistory(Integer tenant, String userId, String sessionId, String consideredItemType, Integer numberOfLastActionsConsidered, String assocType, String requestedItemType) throws ShopRecommenderException;

    /**
     * @param tenantId
     * @param userId
     * @param session
     * @param consideredActionType
     * @param consideredItemType
     * @param numberOfLastActionsConsidered
     * @param assocType
     * @param requestedItemType
     * @param numberOfRecommendations
     * @return
     * @throws EasyRecSoapException
     */
    public Recommendation itemsBasedOnActionHistory(Integer tenantId, String userId, Session session,
                                                    String consideredActionType, String consideredItemType,
                                                    Integer numberOfLastActionsConsidered,
                                                    String assocType, String requestedItemType,
                                                    Integer numberOfRecommendations) throws EasyRecSoapException;


    /**
     * 
     * @param tenantId
     * @param userId
     * @param session
     * @param consideredActionType
     * @param consideredItemType
     * @param numberOfLastActionsConsidered
     * @param numberOfRecommendations
     * @return
     * @throws EasyRecSoapException 
     */
    public Recommendation actionHistory(Integer tenantId, String userId, Session session,
                                                    String consideredActionType, String consideredItemType, 
                                                    Integer numberOfLastActionsConsidered,
                                                    Integer numberOfRecommendations) throws EasyRecSoapException;
    
    /**
     * @param tenantId
     * @param userId
     * @param session
     * @param consideredActionType
     * @param consideredItemType
     * @param numberOfLastActionsConsidered
     * @param assocType
     * @param requestedItemType
     * @param numberOfRecommendations
     * @return
     * @throws EasyRecSoapException
     */
    public Recommendation itemsForUser(Integer tenantId, String userId, Session session,
                                                    String consideredActionType, String consideredItemType,
                                                    Integer numberOfLastActionsConsidered,
                                                    String assocType, String requestedItemType,
                                                    Integer numberOfRecommendations) throws EasyRecSoapException;

    /**
     * returns {@link RecommendedItem}s, based on business rules that identify items as 'bought together'
     *
     * @param tenant            tenant identifier
     * @param userId            optional user id, only used to filter items the user has already bought (and for logging purposes)
     * @param session           optional session id, only used to filter items the user has already bought (and for logging purposes)
     * @param itemId            the id of the item that was already bought (searching for rules containing this item)
     * @param itemType          the type of the item that was already bought (searching for rules containing this item)
     * @param requestedItemType optional, if set, only items of the requested type are used in the result
     * @return an array of {@link RecommendedItem}s, based on business rules that identify items as 'bought together'
     * @throws EasyRecSoapException
     */
    public Recommendation alsoBoughtItems(Integer tenant, String userId, String itemId, String itemType,
                                          String requestedItemType, Session session, Integer numberOfResults)
            throws EasyRecSoapException;

    /**
     * returns {@link RecommendedItem}s, based on business rules that identify items as 'viewed together'
     *
     * @param tenant            tenant identifier
     * @param userId            optional user id, only used to filter items the user has already viewed (and for logging purposes)
     * @param session           optional session id, only used to filter items the user has already viewed (and for logging purposes)
     * @param itemId            the id of the item that was already viewed (searching for rules containing this item)
     * @param itemType          the type of the item that was already viewed (searching for rules containing this item)
     * @param requestedItemType optional, if set, only items of the requested type are used in the result
     * @return an array of {@link RecommendedItem}s, based on business rules that identify items as 'viewed together'
     * @throws EasyRecSoapException
     */
    public Recommendation alsoViewedItems(Integer tenant, String userId, String itemId, String itemType,
                                          String requestedItemType, Session session, Integer numberOfResults)
            throws EasyRecSoapException;

    // HINT: use tenant specific threshold 'ratingRangeNeutral' to decide between good and bad ratings (Mantis Issue: #666)
    //public RecommendedItem[] alsoRatedItems(Integer tenant, String userId, String sessionId, String itemId, String itemType, String requestedItemType) throws ShopRecommenderException;
    //public RecommendedItem[] alsoBadRatedItems(Integer tenant, String userId, String sessionId, String itemId, String itemType, String requestedItemType) throws ShopRecommenderException;

    /**
     * returns {@link RecommendedItem}s, based on business rules that identify items as 'good rated together'
     *
     * @param tenant            tenant identifier
     * @param userId            optional user id, only used to filter items the user has already searched (and for logging purposes)
     * @param sessionId         optional session id, only used to filter items the user has already searched (and for logging purposes)
     * @param itemId            the id of the item that was already searched (searching for rules containing this item)
     * @param itemType          the type of the item that was already searched (searching for rules containing this item)
     * @param requestedItemType optional, if set, only items of the requested type are used in the result
     * @return an array of {@link RecommendedItem}s, based on business rules that identify items as 'searched together'
     * @throws EasyRecSoapException
     */
    public Recommendation alsoGoodRatedItems(Integer tenant, String userId, String itemId, String itemType,
                                             String requestedItemType, Session session, Integer numberOfResults)
            throws EasyRecSoapException;
       
    /**
     * @param tenantId
     * @param assocType
     * @param userId
     * @param itemId
     * @param itemType
     * @param requestedItemType
     * @param session
     * @return
     * @throws EasyRecSoapException
     */
    public Recommendation relatedItems(Integer tenantId, String assocType, String userId, String itemId, String itemType,
                                       String requestedItemType, Session session, Integer numberOfResults)
            throws EasyRecSoapException;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Utility methods
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * returns the possible item types of the given tenant
     *
     * @param tenantId tenant identifier
     * @return an array of {@link String}s representing possible item types of a given tenant
     */
    public Set<String> getItemTypes(Integer tenantId) throws EasyRecSoapException;

    public List<ClusterVO> getClusters(Integer tenantId) throws EasyRecSoapException;

    /**
     * returns the possible association types of the given tenant
     *
     * @param tenantId tenant identifier
     * @return an array of {@link String}s representing possible association types of a given tenant
     */
    public String[] getAssocTypes(Integer tenantId) throws EasyRecSoapException;

    public List<ItemAssocVO<String, String>> getRules(Integer tenantId);

    public List<ItemAssocVO<String, String>> getRules(Item item);

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Profile aware methods
    ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     public void storeProfile(Integer tenantId, String itemId, String itemType, String profileXML) throws ShopRecommenderException;

     public String getProfile(Integer tenantId, String itemId, String itemType) throws ShopRecommenderException;

     public String[] getSimilarProfiles(Integer tenantId, String itemId, String itemType) throws ShopRecommenderException;
     * **/

    /**
     * clear items in cache
     */
    public void emptyCache();
}