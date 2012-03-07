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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @author szavrel
 */
@XmlRootElement(name = "easyrec")
//@XmlType(propOrder={"tenantId", "action", "userid", "sessionid", "item", "recommendedItems"})
public class Recommendation /*extends ResponseItem */ {

    private String tenantId;
    private String action;
    private String userid;
    private String sessionid;
    private Item item;
    private List<Item> recommendedItems;

    public Recommendation() {

    }

    /*
     * TODO: SZ -> DOC plz
     */
    public Recommendation(String tenantId, String action, String userId, String sessionId, Item baseItem,
                          List<Item> recommendedItems) {

        //super(tenantId, action, userId, sessionId, baseItem);
        this.action = action;
        this.item = baseItem;
        this.sessionid = sessionId;
        this.tenantId = tenantId;
        this.userid = userId;
        this.recommendedItems = recommendedItems;
    }

    @XmlElementWrapper(name = "recommendeditems")
    @XmlElementRef
    public List<Item> getRecommendedItems() {
        return recommendedItems;
    }

    public void setRecommendedItems(List<Item> recommendedItems) {
        this.recommendedItems = recommendedItems;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @XmlElement(name = "baseitem")
    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionId) {
        this.sessionid = sessionId;
    }

    @XmlElement(name = "tenantid")
    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userId) {
        this.userid = userId;
    }


}
