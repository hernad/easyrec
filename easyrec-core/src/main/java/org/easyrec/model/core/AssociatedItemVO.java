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
 * This class is a VO (valueobject/dataholder) for a SAT recommender <code>AssociatedItem</code>.
 * These VOs additionally contain a assocValue, how strong they are associated (to the given search <code>Item</code>).
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
public class AssociatedItemVO<I extends Comparable<I>, T extends Comparable<T>>
        implements Serializable {
    ////////////////////////////////////////////////////////////////////////
    // constants
    private static final long serialVersionUID = 3204706699448711622L;

    ////////////////////////////////////////////////////////////////////////
    // members    
    private ItemVO<I,T> item;
    private Double assocValue;
    private Integer itemAssocId;
    private T assocType;

    ////////////////////////////////////////////////////////////////////////
    // constructors
    public AssociatedItemVO(ItemVO<I,T> item) {
        this(item, null, null, null);
    }

    public AssociatedItemVO(ItemVO<I,T> item, Integer itemAssocId, T assocType) {
        this(item, null, itemAssocId, assocType);
    }

    public AssociatedItemVO(ItemVO<I,T> item, Double assocValue, Integer itemAssocId, T assocType) {
        this.setItem(item);
        this.setAssocValue(assocValue);
        this.setItemAssocId(itemAssocId);
        this.setAssocType(assocType);
    }

    public ItemVO<I,T> getItem() {
        return item;

    }

    public void setItem(ItemVO<I,T> item) {
        this.item = item;
    }

    public Double getAssocValue() {
        return assocValue;
    }

    public void setAssocValue(Double assocValue) {
        this.assocValue = assocValue;
    }

    public Integer getItemAssocId() {
        return itemAssocId;
    }

    public void setItemAssocId(Integer itemAssocId) {
        this.itemAssocId = itemAssocId;
    }

    public T getAssocType() {
        return assocType;
    }

    public void setAssocType(T assocType) {
        this.assocType = assocType;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(getClass().getSimpleName());
        s.append('@');
        s.append(Integer.toHexString(hashCode()));
        s.append("[item=");
        s.append(getItem());
        s.append(",assocValue=");
        s.append(assocValue);
        s.append(",itemAssocId=");
        s.append(itemAssocId);
        s.append(",assocType=");
        s.append(assocType);
        s.append("]");
        return s.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((assocType == null) ? 0 : assocType.hashCode());
        result = prime * result + ((assocValue == null) ? 0 : assocValue.hashCode());
        result = prime * result + ((item == null) ? 0 : item.hashCode());
        result = prime * result + ((itemAssocId == null) ? 0 : itemAssocId.hashCode());
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final AssociatedItemVO<I,T> other = (AssociatedItemVO<I,T>) obj;
        if (assocType == null) {
            if (other.assocType != null) return false;
        } else if (!assocType.equals(other.assocType)) return false;
        if (assocValue == null) {
            if (other.assocValue != null) return false;
        } else if (!assocValue.equals(other.assocValue)) return false;
        if (item == null) {
            if (other.item != null) return false;
        } else if (!item.equals(other.item)) return false;
        if (itemAssocId == null) {
            if (other.itemAssocId != null) return false;
        } else if (!itemAssocId.equals(other.itemAssocId)) return false;
        return true;
    }
}
