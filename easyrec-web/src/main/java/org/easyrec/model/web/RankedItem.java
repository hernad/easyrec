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
package org.easyrec.model.web;

import org.easyrec.model.core.ItemVO;

import java.io.Serializable;

/**
 * <DESCRIPTION>
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: dmann $<br/>
 * $Date: 2011-12-20 15:22:22 +0100 (Di, 20 Dez 2011) $<br/>
 * $Revision: 18685 $</p>
 *
 * @author Stephan Zavrel
 */
public class RankedItem implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1442629676113238760L;

    private String tenant;
    private String itemId;
    private String itemType;
    private String actionType;
    private Integer rank;
    private Integer count;

    public RankedItem() {

    }

    public RankedItem(ItemVO<String, String> item, String actionType, Integer rank, Integer count) {
        this.tenant = item.getTenant();
        this.itemId = item.getItem();
        this.itemType = item.getType();
        this.actionType = actionType;
        this.rank = rank;
        this.count = count;
    }

    public RankedItem(String tenant, String itemId, String itemType, String actionType, Integer rank, Integer count) {
        this.tenant = tenant;
        this.itemId = itemId;
        this.itemType = itemType;
        this.actionType = actionType;
        this.rank = rank;
        this.count = count;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
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


}
