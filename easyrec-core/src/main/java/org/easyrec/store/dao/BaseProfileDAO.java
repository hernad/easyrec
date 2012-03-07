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

import org.easyrec.model.core.ItemVO;
import org.easyrec.utils.spring.store.dao.TableCreatingDAO;

import java.util.List;
import java.util.Set;

/**
 * This interface provides methods to store data into and read <code>Profile</code> entries from an easyrec database.
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
 * @author Stephan Zavrel
 */
public interface BaseProfileDAO<T, I, IT> extends TableCreatingDAO {

    ///////////////////////////////////////////////////////////////////////////
    // constants
    public final static String DEFAULT_TABLE_NAME = "profile";

    public final static String DEFAULT_PROFILE_ID_COLUMN_NAME = "profileId";
    public final static String DEFAULT_TENANT_ID_COLUMN_NAME = "tenantId";
    public final static String DEFAULT_ITEM_ID_COLUMN_NAME = "itemId";
    public final static String DEFAULT_ITEM_TYPE_ID_COLUMN_NAME = "itemTypeId";
    public final static String DEFAULT_PROFILE_DATA_COLUMN_NAME = "profileData";
    public final static String DEFAULT_ACTIVE_COLUMN_NAME = "active";

    // abstract
    //public List<String> getProfileQBE(String exampleProfileXML);

    // non abstract
    public int storeProfile(T tenant, I item, IT itemType, String profileXML);

    public String getProfile(T tenant, I item, IT itemType);
    public String getProfile(T tenantId, I itemId, IT itemTypeId, Boolean active);

    public int updateProfileById(Integer profileId, String profileXML);

    public String getProfileById(Integer profileId);

    public void activateProfile(T tenant, I item, IT itemType);

    public void activateProfile(Integer profileId);

    public void deactivateProfile(T tenant, I item, IT itemType);

    public void deactivateProfile(Integer profileId);

    //    public int insertOrUpdateDimension(T tenant, I item, IT itemType, String dimensionXML);
    //    public String getDimension(T tenant, I item, IT itemType, String dimensionXPath);

    public Set<String> getMultiDimensionValue(T tenantId, I itemId, IT itemTypeId, String dimensionXPath);

    public Set<String> getMultiDimensionValue(Integer profileId, String dimensionXPath);

    public String getSimpleDimensionValue(T tenantId, I itemId, IT itemTypeId, String dimensionXPath);

    public String getSimpleDimensionValue(Integer profileId, String dimensionXPath);

    public List<ItemVO<Integer, Integer>> getItemsByDimensionValue(T tenantId, IT itemType,
                                                                            String dimensionXPath, String value);

    public List<ItemVO<Integer, Integer>> getItemsByItemType(T Tenant, IT itemType, int count);
}
