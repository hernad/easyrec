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
package org.easyrec.rest;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import com.sun.jersey.api.json.JSONWithPadding;
import com.sun.jersey.spi.resource.Singleton;
import org.easyrec.model.core.ClusterVO;
import org.easyrec.model.core.transfer.TimeConstraintVO;
import org.easyrec.model.web.*;
import org.easyrec.service.core.TenantService;
import org.easyrec.service.domain.TypeMappingService;
import org.easyrec.service.web.RemoteAssocService;
import org.easyrec.service.web.nodomain.ShopRecommenderService;
import org.easyrec.soap.nodomain.exception.EasyRecSoapException;
import org.easyrec.store.dao.core.types.AssocTypeDAO;
import org.easyrec.store.dao.web.ItemDAO;
import org.easyrec.store.dao.web.OperatorDAO;
import org.easyrec.store.dao.web.RemoteTenantDAO;
import org.easyrec.utils.MyUtils;
import org.easyrec.vocabulary.MSG;
import org.easyrec.vocabulary.WS;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author szavrel
 */
@Path("{type: (1.0(?:/json)?)}")
@Produces({"application/xml", "application/json"})
@Singleton
public class EasyRec {

    @Context
    public HttpServletRequest request;

    private OperatorDAO operatorDAO;
    private ItemDAO itemDAO;
    private RemoteAssocService remoteAssocService;
    private RemoteTenantDAO remoteTenantDAO;
    private TenantService tenantService;
    private ShopRecommenderService shopRecommenderService;
    private TypeMappingService typeMappingService;
    private SimpleDateFormat dateFormatter;

    // Jamon Loggers
    private final static String JAMON_REST_VIEW = "rest.view";
    private final static String JAMON_REST_BUY = "rest.buy";
    private final static String JAMON_REST_RATE = "rest.rate";
    private final static String JAMON_REST_ACTION = "rest.action";
    private final static String JAMON_REST_ALSO_VIEWED = "rest.alsoviewed";
    private final static String JAMON_REST_ALSO_BOUGHT = "rest.alsobought";
    private final static String JAMON_REST_ALSO_RATED = "rest.alsorated";
    private final static String JAMON_REST_RECS_FOR_USER = "rest.recsforuser";
    private final static String JAMON_REST_MOST_BOUGHT = "rest.mostbought";
    private final static String JAMON_REST_MOST_VIEWED = "rest.mostviewed";
    private final static String JAMON_REST_MOST_RATED = "rest.mostrated";
    private final static String JAMON_REST_BEST_RATED = "rest.bestrated";
    private final static String JAMON_REST_WORST_RATED = "rest.worstrated";
    private final static String JAMON_REST_IMPORT_RULE = "rest.import.rule";
    private final static String JAMON_REST_IMPORT_ITEM = "rest.import.item";
    private final static String JAMON_REST_ITEM_ACTIVE = "rest.item.active.rule";
    private final static String JAMON_REST_RELATED_ITEMS = "rest.related.items";
    private final static String JAMON_REST_ITEMTYPES = "rest.itemtypes";
    private final static String JAMON_REST_CLUSTERS = "rest.clusters";
    private final static String JAMON_REST_ACTIONHISTORY = "rest.history";

    public EasyRec(OperatorDAO operatorDAO, RemoteTenantDAO remoteTenantDAO,
                   ShopRecommenderService shopRecommenderService, TenantService tenantService,
                   TypeMappingService typeMappingService, ItemDAO itemDAO, RemoteAssocService remoteAssocService,
                   String dateFormatString) {
        this.operatorDAO = operatorDAO;
        this.remoteTenantDAO = remoteTenantDAO;
        this.shopRecommenderService = shopRecommenderService;
        this.tenantService = tenantService;
        this.typeMappingService = typeMappingService;
        this.itemDAO = itemDAO;
        this.remoteAssocService = remoteAssocService;
        this.dateFormatter = new SimpleDateFormat(dateFormatString);
    }

    @GET
    @Path("/view")
    public Response view(@PathParam("type") String type, @QueryParam("apikey") String apiKey,
                         @QueryParam("tenantid") String tenantId, @QueryParam("userid") String userId,
                         @QueryParam("sessionid") String sessionId, @QueryParam("itemid") String itemId,
                         @QueryParam("itemdescription") String itemDescription, @QueryParam("itemurl") String itemUrl,
                         @QueryParam("itemimageurl") String itemImageUrl, @QueryParam("actiontime") String actionTime,
                         @QueryParam("itemtype") String itemType, @QueryParam("callback") String callback)
            throws EasyRecException {
        Monitor mon = MonitorFactory.start(JAMON_REST_VIEW);

        // Collect a List of messages for the user to understand,
        // what went wrong (e.g. Wrong API key).
        List<Message> messages = new ArrayList<Message>();

        Integer coreTenantId = operatorDAO.getTenantId(apiKey, tenantId);
        RemoteTenant r = remoteTenantDAO.get(coreTenantId);

        //        if (r.isMaxActionLimitExceeded()) {
        //            messages.add(Message.MAXIMUM_ACTIONS_EXCEEDED);
        //        }

        checkParams(coreTenantId, itemId, itemDescription, itemUrl, sessionId, messages);
        Date actionDate = null;

        if (actionTime != null) {
            actionDate = MyUtils.dateFormatCheck(actionTime, dateFormatter);

            if (actionDate == null)
                messages.add(MSG.DATE_PARSE);
        }

        if (messages.size() > 0) {
            if ((WS.JSON_PATH.equals(type)))
                throw new EasyRecException(messages, WS.ACTION_VIEW, WS.RESPONSE_TYPE_JSON, callback);
            else
                throw new EasyRecException(messages, WS.ACTION_VIEW);
        }

        itemType = checkItemType(itemType, type, coreTenantId, tenantId, WS.ACTION_VIEW, callback);
        Session session = new Session(sessionId, request.getRemoteAddr());
        Item item = shopRecommenderService.viewItem(r, userId, itemId, itemType, itemDescription,
                itemUrl, itemImageUrl, actionDate, session);

        ResponseItem respItem = new ResponseItem(tenantId, WS.ACTION_VIEW, userId, sessionId, null, item);
        mon.stop();

        if (WS.JSON_PATH.equals(type)) {
            if (callback != null)
                return Response.ok(new JSONWithPadding(respItem, callback), WS.RESPONSE_TYPE_JSCRIPT)
                        .build();
            else
                return Response.ok(respItem, WS.RESPONSE_TYPE_JSON).build();
        } else
            return Response.ok(respItem, WS.RESPONSE_TYPE_XML).build();
    }

