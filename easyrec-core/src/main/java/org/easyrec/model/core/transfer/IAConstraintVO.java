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

import org.easyrec.store.dao.core.ItemAssocDAO;

import java.io.Serializable;

/**
 * This class is a VO (valueobject/dataholder) for a constraints object for several queries.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: sat-rsa $<br/>
 * $Date: 2012-02-23 16:01:50 +0100 (Do, 23 Feb 2012) $<br/>
 * $Revision: 140 $</p>
 *
 * @author Roman Cerny
 */
public class IAConstraintVO<I extends Comparable<I>, T extends Comparable<T>> implements Serializable {
    ////////////////////////////////////////////////////////////////////////
    // constants
    private static final long serialVersionUID = -3611369563212545896L;

    /////////////////////////////////////////////////////////////////////////
    // members
    private Integer numberOfResults;
    private I tenant;
    private T viewType;
    private T sourceType;
    private String sourceInfo;
    private Boolean active;
    private Boolean sortAsc;
    private String sortField;

    /////////////////////////////////////////////////////////////////////////
    // constructors
    public IAConstraintVO(Integer numberOfResults) {
        this(numberOfResults, null, null, null, null, null, null, ItemAssocDAO.DEFAULT_ASSOC_VALUE_COLUMN_NAME);
    }

    public IAConstraintVO(Integer numberOfResults, I tenant) {
        this(numberOfResults, null, null, null, tenant, null, null, ItemAssocDAO.DEFAULT_ASSOC_VALUE_COLUMN_NAME);
    }

    public IAConstraintVO(Integer numberOfResults, Boolean sortAsc) {
        this(numberOfResults, null, null, null, null, null, sortAsc, ItemAssocDAO.DEFAULT_ASSOC_VALUE_COLUMN_NAME);
    }

    public IAConstraintVO(Integer numberOfResults, Boolean active, Boolean sortAsc) {
        this(numberOfResults, null, null, null, null, active, sortAsc, ItemAssocDAO.DEFAULT_ASSOC_VALUE_COLUMN_NAME);
    }

    public IAConstraintVO(Integer numberOfResults, T viewType, I tenant) {
        this(numberOfResults, viewType, null, null, tenant, null, null, ItemAssocDAO.DEFAULT_ASSOC_VALUE_COLUMN_NAME);
    }

    public IAConstraintVO(Integer numberOfResults, T viewType, I tenant, Boolean active) {
        this(numberOfResults, viewType, null, null, tenant, active, null, ItemAssocDAO.DEFAULT_ASSOC_VALUE_COLUMN_NAME);
    }

    public IAConstraintVO(Integer numberOfResults, T viewType, T sourceType, I tenant) {
        this(numberOfResults, viewType, sourceType, null, tenant, null, null, ItemAssocDAO.DEFAULT_ASSOC_VALUE_COLUMN_NAME);
    }

    public IAConstraintVO(Integer numberOfResults, T viewType, T sourceType, I tenant, Boolean active) {
        this(numberOfResults, viewType, sourceType, null, tenant, active, null, ItemAssocDAO.DEFAULT_ASSOC_VALUE_COLUMN_NAME);
    }

    public IAConstraintVO(Integer numberOfResults, T viewType, T sourceType, String sourceInfo) {
        this(numberOfResults, viewType, sourceType, sourceInfo, null, null, null, ItemAssocDAO.DEFAULT_ASSOC_VALUE_COLUMN_NAME);
    }

    public IAConstraintVO(Integer numberOfResults, T viewType, T sourceType, String sourceInfo, Boolean active) {
        this(numberOfResults, viewType, sourceType, sourceInfo, null, active, null, ItemAssocDAO.DEFAULT_ASSOC_VALUE_COLUMN_NAME);
    }

    public IAConstraintVO(Integer numberOfResults, T viewType, Boolean sortAsc) {
        this(numberOfResults, viewType, null, null, null, null, sortAsc, ItemAssocDAO.DEFAULT_ASSOC_VALUE_COLUMN_NAME);
    }

    public IAConstraintVO(Integer numberOfResults, T viewType, Boolean active, Boolean sortAsc) {
        this(numberOfResults, viewType, null, null, null, active, sortAsc, ItemAssocDAO.DEFAULT_ASSOC_VALUE_COLUMN_NAME);
    }

    public IAConstraintVO(Integer numberOfResults, T sourceType, String sourceInfo, Boolean sortAsc) {
        this(numberOfResults, null, sourceType, sourceInfo, null, null, sortAsc, ItemAssocDAO.DEFAULT_ASSOC_VALUE_COLUMN_NAME);
    }

