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
package org.easyrec.service.web;

import org.easyrec.model.web.statistics.*;

import java.util.HashMap;

/**
 * This Class is a Service for adding extra statistical Information
 * to Tenants. e.g. Userstatistics, number of items, number of actions,...
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: phlavac $<br/>
 * $Date: 2010-04-05 21:45:48 +0200 (Mo, 05 Apr 2010) $<br/>
 * $Revision: 15919 $</p>
 *
 * @author Peter Hlavac
 */
public interface RemoteTenantService {
    /**
     * Updates the TenantStatistics for a given tenant.
     *
     * @param operatorId
     * @param tenantId
     */
    public void updateTenantStatistics(Integer tenantId);

    /**
     * Returns Tenant specific Statistics for the given Tenant
     *
     * @param tenantId
     * @return
     */
    public TenantStatistic getTenantStatistics(Integer tenantId);


    /**
     * Returns User specific Statistics for the given Tenant
     *
     * @param tenantId
     * @return
     */
    public UserStatistic getUserStatistics(Integer tenantId);

    /**
     * Returns RuleMiner specific Statistics for the given Tenant
     *
     * @param tenantId
     * @return
     */
    public RuleMinerStatistic getRuleMinerStatistics(Integer tenantId);


    /**
     * Returns Conversion specific Statistics for the given Tenant
     *
     * @param tenantId
     * @return
     */
    public ConversionStatistic getConversionStatistics(Integer tenantId);


    /**
     * Get Assoc specficic Statistics for the given Tenant
     *
     * @param tenantId
     * @return
     */
    public HashMap<String, AssocStatistic> getAssocStatistic(Integer tenantId);

    /**
     * Clears the statistic properties of the given tenant
     *
     * @param id
     */
    public void resetTenant(Integer tenantId);

    /**
     * Removes a tenant with all its dependencies (items, actions, ...)
     *
     * @param tenantId
     */
    public void removeTenant(Integer tenantId);
}