    @GET
    @Path("/rate")
    public Response rate(@PathParam("type") String type, @QueryParam("apikey") String apiKey,
                         @QueryParam("tenantid") String tenantId, @QueryParam("userid") String userId,
                         @QueryParam("sessionid") String sessionId, @QueryParam("itemid") String itemId,
                         @QueryParam("ratingvalue") String ratingValue,
                         @QueryParam("itemdescription") String itemDescription, @QueryParam("itemurl") String itemUrl,
                         @QueryParam("itemimageurl") String itemImageUrl, @QueryParam("actiontime") String actionTime,
                         @QueryParam("itemtype") String itemType, @QueryParam("callback") String callback)
            throws EasyRecException {

        Monitor mon = MonitorFactory.start(JAMON_REST_RATE);

        // Collect a List of messages for the user to understand,
        // what went wrong (e.g. Wrong API key).
        List<Message> messages = new ArrayList<Message>();

        Integer coreTenantId = operatorDAO.getTenantId(apiKey, tenantId);
        RemoteTenant r = remoteTenantDAO.get(coreTenantId);
        //        if (r.isMaxActionLimitExceeded()) {
        //            messages.add(Message.MAXIMUM_ACTIONS_EXCEEDED);
        //        }

        checkParams(coreTenantId, itemId, itemDescription, itemUrl, sessionId, messages);

        Date actionDate = null;

        if (actionTime != null) {
            actionDate = MyUtils.dateFormatCheck(actionTime, dateFormatter);

            if (actionDate == null)
                messages.add(MSG.DATE_PARSE);
        }

        Integer rateValue = -1;
        try {
            rateValue = Integer.valueOf(ratingValue);

            if (coreTenantId != null && (rateValue < tenantService.getTenantById(coreTenantId).getRatingRangeMin() ||
                    rateValue > tenantService.getTenantById(coreTenantId).getRatingRangeMax()))
                throw new Exception();
        } catch (Exception e) {
            messages.add(MSG.ITEM_INVALID_RATING_VALUE);
        }

        if (messages.size() > 0) {
            if ((WS.JSON_PATH.equals(type)))
                throw new EasyRecException(messages, WS.ACTION_RATE, WS.RESPONSE_TYPE_JSON, callback);
            else
                throw new EasyRecException(messages, WS.ACTION_RATE);
        }

        itemType = checkItemType(itemType, type, coreTenantId, tenantId, WS.ACTION_RATE, callback);
        Session session = new Session(sessionId, request.getRemoteAddr());

        Item item = shopRecommenderService.rateItem(r, userId, itemId, itemType, itemDescription,
                itemUrl, itemImageUrl, rateValue, actionDate, session);

        ResponseItem respItem = new ResponseItem(tenantId, WS.ACTION_RATE, userId, sessionId, ratingValue, item);

        mon.stop();

        if (WS.JSON_PATH.equals(type)) {
            if (callback != null)
                return Response.ok(new JSONWithPadding(respItem, callback), WS.RESPONSE_TYPE_JSCRIPT).build();
            else
                return Response.ok(respItem, WS.RESPONSE_TYPE_JSON).build();
        } else {
            return Response.ok(respItem, WS.RESPONSE_TYPE_XML).build();
        }
    }

    @GET
    @Path("/buy")
    public Response buy(@PathParam("type") String type, @QueryParam("apikey") String apiKey,
                        @QueryParam("tenantid") String tenantId, @QueryParam("userid") String userId,
                        @QueryParam("sessionid") String sessionId, @QueryParam("itemid") String itemId,
                        @QueryParam("itemdescription") String itemDescription, @QueryParam("itemurl") String itemUrl,
                        @QueryParam("itemimageurl") String itemImageUrl, @QueryParam("actiontime") String actionTime,
                        @QueryParam("itemtype") String itemType, @QueryParam("callback") String callback)
            throws EasyRecException {
        Monitor mon = MonitorFactory.start(JAMON_REST_BUY);

        // Collect a List of messages for the user to understand,
        // what went wrong (e.g. Wrong API key).
        List<Message> messages = new ArrayList<Message>();

        Integer coreTenantId = operatorDAO.getTenantId(apiKey, tenantId);
        RemoteTenant r = remoteTenantDAO.get(coreTenantId);
        //        if (r.isMaxActionLimitExceeded()) {
        //            messages.add(Message.MAXIMUM_ACTIONS_EXCEEDED);
        //        }

        checkParams(coreTenantId, itemId, itemDescription, itemUrl, sessionId, messages);

        Date actionDate = null;

        if (actionTime != null) {
            actionDate = MyUtils.dateFormatCheck(actionTime, dateFormatter);

            if (actionDate == null)
                messages.add(MSG.DATE_PARSE);
        }

        if (messages.size() > 0) {
            if ((WS.JSON_PATH.equals(type)))
                throw new EasyRecException(messages, WS.ACTION_BUY, WS.RESPONSE_TYPE_JSON, callback);
            else
                throw new EasyRecException(messages, WS.ACTION_BUY);
        }

        itemType = checkItemType(itemType, type, coreTenantId, tenantId, WS.ACTION_BUY, callback);
        Session session = new Session(sessionId, request.getRemoteAddr());

        Item item = shopRecommenderService.purchaseItem(r, userId, itemId, itemType, itemDescription,
                itemUrl, itemImageUrl, actionDate, session);

        ResponseItem respItem = new ResponseItem(tenantId, WS.ACTION_BUY, userId, sessionId, null, item);

        mon.stop();

        if (WS.JSON_PATH.equals(type)) {
            if (callback != null)
                return Response.ok(new JSONWithPadding(respItem, callback), WS.RESPONSE_TYPE_JSCRIPT).build();
            else
                return Response.ok(respItem, WS.RESPONSE_TYPE_JSON).build();
        } else
            return Response.ok(respItem, WS.RESPONSE_TYPE_XML).build();
    }

    @GET
    @Path("/sendaction")
    public Response sendAction(@PathParam("type") String type, @QueryParam("apikey") String apiKey,
                         @QueryParam("tenantid") String tenantId, @QueryParam("userid") String userId,
                         @QueryParam("sessionid") String sessionId, @QueryParam("itemid") String itemId,
                         @QueryParam("actiontype") String actionType, @QueryParam("actionvalue") String actionValue,
                         @QueryParam("itemdescription") String itemDescription, @QueryParam("itemurl") String itemUrl,
                         @QueryParam("itemimageurl") String itemImageUrl, @QueryParam("actiontime") String actionTime,
                         @QueryParam("itemtype") String itemType, @QueryParam("callback") String callback)
            throws EasyRecException {

        Monitor mon = MonitorFactory.start(JAMON_REST_ACTION);

        // Collect a List of messages for the user to understand,
        // what went wrong (e.g. Wrong API key).
        List<Message> messages = new ArrayList<Message>();

        Integer coreTenantId = operatorDAO.getTenantId(apiKey, tenantId);
        RemoteTenant r = remoteTenantDAO.get(coreTenantId);
        //        if (r.isMaxActionLimitExceeded()) {
        //            messages.add(Message.MAXIMUM_ACTIONS_EXCEEDED);
        //        }

        checkParams(coreTenantId, itemId, itemDescription, itemUrl, sessionId, messages);
        Integer actValue = -1;
        if (Strings.isNullOrEmpty(actionType)) {
            messages.add(MSG.MISSING_ACTIONTYPE);
        } else {
            Boolean hasValue = tenantService.hasActionValue(coreTenantId, actionType);
            if (hasValue == null) {
                messages.add(MSG.INVALID_ACTIONTYPE);
            } else {
                if (hasValue) {
                    if (Strings.isNullOrEmpty(actionValue)) {
                        messages.add(MSG.MISSING_ACTION_VALUE);
                    } else {
                        try {
                            actValue = Integer.valueOf(actionValue);
                        } catch (Exception e) {
                            messages.add(MSG.ITEM_INVALID_RATING_VALUE);
                        }
                    }
                }
            }
        }

        Date actionDate = null;

        if (actionTime != null) {
            actionDate = MyUtils.dateFormatCheck(actionTime, dateFormatter);

            if (actionDate == null)
                messages.add(MSG.DATE_PARSE);
        }

        if (messages.size() > 0) {
            if ((WS.JSON_PATH.equals(type)))
                throw new EasyRecException(messages, WS.ACTION_SENDACTION, WS.RESPONSE_TYPE_JSON, callback);
            else
                throw new EasyRecException(messages, WS.ACTION_SENDACTION);
        }

        itemType = checkItemType(itemType, type, coreTenantId, tenantId, WS.ACTION_RATE, callback);
        Session session = new Session(sessionId, request.getRemoteAddr());

        Item item = shopRecommenderService.sendAction(r, userId, itemId, itemType, itemDescription,
                itemUrl, itemImageUrl, actionType, actValue, actionDate, session);

        ResponseItem respItem = new ResponseItem(tenantId, WS.ACTION_SENDACTION, userId, sessionId, actionValue, item);

        mon.stop();

        if (WS.JSON_PATH.equals(type)) {
            if (callback != null)
                return Response.ok(new JSONWithPadding(respItem, callback), WS.RESPONSE_TYPE_JSCRIPT).build();
            else
                return Response.ok(respItem, WS.RESPONSE_TYPE_JSON).build();
        } else {
            return Response.ok(respItem, WS.RESPONSE_TYPE_XML).build();
        }
    }

    
    @GET
    @Path("/otherusersalsoviewed")
    public Response otherUsersAlsoViewed(@PathParam("type") String type, @QueryParam("apikey") String apiKey,
                                         @QueryParam("tenantid") String tenantId, @QueryParam("userid") String userId,
                                         @QueryParam("sessionid") String sessionId, @QueryParam("itemid") String itemId,
                                         @QueryParam("numberOfResults") Integer numberOfResults,
                                         @QueryParam("itemtype") String itemType,
                                         @QueryParam("requesteditemtype") String requestedItemType,
                                         @QueryParam("callback") String callback) throws EasyRecException {
        Monitor mon = MonitorFactory.start(JAMON_REST_ALSO_VIEWED);
        Recommendation rec = null;
        Integer coreTenantId = operatorDAO.getTenantId(apiKey, tenantId);

        if (coreTenantId == null)
            exceptionResponse(WS.ACTION_OTHER_USERS_ALSO_VIEWED, MSG.TENANT_WRONG_TENANT_APIKEY, type, callback);

        RemoteTenant r = remoteTenantDAO.get(coreTenantId);

        if (r.isMaxActionLimitExceeded())
            exceptionResponse(WS.ACTION_OTHER_USERS_ALSO_VIEWED, MSG.MAXIMUM_ACTIONS_EXCEEDED, type, callback);

        itemType = checkItemType(itemType, type, coreTenantId, tenantId, WS.ACTION_OTHER_USERS_ALSO_VIEWED, callback);
        requestedItemType = checkItemType(requestedItemType, type, coreTenantId, tenantId, WS.ACTION_OTHER_USERS_ALSO_VIEWED, callback, null);
        Session session = new Session(sessionId, request);

        try {
            if ((numberOfResults == null) || (numberOfResults > WS.DEFAULT_NUMBER_OF_RESULTS))
                numberOfResults = WS.DEFAULT_NUMBER_OF_RESULTS;

            rec = shopRecommenderService.alsoViewedItems(coreTenantId, userId, itemId, itemType, requestedItemType,
                    session, numberOfResults);
        } catch (EasyRecSoapException sre) {
            exceptionResponse(WS.ACTION_OTHER_USERS_ALSO_VIEWED, sre.getMessageObject(), type,
                    callback);
        }

        mon.stop();

        if (WS.JSON_PATH.equals(type)) {
            if (callback != null)
                return Response.ok(new JSONWithPadding(rec, callback), WS.RESPONSE_TYPE_JSCRIPT).build();
            else
                return Response.ok(rec, WS.RESPONSE_TYPE_JSON).build();
        } else
            return Response.ok(rec, WS.RESPONSE_TYPE_XML).build();
    }

