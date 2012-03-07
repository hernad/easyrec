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
import java.util.List;

/**
 * This class is a VO (valueobject/dataholder) for a SAT recommender database <code>Recommendation</code>.
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
public class RecommendationVO<I extends Comparable<I>,T extends Comparable<T>>
        implements Serializable {
    ////////////////////////////////////////////////////////////////////////
    // constructors
    private static final long serialVersionUID = 9158471137550177026L;

    ////////////////////////////////////////////////////////////////////////
    // members
    private Integer id;
    private I tenant;
    private I user;
    private I queriedItem;
    private T queriedItemType;
    private T queriedAssocType;
    private T relatedActionType;
    private String recommendationStrategy;
    private String explanation;
    private Date recommendationTime;
    private List<RecommendedItemVO<I,T>> recommendedItems;

    ////////////////////////////////////////////////////////////////////////    
    // constructors
    public RecommendationVO(I tenant, I user, I queriedItem, T queriedItemType, T queriedAssocType,
                            T relatedActionType, String recommendationStrategy, String explanation,
                            List<RecommendedItemVO<I,T>> recommendedItems) {
        this(null, tenant, user, queriedItem, queriedItemType, queriedAssocType, relatedActionType,
                recommendationStrategy, explanation, null, recommendedItems);
    }

    public RecommendationVO(I tenant, I user, I queriedItem, T queriedItemType, T queriedAssocType,
                            T relatedActionType, String recommendationStrategy, String explanation,
                            Date recommendationTime, List<RecommendedItemVO<I,T>> recommendedItems) {
        this(null, tenant, user, queriedItem, queriedItemType, queriedAssocType, relatedActionType,
                recommendationStrategy, explanation, recommendationTime, recommendedItems);
    }

    public RecommendationVO(Integer id, I tenant, I user, I queriedItem, T queriedItemType, T queriedAssocType,
                            T relatedActionType, String recommendationStrategy, String explanation,
                            Date recommendationTime, List<RecommendedItemVO<I,T>> recommendedItems) {
        this.id = id;
        this.tenant = tenant;
        this.user = user;
        this.queriedItem = queriedItem;
        this.queriedItemType = queriedItemType;
        this.queriedAssocType = queriedAssocType;
        this.relatedActionType = relatedActionType;
        this.recommendationStrategy = recommendationStrategy;
        this.explanation = explanation;
        this.recommendationTime = recommendationTime;
        this.recommendedItems = recommendedItems;
    }

    // getter/setter    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public I getTenant() {
        return tenant;
    }

    public void setTenant(I tenant) {
        this.tenant = tenant;
    }

    public I getUser() {
        return user;
    }

    public void setUser(I user) {
        this.user = user;
    }

    public I getQueriedItem() {
        return queriedItem;
    }

    public void setQueriedItem(I queriedItem) {
        this.queriedItem = queriedItem;
    }

    public T getQueriedItemType() {
        return queriedItemType;
    }

    public void setQueriedItemType(T queriedItemType) {
        this.queriedItemType = queriedItemType;
    }

    public T getQueriedAssocType() {
        return queriedAssocType;
    }

    public void setQueriedAssocType(T queriedAssocType) {
        this.queriedAssocType = queriedAssocType;
    }

    public T getRelatedActionType() {
        return relatedActionType;
    }

    public void setRelatedActionType(T relatedActionType) {
        this.relatedActionType = relatedActionType;
    }

    public String getRecommendationStrategy() {
        return recommendationStrategy;
    }

    public void setRecommendationStrategy(String recommendationStrategy) {
        this.recommendationStrategy = recommendationStrategy;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public Date getRecommendationTime() {
        return recommendationTime;
    }

    public void setRecommendationTime(Date recommendationTime) {
        this.recommendationTime = recommendationTime;
    }

    public List<RecommendedItemVO<I,T>> getRecommendedItems() {
        return recommendedItems;
    }

    public void setRecommendedItems(List<RecommendedItemVO<I,T>> recommendedItems) {
        this.recommendedItems = recommendedItems;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(getClass().getSimpleName());
        s.append('@');
        s.append(Integer.toHexString(hashCode()));
        s.append("[id=");
        s.append(id);
        s.append(",tenant=");
        s.append(tenant);
        s.append(",user=");
        s.append(user);
        s.append(",queriedItem=");
        s.append(queriedItem);
        s.append(",queriedItemType=");
        s.append(queriedItemType);
        s.append(",queriedAssocType=");
        s.append(queriedAssocType);
        s.append(relatedActionType);
        s.append(",recommendationStrategy='");
        s.append(recommendationStrategy);
        s.append("',explanation='");
        s.append(explanation);
        s.append("',recommendationTime=");
        s.append(recommendationTime);
        s.append(",recommendedItems=");
        s.append(recommendedItems);
        s.append("]");
        return s.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((explanation == null) ? 0 : explanation.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((queriedAssocType == null) ? 0 : queriedAssocType.hashCode());
        result = prime * result + ((queriedItem == null) ? 0 : queriedItem.hashCode());
        result = prime * result + ((queriedItemType == null) ? 0 : queriedItemType.hashCode());
        result = prime * result + ((recommendationStrategy == null) ? 0 : recommendationStrategy.hashCode());
        result = prime * result + ((recommendationTime == null) ? 0 : recommendationTime.hashCode());
        result = prime * result + ((recommendedItems == null) ? 0 : recommendedItems.hashCode());
        result = prime * result + ((relatedActionType == null) ? 0 : relatedActionType.hashCode());
        result = prime * result + ((tenant == null) ? 0 : tenant.hashCode());
        result = prime * result + ((user == null) ? 0 : user.hashCode());
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final RecommendationVO<I,T> other = (RecommendationVO<I,T>) obj;
        if (explanation == null) {
            if (other.explanation != null) return false;
        } else if (!explanation.equals(other.explanation)) return false;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        if (queriedAssocType == null) {
            if (other.queriedAssocType != null) return false;
        } else if (!queriedAssocType.equals(other.queriedAssocType)) return false;
        if (queriedItem == null) {
            if (other.queriedItem != null) return false;
        } else if (!queriedItem.equals(other.queriedItem)) return false;
        if (queriedItemType == null) {
            if (other.queriedItemType != null) return false;
        } else if (!queriedItemType.equals(other.queriedItemType)) return false;
        if (recommendationStrategy == null) {
            if (other.recommendationStrategy != null) return false;
        } else if (!recommendationStrategy.equals(other.recommendationStrategy)) return false;
        if (recommendationTime == null) {
            if (other.recommendationTime != null) return false;
        } else if (!recommendationTime.equals(other.recommendationTime)) return false;
        if (recommendedItems == null) {
            if (other.recommendedItems != null) return false;
        } else if (!recommendedItems.equals(other.recommendedItems)) return false;
        if (relatedActionType == null) {
            if (other.relatedActionType != null) return false;
        } else if (!relatedActionType.equals(other.relatedActionType)) return false;
        if (tenant == null) {
            if (other.tenant != null) return false;
        } else if (!tenant.equals(other.tenant)) return false;
        if (user == null) {
            if (other.user != null) return false;
        } else if (!user.equals(other.user)) return false;
        return true;
    }
}
