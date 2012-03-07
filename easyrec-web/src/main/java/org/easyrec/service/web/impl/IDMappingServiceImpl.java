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
package org.easyrec.service.web.impl;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.easyrec.controller.BackTrackingController;
import org.easyrec.model.core.*;
import org.easyrec.model.web.*;
import org.easyrec.service.core.TenantService;
import org.easyrec.service.web.IDMappingService;
import org.easyrec.store.dao.core.types.AssocTypeDAO;
import org.easyrec.store.dao.core.types.ItemTypeDAO;
import org.easyrec.store.dao.web.ItemDAO;
import org.easyrec.utils.spring.store.dao.IDMappingDAO;

import java.util.ArrayList;
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
public class IDMappingServiceImpl implements IDMappingService {

    private IDMappingDAO idMappingDAO;
    private ItemDAO itemDAO;
    private TenantService tenantService;
    private AssocTypeDAO assocTypeDAO;
    private ItemTypeDAO itemTypeDAO;


    public IDMappingServiceImpl(IDMappingDAO idMappingDAO, ItemDAO itemDAO, TenantService tenantService,
                                AssocTypeDAO assocTypeDAO, ItemTypeDAO itemTypeDAO) {
        this.idMappingDAO = idMappingDAO;
        this.itemDAO = itemDAO;
        this.tenantService = tenantService;
        this.assocTypeDAO = assocTypeDAO;
        this.itemTypeDAO = itemTypeDAO;
    }


    @Override
    public List<RankedItem> convertListOfRankedItemVOs(/*Integer tenantId,*/
                                                       List<RankedItemVO<Integer, String>> inList) {
        if (inList == null) {
            return null;
        }

        List<RankedItem> outList = new ArrayList<RankedItem>();

        for (RankedItemVO<Integer, String> rankedItem : inList) {
            outList.add(convertRankedItemVO(rankedItem));
        }

        return outList;
    }

    @Override
    public List<RankedItemVO<Integer, String>> convertListOfRankedItems(/*Integer tenantId,*/
                                                                                         List<RankedItem> inList) {
        if (inList == null) {
            return null;
        }

        List<RankedItemVO<Integer, String>> outList =
                new ArrayList<RankedItemVO<Integer, String>>();

        for (RankedItem rankedItem : inList) {
            outList.add(convertRankedItem(rankedItem));
        }

        return outList;
    }

    @Override
    public ItemVO<Integer, Integer> convertItem(Item item) {
        Integer tenantId = item.getTenantId();
        Integer itemId = idMappingDAO.lookup(item.getItemId());
        Integer itemTypeId = itemTypeDAO.getIdOfType(tenantId, item.getItemType());

        return new ItemVO<Integer, Integer>(tenantId, itemId, itemTypeId);
    }

    @Override
    public RankedItem convertRankedItemVO(/*Integer tenantId,*/
                                          RankedItemVO<Integer, String> rankedItemVO) {
        RankedItem rankedItem = new RankedItem(new ItemVO<String, String>(
                tenantService.getTenantById(rankedItemVO.getItem().getTenant()).getStringId(),
                idMappingDAO.lookup(rankedItemVO.getItem().getItem()), rankedItemVO.getItem().getType()),
                rankedItemVO.getActionType(), rankedItemVO.getRank(), rankedItemVO.getCount());
        return rankedItem;
    }

    @Override
    public RankedItemVO<Integer, String> convertRankedItem(/*Integer tenantId,*/
                                                                            RankedItem rankedItem) {
        RankedItemVO<Integer, String> rankedItemVO =
                new RankedItemVO<Integer, String>(
                        new ItemVO<Integer, String>(
                                tenantService.getTenantByStringId(rankedItem.getTenant()).getId(),
                                idMappingDAO.lookup(rankedItem.getItemId()), rankedItem.getItemType()),
                        rankedItem.getActionType(), rankedItem.getRank(), rankedItem.getCount());
        return rankedItemVO;
    }

    @Override
    public List<RatingVO<Integer, String>> convertListOfRatings(List<Rating> inList) {
        if (inList == null) {
            return null;
        }

        List<RatingVO<Integer, String>> outList =
                new ArrayList<RatingVO<Integer, String>>();

        for (Rating rating : inList) {
            outList.add(convertRating(rating));
        }

        return outList;
    }


    @Override
    public List<Rating> convertListOfRatingVOs(List<RatingVO<Integer, String>> inList) {
        if (inList == null) {
            return null;
        }

        List<Rating> outList = new ArrayList<Rating>();

        for (RatingVO<Integer, String> ratingVO : inList) {
            outList.add(convertRatingVO(ratingVO));
        }

        return outList;
    }


