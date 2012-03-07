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
package org.easyrec.store.dao.web;

import org.easyrec.model.web.RemoteTenant;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * The Remote Tenant DAO gives access to the remote Tenants.
 * They are cached in a local Hashmap.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: dmann $<br/>
 * $Date: 2011-03-22 18:43:47 +0100 (Di, 22 Mär 2011) $<br/>
 * $Revision: 18003 $</p>
 *
 * @author <AUTHOR>
 */
public interface RemoteTenantDAO extends BasicDAO {
    public static final String DEFAULT_TABLE_NAME = "tenant";
    public static final String DEFAULT_TABLE_KEY = "id";

    public static final String DEFAULT_ID_COLUMN_NAME = "ID";
    public static final String DEFAULT_STRINGID_COLUMN_NAME = "STRINGID";
    public static final String DEFAULT_OPERATORID_COLUMN_NAME = "OPERATORID";
    public static final String DEFAULT_URL_COLUMN_NAME = "URL";
    public static final String DEFAULT_DESCRIPTION_COLUMN_NAME = "DESCRIPTION";
    public static final String DEFAULT_CREATIONDATE_COLUMN_NAME = "CREATIONDATE";
    public static final String DEFAULT_TENANT_CONFIG_COLUMN_NAME = "tenantConfig";
    public static final String DEFAULT_TENANT_STATISTIC_COLUMN_NAME = "tenantStatistic";


    /**
     * This function checks if the tenant id, submitted by the client,
     * is allowed to communicate with the recommender.
     *
     * @param tenantId
     * @param operatorId
     * @return
     */
    public boolean exists(String operatorId, String tenantId);

    /**
     * Removes a tenant from the remote recommender
     *
     * @param tenantId
     * @param operatorId
     */
    public void remove(String operatorId, String tenantId);

    /**
     * This function returns a tenant
     * with the given tenantid.
     *
     * @param tenantId
     * @param operatorId
     */
    public RemoteTenant get(String operatorId, String tenantId);

    /**
     * This function returns a tenant
     * with the given tenantid.
     *
     * @param tenantId
     */
    public RemoteTenant get(Integer tenantId);

    /**
     * This function returns a tenant for a given tenantid and operatorid
     * that is retrieved from the given request Object.
     *
     * @param request
     * @return
     */
    public RemoteTenant get(HttpServletRequest request);

    /**
     * This function updates the Url of a tenant.
     *
     * @param tenantId
     * @param operatorId
     * @param url
     * @param description
     */
    public void update(String operatorId, Integer tenantId, String url, String description);


    /**
     * Get a list of all tenants
     *
     * @return
     */
    public List<RemoteTenant> getAllTenants();

    /**
     * Get a list of all tenants for a given offset and limit,
     *
     * @param offset
     * @param limit
     * @return
     */
    public List<RemoteTenant> getTenants(int offset, int limit);


     /**
     * Get a list of all tenants for a given offset and limit,
     * with filterDemoTenants you can filter out the automatically
     * created demo tenants. This method is called by getTenants
     * which basically just sets  filterDemoTenants to false.
     *
     * @param offset is used as offset for paging
     * @param limit is used to set a limit on the returned List size. This parameter is also used for paging.
     * @param filterDemoTenants set this parameter to true if you want to filter out the auto generated
     *       Demo Tenants you will get the same amount of items but none of them will be a Demo Tenant.
      *      Set it to false to get the same response as you would get from filterDemoTenants.
     * @return
     */
    public List<RemoteTenant> getTenants(int offset, int limit,boolean filterDemoTenants);

    /**
     * Get a list of tenants that are assigned to an operator
     *
     * @param operatorId
     * @return
     */
    public List<RemoteTenant> getTenantsFromOperator(String operatorId);

    /**
     * updates Tenant from Cache
     */
    public void updateTenantInCache(RemoteTenant r);

    /**
     * Clears the statistic properties
     *
     * @param tenantId
     */
    public void reset(Integer tenantId);
}
