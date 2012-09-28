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
 * This class is a VO (valueobject/dataholder) for a SAT recommender <code>RankedItem</code>.
 * These VOs additionally contain a rank, describing the position in the queried ranking.
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
public class RankedItemVO<I extends Comparable<I>, T extends Comparable<T>>
        implements Serializable {
    ////////////////////////////////////////////////////////////////////////
    // constants
    private static final long serialVersionUID = -8206707517077421439L;

    ////////////////////////////////////////////////////////////////////////
    // members
    private ItemVO<I,T> item;
    private T actionType;
    private Integer rank;
    private Integer count;

    ////////////////////////////////////////////////////////////////////////
    // constructors
    // default constructor (for webservice)
    public RankedItemVO() {

    }

    public RankedItemVO(ItemVO<I,T> item) {
        this(item, null, null, null);
    }

    public RankedItemVO(ItemVO<I,T> item, T actionType, Integer rank, Integer count) {
        this.setItem(item);
        this.setActionType(actionType);
        this.setRank(rank);
        this.setCount(count);
    }

    ////////////////////////////////////////////////////////////////////////
    // public methods
    public ItemVO<I,T> getItem() {
        return item;
    }

    public void setItem(ItemVO<I,T> item) {
        this.item = item;
    }

    public T getActionType() {
        return actionType;
    }

    public void setActionType(T actionType) {
        this.actionType = actionType;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(getClass().getSimpleName());
        s.append('@');
        s.append(Integer.toHexString(hashCode()));
        s.append("[item=");
        s.append(getItem());
        s.append(",actionType=");
        s.append(actionType);
        s.append(",rank=");
        s.append(rank);
        s.append(",count=");
        s.append(count);
        s.append("]");
        return s.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((actionType == null) ? 0 : actionType.hashCode());
        result = prime * result + ((count == null) ? 0 : count.hashCode());
        result = prime * result + ((item == null) ? 0 : item.hashCode());
        result = prime * result + ((rank == null) ? 0 : rank.hashCode());
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final RankedItemVO<I,T> other = (RankedItemVO<I,T>) obj;
        if (actionType == null) {
            if (other.actionType != null) return false;
        } else if (!actionType.equals(other.actionType)) return false;
        if (count == null) {
            if (other.count != null) return false;
        } else if (!count.equals(other.count)) return false;
        if (item == null) {
            if (other.item != null) return false;
        } else if (!item.equals(other.item)) return false;
        if (rank == null) {
            if (other.rank != null) return false;
        } else if (!rank.equals(other.rank)) return false;
        return true;
    }
}
