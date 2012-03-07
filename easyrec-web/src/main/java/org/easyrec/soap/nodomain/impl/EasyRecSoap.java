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
package org.easyrec.soap.nodomain.impl;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyrec.model.core.ClusterVO;
import org.easyrec.model.core.transfer.TimeConstraintVO;
import org.easyrec.model.web.*;
import org.easyrec.rest.*;
import org.easyrec.service.core.TenantService;
import org.easyrec.service.domain.TypeMappingService;
import org.easyrec.service.web.RemoteAssocService;
import org.easyrec.service.web.nodomain.ShopRecommenderService;
import org.easyrec.soap.nodomain.exception.EasyRecSoapException;
import org.easyrec.soap.service.AuthenticationDispatcher;
import org.easyrec.store.dao.web.ItemDAO;
import org.easyrec.store.dao.web.OperatorDAO;
import org.easyrec.store.dao.web.RemoteTenantDAO;
import org.easyrec.utils.MyUtils;
import org.easyrec.vocabulary.MSG;
import org.easyrec.vocabulary.WS;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Recommender Webservice implementation
 * <p/>
 * Note: Since web services and aspects do not work properly on the same class, a wrapper for the functionality of this class is introduced.<br/>
 * Attention: Do NOT put any functionality in this class. All functionality shall be located in the wrapper class.
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
 * @author Roman Cerny
 */
@SuppressWarnings({"UnusedDeclaration"})
@WebService
public class EasyRecSoap {


    @Resource
    public WebServiceContext wsContext;

    private final Log logger = LogFactory.getLog(this.getClass());

    private ShopRecommenderService shopRecommenderService;
    private OperatorDAO operatorDAO;
    private RemoteTenantDAO remoteTenantDAO;
    private TenantService tenantService;
    private SimpleDateFormat dateFormatter;
    private TypeMappingService typeMappingService;
    private ItemDAO itemDAO;
    private RemoteAssocService remoteAssocService;

    private AuthenticationDispatcher authenticationDispatcher;
    private String serviceName = this.getClass().getSimpleName();

    // default constructor for WS
    public EasyRecSoap() {
        if (logger.isDebugEnabled()) {
            logger.debug("called constructor EasyRecSoap()");
        }
    }