    @GET
    @Path("/recommendationsforuser")
    public Response recommendationsForUser(@PathParam("type") String type, @QueryParam("apikey") String apiKey,
                                           @QueryParam("tenantid") String tenantId, @QueryParam("userid") String userId,
                                           @QueryParam("numberOfResults") Integer numberOfResults,
                                           @QueryParam("requesteditemtype") String requestedItemType,
                                           @QueryParam("callback") String callback,
                                           @QueryParam("actiontype") @DefaultValue(TypeMappingService.ACTION_TYPE_VIEW) String actiontype)
            throws EasyRecException {
        Monitor mon = MonitorFactory.start(JAMON_REST_RECS_FOR_USER);

        Recommendation rec = null;
        Session session = new Session(null, request);

        Integer coreTenantId = operatorDAO.getTenantId(apiKey, tenantId);

        if (coreTenantId == null)
            exceptionResponse(WS.ACTION_RECOMMENDATIONS_FOR_USER, MSG.TENANT_WRONG_TENANT_APIKEY, type, callback);

        RemoteTenant remoteTenant = remoteTenantDAO.get(coreTenantId);

        if (remoteTenant.isMaxActionLimitExceeded())
            exceptionResponse(WS.ACTION_RECOMMENDATIONS_FOR_USER, MSG.MAXIMUM_ACTIONS_EXCEEDED, type, callback);

        if (Strings.isNullOrEmpty(userId))
            exceptionResponse(WS.ACTION_RECOMMENDATIONS_FOR_USER, MSG.USER_NO_ID, type, callback);

        requestedItemType = checkItemType(requestedItemType, type, coreTenantId, tenantId, WS.ACTION_RECOMMENDATIONS_FOR_USER, callback, null);


        if (typeMappingService.getIdOfActionType(coreTenantId, actiontype) == null) {
            exceptionResponse(WS.ACTION_RECOMMENDATIONS_FOR_USER, MSG.OPERATION_FAILED.append(String.format(" actionType %s not found for tenant %s", actiontype, tenantId)), type, callback);
        }

        if ((numberOfResults == null) || (numberOfResults > WS.DEFAULT_NUMBER_OF_RESULTS))
            numberOfResults = WS.DEFAULT_NUMBER_OF_RESULTS;

        if (rec == null || rec.getRecommendedItems().isEmpty()) {
            try {
                rec = shopRecommenderService.itemsBasedOnActionHistory(coreTenantId, userId, session, actiontype, null, WS.ACTION_HISTORY_DEPTH, null,
                        requestedItemType, numberOfResults);
            } catch (EasyRecSoapException sre) {
                exceptionResponse(WS.ACTION_RECOMMENDATIONS_FOR_USER, sre.getMessageObject(), type, callback);
            }
        }

        mon.stop();

        if (WS.JSON_PATH.equals(type)) {
            if (callback != null)
                return Response.ok(new JSONWithPadding(rec, callback), WS.RESPONSE_TYPE_JSCRIPT).build();
            else
                return Response.ok(rec, WS.RESPONSE_TYPE_JSON).build();
        } else
            return Response.ok(rec, WS.RESPONSE_TYPE_XML).build();
    }
    
    @GET
    @Path("/actionhistoryforuser")
    public Response actionHistoryForUser(@PathParam("type") String type, @QueryParam("apikey") String apiKey,
                                           @QueryParam("tenantid") String tenantId, @QueryParam("userid") String userId,
                                           @QueryParam("numberOfResults") Integer numberOfResults,
                                           @QueryParam("requesteditemtype") String requestedItemType,
                                           @QueryParam("callback") String callback,
                                           @QueryParam("actiontype") @DefaultValue(TypeMappingService.ACTION_TYPE_VIEW) String actiontype)
            throws EasyRecException {
        Monitor mon = MonitorFactory.start(JAMON_REST_ACTIONHISTORY);

        Recommendation rec = null;
        Session session = new Session(null, request);

        Integer coreTenantId = operatorDAO.getTenantId(apiKey, tenantId);

        if (coreTenantId == null)
            exceptionResponse(WS.ACTION_HISTORY, MSG.TENANT_WRONG_TENANT_APIKEY, type, callback);

        RemoteTenant remoteTenant = remoteTenantDAO.get(coreTenantId);

        if (remoteTenant.isMaxActionLimitExceeded())
            exceptionResponse(WS.ACTION_HISTORY, MSG.MAXIMUM_ACTIONS_EXCEEDED, type, callback);

        if (Strings.isNullOrEmpty(userId))
            exceptionResponse(WS.ACTION_HISTORY, MSG.USER_NO_ID, type, callback);

        requestedItemType = checkItemType(requestedItemType, type, coreTenantId, tenantId, WS.ACTION_HISTORY, callback, null);


        if (typeMappingService.getIdOfActionType(coreTenantId, actiontype) == null) {
            exceptionResponse(WS.ACTION_HISTORY, MSG.OPERATION_FAILED.append(String.format(" actionType %s not found for tenant %s", actiontype, tenantId)), type, callback);
        }

        if ((numberOfResults == null) || (numberOfResults > WS.DEFAULT_NUMBER_OF_RESULTS))
            numberOfResults = WS.DEFAULT_NUMBER_OF_RESULTS;

        if (rec == null || rec.getRecommendedItems().isEmpty()) {
            try {
                rec = shopRecommenderService.actionHistory(coreTenantId, userId, session, actiontype, requestedItemType, numberOfResults + 5, numberOfResults); // +5 to compensate for inactive items 

            } catch (EasyRecSoapException sre) {
                exceptionResponse(WS.ACTION_HISTORY, sre.getMessageObject(), type, callback);
            }
        }

        mon.stop();

        if (WS.JSON_PATH.equals(type)) {
            if (callback != null)
                return Response.ok(new JSONWithPadding(rec, callback), WS.RESPONSE_TYPE_JSCRIPT).build();
            else
                return Response.ok(rec, WS.RESPONSE_TYPE_JSON).build();
        } else
            return Response.ok(rec, WS.RESPONSE_TYPE_XML).build();
    } 