    public IAConstraintVO(Integer numberOfResults, T sourceType, String sourceInfo, Boolean active, Boolean sortAsc) {
        this(numberOfResults, null, sourceType, sourceInfo, null, active, sortAsc, ItemAssocDAO.DEFAULT_ASSOC_VALUE_COLUMN_NAME);
    }

        public IAConstraintVO(Integer numberOfResults, T viewType, T sourceType, String sourceInfo, I tenant,
                          Boolean active, Boolean sortAsc) {
            this(numberOfResults, viewType, sourceType, sourceInfo, tenant, active, sortAsc, ItemAssocDAO.DEFAULT_ASSOC_VALUE_COLUMN_NAME);
        }
    
    public IAConstraintVO(Integer numberOfResults, T viewType, T sourceType, String sourceInfo, I tenant,
                          Boolean active, Boolean sortAsc, String sortField) {
        this.numberOfResults = numberOfResults;
        this.viewType = viewType;
        this.sourceType = sourceType;
        this.sourceInfo = sourceInfo;
        this.tenant = tenant;
        this.active = active;
        this.sortAsc = sortAsc;
        this.sortField = sortField;
    }

    // //////////////////////////////////////////////////////////////////////
    // methods
    public Integer getNumberOfResults() {
        return numberOfResults;
    }

    public T getViewType() {
        return viewType;
    }

    public T getSourceType() {
        return sourceType;
    }

    public String getSourceInfo() {
        return sourceInfo;
    }

    public I getTenant() {
        return tenant;
    }

    public Boolean isActive() {
        return active;
    }

    public Boolean getSortAsc() {
        return sortAsc;
    }
    
    public String getSortField() {
        return sortField;
    }

    public void setNumberOfResults(Integer numberOfResults) {
        this.numberOfResults = numberOfResults;
    }

    public void setViewType(T viewType) {
        this.viewType = viewType;
    }

    public void setSourceType(T sourceType) {
        this.sourceType = sourceType;
    }

    public void setSourceInfo(String sourceInfo) {
        this.sourceInfo = sourceInfo;
    }

    public void setTenant(I tenant) {
        this.tenant = tenant;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setSortAsc(Boolean sortAsc) {
        this.sortAsc = sortAsc;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(getClass().getSimpleName());
        s.append('@');
        s.append(Integer.toHexString(hashCode()));
        s.append("[numberOfResults=");
        s.append(numberOfResults);
        s.append(", tenant=");
        s.append(tenant);
        s.append(", viewType=");
        s.append(viewType);
        s.append(", sourceType=");
        s.append(sourceType);
        s.append(", sourceInfo=");
        s.append(sourceInfo);
        s.append(", active=");
        s.append(active);
        s.append(", sortAsc=");
        s.append(sortAsc);
        s.append(", sortField=");
        s.append(sortField);
        s.append("]");
        return s.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((active == null) ? 0 : active.hashCode());
        result = prime * result + ((numberOfResults == null) ? 0 : numberOfResults.hashCode());
        result = prime * result + ((sortAsc == null) ? 0 : sortAsc.hashCode());
        result = prime * result + ((sourceInfo == null) ? 0 : sourceInfo.hashCode());
        result = prime * result + ((sourceType == null) ? 0 : sourceType.hashCode());
        result = prime * result + ((tenant == null) ? 0 : tenant.hashCode());
        result = prime * result + ((viewType == null) ? 0 : viewType.hashCode());
        result = prime * result + ((sortField == null) ? 0 : sortField.hashCode());
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final IAConstraintVO<I,T> other = (IAConstraintVO<I,T>) obj;
        if (active == null) {
            if (other.active != null) return false;
        } else if (!active.equals(other.active)) return false;
        if (numberOfResults == null) {
            if (other.numberOfResults != null) return false;
        } else if (!numberOfResults.equals(other.numberOfResults)) return false;
        if (sortAsc == null) {
            if (other.sortAsc != null) return false;
        } else if (!sortAsc.equals(other.sortAsc)) return false;
        if (sourceInfo == null) {
            if (other.sourceInfo != null) return false;
        } else if (!sourceInfo.equals(other.sourceInfo)) return false;
        if (sourceType == null) {
            if (other.sourceType != null) return false;
        } else if (!sourceType.equals(other.sourceType)) return false;
        if (tenant == null) {
            if (other.tenant != null) return false;
        } else if (!tenant.equals(other.tenant)) return false;
        if (viewType == null) {
            if (other.viewType != null) return false;
        } else if (!viewType.equals(other.viewType)) return false;
        if (sortField == null) {
            if (other.sortField != null) return false;
        } else if (!sortField.equals(other.sortField)) return false;
        return true;
    }
}