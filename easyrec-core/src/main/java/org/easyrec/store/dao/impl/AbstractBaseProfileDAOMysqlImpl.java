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
package org.easyrec.store.dao.impl;

import org.easyrec.store.dao.BaseProfileDAO;
import org.easyrec.utils.spring.store.dao.annotation.DAO;
import org.easyrec.utils.spring.store.dao.impl.AbstractTableCreatingDAOImpl;
import org.easyrec.utils.spring.store.service.sqlscript.SqlScriptService;

import java.util.Set;

/**
 * @author szavrel
 */
@DAO
public abstract class AbstractBaseProfileDAOMysqlImpl<T, I, IT> extends AbstractTableCreatingDAOImpl
        implements BaseProfileDAO<T, I, IT> {

    // constants
    private final static String TABLE_CREATING_SQL_SCRIPT_NAME = "classpath:sql/core/Profile.sql";


    // constructor
    public AbstractBaseProfileDAOMysqlImpl(SqlScriptService sqlScriptService) {
        super(sqlScriptService);
    }

    @Override
    public String getDefaultTableName() {
        return DEFAULT_TABLE_NAME;
    }

    @Override
    public String getTableCreatingSQLScriptName() {
        return TABLE_CREATING_SQL_SCRIPT_NAME;
    }

    public abstract String getProfile(T tenantId, I itemId, IT itemTypeId);

    public abstract String getProfileById(Integer profileId);

    public abstract int storeProfile(T tenantId, I itemId, IT itemTypeId, String profileXML);

    public abstract int updateProfileById(Integer profileId, String profileXML);

    public abstract Set<String> getMultiDimensionValue(T tenantId, I itemId, IT itemTypeId, String dimensionXPath);

    public abstract Set<String> getMultiDimensionValue(Integer profileId, String dimensionXPath);

    public abstract String getSimpleDimensionValue(T tenantId, I itemId, IT itemTypeId, String dimensionXPath);

    public abstract String getSimpleDimensionValue(Integer profileId, String dimensionXPath);

}
