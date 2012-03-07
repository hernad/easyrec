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
package org.easyrec.store.dao.core.types;

import java.util.List;

/**
 * @author szavrel
 */
public interface ProfiledItemTypeDAO extends ItemTypeDAO {
    public final static String DEFAULT_PROFILE_SCHEMA_COLUMN_NAME = "profileSchema";
    public final static String DEFAULT_PROFILE_MATCHER_COLUMN_NAME = "profileMatcher";

    public int insertOrUpdate(Integer tenantId, String itemType, Integer id, String profileSchema,
                              String profileMatcher);

    public int insertOrUpdate(Integer tenantId, String itemType, Integer id, String profileSchema,
                              String profileMatcher, Boolean visible);

    public String getProfileSchema(Integer tenantId, String itemType);

    public String getProfileMatcher(Integer tenantId, String itemType);

    public String getProfileSchema(Integer tenantId, Integer id);

    public String getProfileMatcher(Integer tenantId, Integer id);

    public List<Integer> getTenantIds();

    public List<Integer> getItemTypeIds(Integer tenantId);

    public List<Integer> getItemTypeIds(Integer tenantId, Boolean visible);
}
