/*
 * Copyright 2010 Research Studios Austria Forschungsgesellschaft mBH
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
package org.easyrec.service.web.nodomain.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyrec.model.core.*;
import org.easyrec.model.core.transfer.TimeConstraintVO;
import org.easyrec.model.web.*;
import org.easyrec.model.web.enums.TimeRange;
import org.easyrec.service.core.ClusterService;
import org.easyrec.service.core.TenantService;
import org.easyrec.service.domain.DomainActionService;
import org.easyrec.service.domain.DomainItemAssocService;
import org.easyrec.service.domain.DomainRecommenderService;
import org.easyrec.service.domain.TypeMappingService;
import org.easyrec.service.domain.profile.ProfileMatcherService;
import org.easyrec.service.domain.profile.ProfileService;
import org.easyrec.service.web.IDMappingService;
import org.easyrec.service.web.ItemService;
import org.easyrec.service.web.nodomain.ShopRecommenderService;
import org.easyrec.soap.nodomain.exception.EasyRecSoapException;
import org.easyrec.store.dao.core.types.AssocTypeDAO;
import org.easyrec.store.dao.web.ItemDAO;
import org.easyrec.store.dao.web.RemoteTenantDAO;
import org.easyrec.utils.spring.log.annotation.IOLog;
import org.easyrec.utils.spring.profile.annotation.Profiled;
import org.easyrec.utils.spring.store.dao.IDMappingDAO;
import org.easyrec.vocabulary.MSG;
import org.easyrec.vocabulary.WS;

import java.util.*;

/**
 * Service class to provide functions for web service recommendation functions.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: szavrel $<br/>
 * $Date: 2012-02-07 22:31:55 +0100 (Di, 07 Feb 2012) $<br/>
 * $Revision: 18713 $</p>
 *
 * @author Stephan Zavrel
 */
//@MapThrowableToException(exceptionClazz=ShopRecommenderException.class)
public class ShopRecommenderServiceImpl implements ShopRecommenderService {
    private final Log logger = LogFactory.getLog(this.getClass());

    private DomainActionService domainActionService;
    private DomainRecommenderService domainRecommenderService;
    private DomainItemAssocService domainItemAssocService;
    private TypeMappingService typeMappingService;
    private IDMappingDAO idMappingDAO;
    private IDMappingService idMappingService;
    private ItemService itemService;
    private ItemDAO itemDAO;
    private RemoteTenantDAO remoteTenantDAO;
    private Cache cache;
    private ProfileService profileService;
    private ProfileMatcherService profileMatcherService;
    private ClusterService clusterService;
    private TenantService tenantService;

    // Jamon Loggers
    private final static String JAMON_REST_VIEW_CORE = "rest.view.core";
    private final static String JAMON_REST_RATE_CORE = "rest.rate.core";
    private final static String JAMON_REST_BUY_CORE = "rest.buy.core";
    private final static String JAMON_REST_SENDACTION_CORE = "rest.sendaction.core";

    private final static String JAMON_REST_ALSO_VIEWED_CORE = "rest.alsoviewed.core";
    private final static String JAMON_REST_ALSO_BOUGHT_CORE = "rest.alsobought.core";
    private final static String JAMON_REST_ALSO_RATED_CORE = "rest.alsorated.core";
    private final static String JAMON_REST_RECS_FOR_USER = "rest.recsforuser.core";
    private final static String JAMON_REST_ACTION_HISTORY_CORE = "rest.history.core";
    private final static String JAMON_REST_RECOMMENDED_ITEMS_CORE = "rest.recommendeditems.core";

    private final static String JAMON_REST_MOST_BOUGHT_CORE = "rest.mostbought.core";
    private final static String JAMON_REST_MOST_VIEWED_CORE = "rest.mostviewed.core";
    private final static String JAMON_REST_MOST_RATED_CORE = "rest.mostrated.core";

    private final static String JAMON_REST_BEST_RATED_CORE = "rest.bestrated.core";
    private final static String JAMON_REST_WORST_RATED_CORE = "rest.worstrated.core";

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // interface "ShopRecommenderService" implementation

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Actions
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * This procedure tells the ProfilerController that an Item is purchased.
     * Note:
     * A SessionId is always required. In case no userId is given the sessionId
     * is used a the userId instead.
     */
    @IOLog
    @Profiled
    @Override
    public Item purchaseItem(RemoteTenant remoteTenant, String userId, String itemId, String itemType,
                             String itemDescription, String itemUrl, String itemImageUrl, Date actionTime,
                             Session session) {
        Item item = itemDAO.get(remoteTenant, itemId, itemType);

        if (item == null) {
            item = itemDAO.add(remoteTenant.getId(), itemId, itemType, itemDescription, itemUrl, itemImageUrl);

            if (logger.isDebugEnabled()) {
                logger.debug(new StringBuilder().
                        append("<ITEMCOLLECT@").
                        append(remoteTenant.getId()).
                        append("> ").
                        append(itemType).
                        append(" ").
                        append(itemDescription).
                        append(" (id:").
                        append(itemId).
                        append(")").toString());
            }
        }

        if (item != null && item.isActive()) {

            // if userid is empty use sessionid instead of the userid
            userId = Strings.isNullOrEmpty(userId) ? session.getSessionId() : userId;

            Monitor monCore = MonitorFactory.start(JAMON_REST_BUY_CORE);

            if (actionTime == null) {
                domainActionService
                        .purchaseItem(remoteTenant.getId(), idMappingDAO.lookup(userId), session.getSessionId(),
                                session.getIp(),
                                new ItemVO<Integer, String>(remoteTenant.getId(), idMappingDAO.lookup(itemId),
                                        itemType), itemDescription);
            } else {
                domainActionService
                        .purchaseItem(remoteTenant.getId(), idMappingDAO.lookup(userId), session.getSessionId(),
                                session.getIp(),
                                new ItemVO<Integer, String>(remoteTenant.getId(), idMappingDAO.lookup(itemId),
                                        itemType), itemDescription, actionTime);
            }


            monCore.stop();
            if (logger.isDebugEnabled()) {
                logger.debug(new StringBuilder().
                        append("<buy@").
                        append(remoteTenant.getId()).
                        append("> ").
                        append(userId).
                        append(" view ").
                        append(itemDescription).
                        append(" (id:").
                        append(itemId).
                        append(")").toString());
            }
        }
        return item;

    }

