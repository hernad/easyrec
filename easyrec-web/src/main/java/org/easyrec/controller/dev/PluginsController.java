/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.easyrec.controller.dev;

import com.google.common.collect.Maps;
import org.easyrec.model.plugin.PluginVO;
import org.easyrec.plugin.Progress;
import org.easyrec.plugin.container.PluginRegistry;
import org.easyrec.plugin.generator.Generator;
import org.easyrec.plugin.generator.GeneratorConfiguration;
import org.easyrec.plugin.model.PluginId;
import org.easyrec.plugin.model.Version;
import org.easyrec.plugin.stats.GeneratorStatistics;
import org.easyrec.store.dao.plugin.PluginDAO;
import org.easyrec.store.dao.web.RemoteTenantDAO;
import org.easyrec.utils.MessageBlock;
import org.easyrec.utils.Security;
import org.easyrec.utils.servlet.ServletUtils;
import org.easyrec.vocabulary.MSG;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

/**
 *
 * @author dmann
 */
public class PluginsController extends MultiActionController {

    private RemoteTenantDAO remoteTenantDAO;
    private PluginDAO pluginDAO;
    private PluginRegistry pluginRegistry;

    public void setRemoteTenantDAO(RemoteTenantDAO remoteTenantDAO) {
        this.remoteTenantDAO = remoteTenantDAO;
    }

    public void setPluginDAO(PluginDAO pluginDAO) {
        this.pluginDAO = pluginDAO;
    }

    public void setPluginRegistry(PluginRegistry pluginRegistry) {
        this.pluginRegistry = pluginRegistry;
    }
    private static final String VIEW_PLUGINS = "plugins";
    private static final String VIEW_PLUGIN_DESCR = "plugindescription";
    private static final String PLUGIN_UPLOAD = "pluginupload";
    private static final String PLUGIN_INSTALL = "plugininstall";
    private static final String PLUGIN_STOP = "pluginstop";
    private static final String PLUGIN_DELETE = "plugindelete";
    private static final String PLUGIN_CHANGESTATE = "pluginchangestate";

    public ModelAndView plugins(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        String tenantId = ServletUtils.getSafeParameter(request, "tenantId", "");
        String operatorId = ServletUtils.getSafeParameter(request, "operatorId", "");

        ModelAndView mav = new ModelAndView("page");

        mav.addObject("title", "easyrec :: administration");

        mav.addObject("operatorId", operatorId);
        mav.addObject("tenantId", tenantId);

        if (Security.isDeveloper(request)) {
            mav.setViewName("dev/page");
            mav.addObject("page", "plugins");
            mav.addObject("signedinOperatorId", Security.signedInOperatorId(request));

            List<PluginVO> plugins = pluginDAO.loadPlugins();
            Map<PluginId, Generator<GeneratorConfiguration, GeneratorStatistics>> generators = this.pluginRegistry
                    .getGenerators();
            //for each generator, fetch Progress and ExecutionState
            //put that into MAV
            //display

            Map<PluginId, String> executionStates = Maps.newHashMap();
            Map<PluginId, Progress> progresses = Maps.newHashMap();

            for (PluginId pluginID : generators.keySet()) {
                Generator<GeneratorConfiguration, GeneratorStatistics> generator = generators.get(pluginID);
                executionStates.put(pluginID, generator.getExecutionState().toString());
                progresses.put(pluginID, generator.getProgress());
            }

            mav.addObject("pluginList", plugins);
            mav.addObject("executionStates", executionStates);
            mav.addObject("progresses", progresses);
            return mav;
        } else {
            return MessageBlock.createSingle(mav, MSG.NOT_SIGNED_IN, VIEW_PLUGINS, MSG.ERROR);
        }
    }

