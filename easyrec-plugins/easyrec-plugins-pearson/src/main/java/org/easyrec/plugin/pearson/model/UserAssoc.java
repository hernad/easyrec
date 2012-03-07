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

package org.easyrec.plugin.pearson.model;

import org.easyrec.model.core.ItemVO;

import java.util.Date;

public class UserAssoc {
    private Double assocValue;
    private Date changeDate;
    private Integer id;
    private ItemVO<Integer, Integer> itemTo;
    private Integer sourceTypeId;
    private Integer tenantId;
    private Integer userFrom;

    public UserAssoc(Double assocValue, Date changeDate, Integer id, ItemVO<Integer, Integer> itemTo,
                     Integer sourceTypeId, Integer tenantId, Integer userFrom) {
        super();
        this.assocValue = assocValue;
        this.changeDate = changeDate;
        this.id = id;
        this.itemTo = itemTo;
        this.sourceTypeId = sourceTypeId;
        this.tenantId = tenantId;
        this.userFrom = userFrom;
    }

    public UserAssoc(Double assocValue, Date changeDate, ItemVO<Integer, Integer> itemTo, Integer sourceTypeId,
                     Integer tenantId, Integer userFrom) {
        super();
        this.assocValue = assocValue;
        this.changeDate = changeDate;
        this.itemTo = itemTo;
        this.sourceTypeId = sourceTypeId;
        this.tenantId = tenantId;
        this.userFrom = userFrom;
    }

    public Double getAssocValue() {
        return assocValue;
    }

    public Date getChangeDate() {
        return changeDate;
    }

    public Integer getId() {
        return id;
    }

    public ItemVO<Integer, Integer> getItemTo() {
        return itemTo;
    }

    public Integer getSourceTypeId() {
        return sourceTypeId;
    }

    public Integer getTenantId() {
        return tenantId;
    }

    public Integer getUserFrom() {
        return userFrom;
    }

    public void setAssocValue(Double assocValue) {
        this.assocValue = assocValue;
    }

    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setItemTo(ItemVO<Integer, Integer> itemTo) {
        this.itemTo = itemTo;
    }

    public void setSourceTypeId(Integer sourceTypeId) {
        this.sourceTypeId = sourceTypeId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }

    public void setUserFrom(Integer userFrom) {
        this.userFrom = userFrom;
    }

}
