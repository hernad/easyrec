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
import java.util.Date;

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
public class Rating implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 7109624825756504597L;

    private String tenant;
    private String itemId;
    private String itemType;
    private Double ratingValue;
    private Integer count;
    private Date lastActionTime;
    private String userId;
    private String sessionId;

    public Rating() {

    }

    public Rating(String tenant, String itemId, String itemType, Double ratingValue, Integer count,
                  Date lastActionTime) {
        this.tenant = tenant;
        this.itemId = itemId;
        this.itemType = itemType;
        this.ratingValue = ratingValue;
        this.count = count;
        this.lastActionTime = lastActionTime;
    }

    public Rating(String tenant, String itemId, String itemType, Double ratingValue, Integer count, Date lastActionTime,
                  String userId) {
        this(tenant, itemId, itemType, ratingValue, count, lastActionTime);
        this.userId = userId;
    }

    public Rating(String tenant, String itemId, String itemType, Double ratingValue, Integer count, Date lastActionTime,
                  String userId, String sessionId) {
        this(tenant, itemId, itemType, ratingValue, count, lastActionTime, userId);
        this.sessionId = sessionId;
    }

    public Rating(ItemVO<String, String> item, Double ratingValue, Integer count, Date lastActionTime) {
        this.tenant = item.getTenant();
        this.itemId = item.getItem();
        this.itemType = item.getType();
        this.ratingValue = ratingValue;
        this.count = count;
        this.lastActionTime = lastActionTime;
    }

    public Rating(ItemVO<String, String> item, Double ratingValue, Integer count, Date lastActionTime,
                  String userId) {
        this(item, ratingValue, count, lastActionTime);
        this.userId = userId;
    }

    public Rating(ItemVO<String, String> item, Double ratingValue, Integer count, Date lastActionTime,
                  String userId, String sessionId) {
        this(item, ratingValue, count, lastActionTime, userId);
        this.sessionId = sessionId;
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

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Date getLastActionTime() {
        return lastActionTime;
    }

    public void setLastActionTime(Date lastActionTime) {
        this.lastActionTime = lastActionTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Double getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(Double ratingValue) {
        this.ratingValue = ratingValue;
    }


}
