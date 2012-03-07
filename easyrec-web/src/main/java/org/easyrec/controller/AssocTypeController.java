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

package org.easyrec.controller;

import org.easyrec.model.web.Operator;
import org.easyrec.model.web.RemoteTenant;
import org.easyrec.service.core.TenantService;
import org.easyrec.service.web.ViewInitializationService;
import org.easyrec.store.dao.core.types.AssocTypeDAO;
import org.easyrec.store.dao.web.RemoteTenantDAO;
import org.easyrec.utils.Security;
import org.easyrec.utils.servlet.ServletUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This controller contains ItemType specific views to manage the itemTypes for a tenant.
 * User: dmann
 * Date: 16.05.11
 * Time: 14:37
 */
public class AssocTypeController extends MultiActionController {

    private ViewInitializationService viewInitializationService;
    private AssocTypeDAO assocTypeDAO;
    private RemoteTenantDAO remoteTenantDAO;
    private TenantService tenantService;

    public void setRemoteTenantDAO(RemoteTenantDAO remoteTenantDAO) {
        this.remoteTenantDAO = remoteTenantDAO;
    }

    public void setAssocTypeDAO(AssocTypeDAO assocTypeDAO) {
        this.assocTypeDAO = assocTypeDAO;
    }

    public void setTenantService(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    public void setViewInitializationService(ViewInitializationService viewInitializationService) {
        this.viewInitializationService = viewInitializationService;
    }


    public String isValidAssocTypeName(String itemTypeName) {

        if (itemTypeName.contains(" ")) {
            return "The assoc type name cannot contain spaces.";
        }

        if ("BELONGS_TO".equals(itemTypeName)) {
            return "BELONGS_TO is a reserved assoc type name used for the easyrec clusters.";
        }

        if ("IS_PARENT_OF".equals(itemTypeName)) {
            return "IS_PARENT_OF is a reserved assoc type name used for the easyrec clusters.";
        }

        if (!itemTypeName.equals(itemTypeName.replaceAll("[^A-Z_0-9]+", ""))) {
            return "Only use machine readable UPPERCASE names containing 0-9, A-Z and _ ";
        }

        return "";
    }


    /*
    * This view displays a list of all ItemTypes available.
    * If the parameter itemTypeName is set a new item type will be created.
    */
    public ModelAndView assoctypes(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        ModelAndView mav = new ModelAndView("easyrec/assoctypes");

        Operator signedInOperator = Security.signedInOperator(request);
        RemoteTenant remoteTenant = viewInitializationService.initializeView(request, mav);

        String assocTypeName = ServletUtils.getSafeParameter(request, "assocTypeName", "").toUpperCase();

        if (signedInOperator != null) {

            if (!"".equals(assocTypeName)) {
                //create a new item type if the itemTypeName parameter is set

                String error = isValidAssocTypeName(assocTypeName);

                if ("".equals(error)) {
                    tenantService.insertAssocTypeForTenant(remoteTenant.getId(), assocTypeName, true);
                } else {
                    mav.addObject("error", error);
                }
            }

            mav.addObject("apiKey", signedInOperator.getApiKey());
            mav.addObject("assocTypes", assocTypeDAO.getTypes(remoteTenant.getId(), true));
            mav.addObject("tenants", remoteTenantDAO.getTenantsFromOperator(remoteTenant.getOperatorId()));
        }


        return mav;
    }


}
