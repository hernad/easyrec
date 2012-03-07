/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.easyrec.controller.dev;


import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.easyrec.model.plugin.NamedConfiguration;
import org.easyrec.model.plugin.PluginVO;
import org.easyrec.model.web.EasyRecSettings;
import org.easyrec.model.web.PluginParamDetails;
import org.easyrec.model.web.RemoteTenant;
import org.easyrec.plugin.Plugin.LifecyclePhase;
import org.easyrec.plugin.configuration.ConfigurationHelper;
import org.easyrec.plugin.container.PluginRegistry;
import org.easyrec.plugin.generator.Generator;
import org.easyrec.plugin.generator.GeneratorConfiguration;
import org.easyrec.plugin.model.PluginId;
import org.easyrec.plugin.stats.GeneratorStatistics;
import org.easyrec.service.core.TenantService;
import org.easyrec.service.domain.DomainActionService;
import org.easyrec.service.domain.DomainItemAssocService;
import org.easyrec.service.domain.TypeMappingService;
import org.easyrec.service.web.PluginScheduler;
import org.easyrec.service.web.RemoteTenantService;
import org.easyrec.service.web.nodomain.ShopRecommenderService;
import org.easyrec.store.dao.core.types.AssocTypeDAO;
import org.easyrec.store.dao.plugin.LogEntryDAO;
import org.easyrec.store.dao.plugin.NamedConfigurationDAO;
import org.easyrec.store.dao.plugin.PluginDAO;
import org.easyrec.store.dao.web.BackTrackingDAO;
import org.easyrec.store.dao.web.RemoteTenantDAO;
import org.easyrec.utils.MessageBlock;
import org.easyrec.utils.MyUtils;
import org.easyrec.utils.PageStringGenerator;
import org.easyrec.utils.Security;
import org.easyrec.utils.servlet.ServletUtils;
import org.easyrec.vocabulary.MSG;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;


/**
 * @author dmann
 */
public class TenantsController extends MultiActionController {

    private RemoteTenantDAO remoteTenantDAO;
    private DomainItemAssocService itemAssocService;
    private DomainActionService actionService;
    private TenantService tenantService;
    private PluginScheduler pluginScheduler;
    private LogEntryDAO logEntryDAO;
    private BackTrackingDAO backTrackingDAO;
    private ShopRecommenderService shopRecommenderService;
    private RemoteTenantService remoteTenantService;
    private PluginDAO pluginDAO;
    private PluginRegistry pluginRegistry;
    private TypeMappingService typeMappingService;
    private AssocTypeDAO assocTypeDAO;
    private NamedConfigurationDAO namedConfigurationDAO;
    private EasyRecSettings easyrecSettings;

    public void setEasyrecSettings(EasyRecSettings easyrecSettigs) {
        this.easyrecSettings = easyrecSettigs;
    }

    public void setBackTrackingDAO(BackTrackingDAO backTrackingDAO) {
        this.backTrackingDAO = backTrackingDAO;
    }

    public void setLogEntryDAO(LogEntryDAO logEntryDAO) {
        this.logEntryDAO = logEntryDAO;
    }

    public void setPluginScheduler(PluginScheduler pluginScheduler) {
        this.pluginScheduler = pluginScheduler;
    }

    public void setRemoteTenantDAO(RemoteTenantDAO remoteTenantDAO) {
        this.remoteTenantDAO = remoteTenantDAO;
    }

    public void setActionService(DomainActionService actionService) {
        this.actionService = actionService;
    }

    public void setItemAssocService(DomainItemAssocService itemAssocService) {
        this.itemAssocService = itemAssocService;
    }