    @GET
    @Path("/otherusersalsobought")
    public Response otherUsersAlsoBought(@PathParam("type") String type, @QueryParam("apikey") String apiKey,
                                         @QueryParam("tenantid") String tenantId, @QueryParam("userid") String userId,
                                         @QueryParam("sessionid") String sessionId, @QueryParam("itemid") String itemId,
                                         @QueryParam("numberOfResults") Integer numberOfResults,
                                         @QueryParam("itemtype") String itemType,
                                         @QueryParam("requesteditemtype") String requestedItemType,
                                         @QueryParam("callback") String callback)
            throws EasyRecException {
        Monitor mon = MonitorFactory.start(JAMON_REST_ALSO_BOUGHT);
        Recommendation rec = null;
        Integer coreTenantId = operatorDAO.getTenantId(apiKey, tenantId);

        if (coreTenantId == null)
            exceptionResponse(WS.ACTION_OTHER_USERS_ALSO_BOUGHT, MSG.TENANT_WRONG_TENANT_APIKEY, type, callback);

        RemoteTenant r = remoteTenantDAO.get(coreTenantId);

        if (r.isMaxActionLimitExceeded())
            exceptionResponse(WS.ACTION_OTHER_USERS_ALSO_BOUGHT, MSG.MAXIMUM_ACTIONS_EXCEEDED, type, callback);

        itemType = checkItemType(itemType, type, coreTenantId, tenantId, WS.ACTION_OTHER_USERS_ALSO_BOUGHT, callback);
        requestedItemType = checkItemType(requestedItemType, type, coreTenantId, tenantId, WS.ACTION_OTHER_USERS_ALSO_BOUGHT, callback, null);
        Session session = new Session(sessionId, request);

        try {
            if ((numberOfResults == null) || (numberOfResults > WS.DEFAULT_NUMBER_OF_RESULTS))
                numberOfResults = WS.DEFAULT_NUMBER_OF_RESULTS;

            rec = shopRecommenderService.alsoBoughtItems(coreTenantId, userId, itemId, itemType, requestedItemType,
                    session, numberOfResults);
        } catch (EasyRecSoapException sre) {
            exceptionResponse(WS.ACTION_OTHER_USERS_ALSO_BOUGHT, sre.getMessageObject(), type, callback);
        }

        mon.stop();

        if (WS.JSON_PATH.equals(type)) {
            if (callback != null)
                return Response.ok(new JSONWithPadding(rec, callback), WS.RESPONSE_TYPE_JSCRIPT).build();
            else
                return Response.ok(rec, WS.RESPONSE_TYPE_JSON).build();
        } else
            return Response.ok(rec, WS.RESPONSE_TYPE_XML).build();
    }

    @GET
    @Path("/itemsratedgoodbyotherusers")
    public Response itemsRatedGoodByOtherUsers(@PathParam("type") String type, @QueryParam("apikey") String apiKey,
                                               @QueryParam("tenantid") String tenantId,
                                               @QueryParam("userid") String userId,
                                               @QueryParam("sessionid") String sessionId,
                                               @QueryParam("itemid") String itemId,
                                               @QueryParam("numberOfResults") Integer numberOfResults,
                                               @QueryParam("itemtype") String itemType,
                                               @QueryParam("requesteditemtype") String requestedItemType,
                                               @QueryParam("callback") String callback) throws EasyRecException {

        Monitor mon = MonitorFactory.start(JAMON_REST_ALSO_RATED);
        Recommendation rec = null;
        Integer coreTenantId = operatorDAO.getTenantId(apiKey, tenantId);

        if (coreTenantId == null)
            exceptionResponse(WS.ACTION_ITEMS_RATED_GOOD_BY_OTHER_USERS, MSG.TENANT_WRONG_TENANT_APIKEY, type,
                    callback);

        RemoteTenant r = remoteTenantDAO.get(coreTenantId);

        if (r.isMaxActionLimitExceeded())
            exceptionResponse(WS.ACTION_ITEMS_RATED_GOOD_BY_OTHER_USERS, MSG.MAXIMUM_ACTIONS_EXCEEDED, type, callback);

        itemType = checkItemType(itemType, type, coreTenantId, tenantId, WS.ACTION_ITEMS_RATED_GOOD_BY_OTHER_USERS, callback);
        requestedItemType = checkItemType(requestedItemType, type, coreTenantId, tenantId, WS.ACTION_ITEMS_RATED_GOOD_BY_OTHER_USERS, callback, null);
        Session session = new Session(sessionId, request);

        try {
            if ((numberOfResults == null) || (numberOfResults > WS.DEFAULT_NUMBER_OF_RESULTS))
                numberOfResults = WS.DEFAULT_NUMBER_OF_RESULTS;

            rec = shopRecommenderService.alsoGoodRatedItems(coreTenantId, userId, itemId, itemType, requestedItemType,
                    session, numberOfResults);
        } catch (EasyRecSoapException sre) {
            exceptionResponse(WS.ACTION_ITEMS_RATED_GOOD_BY_OTHER_USERS, sre.getMessageObject(),
                    type, callback);
        }

        mon.stop();

        if (WS.JSON_PATH.equals(type)) {
            if (callback != null)
                return Response.ok(new JSONWithPadding(rec, callback), WS.RESPONSE_TYPE_JSCRIPT).build();
            else
                return Response.ok(rec, WS.RESPONSE_TYPE_JSON).build();
        } else
            return Response.ok(rec, WS.RESPONSE_TYPE_XML).build();
    }

