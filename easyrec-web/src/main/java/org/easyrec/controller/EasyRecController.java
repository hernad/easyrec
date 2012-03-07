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
package org.easyrec.controller;

import com.google.common.base.Strings;
import org.easyrec.model.web.Operator;
import org.easyrec.model.web.RemoteTenant;
import org.easyrec.store.dao.web.RemoteTenantDAO;
import org.easyrec.utils.Security;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.management.ManagementFactory;
import java.util.List;

/**
 * This Controller manages the tenants (create & view)
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: dmann $<br/>
 * $Date: 2011-05-20 11:34:51 +0200 (Fr, 20 Mai 2011) $<br/>
 * $Revision: 18345 $</p>
 *
 * @author phlavac
 * @version 1.0
 * @since 1.0
 */
public class EasyRecController extends MultiActionController {

    private RemoteTenantDAO remoteTenantDAO;

    public void setRemoteTenantDAO(RemoteTenantDAO remoteTenantDAO) {
        this.remoteTenantDAO = remoteTenantDAO;
    }

    private ModelAndView security(HttpServletRequest request, HttpServletResponse response) {
        String operatorId = null;
        ModelAndView mav = new ModelAndView("page");
        Operator signedInOperator = Security.signedInOperator(request);

        if (signedInOperator != null)
            operatorId = signedInOperator.getOperatorId();

        String parOperatorId = request.getParameter("operatorId");
        String parTenantId = request.getParameter("tenantId");

        if (parTenantId != null)
            Security.setAttribute(request, "tenantId", parTenantId);
        else
            parTenantId = RemoteTenant.DEFAULT_TENANT_ID;

        if (Strings.isNullOrEmpty(parOperatorId))
            parOperatorId = operatorId;

        mav.addObject("tenant", remoteTenantDAO.get(parOperatorId, parTenantId));

        List<RemoteTenant> remoteTenants = null;

        if (signedInOperator != null) {
            mav.addObject("apiKey", signedInOperator.getApiKey());
            mav.addObject("selectedMenu", "myEasyrec");
            mav.addObject("signedIn", Security.isSignedIn(request));
            remoteTenants = remoteTenantDAO.getTenantsFromOperator(signedInOperator.getOperatorId());
        }

        if (Security.isDeveloper(request) && request.getParameter("operatorId") != null) {
            operatorId = request.getParameter("operatorId");
            remoteTenants = remoteTenantDAO.getTenantsFromOperator(request.getParameter("operatorId"));
        }

        mav.addObject("operatorId", operatorId);
        mav.addObject("tenants", remoteTenants);

        Long memory = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax() / (1000000);
        mav.addObject("heapsize", memory);

        return mav;
    }

    public ModelAndView overview(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = security(request, response);
        mav.addObject("title", "easyrec :: management");
        mav.addObject("menubar", "tenant");

        if (Security.nullAttribute(request, "tenantId") ||
                RemoteTenant.DEFAULT_TENANT_ID.equals(Security.getAttribute(request, "tenantId"))) {
            mav.addObject("displayingDefaultTenant", true);
        }

        mav.addObject("page", "easyrec/tenant");
        mav.addObject("tenantId", (String) Security.getAttribute(request, "tenantId"));

        return mav;
    }

    public ModelAndView create(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("page");
        String operatorId = Security.signedInOperatorId(request);
        Operator operator = Security.signedInOperator(request);
        String operatorIdPar = request.getParameter("operatorId");

        if (Security.isDeveloper(request) || !Strings.isNullOrEmpty(operatorId))
            mav.addObject("operatorId", operatorIdPar);
        else
            mav.addObject("operatorId", operatorId);

        if (operator != null)
            mav.addObject("apiKey", operator.getApiKey());

        if (Security.isSignedIn(request)) {
            mav.addObject("title", "easyrec :: create tenant");
            mav.addObject("page", "easyrec/create");
            mav.addObject("create", true);
            mav.addObject("tenants", remoteTenantDAO.getTenantsFromOperator(operatorId));

            Security.setAttribute(request, "tenantId", null);
        } else {
            mav.addObject("page", "home");
        }


        return mav;
    }

    public ModelAndView update(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = security(request, response);

        mav.addObject("title", "easyrec :: update tenant");
        mav.addObject("page", "easyrec/update");
        mav.addObject("update", true);

        Security.setAttribute(request, "tenantId", null);

        return mav;
    }

    public ModelAndView tenant(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = security(request, response);

        mav.addObject("title", "easyrec :: management");
        mav.addObject("page", "easyrec/tenant");
        mav.addObject("menubar", "tenant");
        mav.addObject("tenantId", (String) Security.getAttribute(request, "tenantId"));

        return mav;
    }
}