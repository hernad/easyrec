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
package org.easyrec.controller;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.easyrec.model.core.ItemAssocVO;
import org.easyrec.model.core.TenantVO;
import org.easyrec.model.plugin.LogEntry;
import org.easyrec.model.web.*;
import org.easyrec.plugin.container.PluginRegistry;
import org.easyrec.plugin.generator.Generator;
import org.easyrec.plugin.generator.GeneratorConfiguration;
import org.easyrec.plugin.model.PluginId;
import org.easyrec.plugin.stats.GeneratorStatistics;
import org.easyrec.service.core.TenantService;
import org.easyrec.service.web.ItemService;
import org.easyrec.service.web.NamedConfigurationService;
import org.easyrec.service.web.PluginScheduler;
import org.easyrec.service.web.RemoteTenantService;
import org.easyrec.service.web.nodomain.ShopRecommenderService;
import org.easyrec.store.dao.core.types.AssocTypeDAO;
import org.easyrec.store.dao.plugin.LogEntryDAO;
import org.easyrec.store.dao.web.BackTrackingDAO;
import org.easyrec.store.dao.web.ItemDAO;
import org.easyrec.store.dao.web.OperatorDAO;
import org.easyrec.store.dao.web.RemoteTenantDAO;
import org.easyrec.utils.MessageBlock;
import org.easyrec.utils.Security;
import org.easyrec.utils.Web;
import org.easyrec.utils.servlet.ServletUtils;
import org.easyrec.utils.spring.store.dao.IDMappingDAO;
import org.easyrec.vocabulary.MSG;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Controller handles the Tenant operation.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2009</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: dmann $<br/>
 * $Date: 2011-12-20 15:22:22 +0100 (Di, 20 Dez 2011) $<br/>
 * $Revision: 18685 $</p>
 *
 * @author dmann
 * @version 1.0
 * @since 1.0
 */
public class RemoteTenantController extends MultiActionController {

    // TODO: move to vocabulary?
    private static final String REGISTER_TENANT = "registerTenant";
    private static final String UPDATE_TENANT = "updateTenant";
    private static final String VIEW_TENANTS = "viewTenants";
    private static final String VIEW_ITEMS = "viewItems";
    private static final String VIEW_TOP_RANKED_ITEMS = "viewMostViewedItems";
    private static final String VIEW_HOT_RECOMMENDATIONS = "viewHotRecommendations";
    private static final String VIEW_CLUSTER_MANAGER = "clustermanager";
    private static final String VIEW_RULES_TO_ITEM = "viewRulesToItem";
    private static final String VIEW_STATISTICS = "viewStatistics";
    private static final String REFRESH_STATISTICS = "refreshstatistics";

    private OperatorDAO operatorDAO;
    private RemoteTenantDAO remoteTenantDAO;
    private ItemDAO itemDAO;
    private TenantService tenantService;
    private RemoteTenantService remoteTenantService;
    private ShopRecommenderService shopRecommenderService;
    private PluginScheduler pluginScheduler;
    private BackTrackingDAO backTrackingDAO;
    private IDMappingDAO idMappingDAO;
    private ItemService itemService;
    private AssocTypeDAO assocTypeDAO;
    private LogEntryDAO logEntryDAO;
    private NamedConfigurationService namedConfigurationService;
    private PluginRegistry pluginRegistry;

    public void setNamedConfigurationService(NamedConfigurationService namedConfigurationService) {
        this.namedConfigurationService = namedConfigurationService;
    }

    public void setPluginRegistry(PluginRegistry pluginRegistry) {
        this.pluginRegistry = pluginRegistry;
    }

    public void setLogEntryDAO(LogEntryDAO logEntryDAO) {
        this.logEntryDAO = logEntryDAO;
    }

    public void setRemoteTenantDAO(RemoteTenantDAO remoteTenantDAO) {
        this.remoteTenantDAO = remoteTenantDAO;
    }

    public void setItemDAO(ItemDAO itemDAO) {
        this.itemDAO = itemDAO;
    }

