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
package org.easyrec.model.core.transfer;

import java.io.Serializable;
import java.util.Date;

/**
 * This class is a VO (valueobject/dataholder) for <code>constraints</code> passed to another method.
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
public class TimeConstraintVO implements Serializable {
    ////////////////////////////////////////////////////////////////////////
    // constants
    private static final long serialVersionUID = 6284507508079336526L;

    ////////////////////////////////////////////////////////////////////////
    // members
    private Date dateFrom;
    private Date dateTo;

    ////////////////////////////////////////////////////////////////////////
    // constructors
    // default constructor (for webservice)
    public TimeConstraintVO() {

    }

    public TimeConstraintVO(Date dateFrom, Date dateTo) {
        setDateFrom(dateFrom);
        setDateTo(dateTo);
    }

    ////////////////////////////////////////////////////////////////////////
    // public methods
    public Date getDateFrom() {
        return dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(this.getClass().getSimpleName());

        buf.append("(");

        buf.append("dateFrom=");
        buf.append(getDateFrom());

        buf.append(", dateTo=");
        buf.append(getDateTo());

        buf.append(")");

        return buf.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dateFrom == null) ? 0 : dateFrom.hashCode());
        result = prime * result + ((dateTo == null) ? 0 : dateTo.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final TimeConstraintVO other = (TimeConstraintVO) obj;
        if (dateFrom == null) {
            if (other.dateFrom != null) return false;
        } else if (!dateFrom.equals(other.dateFrom)) return false;
        if (dateTo == null) {
            if (other.dateTo != null) return false;
        } else if (!dateTo.equals(other.dateTo)) return false;
        return true;
    }
}