    @Override
    public RatingVO<Integer, String> convertRating(Rating rating) {
        RatingVO<Integer, String> ratingVO = new RatingVO<Integer, String>(
                new ItemVO<Integer, String>(tenantService.getTenantByStringId(rating.getTenant()).getId(),
                        idMappingDAO.lookup(rating.getItemId()), rating.getItemType()), rating.getRatingValue(),
                rating.getCount(), rating.getLastActionTime(), idMappingDAO.lookup(rating.getUserId()),
                rating.getSessionId());
        return ratingVO;
    }


    @Override
    public Rating convertRatingVO(RatingVO<Integer, String> ratingVO) {
        Rating rating = new Rating(new ItemVO<String, String>(
                tenantService.getTenantById(ratingVO.getItem().getTenant()).getStringId(),
                idMappingDAO.lookup(ratingVO.getItem().getItem()), ratingVO.getItem().getType()),
                ratingVO.getRatingValue(), ratingVO.getCount(), ratingVO.getLastActionTime(),
                idMappingDAO.lookup(ratingVO.getUser()), ratingVO.getSessionId());
        return rating;
    }


    @Override
    public List<RecommendedItemVO<Integer, String>> convertListOfRecommendedItems(
            List<RecommendedItem> inList) {
        if (inList == null) {
            return null;
        }

        List<RecommendedItemVO<Integer, String>> outList =
                new ArrayList<RecommendedItemVO<Integer, String>>();

        for (RecommendedItem recommendedItem : inList) {
            outList.add(convertRecommendedItem(recommendedItem));
        }

        return outList;
    }


    @Override
    public List<RecommendedItem> convertListOfRecommendedItemVOs(
            List<RecommendedItemVO<Integer, String>> inList) {
        if (inList == null) {
            return null;
        }

        List<RecommendedItem> outList = new ArrayList<RecommendedItem>();

        for (RecommendedItemVO<Integer, String> recommendedItemVO : inList) {
            outList.add(convertRecommendedItemVO(recommendedItemVO));
        }

        return outList;
    }


    @Override
    public RecommendedItemVO<Integer, String> convertRecommendedItem(RecommendedItem recommendedItem) {
        RecommendedItemVO<Integer, String> recommendedItemVO = new RecommendedItemVO<Integer, String>(
                new ItemVO<Integer, String>(
                        tenantService.getTenantByStringId(recommendedItem.getTenant()).getId(),
                        idMappingDAO.lookup(recommendedItem.getItemId()), recommendedItem.getItemType()),
                recommendedItem.getPredictionValue(), recommendedItem.getExplanation());
        return recommendedItemVO;
    }


    @Override
    public RecommendedItem convertRecommendedItemVO(RecommendedItemVO<Integer, String> recommendedItemVO) {
        RecommendedItem recommendedItem = new RecommendedItem(new ItemVO<String, String>(
                tenantService.getTenantById(recommendedItemVO.getItem().getTenant()).getStringId(),
                idMappingDAO.lookup(recommendedItemVO.getItem().getItem()), recommendedItemVO.getItem().getType()),
                recommendedItemVO.getPredictionValue(), recommendedItemVO.getExplanation());
        return recommendedItem;
    }
    


    @Override
    public List<Item> mapListOfItemVOs(List<ItemVO<Integer, Integer>> inList,
                                       final RemoteTenant remoteTenant) {
        return Lists.transform(inList, new Function<ItemVO<Integer, Integer>, Item>() {
            @Override
            public Item apply(ItemVO<Integer, Integer> input) {
                String itemId = idMappingDAO.lookup(input.getItem());
                String itemType = itemTypeDAO.getTypeById(input.getTenant(), input.getType());

                return itemDAO.get(remoteTenant, itemId, itemType);
            }
        });
    }
    
    @Override
    public List<Item> mapListOfItemVOs(List<ItemVO<Integer, String>> inList,
            RemoteTenant remoteTenant, Integer userId, Session session, Integer numberOfRecommendations) {
     
        List<Item> items = new ArrayList<Item>();
        Item item = null;
        if (inList != null) {
            for (ItemVO<Integer, String> itemVO : inList) {

                if (items.size() >= numberOfRecommendations) break;

                item = itemDAO.get(remoteTenant, idMappingDAO.lookup(itemVO.getItem()),
                        itemVO.getType());

                if (item != null) {
                    if (item.isActive()) {

                        // set tracking url
                        // e.g. http://localhost:8084/easyrec-web/t?t=1&f=2&t=3&a=4&u=www.flimmit.com
                        String itemUrl = item.getUrl();

                        if (remoteTenant.backtrackingEnabled()) {
                            itemUrl = Item
                                    .getTrackingUrl(session, userId, remoteTenant, 0, itemVO.getItem(),
                                            BackTrackingController.ASSOC_HISTORY, item.getUrl());
                        }

                        // to make webapp thread-safe, a new item is created
                        items.add(new Item(item.getId(), item.getTenantId(), item.getItemId(), item.getItemType(),
                                item.getDescription(), itemUrl, item.getImageUrl(), null,
                                true, item.getCreationDate()));
                    }
                }
            }
        }
        return items;
    }

