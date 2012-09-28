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
 * This class is a VO (valueobject/dataholder) for a SAT recommender database <code>Tenant</code>.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: sat-rsa $<br/>
 * $Date: 2011-08-12 16:46:14 +0200 (Fri, 12 Aug 2011) $<br/>
 * $Revision: 113 $</p>
 *
 * @author Roman Cerny
 */
public class TenantVO implements Serializable {
    ////////////////////////////////////////////////////////////////////////
    // constants
    private static final long serialVersionUID = 4389791969944377288L;
    public static final String DEFAULT_ITEMTYPE = "ITEM";
    public static final Integer DEFAULT_RATING_RANGE_MIN = 1;
    public static final Integer DEFAULT_RATING_RANGE_MAX = 10;
    public static final Double DEFAULT_RATING_RANGE_NEUTRAL = 5.5;

    ////////////////////////////////////////////////////////////////////////
    // members
    private Integer id;
    private String stringId;
    private String description;
    private Integer ratingRangeMin;
    private Integer ratingRangeMax;
    private Double ratingRangeNeutral;
    private Boolean active;

    //////////////////////////////////////////////////////////////////////////////
    // public methods

    // constructor    

    /**
     * used for queried tenants from the database
     */
    public TenantVO(Integer id, String stringId, String description, Integer ratingRangeMin, Integer ratingRangeMax,
                    Double ratingRangeNeutral, Boolean active) {
        this.id = id;
        this.stringId = stringId;
        this.description = description;
        this.ratingRangeMin = ratingRangeMin;
        this.ratingRangeMax = ratingRangeMax;
        this.ratingRangeNeutral = ratingRangeNeutral;
        this.active = active;
    }

    public TenantVO(Integer id, String stringId, String description, Integer ratingRangeMin, Integer ratingRangeMax,
                    Double ratingRangeNeutral) {
        this.id = id;
        this.stringId = stringId;
        this.description = description;
        this.ratingRangeMin = ratingRangeMin;
        this.ratingRangeMax = ratingRangeMax;
        this.ratingRangeNeutral = ratingRangeNeutral;
        this.active = true;
    }
    // constructor    

    /**
     * used for queried tenants from the database
     */
    public TenantVO(String stringId, String description, Integer ratingRangeMin, Integer ratingRangeMax,
                    Double ratingRangeNeutral) {
        this(null, stringId, description, ratingRangeMin, ratingRangeMax, ratingRangeNeutral);
    }

    public TenantVO(String stringId, String description) {
        this(null, stringId, description, DEFAULT_RATING_RANGE_MIN, DEFAULT_RATING_RANGE_MAX,
                DEFAULT_RATING_RANGE_NEUTRAL);
    }

    // getter/setter
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStringId() {
        return stringId;
    }

    public void setStringId(String stringId) {
        this.stringId = stringId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getRatingRangeMin() {
        return ratingRangeMin;
    }

    public void setRatingRangeMin(Integer ratingRangeMin) {
        this.ratingRangeMin = ratingRangeMin;
    }

    public Integer getRatingRangeMax() {
        return ratingRangeMax;
    }

    public void setRatingRangeMax(Integer ratingRangeMax) {
        this.ratingRangeMax = ratingRangeMax;
    }

    public Double getRatingRangeNeutral() {
        return ratingRangeNeutral;
    }

    public void setRatingRangeNeutral(Double ratingRangeNeutral) {
        this.ratingRangeNeutral = ratingRangeNeutral;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(getClass().getSimpleName());
        s.append('@');
        s.append(Integer.toHexString(hashCode()));
        s.append("[id=");
        s.append(id);
        s.append(",stringId=");
        s.append(stringId);
        s.append(",description=");
        s.append(description);
        s.append(",ratingRangeMin=");
        s.append(ratingRangeMin);
        s.append(",ratingRangeMax=");
        s.append(ratingRangeMax);
        s.append(",ratingRangeNeutral=");
        s.append(ratingRangeNeutral);
        s.append("]");
        return s.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((ratingRangeMax == null) ? 0 : ratingRangeMax.hashCode());
        result = prime * result + ((ratingRangeMin == null) ? 0 : ratingRangeMin.hashCode());
        result = prime * result + ((ratingRangeNeutral == null) ? 0 : ratingRangeNeutral.hashCode());
        result = prime * result + ((stringId == null) ? 0 : stringId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final TenantVO other = (TenantVO) obj;
        if (description == null) {
            if (other.description != null) return false;
        } else if (!description.equals(other.description)) return false;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        if (ratingRangeMax == null) {
            if (other.ratingRangeMax != null) return false;
        } else if (!ratingRangeMax.equals(other.ratingRangeMax)) return false;
        if (ratingRangeMin == null) {
            if (other.ratingRangeMin != null) return false;
        } else if (!ratingRangeMin.equals(other.ratingRangeMin)) return false;
        if (ratingRangeNeutral == null) {
            if (other.ratingRangeNeutral != null) return false;
        } else if (!ratingRangeNeutral.equals(other.ratingRangeNeutral)) return false;
        if (stringId == null) {
            if (other.stringId != null) return false;
        } else if (!stringId.equals(other.stringId)) return false;
        return true;
    }
}
