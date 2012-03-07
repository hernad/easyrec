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
package org.easyrec.service.domain.profile;

import org.easyrec.model.core.ItemVO;

import java.util.List;
import java.util.Set;

/**
 * @author szavrel
 */
public interface ProfileService {

    public String getProfile(Integer tenantId, Integer itemId, String itemTypeId);

    public int storeProfile(Integer tenantId, Integer itemId, String itemTypeId, String profileXML, boolean validate);

    public void activateProfile(Integer tenantId, Integer itemId, String itemTypeId);

    public void deactivateProfile(Integer tenantId, Integer itemId, String itemTypeId);

    public String getProfileSchema(Integer tenantId, Integer id);

    public String getProfileSchema(Integer tenantId, String itemType);

    public String getSimpleDimensionValue(Integer tenantId, Integer itemId, String itemTypeId, String dimensionXPath);

    public Set<String> getMultiDimensionValue(Integer tenantId, Integer itemId, String itemTypeId,
                                              String dimensionXPath);

    public void insertOrUpdateSimpleDimension(Integer tenantId, Integer itemId, String itemTypeId,
                                              String dimensionXPath, String value);

    public void insertOrUpdateMultiDimension(Integer tenantId, Integer itemId, String itemTypeId, String dimensionXPath,
                                             List<String> values);

    public void loadSchemas();

    public List<ItemVO<Integer, Integer>> getItemsByDimensionValue(Integer tenantId, String itemType,
                                                                            String dimensionXPath, String value);

    public List<ItemVO<Integer, Integer>> getItemsByItemType(Integer tenantId, String itemType, int count);
}