    @GET
    @Path("/mostboughtitems")
    public Response mostBoughtItems(@PathParam("type") String type, @QueryParam("apikey") String apiKey,
                                    @QueryParam("tenantid") String tenantId,
                                    @QueryParam("numberOfResults") Integer numberOfResults,
                                    @QueryParam("timeRange") String timeRange,
                                    @QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate,
                                    @QueryParam("requesteditemtype") String requesteditemtype,
                                    @QueryParam("callback") String callback) throws EasyRecException {
        Monitor mon = MonitorFactory.start(JAMON_REST_MOST_BOUGHT);
        Recommendation rr = null;
        Integer coreTenantId = operatorDAO.getTenantId(apiKey, tenantId);

        if (coreTenantId == null)
            exceptionResponse(WS.ACTION_MOST_BOUGHT, MSG.TENANT_WRONG_TENANT_APIKEY, type, callback);

        RemoteTenant r = remoteTenantDAO.get(coreTenantId);

        if (r.isMaxActionLimitExceeded())
            exceptionResponse(WS.ACTION_MOST_BOUGHT, MSG.MAXIMUM_ACTIONS_EXCEEDED, type, callback);

        TimeConstraintVO tc = checkTimeConstraints(startDate, endDate);

        if (tc == null)
            exceptionResponse(WS.ACTION_MOST_BOUGHT, MSG.DATE_PARSE, type, callback);

        requesteditemtype = checkItemType(requesteditemtype, type, coreTenantId, tenantId, WS.ACTION_MOST_BOUGHT, callback, null);
        List<Item> items;

        if (tc != null) {
            items = shopRecommenderService.mostBoughtItems(coreTenantId, requesteditemtype,
                    numberOfResults != null ? numberOfResults : WS.DEFAULT_NUMBER_OF_RESULTS, timeRange, tc,
                    new Session(null, request));

            rr = new Recommendation(tenantId, WS.ACTION_MOST_BOUGHT, null, null, null, items);
        }

        mon.stop();

        if (WS.JSON_PATH.equals(type)) {
            if (callback != null)
                return Response.ok(new JSONWithPadding(rr, callback), WS.RESPONSE_TYPE_JSCRIPT).build();
            else
                return Response.ok(rr, WS.RESPONSE_TYPE_JSON).build();
        } else
            return Response.ok(rr, WS.RESPONSE_TYPE_XML).build();
    }

    @GET
    @Path("/mostvieweditems")
    public Response mostViewedItems(@PathParam("type") String type, @QueryParam("apikey") String apiKey,
                                    @QueryParam("tenantid") String tenantId,
                                    @QueryParam("numberOfResults") Integer numberOfResults,
                                    @QueryParam("timeRange") String timeRange,
                                    @QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate,
                                    @QueryParam("requesteditemtype") String requestedItemType,
                                    @QueryParam("callback") String callback)
            throws EasyRecException {
        Monitor mon = MonitorFactory.start(JAMON_REST_MOST_VIEWED);
        Recommendation rr = null;
        Integer coreTenantId = operatorDAO.getTenantId(apiKey, tenantId);

        if (coreTenantId == null)
            exceptionResponse(WS.ACTION_MOST_VIEWED, MSG.TENANT_WRONG_TENANT_APIKEY, type, callback);

        RemoteTenant r = remoteTenantDAO.get(coreTenantId);

        if (r.isMaxActionLimitExceeded())
            exceptionResponse(WS.ACTION_MOST_VIEWED, MSG.MAXIMUM_ACTIONS_EXCEEDED, type, callback);

        TimeConstraintVO tc = checkTimeConstraints(startDate, endDate);

        if (tc == null)
            exceptionResponse(WS.ACTION_MOST_VIEWED, MSG.DATE_PARSE, type, callback);

        requestedItemType = checkItemType(requestedItemType, type, coreTenantId, tenantId, WS.ACTION_MOST_VIEWED, callback, null);
        List<Item> items;

        if (tc != null) {
            items = shopRecommenderService.mostViewedItems(coreTenantId, requestedItemType,
                    numberOfResults != null ? numberOfResults : WS.DEFAULT_NUMBER_OF_RESULTS, timeRange, tc,
                    new Session(null, request));

            rr = new Recommendation(tenantId, WS.ACTION_MOST_VIEWED, null, null, null, items);
        }

        mon.stop();

        if (WS.JSON_PATH.equals(type)) {
            if (callback != null)
                return Response.ok(new JSONWithPadding(rr, callback), WS.RESPONSE_TYPE_JSCRIPT).build();
            else
                return Response.ok(rr, WS.RESPONSE_TYPE_JSON).build();
        } else
            return Response.ok(rr, WS.RESPONSE_TYPE_XML).build();
    }

    @GET
    @Path("/mostrateditems")
    public Response mostRatedItems(@PathParam("type") String type, @QueryParam("apikey") String apiKey,
                                   @QueryParam("tenantid") String tenantId,
                                   @QueryParam("numberOfResults") Integer numberOfResults,
                                   @QueryParam("timeRange") String timeRange, @QueryParam("startDate") String startDate,
                                   @QueryParam("endDate") String endDate,
                                   @QueryParam("requesteditemtype") String requestedItemType,
                                   @QueryParam("callback") String callback) throws EasyRecException {
        Monitor mon = MonitorFactory.start(JAMON_REST_MOST_RATED);
        Recommendation rr = null;
        Integer coreTenantId = operatorDAO.getTenantId(apiKey, tenantId);

        if (coreTenantId == null)
            exceptionResponse(WS.ACTION_MOST_RATED, MSG.TENANT_WRONG_TENANT_APIKEY, type, callback);

        RemoteTenant r = remoteTenantDAO.get(coreTenantId);

        if (r.isMaxActionLimitExceeded())
            exceptionResponse(WS.ACTION_MOST_RATED, MSG.MAXIMUM_ACTIONS_EXCEEDED, type, callback);

        TimeConstraintVO tc = checkTimeConstraints(startDate, endDate);

        if (tc == null)
            exceptionResponse(WS.ACTION_MOST_RATED, MSG.DATE_PARSE, type, callback);

        requestedItemType = checkItemType(requestedItemType, type, coreTenantId, tenantId, WS.ACTION_MOST_RATED, callback, null);
        List<Item> items;

        if (tc != null) {
            items = shopRecommenderService.mostRatedItems(coreTenantId, requestedItemType,
                    numberOfResults != null ? numberOfResults : WS.DEFAULT_NUMBER_OF_RESULTS,
                    timeRange, tc, new Session(null, request));

            rr = new Recommendation(tenantId, WS.ACTION_MOST_RATED, null, null, null, items);
        }

        mon.stop();

        if (WS.JSON_PATH.equals(type)) {
            if (callback != null)
                return Response.ok(new JSONWithPadding(rr, callback), WS.RESPONSE_TYPE_JSCRIPT).build();
            else
                return Response.ok(rr, WS.RESPONSE_TYPE_JSON).build();
        } else
            return Response.ok(rr, WS.RESPONSE_TYPE_XML).build();
    }


    @GET
    @Path("/itemsofcluster")
    public Response getItemsOfCluster(@PathParam("type") String type, @QueryParam("apikey") String apiKey,
                                      @QueryParam("tenantid") String tenantId,
                                      @QueryParam("clusterid") String clusterId,
                                      @QueryParam("numberOfResults") Integer numberOfResults,
                                      @QueryParam("strategy") String strategy,
                                      @QueryParam("usefallback") @DefaultValue("false") Boolean useFallback,
                                      @QueryParam("requesteditemtype") String requestedItemType,
                                      @QueryParam("callback") String callback) {
        Monitor monitor = MonitorFactory.start(JAMON_REST_MOST_RATED);
        Recommendation recommendation = null;
        Integer coreTenantId = operatorDAO.getTenantId(apiKey, tenantId);

        if (coreTenantId == null)
            exceptionResponse(WS.ACTION_ITEMS_OF_CLUSTER, MSG.TENANT_WRONG_TENANT_APIKEY, type, callback);

        RemoteTenant remoteTenant = remoteTenantDAO.get(coreTenantId);

        if (remoteTenant.isMaxActionLimitExceeded())
            exceptionResponse(WS.ACTION_ITEMS_OF_CLUSTER, MSG.MAXIMUM_ACTIONS_EXCEEDED, type, callback);

        if (clusterId == null)
            exceptionResponse(WS.ACTION_ITEMS_OF_CLUSTER, MSG.CLUSTER_NO_ID, type, callback);

        requestedItemType = checkItemType(requestedItemType, type, coreTenantId, tenantId, WS.ACTION_ITEMS_OF_CLUSTER, callback, null);
        List<Item> items;

        if (clusterId != null)
            try {
                Integer coreItemType = typeMappingService.getIdOfItemType(coreTenantId, requestedItemType);

                items = shopRecommenderService.itemsOfCluster(coreTenantId, clusterId,
                        numberOfResults != null ? numberOfResults : WS.DEFAULT_NUMBER_OF_RESULTS, strategy, useFallback,
                        coreItemType, new Session(null, request));

                recommendation = new Recommendation(tenantId, WS.ACTION_ITEMS_OF_CLUSTER, null, null, null, items);
            } catch (EasyRecSoapException sre) {
                exceptionResponse(WS.ACTION_ITEMS_OF_CLUSTER, sre.getMessageObject(), type,
                        callback);
            }

        monitor.stop();

        if (WS.JSON_PATH.equals(type)) {
            if (callback != null)
                return Response.ok(new JSONWithPadding(recommendation, callback), WS.RESPONSE_TYPE_JSCRIPT).build();
            else
                return Response.ok(recommendation, WS.RESPONSE_TYPE_JSON).build();
        } else
            return Response.ok(recommendation, WS.RESPONSE_TYPE_XML).build();
    }

