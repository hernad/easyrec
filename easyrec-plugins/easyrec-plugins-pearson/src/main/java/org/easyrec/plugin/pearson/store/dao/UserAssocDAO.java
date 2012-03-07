/*
 * Copyright 2011 Research Studios Austria Forschungsgesellschaft mBH
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

package org.easyrec.plugin.pearson.store.dao;

import org.easyrec.model.core.ItemVO;
import org.easyrec.plugin.pearson.model.UserAssoc;
import org.easyrec.utils.spring.store.dao.TableCreatingDAO;

import java.util.List;

/**
 * <DESCRIPTION>
 * <p/>
 * <p>
 * <b>Company:&nbsp;</b> SAT, Research Studios Austria
 * </p>
 * <p/>
 * <p>
 * <b>Copyright:&nbsp;</b> (c) 2007
 * </p>
 * <p/>
 * <p>
 * <b>last modified:</b><br/>
 * $Author$<br/>
 * $Date$<br/>
 * $Revision$
 * </p>
 *
 * @author Patrick Marschik
 */
public interface UserAssocDAO extends TableCreatingDAO {
    public static final String DEFAULT_ASSOC_VALUE_COLUMN_NAME = "assocValue";
    public static final String DEFAULT_CHANGE_DATE_COLUMN_NAME = "changeDate";
    public static final String DEFAULT_ITEM_TO_COLUMN_NAME = "itemToId";
    public static final String DEFAULT_ITEM_TO_TYPE_COLUMN_NAME = "itemToTypeId";
    public static final String DEFAULT_SOURCE_TYPE_COLUMN_NAME = "sourceTypeId";
    public static final String DEFAULT_TABLE_NAME = "p_userassoc";
    public static final String DEFAULT_TENANT_COLUMN_NAME = "tenantId";
    public static final String DEFAULT_USER_FROM_COLUMN_NAME = "userFromId";

    int deleteAlreadyVotedAssocs(Integer tenantId, Integer sourceTypeId);

    List<ItemVO<Integer, Integer>> getItemsAssociatedToUser(Integer tenantId, Integer userId,
                                                                     Integer itemTypeId, Integer sourceTypeId);

    int insertOrUpdateUserAssoc(UserAssoc userAssoc);
}
