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
package org.easyrec.service.domain.profile.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyrec.model.core.ItemVO;
import org.easyrec.service.domain.profile.ProfileMatcherService;
import org.easyrec.service.domain.profile.ProfileService;
import org.easyrec.store.dao.core.types.ProfiledItemTypeDAO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author szavrel
 */
public class ProfileMatcherServiceImpl implements ProfileMatcherService {

    private Map<Integer, Map<String, ProfileMatcherService>> matchers;
    private ProfiledItemTypeDAO profiledItemTypeDAO;
    private ProfileService profileService;

    // logging
    private final Log logger = LogFactory.getLog(this.getClass());

    public ProfileMatcherServiceImpl(ProfiledItemTypeDAO profiledItemTypeDAO, ProfileService profileService) {
        this.profiledItemTypeDAO = profiledItemTypeDAO;
        this.profileService = profileService;
        matchers = new HashMap<Integer, Map<String, ProfileMatcherService>>();
        loadMatchers();
    }

    public float match(ItemVO<Integer, String> item1, ItemVO<Integer, String> item2) {
        float ret = -1;
        try {
            ret = matchers.get(item1.getTenant()).get(item1.getType()).match(item1, item2);
        } catch (NullPointerException npe) {
            if (logger.isDebugEnabled()) {
                logger.debug("Could not match profiles! There seem to be no matchers defined for the given itemTypes!" +
                        "TenantId: " + item1.getTenant() + " ItemTypeId1: " + item1.getType() + " ItemTypeId2: " +
                        item2.getType());
            }
        }
        return ret;
    }

    public void loadMatchers() {

        List<Integer> tenantIds = profiledItemTypeDAO.getTenantIds();
        logger.debug("Found " + tenantIds.size() + " tenants!");
        for (Integer tenantId : tenantIds) {
            Set<String> typesForTenant = profiledItemTypeDAO.getTypes(tenantId);
            Map<String, ProfileMatcherService> tenantMatchers = new HashMap<String, ProfileMatcherService>();
            for (String itemType : typesForTenant) {
                String XSLTString = profiledItemTypeDAO.getProfileMatcher(tenantId, itemType);
                tenantMatchers.put(itemType,
                        new TypeSpecificProfileMatcherImpl(profileService, tenantId, itemType, XSLTString));
                logger.debug("Loaded matcher: tenant " + tenantId + " matcher: " + XSLTString);
            }
            matchers.put(tenantId, tenantMatchers);

        }
    }

}