    public void setTenantService(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    public void setShopRecommenderService(ShopRecommenderService shopRecommenderService) {
        this.shopRecommenderService = shopRecommenderService;
    }

    public void setRemoteTenantService(RemoteTenantService remoteTenantService) {
        this.remoteTenantService = remoteTenantService;
    }

    public void setPluginDAO(PluginDAO pluginDAO) {
        this.pluginDAO = pluginDAO;
    }

    public void setPluginRegistry(PluginRegistry pluginRegistry) {
        this.pluginRegistry = pluginRegistry;
    }

    public void setTypeMappingService(TypeMappingService typeMappingService) {
        this.typeMappingService = typeMappingService;
    }

    public void setAssocTypeDAO(AssocTypeDAO assocTypeDAO) {
        this.assocTypeDAO = assocTypeDAO;
    }

    public void setNamedConfigurationDAO(NamedConfigurationDAO namedConfigurationDAO) {
        this.namedConfigurationDAO = namedConfigurationDAO;
    }

    private static final String REMOVE_TENANT = "removeTenant";
    private static final String RESET_TENANT = "resetTenant";
    private static final String VIEW_ALL_TENANTS = "viewAllTenants";
    private static final String VIEW_TENANTS = "viewTenants";
    private static final String VIEW_PLUGIN_CONFIG = "viewPluginConfig";
    private static final String VIEW_PLUGIN_DETAILS = "viewPluginDetails";
    private static final String STORE_SCHEDULER = "storeScheduler";
    private static final String STORE_BACKTRACKING = "storeBackTracking";
    private static final String STORE_MAXACTIONS = "storeMaxActions";
    private static final String STORE_ARCHIVE = "storeArchive";
    private static final String STORE_PLUGIN_CONFIG = "storePluginConfig";
    private static final String STORE_PLUGINS_ACTIVE = "storePluginsActive";


    public ModelAndView removetenant(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        String tenantId = ServletUtils.getSafeParameter(request, "tenantId", "");
        String operatorId = ServletUtils.getSafeParameter(request, "operatorId", "");

        ModelAndView mav = new ModelAndView("page");

        mav.addObject("title", "easyrec :: administration");

        mav.addObject("operatorId", operatorId);
        mav.addObject("tenantId", tenantId);

        if (Security.isDeveloper(request)) {
            RemoteTenant remoteTenant = remoteTenantDAO.get(operatorId, tenantId);

            if (remoteTenant != null) {
                remoteTenantService.removeTenant(remoteTenant.getId());

                pluginScheduler.stopTask(remoteTenant);
                Security.setAttribute(request, "tenantId", null);
                return MessageBlock.createSingle(mav, MSG.TENANT_REMOVED, REMOVE_TENANT, MSG.SUCCESS);
            }

            return MessageBlock.createSingle(mav, MSG.TENANT_REMOVE_FAILED, REMOVE_TENANT, MSG.ERROR);
        } else {
            return MessageBlock.createSingle(mav, MSG.NOT_SIGNED_IN, REMOVE_TENANT, MSG.ERROR);
        }
    }

    public ModelAndView resettenant(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        String tenantId = ServletUtils.getSafeParameter(request, "tenantId", "");
        String operatorId = ServletUtils.getSafeParameter(request, "operatorId", "");

        ModelAndView mav = new ModelAndView("page");

        mav.addObject("title", "easyrec :: administration");

        mav.addObject("operatorId", operatorId);
        mav.addObject("tenantId", tenantId);

        if (Security.isDeveloper(request)) {
            RemoteTenant remoteTenant = remoteTenantDAO.get(operatorId, tenantId);

            if (remoteTenant != null)
                try {
                    itemAssocService.removeAllItemAssocsFromTenant(remoteTenant.getId());
                    actionService.removeActionsByTenant(remoteTenant.getId());
                    backTrackingDAO.clear(remoteTenant.getId());
                    remoteTenantService.resetTenant(remoteTenant.getId());
                    return MessageBlock.createSingle(mav, MSG.TENANT_RESET, RESET_TENANT, MSG.SUCCESS);
                } catch (Exception e) {
                    logger.debug(e);
                }

            return MessageBlock.createSingle(mav, MSG.TENANT_RESET_FAILED, RESET_TENANT, MSG.ERROR);
        } else {
            return MessageBlock.createSingle(mav, MSG.NOT_SIGNED_IN, RESET_TENANT, MSG.ERROR);
        }
    }

    public ModelAndView viewalltenants(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        String tenantId = ServletUtils.getSafeParameter(request, "tenantId", "");
        String operatorId = ServletUtils.getSafeParameter(request, "operatorId", "");

        int siteNumber = ServletUtils.getSafeParameter(request, "siteNumber", 0);
        boolean filterDemoTenants = (ServletUtils.getSafeParameter(request, "filterDemoTenants", 1) == 1);

        ModelAndView mav = new ModelAndView("page");

        mav.addObject("title", "easyrec :: administration");

        mav.addObject("operatorId", operatorId);
        mav.addObject("tenantId", tenantId);
        mav.addObject("filterDemoTenants", filterDemoTenants);


        if (Security.isDeveloper(request)) {
            PageStringGenerator psg = new PageStringGenerator(
                    request.getRequestURL() + "?" + request.getQueryString());

            List<RemoteTenant> remoteTenants = remoteTenantDAO.getTenants(siteNumber * psg.getNumberOfItemsPerPage(),
                    psg.getNumberOfItemsPerPage(), filterDemoTenants);

            if (remoteTenants.isEmpty()) {
                remoteTenants = remoteTenantDAO.getTenants(siteNumber * psg.getNumberOfItemsPerPage(),
                        psg.getNumberOfItemsPerPage(), false);
                mav.addObject("filterDemoTenants", false);
            }

            List<Integer> runningTenants = logEntryDAO.getRunningTenants();
            Integer runningTenantId = runningTenants.size() > 0 ? runningTenants.get(0) : null;

            mav.addObject("runningTenantId", runningTenantId);

            int remoteTenantsTotal = remoteTenantDAO.count();
            mav.addObject("remoteTenantsTotal", remoteTenantsTotal);
            mav.addObject("pageMenuString", psg.getPageMenuString(remoteTenantsTotal, siteNumber));

            mav.setViewName("dev/page");
            mav.addObject("page", "viewalltenants");
            mav.addObject("dbname", remoteTenantDAO.getDbName());
            mav.addObject("remoteTenants", MyUtils.sizeOf(remoteTenants) > 0 ? remoteTenants : null);

            return mav;
        } else {
            return MessageBlock.createSingle(mav, MSG.NOT_SIGNED_IN, VIEW_ALL_TENANTS, MSG.ERROR);
        }
    }

    public ModelAndView viewtenants(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        String tenantId = ServletUtils.getSafeParameter(request, "tenantId", "");
        String operatorId = ServletUtils.getSafeParameter(request, "operatorId", "");

        ModelAndView mav = new ModelAndView("page");

        mav.addObject("title", "easyrec :: administration");

        mav.addObject("operatorId", operatorId);
        mav.addObject("tenantId", tenantId);

        if (Security.isDeveloper(request)) {
            List<RemoteTenant> remoteTenants = remoteTenantDAO.getTenantsFromOperator(operatorId);

            List<Integer> runningTenants = logEntryDAO.getRunningTenants();
            Integer runningTenantId = runningTenants.size() > 0 ? runningTenants.get(0) : null;

            mav.setViewName("dev/page");
            mav.addObject("page", "viewtenants");
            mav.addObject("remoteTenants", MyUtils.sizeOf(remoteTenants) > 0 ? remoteTenants : null);
            mav.addObject("dbname", remoteTenantDAO.getDbName());
            mav.addObject("runningTenantId", runningTenantId);

            return mav;
        } else {
            return MessageBlock.createSingle(mav, MSG.NOT_SIGNED_IN, VIEW_TENANTS, MSG.ERROR);
        }
    }

    @RequestMapping
    public ModelAndView viewpluginconfig(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        String tenantId = ServletUtils.getSafeParameter(request, "tenantId", "");
        String operatorId = ServletUtils.getSafeParameter(request, "operatorId", "");

        ModelAndView mav = new ModelAndView("page");

        mav.addObject("title", "easyrec :: administration");

        mav.addObject("operatorId", operatorId);
        mav.addObject("tenantId", tenantId);

        if (Security.isDeveloper(request)) {
            mav.setViewName("dev/page");
            mav.addObject("page", "viewpluginconfig");

            final RemoteTenant remoteTenant = remoteTenantDAO.get(operatorId, tenantId);

            if (remoteTenant != null) {
                Properties tenantConfig = tenantService.getTenantConfig(remoteTenant.getId());
                if (tenantConfig != null) {
                    mav.addObject("schedulerEnabled",
                            tenantConfig.getProperty(RemoteTenant.SCHEDULER_ENABLED));
                    mav.addObject("schedulerExecutionTime",
                            tenantConfig.getProperty(RemoteTenant.SCHEDULER_EXECUTION_TIME));
                    mav.addObject("backtrackingEnabled", tenantConfig.getProperty(RemoteTenant.BACKTRACKING));
                    mav.addObject("backtrackingURL", tenantConfig.getProperty(RemoteTenant.BACKTRACKING_URL));
                    mav.addObject("archivingEnabled", tenantConfig.getProperty(RemoteTenant.AUTO_ARCHIVER_ENABLED));
                    mav.addObject("archivingTime", tenantConfig.getProperty(RemoteTenant.AUTO_ARCHIVER_TIME_RANGE));
                    mav.addObject("maxActions", tenantConfig.getProperty(RemoteTenant.MAXACTIONS));
                    logger.info("DevController - view generators config - tenantConfig found!!!!!!!!!!!!!!!!");
                    mav.addObject("selectedPlugin", tenantConfig.getProperty(PluginRegistry.GENERATOR_PROP));
                } else {
                    mav.addObject("schedulerEnabled", "false");
                    mav.addObject("backtrackingEnabled", "false");
                    mav.addObject("pluginsActive", "false");
                    logger.info("DevController - view generators config - tenantConfig NOT found!!!!!!!!!!!!!!!!");
                }

                Map<String, Integer> assocTypes = assocTypeDAO.getMapping(remoteTenant.getId(), true);
                mav.addObject("assocTypes", assocTypes);

                Map<String, PluginId> activePlugins =
                        Maps.transformValues(assocTypes, new Function<Integer, PluginId>() {
                            @Override
                            public PluginId apply(Integer input) {
                                NamedConfiguration namedConfiguration =
                                        namedConfigurationDAO.readActiveConfiguration(remoteTenant.getId(), input);

                                return namedConfiguration != null ? namedConfiguration.getPluginId()
                                        : new PluginId("http://www.easyrec.org/plugins/ARM/",
                                        easyrecSettings.getVersion());
                            }
                        });
                mav.addObject("activePlugins", activePlugins);

                List<PluginVO> pluginList = pluginDAO.loadPluginInfos(LifecyclePhase.INITIALIZED.toString());
                mav.addObject("pluginList", pluginList);
            }

            mav.addObject("tenantId", tenantId);
            return mav;
        } else {
            return MessageBlock.createSingle(mav, MSG.NOT_SIGNED_IN, VIEW_PLUGIN_CONFIG, MSG.ERROR);
        }
    }

    public ModelAndView storebacktracking(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        String tenantId = ServletUtils.getSafeParameter(request, "tenantId", "");
        String operatorId = ServletUtils.getSafeParameter(request, "operatorId", "");
        String backtracking = ServletUtils.getSafeParameter(request, "backtracking", "");
        String backtrackingURL = ServletUtils.getSafeParameter(request, "backtrackingURL", null);

        ModelAndView mav = new ModelAndView("page");

        mav.addObject("title", "easyrec :: administration");

        mav.addObject("operatorId", operatorId);
        mav.addObject("tenantId", tenantId);

        if (Security.isDeveloper(request)) {
            RemoteTenant r = remoteTenantDAO.get(operatorId, tenantId);
            if (r != null) {

                if (!"".equals(backtracking)) {
                    tenantService.updateConfigProperty(r.getId(), RemoteTenant.BACKTRACKING, backtracking);
                    r.setBacktracking(backtracking);
                    shopRecommenderService.emptyCache();
                } 
                if (backtrackingURL != null) {
                    tenantService.updateConfigProperty(r.getId(), RemoteTenant.BACKTRACKING_URL, backtrackingURL);
                    r.setBackTrackingURL(backtrackingURL);
                    shopRecommenderService.emptyCache();
                }

            }
            return MessageBlock.createSingle(mav, MSG.TENANT_UPDATED, STORE_BACKTRACKING, MSG.SUCCESS);
        } else {
            return MessageBlock.createSingle(mav, MSG.NOT_SIGNED_IN, STORE_BACKTRACKING, MSG.ERROR);
        }
    }

    public ModelAndView storemaxactions(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        String tenantId = ServletUtils.getSafeParameter(request, "tenantId", "");
        String operatorId = ServletUtils.getSafeParameter(request, "operatorId", "");

        String maxactions = ServletUtils.getSafeParameter(request, "maxactions", "");


        ModelAndView mav = new ModelAndView("page");

        mav.addObject("title", "easyrec :: administration");

        mav.addObject("operatorId", operatorId);
        mav.addObject("tenantId", tenantId);

        if (Security.isDeveloper(request)) {

            RemoteTenant r = remoteTenantDAO.get(operatorId, tenantId);
            if (r != null) {
                tenantService.updateConfigProperty(r.getId(), RemoteTenant.MAXACTIONS, maxactions);
                r.setMaxActions(maxactions);
                shopRecommenderService.emptyCache();
                remoteTenantDAO.updateTenantInCache(r);

            }
            return MessageBlock.createSingle(mav, MSG.OPERATION_SUCCESSFUL, STORE_MAXACTIONS, MSG.SUCCESS);
        } else {
            return MessageBlock.createSingle(mav, MSG.NOT_SIGNED_IN, STORE_MAXACTIONS, MSG.ERROR);
        }
    }

    public ModelAndView scheduler(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        String tenantId = ServletUtils.getSafeParameter(request, "tenantId", "");
        String operatorId = ServletUtils.getSafeParameter(request, "operatorId", "");
        String enabled = ServletUtils.getSafeParameter(request, "enabled", "");
        String executionTime = ServletUtils.getSafeParameter(request, "executiontime", "");

        ModelAndView mav = new ModelAndView("page");

        mav.addObject("title", "easyrec :: administration");

        mav.addObject("operatorId", operatorId);
        mav.addObject("tenantId", tenantId);

        if (!Security.isDeveloper(request))
            return MessageBlock.createSingle(mav, MSG.NOT_SIGNED_IN, STORE_SCHEDULER, MSG.ERROR);

        RemoteTenant remoteTenant = remoteTenantDAO.get(operatorId, tenantId);

        if (remoteTenant != null) {
            remoteTenant.setSchedulingEnabled(enabled);
            remoteTenant.setSchedulerExecutionTime(executionTime);

            tenantService.storeTenantConfig(remoteTenant.getId(), remoteTenant.getTenantConfigProperties());

            if (!Strings.isNullOrEmpty(executionTime)) {
                pluginScheduler.updateTask(remoteTenant);
            }
        }

        return MessageBlock.createSingle(mav, MSG.PLUGIN_CONFIG_CHANGED, STORE_SCHEDULER, MSG.SUCCESS);
    }

    public ModelAndView storearchive(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        String tenantId = ServletUtils.getSafeParameter(request, "tenantId", "");
        String operatorId = ServletUtils.getSafeParameter(request, "operatorId", "");
        String archiving = ServletUtils.getSafeParameter(request, "archiving", "");
        String archivingtime = ServletUtils.getSafeParameter(request, "archivingtime", "");

        ModelAndView mav = new ModelAndView("page");

        mav.addObject("title", "easyrec :: administration");

        mav.addObject("operatorId", operatorId);
        mav.addObject("tenantId", tenantId);

        if (Security.isDeveloper(request)) {
            RemoteTenant r = remoteTenantDAO.get(operatorId, tenantId);
            if (r != null) {

                r.setAutoArchiving(archiving);
                if (!Strings.isNullOrEmpty(archivingtime)) {
                    r.setAutoArchiverTimeRange(archivingtime);
                }

                tenantService.storeTenantConfig(r.getId(), r.getTenantConfigProperties());

            }
            return MessageBlock.createSingle(mav, MSG.ARCHIVE_CONFIG_CHANGED, STORE_ARCHIVE, MSG.SUCCESS);
        } else {
            return MessageBlock.createSingle(mav, MSG.NOT_SIGNED_IN, STORE_ARCHIVE, MSG.ERROR);
        }
    }


    public ModelAndView viewpluginconfigdetails(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        String tenantId = ServletUtils.getSafeParameter(request, "tenantId", "");
        String operatorId = ServletUtils.getSafeParameter(request, "operatorId", "");
        int assocTypeId = ServletUtils.getSafeParameter(request, "assocTypeId", -1);

        ModelAndView mav = new ModelAndView("page");

        mav.addObject("title", "easyrec :: administration");

        mav.addObject("operatorId", operatorId);
        mav.addObject("tenantId", tenantId);
        mav.addObject("assocTypeId", assocTypeId);

        if (!Security.isDeveloper(request)) {
            return MessageBlock.createSingle(mav, MSG.NOT_SIGNED_IN, VIEW_PLUGIN_DETAILS, MSG.ERROR);
        }

        mav.setViewName("dev/pluginconfigdetails");
        mav.addObject("signedinOperatorId", Security.signedInOperatorId(request));
        RemoteTenant remoteTenant = remoteTenantDAO.get(operatorId, tenantId);
        String pluginIdStr = ServletUtils.getSafeParameter(request, "pluginId", "");
        if (assocTypeId < 0) {
            logger.info("invalid assoc type.");
            return mav;
        }
        Map<String, List<PluginParamDetails>> params = null;
        if (pluginIdStr.equals("")) {            
            namedConfigurationDAO.deactivateByAssocType(remoteTenant.getId(), assocTypeId);
            params = new HashMap<String, List<PluginParamDetails>>();
        } else {
            PluginId pluginId = PluginId.parsePluginId(pluginIdStr);
            logger.info("pluginId: " + pluginId);

            Properties pluginConfig = null;

            if (remoteTenant != null) {
                pluginConfig = tenantService.getTenantConfig(remoteTenant.getId());
            }

            try {
                Generator<GeneratorConfiguration, GeneratorStatistics> generator =
                        pluginRegistry.getGenerators().get(pluginId);

                if (generator == null) {
                    logger.info("no generator found!");
                    return mav;
                }

                // make sure the plugin is registered as a valid source for the tenant
                try {
                    typeMappingService.getIdOfSourceType(remoteTenant.getId(), pluginId.toString());
                } catch (IllegalArgumentException iae) {
                    tenantService.insertSourceTypeForTenant(remoteTenant.getId(), pluginId.toString());
                    logger.debug("added new sourceType");
                }

                List<NamedConfiguration> namedConfigurations =
                        namedConfigurationDAO.readConfigurations(remoteTenant.getId(), assocTypeId,
                            generator.getId());

                if (namedConfigurations.isEmpty()) {
                    GeneratorConfiguration defaultConfiguration = generator.newConfiguration();
                    defaultConfiguration.setAssociationType(
                            typeMappingService.getAssocTypeById(remoteTenant.getId(), assocTypeId));


                    // when there is no active configuration for <tenant, assocType> then set the newly created
                    // configuration as the active one
                    boolean isActive = namedConfigurationDAO.readActiveConfiguration(remoteTenant.getId(),
                            assocTypeId) == null;
                    // more intuitive semantics? select configuration -> is instantly active
                    isActive = true;

                    NamedConfiguration namedConfiguration =
                            new NamedConfiguration(remoteTenant.getId(), assocTypeId, generator.getId(),
                                defaultConfiguration.getConfigurationName(), defaultConfiguration, isActive);

                    int rowsModified = namedConfigurationDAO.createConfiguration(namedConfiguration);
                    if (rowsModified == 0) {
                        logger.error("could not store named configuration");
                        return mav;
                    }

                    namedConfigurations.add(namedConfiguration);
                } else {
                    // TODO currently a workaround for setting the first of the named configurations as active when the
                    // plugin changes
                    namedConfigurations.get(0).setActive(true);
                    namedConfigurationDAO.updateConfiguration(namedConfigurations.get(0));
                }

                params = Maps.newHashMapWithExpectedSize(namedConfigurations.size());
                String activeConfiguration = null;

                for (NamedConfiguration namedConfiguration : namedConfigurations) {
                    if (namedConfiguration.isActive()) activeConfiguration = namedConfiguration.getName();

                    final ConfigurationHelper configurationHelper =
                        new ConfigurationHelper(namedConfiguration.getConfiguration());
                    List<PluginParamDetails> parameters = Lists.newArrayList();
                    Set<String> paramNames = configurationHelper.getParameterNames();
                    List<String> orderedParameterNames = new ArrayList<String>(paramNames);
                    Collections.sort(orderedParameterNames, new Comparator<String>(){

                        @Override
                        public int compare(String o1, String o2) {
                            int diff = configurationHelper.getParameterDisplayOrder(o1) - configurationHelper.getParameterDisplayOrder(o2);
                            if (diff != 0) return diff;
                            return o1.compareTo(o2);
                        }
                    });
                    for (String param : orderedParameterNames) {
                        if (param.equals("associationType") || param.equals("configurationName")) continue;

                        PluginParamDetails pluginParamDetails = new PluginParamDetails(param,
                            configurationHelper.getParameterDisplayName(param),
                            configurationHelper.getParameterDescription(param),
                            configurationHelper.getParameterShortDescription(param),
                            configurationHelper.getParameterValue(param),
                            configurationHelper.getParameterStringValue(param),
                            configurationHelper.getParameterOptional(param));

                        parameters.add(pluginParamDetails);

                        // TODO check if sourceType needs to be treated in a similar way
                        if (param.equals("associationType")) {
                            try {
                                typeMappingService.getIdOfAssocType(remoteTenant.getId(),
                                    pluginParamDetails.getStringValue());
                            } catch (IllegalArgumentException iae) {
                                tenantService.insertAssocTypeForTenant(remoteTenant.getId(),
                                    pluginParamDetails.getStringValue());
                                logger.debug("added new assocType");
                            }
                        }
                    }

                    params.put(namedConfiguration.getName(), parameters);
                }
            

                mav.addObject("activeConfiguration", activeConfiguration);
                
                mav.addObject("generator", generator);

            /*
            pluginConfig = configurationHelper.getValuesAsProperties(pluginConfig, pluginId);
            pluginConfig.setProperty(PluginRegistry.GENERATOR_PROP, pluginId);
            tenantService.storeTenantConfig(remoteTenant.getId(), pluginConfig);
            remoteTenant.setGeneratorConfig(configuration);
            */
            } catch (Exception e) {
                logger.error("An error occurred trying to get the generator parameters", e);
            }
        }
        
        mav.addObject("paramList", params);
        return mav;
    }

    public ModelAndView storepluginconfig(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        String tenantId = ServletUtils.getSafeParameter(request, "tenantId", "");
        String operatorId = ServletUtils.getSafeParameter(request, "operatorId", "");
        String key = ServletUtils.getSafeParameter(request, "key", "");
        String value = ServletUtils.getSafeParameter(request, "value", "");
        String configurationName = ServletUtils.getSafeParameter(request, "configurationName", "");
        int assocTypeId = ServletUtils.getSafeParameter(request, "assocTypeId", -1);

        ModelAndView mav = new ModelAndView("page");

        mav.addObject("title", "easyrec :: administration");

        mav.addObject("operatorId", operatorId);
        mav.addObject("tenantId", tenantId);
        mav.addObject("assocTypeId", assocTypeId);
        mav.addObject("configurationName", configurationName);

        if (!Security.isDeveloper(request))
            return MessageBlock.createSingle(mav, MSG.NOT_SIGNED_IN, STORE_PLUGIN_CONFIG, MSG.ERROR);

        RemoteTenant remoteTenant = remoteTenantDAO.get(operatorId, tenantId);

        if (remoteTenant == null)
            return MessageBlock.createSingle(mav, MSG.PLUGIN_CONFIG_CHANGED, STORE_PLUGIN_CONFIG, MSG.ERROR);

        // skip configuration renaming
        if (key.equals("configurationName"))
            return MessageBlock.createSingle(mav, MSG.PLUGIN_CONFIG_CHANGED, STORE_PLUGIN_CONFIG, MSG.SUCCESS);

        if (key.equals("associationType"))
            try {
                typeMappingService.getIdOfAssocType(remoteTenant.getId(), value);
            } catch (IllegalArgumentException iae) {
                tenantService.insertAssocTypeForTenant(remoteTenant.getId(), value);
                logger.debug("added new assocType");
            }

        if (key.equals("sourceType"))
            try {
                typeMappingService.getIdOfSourceType(remoteTenant.getId(), value);
            } catch (IllegalArgumentException iae) {
                tenantService.insertSourceTypeForTenant(remoteTenant.getId(), value);
                logger.debug("added new sourceType");
            }

        String pluginIdStr = ServletUtils.getSafeParameter(request, "pluginId", "");
        PluginId pluginId = PluginId.parsePluginId(pluginIdStr);
        Properties pluginConfig = tenantService.getTenantConfig(remoteTenant.getId());

        try {
            Generator<GeneratorConfiguration, GeneratorStatistics> generator =
                    pluginRegistry.getGenerators().get(pluginId);

            if (generator == null)
                return MessageBlock.createSingle(mav, MSG.PLUGIN_CONFIG_CHANGED, STORE_PLUGIN_CONFIG, MSG.ERROR);

            NamedConfiguration namedConfiguration =
                    namedConfigurationDAO.readConfiguration(remoteTenant.getId(), assocTypeId, pluginId,
                            configurationName);

            if (namedConfiguration == null)
                return MessageBlock.createSingle(mav, MSG.PLUGIN_CONFIG_CHANGED, STORE_PLUGIN_CONFIG, MSG.ERROR);

            ConfigurationHelper configurationHelper =
                    new ConfigurationHelper(namedConfiguration.getConfiguration());

            MutablePropertyValues values = new MutablePropertyValues();
            values.addPropertyValue(key, value);
            BindingResult bindingResult = configurationHelper.setValues(values);

            if (!bindingResult.hasErrors())
                namedConfigurationDAO.updateConfiguration(namedConfiguration);
            else
                return MessageBlock.createSingle(mav, MSG.PLUGIN_PARAM_INVALID.replace(
                        bindingResult.getFieldError().getDefaultMessage()), STORE_PLUGIN_CONFIG, MSG.ERROR);
        } catch (Exception e) {
            logger.error("An error occurred storing the plugin configuration! " + e);
        }

        return MessageBlock.createSingle(mav, MSG.PLUGIN_CONFIG_CHANGED, STORE_PLUGIN_CONFIG, MSG.SUCCESS);
    }

    public ModelAndView storepluginsactive(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        String tenantId = ServletUtils.getSafeParameter(request, "tenantId", "");
        String operatorId = ServletUtils.getSafeParameter(request, "operatorId", "");
        String pluginsactive = ServletUtils.getSafeParameter(request, "pluginsactive", "");

        ModelAndView mav = new ModelAndView("page");

        mav.addObject("title", "easyrec :: administration");

        mav.addObject("operatorId", operatorId);
        mav.addObject("tenantId", tenantId);

        if (Security.isDeveloper(request)) {
            mav.setViewName("dev/page");
            RemoteTenant r = remoteTenantDAO.get(operatorId, tenantId);
            if (r != null) {
                tenantService.updateConfigProperty(r.getId(), PluginRegistry.PLUGINS_ENABLED_PROP, pluginsactive);
                r.setPlugins(pluginsactive);
                remoteTenantDAO.updateTenantInCache(r);
            }
            return MessageBlock.createSingle(mav, MSG.OPERATION_SUCCESSFUL, STORE_PLUGINS_ACTIVE, MSG.SUCCESS);
        } else {
            return MessageBlock.createSingle(mav, MSG.NOT_SIGNED_IN, STORE_PLUGINS_ACTIVE, MSG.ERROR);
        }
    }

}
