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
package org.easyrec.store.dao.core;

import java.util.List;

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
 * $Author: sat-rsa $<br/>
 * $Date: 2011-08-12 16:46:14 +0200 (Fri, 12 Aug 2011) $<br/>
 * $Revision: 113 $</p>
 *
 * @author Stephan Zavrel
 */
public interface AuthenticationDAO {
    // constants
    public final static String DEFAULT_TABLE_NAME = "authentication";

    public final static String DEFAULT_TENANT_ID_COLUMN_NAME = "tenantId";
    public final static String DEFAULT_DOMAIN_URL_COLUMN_NAME = "domainURL";

    /**
     * Return a List of valid access domains for a given tenantId.
     *
     * @param tenantId id of the tenant to be authenticated
     * @return List of valid access domains
     */
    public List<String> getDomainURLsForTenant(Integer tenantId);

    /**
     * Returns a list of tenantIds that are allowed access from the given domain.
     *
     * @param domain the domain that is queried for valid tenants
     * @return List of valid tenantIds
     */
    public List<Integer> getTenantsForDomainURL(String domain);

    /**
     * Inserts a domain for a given tenant.
     *
     * @param tenantId
     * @param domain
     * @return
     */
    public int insertDomainURLForTenant(Integer tenantId, String domain);


}