    /**
     * This function maps the Items from the coreRec into
     * local Items that contain more information such as description,
     * item url or item image url.
     * Note 23.10.2008:
     * The CoreRec does not keep any information about the items so far.
     * This is why all information about items is kept in a separate
     * table. It is planned that easyrec will also store itemProfiles
     * in near future.
     */
    @Override
    public List<Item> mapRecommendedItems(
            RecommendationVO<Integer, String> recommendation,
            RemoteTenant remoteTenant, Integer userId, Session session, Integer numberOfRecommendations) {
        List<Item> items = new ArrayList<Item>();
        Item item = null;

        if (recommendation != null && recommendation.getRecommendedItems() != null) {
            for (RecommendedItemVO<Integer, String> recommendedItem : recommendation.getRecommendedItems()) {

                if (items.size() >= numberOfRecommendations) break;

                item = itemDAO.get(remoteTenant, idMappingDAO.lookup(recommendedItem.getItem().getItem()),
                        recommendedItem.getItem().getType());


                if (item != null) {
                    if (item.isActive()) {

                        // set tracking url
                        // e.g. http://localhost:8084/easyrec-web/t?t=1&f=2&t=3&a=4&u=www.flimmit.com
                        String itemUrl = item.getUrl();

                        Integer assocTypeId = assocTypeDAO
                                .getIdOfType(remoteTenant.getId(), recommendation.getQueriedAssocType());

                        // assocTypeId: recommendations for user
                        if (assocTypeId == null) {
                            assocTypeId = BackTrackingController.ASSOC_RECOMMENDATIONS_FOR_USER;
                        }

                        Integer itemFromId = recommendation.getQueriedItem();

                        // default item fromid = 0 in case of "recommendations for user"
                        if (itemFromId == null) {
                            itemFromId = 0;
                        }

                        if (remoteTenant.backtrackingEnabled()) {
                                itemUrl = Item.getTrackingUrl(session, userId, remoteTenant, itemFromId,
                                    recommendedItem.getItem().getItem(), assocTypeId, item.getUrl());
                        }

                        // to make webapp thread-safe, a new item is created
                        // because this item contains request specific information
                        //if (recommendedItem.getPredictionValue() > 0) {
                        items.add(new Item(item.getId(), item.getTenantId(), item.getItemId(), item.getItemType(),
                                item.getDescription(), itemUrl, item.getImageUrl(),
                                recommendedItem.getPredictionValue(), true, item.getCreationDate()));
                        //}
                    }
                }
            }
        }
        return items;
    }

    /**
     * This function maps the Items from the coreRec into
     * local Items that contain more information such as description,
     * item url or item image url.
     */
    @Override
    public List<Item> mapRankedItems(List<RankedItemVO<Integer, String>> rankedItems,
                                     RemoteTenant remoteTenant, Session session, Integer numberOfRecommendations) {
        List<Item> items = new ArrayList<Item>();
        Item item = null;

        if (rankedItems != null) {
            for (RankedItemVO<Integer, String> rankedItem : rankedItems) {

                if (items.size() >= numberOfRecommendations) break;

                item = itemDAO.get(remoteTenant, idMappingDAO.lookup(rankedItem.getItem().getItem()),
                        rankedItem.getItem().getType());

                if (item != null) {
                    if (item.isActive()) {

                        // set tracking url
                        // e.g. http://localhost:8084/easyrec-web/t?t=1&f=2&t=3&a=4&u=www.flimmit.com
                        String itemUrl = item.getUrl();

                        if (remoteTenant.backtrackingEnabled()) {
                            itemUrl = Item
                                    .getTrackingUrl(session, 0, remoteTenant, 0, rankedItem.getItem().getItem(),
                                            BackTrackingController.ASSOC_RANKINGS, item.getUrl());
                        }

                        // to make webapp thread-safe, a new item is created
                        items.add(new Item(item.getId(), item.getTenantId(), item.getItemId(), item.getItemType(),
                                item.getDescription(), itemUrl, item.getImageUrl(), rankedItem.getCount().doubleValue(),
                                true, item.getCreationDate()));
                    }
                }
            }
        }
        return items;
    }

