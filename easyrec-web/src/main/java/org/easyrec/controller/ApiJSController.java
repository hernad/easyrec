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

import org.easyrec.model.web.Operator;
import org.easyrec.model.web.RemoteTenant;
import org.easyrec.utils.Security;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This Controller handles the JS API.
 * The JS API communicates with the REST-API through AJAX request from
 * a javacsript placed in the current website.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: pmarschik $<br/>
 * $Date: 2011-07-19 16:10:01 +0200 (Di, 19 Jul 2011) $<br/>
 * $Revision: 18517 $</p>
 *
 * @author dmann
 * @version 1.0
 * @since 1.0
 */
public class ApiJSController extends MultiActionController {


    private ModelAndView security(HttpServletRequest request) {

        ModelAndView mav = new ModelAndView("page");

        String apiKey = Operator.DEFAULT_API_KEY;
        String tenantId = RemoteTenant.DEFAULT_TENANT_ID;

        Operator signedInOperator = Security.signedInOperator(request);
        if (signedInOperator != null) {
            apiKey = signedInOperator.getApiKey();
        }

        mav.addObject("apiKey", apiKey);
        mav.addObject("tenant", tenantId);
        mav.addObject("selectedMenu", "api");
        mav.addObject("signedIn", Security.isSignedIn(request));

        return mav;
    }

    public ModelAndView easyrec(HttpServletRequest request, HttpServletResponse httpServletResponse) {

        ModelAndView mav = security(request);

        // cache for 24 hours
        httpServletResponse.addHeader("Cache-Control", "max-age=86400");
        mav.setViewName("api-js/easyrec");
        return mav;
    }

}