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
import org.easyrec.store.dao.core.types.ItemTypeDAO;
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
public class ItemTypeController extends MultiActionController {

    private ViewInitializationService viewInitializationService;
    private ItemTypeDAO itemTypeDAO;
    private RemoteTenantDAO remoteTenantDAO;
    private TenantService tenantService;

    public void setRemoteTenantDAO(RemoteTenantDAO remoteTenantDAO) {
        this.remoteTenantDAO = remoteTenantDAO;
    }

    public void setItemTypeDAO(ItemTypeDAO itemTypeDAO) {
        this.itemTypeDAO = itemTypeDAO;
    }

    public void setTenantService(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    public void setViewInitializationService(ViewInitializationService viewInitializationService) {
        this.viewInitializationService = viewInitializationService;
    }


    public String isValidItemTypeName(String itemTypeName) {

        if (itemTypeName.contains(" ")) {
            return "The item type name cannot contain spaces.";
        }

        if ("CLUSTER".equals(itemTypeName)) {
            return "CLUSTER is a reserved item type name used for the easyrec clusters.";
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
    public ModelAndView itemtypes(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        ModelAndView mav = new ModelAndView("easyrec/itemtypes");

        Operator signedInOperator = Security.signedInOperator(request);
        RemoteTenant remoteTenant = viewInitializationService.initializeView(request, mav);

        String itemTypeName = ServletUtils.getSafeParameter(request, "itemTypeName", "").toUpperCase();

        if (signedInOperator != null) {

            if (!"".equals(itemTypeName)) {
                //create a new item type if the itemTypeName parameter is set

                String error = isValidItemTypeName(itemTypeName);

                if ("".equals(error)) {
                    tenantService.insertItemTypeForTenant(remoteTenant.getId(), itemTypeName, true);
                } else {
                    mav.addObject("error", error);
                }
            }

            mav.addObject("apiKey", signedInOperator.getApiKey());
            mav.addObject("itemTypes", itemTypeDAO.getTypes(remoteTenant.getId(), true));
            mav.addObject("tenants", remoteTenantDAO.getTenantsFromOperator(remoteTenant.getOperatorId()));
        }


        return mav;
    }


}
