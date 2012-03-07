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

package org.easyrec.plugin.itemitem.store.dao.impl;

import org.easyrec.plugin.itemitem.model.UserAssoc;
import org.easyrec.plugin.itemitem.store.dao.UserAssocDAO;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Implementation of {@link UserAssocDAO} where the data is stored in-memory in a List. <p><b>Company:&nbsp;</b> SAT,
 * Research Studios Austria</p> <p><b>Copyright:&nbsp;</b> (c) 2007</p> <p><b>last modified:</b><br/> $Author$<br/>
 * $Date$<br/> $Revision$</p>
 *
 * @author Patrick Marschik
 */
public class UserAssocDAOMemoryImpl implements UserAssocDAO {
    // ------------------------------ FIELDS ------------------------------

    private List<UserAssoc> userAssocs;

    // --------------------------- CONSTRUCTORS ---------------------------

    public UserAssocDAOMemoryImpl() {
        userAssocs = new LinkedList<UserAssoc>();
    }

    // --------------------- GETTER / SETTER METHODS ---------------------

    public List<UserAssoc> getUserAssocs() { return userAssocs; }

    // ------------------------ INTERFACE METHODS ------------------------


    // --------------------- Interface TableCreatingDAO ---------------------

    public String getDefaultTableName() { return null; }

    public String getTableCreatingSQLScriptName() { return null; }

    public void createTable() {}

    public boolean existsTable() {return true;}

    // --------------------- Interface TableCreatingDroppingDAO ---------------------

    public void dropTable() { }

    // --------------------- Interface UserAssocDAO ---------------------

    public int insertOrUpdateUserAssoc(final UserAssoc userAssoc) {
        ListIterator<UserAssoc> it = userAssocs.listIterator();

        while (it.hasNext()) {
            UserAssoc ua = it.next();

            if (!userAssoc.getTenantId().equals(ua.getTenantId())) continue;
            if (!userAssoc.getUserFrom().equals(ua.getUserFrom())) continue;
            if (!userAssoc.getItemTo().getItem().equals(ua.getItemTo().getItem())) continue;
            if (!userAssoc.getItemTo().getType().equals(ua.getItemTo().getType())) continue;
            if (!userAssoc.getSourceTypeId().equals(ua.getSourceTypeId())) continue;

            it.set(ua);
            return 1;
        }

        userAssocs.add(userAssoc);

        return 1;
    }
}