    public EasyRecSoap(OperatorDAO operatorDAO, RemoteTenantDAO remoteTenantDAO,
                       ShopRecommenderService shopRecommenderService, TenantService tenantService,
                       TypeMappingService typeMappingService, ItemDAO itemDAO, RemoteAssocService remoteAssocService,
                       String dateFormatString) {

        if (logger.isDebugEnabled()) {
            logger.debug("called constructor EasyRecSoap(shopRecommenderService, operatorDAO)");
        }
        this.shopRecommenderService = shopRecommenderService;
        this.operatorDAO = operatorDAO;
        this.remoteTenantDAO = remoteTenantDAO;
        this.tenantService = tenantService;
        this.typeMappingService = typeMappingService;
        this.itemDAO = itemDAO;
        this.remoteAssocService = remoteAssocService;
        this.dateFormatter = new SimpleDateFormat(dateFormatString);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // interface "ShopRecommenderWS" implementation

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Actions
    ///////////////////////////////////////////////////////////////////////////////////////////////
    @WebMethod
    public ResponseItem view(@WebParam(name = "apikey") String apiKey, @WebParam(name = "tenantid") String tenantId,
                             @WebParam(name = "userid") String userId, @WebParam(name = "sessionid") String sessionId,
                             @WebParam(name = "itemid") String itemId,
                             @WebParam(name = "itemdescription") String itemDescription,
                             @WebParam(name = "itemurl") String itemUrl,
                             @WebParam(name = "itemimageurl") String itemImageUrl,
                             @WebParam(name = "actionTime") String actionTime,
                             @WebParam(name = "itemtype") String itemType) throws EasyRecSoapException {

        // Collect a List of messages for the user to understand,
        // what went wrong (e.g. Wrong API key).
        List<Message> messages = new ArrayList<Message>();

        Integer coreTenantId = operatorDAO.getTenantId(apiKey, tenantId);

        checkParams(coreTenantId, itemId, itemDescription, itemUrl, sessionId, messages);

        Date actionDate = null;

        if (actionTime != null) {
            actionDate = MyUtils.dateFormatCheck(actionTime, dateFormatter);
            if (actionDate == null) {
                messages.add(MSG.DATE_PARSE);
            }
        }

        if (messages.size() > 0) {
            throw new EasyRecSoapException(messages, WS.ACTION_VIEW);
        }

        itemType = checkItemType(itemType, coreTenantId, tenantId, WS.ACTION_VIEW);
        Session session = new Session(sessionId,
                ((HttpServletRequest) wsContext.getMessageContext().get(MessageContext.SERVLET_REQUEST))
                        .getRemoteAddr());

        Item item = shopRecommenderService
                .viewItem(remoteTenantDAO.get(coreTenantId), userId, itemId, itemType,
                        itemDescription, itemUrl, itemImageUrl, actionDate, session);

        return new ResponseItem(tenantId, WS.ACTION_VIEW, userId, sessionId, null, item);

    }

    public ResponseItem rate(@WebParam(name = "apikey") String apiKey, @WebParam(name = "tenantid") String tenantId,
                             @WebParam(name = "userid") String userId, @WebParam(name = "sessionid") String sessionId,
                             @WebParam(name = "itemid") String itemId,
                             @WebParam(name = "ratingvalue") String ratingValue,
                             @WebParam(name = "itemdescription") String itemDescription,
                             @WebParam(name = "itemurl") String itemUrl,
                             @WebParam(name = "itemimageurl") String itemImageUrl,
                             @WebParam(name = "actionTime") String actionTime,
                             @WebParam(name = "itemtype") String itemType) throws EasyRecSoapException {
        // Collect a List of messages for the user to understand,
        // what went wrong (e.g. Wrong API key).
        List<Message> messages = new ArrayList<Message>();

        Integer coreTenantId = operatorDAO.getTenantId(apiKey, tenantId);

        checkParams(coreTenantId, itemId, itemDescription, itemUrl, sessionId, messages);

        Date actionDate = null;

        if (actionTime != null) {
            actionDate = MyUtils.dateFormatCheck(actionTime, dateFormatter);
            if (actionDate == null) {
                messages.add(MSG.DATE_PARSE);
            }
        }

        Integer rateValue = -1;
        try {
            rateValue = Integer.valueOf(ratingValue);

            if (rateValue < tenantService.getTenantById(coreTenantId).getRatingRangeMin() ||
                    rateValue > tenantService.getTenantById(coreTenantId).getRatingRangeMax()) {
                throw new Exception();
            }

        } catch (Exception e) {
            messages.add(MSG.ITEM_INVALID_RATING_VALUE);
        }

        if (messages.size() > 0) {

            throw new EasyRecSoapException(messages, WS.ACTION_RATE);
        }

        itemType = checkItemType(itemType, coreTenantId, tenantId, WS.ACTION_RATE);
        Session session = new Session(sessionId,
                ((HttpServletRequest) wsContext.getMessageContext().get(MessageContext.SERVLET_REQUEST))
                        .getRemoteAddr());

        Item item = shopRecommenderService
                .rateItem(remoteTenantDAO.get(coreTenantId), userId, itemId, itemType,
                        itemDescription, itemUrl, itemImageUrl, rateValue, actionDate, session);


        return new ResponseItem(tenantId, WS.ACTION_RATE, userId, sessionId, ratingValue, item);
    }

    @WebMethod
    public ResponseItem buy(@WebParam(name = "apikey") String apiKey, @WebParam(name = "tenantid") String tenantId,
                            @WebParam(name = "userid") String userId, @WebParam(name = "sessionid") String sessionId,
                            @WebParam(name = "itemid") String itemId,
                            @WebParam(name = "itemdescription") String itemDescription,
                            @WebParam(name = "itemurl") String itemUrl,
                            @WebParam(name = "itemimageurl") String itemImageUrl,
                            @WebParam(name = "actionTime") String actionTime,
                            @WebParam(name = "itemtype") String itemType) throws EasyRecSoapException {
        // Collect a List of messages for the user to understand,
        // what went wrong (e.g. Wrong API key).
        List<Message> messages = new ArrayList<Message>();

        Integer coreTenantId = operatorDAO.getTenantId(apiKey, tenantId);

        checkParams(coreTenantId, itemId, itemDescription, itemUrl, sessionId, messages);

        Date actionDate = null;

        if (actionTime != null) {
            actionDate = MyUtils.dateFormatCheck(actionTime, dateFormatter);
            if (actionDate == null) {
                messages.add(MSG.DATE_PARSE);
            }
        }

        if (messages.size() > 0) {

            throw new EasyRecSoapException(messages, WS.ACTION_BUY);
        }

        itemType = checkItemType(itemType, coreTenantId, tenantId, WS.ACTION_BUY);
        Session session = new Session(sessionId,
                ((HttpServletRequest) wsContext.getMessageContext().get(MessageContext.SERVLET_REQUEST))
                        .getRemoteAddr());

        Item item = shopRecommenderService
                .purchaseItem(remoteTenantDAO.get(coreTenantId), userId, itemId, itemType,
                        itemDescription, itemUrl, itemImageUrl, actionDate, session);

        return new ResponseItem(tenantId, WS.ACTION_BUY, userId, sessionId, null, item);
    }

    @WebMethod
    public ResponseItem sendAction(@WebParam(name = "apikey") String apiKey, @WebParam(name = "tenantid") String tenantId,
                            @WebParam(name = "userid") String userId, @WebParam(name = "sessionid") String sessionId,
                            @WebParam(name = "itemid") String itemId,
                            @WebParam(name = "actiontype") String actionType, 
                            @WebParam(name = "actionvalue") String actionValue,
                            @WebParam(name = "itemdescription") String itemDescription,
                            @WebParam(name = "itemurl") String itemUrl,
                            @WebParam(name = "itemimageurl") String itemImageUrl,
                            @WebParam(name = "actionTime") String actionTime,
                            @WebParam(name = "itemtype") String itemType) throws EasyRecSoapException {
        // Collect a List of messages for the user to understand,
        // what went wrong (e.g. Wrong API key).
        List<Message> messages = new ArrayList<Message>();

        Integer coreTenantId = operatorDAO.getTenantId(apiKey, tenantId);

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
            if (actionDate == null) {
                messages.add(MSG.DATE_PARSE);
            }
        }

        if (messages.size() > 0) {

            throw new EasyRecSoapException(messages, WS.ACTION_SENDACTION);
        }

        itemType = checkItemType(itemType, coreTenantId, tenantId, WS.ACTION_SENDACTION);
        Session session = new Session(sessionId,
                ((HttpServletRequest) wsContext.getMessageContext().get(MessageContext.SERVLET_REQUEST))
                        .getRemoteAddr());

        Item item = shopRecommenderService
                .sendAction(remoteTenantDAO.get(coreTenantId), userId, itemId, itemType,
                        itemDescription, itemUrl, itemImageUrl, actionType, actValue, actionDate, session);

        return new ResponseItem(tenantId, WS.ACTION_SENDACTION, userId, sessionId, null, item);
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Recommendations
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @WebMethod
    public Recommendation otherUsersAlsoViewed(@WebParam(name = "apikey") String apiKey,
                                               @WebParam(name = "tenantid") String tenantId,
                                               @WebParam(name = "userid") String userId,
                                               @WebParam(name = "sessionid") String sessionId,
                                               @WebParam(name = "itemid") String itemId,
                                               @WebParam(name = "numberOfResults") Integer numberOfResults,
                                               @WebParam(name = "itemtype") String itemType,
                                               @WebParam(name = "requesteditemtype") String requestedItemType)
            throws EasyRecSoapException {
        List<Message> messages = new ArrayList<Message>();
        Recommendation rec = null;

        Integer coreTenantId = operatorDAO.getTenantId(apiKey, tenantId);

        if (coreTenantId != null) {

        itemType = checkItemType(itemType, coreTenantId, tenantId, WS.ACTION_OTHER_USERS_ALSO_VIEWED);
        requestedItemType = checkItemType(requestedItemType, coreTenantId, tenantId, WS.ACTION_OTHER_USERS_ALSO_VIEWED, null);

            Session session = new Session(sessionId,
                    ((HttpServletRequest) wsContext.getMessageContext().get(MessageContext.SERVLET_REQUEST)));

            try {
                if ((numberOfResults == null) || (numberOfResults > WS.DEFAULT_NUMBER_OF_RESULTS)) {
                    numberOfResults = WS.DEFAULT_NUMBER_OF_RESULTS;
                }

                rec = shopRecommenderService
                        .alsoViewedItems(coreTenantId, userId, itemId, itemType,
                                requestedItemType, session, numberOfResults);
            } catch (EasyRecSoapException sre) {
                messages.add(MSG.ITEM_NOT_EXISTS);
            }

        } else {
            messages.add(MSG.TENANT_WRONG_TENANT_APIKEY);
        }

        if (messages.size() > 0) {

            throw new EasyRecSoapException(messages, WS.ACTION_OTHER_USERS_ALSO_VIEWED);
        }

        return rec;
    }

    @WebMethod
    public Recommendation recommendationsForUser(@WebParam(name = "apikey") String apiKey,
                                           @WebParam(name = "tenantid") String tenantId,
                                           @WebParam(name = "userid") String userId,
                                           @WebParam(name = "numberOfResults") Integer numberOfResults,
                                           @WebParam(name = "requesteditemtype") String requestedItemType,
                                           @WebParam(name = "actiontype") String actiontype)
            throws EasyRecSoapException {

        Recommendation rec = null;
        List<Message> messages = new ArrayList<Message>();
        Session session = new Session(null,
                    ((HttpServletRequest) wsContext.getMessageContext().get(MessageContext.SERVLET_REQUEST)));

        Integer coreTenantId = operatorDAO.getTenantId(apiKey, tenantId);

       if (coreTenantId != null) {

        RemoteTenant r = remoteTenantDAO.get(coreTenantId);

            if (!Strings.isNullOrEmpty(userId)) {

                requestedItemType = checkItemType(requestedItemType, coreTenantId, tenantId, WS.ACTION_RECOMMENDATIONS_FOR_USER, null);

                if (actiontype == null) actiontype = TypeMappingService.ACTION_TYPE_VIEW;
                if (typeMappingService.getIdOfActionType(coreTenantId, actiontype) != null) {
                    try {
                        if ((numberOfResults == null) || (numberOfResults > WS.DEFAULT_NUMBER_OF_RESULTS))
                            numberOfResults = WS.DEFAULT_NUMBER_OF_RESULTS;

                        rec = shopRecommenderService.itemsBasedOnActionHistory(coreTenantId, userId, session, actiontype, null, null, null,
                                requestedItemType, numberOfResults);
                    } catch (EasyRecSoapException sre) {
                        messages.add(MSG.ITEM_NOT_EXISTS);
                    }
                } else {
                    messages.add(MSG.OPERATION_FAILED.append(String.format(" actionType %s not found for tenant %s", actiontype, tenantId)));
                }
            } else {
                messages.add(MSG.USER_NO_ID);
            }
        } else {
            messages.add(MSG.TENANT_WRONG_TENANT_APIKEY);
        }

        if (messages.size() > 0) {

            throw new EasyRecSoapException(messages,WS.ACTION_RECOMMENDATIONS_FOR_USER);
        }

        return rec;
    }

    @WebMethod
    public Recommendation actionHistoryForUser(@WebParam(name = "apikey") String apiKey,
                                           @WebParam(name = "tenantid") String tenantId, 
                                           @WebParam(name = "userid") String userId,
                                           @WebParam(name = "numberOfResults") Integer numberOfResults,
                                           @WebParam(name = "requesteditemtype") String requestedItemType,
                                           @WebParam(name = "actiontype") String actiontype)
            throws EasyRecSoapException {
        Recommendation rec = null;
        List<Message> messages = new ArrayList<Message>();
        Session session = new Session(null,
                    ((HttpServletRequest) wsContext.getMessageContext().get(MessageContext.SERVLET_REQUEST)));

        Integer coreTenantId = operatorDAO.getTenantId(apiKey, tenantId);

        if (coreTenantId != null) {

            RemoteTenant r = remoteTenantDAO.get(coreTenantId);

            if (!Strings.isNullOrEmpty(userId)) {

                requestedItemType = checkItemType(requestedItemType, coreTenantId, tenantId, WS.ACTION_RECOMMENDATIONS_FOR_USER, null);

                if (actiontype == null) actiontype = TypeMappingService.ACTION_TYPE_VIEW;
                if (typeMappingService.getIdOfActionType(coreTenantId, actiontype) != null) {
                    try {
                        if ((numberOfResults == null) || (numberOfResults > WS.DEFAULT_NUMBER_OF_RESULTS))
                            numberOfResults = WS.DEFAULT_NUMBER_OF_RESULTS;

                        rec = rec = shopRecommenderService.actionHistory(coreTenantId, userId, session, actiontype, requestedItemType, numberOfResults + 5, numberOfResults); // +5 to compensate for inactive items
                    } catch (EasyRecSoapException sre) {
                        messages.add(MSG.OPERATION_FAILED.append("Error retrieving actionHistory for user!"));
                    }
                } else {
                    messages.add(MSG.OPERATION_FAILED.append(String.format(" actionType %s not found for tenant %s", actiontype, tenantId)));
                }
            } else {
                messages.add(MSG.USER_NO_ID);
            }
        } else {
            messages.add(MSG.TENANT_WRONG_TENANT_APIKEY);
        }

        if (messages.size() > 0) {

            throw new EasyRecSoapException(messages,WS.ACTION_RECOMMENDATIONS_FOR_USER);
        }

        return rec;

    } 
    
    
    @WebMethod
    public Recommendation otherUsersAlsoBought(@WebParam(name = "apikey") String apiKey,
                                               @WebParam(name = "tenantid") String tenantId,
                                               @WebParam(name = "userid") String userId,
                                               @WebParam(name = "sessionid") String sessionId,
                                               @WebParam(name = "itemid") String itemId,
                                               @WebParam(name = "numberOfResults") Integer numberOfResults,
                                               @WebParam(name = "itemtype") String itemType,
                                               @WebParam(name = "requesteditemtype") String requestedItemType)
            throws EasyRecSoapException {
        List<Message> messages = new ArrayList<Message>();
        Recommendation rec = null;

        Integer coreTenantId = operatorDAO.getTenantId(apiKey, tenantId);

        if (coreTenantId != null) {

            itemType = checkItemType(itemType, coreTenantId, tenantId, WS.ACTION_OTHER_USERS_ALSO_VIEWED);
            requestedItemType = checkItemType(requestedItemType, coreTenantId, tenantId, WS.ACTION_OTHER_USERS_ALSO_VIEWED, null);

            Session session = new Session(sessionId,
                    ((HttpServletRequest) wsContext.getMessageContext().get(MessageContext.SERVLET_REQUEST)));

            try {
                if ((numberOfResults == null) || (numberOfResults > WS.DEFAULT_NUMBER_OF_RESULTS)) {
                    numberOfResults = WS.DEFAULT_NUMBER_OF_RESULTS;
                }

                rec = shopRecommenderService
                        .alsoBoughtItems(coreTenantId, userId, itemId, itemType,
                                requestedItemType, session, numberOfResults);
            } catch (EasyRecSoapException sre) {
                messages.add(MSG.ITEM_NOT_EXISTS);
            }

        } else {
            messages.add(MSG.TENANT_WRONG_TENANT_APIKEY);
        }

        if (messages.size() > 0) {
            throw new EasyRecSoapException(messages, WS.ACTION_OTHER_USERS_ALSO_BOUGHT);
        }

        return rec;
    }

    public Recommendation itemsRatedGoodByOtherUsers(@WebParam(name = "apikey") String apiKey,
                                                     @WebParam(name = "tenantid") String tenantId,
                                                     @WebParam(name = "userid") String userId,
                                                     @WebParam(name = "sessionid") String sessionId,
                                                     @WebParam(name = "itemid") String itemId,
                                                     @WebParam(name = "numberOfResults") Integer numberOfResults,
                                                     @WebParam(name = "itemtype") String itemType,
                                                     @WebParam(name = "requesteditemtype") String requestedItemType)
            throws EasyRecSoapException {
        List<Message> messages = new ArrayList<Message>();
        Recommendation rec = null;

        Integer coreTenantId = operatorDAO.getTenantId(apiKey, tenantId);

        if (coreTenantId != null) {

            itemType = checkItemType(itemType, coreTenantId, tenantId, WS.ACTION_OTHER_USERS_ALSO_VIEWED);
            requestedItemType = checkItemType(requestedItemType, coreTenantId, tenantId, WS.ACTION_OTHER_USERS_ALSO_VIEWED, null);

            Session session = new Session(sessionId,
                    ((HttpServletRequest) wsContext.getMessageContext().get(MessageContext.SERVLET_REQUEST)));

            try {
                if ((numberOfResults == null) || (numberOfResults > WS.DEFAULT_NUMBER_OF_RESULTS)) {
                    numberOfResults = WS.DEFAULT_NUMBER_OF_RESULTS;
                }
                rec = shopRecommenderService
                        .alsoGoodRatedItems(coreTenantId, userId, itemId, itemType,
                                requestedItemType, session, numberOfResults);
            } catch (EasyRecSoapException sre) {
                messages.add(MSG.ITEM_NOT_EXISTS);
            }

        } else {
            messages.add(MSG.TENANT_WRONG_TENANT_APIKEY);
        }

        if (messages.size() > 0) {
            throw new EasyRecSoapException(messages, WS.ACTION_ITEMS_RATED_GOOD_BY_OTHER_USERS);
        }
        return rec;
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Rankings
    ///////////////////////////////////////////////////////////////////////////////////////////////

    // TODO: maybe use /ext/ path like with REST calls

    @WebMethod
    public Recommendation mostBoughtItems(@WebParam(name = "apikey") String apiKey,
                                          @WebParam(name = "tenantid") String tenantId,
                                          @WebParam(name = "numberOfResults") Integer numberOfResults,
                                          @WebParam(name = "timeRange") String timeRange,
                                          @WebParam(name = "startDate") String startDate,
                                          @WebParam(name = "endDate") String endDate,
                                          @WebParam(name = "requesteditemtype") String requesteditemtype) throws EasyRecSoapException {
        List<Message> messages = new ArrayList<Message>();
        Recommendation rr = null;

        Integer coreTenantId = operatorDAO.getTenantId(apiKey, tenantId);

        if (coreTenantId != null) {

            TimeConstraintVO tc = checkTimeConstraints(startDate, endDate, messages);
            requesteditemtype = checkItemType(requesteditemtype, coreTenantId, tenantId, WS.ACTION_MOST_BOUGHT, null);
            List<Item> items;

            if (tc != null) {

                Session session = new Session(null,
                        ((HttpServletRequest) wsContext.getMessageContext().get(MessageContext.SERVLET_REQUEST)));

                items = shopRecommenderService
                        .mostBoughtItems(coreTenantId, requesteditemtype, numberOfResults, timeRange, tc,
                                session);

                rr = new Recommendation(tenantId, WS.ACTION_MOST_BOUGHT, null, null, null, items);
            }

        } else {
            messages.add(MSG.TENANT_WRONG_TENANT_APIKEY);
        }

        if (messages.size() > 0) {
            throw new EasyRecSoapException(messages, WS.ACTION_MOST_BOUGHT);
        }

        return rr;
    }


    @WebMethod
    public Recommendation mostViewedItems(@WebParam(name = "apikey") String apiKey,
                                          @WebParam(name = "tenantid") String tenantId,
                                          @WebParam(name = "numberOfResults") Integer numberOfResults,
                                          @WebParam(name = "timeRange") String timeRange,
                                          @WebParam(name = "startDate") String startDate,
                                          @WebParam(name = "endDate") String endDate,
                                          @WebParam(name = "requesteditemtype") String requesteditemtype) throws EasyRecSoapException {
        List<Message> messages = new ArrayList<Message>();
        Recommendation rr = null;

        Integer coreTenantId = operatorDAO.getTenantId(apiKey, tenantId);

        if (coreTenantId != null) {

            TimeConstraintVO tc = checkTimeConstraints(startDate, endDate, messages);
            requesteditemtype = checkItemType(requesteditemtype, coreTenantId, tenantId, WS.ACTION_MOST_VIEWED, null);
            List<Item> items;

            if (tc != null) {

                Session session = new Session(null,
                        ((HttpServletRequest) wsContext.getMessageContext().get(MessageContext.SERVLET_REQUEST)));

                items = shopRecommenderService
                        .mostViewedItems(coreTenantId, requesteditemtype, numberOfResults, timeRange, tc,
                                session);

                rr = new Recommendation(tenantId, WS.ACTION_MOST_VIEWED, null, null, null, items);
            }

        } else {
            messages.add(MSG.TENANT_WRONG_TENANT_APIKEY);
        }

        if (messages.size() > 0) {

            throw new EasyRecSoapException(messages, WS.ACTION_MOST_VIEWED);
        }
        return rr;
    }


    @WebMethod
    public Recommendation mostRatedItems(@WebParam(name = "apikey") String apiKey,
                                         @WebParam(name = "tenantid") String tenantId,
                                         @WebParam(name = "numberOfResults") Integer numberOfResults,
                                         @WebParam(name = "timeRange") String timeRange,
                                         @WebParam(name = "startDate") String startDate,
                                         @WebParam(name = "endDate") String endDate,
                                         @WebParam(name = "requesteditemtype") String requesteditemtype) throws EasyRecSoapException {
        List<Message> messages = new ArrayList<Message>();
        Recommendation rr = null;

        Integer coreTenantId = operatorDAO.getTenantId(apiKey, tenantId);

        if (coreTenantId != null) {

            TimeConstraintVO tc = checkTimeConstraints(startDate, endDate, messages);
            requesteditemtype = checkItemType(requesteditemtype, coreTenantId, tenantId, WS.ACTION_MOST_RATED, null);
            List<Item> items;

            if (tc != null) {

                Session session = new Session(null,
                        ((HttpServletRequest) wsContext.getMessageContext().get(MessageContext.SERVLET_REQUEST)));

                items = shopRecommenderService
                        .mostRatedItems(coreTenantId, requesteditemtype, numberOfResults, timeRange, tc,
                                session);

                rr = new Recommendation(tenantId, WS.ACTION_MOST_RATED, null, null, null, items);
            }

        } else {
            messages.add(MSG.TENANT_WRONG_TENANT_APIKEY);
        }

        if (messages.size() > 0) {

            throw new EasyRecSoapException(messages, WS.ACTION_MOST_RATED);
        }

        return rr;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Ratings
    ///////////////////////////////////////////////////////////////////////////////////////////////
    @WebMethod
    public Recommendation bestRatedItems(@WebParam(name = "apikey") String apiKey,
                                         @WebParam(name = "tenantid") String tenantId,
                                         @WebParam(name = "userid") String userId,
                                         @WebParam(name = "numberOfResults") Integer numberOfResults,
                                         @WebParam(name = "timeRange") String timeRange,
                                         @WebParam(name = "startDate") String startDate,
                                         @WebParam(name = "endDate") String endDate,
                                         @WebParam(name = "requesteditemtype") String requesteditemtype) throws EasyRecSoapException {
        List<Message> messages = new ArrayList<Message>();
        Recommendation rr = null;

        Integer coreTenantId = operatorDAO.getTenantId(apiKey, tenantId);

        if (coreTenantId != null) {

            TimeConstraintVO tc = checkTimeConstraints(startDate, endDate, messages);
            requesteditemtype = checkItemType(requesteditemtype, coreTenantId, tenantId,  WS.ACTION_BEST_RATED, null);
            List<Item> items;

            if (tc != null) {

                Session session = new Session(null,
                        ((HttpServletRequest) wsContext.getMessageContext().get(MessageContext.SERVLET_REQUEST)));

                items = shopRecommenderService
                        .bestRatedItems(coreTenantId, userId, requesteditemtype, numberOfResults, timeRange,
                                tc, session);

                rr = new Recommendation(tenantId, WS.ACTION_BEST_RATED, null, null, null, items);
            }

        } else {
            messages.add(MSG.TENANT_WRONG_TENANT_APIKEY);
        }

        if (messages.size() > 0) {

            throw new EasyRecSoapException(messages, WS.ACTION_BEST_RATED);
        }
        return rr;
    }

    @WebMethod
    public Recommendation worstRatedItems(@WebParam(name = "apikey") String apiKey,
                                          @WebParam(name = "tenantid") String tenantId,
                                          @WebParam(name = "userid") String userId,
                                          @WebParam(name = "numberOfResults") Integer numberOfResults,
                                          @WebParam(name = "timeRange") String timeRange,
                                          @WebParam(name = "startDate") String startDate,
                                          @WebParam(name = "endDate") String endDate,
                                          @WebParam(name = "requesteditemtype") String requesteditemtype) throws EasyRecSoapException {
        List<Message> messages = new ArrayList<Message>();
        Recommendation rr = null;

        Integer coreTenantId = operatorDAO.getTenantId(apiKey, tenantId);

        if (coreTenantId != null) {

            TimeConstraintVO tc = checkTimeConstraints(startDate, endDate, messages);
            requesteditemtype = checkItemType(requesteditemtype, coreTenantId, tenantId,  WS.ACTION_WORST_RATED, null);
            List<Item> items;

            if (tc != null) {

                Session session = new Session(null,
                        ((HttpServletRequest) wsContext.getMessageContext().get(MessageContext.SERVLET_REQUEST)));

                items = shopRecommenderService
                        .worstRatedItems(coreTenantId, userId, requesteditemtype, numberOfResults,
                                timeRange, tc, session);

                rr = new Recommendation(tenantId, WS.ACTION_WORST_RATED, null, null, null, items);
            }

        } else {
            messages.add(MSG.TENANT_WRONG_TENANT_APIKEY);
        }

        if (messages.size() > 0) {

            throw new EasyRecSoapException(messages, WS.ACTION_WORST_RATED);
        }
        return rr;
    }

    @WebMethod
    public Recommendation relatedItems(@WebParam(name = "apikey") String apiKey,
                                       @WebParam(name = "tenantid") String tenantId,
                                       @WebParam(name = "assoctype") String assocType,
                                       @WebParam(name = "userid") String userId,
                                       @WebParam(name = "sessionid") String sessionId,
                                       @WebParam(name = "itemid") String itemId,
                                       @WebParam(name = "numberOfResults") Integer numberOfResults,
                                       @WebParam(name = "itemtype") String itemType,
                                       @WebParam(name = "requesteditemtype") String requestedItemType)
            throws EasyRecSoapException {

        List<Message> messages = new ArrayList<Message>();
        Recommendation rr = null;

        Integer coreTenantId = operatorDAO.getTenantId(apiKey, tenantId);

        if (coreTenantId != null) {

            itemType = checkItemType(itemType, coreTenantId, tenantId, WS.ACTION_RELATED_ITEMS);
            requestedItemType = checkItemType(requestedItemType, coreTenantId, tenantId, WS.ACTION_RELATED_ITEMS, null);


            Session session = new Session(sessionId,
                    ((HttpServletRequest) wsContext.getMessageContext().get(MessageContext.SERVLET_REQUEST)));

            RemoteTenant r = remoteTenantDAO.get(coreTenantId);
            if (!r.getPluginsEnabled()) {
                messages.add(MSG.PLUGIN_NOT_INSTALLED);
            }

            if (messages.isEmpty()) {
                try {
                    if ((numberOfResults == null) ||
                            (numberOfResults > WS.DEFAULT_NUMBER_OF_RESULTS)) {
                        numberOfResults = WS.DEFAULT_NUMBER_OF_RESULTS;
                    }

                    rr = shopRecommenderService.relatedItems(coreTenantId, assocType, null, //userId
                            itemId, itemType, requestedItemType, session,
                            numberOfResults);
                } catch (EasyRecSoapException sre) {
                    messages.add(MSG.ITEM_NOT_EXISTS);
                }
            }

        } else {
            messages.add(MSG.TENANT_WRONG_TENANT_APIKEY);
        }

        if (messages.size() > 0) {
            throw new EasyRecSoapException(messages, WS.ACTION_RELATED_ITEMS);
        }

        return rr;

    }

    @WebMethod
    public ResponseItem setItemActive(@WebParam(name = "apikey") String apiKey,
                                      @WebParam(name = "tenantid") String tenantId,
                                      @WebParam(name = "itemid") String itemId,
                                      @WebParam(name = "active") Boolean active,
                                      @WebParam(name = "itemtype") String itemType) throws EasyRecSoapException {

        // Collect a List of messages for the user to understand,
        // what went wrong (e.g. Wrong API key).
        List<Message> messages = new ArrayList<Message>();

        Integer coreTenantId = operatorDAO.getTenantId(apiKey, tenantId);

        if (coreTenantId == null) {
            messages.add(MSG.TENANT_WRONG_TENANT_APIKEY);
        }

        if (itemId == null) {
            messages.add(MSG.ITEM_NO_ID);
        }

        if (active == null) {
            messages.add(MSG.ITEM_NOT_ACTIVE);
        }

        itemType = checkItemType(itemType, coreTenantId, tenantId, WS.ACTION_SET_ITEM_ACTIVE);
        RemoteTenant r = remoteTenantDAO.get(coreTenantId);

        Item item = itemDAO.get(r, itemId, itemType);
        if (item == null) {
            messages.add(MSG.ITEM_NOT_EXISTS);
        }

        if (messages.size() > 0) {
            throw new EasyRecSoapException(messages, WS.ACTION_SET_ITEM_ACTIVE);
        }

        assert item != null;
        if (item.isActive() && !active) {
            itemDAO.deactivate(coreTenantId, itemId, itemType);
        }

        if (!item.isActive() && active) {
            itemDAO.activate(coreTenantId, itemId, itemType);
        }

        return new ResponseItem(tenantId, WS.ACTION_SET_ITEM_ACTIVE + active, null,
                null, null, item);
    }

    @WebMethod
    public Recommendation getItemsOfCluster(@WebParam(name = "apikey") String apiKey,
                                      @WebParam(name = "tenantid") String tenantId,
                                      @WebParam(name = "clusterid") String clusterId,
                                      @WebParam(name = "numberOfResults") Integer numberOfResults,
                                      @WebParam(name = "strategy") String strategy,
                                      @WebParam(name = "usefallback") Boolean useFallback,
                                      @WebParam(name = "requesteditemtype") String requestedItemType)
            throws EasyRecSoapException {

        List<Message> messages = new ArrayList<Message>();
        Recommendation recommendation = null;
        Integer coreTenantId = operatorDAO.getTenantId(apiKey, tenantId);

        if (coreTenantId != null) {

            RemoteTenant remoteTenant = remoteTenantDAO.get(coreTenantId);

            if (clusterId != null) {

                requestedItemType = checkItemType(requestedItemType, coreTenantId, tenantId, WS.ACTION_ITEMS_OF_CLUSTER, null);
                Session session = new Session(null,
                    ((HttpServletRequest) wsContext.getMessageContext().get(MessageContext.SERVLET_REQUEST)));
                List<Item> items;
                try {
                    Integer coreItemType = typeMappingService.getIdOfItemType(coreTenantId, requestedItemType);
                    if (useFallback == null) useFallback = false;
                    items = shopRecommenderService.itemsOfCluster(coreTenantId, clusterId,
                            numberOfResults != null ? numberOfResults : WS.DEFAULT_NUMBER_OF_RESULTS, strategy, useFallback,
                            coreItemType, session);

                    recommendation = new Recommendation(tenantId, WS.ACTION_ITEMS_OF_CLUSTER, null, null, null, items);
                } catch (EasyRecSoapException sre) {
                    messages.add(sre.getMessageObject());
                }
                return recommendation;
            } else {
                messages.add(MSG.CLUSTER_NO_ID);
            }
        } else {
            messages.add(MSG.TENANT_WRONG_TENANT_APIKEY);
        }

        if (messages.size() > 0) {
            throw new EasyRecSoapException(messages, WS.ACTION_RELATED_ITEMS);
        }

        return recommendation;
    }

    @WebMethod
    public ResponseItemTypes itemTypes(@WebParam(name = "apikey") String apiKey,
                              @WebParam(name = "tenantid") String tenantId)
            throws EasyRecSoapException{

        List<Message> messages = new ArrayList<Message>();
        ResponseItemTypes responseItemTypes = null;

        Integer coreTenantId = operatorDAO.getTenantId(apiKey, tenantId);

        if (coreTenantId != null) {

            try {
                Set<String> itemTypes = shopRecommenderService.getItemTypes(coreTenantId);
                responseItemTypes = new ResponseItemTypes(tenantId, itemTypes);
            } catch (EasyRecSoapException e) {
                messages.add(e.getMessageObject().append("Failed to retrieve item types"));
            }

            return responseItemTypes;
        } else {
            messages.add(MSG.TENANT_WRONG_TENANT_APIKEY);
        }

        if (messages.size() > 0) {
            throw new EasyRecSoapException(messages, WS.ACTION_ITEMTYPES);
        }

        return responseItemTypes;
    }

    @WebMethod
    public ResponseClusters clusters(@WebParam(name = "apikey") String apiKey,
                             @WebParam(name = "tenantid") String tenantId)
            throws EasyRecSoapException {

        List<Message> messages = new ArrayList<Message>();
        ResponseClusters responseClusters = null;

        Integer coreTenantId = operatorDAO.getTenantId(apiKey, tenantId);

        if (coreTenantId != null)  {

            try {
                List<ClusterVO> clusters = shopRecommenderService.getClusters(coreTenantId);
                responseClusters = new ResponseClusters(tenantId, clusters);
            } catch (EasyRecSoapException e) {
                messages.add(e.getMessageObject().append("Failed to retrieve clusters"));
            }

            return responseClusters;
        } else {
              messages.add(MSG.TENANT_WRONG_TENANT_APIKEY);
        }

        if (messages.size() > 0) {
            throw new EasyRecSoapException(messages, WS.ACTION_CLUSTERS);
        }

        return responseClusters;
    }

    ///////////////////////////////////////////////////////////////////////////
    // import API
    ///////////////////////////////////////////////////////////////////////////

    @WebMethod
    public ResponseItem importitem(@WebParam(name = "token") String token, @WebParam(name = "tenantid") String tenantId,
                                   @WebParam(name = "itemid") String itemId,
                                   @WebParam(name = "itemdescription") String itemDescription,
                                   @WebParam(name = "itemurl") String itemUrl,
                                   @WebParam(name = "itemimageurl") String itemImageUrl) throws EasyRecSoapException {

        // Collect a List of messages for the user to understand,
        // what went wrong (e.g. Wrong API key).
        List<Message> messages = new ArrayList<Message>();
        Integer coreTenantId = null;

        Operator o = operatorDAO.getOperatorFromToken(token);
        if (o != null) {
            coreTenantId = operatorDAO.getTenantId(o.getApiKey(), tenantId);

            checkParameters(coreTenantId, itemId, itemDescription, itemUrl, messages);
        } else {
            messages.add(MSG.WRONG_TOKEN);
        }


        if (messages.size() > 0) {
            throw new EasyRecSoapException(messages, WS.ACTION_IMPORT_ITEM);
        }

        itemDAO.insertOrUpdate(coreTenantId, itemId, Item.DEFAULT_STRING_ITEM_TYPE, itemDescription, itemUrl,
                itemImageUrl);

        return new ResponseItem(tenantId, WS.ACTION_IMPORT_ITEM, null,  //userId
                null,  //sessionId
                null, itemDAO.get(remoteTenantDAO.get(coreTenantId), itemId, Item.DEFAULT_STRING_ITEM_TYPE));

    }

    @WebMethod
    public ResponseRule importrule(@WebParam(name = "token") String token, @WebParam(name = "tenantid") String tenantId,
                                   @WebParam(name = "itemfromid") String itemfromid,
                                   @WebParam(name = "itemtoid") String itemtoid,
                                   @WebParam(name = "assocvalue") String assocvalue,
                                   @WebParam(name = "assoctype") String assoctype) throws EasyRecSoapException {

        Item itemfrom = null;
        Item itemto = null;
        Float assocValue = null;
        Integer assocTypeId = null;
        Integer coreTenantId = null;

        // Collect a List of messages for the user to understand,
        // what went wrong (e.g. Wrong API key).
        List<Message> messages = new ArrayList<Message>();

        Operator o = operatorDAO.getOperatorFromToken(token);
        if (o != null) {

            coreTenantId = operatorDAO.getTenantId(o.getApiKey(), tenantId);

            if (itemfromid != null && itemfromid.equals(itemtoid)) {
                messages.add(MSG.ITEMFROM_EQUAL_ITEMTO);
            }

            RemoteTenant r = remoteTenantDAO.get(coreTenantId);
            if (r != null) {

                itemfrom = itemDAO.get(r, itemfromid, Item.DEFAULT_STRING_ITEM_TYPE);
                if (itemfrom == null) {
                    messages.add(MSG.ITEM_FROM_ID_DOES_NOT_EXIST);
                }

                itemto = itemDAO.get(r, itemtoid, Item.DEFAULT_STRING_ITEM_TYPE);
                if (itemto == null) {
                    messages.add(MSG.ITEM_TO_ID_DOES_NOT_EXIST);
                }

                try {
                    assocTypeId = typeMappingService.getIdOfAssocType(coreTenantId, assoctype);
                } catch (Exception e) {
                    messages.add(MSG.ASSOC_TYPE_DOES_NOT_EXIST);
                }

            } else {
                messages.add(MSG.TENANT_WRONG_TENANT);
            }
            try {
                assocValue = Float.parseFloat(assocvalue);
                if (assocValue < 0 || assocValue > 100) {
                    messages.add(MSG.INVALID_ASSOC_VALUE);
                }
            } catch (Exception e) {
                messages.add(MSG.INVALID_ASSOC_VALUE);
            }

        } else {
            messages.add(MSG.WRONG_TOKEN);
        }

        if (messages.size() > 0) {
            throw new EasyRecSoapException(messages, WS.ACTION_IMPORT_RULE);
        }

        remoteAssocService.addRule(coreTenantId, itemfromid, null, itemtoid, null, assocTypeId, assocValue);

        return new ResponseRule(tenantId, WS.ACTION_IMPORT_RULE,
                itemfrom.getItemId(), itemto.getItemId(), assoctype, Float.toString(assocValue));
    }

    // private methods
    private Integer authenticate(String tenant) throws EasyRecSoapException {
        try {
            MessageContext mc = wsContext.getMessageContext();
            HttpServletRequest req = (HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST);
            //logger.info("Request from: " + getRequestIP() + "/" + getRequestHost());
            Integer tenantId = authenticationDispatcher.authenticateTenant(tenant, serviceName, req);
            if (tenantId == null) {
                throw new EasyRecSoapException("Unauthorized access!");
            }
            return tenantId;
        } catch (Exception e) {
            throw new EasyRecSoapException(e.getMessage());
        }
    }

    private void checkParams(Integer coreTenantId, String itemId, String itemDescription, String itemUrl,
                             String sessionId, List<Message> messages) throws EasyRecSoapException {

        if (coreTenantId == null) {
            messages.add(MSG.TENANT_WRONG_TENANT_APIKEY);
        }

        if (itemId == null) {
            messages.add(MSG.ITEM_NO_ID);
        }

        if (itemDescription == null) {
            messages.add(MSG.ITEM_NO_DESCRIPTION);
        }

        if (itemUrl == null) {
            messages.add(MSG.ITEM_NO_URL);
        }

        if (sessionId == null) {
            messages.add(MSG.USER_NO_SESSION_ID);
        }
    }

    private void checkParameters(Integer coreTenantId, String itemId, String itemDescription, String itemUrl,
                                 List<Message> messages) throws EasyRecSoapException {

        if (coreTenantId == null) {
            messages.add(MSG.TENANT_WRONG_TENANT_APIKEY);
        }

        if (itemId == null) {
            messages.add(MSG.ITEM_NO_ID);
        }

        if (itemDescription == null) {
            messages.add(MSG.ITEM_NO_DESCRIPTION);
        }

        if (itemUrl == null) {
            messages.add(MSG.ITEM_NO_URL);
        }

    }

    private String checkItemType(String itemType, Integer coreTenantId, String tenantId, String operation) throws EasyRecSoapException
    {
        return checkItemType(itemType, coreTenantId, tenantId, operation, Item.DEFAULT_STRING_ITEM_TYPE);
    }

    private String checkItemType(String itemType, Integer coreTenantId, String tenantId, String operation,
                                 @Nullable String defaultValue) throws EasyRecSoapException {
        if (itemType != null)
            itemType = CharMatcher.WHITESPACE.trimFrom(itemType);

        if (Strings.isNullOrEmpty(itemType))
            return defaultValue;
        else
            try {
                typeMappingService.getIdOfItemType(coreTenantId, itemType, true);

                return itemType;
            } catch (IllegalArgumentException ex) {
                List<Message> messages = new ArrayList<Message>();
                messages.add(MSG.OPERATION_FAILED.append(String.format(" itemType %s not found for tenant %s", itemType, tenantId)));
                throw new EasyRecSoapException(messages, operation);
            }
    }

    private TimeConstraintVO checkTimeConstraints(String startTime, String endTime, List<Message> messages) {

        Date startDate = null;
        Date endDate;

        if (startTime != null) {
            startDate = MyUtils.dateFormatCheck(startTime, dateFormatter);
            if (startDate == null) {
                messages.add(MSG.DATE_PARSE);
                return null;
            }
        }

        if (endTime == null) {
            endDate = new Date(System.currentTimeMillis());
        } else {
            endDate = MyUtils.dateFormatCheck(endTime, dateFormatter);
            if (endDate == null) {
                messages.add(MSG.DATE_PARSE);
                return null;
            }
        }

        return new TimeConstraintVO(startDate, endDate);
    }
}