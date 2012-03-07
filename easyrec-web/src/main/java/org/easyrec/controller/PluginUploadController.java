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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyrec.model.plugin.PluginVO;
import org.easyrec.model.web.FileUploadBean;
import org.easyrec.plugin.container.PluginRegistry;
import org.easyrec.store.dao.plugin.PluginDAO;
import org.easyrec.utils.Security;
import org.easyrec.utils.servlet.ServletUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.validation.BindException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * This controller provides file upload for the webapp.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2010</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: phlavac $<br/>
 * $Date: 2010-03-24 18:37:33 +0100 (Mi, 24 Mrz 2010) $<br/>
 * $Revision: 15881 $</p>
 *
 * @author Stephan Zavrel
 */
public class PluginUploadController extends SimpleFormController implements ApplicationContextAware {
    private final Log logger = LogFactory.getLog(this.getClass());

    private ApplicationContext appContext;
    private PluginRegistry pluginRegistry;
    private PluginDAO pluginDAO;

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command,
                                    BindException errors) throws Exception {

        String tenantId = ServletUtils.getSafeParameter(request, "tenantId", "");
        String operatorId = ServletUtils.getSafeParameter(request, "operatorId", "");

        logger.info("PluginUploadController: submit called");

        // cast the fubean
        FileUploadBean fubean = (FileUploadBean) command;
        if (fubean == null || fubean.getFile() == null || fubean.getFile().isEmpty()) {
            logger.info("no file or empty file was uploaded, aborting");
            // well, let's do nothing with the fubean for now and return
            return super.onSubmit(request, response, command, errors);
        }
        // check if there's content there
        MultipartFile file = fubean.getFile();
        PluginVO plugin = pluginRegistry.checkPlugin(file.getBytes());
        plugin.setOrigFilename(file.getOriginalFilename());
        pluginDAO.storePlugin(plugin);

        ModelAndView mav = new ModelAndView("dev/page");

        mav.addObject("title", "easyrec :: administration");

        mav.addObject("operatorId", operatorId);
        mav.addObject("tenantId", tenantId);
        mav.addObject("signedinOperatorId", Security.signedInOperatorId(request));

        mav.addObject("page", "plugins");
        List<PluginVO> plugins = pluginDAO.loadPlugins();
        mav.addObject("pluginList", plugins);


        return mav;

        // well, let's do nothing with the fubean for now and return
        //return super.onSubmit(request, response, command, errors);
    }

    public void setPluginRegistry(PluginRegistry pluginRegistry) {
        this.pluginRegistry = pluginRegistry;
    }

    public void setAppContext(ApplicationContext appContext) {
        this.appContext = appContext;
    }

    public void setPluginDAO(PluginDAO pluginDAO) {
        this.pluginDAO = pluginDAO;
    }

}
