/*
 * Copyright 2011 Research Studios Austria Forschungsgesellschaft mBH
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
package org.easyrec.controller.clusterManager;

import org.easyrec.exception.core.ClusterException;
import org.easyrec.model.core.ClusterVO;
import org.easyrec.model.web.FileUploadBean;
import org.easyrec.model.web.Item;
import org.easyrec.model.web.RemoteTenant;
import org.easyrec.service.core.ClusterService;
import org.easyrec.service.web.ViewInitializationService;
import org.easyrec.store.dao.core.types.ItemTypeDAO;
import org.easyrec.store.dao.web.ItemDAO;
import org.easyrec.utils.io.tabular.input.InconsistentFieldCountException;
import org.easyrec.utils.io.tabular.input.impl.CsvInput;
import org.easyrec.utils.spring.store.dao.IDMappingDAO;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
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
 * $Author: szavrel $<br/>
 * $Date: 2011-07-26 18:06:43 +0200 (Di, 26 Jul 2011) $<br/>
 * $Revision: 18543 $</p>
 *
 * @author Stephan Zavrel
 */
public class ClusterImportController extends SimpleFormController {

    private ClusterService clusterService;
    private ItemDAO itemDAO;
    private ItemTypeDAO itemTypeDAO;
    private IDMappingDAO idMappingDAO;
    private ViewInitializationService viewInitializationService;

    public ClusterImportController(ClusterService clusterService, IDMappingDAO idMappingDAO, ItemDAO itemDAO, ItemTypeDAO itemTypeDAO,ViewInitializationService viewInitializationService) {
        this.clusterService = clusterService;
        this.idMappingDAO = idMappingDAO;
        this.itemDAO = itemDAO;
        this.itemTypeDAO = itemTypeDAO;
        this.viewInitializationService = viewInitializationService;
    }

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command,
                                    BindException errors) throws Exception {

        ModelAndView mav = new ModelAndView("page");

        RemoteTenant remoteTenant = viewInitializationService.initializeView(request, mav);

        FileUploadBean bean = (FileUploadBean) command; // cast the bean
        if (bean == null || bean.getFile() == null || bean.getFile().isEmpty()) {
            logger.info("no file or empty file was uploaded, aborting");
            // well, let's do nothing with the bean for now and return
            return super.onSubmit(request, response, command, errors);
        }

        // check if there's content there
        MultipartFile file = bean.getFile();

        CsvInput csvInput = new CsvInput(";", null);
        csvInput.setSource(file.getInputStream(), "UTF-8");

        List<String> validationErrors = new ArrayList<String>();
        List<String> touchedClusters = new ArrayList<String>();

        int lineNumber = 0;
        int numberOfItems = 0;
        int numberOfTouchedClusters = 0;
        int tenantId = 0;

        while (csvInput.hasNext()) {
            lineNumber++;

            List<String> fields = null;
            try {
                fields = csvInput.next(); //read one line
            } catch (InconsistentFieldCountException e) {
                validationErrors.add("ERROR in line number " + lineNumber + ": column numbers don't equal headerline columns!");
                continue;
            }

            if (lineNumber == 1) {
                continue;
            } // header line
            boolean lineError = false;

            if (fields.size() < 3) {
                validationErrors.add("ERROR in line number " + lineNumber + ": column numbers smaller then 3!");
                continue;
            }

            // init the CSV Columns to Java Variables
            String clusterName = fields.get(0);
            String itemId = fields.get(1);
            String itemType = fields.get(2);

            // the following code is used to validate the CSV line ...
            if (clusterName.equals("")) {
                validationErrors.add("ERROR in line number " + lineNumber + ": clusterName is missing.");
                lineError = true;
            }
            if (itemId.equals("")) {
                validationErrors.add("ERROR in line number " + lineNumber + ": itemId is missing.");
                lineError = true;
            }
            if (itemType.equals("")) {
                validationErrors.add("ERROR in line number " + lineNumber + ": itemType is missing.");
                lineError = true;
            }

            ClusterVO cluster = clusterService.loadCluster(remoteTenant.getId(), clusterName);

            if(cluster == null) {
                validationErrors.add("ERROR in line number " + lineNumber + ": cluster with name'" + clusterName + "' does not exist. Please create before importing!");
                lineError = true;
            }

            Integer itemTypeId = itemTypeDAO.getIdOfType(remoteTenant.getId(), itemType);
            Item item = itemDAO.get(remoteTenant, itemId, itemType);

            if (null == item) {
                validationErrors.add("ERROR in line number " + lineNumber + ": item with id '" + itemId + "' and type '" + itemType + "' does not exist.");
                lineError = true;
            }

            if (lineError) {
                continue;
            }



            Integer itemIdInt = idMappingDAO.lookup(item.getItemId());

            try {
                clusterService.addItemToCluster(remoteTenant.getId(), clusterName, itemIdInt, itemTypeId);
            } catch (ClusterException ex) {
                validationErrors.add("ERROR in line number " + lineNumber + ": " + ex.getMessage());
                continue;
            }

            numberOfItems++;

            if (!touchedClusters.contains(clusterName)) {
                numberOfTouchedClusters++;
                touchedClusters.add(clusterName);
            }
        }

        mav.addObject("tenant", remoteTenant);
        mav.addObject("page", "clustermanager/import/importResult");
        mav.addObject("numberOfItems", numberOfItems);
        mav.addObject("numberOfTouchedClusters", numberOfTouchedClusters);
        mav.addObject("validationErrors", validationErrors);
        return mav;
    }

    @Override
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws ServletException {
        // to actually be able to convert Multipart instance to byte[]
        // we have to register a custom editor
        binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
        // now Spring knows how to handle multipart object and convert them
    }

}
