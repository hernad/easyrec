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
import org.easyrec.model.core.TenantConfigVO;
import org.easyrec.model.core.TenantVO;
import org.easyrec.service.core.impl.TenantServiceImpl;
import org.easyrec.soap.service.AuthenticationService;
import org.easyrec.store.dao.core.AuthenticationDAO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Properties;

/**
 * Basic authentication Service that only authenticates the WebService request based on whether a valid
 * tenant is send or not.
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
public class TenantAuthenticationServiceImpl implements AuthenticationService {

    private TenantServiceImpl tenantService;
    private AuthenticationDAO authenticationDAO;

    // logging
    private final Log logger = LogFactory.getLog(this.getClass());

    public TenantAuthenticationServiceImpl(TenantServiceImpl tenantService) {
        this.tenantService = tenantService;
        this.authenticationDAO = tenantService.getAuthenticationDAO();
    }

    public Integer authenticateTenant(String tenant, HttpServletRequest request) {
        if (logger.isDebugEnabled()) logger.debug("Trying to authenticate tenant '" + tenant + "'");
        TenantVO tenantVO = getTenantByStringId(tenant);
        if (tenantVO != null) {
            return tenantVO.getId();
        } else return null;
    }

    public boolean authenticateTenant(Integer tenantId, HttpServletRequest request) {
        if (logger.isDebugEnabled()) logger.debug("Trying to authenticate tenantId '" + tenantId + "'");
        TenantVO tenantVO = getTenantById(tenantId);
        if (tenantVO != null) {
            return true;
        } else return false;
    }

    public List<Integer> authenticateDomain(String accessDomain) {
        return authenticationDAO.getTenantsForDomainURL(accessDomain);
    }

    public int deactivateTenant(TenantVO tenant) {
        return tenantService.deactivateTenant(tenant);
    }

    public TenantVO getTenantById(Integer tenantId) {
        return tenantService.getTenantById(tenantId);
    }

    public TenantVO getTenantByStringId(String stringId) {
        return tenantService.getTenantByStringId(stringId);
    }

    public int insertTenantWithTypes(TenantVO tenant, TenantConfigVO tenantConfig) {
        return tenantService.insertTenantWithTypes(tenant, tenantConfig);
    }

    public List<TenantVO> getAllTenants() {
        return tenantService.getAllTenants();
    }

    public boolean removeTenantWithTypes(TenantVO tenant) {
        return tenantService.removeTenantWithTypes(tenant);
    }

    public Properties getTenantConfig(Integer tenantId) {
        return tenantService.getTenantConfig(tenantId);
    }

    public int storeTenantConfig(Integer tenantId, Properties tenantConfig) {
        return tenantService.storeTenantConfig(tenantId, tenantConfig);
    }

}
