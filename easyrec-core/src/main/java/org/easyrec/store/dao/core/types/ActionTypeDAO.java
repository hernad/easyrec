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

import org.easyrec.utils.spring.store.dao.TableCreatingDAO;

import java.util.HashMap;
import java.util.Set;

/**
 * This interface provides methods to manage <code>ActionType</code> entries within an easyrec database.
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
 * @author Roman Cerny
 */
public interface ActionTypeDAO extends TableCreatingDAO {
    // constants
    public final static String DEFAULT_TABLE_NAME = "actiontype";

    public final static String DEFAULT_TENANT_COLUMN_NAME = "tenantId";
    public final static String DEFAULT_NAME_COLUMN_NAME = "name";
    public final static String DEFAULT_ID_COLUMN_NAME = "id";
    public final static String DEFAULT_HAS_VALUE_COLUMN_NAME = "hasvalue";

    // methods
    public int insertOrUpdate(Integer tenantId, String actionType);

    public int insertOrUpdate(Integer tenantId, String actionType, Boolean visible);
    
    public int insertOrUpdate(Integer tenantId, String actionType, Integer id);
    
    public int insertOrUpdate(Integer tenantId, String actionType, Integer id, boolean hasValue);

    public String getTypeById(Integer tenantId, final Integer id);

    public Integer getIdOfType(Integer tenantId, final String actionType);

    public HashMap<String, Integer> getMapping(Integer tenantId);

    public Set<String> getTypes(Integer tenantId);
    
    public Boolean hasValue(Integer tenantId, final String actionType );
}