    @GET
    @Path("/bestrateditems")
    public Response bestRatedItems(@PathParam("type") String type, @QueryParam("apikey") String apiKey,
                                   @QueryParam("tenantid") String tenantId, @QueryParam("userid") String userId,
                                   @QueryParam("numberOfResults") Integer numberOfResults,
                                   @QueryParam("timeRange") String timeRange, @QueryParam("startDate") String startDate,
                                   @QueryParam("endDate") String endDate,
                                   @QueryParam("requesteditemtype") String requestedItemType,
                                   @QueryParam("callback") String callback)
            throws EasyRecException {
        Monitor mon = MonitorFactory.start(JAMON_REST_BEST_RATED);
        Recommendation rr = null;
        Integer coreTenantId = operatorDAO.getTenantId(apiKey, tenantId);

        if (coreTenantId == null)
            exceptionResponse(WS.ACTION_BEST_RATED, MSG.TENANT_WRONG_TENANT_APIKEY, type, callback);

        RemoteTenant r = remoteTenantDAO.get(coreTenantId);

        if (r.isMaxActionLimitExceeded())
            exceptionResponse(WS.ACTION_BEST_RATED, MSG.MAXIMUM_ACTIONS_EXCEEDED, type, callback);

        TimeConstraintVO tc = checkTimeConstraints(startDate, endDate);

        if (tc == null)
            exceptionResponse(WS.ACTION_BEST_RATED, MSG.DATE_PARSE, type, callback);

        requestedItemType = checkItemType(requestedItemType, type, coreTenantId, tenantId, WS.ACTION_BEST_RATED, callback, null);
        List<Item> items;

        if (tc != null) {
            items = shopRecommenderService.bestRatedItems(coreTenantId, userId, requestedItemType,
                    numberOfResults != null ? numberOfResults : WS.DEFAULT_NUMBER_OF_RESULTS, timeRange, tc,
                    new Session(null, request));

            rr = new Recommendation(tenantId, WS.ACTION_BEST_RATED, null, null, null, items);
        }

        mon.stop();

        if (WS.JSON_PATH.equals(type)) {
            if (callback != null)
                return Response.ok(new JSONWithPadding(rr, callback), WS.RESPONSE_TYPE_JSCRIPT).build();
            else
                return Response.ok(rr, WS.RESPONSE_TYPE_JSON).build();
        } else
            return Response.ok(rr, WS.RESPONSE_TYPE_XML).build();
    }

    @GET
    @Path("/worstrateditems")
    public Response worstRatedItems(@PathParam("type") String type, @QueryParam("apikey") String apiKey,
                                    @QueryParam("tenantid") String tenantId, @QueryParam("userid") String userId,
                                    @QueryParam("numberOfResults") Integer numberOfResults,
                                    @QueryParam("timeRange") String timeRange,
                                    @QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate,
                                    @QueryParam("requesteditemtype") String requestedItemType,
                                    @QueryParam("callback") String callback)
            throws EasyRecException {
        Monitor mon = MonitorFactory.start(JAMON_REST_WORST_RATED);
        Recommendation rr = null;
        Integer coreTenantId = operatorDAO.getTenantId(apiKey, tenantId);

        if (coreTenantId == null)
            exceptionResponse(WS.ACTION_WORST_RATED, MSG.TENANT_WRONG_TENANT_APIKEY, type, callback);

        RemoteTenant r = remoteTenantDAO.get(coreTenantId);

        if (r.isMaxActionLimitExceeded())
            exceptionResponse(WS.ACTION_WORST_RATED, MSG.MAXIMUM_ACTIONS_EXCEEDED, type, callback);

        TimeConstraintVO tc = checkTimeConstraints(startDate, endDate);

        if (tc == null)
            exceptionResponse(WS.ACTION_WORST_RATED, MSG.DATE_PARSE, type, callback);

        requestedItemType = checkItemType(requestedItemType, type, coreTenantId, tenantId, WS.ACTION_WORST_RATED, null, callback);
        List<Item> items;

        if (tc != null) {
            items = shopRecommenderService.worstRatedItems(coreTenantId, userId, requestedItemType,
                    numberOfResults != null ? numberOfResults : WS.DEFAULT_NUMBER_OF_RESULTS, timeRange, tc,
                    new Session(null, request));

            rr = new Recommendation(tenantId, WS.ACTION_WORST_RATED, null, null, null, items);
        }

        mon.stop();

        if (WS.JSON_PATH.equals(type)) {
            if (callback != null)
                return Response.ok(new JSONWithPadding(rr, callback), WS.RESPONSE_TYPE_JSCRIPT).build();
            else
                return Response.ok(rr, WS.RESPONSE_TYPE_JSON).build();
        } else
            return Response.ok(rr, WS.RESPONSE_TYPE_XML).build();
    }

    @GET
    @Path("/relateditems")
    public Response relatedItems(@PathParam("type") String type, @QueryParam("apikey") String apiKey,
                                 @QueryParam("tenantid") String tenantId, 
                                 @QueryParam("assoctype") String assocType, @QueryParam("userid") String userId,
                                 @QueryParam("sessionid") String sessionId, @QueryParam("itemid") String itemId,
                                 @QueryParam("numberOfResults") Integer numberOfResults,
                                 @QueryParam("itemtype") String itemType,
                                 @QueryParam("requesteditemtype") String requestedItemType,
                                 @QueryParam("callback") String callback)
            throws EasyRecException {

        Monitor mon = MonitorFactory.start(JAMON_REST_RELATED_ITEMS);
        Recommendation rec = null;
        Integer coreTenantId = operatorDAO.getTenantId(apiKey, tenantId);
        Integer assocTypeId = null;

        if (coreTenantId == null)
            exceptionResponse(WS.ACTION_RELATED_ITEMS, MSG.TENANT_WRONG_TENANT_APIKEY, type, callback);

        RemoteTenant r = remoteTenantDAO.get(coreTenantId);

        if (r.isMaxActionLimitExceeded())
            exceptionResponse(WS.ACTION_RELATED_ITEMS, MSG.MAXIMUM_ACTIONS_EXCEEDED, type, callback);

        if (itemId == null)
            exceptionResponse(WS.ACTION_RELATED_ITEMS, MSG.ITEM_NO_ID, type, callback);
        
        if (assocType != null) {
            assocTypeId = typeMappingService.getIdOfAssocType(coreTenantId, assocType, Boolean.TRUE); // only visible assocTypes can be queried
            if (assocTypeId == null) exceptionResponse(WS.ACTION_RELATED_ITEMS, MSG.ASSOC_TYPE_DOES_NOT_EXIST, type, callback);
        } else {
            assocType = AssocTypeDAO.ASSOCTYPE_IS_RELATED;
        }

        itemType = checkItemType(itemType, type, coreTenantId, tenantId, WS.ACTION_RELATED_ITEMS, callback);
        requestedItemType = checkItemType(requestedItemType, type, coreTenantId, tenantId, WS.ACTION_RELATED_ITEMS, callback, null);
        Session session = new Session(null, request);

        try {
            if ((numberOfResults == null) || (numberOfResults > WS.DEFAULT_NUMBER_OF_RESULTS))
                numberOfResults = WS.DEFAULT_NUMBER_OF_RESULTS;

            rec = shopRecommenderService.relatedItems(coreTenantId, assocType, userId, itemId, itemType, requestedItemType, session,
                    numberOfResults);
        } catch (EasyRecSoapException e) {
            exceptionResponse(WS.ACTION_RELATED_ITEMS, e.getMessageObject(), type,
                    callback);
        }

        mon.stop();

        if (WS.JSON_PATH.equals(type)) {
            if (callback != null)
                return Response.ok(new JSONWithPadding(rec, callback), WS.RESPONSE_TYPE_JSCRIPT).build();
            else
                return Response.ok(rec, WS.RESPONSE_TYPE_JSON).build();
        } else
            return Response.ok(rec, WS.RESPONSE_TYPE_XML).build();
    }

