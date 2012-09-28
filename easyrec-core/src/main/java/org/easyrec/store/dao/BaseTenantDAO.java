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
package org.easyrec.store.dao;

import org.easyrec.model.core.TenantVO;

import java.util.List;

/**
 * This interface provides methods to store data into and read <code>Tenant</code> entries from an easyrec database.
 * Provides base methods and constants.
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
 * @author Roman Cerny
 */
public interface BaseTenantDAO {
    ///////////////////////////////////////////////////////////////////////////
    // constants
    public final static String DEFAULT_TABLE_NAME = "tenant";

    public final static String DEFAULT_ID_COLUMN_NAME = "id";
    public final static String DEFAULT_STRING_ID_COLUMN_NAME = "stringId";
    public final static String DEFAULT_DESCRIPTION_COLUMN_NAME = "description";
    public final static String DEFAULT_RATING_RANGE_MIN_COLUMN_NAME = "ratingRangeMin";
    public final static String DEFAULT_RATING_RANGE_MAX_COLUMN_NAME = "ratingRangeMax";
    public final static String DEFAULT_RATING_RANGE_NEUTRAL_COLUMN_NAME = "ratingRangeNeutral";
    public final static String DEFAULT_ACTIVE_COLUMN_NAME = "active";
    public final static String DEFAULT_CONFIG_COLUMN_NAME = "tenantConfig";
    public final static String DEFAULT_STATISTIC_COLUMN_NAME = "tenantStatistic";

    ///////////////////////////////////////////////////////////////////////////
    // non-generic methods
    public TenantVO getTenantById(Integer tenantId);

    public TenantVO getTenantByStringId(String stringId);

    public List<TenantVO> getAllTenants();

    public String getTenantConfig(Integer tenantId);

    public int storeTenantConfig(Integer tenantId, String tenantConfig);

    public String getTenantStatistic(Integer tenantId);

    public int storeTenantStatistic(Integer tenantId, String tenantConfig);

    /**
     * Inserts a tenant into the database. The new tenant will automatically be assigned a new tenantId, any
     * value passed for this field in the TenantVO parameter will be ignored.
     *
     * @param tenant the TenantVO to be inserted
     * @return the id of the inserted tenant
     */
    public int insertTenant(TenantVO tenant);

    /**
     * Sets the active field of a tenant to the value given in the active parameter.
     *
     * @param tenant the tenant to be set active or inactive
     * @param active the value the active flag will be set to.
     * @return
     */
    public int setTenantActive(TenantVO tenant, boolean active);
}
