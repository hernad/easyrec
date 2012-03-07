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
package org.easyrec.rest;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author szavrel
 */
@XmlRootElement(name = "easyrec")
//@XmlType(propOrder={"tenantId","action","userid","sessionid","ratingValue","item"})
public class ResponseRule {

    private String tenantId;
    private String action;
    private String itemfromid;
    private String itemtoid;
    private String assoctype;
    private String assocvalue;

    public ResponseRule() {

    }

    public ResponseRule(String tenantId, String action, String itemfromid, String itemtoid, String assoctype,
                        String assocvalue) {
        this.tenantId = tenantId;
        this.action = action;
        this.itemfromid = itemfromid;
        this.itemtoid = itemtoid;
        this.assoctype = assoctype;
        this.assocvalue = assocvalue;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAssoctype() {
        return assoctype;
    }

    public void setAssoctype(String assoctype) {
        this.assoctype = assoctype;
    }

    public String getAssocvalue() {
        return assocvalue;
    }

    public void setAssocvalue(String assocvalue) {
        this.assocvalue = assocvalue;
    }

    public String getItemfromid() {
        return itemfromid;
    }

    public void setItemfromid(String itemfromid) {
        this.itemfromid = itemfromid;
    }

    public String getItemtoid() {
        return itemtoid;
    }

    public void setItemtoid(String itemtoid) {
        this.itemtoid = itemtoid;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

}
