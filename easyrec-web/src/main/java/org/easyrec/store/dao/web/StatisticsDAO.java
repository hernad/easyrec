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
import org.easyrec.model.web.statistics.*;

import java.util.HashMap;

/**
 * This class computes statistics about Tenant, Items, Users actions.
 *
 * @author phlavac
 */
public interface StatisticsDAO {

    public AssocStatistic getAssocStatistics(int tenantId, int actionTypeId, int assocTypeId);

    /**
     * Return information about a tenant.
     *
     * @param RemoteTenant
     * @return
     */
    public TenantStatistic getTenantStatistics(RemoteTenant remoteTenant);

    /**
     * Return information about user statistics for a tenant
     *
     * @param tenantId
     * @return
     */
    public UserStatistic getUserStatistics(int tenantId);

    /**
     * Get Users Statistics for a given Tenant for the last X day.
     * The more day the longer the query need to execute.
     *
     * @param tenantId
     * @param days
     * @return
     */
    public UserStatistic getUserStatistics(int tenantId, int days);

    /**
     * Return Information about converstion rate for a tenantd
     *
     * @param tenantId
     * @return
     */
    public ConversionStatistic getConversionStatistics(Integer tenantId, Integer buyActionTypeId);


    /**
     * This function returns a map of ActionBundles. An ActionBundle stores
     * the number of actions for a given unit.
     * e.g. {"VIEW", {{1,5},{2,8},{6,30},{11,10}}},
     * {"BUY",  {{2,5},{6,18},{8,13},{15,17}}},
     * This can be interpreted as follows:
     * On day 1 there were 5 view-actions.
     * On day 2 there were 8 view- and 5 buy-actions.
     * ....
     * If the actionType Parameter is not null, only Action Bundles of the
     * given type are returned.
     *
     * @param tenant
     * @param from
     * @param to
     * @param actionType
     * @return
     */
    public HashMap<Integer, HashMap<Integer, Integer>> getActionBundleMap(int tenant, long from, long to,
                                                                          Integer actionType, Integer assocType);


    /**
     * Show the distribution of items with rules group by assocValue greater then
     * the given Parameters
     *
     * @param tenantId
     * @param minAssocValue1
     * @param minAssocValue2
     * @param minAssocValue3
     * @param minAssocValue4
     * @return
     */
    public RuleMinerStatistic getRuleMinerStatistics(Integer tenantId, Integer minAssocValue1, Integer minAssocValue2,
                                                     Integer minAssocValue3, Integer minAssocValue4);

    /**
     * Returns the number of actions for the current month
     *
     * @param tenantId
     * @return
     */
    public Integer getMonthlyActions(Integer tenantId);


}