    public ModelAndView pluginupload(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        String tenantId = ServletUtils.getSafeParameter(request, "tenantId", "");
        String operatorId = ServletUtils.getSafeParameter(request, "operatorId", "");

        ModelAndView mav = new ModelAndView("page");

        mav.addObject("title", "easyrec :: administration");

        mav.addObject("operatorId", operatorId);
        mav.addObject("tenantId", tenantId);

        if (Security.isDeveloper(request)) {

            mav.setViewName("dev/page");
            mav.addObject("page", "pluginupload");
            mav.addObject("tenantId", tenantId);
            mav.addObject("operatorId", operatorId);
            mav.addObject("signedinOperatorId", Security.signedInOperatorId(request));

            return mav;
        } else {
            return MessageBlock.createSingle(mav, MSG.NOT_SIGNED_IN, PLUGIN_UPLOAD, MSG.ERROR);
        }
    }

    public ModelAndView plugininstall(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        String tenantId = ServletUtils.getSafeParameter(request, "tenantId", "");
        String operatorId = ServletUtils.getSafeParameter(request, "operatorId", "");

        ModelAndView mav = new ModelAndView("page");

        mav.addObject("title", "easyrec :: administration");

        mav.addObject("operatorId", operatorId);
        mav.addObject("tenantId", tenantId);

        if (Security.isDeveloper(request)) {
            mav.setViewName("dev/page");
            mav.addObject("page", "plugins");
            mav.addObject("signedinOperatorId", Security.signedInOperatorId(request));
            String pluginId = ServletUtils.getSafeParameter(request, "pluginId", "");
            String versionStr = ServletUtils.getSafeParameter(request, "version", "");
            Version version = new Version(versionStr);

            logger.info("Called plugin install! " + pluginId + ", " + version);
            try {
                pluginRegistry.installPlugin(new URI(pluginId), version);
            } catch (URISyntaxException use) {
                // should never happen since the id has been checkec several times before in the workflow
                logger.error("PluginId is not a valid URI!");
            }

            return MessageBlock.createSingle(mav, MSG.PLUGIN_INSTALLED, PLUGIN_INSTALL, MSG.SUCCESS);
        } else {
            return MessageBlock.createSingle(mav, MSG.NOT_SIGNED_IN, PLUGIN_INSTALL, MSG.ERROR);
        }
    }

    public ModelAndView pluginchangestate(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        String tenantId = ServletUtils.getSafeParameter(request, "tenantId", "");
        String operatorId = ServletUtils.getSafeParameter(request, "operatorId", "");

        ModelAndView mav = new ModelAndView("page");

        mav.addObject("title", "easyrec :: administration");

        mav.addObject("operatorId", operatorId);
        mav.addObject("tenantId", tenantId);

        if (Security.isDeveloper(request)) {
            mav.setViewName("dev/page");
            mav.addObject("page", "plugins");
            String pluginId = ServletUtils.getSafeParameter(request, "pluginId", "");
            String versionStr = ServletUtils.getSafeParameter(request, "version", "");
            String checked = ServletUtils.getSafeParameter(request, "checked", ""); // Checkbox checked state

            if (checked.equalsIgnoreCase("false")) {
                pluginRegistry.deactivatePlugin(URI.create(pluginId), Version.parseVersion(versionStr));
            } else {
                pluginRegistry.installPlugin(URI.create(pluginId), Version.parseVersion(versionStr));
            }

            // this call is to refresh all tenant settings in the db
            remoteTenantDAO.getAllTenants();
            return MessageBlock.createSingle(mav, MSG.OPERATION_SUCCESSFUL, PLUGIN_CHANGESTATE, MSG.SUCCESS, "");
        } else {
            return MessageBlock.createSingle(mav, MSG.NOT_SIGNED_IN, PLUGIN_CHANGESTATE, MSG.ERROR);
        }
    }

