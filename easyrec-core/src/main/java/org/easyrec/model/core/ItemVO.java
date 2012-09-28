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
package org.easyrec.model.core;

import java.io.Serializable;

/**
 * This class is a VO (valueobject/dataholder) for an easyrec <code>Item</code>.
 * All typed attributes use a different set of integer ids for each type.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: sat-rsa $<br/>
 * $Date: 2012-02-23 16:01:50 +0100 (Thu, 23 Feb 2012) $<br/>
 * $Revision: 140 $</p>
 *
 * @author Roman Cerny
 */
public class ItemVO<I extends Comparable<I>, T extends Comparable<T>>
        implements Cloneable, Comparable<ItemVO<I,T>>, Serializable {
    ////////////////////////////////////////////////////////////////////////
    // constants
    private static final long serialVersionUID = 7797728706663981575L;

    ////////////////////////////////////////////////////////////////////////
    // members
    private I tenant;
    private I item;
    private T type;

    ////////////////////////////////////////////////////////////////////////
    // constructors

    // default constructor (for webservice)
    public ItemVO() {

    }

    public ItemVO(I tenant, I item, T type) {
        this.tenant = tenant;
        this.item = item;
        this.type = type;
    }

    ////////////////////////////////////////////////////////////////////////
    // public methods
    public I getItem() {
        return item;
    }

    public void setItem(I item) {
        this.item = item;
    }

    public I getTenant() {
        return tenant;
    }

    public void setTenant(I tenant) {
        this.tenant = tenant;
    }

    public T getType() {
        return type;
    }

    public void setType(T type) {
        this.type = type;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(getClass().getSimpleName());
        s.append('@');
        s.append(Integer.toHexString(hashCode()));
        s.append("[tenant=");
        s.append(tenant);
        s.append(",item=");
        s.append(item);
        s.append(",type=");
        s.append(type);
        s.append("]");
        return s.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((item == null) ? 0 : item.hashCode());
        result = prime * result + ((tenant == null) ? 0 : tenant.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final ItemVO<I,T> other = (ItemVO<I,T>) obj;
        if (item == null) {
            if (other.item != null) return false;
        } else if (!item.equals(other.item)) return false;
        if (tenant == null) {
            if (other.tenant != null) return false;
        } else if (!tenant.equals(other.tenant)) return false;
        if (type == null) {
            if (other.type != null) return false;
        } else if (!type.equals(other.type)) return false;
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ItemVO<I,T> clone() throws CloneNotSupportedException {
        return (ItemVO<I,T>) super.clone();
    }

    public int compareTo(ItemVO<I,T> that) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        int comparison = 0;

        if (this == that) {
            return EQUAL;
        }

        if (this.getTenant() != null) {
            comparison = this.getTenant().compareTo(that.getTenant());
            if (comparison != EQUAL) {
                return comparison;
            }
        } else if (that.getTenant() != null) {
            return BEFORE;
        }
        if (this.getItem() != null) {
            comparison = this.getItem().compareTo(that.getItem());
            if (comparison != EQUAL) {
                return comparison;
            }
        } else if (that.getItem() != null) {
            return BEFORE;
        }
        if (this.getType() != null) {
            comparison = this.getType().compareTo(that.getType());
            if (comparison != EQUAL) {
                return comparison;
            }
        } else if (that.getType() != null) {
            return BEFORE;
        }

        assert this.equals(that) : "compareTo(...) inconsistent with equals(...)";

        return EQUAL;
    }

}