    @GET
    @Path("/setitemactive")
    public Response setItemActive(@PathParam("type") String type, @QueryParam("apikey") String apiKey,
                                  @QueryParam("tenantid") String tenantId, @QueryParam("itemid") String itemId,
                                  @QueryParam("active") Boolean active, @QueryParam("itemtype") String itemType,
                                  @QueryParam("callback") String callback) throws EasyRecException {
        Monitor mon = MonitorFactory.start(JAMON_REST_ITEM_ACTIVE);
        Integer coreTenantId = operatorDAO.getTenantId(apiKey, tenantId);

        if (coreTenantId == null)
            exceptionResponse(WS.ACTION_SET_ITEM_ACTIVE, MSG.TENANT_WRONG_TENANT_APIKEY, type, callback);

        if (itemId == null)
            exceptionResponse(WS.ACTION_SET_ITEM_ACTIVE, MSG.ITEM_NO_ID, type, callback);

        if (active == null)
            exceptionResponse(WS.ACTION_SET_ITEM_ACTIVE, MSG.ITEM_NO_ACTIVE, type, callback);

        itemType = checkItemType(itemType, type, coreTenantId, tenantId, WS.ACTION_SET_ITEM_ACTIVE, callback);
        RemoteTenant r = remoteTenantDAO.get(coreTenantId);
        Item item = itemDAO.get(r, itemId, itemType);

        if (item == null)
            exceptionResponse(WS.ACTION_SET_ITEM_ACTIVE, MSG.ITEM_NOT_EXISTS, type, callback);
        else {
            if (item.isActive() && !active)
                itemDAO.deactivate(coreTenantId, itemId, itemType);

            if (!item.isActive() && active)
                itemDAO.activate(coreTenantId, itemId, itemType);
        }

        ResponseItem respItem = new ResponseItem(tenantId, WS.ACTION_SET_ITEM_ACTIVE + active, null, null, null, item);

        mon.stop();

        if (WS.JSON_PATH.equals(type)) {
            if (callback != null)
                return Response.ok(new JSONWithPadding(respItem, callback), WS.RESPONSE_TYPE_JSCRIPT).build();
            else
                return Response.ok(respItem, WS.RESPONSE_TYPE_JSON).build();
        } else
            return Response.ok(respItem, WS.RESPONSE_TYPE_XML).build();
    }

    @GET
    @Path("/itemtypes")
    public Response itemTypes(@PathParam("type") String type, @QueryParam("apikey") String apiKey,
                              @QueryParam("tenantid") String tenantId, @QueryParam("callback") String callback)
            throws EasyRecException {

        Monitor mon = MonitorFactory.start(JAMON_REST_ITEMTYPES);

        Integer coreTenantId = operatorDAO.getTenantId(apiKey, tenantId);

        if (coreTenantId == null)
            exceptionResponse(WS.ACTION_ITEMTYPES, MSG.TENANT_WRONG_TENANT_APIKEY, type, callback);

        ResponseItemTypes responseItemTypes = null;

        try {
            Set<String> itemTypes = shopRecommenderService.getItemTypes(coreTenantId);
            responseItemTypes = new ResponseItemTypes(tenantId, itemTypes);
        } catch (EasyRecSoapException e) {
            exceptionResponse(WS.ACTION_ITEMTYPES, e.getMessageObject().append("Failed to retrieve item types"), type,
                    callback);
        }

        mon.stop();

        if (WS.JSON_PATH.equals(type)) {
            if (callback != null) {
                return Response.ok(new JSONWithPadding(responseItemTypes, callback), WS.RESPONSE_TYPE_JSCRIPT).build();
            } else {
                return Response.ok(responseItemTypes, WS.RESPONSE_TYPE_JSON).build();
            }
        } else {
            return Response.ok(responseItemTypes, WS.RESPONSE_TYPE_XML).build();
        }
    }