    /**
     * This procedure tells the ProfilerController that an Item is viewed.
     * Note:
     * A SessionId is always required. In case no userId is given the sessionId
     * is used a the userId instead.
     */
    @IOLog
    @Profiled
    @Override
    public Item viewItem(RemoteTenant remoteTenant, String userId, String itemId, String itemType,
                         String itemDescription, String itemUrl, String itemImageUrl, Date actionTime,
                         Session session) {
        Item item = itemDAO.get(remoteTenant, itemId, itemType);

        if (item == null) {
            item = itemDAO.add(remoteTenant.getId(), itemId, itemType, itemDescription, itemUrl, itemImageUrl);

            if (logger.isDebugEnabled()) {
                logger.debug(new StringBuilder().
                        append("<ITEMCOLLECT@").
                        append(remoteTenant.getId()).
                        append("> ").
                        append(itemType).
                        append(" ").
                        append(itemDescription).
                        append(" (id:").
                        append(itemId).
                        append(")").toString());
            }
        }

        if (item != null && item.isActive()) {
            // if userid is empty use sessionid instead of the userid
            userId = Strings.isNullOrEmpty(userId) ? session.getSessionId() : userId;

            Monitor monCore = MonitorFactory.start(JAMON_REST_VIEW_CORE);

            if (actionTime == null) {
                domainActionService.viewItem(remoteTenant.getId(), idMappingDAO.lookup(userId), session.getSessionId(),
                        session.getIp(),
                        new ItemVO<Integer, String>(remoteTenant.getId(), idMappingDAO.lookup(itemId),
                                itemType), itemDescription);
            } else {
                domainActionService.viewItem(remoteTenant.getId(), idMappingDAO.lookup(userId), session.getSessionId(),
                        session.getIp(),
                        new ItemVO<Integer, String>(remoteTenant.getId(), idMappingDAO.lookup(itemId),
                                itemType), itemDescription, actionTime);

            }

            monCore.stop();

            if (logger.isDebugEnabled()) {
                logger.debug(new StringBuilder().
                        append("<view@").
                        append(remoteTenant.getId()).
                        append("> ").
                        append(userId).
                        append(" view ").
                        append(itemDescription).
                        append(" (id:").
                        append(itemId).
                        append(")").toString());
            }
        }

        return item;
    }


    /**
     * This procedure tells the ProfilerController that an Item is rated
     * with a given value.
     * Note:
     * A SessionId is always required. In case no userId is given the sessionId
     * is used a the userId instead.
     */
    @IOLog
    @Profiled
    @Override
    public Item rateItem(RemoteTenant remoteTenant, String userId, String itemId, String itemType,
                         String itemDescription, String itemUrl, String itemImageUrl, Integer ratingValue,
                         Date actionTime, Session session) {
        Item item = itemDAO.get(remoteTenant, itemId, itemType);

        if (item == null) {
            item = itemDAO.add(remoteTenant.getId(), itemId, itemType, itemDescription, itemUrl, itemImageUrl);

            if (logger.isDebugEnabled()) {
                logger.debug(new StringBuilder().
                        append("<ITEMCOLLECT@").
                        append(remoteTenant.getId()).
                        append("> ").
                        append(itemType).
                        append(" ").
                        append(itemDescription).
                        append(" (id:").
                        append(itemId).
                        append(")").toString());
            }
        }

        if (item != null && item.isActive()) {

            // if userid is empty use sessionid instead of the userid
            userId = Strings.isNullOrEmpty(userId) ? session.getSessionId() : userId;

            Monitor monCore = MonitorFactory.start(JAMON_REST_RATE_CORE);


            if (actionTime == null) {
                domainActionService.rateItem(remoteTenant.getId(), idMappingDAO.lookup(userId), session.getSessionId(),
                        session.getIp(),
                        new ItemVO<Integer, String>(remoteTenant.getId(), idMappingDAO.lookup(itemId),
                                itemType), ratingValue, itemDescription);
            } else {
                domainActionService.rateItem(remoteTenant.getId(), idMappingDAO.lookup(userId), session.getSessionId(),
                        session.getIp(),
                        new ItemVO<Integer, String>(remoteTenant.getId(), idMappingDAO.lookup(itemId),
                                itemType), ratingValue, itemDescription, actionTime);
            }

            monCore.stop();
            if (logger.isDebugEnabled()) {
                logger.debug(new StringBuilder().
                        append("<rate@").
                        append(remoteTenant.getId()).
                        append("> ").
                        append(userId).
                        append(" view ").
                        append(itemDescription).
                        append(" (id:").
                        append(itemId).
                        append(")").toString());
            }
        }

        return item;

    }

