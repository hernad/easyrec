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

package org.easyrec.plugin.slopeone.model;

import com.google.common.base.Objects;

/**
 * Stores the necessary information to hold the changed items.
 * <p/>
 * <p><b>Company:</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:</b>
 * (c) 2011</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: $<br/>
 * $Date: $<br/>
 * $Revision: $</p>
 *
 * @author pmarschik
 */
public class TenantItem {
    private int itemId;
    private int itemTypeId;

    public TenantItem(int itemId, int itemTypeId) {
        this.itemId = itemId;
        this.itemTypeId = itemTypeId;
    }

    public int getItemId() {
        return itemId;
    }

    public int getItemTypeId() {
        return itemTypeId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(itemId, itemTypeId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TenantItem that = (TenantItem) o;

        return Objects.equal(itemId, that.itemId) && Objects.equal(itemTypeId, that.itemTypeId);
    }
}
