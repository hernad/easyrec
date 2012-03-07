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
package org.easyrec.store.dao.domain.impl;

import org.easyrec.model.core.ItemVO;
import org.easyrec.service.domain.TypeMappingService;
import org.easyrec.store.dao.core.ProfileDAO;
import org.easyrec.store.dao.domain.TypedProfileDAO;
import org.easyrec.store.dao.impl.AbstractBaseProfileDAOMysqlImpl;
import org.easyrec.utils.spring.store.dao.DaoUtils;
import org.easyrec.utils.spring.store.dao.annotation.DAO;
import org.easyrec.utils.spring.store.service.sqlscript.SqlScriptService;

import javax.sql.DataSource;
import java.util.List;
import java.util.Set;

/**
 * @author szavrel
 */
@DAO
public class TypedProfileDAOMysqlImpl extends AbstractBaseProfileDAOMysqlImpl<Integer, Integer, String>
        implements TypedProfileDAO

{

    private ProfileDAO profileDAO;
    private TypeMappingService typeMappingService;

    // constructor
    public TypedProfileDAOMysqlImpl(DataSource dataSource, ProfileDAO profileDAO, TypeMappingService typeMappingService,
                                    SqlScriptService sqlScriptService) {
        super(sqlScriptService);
        setDataSource(dataSource);
        this.profileDAO = profileDAO;
        this.typeMappingService = typeMappingService;

        // output connection information
        if (logger.isInfoEnabled()) {
            try {
                logger.info(DaoUtils.getDatabaseURLAndUserName(dataSource));
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    @Override
    public String getProfile(Integer tenantId, Integer itemId, String itemTypeId, Boolean active) {

        return profileDAO.getProfile(tenantId, itemId, typeMappingService.getIdOfItemType(tenantId, itemTypeId), active);
    }
    
    @Override
    public String getProfile(Integer tenantId, Integer itemId, String itemTypeId) {

        return profileDAO.getProfile(tenantId, itemId, typeMappingService.getIdOfItemType(tenantId, itemTypeId));
    }

    @Override
    public int storeProfile(Integer tenantId, Integer itemId, String itemTypeId, String profileXML) {
        return profileDAO
                .storeProfile(tenantId, itemId, typeMappingService.getIdOfItemType(tenantId, itemTypeId), profileXML);
    }

    public void activateProfile(Integer tenantId, Integer itemId, String itemTypeId) {
        profileDAO.activateProfile(tenantId, itemId, typeMappingService.getIdOfItemType(tenantId, itemTypeId));
    }

    public void activateProfile(Integer profileId) {
        profileDAO.activateProfile(profileId);
    }

    public void deactivateProfile(Integer tenantId, Integer itemId, String itemTypeId) {
        profileDAO.deactivateProfile(tenantId, itemId, typeMappingService.getIdOfItemType(tenantId, itemTypeId));
    }

    public void deactivateProfile(Integer profileId) {
        profileDAO.deactivateProfile(profileId);
    }

    @Override
    public Set<String> getMultiDimensionValue(Integer tenantId, Integer itemId, String itemTypeId,
                                              String dimensionXPath) {
        return profileDAO
                .getMultiDimensionValue(tenantId, itemId, typeMappingService.getIdOfItemType(tenantId, itemTypeId),
                        dimensionXPath);
    }

    @Override
    public String getSimpleDimensionValue(Integer tenantId, Integer itemId, String itemTypeId, String dimensionXPath) {
        return profileDAO
                .getSimpleDimensionValue(tenantId, itemId, typeMappingService.getIdOfItemType(tenantId, itemTypeId),
                        dimensionXPath);
    }

    @Override
    public Set<String> getMultiDimensionValue(Integer profileId, String dimensionXPath) {
        return profileDAO.getMultiDimensionValue(profileId, dimensionXPath);
    }

    @Override
    public String getProfileById(Integer profileId) {
        return profileDAO.getProfileById(profileId);
    }

    @Override
    public String getSimpleDimensionValue(Integer profileId, String dimensionXPath) {
        return profileDAO.getSimpleDimensionValue(profileId, dimensionXPath);
    }

    @Override
    public int updateProfileById(Integer profileId, String profileXML) {
        return profileDAO.updateProfileById(profileId, profileXML);
    }

    public List<ItemVO<Integer, Integer>> getItemsByDimensionValue(Integer tenantId, String itemType,
                                                                            String dimensionXPath, String value) {
        return profileDAO.getItemsByDimensionValue(tenantId, typeMappingService.getIdOfItemType(tenantId, itemType),
                dimensionXPath, value);
    }

    public List<ItemVO<Integer, Integer>> getItemsByItemType(Integer tenantId, String itemType, int count) {
        return profileDAO.getItemsByItemType(tenantId, typeMappingService.getIdOfItemType(tenantId, itemType), count);
    }

}
