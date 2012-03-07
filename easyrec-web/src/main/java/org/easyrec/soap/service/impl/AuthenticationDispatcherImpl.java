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
package org.easyrec.soap.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyrec.soap.service.AuthenticationDispatcher;
import org.easyrec.soap.service.AuthenticationService;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <DESCRIPTION>
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: pmarschik $<br/>
 * $Date: 2011-02-11 11:04:49 +0100 (Fr, 11 Feb 2011) $<br/>
 * $Revision: 17656 $</p>
 *
 * @author Stephan Zavrel
 */
public class AuthenticationDispatcherImpl implements AuthenticationDispatcher {
    private Map<String, AuthenticationService> authenticationTypes;
    private AuthenticationService defaultAuthService;

    // logging
    private final Log logger = LogFactory.getLog(this.getClass());

    public AuthenticationDispatcherImpl(Map<String, AuthenticationService> authenticationTypes,
                                        AuthenticationService defaultAuthService) {

        this.authenticationTypes = authenticationTypes;
        this.defaultAuthService = defaultAuthService;
    }

    public Integer authenticateTenant(String tenant, String serviceName, HttpServletRequest request) {
        if (logger.isDebugEnabled()) {
            logger.debug("trying to authenticate: " + tenant + "." + serviceName);
        }

        AuthenticationService authService = authenticationTypes.get(tenant + "." + serviceName);
        if (authService == null) {
            authService = defaultAuthService;
        }
        return authService.authenticateTenant(tenant, request);
    }

    public Map<String, AuthenticationService> getAuthenticationTypes() {
        return authenticationTypes;
    }

    public void setAuthenticationTypes(Map<String, AuthenticationService> authenticationTypes) {
        this.authenticationTypes = authenticationTypes;
    }

    public AuthenticationService getDefaultAuthService() {
        return defaultAuthService;
    }

    public void setDefaultAuthService(AuthenticationService defaultAuthService) {
        this.defaultAuthService = defaultAuthService;
    }


}