    @GET
    @Path("/clusters")
    public Response clusters(@PathParam("type") String type, @QueryParam("apikey") String apiKey,
                             @QueryParam("tenantid") String tenantId, @QueryParam("callback") String callback)
            throws EasyRecException {

        Monitor mon = MonitorFactory.start(JAMON_REST_CLUSTERS);

        Integer coreTenantId = operatorDAO.getTenantId(apiKey, tenantId);

        if (coreTenantId == null)
            exceptionResponse(WS.ACTION_CLUSTERS, MSG.TENANT_WRONG_TENANT_APIKEY, type, callback);

        ResponseClusters responseClusters = null;

        try {
            List<ClusterVO> clusters = shopRecommenderService.getClusters(coreTenantId);
            responseClusters = new ResponseClusters(tenantId, clusters);
        } catch (EasyRecSoapException e) {
            exceptionResponse(WS.ACTION_CLUSTERS, e.getMessageObject().append("Failed to retrieve clusters"), type,
                    callback);
        }

        mon.stop();

        if (WS.JSON_PATH.equals(type)) {
            if (callback != null) {
                return Response.ok(new JSONWithPadding(responseClusters, callback), WS.RESPONSE_TYPE_JSCRIPT).build();
            } else {
                return Response.ok(responseClusters, WS.RESPONSE_TYPE_JSON).build();
            }
        } else {
            return Response.ok(responseClusters, WS.RESPONSE_TYPE_XML).build();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // import API
    ///////////////////////////////////////////////////////////////////////////
    @GET
    @Path("/importrule")
    public Response importrule(@PathParam("type") String type, @QueryParam("token") String token,
                               @QueryParam("tenantid") String tenantId, @QueryParam("itemfromid") String itemFromId,
                               @QueryParam("itemfromtype") String itemFromType, @QueryParam("itemtoid") String itemToId,
                               @QueryParam("itemtotype") String itemToType, @QueryParam("assocvalue") String assocvalue,
                               @QueryParam("assoctype") String assocType, @QueryParam("callback") String callback)
            throws EasyRecException {
        Monitor mon = MonitorFactory.start(JAMON_REST_IMPORT_RULE);
        Item itemFrom = null;
        Item itemTo = null;
        Float assocValue = null;
        Integer assocTypeId = null;
        Integer coreTenantId = null;

        // Collect a List of messages for the user to understand,
        // what went wrong (e.g. Wrong API key).
        List<Message> messages = Lists.newArrayList();
        Operator o = operatorDAO.getOperatorFromToken(token);

        if (itemFromType != null) itemFromType = CharMatcher.WHITESPACE.trimFrom(itemFromType);
        if (itemToType != null) itemToType = CharMatcher.WHITESPACE.trimFrom(itemToType);

        if (Strings.isNullOrEmpty(itemFromType)) itemFromType = Item.DEFAULT_STRING_ITEM_TYPE;
        if (Strings.isNullOrEmpty(itemToType)) itemToType = Item.DEFAULT_STRING_ITEM_TYPE;

        // TODO create itemtypes if they don't exist?

        if (o != null) {
            coreTenantId = operatorDAO.getTenantId(o.getApiKey(), tenantId);

            if (itemFromId != null && itemFromId.equals(itemToId))
                messages.add(MSG.ITEMFROM_EQUAL_ITEMTO);

            RemoteTenant r = remoteTenantDAO.get(coreTenantId);

            if (r != null) {
                itemFrom = itemDAO.get(r, itemFromId, itemFromType);

                if (itemFrom == null)
                    messages.add(MSG.ITEM_FROM_ID_DOES_NOT_EXIST);

                itemTo = itemDAO.get(r, itemToId, itemToType);

                if (itemTo == null)
                    messages.add(MSG.ITEM_TO_ID_DOES_NOT_EXIST);

                try {
                    assocTypeId = typeMappingService.getIdOfAssocType(coreTenantId, assocType);
                } catch (Exception e) {
                    messages.add(MSG.ASSOC_TYPE_DOES_NOT_EXIST);
                }

            } else
                messages.add(MSG.TENANT_WRONG_TENANT);

            try {
                assocValue = Float.parseFloat(assocvalue);

                if (assocValue < 0 || assocValue > 100)
                    messages.add(MSG.INVALID_ASSOC_VALUE);
            } catch (Exception e) {
                messages.add(MSG.INVALID_ASSOC_VALUE);
            }

        } else
            messages.add(MSG.WRONG_TOKEN);

        if (messages.size() > 0) {
            if ((WS.JSON_PATH.equals(type)))
                throw new EasyRecException(messages, WS.ACTION_IMPORT_RULE, WS.RESPONSE_TYPE_JSON, callback);
            else
                throw new EasyRecException(messages, WS.ACTION_IMPORT_RULE);
        }

        remoteAssocService.addRule(coreTenantId, itemFromId, itemFromType, itemToId, itemToType, assocTypeId,
                assocValue);

        ResponseRule respRule =
                new ResponseRule(tenantId, WS.ACTION_IMPORT_RULE, itemFrom.getItemId(), itemTo.getItemId(), assocType,
                        Float.toString(assocValue));
        mon.stop();

        if (WS.JSON_PATH.equals(type)) {
            if (callback != null)
                return Response.ok(new JSONWithPadding(respRule, callback), WS.RESPONSE_TYPE_JSCRIPT).build();
            else
                return Response.ok(respRule, WS.RESPONSE_TYPE_JSON).build();
        } else
            return Response.ok(respRule, WS.RESPONSE_TYPE_XML).build();
    }

    @GET
    @Path("/importitem")
    public Response importitem(@PathParam("type") String type, @QueryParam("token") String token,
                               @QueryParam("tenantid") String tenantId, @QueryParam("itemid") String itemId,
                               @QueryParam("itemdescription") String itemDescription,
                               @QueryParam("itemurl") String itemUrl, @QueryParam("itemimageurl") String itemImageUrl,
                               @QueryParam("itemtype") String itemType,
                               @QueryParam("callback") String callback) throws EasyRecException {
        Monitor mon = MonitorFactory.start(JAMON_REST_IMPORT_ITEM);

        // Collect a List of messages for the user to understand,
        // what went wrong (e.g. Wrong API key).
        List<Message> messages = new ArrayList<Message>();
        Integer coreTenantId = null;

        Operator o = operatorDAO.getOperatorFromToken(token);
        if (o != null) {
            coreTenantId = operatorDAO.getTenantId(o.getApiKey(), tenantId);
            checkParameters(coreTenantId, itemId, itemDescription, itemUrl, messages);
        } else
            messages.add(MSG.WRONG_TOKEN);

        if (messages.size() > 0) {
            if ((WS.JSON_PATH.equals(type)))
                throw new EasyRecException(messages, WS.ACTION_IMPORT_ITEM, WS.RESPONSE_TYPE_JSON, callback);
            else
                throw new EasyRecException(messages, WS.ACTION_IMPORT_ITEM);
        }

        itemType = checkItemType(itemType, type, coreTenantId, tenantId, WS.ACTION_IMPORT_ITEM, callback);

        itemDAO.insertOrUpdate(coreTenantId, itemId, itemType, itemDescription, itemUrl, itemImageUrl);

        ResponseItem respItem = new ResponseItem(tenantId, WS.ACTION_IMPORT_ITEM, null, null, null,
                itemDAO.get(remoteTenantDAO.get(coreTenantId), itemId, itemType));

        mon.stop();

        if (WS.JSON_PATH.equals(type)) {
            if (callback != null)
                return Response.ok(new JSONWithPadding(respItem, callback), WS.RESPONSE_TYPE_JSCRIPT)
                        .build();
            else
                return Response.ok(respItem, WS.RESPONSE_TYPE_JSON).build();
        } else
            return Response.ok(respItem, WS.RESPONSE_TYPE_XML).build();
    }

    // private methods

    private void exceptionResponse(String operation, Message message, String type, String callback)
            throws EasyRecException {
        List<Message> messages = new ArrayList<Message>();
        messages.add(message);

        if ((WS.JSON_PATH.equals(type)))
            throw new EasyRecException(messages, operation, WS.RESPONSE_TYPE_JSON, callback);
        else
            throw new EasyRecException(messages, operation);
    }

    private void checkParameters(Integer coreTenantId, String itemId, String itemDescription, String itemUrl,
                                 List<Message> messages) throws EasyRecException {
        if (coreTenantId == null)
            messages.add(MSG.TENANT_WRONG_TENANT_APIKEY);

        if (Strings.isNullOrEmpty(itemId))
            messages.add(MSG.ITEM_NO_ID);

        if (Strings.isNullOrEmpty(itemDescription)) {
            messages.add(MSG.ITEM_NO_DESCRIPTION);
        } else {
            if (itemDescription.length() > 500) {
                itemDescription = itemDescription.substring(0, 499);
            }
        }

        if (itemUrl == null)
            messages.add(MSG.ITEM_NO_URL);
    }

    private void checkParams(Integer coreTenantId, String itemId, String itemDescription, String itemUrl,
                             String sessionId, List<Message> messages) throws EasyRecException {
        checkParameters(coreTenantId, itemId, itemDescription, itemUrl, messages);

        if (Strings.isNullOrEmpty(sessionId))
            messages.add(MSG.USER_NO_SESSION_ID);
    }

    private String checkItemType(String itemType, String type, Integer coreTenantId, String tenantId, String operation,
                                 String callback) {
        return checkItemType(itemType, type, coreTenantId, tenantId, operation, callback, Item.DEFAULT_STRING_ITEM_TYPE);
    }

    private String checkItemType(String itemType, String type, Integer coreTenantId, String tenantId, String operation,
                                 String callback, @Nullable String defaultValue) {
        if (itemType != null)
            itemType = CharMatcher.WHITESPACE.trimFrom(itemType);

        if (Strings.isNullOrEmpty(itemType))
            return defaultValue;
        else
            try {
                typeMappingService.getIdOfItemType(coreTenantId, itemType, true);

                return itemType;
            } catch (IllegalArgumentException ex) {
                exceptionResponse(operation, MSG.OPERATION_FAILED.append(
                        String.format(" itemType %s not found for tenant %s", itemType, tenantId)), type, callback);

                return null;
            }
    }

    private TimeConstraintVO checkTimeConstraints(String startTime, String endTime) {
        Date startDate = null;
        Date endDate;

        if (startTime != null) {
            startDate = MyUtils.dateFormatCheck(startTime, dateFormatter);

            if (startDate == null)
                return null;
        }

        if (endTime == null)
            endDate = new Date(System.currentTimeMillis());
        else {
            endDate = MyUtils.dateFormatCheck(endTime, dateFormatter);

            if (endDate == null)
                return null;
        }

        return new TimeConstraintVO(startDate, endDate);
    }
}
