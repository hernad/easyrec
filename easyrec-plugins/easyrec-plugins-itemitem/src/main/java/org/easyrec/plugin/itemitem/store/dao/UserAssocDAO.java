/*
 * Copyright 2010 Research Studios Austria Forschungsgesellschaft mBH
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

package org.easyrec.plugin.itemitem.store.dao;

import org.easyrec.plugin.itemitem.model.UserAssoc;
import org.easyrec.utils.spring.store.dao.TableCreatingDroppingDAO;

/**
 * Stores associations user to item associations. <p/> <p> <b>Company:&nbsp;</b> SAT, Research Studios Austria </p> <p/>
 * <p> <b>Copyright:&nbsp;</b> (c) 2007 </p> <p/> <p> <b>last modified:</b><br/> $Author$<br/> $Date$<br/> $Revision$
 * </p>
 *
 * @author Patrick Marschik
 */
public interface UserAssocDAO extends TableCreatingDroppingDAO {
    // ------------------------------ FIELDS ------------------------------

    public static final String TABLE_NAME = "ii_userassoc";
    public static final String COLUMN_ASSOCVALUE = "assocValue";
    public static final String COLUMN_CHANGEDATE = "changeDate";
    public static final String COLUMN_ITEMTO = "itemToId";
    public static final String COLUMN_ITEMTOTYPE = "itemToTypeId";
    public static final String COLUMN_SOURCETYPE = "sourceTypeId";
    public static final String COLUMN_TENANT = "tenantId";
    public static final String COLUMN_USERFROM = "userFromId";

    // -------------------------- OTHER METHODS --------------------------

    /**
     * Inserts a user to item association (recommendation) or - if the association already existed - updated it.
     *
     * @param userAssoc User to item association to insert/update.
     * @return Number of added/modified rows.
     */
    int insertOrUpdateUserAssoc(UserAssoc userAssoc);
}
