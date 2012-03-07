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
import org.easyrec.service.web.ViewInitializationService;
import org.easyrec.store.dao.web.RemoteTenantDAO;
import org.easyrec.utils.Security;
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
public class TypeManagerController extends MultiActionController {
    private ViewInitializationService viewInitializationService;
    private RemoteTenantDAO remoteTenantDAO;

    public void setViewInitializationService(ViewInitializationService viewInitializationService) {
        this.viewInitializationService = viewInitializationService;
    }

    public void setRemoteTenantDAO(RemoteTenantDAO remoteTenantDAO) {
        this.remoteTenantDAO = remoteTenantDAO;
    }

    /*
    * This view will show 3 tabs which will load the specific tab via ajax
    */
    public ModelAndView typemanager(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        ModelAndView mav = new ModelAndView("page");
        mav.addObject("page", "easyrec/typemanager");
        mav.addObject("menubar", "itemtypes");
        mav.addObject("selectedMenu", "myEasyrec");

        Operator signedInOperator = Security.signedInOperator(request);
        RemoteTenant remoteTenant = viewInitializationService.initializeView(request, mav);

        if (signedInOperator != null) {
            mav.addObject("apiKey", signedInOperator.getApiKey());
            mav.addObject("tenants", remoteTenantDAO.getTenantsFromOperator(remoteTenant.getOperatorId()));
        }

        return mav;
    }


}