    /**
     * This procedure tells the ProfilerController that an Item is rated
     * with a given value.
     * Note:
     * A SessionId is always required. In case no userId is given the sessionId
     * is used as the userId instead.
     */
    @IOLog
    @Profiled
    @Override
    public Item sendAction(RemoteTenant remoteTenant, String userId, String itemId, String itemType,
                         String itemDescription, String itemUrl, String itemImageUrl, String actionType, Integer actionValue,
                         Date actionTime, Session session) {
        Item item = itemDAO.get(remoteTenant, itemId, itemType);

        if (item == null) {
            item = itemDAO.add(remoteTenant.getId(), itemId, itemType, itemDescription, itemUrl, itemImageUrl);

            if (logger.isDebugEnabled()) {
                logger.debug(new StringBuilder().
                        append("<ITEMCOLLECT@").
                        append(remoteTenant.getId()).
                        append("> ").
                        append(itemType).
                        append(" ").
                        append(itemDescription).
                        append(" (id:").
                        append(itemId).
                        append(")").toString());
            }
        }

        if (item != null && item.isActive()) {

            // if userid is empty use sessionid instead of the userid
            userId = Strings.isNullOrEmpty(userId) ? session.getSessionId() : userId;

            Monitor monCore = MonitorFactory.start(JAMON_REST_SENDACTION_CORE);


            if (actionTime == null) {
                domainActionService.insertAction(remoteTenant.getId(), idMappingDAO.lookup(userId), session.getSessionId(),
                        session.getIp(),
                        new ItemVO<Integer, String>(remoteTenant.getId(), idMappingDAO.lookup(itemId),
                                itemType), actionType, actionValue, itemDescription);
            } else {
                domainActionService.insertAction(remoteTenant.getId(), idMappingDAO.lookup(userId), session.getSessionId(),
                        session.getIp(),
                        new ItemVO<Integer, String>(remoteTenant.getId(), idMappingDAO.lookup(itemId),
                                itemType), actionType, actionValue, itemDescription, actionTime);
            }

            monCore.stop();
            if (logger.isDebugEnabled()) {
                logger.debug(new StringBuilder().
                        append("<sendAction@").
                        append(remoteTenant.getId()).
                        append("> ").
                        append(userId).
                        append(" view ").
                        append(itemDescription).
                        append(" (id:").
                        append(itemId).
                        append(")").toString());
            }
        }

        return item;

    }

    
    
    //    @IOLog
    //    @Profiled
    //    public void searchItem(Integer tenantId,
    //                           String userId,
    //                           String sessionId,
    //                           String ip,
    //                           String itemId,
    //                           String itemType,
    //                           Boolean searchSucceeded,
    //                           Integer numberOfFoundItems,
    //                           String description) throws ShopRecommenderException
    //    {
    //        domainActionService.searchItem(tenantId, idMappingDAO.lookup(userId), sessionId, ip, new ItemVO<Integer, String>(tenantId, idMappingDAO.lookup(itemId), itemType), searchSucceeded, numberOfFoundItems, description);
    //    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Recommendations
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @IOLog
    @Profiled
    @Override
    public Recommendation alsoBoughtItems(Integer tenantId, String userId, String itemId, String itemType,
                                          String requestedItemType, Session session, Integer numberOfResults)
            throws EasyRecSoapException {

        Recommendation rec;
        RemoteTenant remoteTenant = remoteTenantDAO.get(tenantId);
        Item i = itemDAO.get(remoteTenant, itemId, itemType);

        if (i != null) {
            if (i.isActive()) {

                if (logger.isDebugEnabled()) {

                    logger.debug("<BOUGHT_SIMILAR@" + remoteTenant.getStringId() + "> " +
                            (!Strings.isNullOrEmpty(userId) ? userId : "anonymous") +
                            " requesting similar bought Items for " +
                            itemType + " " + i.getDescription() + " (id:" + itemId + ")");
                }

                Monitor monCore = MonitorFactory.start(JAMON_REST_ALSO_BOUGHT_CORE);

                RecommendationVO<Integer, String> recommendation =
                        domainRecommenderService
                                .alsoBoughtItems(tenantId, idMappingDAO.lookup(userId), session.getSessionId(),
                                        new ItemVO<Integer, String>(tenantId, idMappingDAO.lookup(itemId),
                                                itemType),
                                        requestedItemType);
                monCore.stop();

                List<Item> items = idMappingService
                        .mapRecommendedItems(recommendation, remoteTenant, idMappingDAO.lookup(userId), session,
                                numberOfResults);

                rec = new Recommendation(remoteTenant.getStringId(), WS.ACTION_OTHER_USERS_ALSO_BOUGHT, userId,
                        session.getSessionId(), i, items);
            } else {
                throw new EasyRecSoapException(MSG.ITEM_NOT_ACTIVE);
            }
        } else {
            throw new EasyRecSoapException(MSG.ITEM_NOT_EXISTS);
        }

        return rec;
    }

    @IOLog
    @Profiled
    @Override
    public Recommendation alsoViewedItems(Integer tenantId, String userId, String itemId, String itemType,
                                          String requestedItemType, Session session, Integer numberOfResults)
            throws EasyRecSoapException {

        Recommendation rec;
        RemoteTenant remoteTenant = remoteTenantDAO.get(tenantId);
        Item i = itemDAO.get(remoteTenant, itemId, itemType);

        if (i != null) {
            if (i.isActive()) {

                if (logger.isDebugEnabled()) {

                    logger.debug("<VIEW_SIMILAR@" + remoteTenant.getStringId() + "> " +
                            (!Strings.isNullOrEmpty(userId) ? userId : "anonymous") +
                            " requesting similar viewed Items for " +
                            itemType + " " + i.getDescription() + " (id:" + itemId + ")");
                }

                Monitor monCore = MonitorFactory.start(JAMON_REST_ALSO_VIEWED_CORE);

                RecommendationVO<Integer, String> recommendation =
                        domainRecommenderService
                                .alsoViewedItems(tenantId, idMappingDAO.lookup(userId), session.getSessionId(),
                                        new ItemVO<Integer, String>(tenantId, idMappingDAO.lookup(itemId),
                                                itemType),
                                        requestedItemType);
                monCore.stop();

                List<Item> items = idMappingService
                        .mapRecommendedItems(recommendation, remoteTenant, idMappingDAO.lookup(userId), session,
                                numberOfResults);

                rec = new Recommendation(remoteTenant.getStringId(), WS.ACTION_OTHER_USERS_ALSO_VIEWED, userId,
                        session.getSessionId(), i, items);

            } else {
                throw new EasyRecSoapException(MSG.ITEM_NOT_ACTIVE);
            }
        } else {
            throw new EasyRecSoapException(MSG.ITEM_NOT_EXISTS);
        }

        return rec;
    }