    public ModelAndView plugindelete(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        String tenantId = ServletUtils.getSafeParameter(request, "tenantId", "");
        String operatorId = ServletUtils.getSafeParameter(request, "operatorId", "");

        ModelAndView mav = new ModelAndView("page");

        mav.addObject("title", "easyrec :: administration");

        mav.addObject("operatorId", operatorId);
        mav.addObject("tenantId", tenantId);

        if (Security.isDeveloper(request)) {

            mav.setViewName("dev/page");
            mav.addObject("page", "plugins");
            String pluginId = ServletUtils.getSafeParameter(request, "pluginId", "");
            String versionStr = ServletUtils.getSafeParameter(request, "version", "");
            //                String pluginIdToStop = pluginId + "/"+ versionStr;
            //
            //                Map<String, Generator<GeneratorConfiguration, GeneratorStatistics>> generators = this.pluginRegistry.getGenerators();

            pluginRegistry.deletePlugin(URI.create(pluginId), Version.parseVersion(versionStr));
            // this call is to refresh all tenant settings in the db
            remoteTenantDAO.getAllTenants();
            return MessageBlock.createSingle(mav, MSG.OPERATION_SUCCESSFUL, PLUGIN_DELETE, MSG.SUCCESS, "");
        } else {
            return MessageBlock.createSingle(mav, MSG.NOT_SIGNED_IN, PLUGIN_DELETE, MSG.ERROR);
        }
    }

    public ModelAndView pluginstop(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        String tenantId = ServletUtils.getSafeParameter(request, "tenantId", "");
        String operatorId = ServletUtils.getSafeParameter(request, "operatorId", "");
        String key = ServletUtils.getSafeParameter(request, "key", "");
        String value = ServletUtils.getSafeParameter(request, "value", "");
        String enabled = ServletUtils.getSafeParameter(request, "enabled", "");
        String executiontime = ServletUtils.getSafeParameter(request, "executiontime", "");
        String archiving = ServletUtils.getSafeParameter(request, "archiving", "");
        String archivingtime = ServletUtils.getSafeParameter(request, "archivingtime", "");
        String backtracking = ServletUtils.getSafeParameter(request, "backtracking", "");
        String maxactions = ServletUtils.getSafeParameter(request, "maxactions", "");
        String pluginsactive = ServletUtils.getSafeParameter(request, "pluginsactive", "");

        int siteNumber = ServletUtils.getSafeParameter(request, "siteNumber", 0);

        ModelAndView mav = new ModelAndView("page");

        mav.addObject("title", "easyrec :: administration");

        mav.addObject("operatorId", operatorId);
        mav.addObject("tenantId", tenantId);

        if (Security.isDeveloper(request)) {
            mav.setViewName("dev/page");
            mav.addObject("page", "plugins");
            String pluginId = ServletUtils.getSafeParameter(request, "pluginId", "");
            String versionStr = ServletUtils.getSafeParameter(request, "version", "");
            PluginId pluginIdToStop = new PluginId(pluginId, versionStr);

            Map<PluginId, Generator<GeneratorConfiguration, GeneratorStatistics>> generators = this.pluginRegistry
                    .getGenerators();
            Generator<GeneratorConfiguration, GeneratorStatistics> generator = generators.get(pluginIdToStop);
            generator.abort();

            return MessageBlock.createSingle(mav, MSG.OPERATION_SUCCESSFUL, "pluginstop", MSG.SUCCESS, "");
        } else {
            return MessageBlock.createSingle(mav, MSG.NOT_SIGNED_IN, PLUGIN_STOP, MSG.ERROR);
        }
    }

    public ModelAndView viewplugindescription(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        String tenantId = ServletUtils.getSafeParameter(request, "tenantId", "");
        String operatorId = ServletUtils.getSafeParameter(request, "operatorId", "");

        ModelAndView mav = new ModelAndView("page");

        mav.addObject("title", "easyrec :: administration");

        mav.addObject("operatorId", operatorId);
        mav.addObject("tenantId", tenantId);

        if (Security.isDeveloper(request)) {
            String pluginId = ServletUtils.getSafeParameter(request, "pluginId", "");
            String versionStr = ServletUtils.getSafeParameter(request, "version", "");

            String descr = pluginRegistry.getPluginDescription(URI.create(pluginId), Version.parseVersion(versionStr));
            mav.addObject("description", descr);

            return MessageBlock.createSingle(mav, MSG.OPERATION_SUCCESSFUL, VIEW_PLUGIN_DESCR, MSG.SUCCESS, descr);
        } else {
            return MessageBlock.createSingle(mav, MSG.NOT_SIGNED_IN, VIEW_PLUGIN_DESCR, MSG.ERROR);
        }

    }
}
