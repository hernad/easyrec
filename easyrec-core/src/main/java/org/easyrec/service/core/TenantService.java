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
package org.easyrec.service.core;

import org.easyrec.model.core.TenantConfigVO;
import org.easyrec.model.core.TenantVO;

import java.util.List;
import java.util.Properties;

/**
 * This interface defines methods to access Actions.
 * In contrary to the most core interfaces this one is a non-typed interface.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: sat-rsa $<br/>
 * $Date: 2012-02-23 16:01:50 +0100 (Do, 23 Feb 2012) $<br/>
 * $Revision: 140 $</p>
 *
 * @author Roman Cerny
 */
public interface TenantService {
    // constants for all possible tenants (well known tenants; convenience constants)
    public final static String TENANT_TEST = "TEST";

    public TenantVO getTenantById(Integer tenantId);

    public TenantVO getTenantByStringId(String stringId);

    public List<TenantVO> getAllTenants();

    public int insertTenantWithTypes(TenantVO tenant, TenantConfigVO tenantConfig);

    public void insertAssocTypeForTenant(Integer tenantId, String assocType);

    public int insertAssocTypeForTenant(Integer tenantId, String assocType, boolean visible);

    public void insertSourceTypeForTenant(Integer tenantId, String sourceType);

    public int insertItemTypeForTenant(Integer tenantId, String itemType, boolean visible);

    public boolean removeTenantWithTypes(TenantVO tenant);

    public int deactivateTenant(TenantVO tenant);

    public TenantConfigVO getDefaultTenantConfig();

    public Properties getTenantConfig(Integer tenantId);

    public int storeTenantConfig(Integer tenantId, Properties tenantConfig);

    public void updateConfigProperty(Integer tenantId, String key, String value);

    public Properties getTenantStatistic(Integer tenantId);

    public int storeTenantStatistic(Integer tenantId, Properties tenantStatistic);

    public void updateTenantStatistic(Integer tenantId, String key, String value);
    
    public Boolean hasActionValue(Integer tenantId, String actionType);

}
