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

package org.easyrec.model.core;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author szavrel
 */
@XmlRootElement
public class ClusterVO {

    private static final long serialVersionUID = 3790951809411794170L;

    @XmlTransient
    private ItemVO<Integer, Integer> item;
    private String name;
    private String description;

    // default constructor for JAXB
    public ClusterVO() {
    }

    public ClusterVO(Integer tenantId, Integer itemId, Integer itemTypeId, String name, String description) {
        item = new ItemVO<Integer,Integer>(tenantId, itemId, itemTypeId);
        this.name = name;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlTransient
    public ItemVO<Integer, Integer> getItem() {
        return item;
    }

    public void setItem(ItemVO<Integer, Integer> item) {
        this.item = item;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final ClusterVO other = (ClusterVO) obj;
        // two clusters are equal if their items are equal!
        return this.getItem().equals(other.getItem());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (this.item != null ? this.item.hashCode() : 0);
        return hash;
    }

}
