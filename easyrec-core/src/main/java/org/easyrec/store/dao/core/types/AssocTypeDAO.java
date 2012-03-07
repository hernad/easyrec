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
 * This interface provides methods to manage <code>AssocType</code> entries within an easyrec database.
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
public interface AssocTypeDAO extends TableCreatingDAO {
    // constants
    public final static String DEFAULT_TABLE_NAME = "assoctype";

    public final static String DEFAULT_TENANT_COLUMN_NAME = "tenantId";
    public final static String DEFAULT_NAME_COLUMN_NAME = "name";
    public final static String DEFAULT_ID_COLUMN_NAME = "id";
    public final static String DEFAULT_VISIBLE_COLUMN_NAME = "visible";

    public final static String ASSOCTYPE_VIEWED_TOGETHER = "VIEWED_TOGETHER";
    public final static String ASSOCTYPE_BOUGHT_TOGETHER = "BOUGHT_TOGETHER";
    public final static String ASSOCTYPE_SEARCHED_TOGETHER = "SEARCHED_TOGETHER";
    public final static String ASSOCTYPE_RATED_TOGETHER = "RATED_TOGETHER";
    public final static String ASSOCTYPE_GOOD_RATED_TOGETHER = "GOOD_RATED_TOGETHER";
    public final static String ASSOCTYPE_IS_ELEMENT_OF = "IS_ELEMENT_OF";
    public final static String ASSOCTYPE_LIKES = "LIKES";
    public final static String ASSOCTYPE_IS_SIMILAR_TO = "IS_SIMILAR_TO";
    public final static String ASSOCTYPE_SOUNDS_SIMILAR = "SOUNDS_SIMILAR";
    public final static String ASSOCTYPE_COLL_TOGETHER = "COLL_TOGETHER";
    public final static String ASSOCTYPE_IS_RELATED = "IS_RELATED";
    public final static String ASSOCTYPE_USER_TO_ITEM = "USER_TO_ITEM";

    // methods
    public int insertOrUpdate(Integer tenantId, String assocType, Integer id);

    public int insertOrUpdate(Integer tenantId, String assocType);

    public int insertOrUpdate(Integer tenantId, String assocType, Integer id, Boolean visible);

    public int insertOrUpdate(Integer tenantId, String assocType, Boolean visible);

    public String getTypeById(Integer tenantId, final Integer id);

    public Integer getIdOfType(Integer tenantId, final String assocType);

    public Integer getIdOfType(Integer tenantId, final String assocType, Boolean visible);

    public HashMap<String, Integer> getMapping(Integer tenantId);

    public HashMap<String, Integer> getMapping(Integer tenantId, Boolean visible);

    public Set<String> getTypes(Integer tenantId);

    public Set<String> getTypes(Integer tenantId, Boolean visible);

    public boolean isVisible(Integer tenantId, Integer id);

    public boolean isVisible(Integer tenantId, String assocType);
}