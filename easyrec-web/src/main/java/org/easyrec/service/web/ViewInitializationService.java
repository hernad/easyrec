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

package org.easyrec.service.web;

import com.google.common.base.Strings;
import org.easyrec.model.web.Operator;
import org.easyrec.model.web.RemoteTenant;
import org.easyrec.store.dao.web.RemoteTenantDAO;
import org.easyrec.utils.Security;
import org.easyrec.utils.servlet.ServletUtils;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by IntelliJ IDEA.
 * User: dmann
 * Date: 16.05.11
 * Time: 14:42
 * To change this template use File | Settings | File Templates.
 */
public class ViewInitializationService {

    private RemoteTenantDAO remoteTenantDAO;

    public ViewInitializationService(RemoteTenantDAO remoteTenantDAO) {
        this.remoteTenantDAO = remoteTenantDAO;
    }

    /**
     * This function initializes the Easyrec Web applictaion variables for specific views.
     * Depending whenever the signed in user is a developer or not, this function allows the operator to impersonate
     * another operator and returns the corresponding remote tenant.
     *
     * This function was created due historical circumstances. The code was extracted from the Remote Tenant controller where
     * this code is used very often. Look into this controller tro understand the usage better - sorry
     * @param request
     * @param mav
     * @return
     */
    public RemoteTenant initializeView(HttpServletRequest request, ModelAndView mav) {
        RemoteTenant remoteTenant = null;

        String operatorId = ServletUtils.getSafeParameter(request, "operatorId", "");
        String tenantId = ServletUtils.getSafeParameter(request, "tenantId", "");

        String signedInOperatorId = Security.signedInOperatorId(request);
        Operator signedInOperator = Security.signedInOperator(request);

        if (signedInOperator != null) {
            if (Security.isDeveloper(request) && !Strings.isNullOrEmpty(operatorId))
                remoteTenant = remoteTenantDAO.get(operatorId, tenantId);
            else
                remoteTenant = remoteTenantDAO.get(signedInOperator.getOperatorId(), tenantId);

            if (remoteTenant != null)
                mav.addObject("remoteTenant", remoteTenant);
        }

        if (Security.isDeveloper(request) && !Strings.isNullOrEmpty(operatorId))
            mav.addObject("operatorId", operatorId);
        else
            mav.addObject("operatorId", signedInOperatorId);

        mav.addObject("tenantId", tenantId);



        return remoteTenant;
    }


}