    @IOLog
    @Profiled
    @Override
    public Recommendation alsoGoodRatedItems(Integer tenantId, String userId, String itemId, String itemType,
                                             String requestedItemType, Session session, Integer numberOfResults)
            throws EasyRecSoapException {
        Recommendation rec;
        RemoteTenant remoteTenant = remoteTenantDAO.get(tenantId);

        Item i = itemDAO.get(remoteTenant, itemId, itemType);

        if (i != null) {
            if (i.isActive()) {

                if (logger.isDebugEnabled()) {

                    logger.debug("<ALSO_RATED_GOOD_SIMILAR@" + remoteTenant.getStringId() + "> " +
                            (!Strings.isNullOrEmpty(userId) ? userId : "anonymous") +
                            " requesting similar also good rated Items for " + itemType + " " +
                            i.getDescription() + " (id:" + itemId + ")");
                }

                Monitor monCore = MonitorFactory.start(JAMON_REST_ALSO_RATED_CORE);

                RecommendationVO<Integer, String> recommendation =
                        domainRecommenderService
                                .alsoGoodRatedItems(tenantId, idMappingDAO.lookup(userId), session.getSessionId(),
                                        new ItemVO<Integer, String>(tenantId, idMappingDAO.lookup(itemId),
                                                itemType),
                                        requestedItemType);
                monCore.stop();

                List<Item> items = idMappingService
                        .mapRecommendedItems(recommendation, remoteTenant, idMappingDAO.lookup(userId), session,
                                numberOfResults);

                rec = new Recommendation(remoteTenant.getStringId(), WS.ACTION_ITEMS_RATED_GOOD_BY_OTHER_USERS, userId,
                        session.getSessionId(), i, items);

            } else {
                throw new EasyRecSoapException(MSG.ITEM_NOT_ACTIVE);
            }
        } else {
            throw new EasyRecSoapException(MSG.ITEM_NOT_EXISTS);
        }

        return rec;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Rankings
    ///////////////////////////////////////////////////////////////////////////////////////////////
    @SuppressWarnings({"unchecked"})
    @IOLog
    @Profiled
    @Override
    public List<Item> mostBoughtItems(Integer tenantId, String itemType, Integer numberOfResults, String timeRange,
                                      TimeConstraintVO constraint, Session session) {
        List<Item> items;

        RemoteTenant remoteTenant = remoteTenantDAO.get(tenantId);

        if (logger.isDebugEnabled()) {

            logger.debug("<MOST_BOUGHT@" + remoteTenant.getStringId() + "> " + " requesting most bought Items");
        }

        if (timeRange == null) {
            timeRange = "ALL";
        } // default timeRange
        Element e = cache.get(tenantId + WS.ACTION_MOST_BOUGHT + itemType + timeRange);
        if ((e != null) && (!e.isExpired())) {
            items = itemService.filterDeactivatedItems((List<Item>) e.getValue());
            return items.subList(0, Math.min(items.size(), numberOfResults));
        }
        if (constraint == null) constraint = new TimeConstraintVO();
        adjustConstraint(constraint, TimeRange.getEnumFromString(timeRange));


        Monitor monCore = MonitorFactory.start(JAMON_REST_MOST_BOUGHT_CORE);

        List<RankedItemVO<Integer, String>> rankedItems = domainActionService
                .mostBoughtItems(tenantId, itemType, WS.MAX_NUMBER_OF_RANKING_RESULTS, constraint, Boolean.TRUE);

        // filter invisible items
        Set<String> invisibleItemTypes = typeMappingService.getItemTypes(tenantId, false);

        if (invisibleItemTypes.size() > 0) {
            ListIterator<RankedItemVO<Integer, String>> iterator = rankedItems.listIterator();

            while (iterator.hasNext()) {
                RankedItemVO<Integer, String> rankedItemVO = iterator.next();

                if (invisibleItemTypes.contains(rankedItemVO.getItem().getType()))
                    iterator.remove();
            }
        }

        monCore.stop();

        items = idMappingService.mapRankedItems(rankedItems, remoteTenant, session, WS.MAX_NUMBER_OF_RANKING_RESULTS);

        cache.put(new Element(tenantId + WS.ACTION_MOST_BOUGHT + itemType + timeRange, items));

        return items.subList(0, Math.min(items.size(), numberOfResults));
    }

    @SuppressWarnings({"unchecked"})
    @IOLog
    @Profiled
    @Override
    public List<Item> mostViewedItems(Integer tenantId, String itemType, Integer numberOfResults, String timeRange,
                                      TimeConstraintVO constraint, Session session) {
        List<Item> items;

        RemoteTenant remoteTenant = remoteTenantDAO.get(tenantId);

        if (logger.isDebugEnabled()) {

            logger.debug("<MOST_VIEWED@" + remoteTenant.getStringId() + "> " + " requesting most viewed Items");
        }

        if (timeRange == null) {
            timeRange = "ALL";
        } // default timeRange
        Element e = cache.get(tenantId + WS.ACTION_MOST_VIEWED + itemType + timeRange);
        if ((e != null) && (!e.isExpired())) {
            logger.debug("most viewed - cache hit");
            items = itemService.filterDeactivatedItems((List<Item>) e.getValue());
            return items.subList(0, Math.min(items.size(), numberOfResults));
        }
        if (constraint == null) constraint = new TimeConstraintVO();
        adjustConstraint(constraint, TimeRange.getEnumFromString(timeRange));

        logger.debug("most viewed - cache miss - fetching new data from db");
        Monitor monCore = MonitorFactory.start(JAMON_REST_MOST_VIEWED_CORE);
        List<RankedItemVO<Integer, String>> rankedItems = domainActionService
                .mostViewedItems(tenantId, itemType, WS.MAX_NUMBER_OF_RANKING_RESULTS, constraint, Boolean.TRUE);

        // filter invisible items
        Set<String> invisibleItemTypes = typeMappingService.getItemTypes(tenantId, false);

        if (invisibleItemTypes.size() > 0) {
            ListIterator<RankedItemVO<Integer, String>> iterator = rankedItems.listIterator();

            while (iterator.hasNext()) {
                RankedItemVO<Integer, String> rankedItemVO = iterator.next();

                if (invisibleItemTypes.contains(rankedItemVO.getItem().getType()))
                    iterator.remove();
            }
        }

        monCore.stop();

        items = idMappingService.mapRankedItems(rankedItems, remoteTenant, session, WS.MAX_NUMBER_OF_RANKING_RESULTS);

        cache.put(new Element(tenantId + WS.ACTION_MOST_VIEWED + itemType + timeRange, items));
        return items.subList(0, Math.min(items.size(), numberOfResults));
    }

    @SuppressWarnings({"unchecked"})
    @IOLog
    @Profiled
    @Override
    public List<Item> mostRatedItems(Integer tenantId, String itemType, Integer numberOfResults, String timeRange,
                                     TimeConstraintVO constraint, Session session) {
        List<Item> items;

        RemoteTenant remoteTenant = remoteTenantDAO.get(tenantId);

        if (logger.isDebugEnabled()) {

            logger.debug("<MOST_RATED@" + remoteTenant.getStringId() + "> " + " requesting most rated Items");
        }

        if (timeRange == null) {
            timeRange = "ALL";
        } // default timeRange
        Element e = cache.get(tenantId + WS.ACTION_MOST_RATED + itemType + timeRange);
        if ((e != null) && (!e.isExpired())) {
            items = itemService.filterDeactivatedItems((List<Item>) e.getValue());
            return items.subList(0, Math.min(items.size(), numberOfResults));
        }
        if (constraint == null) constraint = new TimeConstraintVO();
        adjustConstraint(constraint, TimeRange.getEnumFromString(timeRange));

        Monitor monCore = MonitorFactory.start(JAMON_REST_MOST_RATED_CORE);

        List<RankedItemVO<Integer, String>> rankedItems = domainActionService
                .mostRatedItems(tenantId, itemType, WS.MAX_NUMBER_OF_RANKING_RESULTS, constraint, Boolean.TRUE);

        // filter invisible items
        Set<String> invisibleItemTypes = typeMappingService.getItemTypes(tenantId, false);

        if (invisibleItemTypes.size() > 0) {
            ListIterator<RankedItemVO<Integer, String>> iterator = rankedItems.listIterator();

            while (iterator.hasNext()) {
                RankedItemVO<Integer, String> rankedItemVO = iterator.next();

                if (invisibleItemTypes.contains(rankedItemVO.getItem().getType()))
                    iterator.remove();
            }
        }

        monCore.stop();

        items = idMappingService.mapRankedItems(rankedItems, remoteTenant, session, WS.MAX_NUMBER_OF_RANKING_RESULTS);

        cache.put(new Element(tenantId + WS.ACTION_MOST_RATED + itemType + timeRange, items));
        return items.subList(0, Math.min(items.size(), numberOfResults));
    }

    @Override
    public List<Item> itemsOfCluster(Integer tenant, String clusterName, Integer numberOfResults, String strategy,
                                     Boolean useFallback, Integer itemType, Session session)
            throws EasyRecSoapException {
        List<Item> items;
        RemoteTenant remoteTenant = remoteTenantDAO.get(tenant);

        if (logger.isDebugEnabled())
            logger.debug("<ITEMS_OF_CLUSTER@" + remoteTenant.getStringId() + "> " + " requesting items of cluster " +
                    clusterName);

        logger.info("Use fallback: " + useFallback);

        ClusterVO cluster = clusterService.loadCluster(tenant, clusterName);

        if (cluster != null) {
            List<ItemVO<Integer, Integer>> clusterItems =
                    clusterService.getItemsOfCluster(cluster, strategy, useFallback, numberOfResults, itemType);

            items = idMappingService.mapClusterItems(clusterItems, remoteTenant, session,
                    WS.MAX_NUMBER_OF_RANKING_RESULTS);

            return items.subList(0, Math.min(items.size(), numberOfResults));
        } else {
            throw new EasyRecSoapException(MSG.CLUSTER_NOT_EXISTS);
        }
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Ratings
    ///////////////////////////////////////////////////////////////////////////////////////////////
    @SuppressWarnings({"unchecked"})
    @IOLog
    @Profiled
    @Override
    public List<Item> worstRatedItems(Integer tenantId, String userId, String itemType, Integer numberOfResults,
                                      String timeRange, TimeConstraintVO constraint, Session session) {
        List<Item> items;

        RemoteTenant remoteTenant = remoteTenantDAO.get(tenantId);

        if (logger.isDebugEnabled()) {

            logger.debug("<WORST_RATED@" + remoteTenant.getStringId() + "> " + " requesting worst rated Items");
        }

        if (timeRange == null) {
            timeRange = "ALL";
        } // default timeRange
        if (userId == null) {
            Element e = cache.get(tenantId + WS.ACTION_WORST_RATED + itemType + timeRange);
            if ((e != null) && (!e.isExpired())) {
                items = itemService.filterDeactivatedItems((List<Item>) e.getValue());
                return items.subList(0, Math.min(items.size(), numberOfResults));
            }
            if (constraint == null) constraint = new TimeConstraintVO();
            adjustConstraint(constraint, TimeRange.getEnumFromString(timeRange));
        }

        Monitor monCore = MonitorFactory.start(JAMON_REST_WORST_RATED_CORE);

        List<RatingVO<Integer, String>> ratedItems = domainActionService
                .badItemRatings(tenantId, idMappingDAO.lookup(userId), null, itemType, WS.MAX_NUMBER_OF_RANKING_RESULTS,
                        constraint);

        // filter invisible items
        Set<String> invisibleItemTypes = typeMappingService.getItemTypes(tenantId, false);

        if (invisibleItemTypes.size() > 0) {
            ListIterator<RatingVO<Integer, String>> iterator = ratedItems.listIterator();

            while (iterator.hasNext()) {
                RatingVO<Integer, String> ratingVO = iterator.next();

                if (invisibleItemTypes.contains(ratingVO.getItem().getType()))
                    iterator.remove();
            }
        }

        monCore.stop();

        items = idMappingService.mapRatedItems(ratedItems, remoteTenant, session, WS.MAX_NUMBER_OF_RANKING_RESULTS);

        if ((userId == null)) cache.put(new Element(tenantId + WS.ACTION_WORST_RATED + itemType + timeRange, items));
        return items.subList(0, Math.min(items.size(), numberOfResults));
    }

    @SuppressWarnings({"unchecked"})
    @IOLog
    @Profiled
    @Override
    public List<Item> bestRatedItems(Integer tenantId, String userId, String itemType, Integer numberOfResults,
                                     String timeRange, TimeConstraintVO constraint, Session session) {
        List<Item> items;

        RemoteTenant remoteTenant = remoteTenantDAO.get(tenantId);

        if (logger.isDebugEnabled()) {

            logger.debug("<BEST_RATED@" + remoteTenant.getStringId() + "> " + " requesting best rated Items");
        }

        if (timeRange == null) {
            timeRange = "ALL";
        } // default timeRange
        if (userId == null) {
            Element e = cache.get(tenantId + WS.ACTION_BEST_RATED + itemType + timeRange);
            if ((e != null) && (!e.isExpired())) {
                items = itemService.filterDeactivatedItems((List<Item>) e.getValue());
                return items.subList(0, Math.min(items.size(), numberOfResults));
            }
            if (constraint == null) constraint = new TimeConstraintVO();
            adjustConstraint(constraint, TimeRange.getEnumFromString(timeRange));
        }

        Monitor monCore = MonitorFactory.start(JAMON_REST_BEST_RATED_CORE);

        List<RatingVO<Integer, String>> ratedItems = domainActionService
                .goodItemRatings(tenantId, idMappingDAO.lookup(userId), null, itemType,
                        WS.MAX_NUMBER_OF_RANKING_RESULTS,
                        constraint);

        // filter invisible items
        Set<String> invisibleItemTypes = typeMappingService.getItemTypes(tenantId, false);

        if (invisibleItemTypes.size() > 0) {
            ListIterator<RatingVO<Integer, String>> iterator = ratedItems.listIterator();

            while (iterator.hasNext()) {
                RatingVO<Integer, String> ratingVO = iterator.next();

                if (invisibleItemTypes.contains(ratingVO.getItem().getType()))
                    iterator.remove();
            }
        }

        monCore.stop();

        items = idMappingService.mapRatedItems(ratedItems, remoteTenant, session, WS.MAX_NUMBER_OF_RANKING_RESULTS);

        if ((userId == null)) cache.put(new Element(tenantId + WS.ACTION_BEST_RATED + itemType + timeRange, items));
        return items.subList(0, Math.min(items.size(), numberOfResults));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Recommendations
    ///////////////////////////////////////////////////////////////////////////////////////////////
    @IOLog
    @Profiled
    @Override
    public RecommendedItem[] itemsBasedOnPurchaseHistory(Integer tenantId, String userId, String sessionId,
                                                         String consideredItemType,
                                                         Integer numberOfLastActionsConsidered, String assocType,
                                                         String requestedItemType) throws EasyRecSoapException {
        RecommendationVO<Integer, String> recommendation = domainRecommenderService
                .itemsBasedOnPurchaseHistory(tenantId, idMappingDAO.lookup(userId), sessionId, consideredItemType,
                        numberOfLastActionsConsidered, assocType, requestedItemType);
        List<RecommendedItem> recommendedItems = idMappingService
                .convertListOfRecommendedItemVOs(recommendation.getRecommendedItems());
        if (recommendedItems != null && recommendedItems.size() > 0) {
            return recommendedItems.toArray(new RecommendedItem[recommendedItems.size()]);
        } else {
            return new RecommendedItem[0];
        }
    }

    @IOLog
    @Profiled
    @Override
    public Recommendation itemsBasedOnViewingHistory(Integer tenantId, String userId, Session session,
                                                     String consideredItemType, Integer numberOfLastActionsConsidered,
                                                     String assocType, String requestedItemType,
                                                     Integer numberOfRecommendations) throws EasyRecSoapException {
        Recommendation rec;
        RemoteTenant remoteTenant = remoteTenantDAO.get(tenantId);

        Monitor monCore = MonitorFactory.start(JAMON_REST_RECS_FOR_USER);
        RecommendationVO<Integer, String> recommendation =
                domainRecommenderService.itemsBasedOnViewingHistory(
                        tenantId, idMappingDAO.lookup(userId), null, // no sessionId needed
                        consideredItemType, numberOfLastActionsConsidered, assocType, requestedItemType);

        monCore.stop();
        List<Item> items = idMappingService.mapRecommendedItems(recommendation, remoteTenant,
                idMappingDAO.lookup(userId), session,
                numberOfRecommendations);  // session needed for building backtracking url (session.getRequest())


        rec = new Recommendation(remoteTenant.getStringId(), WS.ACTION_RECOMMENDATIONS_FOR_USER, userId, null,
                // no sessionId needed
                null, // no base item needed
                items);
        return rec;
    }

    @IOLog
    @Profiled
    @Override
    public Recommendation actionHistory(Integer tenantId, String userId, Session session,
                                                    String consideredActionType, String consideredItemType, 
                                                    Integer numberOfLastActionsConsidered,
                                                    Integer numberOfRecommendations) throws EasyRecSoapException {
        Recommendation rec;
        RemoteTenant remoteTenant = remoteTenantDAO.get(tenantId);

        Monitor monCore = MonitorFactory.start(JAMON_REST_ACTION_HISTORY_CORE);
        List<ItemVO<Integer, String>> recommendation =
                domainRecommenderService.getActionHistory(tenantId, 
                                                          idMappingDAO.lookup(userId),
                                                          null, 
                                                          consideredActionType, 
                                                          consideredItemType, 
                                                          null, 
                                                          numberOfLastActionsConsidered);

        monCore.stop();
        List<Item> items = idMappingService.mapListOfItemVOs(recommendation, 
                                                            remoteTenant, 
                                                            idMappingDAO.lookup(userId), 
                                                            session, 
                                                            numberOfRecommendations);

        rec = new Recommendation(remoteTenant.getStringId(), WS.ACTION_HISTORY, userId, 
                null, // no sessionId needed
                null, // no base item needed
                items);
        return rec;
    }
    
    @IOLog
    @Profiled
    @Override
    public Recommendation itemsBasedOnActionHistory(Integer tenantId, String userId, Session session,
                                                    String consideredActionType, String consideredItemType, 
                                                    Integer numberOfLastActionsConsidered,
                                                    String assocType, String requestedItemType,
                                                    Integer numberOfRecommendations) throws EasyRecSoapException {
        Recommendation rec;
        RemoteTenant remoteTenant = remoteTenantDAO.get(tenantId);

        Double ratingThreshold = null;
        if (consideredActionType.equals(TypeMappingService.ACTION_TYPE_RATE)) {
            ratingThreshold = tenantService.getTenantById(tenantId).getRatingRangeNeutral();
        }
        Monitor monCore = MonitorFactory.start(JAMON_REST_RECS_FOR_USER);
        RecommendationVO<Integer, String> recommendation =
                domainRecommenderService.getItemsBasedOnActionHistory(
                        tenantId, idMappingDAO.lookup(userId), null, // no sessionId needed
                        consideredActionType, consideredItemType, ratingThreshold, numberOfLastActionsConsidered, assocType, requestedItemType);

        monCore.stop();
        List<Item> items = idMappingService.mapRecommendedItems(recommendation, remoteTenant,
                idMappingDAO.lookup(userId), session,
                numberOfRecommendations);  // session needed for building backtracking url (session.getRequest())


        rec = new Recommendation(remoteTenant.getStringId(), WS.ACTION_RECOMMENDATIONS_FOR_USER, userId, null,
                // no sessionId needed
                null, // no base item needed
                items);
        return rec;
    }

    @IOLog
    @Profiled
    @Override
    public Recommendation itemsForUser(Integer tenantId, String userId, Session session,
                                       String consideredActionType, String consideredItemType, Integer numberOfLastActionsConsidered,
                                       String assocType, String requestedItemType,
                                       Integer numberOfRecommendations) throws EasyRecSoapException {

        Recommendation rec = null;
        RemoteTenant remoteTenant = remoteTenantDAO.get(tenantId);

        // double check: should have been checked before
        if (remoteTenant.getPluginsEnabled()) {
            //TODO: add sourceType to query!!! (dm: copy paste from relatedItems - refactor shared code ?)
            RecommendationVO<Integer, String> recommendation =
                    domainRecommenderService
                            .getAlsoActedItems(tenantId, idMappingDAO.lookup(userId), session.getSessionId(),
                                    AssocTypeDAO.ASSOCTYPE_USER_TO_ITEM,
                                    new ItemVO<Integer, String>(tenantId, idMappingDAO.lookup(userId),
                                            TypeMappingService.ITEM_TYPE_USER), null,
                                    requestedItemType);

            List<Item> items = idMappingService
                    .mapRecommendedItems(recommendation, remoteTenant, idMappingDAO.lookup(userId), session,
                            numberOfRecommendations);

            rec = new Recommendation(remoteTenant.getStringId(), WS.ACTION_RECOMMENDATIONS_FOR_USER, userId, null, null, items);

        }
        return rec;

    }

    @IOLog
    @Profiled
    @Override
    public RecommendedItem[] itemsBasedOnSearchingHistory(Integer tenantId, String userId, String sessionId,
                                                          String consideredItemType,
                                                          Integer numberOfLastActionsConsidered, String assocType,
                                                          String requestedItemType) throws EasyRecSoapException {
        RecommendationVO<Integer, String> recommendation = domainRecommenderService
                .itemsBasedOnSearchingHistory(tenantId, idMappingDAO.lookup(userId), sessionId, consideredItemType,
                        numberOfLastActionsConsidered, assocType, requestedItemType);
        List<RecommendedItem> recommendedItems = idMappingService
                .convertListOfRecommendedItemVOs(recommendation.getRecommendedItems());
        if (recommendedItems != null && recommendedItems.size() > 0) {
            return recommendedItems.toArray(new RecommendedItem[recommendedItems.size()]);
        } else {
            return new RecommendedItem[0];
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Plugin supported methods
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Recommendation relatedItems(Integer tenantId, String assocType, String userId, String itemId, String itemType,
                                       String requestedItemType, Session session, Integer numberOfResults)
            throws EasyRecSoapException {

        Recommendation rec = null;
        RemoteTenant remoteTenant = remoteTenantDAO.get(tenantId);
        Item i = null;
        
        if (!"USER".equals(itemType)) {
            i = itemDAO.get(remoteTenant, itemId, itemType);

            if (i == null)
                throw new EasyRecSoapException(MSG.ITEM_NOT_EXISTS);

            if (!i.isActive())
                throw new EasyRecSoapException(MSG.ITEM_NOT_ACTIVE);
        } else {
            //create dummy item for User
            i = new Item(null,tenantId,itemId,itemType,"user id " + itemId, null, null, null, true,null);
        }
        // double check: should have been checked before
        if (remoteTenant.getPluginsEnabled()) {

            //TODO: add sourceType to query!!!
            RecommendationVO<Integer, String> recommendation =
                    domainRecommenderService
                            .getAlsoActedItems(tenantId, idMappingDAO.lookup(userId), session.getSessionId(),
                                    assocType,
                                    new ItemVO<Integer, String>(tenantId, idMappingDAO.lookup(itemId),
                                            itemType), null,
                                    requestedItemType);

            List<Item> items = idMappingService
                    .mapRecommendedItems(recommendation, remoteTenant, idMappingDAO.lookup(userId), session,
                            numberOfResults);

            rec = new Recommendation(remoteTenant.getStringId(), WS.ACTION_RELATED_ITEMS, userId,
                    session.getSessionId(),
                    i, items);

        }
        return rec;
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Utility Methods
    ///////////////////////////////////////////////////////////////////////////////////////////////
    @IOLog
    @Profiled
    @Override
    public String[] getAssocTypes(Integer tenantId) throws EasyRecSoapException {
        return typeMappingService.getAssocTypes(tenantId)
                .toArray(new String[typeMappingService.getAssocTypes(tenantId).size()]);
    }

    @IOLog
    @Profiled
    @Override
    public Set<String> getItemTypes(Integer tenantId) throws EasyRecSoapException {
        return typeMappingService.getItemTypes(tenantId, true);
    }

    @IOLog
    @Profiled
    @Override
    public List<ClusterVO> getClusters(Integer tenantId) throws EasyRecSoapException {
        return Lists.newArrayList(clusterService.getClustersForTenant(tenantId).getVertices());
    }

    @IOLog
    @Profiled
    @Override
    public List<ItemAssocVO<String, String>> getRules(Integer tenantId) {
        List<ItemAssocVO<String, String>> rules;

        List<ItemAssocVO<Integer, String>> itemAssocs = domainItemAssocService
                .getItemAssocsFromTenant(tenantId, 2000);

        rules = idMappingService.mapItemAssocs(itemAssocs, true);

        return rules;
    }

    @IOLog
    @Profiled
    @Override
    public List<ItemAssocVO<String, String>> getRules(Item item) {
        List<ItemAssocVO<String, String>> rules;

        ItemVO<Integer, String> itemFrom = new ItemVO<Integer, String>(item.getTenantId(),
                idMappingDAO.lookup(item.getItemId()), item.getItemType());

        List<ItemAssocVO<Integer, String>> itemAssocs = domainItemAssocService
                .getItemAssocsForItem(item.getTenantId(), itemFrom, 200);

        rules = idMappingService.mapItemAssocs(itemAssocs, true);

        return rules;
    }

    /*
        @IOLog
        @Profiled
        public String getProfile(Integer tenantId, String itemId, String itemType) throws ShopRecommenderException {
            return profileService.getProfile(tenantId, idMappingDAO.lookup(itemId), itemType);
        }

        @IOLog
        @Profiled
        public String[] getSimilarProfiles(Integer tenantId, String itemId, String itemType) throws ShopRecommenderException {

            ItemVO<Integer, String> item2 = new ItemVO<Integer, String>(tenantId, idMappingDAO.lookup("1"), itemType);
            profileMatcherService.match(new ItemVO<Integer, String>(tenantId, idMappingDAO.lookup(itemId), itemType), item2);
            return new String[0];
        }

        @IOLog
        @Profiled
        public void storeProfile(Integer tenantId, String itemId, String itemType, String profileXML) throws ShopRecommenderException {
            profileService.storeProfile(tenantId, idMappingDAO.lookup(itemId), itemType, profileXML, true);
        }
    */
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Profile aware Methods
    ///////////////////////////////////////////////////////////////////////////////////////////////


    // private methods
    private void adjustConstraint(TimeConstraintVO constraint, TimeRange timeRange) {

        Calendar cal = Calendar.getInstance();
        Date end = cal.getTime();
        Date start = null;
        switch (timeRange) {
            case DAY:
                cal.setTimeInMillis(end.getTime() - 86400000);
                start = cal.getTime();
                break;
            case WEEK:
                cal.setTimeInMillis(end.getTime() - 604800000);
                start = cal.getTime();
                break;
            case MONTH:
                cal.add(Calendar.MONTH, -1);
                start = cal.getTime();
                break;
            case ALL:   //start = null;
                break;
        }
        constraint.setDateFrom(start);
        constraint.setDateTo(end);
    }


    // getter/setter
    @SuppressWarnings({"UnusedDeclaration"})
    public DomainRecommenderService getDomainRecommenderService() {
        return domainRecommenderService;
    }

    public void setDomainRecommenderService(DomainRecommenderService domainRecommenderService) {
        this.domainRecommenderService = domainRecommenderService;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public DomainActionService getDomainActionService() {
        return domainActionService;
    }

    public void setDomainActionService(DomainActionService domainActionService) {
        this.domainActionService = domainActionService;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public DomainItemAssocService getDomainItemAssocService() {
        return domainItemAssocService;
    }

    public void setDomainItemAssocService(DomainItemAssocService domainItemAssocService) {
        this.domainItemAssocService = domainItemAssocService;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public TypeMappingService getTypeMappingService() {
        return typeMappingService;
    }

    public void setTypeMappingService(TypeMappingService typeMappingService) {
        this.typeMappingService = typeMappingService;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public IDMappingDAO getIdMappingDAO() {
        return idMappingDAO;
    }

    public void setIdMappingDAO(IDMappingDAO idMappingDAO) {
        this.idMappingDAO = idMappingDAO;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public IDMappingService getIdMappingService() {
        return idMappingService;
    }

    public void setIdMappingService(IDMappingService idMappingService) {
        this.idMappingService = idMappingService;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public ItemDAO getItemDAO() {
        return itemDAO;
    }

    public void setItemDAO(ItemDAO itemDAO) {
        this.itemDAO = itemDAO;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public RemoteTenantDAO getRemoteTenantDAO() {
        return remoteTenantDAO;
    }

    public void setRemoteTenantDAO(RemoteTenantDAO remoteTenantDAO) {
        this.remoteTenantDAO = remoteTenantDAO;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public ProfileService getProfileService() {
        return profileService;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void setProfileService(ProfileService profileService) {
        this.profileService = profileService;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public ProfileMatcherService getProfileMatcherService() {
        return profileMatcherService;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void setProfileMatcherService(ProfileMatcherService profileMatcherService) {
        this.profileMatcherService = profileMatcherService;
    }

    public void setItemService(ItemService itemService) {
        this.itemService = itemService;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
    }

    @Override
    public void emptyCache() {
        cache.removeAll();
    }

    public ClusterService getClusterService() {
        return clusterService;
    }

    public void setClusterService(ClusterService clusterService) {
        this.clusterService = clusterService;
    }

    public TenantService getTenantService() {
        return tenantService;
    }

    public void setTenantService(TenantService tenantService) {
        this.tenantService = tenantService;
    }
}
