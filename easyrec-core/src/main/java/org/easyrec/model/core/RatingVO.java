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
import java.util.Date;

/**
 * This class is a VO (valueobject/dataholder) for a SAT recommender <code>Rating</code>.
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
public class RatingVO<I extends Comparable<I>, T extends Comparable<T>>
        implements Serializable {
    ////////////////////////////////////////////////////////////////////////
    // constants
    private static final long serialVersionUID = 167749371958590235L;

    //////////////////////////////////////////////////////////////////////////////
    // members
    private ItemVO<I,T> item;
    private Double ratingValue;
    private Integer count;
    private Date lastActionTime;
    private I user;
    private String sessionId;

    //////////////////////////////////////////////////////////////////////////////
    // public methods

    ////////////////////////////////////////////////////////////////////////
    // constructors
    // default constructor (for webservice)
    public RatingVO() {
        super();
    }

    public RatingVO(ItemVO<I,T> item, Double ratingValue, Integer count, Date lastActionTime) {
        this(item, ratingValue, count, lastActionTime, null);
    }


    public RatingVO(ItemVO<I,T> item, Double ratingValue, Integer count, Date lastActionTime, I user) {
        this(item, ratingValue, count, lastActionTime, user, null);
    }

    public RatingVO(ItemVO<I,T> item, Double ratingValue, Integer count, Date lastActionTime, I user,
                    String sessionId) {
        this.item = item;
        this.ratingValue = ratingValue;
        this.count = count;
        this.lastActionTime = lastActionTime;
        this.user = user;
        this.sessionId = sessionId;
    }

    // getter/setter
    public ItemVO<I,T> getItem() {
        return item;
    }

    public void setItem(ItemVO<I,T> item) {
        this.item = item;
    }

    public Double getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(Double ratingValue) {
        this.ratingValue = ratingValue;
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

    public void setUserId(I user) {
        this.user = user;
    }

    public I getUser() {
        return user;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(getClass().getSimpleName());
        s.append('@');
        s.append(Integer.toHexString(hashCode()));
        s.append("[item=");
        s.append(item);
        s.append(",ratingValue=");
        s.append(ratingValue);
        s.append(",count=");
        s.append(count);
        s.append(",lastActionTime=");
        s.append(lastActionTime);
        s.append(",user=");
        s.append(user);
        s.append(",sessionId=");
        s.append(sessionId);
        s.append("]");
        return s.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((count == null) ? 0 : count.hashCode());
        result = prime * result + ((item == null) ? 0 : item.hashCode());
        result = prime * result + ((lastActionTime == null) ? 0 : lastActionTime.hashCode());
        result = prime * result + ((ratingValue == null) ? 0 : ratingValue.hashCode());
        result = prime * result + ((sessionId == null) ? 0 : sessionId.hashCode());
        result = prime * result + ((user == null) ? 0 : user.hashCode());
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final RatingVO<I,T> other = (RatingVO<I,T>) obj;
        if (count == null) {
            if (other.count != null) return false;
        } else if (!count.equals(other.count)) return false;
        if (item == null) {
            if (other.item != null) return false;
        } else if (!item.equals(other.item)) return false;
        if (lastActionTime == null) {
            if (other.lastActionTime != null) return false;
        } else if (!lastActionTime.equals(other.lastActionTime)) return false;
        if (ratingValue == null) {
            if (other.ratingValue != null) return false;
        } else if (!ratingValue.equals(other.ratingValue)) return false;
        if (sessionId == null) {
            if (other.sessionId != null) return false;
        } else if (!sessionId.equals(other.sessionId)) return false;
        if (user == null) {
            if (other.user != null) return false;
        } else if (!user.equals(other.user)) return false;
        return true;
    }
}