    @Override
    public List<Item> mapRatedItems(List<RatingVO<Integer, String>> ratedItems,
                                    RemoteTenant remoteTenant, Session session, Integer numberOfRecommendations) {
        List<Item> items = new ArrayList<Item>();
        Item item = null;

        if (ratedItems != null) {
            for (RatingVO<Integer, String> ratedItem : ratedItems) {
                if (items.size() >= numberOfRecommendations) break;
                item = itemDAO.get(remoteTenant, idMappingDAO.lookup(ratedItem.getItem().getItem()),
                        ratedItem.getItem().getType());

                if (item != null) {
                    if (item.isActive()) {

                        // set tracking url
                        // e.g. http://localhost:8084/easyrec-web/t?t=1&f=2&t=3&a=4&u=www.flimmit.com
                        String itemUrl = item.getUrl();

                        if (remoteTenant.backtrackingEnabled()) {
                            itemUrl = Item
                                    .getTrackingUrl(session, 0, remoteTenant, 0, ratedItem.getItem().getItem(),
                                            BackTrackingController.ASSOC_RATINGS, item.getUrl());
                        }

                        // to make webapp thread-safe, a new item is created
                        items.add(new Item(item.getId(), item.getTenantId(), item.getItemId(), item.getItemType(),
                                item.getDescription(), itemUrl, item.getImageUrl(), ratedItem.getRatingValue(), true,
                                item.getCreationDate()));
                    }
                }
            }
        }
        return items;
    }

    @Override
    public List<ItemAssocVO<String, String>> mapItemAssocs(
            List<ItemAssocVO<Integer, String>> itemAssocs,
            boolean filterInactiveRules) {
        List<ItemAssocVO<String, String>> rules =
                new ArrayList<ItemAssocVO<String, String>>();
        ItemAssocVO<String, String> rule = null;

        if (itemAssocs != null) {
            for (ItemAssocVO<Integer, String> itemAssoc : itemAssocs) {
                if (itemAssoc.isActive() || !filterInactiveRules) {
                    rule = new ItemAssocVO<String, String>(itemAssoc.getId(),
                            tenantService.getTenantById(itemAssoc.getItemFrom().getTenant()).getStringId(),
                            new ItemVO<String, String>(
                                    tenantService.getTenantById(itemAssoc.getItemFrom().getTenant()).getStringId(),
                                    idMappingDAO.lookup(itemAssoc.getItemFrom().getItem()),
                                    itemAssoc.getItemFrom().getType()), itemAssoc.getAssocType(),
                            itemAssoc.getAssocValue(), new ItemVO<String, String>(
                            tenantService.getTenantById(itemAssoc.getItemTo().getTenant()).getStringId(),
                            idMappingDAO.lookup(itemAssoc.getItemTo().getItem()),
                            itemAssoc.getItemTo().getType()), itemAssoc.getSourceType(),
                            itemAssoc.getSourceInfo(), itemAssoc.getViewType(), itemAssoc.isActive(),
                            itemAssoc.getChangeDate());
                    rules.add(rule);
                }
            }
        }

        return rules;
    }

    /**
     * This function maps the Items from the coreRec into
     * local Items that contain more information such as description,
     * item url or item image url.
     */
    @Override
    public List<Item> mapClusterItems(List<ItemVO<Integer, Integer>> clusterItems,
                                      RemoteTenant remoteTenant, Session session, Integer numberOfRecommendations) {
        List<Item> items = new ArrayList<Item>();
        Item item = null;

        if (clusterItems != null) {
            for (ItemVO<Integer, Integer> clusterItem : clusterItems) {

                if (items.size() >= numberOfRecommendations) break;

                item = itemDAO.get(remoteTenant, idMappingDAO.lookup(clusterItem.getItem()),
                        itemTypeDAO.getTypeById(clusterItem.getTenant(), clusterItem.getType()));

                if (item != null) {
                    if (item.isActive()) {

                        // set tracking url
                        // e.g. http://localhost:8084/easyrec-web/t?t=1&f=2&t=3&a=4&u=www.flimmit.com
                        String itemUrl = item.getUrl();

                        if (remoteTenant.backtrackingEnabled()) {
                            itemUrl = Item
                                    .getTrackingUrl(session, 0, remoteTenant, 0, clusterItem.getItem(),
                                            BackTrackingController.ASSOC_CLUSTER, item.getUrl());
                        }

                        // to make webapp thread-safe, a new item is created
                        items.add(new Item(item.getId(), item.getTenantId(), item.getItemId(), item.getItemType(),
                                item.getDescription(), itemUrl, item.getImageUrl(), null,
                                true, item.getCreationDate()));
                    }
                }
            }
        }
        return items;
    }
}