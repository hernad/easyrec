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
 * This interface provides methods to manage <code>SourceType</code> entries within an easyrec database.
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
public interface SourceTypeDAO extends TableCreatingDAO {
    // constants
    public final static String DEFAULT_TABLE_NAME = "sourcetype";

    public final static String DEFAULT_TENANT_COLUMN_NAME = "tenantId";
    public final static String DEFAULT_NAME_COLUMN_NAME = "name";
    public final static String DEFAULT_ID_COLUMN_NAME = "id";

    public final static String SOURCETYPE_AMG = "AMG";
    public final static String SOURCETYPE_UM = "UM";
    public final static String SOURCETYPE_RMG = "RMG";
    public final static String SOURCETYPE_FE = "FE";
    public final static String SOURCETYPE_MANUALLY_CREATED = "MANUALLY_CREATED";

    // methods
    public int insertOrUpdate(Integer tenantId, String sourceType, Integer id);

    public int insertOrUpdate(Integer tenantId, String sourceType);

    public String getTypeById(Integer tenantId, final Integer id);

    public Integer getIdOfType(Integer tenantId, final String sourceType);

    public HashMap<String, Integer> getMapping(Integer tenantId);

    public Set<String> getTypes(Integer tenantId);
}