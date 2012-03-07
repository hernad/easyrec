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

package org.easyrec.plugin.itemitem.model;

import com.google.common.base.Objects;
import org.easyrec.model.core.ItemVO;

import java.util.Date;

/**
 * This class is a VO (valueobject/dataholder) for an easyrec Item-Item <code>UserAssoc</code>. All typed attributes use
 * a different set of integer ids for each type. <p/> <p> <b>Company:&nbsp;</b> SAT, Research Studios Austria </p> <p/>
 * <p> <b>Copyright:&nbsp;</b> (c) 2009 </p> <p/> <p> <b>last modified:</b><br/> $Author$<br/> $Date$<br/> $Revision$
 * </p>
 */
@SuppressWarnings({"UnusedDeclaration"})
public class UserAssoc implements Cloneable {
    // ------------------------------ FIELDS ------------------------------

    private Double assocValue;
    private Date changeDate;
    private Integer id;
    private ItemVO<Integer, Integer> itemTo;
    private Integer sourceTypeId;
    private Integer tenantId;
    private Integer userFrom;

    // --------------------------- CONSTRUCTORS ---------------------------

    public UserAssoc(Double assocValue, Date changeDate, ItemVO<Integer, Integer> itemTo, Integer sourceTypeId,
                     Integer tenantId, Integer userFrom) {
        this(null, assocValue, changeDate, itemTo, sourceTypeId, tenantId, userFrom);
    }

    public UserAssoc(Integer id, Double assocValue, Date changeDate, ItemVO<Integer, Integer> itemTo,
                     Integer sourceTypeId, Integer tenantId, Integer userFrom) {
        this.id = id;
        this.assocValue = assocValue;
        this.changeDate = changeDate;
        this.itemTo = itemTo;
        this.sourceTypeId = sourceTypeId;
        this.tenantId = tenantId;
        this.userFrom = userFrom;
    }

    // --------------------- GETTER / SETTER METHODS ---------------------

    public Double getAssocValue() { return assocValue; }

    public void setAssocValue(Double assocValue) { this.assocValue = assocValue; }

    public Date getChangeDate() { return changeDate; }

    public void setChangeDate(Date changeDate) { this.changeDate = changeDate; }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public ItemVO<Integer, Integer> getItemTo() { return itemTo; }

    public void setItemTo(ItemVO<Integer, Integer> itemTo) { this.itemTo = itemTo; }

    public Integer getSourceTypeId() { return sourceTypeId; }

    public void setSourceTypeId(Integer sourceTypeId) { this.sourceTypeId = sourceTypeId; }

    public Integer getTenantId() { return tenantId; }

    public void setTenantId(Integer tenantId) { this.tenantId = tenantId; }

    public Integer getUserFrom() { return userFrom; }

    public void setUserFrom(Integer userFrom) { this.userFrom = userFrom; }

    // ------------------------ CANONICAL METHODS ------------------------

    @Override
    public UserAssoc clone() throws CloneNotSupportedException {
        UserAssoc result = (UserAssoc) super.clone();
        result.itemTo = itemTo.clone();

        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAssoc)) return false;

        final UserAssoc userAssoc = (UserAssoc) o;

        if (assocValue != null ? !assocValue.equals(userAssoc.assocValue) : userAssoc.assocValue != null) return false;
        if (changeDate != null ? !changeDate.equals(userAssoc.changeDate) : userAssoc.changeDate != null) return false;
        if (id != null ? !id.equals(userAssoc.id) : userAssoc.id != null) return false;
        if (itemTo != null ? !itemTo.equals(userAssoc.itemTo) : userAssoc.itemTo != null) return false;
        if (sourceTypeId != null ? !sourceTypeId.equals(userAssoc.sourceTypeId) : userAssoc.sourceTypeId != null)
            return false;
        if (tenantId != null ? !tenantId.equals(userAssoc.tenantId) : userAssoc.tenantId != null) return false;
        if (userFrom != null ? !userFrom.equals(userAssoc.userFrom) : userAssoc.userFrom != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = assocValue != null ? assocValue.hashCode() : 0;
        result = 31 * result + (changeDate != null ? changeDate.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (itemTo != null ? itemTo.hashCode() : 0);
        result = 31 * result + (sourceTypeId != null ? sourceTypeId.hashCode() : 0);
        result = 31 * result + (tenantId != null ? tenantId.hashCode() : 0);
        result = 31 * result + (userFrom != null ? userFrom.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).
                add("id", id).
                add("userFrom", userFrom).
                add("itemTo", itemTo).
                add("assocValue", assocValue).
                add("changeDate", changeDate).
                add("sourceTypeId", sourceTypeId).
                add("tenantId", tenantId).toString();
    }
}
