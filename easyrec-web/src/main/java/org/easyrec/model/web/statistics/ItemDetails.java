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
package org.easyrec.model.web.statistics;

/**
 * @author phlavac
 */
public class ItemDetails {
    private String itemId;
    private Integer itemType;
    private Integer tenantId;
    private String minActionTime;
    private String maxActionTime;
    private Integer actions;
    private Integer users;

    public ItemDetails(String itemId, Integer itemType, Integer tenantId, String minActionTime, String maxActionTime,
                       Integer actions, Integer users) {
        this.itemId = itemId;
        this.itemType = itemType;
        this.tenantId = tenantId;
        this.minActionTime = minActionTime;
        this.maxActionTime = maxActionTime;
        this.actions = actions;
        this.users = users;
    }

    public Integer getActions() {
        return actions;
    }

    public void setActions(Integer actions) {
        this.actions = actions;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public Integer getItemType() {
        return itemType;
    }

    public void setItemType(Integer itemType) {
        this.itemType = itemType;
    }

    public String getMaxActionTime() {
        return maxActionTime;
    }

    public void setMaxActionTime(String maxActionTime) {
        this.maxActionTime = maxActionTime;
    }

    public String getMinActionTime() {
        return minActionTime;
    }

    public void setMinActionTime(String minActionTime) {
        this.minActionTime = minActionTime;
    }

    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getUsers() {
        return users;
    }

    public void setUsers(Integer users) {
        this.users = users;
    }
}