    public void setTenantService(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    public void setOperatorDAO(OperatorDAO operatorDAO) {
        this.operatorDAO = operatorDAO;
    }

    public void setShopRecommenderService(ShopRecommenderService shopRecommenderService) {
        this.shopRecommenderService = shopRecommenderService;
    }

    public void setPluginScheduler(PluginScheduler pluginScheduler) {
        this.pluginScheduler = pluginScheduler;
    }

    public void setBackTrackingDAO(BackTrackingDAO backTrackingDAO) {
        this.backTrackingDAO = backTrackingDAO;
    }

    public void setAssocTypeDAO(AssocTypeDAO assocTypeDAO) {
        this.assocTypeDAO = assocTypeDAO;
    }

    public void setIdMappingDAO(IDMappingDAO idMappingDAO) {
        this.idMappingDAO = idMappingDAO;
    }

    public void setRemoteTenantService(RemoteTenantService remoteTenantService) {
        this.remoteTenantService = remoteTenantService;
    }

    public void setItemService(ItemService itemService) {
        this.itemService = itemService;
    }

    public ModelAndView register(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView();
        List<Message> messages = new ArrayList<Message>();

        String operatorId = ServletUtils.getSafeParameter(request, "operatorId", "");
        String tenantId = ServletUtils.getSafeParameter(request, "tenantId", "");
        String url = ServletUtils.getSafeParameter(request, "url", "");
        String description = HtmlUtils.htmlEscape(ServletUtils.getSafeParameter(request, "description", ""));

        String signedInOperatorId = Security.signedInOperatorId(request);
        Operator signedInOperator = Security.signedInOperator(request);
        RemoteTenant remoteTenant = null;

        remoteTenant = initializeView(request, mav, operatorId, tenantId, url, description, signedInOperatorId,
                signedInOperator, remoteTenant);

        if (!Security.isSignedIn(request)) {
            messages.add(MSG.NOT_SIGNED_IN);
            return MessageBlock.create(mav, messages, REGISTER_TENANT, MSG.ERROR);
        }

        if (!operatorDAO.exists(operatorId)) {
            messages.add(MSG.NO_VALID_OPERATOR);
        }

        // an administrator is allowed to create a tenant for any operator
        if (Security.isDeveloper(request)) {
            if (!Strings.isNullOrEmpty(operatorId)) {
                signedInOperatorId = operatorId;
            }
        }
        tenantId = tenantId.replace("?", "");
        logger.info("Registering Tenant: " + signedInOperator + ":" + tenantId);

        if (!Web.isValidUrl(url) || url.equalsIgnoreCase("http://localhost/")) {
            messages.add(MSG.NO_VALID_URL);
        }

        if (Strings.isNullOrEmpty(tenantId) || !tenantId.equals(tenantId.replaceAll("[^A-Za-z_0-9]+", ""))) {
            messages.add(MSG.INVALID_TENANTID);
        }


        TenantVO tenantVO = new TenantVO(tenantId, description);
        Integer iTenantId;

        if (remoteTenantDAO.exists(signedInOperatorId, tenantId)) {
            messages.add(MSG.REMOTE_TENANT_EXISTS);
        }

        if (messages.size() > 0) {
            return MessageBlock.create(mav, messages, REGISTER_TENANT, MSG.ERROR);
        } else {
            synchronized (this) {
                try {
                    iTenantId = tenantService.insertTenantWithTypes(tenantVO, null);
                } catch (Exception e) {
                    return MessageBlock.createSingle(mav, MSG.TENANT_AUTHENTICATION_FAILED, REGISTER_TENANT, MSG.ERROR);
                }

                remoteTenantDAO.update(signedInOperatorId, iTenantId, url, description);

                // enable auto archive function
                // by default actions older than 5 years are moved
                // to the archive
                tenantService.updateConfigProperty(iTenantId, RemoteTenant.AUTO_ARCHIVER_ENABLED, "true");

                tenantService.updateConfigProperty(iTenantId, RemoteTenant.AUTO_ARCHIVER_TIME_RANGE,
                        RemoteTenant.AUTO_ARCHIVER_DEFAULT_TIME_RANGE);


                // enable backtracking by default
                tenantService.updateConfigProperty(iTenantId, RemoteTenant.BACKTRACKING, "true");
                // enable auto rule mining by default
                tenantService.updateConfigProperty(iTenantId, RemoteTenant.SCHEDULER_ENABLED, "true");
                tenantService.updateConfigProperty(iTenantId, RemoteTenant.SCHEDULER_EXECUTION_TIME,
                        RemoteTenant.SCHEDULER_DEFAULT_EXECUTION_TIME);
                pluginScheduler.addTask(remoteTenantDAO.get(iTenantId));

                if (RemoteTenant.DEFAULT_TENANT_ID.equals(tenantId))
                    // create default items and rules for "EASYREC_DEMO"
                    namedConfigurationService.setupDefaultTenant(iTenantId, request.getRemoteAddr());
                else
                    // create configuration for ARM plugin for buy, view, rate actions
                    namedConfigurationService.setupDefaultConfiguration(iTenantId);
            }

            messages.add(MSG.TENANT_REGISTERED.append(" (" + iTenantId + "@" + url + ")"));

            return MessageBlock.create(mav, messages, REGISTER_TENANT, MSG.SUCCESS);
        }
    }

    public ModelAndView update(HttpServletRequest request, HttpServletResponse response) {

        ModelAndView mav = new ModelAndView();
        List<Message> messages = new ArrayList<Message>();

        String operatorId = ServletUtils.getSafeParameter(request, "operatorId", "");
        String tenantId = ServletUtils.getSafeParameter(request, "tenantId", "");
        String url = ServletUtils.getSafeParameter(request, "url", "");
        String description = ServletUtils.getSafeParameter(request, "description", "");


        String signedInOperatorId = Security.signedInOperatorId(request);
        Operator signedInOperator = Security.signedInOperator(request);
        RemoteTenant remoteTenant = null;

        remoteTenant = initializeView(request, mav, operatorId, tenantId, url, description, signedInOperatorId,
                signedInOperator, remoteTenant);

        if (!Security.isSignedIn(request)) {
            messages.add(MSG.NOT_SIGNED_IN);
            return MessageBlock.create(mav, messages, UPDATE_TENANT, MSG.ERROR);
        }

        if (!operatorDAO.exists(operatorId)) {
            messages.add(MSG.NO_VALID_OPERATOR);
        }


        if (remoteTenant == null) {
            messages.add(MSG.TENANT_NOT_EXISTS);
        }

        if (!Web.isValidUrl(url) || url.equalsIgnoreCase("http://localhost/")) {
            messages.add(MSG.NO_VALID_URL);
        }

        if (messages.size() > 0) {
            return MessageBlock.create(mav, messages, UPDATE_TENANT, MSG.ERROR);
        } else {
            remoteTenantDAO.update(operatorId, remoteTenant.getId(), url, description);

            itemDAO.emptyCache();

            return MessageBlock.createSingle(mav, MSG.TENANT_UPDATED, UPDATE_TENANT, MSG.SUCCESS);

        }
    }

    public ModelAndView view(HttpServletRequest request, HttpServletResponse response) {

        ModelAndView mav = new ModelAndView();
        List<Message> messages = new ArrayList<Message>();

        String operatorId = ServletUtils.getSafeParameter(request, "operatorId", "");
        String tenantId = ServletUtils.getSafeParameter(request, "tenantId", "");
        String url = ServletUtils.getSafeParameter(request, "url", "");
        String description = ServletUtils.getSafeParameter(request, "description", "");

        String signedInOperatorId = Security.signedInOperatorId(request);
        Operator signedInOperator = Security.signedInOperator(request);
        RemoteTenant remoteTenant = null;

        remoteTenant = initializeView(request, mav, operatorId, tenantId, url, description, signedInOperatorId,
                signedInOperator, remoteTenant);

        if (!Security.isSignedIn(request)) {
            messages.add(MSG.NOT_SIGNED_IN);
            return MessageBlock.create(mav, messages, VIEW_TENANTS, MSG.ERROR);
        }

        if (!operatorDAO.exists(operatorId)) {
            messages.add(MSG.NO_VALID_OPERATOR);
        }

        if (messages.size() > 0 && !"easyrec".equals(operatorId)) {
            return MessageBlock.create(mav, messages, VIEW_TENANTS, MSG.ERROR);
        } else {
            Security.setAttribute(request, "menu", "tenant");
            mav.setViewName("easyrec/overview");

            if (Security.signedInOperator(request) != null && remoteTenant != null) {
                mav.addObject("tenant", remoteTenant);
            }

            mav.addObject("tenantsShow", remoteTenant != null);
        }

        String apiKey = Operator.DEFAULT_API_KEY;

        if (signedInOperator != null) {
            apiKey = signedInOperator.getApiKey();
        }

        mav.addObject("apiKey", apiKey);
        mav.addObject("signedIn", Security.isSignedIn(request));

        return mav;

    }

    /**
     * Depending whenever the signed in user is a developer or not, this function allows the operator to impersonate
     * another operator and returns the corresponding remote tenant.
     * <p/>
     * This function was created due historical circumstances. The code was extracted from the Remote Tenant controller where
     * this code is used very often. Look into this controller tro understand the usage better - sorry
     *
     * @param request
     * @param mav
     * @param operatorId
     * @param tenantId
     * @param url
     * @param description
     * @param signedInOperatorId
     * @param signedInOperator
     * @param remoteTenant
     * @return
     */
    private RemoteTenant initializeView(HttpServletRequest request, ModelAndView mav, String operatorId,
                                        String tenantId, String url, String description, String signedInOperatorId,
                                        Operator signedInOperator, RemoteTenant remoteTenant) {
        if (signedInOperator != null) {
            if (Security.isDeveloper(request) && !Strings.isNullOrEmpty(operatorId)) {
                remoteTenant = remoteTenantDAO.get(operatorId, tenantId);
            } else {
                remoteTenant = remoteTenantDAO.get(signedInOperator.getOperatorId(), tenantId);
            }


            if (remoteTenant != null) {
                mav.addObject("remoteTenant", remoteTenant);
            }
        }

        mav.addObject("title", "tenant");

        if (Security.isDeveloper(request) && !Strings.isNullOrEmpty(operatorId)) {
            mav.addObject("operatorId", operatorId);
        } else {
            mav.addObject("operatorId", signedInOperatorId);
        }
        mav.addObject("tenantId", tenantId);
        mav.addObject("selectedMenu", "myEasyrec");
        mav.addObject("url", url);
        mav.addObject("description", description);
        return remoteTenant;
    }

    public ModelAndView rulestoitem(HttpServletRequest request, HttpServletResponse response) {

        ModelAndView mav = new ModelAndView();
        List<Message> messages = new ArrayList<Message>();

        String operatorId = ServletUtils.getSafeParameter(request, "operatorId", "");
        String tenantId = ServletUtils.getSafeParameter(request, "tenantId", "");
        String url = ServletUtils.getSafeParameter(request, "url", "");
        String description = ServletUtils.getSafeParameter(request, "description", "");


        String signedInOperatorId = Security.signedInOperatorId(request);
        Operator signedInOperator = Security.signedInOperator(request);
        RemoteTenant remoteTenant = null;

        remoteTenant = initializeView(request, mav, operatorId, tenantId, url, description, signedInOperatorId,
                signedInOperator, remoteTenant);

        if (!Security.isSignedIn(request)) {
            messages.add(MSG.NOT_SIGNED_IN);
            return MessageBlock.create(mav, messages, VIEW_RULES_TO_ITEM, MSG.ERROR);
        }

        if (!operatorDAO.exists(operatorId)) {
            messages.add(MSG.NO_VALID_OPERATOR);
        }


        List<ItemAssocVO<String, String>> itemAssocVOs;

        try {
            Item item = itemService.get(request);
            response.setContentType("text/xml; charset=utf-8");

            itemAssocVOs = shopRecommenderService.getRules(item);
            Set<String> assocTypes;
            String viewType = null;

            //                assocTypes = shopRecommenderService.getAssocTypes(remoteTenant.getId());
            assocTypes =  assocTypeDAO.getTypes(remoteTenant.getId());

            // initializing itemAssocs e.g.
            // key      value
            // "view"   null
            // "bought" null
            // "rated"  null
            HashMap<String, List<Assoc>> itemAssocs = new HashMap<String, List<Assoc>>();
            for (String string : assocTypes) {
                itemAssocs.put(string, new ArrayList<Assoc>());
            }

            // filling assoc types with rules
            for (ItemAssocVO<String, String> itemAssocVO : itemAssocVOs) {

                // no need to consider rule if for an uninteresting type
                if (itemAssocs.containsKey(itemAssocVO.getAssocType())) {
                    Item itemFrom = itemDAO.get(remoteTenant, itemAssocVO.getItemFrom().getItem(),
                            itemAssocVO.getItemFrom().getType());
                    Item itemTo = itemDAO.get(remoteTenant, itemAssocVO.getItemTo().getItem(),
                            itemAssocVO.getItemTo().getType());

                    if (itemFrom != null && itemTo != null) {
                        String sourceType = itemAssocVO.getSourceType();

                        try {
                            PluginId pluginId = PluginId.parsePluginId(sourceType);
                            Generator<?, ?> generator = pluginRegistry.getGenerators().get(pluginId);

                            if (generator != null) {
                                sourceType = generator.getDisplayName();
                            }
                        } catch (IllegalArgumentException ignored) {
                        }


                        Assoc assoc = new Assoc(itemFrom, itemTo, itemAssocVO.getAssocType(),
                                itemAssocVO.getAssocValue(), sourceType, itemAssocVO.getViewType(),
                                itemAssocVO.getSourceInfo());

                        Integer count = backTrackingDAO.getItemCount(remoteTenant.getId(),
                                idMappingDAO.lookup(itemAssocVO.getItemFrom().getItem()),
                                idMappingDAO.lookup(itemAssocVO.getItemTo().getItem()),
                                assocTypeDAO.getIdOfType(remoteTenant.getId(), itemAssocVO.getAssocType()));

                        itemTo.setValue(count != null ? count.doubleValue() : null);
                        itemAssocs.get(itemAssocVO.getAssocType()).add(assoc);

                    }
                }
            }
            mav.setViewName("easyrec/viewrules");
            mav.addObject("assocs", itemAssocs);
            mav.addObject("tenantId", tenantId);
            mav.addObject("item", item);
        } catch (Exception ex) {
            Logger.getLogger(ItemController.class.getName()).log(Level.SEVERE, null, ex);
        }


        String apiKey = Operator.DEFAULT_API_KEY;

        if (signedInOperator != null) {
            apiKey = signedInOperator.getApiKey();
        }

        mav.addObject("apiKey", apiKey);
        mav.addObject("signedIn", Security.isSignedIn(request));

        return mav;
    }

    public ModelAndView statistics(HttpServletRequest request, HttpServletResponse response) {

        ModelAndView mav = new ModelAndView();
        List<Message> messages = new ArrayList<Message>();

        String operatorId = ServletUtils.getSafeParameter(request, "operatorId", "");
        String tenantId = ServletUtils.getSafeParameter(request, "tenantId", "");
        String url = ServletUtils.getSafeParameter(request, "url", "");
        String description = ServletUtils.getSafeParameter(request, "description", "");

        String responseType = ServletUtils.getSafeParameter(request, "responseType", Web.HTML);


        String signedInOperatorId = Security.signedInOperatorId(request);
        Operator signedInOperator = Security.signedInOperator(request);
        RemoteTenant remoteTenant = null;

        remoteTenant = initializeView(request, mav, operatorId, tenantId, url, description, signedInOperatorId,
                signedInOperator, remoteTenant);

        if (!Security.isSignedIn(request)) {
            messages.add(MSG.NOT_SIGNED_IN);
            return MessageBlock.create(mav, messages, VIEW_STATISTICS, MSG.ERROR);
        }

        if (!operatorDAO.exists(operatorId)) {
            messages.add(MSG.NO_VALID_OPERATOR);
        }


        if (Web.HTML.equals(responseType)) {
            mav.setViewName("page");
        } else {
            mav.setViewName("xml/statistics");
        }
        Security.setAttribute(request, "menu", "statistics");
        mav.addObject("title", "easyrec :: view statistics");
        mav.addObject("menubar", VIEW_STATISTICS);
        mav.addObject("page", "easyrec/viewstatisticsassocs");
        mav.addObject("tenantId", tenantId);
        mav.addObject("tenants", remoteTenantDAO.getTenantsFromOperator(operatorId));

        if (remoteTenant != null) {

            mav.addObject("statAssoc", remoteTenantService.getAssocStatistic(remoteTenant.getId()));

            mav.addObject("tenantStatistics", remoteTenantService.getTenantStatistics(remoteTenant.getId()));
            mav.addObject("userStatistics", remoteTenantService.getUserStatistics(remoteTenant.getId()));

            mav.addObject("conversionStatistics",
                    remoteTenantService.getConversionStatistics(remoteTenant.getId()));

            mav.addObject("ruleMinerStatistics", remoteTenantService.getRuleMinerStatistics(remoteTenant.getId()));
            if (remoteTenant.getPluginsEnabled() && (remoteTenant.getGeneratorConfig() != null)) {
                int assocType = assocTypeDAO.getIdOfType(remoteTenant.getId(),
                        remoteTenant.getGeneratorConfig().getAssociationType());

                List<LogEntry> logEntries = logEntryDAO.getLogEntriesForTenant(remoteTenant.getId(), assocType, 0, 1);
                LogEntry entry = logEntries.size() > 0 ? logEntries.get(0) : null;

                mav.addObject("generatorLog", entry);
            }

            // i am sorry for such a code... will be fixed soon
            if (remoteTenant == null) {    // seems like there is no session for this user.
                messages.add(MSG.NOT_SIGNED_IN);
                return MessageBlock.create(mav, messages, REGISTER_TENANT, MSG.ERROR);
            }

            // We need to get a full list of ASSOC types before we can create Statistics
            HashMap<String, Integer> mapping = assocTypeDAO.getMapping(remoteTenant.getId());

            Map<String, String> assocTypeToStatistic = Maps.newHashMap();
            Map<String, PluginId> assocTypeToPlugin = Maps.newHashMap();
            Map<String, String> pluginRealName = Maps.newHashMap();

            for (Map.Entry<String, Integer> assocTypeFromTenant : mapping.entrySet()) {
                // now we can load the last ran log entry from each assoc type. if there is no log the assoc type gets skipped.
                List<LogEntry> logEntries =
                        logEntryDAO.getLogEntriesForTenant(remoteTenant.getId(), assocTypeFromTenant.getValue(), 0, 1);
                if (logEntries.size() == 0) continue;

                // due some problems with the taglib which converts the XML to a table we have to remove the first line which contains
                // <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                // if we don't remove this part from the string the taglib cannot create the HTML table.
                String xmlString = logEntries.get(0).getStatistics().getXmlRepresentation();
                xmlString = xmlString.substring(xmlString.indexOf("?>") + 2);

                // here we fill our maps with the data for later use in the JSP.
                PluginId pluginId = logEntries.get(0).getPluginId();
                assocTypeToPlugin.put(assocTypeFromTenant.getKey(), pluginId);
                assocTypeToStatistic.put(assocTypeFromTenant.getKey(), xmlString);

                Generator<GeneratorConfiguration, GeneratorStatistics> generator =
                        pluginRegistry.getGenerators().get(pluginId);
                if (generator == null) continue; // avoid null pointer exception when the plugin is uninstalled now.
                pluginRealName.put(assocTypeFromTenant.getKey(), generator.getDisplayName());
            }

            // as i told you we use this two hash-maps in our JSP
            mav.addObject("assocTypeToStatistic",
                    assocTypeToStatistic); // this will be transformed to a table in the jsp.
            mav.addObject("assocTypeToPlugin", assocTypeToPlugin); // this is used to display the help texts
            mav.addObject("pluginRealName", pluginRealName); // contains human readable names of the plugins

            mav.addObject("title", "easyrec :: view statistics");

            if (Security.isDeveloper(request) && !Strings.isNullOrEmpty(operatorId)) {
                mav.addObject("operatorId", operatorId);
            } else {
                mav.addObject("operatorId", signedInOperatorId);
            }
            mav.addObject("tenantId", tenantId);
            mav.addObject("selectedMenu", "myEasyrec");
            mav.addObject("url", url);
            mav.addObject("description", description);

        }


        String apiKey = Operator.DEFAULT_API_KEY;

        if (signedInOperator != null) {
            apiKey = signedInOperator.getApiKey();
        }

        mav.addObject("apiKey", apiKey);
        mav.addObject("signedIn", Security.isSignedIn(request));

        return mav;
    }

    public ModelAndView viewmostvieweditems(HttpServletRequest request, HttpServletResponse response) {

        ModelAndView mav = new ModelAndView();
        List<Message> messages = new ArrayList<Message>();

        String operatorId = ServletUtils.getSafeParameter(request, "operatorId", "");
        String tenantId = ServletUtils.getSafeParameter(request, "tenantId", "");
        String url = ServletUtils.getSafeParameter(request, "url", "");
        String description = ServletUtils.getSafeParameter(request, "description", "");
        String timerange = ServletUtils.getSafeParameter(request, "timerange", "MONTH");
        String assocType = ServletUtils.getSafeParameter(request, "assoc", "");


        String signedInOperatorId = Security.signedInOperatorId(request);
        Operator signedInOperator = Security.signedInOperator(request);
        RemoteTenant remoteTenant = null;

        remoteTenant = initializeView(request, mav, operatorId, tenantId, url, description, signedInOperatorId,
                signedInOperator, remoteTenant);

        if (!Security.isSignedIn(request)) {
            messages.add(MSG.NOT_SIGNED_IN);
            return MessageBlock.create(mav, messages, VIEW_TOP_RANKED_ITEMS, MSG.ERROR);
        }

        if (!operatorDAO.exists(operatorId)) {
            messages.add(MSG.NO_VALID_OPERATOR);
        }


        Security.setAttribute(request, "menu", "viewmostvieweditems");
        mav.setViewName("page");
        mav.addObject("menubar", VIEW_TOP_RANKED_ITEMS);
        mav.addObject("title", "easyrec :: view top ranked items");
        mav.addObject("page", "easyrec/viewmostvieweditems");
        mav.addObject("tenantId", tenantId);
        mav.addObject("tenants", remoteTenantDAO.getTenantsFromOperator(operatorId));

        if (remoteTenant != null) {

            if ("mostviewed".equals(assocType) || Strings.isNullOrEmpty(assocType)) {
                List<Item> items =
                        shopRecommenderService.mostViewedItems(remoteTenant.getId(), null, 50, timerange, null,
                                new Session(null, request.getRemoteAddr()));
                mav.addObject("items", items);
            }

            if ("mostbought".equals(assocType)) {
                List<Item> items =
                        shopRecommenderService.mostBoughtItems(remoteTenant.getId(), null, 50, timerange, null,
                                new Session(null, request.getRemoteAddr()));
                mav.addObject("items", items);
            }

            if ("mostrated".equals(assocType)) {
                List<Item> items =
                        shopRecommenderService.mostRatedItems(remoteTenant.getId(), null, 50, timerange, null,
                                new Session(null, request.getRemoteAddr()));
                mav.addObject("items", items);
            }

            if ("bestrated".equals(assocType)) {
                List<Item> items =
                        shopRecommenderService.bestRatedItems(remoteTenant.getId(), null, null, 50, timerange, null,
                                new Session(null, request.getRemoteAddr()));
                mav.addObject("items", items);
            }

            if ("worstrated".equals(assocType)) {
                List<Item> items =
                        shopRecommenderService.worstRatedItems(remoteTenant.getId(), null, null, 50, timerange, null,
                                new Session(null, request.getRemoteAddr()));
                mav.addObject("items", items);
            }

        }


        String apiKey = Operator.DEFAULT_API_KEY;

        if (signedInOperator != null) {
            apiKey = signedInOperator.getApiKey();
        }

        mav.addObject("apiKey", apiKey);
        mav.addObject("signedIn", Security.isSignedIn(request));

        return mav;
    }

    public ModelAndView viewhotrecommendations(HttpServletRequest request, HttpServletResponse response) {

        ModelAndView mav = new ModelAndView();
        List<Message> messages = new ArrayList<Message>();

        String operatorId = ServletUtils.getSafeParameter(request, "operatorId", "");
        String tenantId = ServletUtils.getSafeParameter(request, "tenantId", "");
        String url = ServletUtils.getSafeParameter(request, "url", "");
        String description = ServletUtils.getSafeParameter(request, "description", "");

        String signedInOperatorId = Security.signedInOperatorId(request);
        Operator signedInOperator = Security.signedInOperator(request);
        RemoteTenant remoteTenant = null;

        remoteTenant = initializeView(request, mav, operatorId, tenantId, url, description, signedInOperatorId,
                signedInOperator, remoteTenant);

        if (!Security.isSignedIn(request)) {
            messages.add(MSG.NOT_SIGNED_IN);
            return MessageBlock.create(mav, messages, VIEW_HOT_RECOMMENDATIONS, MSG.ERROR);
        }

        if (!operatorDAO.exists(operatorId)) {
            messages.add(MSG.NO_VALID_OPERATOR);
        }


        Security.setAttribute(request, "menu", "viewhotrecommendations");
        // the page view provides the design with a side menu and the menu bar
        mav.setViewName("page");
        mav.addObject("title", "easyrec :: view hot recommendations");
        mav.addObject("menubar", VIEW_HOT_RECOMMENDATIONS);
        mav.addObject("page", "easyrec/viewhotrecommendations");
        mav.addObject("tenantId", tenantId);
        mav.addObject("tenants", remoteTenantDAO.getTenantsFromOperator(operatorId));
        if (remoteTenant != null) {
            mav.addObject("items", itemDAO.getHotItems(remoteTenant, 0, 100));
        }


        String apiKey = Operator.DEFAULT_API_KEY;

        if (signedInOperator != null) {
            apiKey = signedInOperator.getApiKey();
        }

        mav.addObject("apiKey", apiKey);
        mav.addObject("signedIn", Security.isSignedIn(request));

        return mav;
    }

    /*
     * This view is used to start the cluster explorer from the tenant specific Menu bar
     * It will display a Start Cluster explorer link with a description text of the cluster
     * explorer.
     *
     * the code is a mess - sorry - i had to use the existing code to integrate it to the easyrec
     */
    public ModelAndView clustermanager(HttpServletRequest request, HttpServletResponse response) {

        ModelAndView mav = new ModelAndView();
        List<Message> messages = new ArrayList<Message>();

        String operatorId = ServletUtils.getSafeParameter(request, "operatorId", "");
        String tenantId = ServletUtils.getSafeParameter(request, "tenantId", "");
        String url = ServletUtils.getSafeParameter(request, "url", "");
        String description = ServletUtils.getSafeParameter(request, "description", "");

        String signedInOperatorId = Security.signedInOperatorId(request);
        Operator signedInOperator = Security.signedInOperator(request);
        RemoteTenant remoteTenant = null;

        remoteTenant = initializeView(request, mav, operatorId, tenantId, url, description, signedInOperatorId,
                signedInOperator, remoteTenant);

        if (!Security.isSignedIn(request)) {
            messages.add(MSG.NOT_SIGNED_IN);
            return MessageBlock.create(mav, messages, VIEW_CLUSTER_MANAGER, MSG.ERROR);
        }

        if (!operatorDAO.exists(operatorId)) {
            messages.add(MSG.NO_VALID_OPERATOR);
        }

        Security.setAttribute(request, "menu", VIEW_CLUSTER_MANAGER);
        // the page view provides the design with a header and footer
        mav.setViewName("page");
        mav.addObject("title", "easyrec :: cluster manager");
        mav.addObject("menubar", VIEW_CLUSTER_MANAGER);
        mav.addObject("page", "easyrec/clustermanager");
        mav.addObject("tenantId", tenantId);
        mav.addObject("tenants", remoteTenantDAO.getTenantsFromOperator(operatorId));
        if (remoteTenant != null) {
            mav.addObject("items", itemDAO.getHotItems(remoteTenant, 0, 100));
        }


        String apiKey = Operator.DEFAULT_API_KEY;

        if (signedInOperator != null) {
            apiKey = signedInOperator.getApiKey();
        }

        mav.addObject("apiKey", apiKey);
        mav.addObject("signedIn", Security.isSignedIn(request));

        return mav;
    }

    public ModelAndView refreshstatistics(HttpServletRequest request, HttpServletResponse response) {

        ModelAndView mav = new ModelAndView();
        List<Message> messages = new ArrayList<Message>();

        String operatorId = ServletUtils.getSafeParameter(request, "operatorId", "");
        String tenantId = ServletUtils.getSafeParameter(request, "tenantId", "");
        String url = ServletUtils.getSafeParameter(request, "url", "");
        String description = ServletUtils.getSafeParameter(request, "description", "");

        String signedInOperatorId = Security.signedInOperatorId(request);
        Operator signedInOperator = Security.signedInOperator(request);
        RemoteTenant remoteTenant = null;

        remoteTenant = initializeView(request, mav, operatorId, tenantId, url, description, signedInOperatorId,
                signedInOperator, remoteTenant);

        if (!Security.isSignedIn(request)) {
            messages.add(MSG.NOT_SIGNED_IN);
            return MessageBlock.create(mav, messages, REFRESH_STATISTICS, MSG.ERROR);
        }

        if (!operatorDAO.exists(operatorId)) {
            messages.add(MSG.NO_VALID_OPERATOR);
        }

        if (remoteTenant != null) {
            remoteTenantService.updateTenantStatistics(remoteTenant.getId());
        }
        return MessageBlock.createSingle(mav, MSG.OPERATION_SUCCESSFUL, REFRESH_STATISTICS, MSG.SUCCESS);
    }

}
