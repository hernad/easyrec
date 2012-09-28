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
 * This class is a VO (value object/data holder) for a SAT recommender database <code>Item</code>.
 * These VOs additionally contain a predictionValue and an explanation.
 * All typed attributes use a different set of integer id's for each type.
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
public class RecommendedItemVO<I extends Comparable<I>,T extends Comparable<T>>
        implements Comparable<RecommendedItemVO<I,T>>, Serializable {
    ////////////////////////////////////////////////////////////////////////
    // constants
    private static final long serialVersionUID = -3725554674321957780L;

    ////////////////////////////////////////////////////////////////////////
    // members
    private Integer id;
    private Integer recommendationId;
    private ItemVO<I,T> item;
    private Double predictionValue = 0.0;
    private Integer itemAssocId;
    private String explanation = null;

    ////////////////////////////////////////////////////////////////////////
    // constructors
    // default constructor (for webservice)
    public RecommendedItemVO() {
        super();
    }

    public RecommendedItemVO(ItemVO<I,T> item, Double predictionValue) {
        this.item = item;
        this.predictionValue = predictionValue;
    }

    public RecommendedItemVO(ItemVO<I,T> item, Double predictionValue, Integer itemAssocId) {
        this(item, predictionValue);
        this.itemAssocId = itemAssocId;
    }

    public RecommendedItemVO(ItemVO<I,T> item, Double predictionValue, String explanation) {
        this(item, predictionValue);
        this.explanation = explanation;
    }

    public RecommendedItemVO(ItemVO<I,T> item, Double predictionValue, Integer itemAssocId, String explanation) {
        this(item, predictionValue, itemAssocId);
        this.explanation = explanation;
    }

    public RecommendedItemVO(Integer id, ItemVO<I,T> item, Double predictionValue, Integer recommendationId,
                             Integer itemAssocId, String explanation) {
        this(item, predictionValue, itemAssocId, explanation);
        this.id = id;
        this.recommendationId = recommendationId;
    }

    // getter / setter
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setItem(ItemVO<I,T> item) {
        this.item = item;
    }

    public ItemVO<I,T> getItem() {
        return item;
    }

    public void setItemAssocId(Integer itemAssocId) {
        this.itemAssocId = itemAssocId;
    }

    public Integer getItemAssocId() {
        return itemAssocId;
    }

    public void setRecommendationId(Integer recommendationId) {
        this.recommendationId = recommendationId;
    }

    public Integer getRecommendationId() {
        return recommendationId;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getExplanation() {
        return this.explanation;
    }

    public void setPredictionValue(Double predictionValue) {
        this.predictionValue = predictionValue;
    }

    public Double getPredictionValue() {
        return this.predictionValue;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(getClass().getSimpleName());
        s.append('@');
        s.append(Integer.toHexString(hashCode()));
        s.append("[id=");
        s.append(id);
        s.append(",recommendationId=");
        s.append(getRecommendationId());
        s.append(",item=");
        s.append(getItem());
        s.append(",itemAssocId=");
        s.append(getItemAssocId());
        s.append(",predictionValue=");
        s.append(getPredictionValue());
        s.append(",explanation='");
        s.append(getExplanation());
        s.append("']");
        return s.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((explanation == null) ? 0 : explanation.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((item == null) ? 0 : item.hashCode());
        result = prime * result + ((itemAssocId == null) ? 0 : itemAssocId.hashCode());
        result = prime * result + ((predictionValue == null) ? 0 : predictionValue.hashCode());
        result = prime * result + ((recommendationId == null) ? 0 : recommendationId.hashCode());
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final RecommendedItemVO<I,T> other = (RecommendedItemVO<I,T>) obj;
        if (explanation == null) {
            if (other.explanation != null) return false;
        } else if (!explanation.equals(other.explanation)) return false;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        if (item == null) {
            if (other.item != null) return false;
        } else if (!item.equals(other.item)) return false;
        if (itemAssocId == null) {
            if (other.itemAssocId != null) return false;
        } else if (!itemAssocId.equals(other.itemAssocId)) return false;
        if (predictionValue == null) {
            if (other.predictionValue != null) return false;
        } else if (!predictionValue.equals(other.predictionValue)) return false;
        if (recommendationId == null) {
            if (other.recommendationId != null) return false;
        } else if (!recommendationId.equals(other.recommendationId)) return false;
        return true;
    }

    public int compareTo(RecommendedItemVO<I,T> that) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;
        int comparison = 0;

        if (this == that) {
            return EQUAL;
        }

        // primitive number
        if (this.getId() < that.getId()) {
            return BEFORE;
        } else if (this.getId() > that.getId()) {
            return AFTER;
        }

        // string
        if (this.getItem() != null) {
            comparison = this.getItem().compareTo(that.getItem());
            if (comparison != EQUAL) {
                return comparison;
            }
        } else if (that.getItem() != null) {
            return BEFORE;
        }

        // primitive number
        if (this.recommendationId < that.recommendationId) {
            return BEFORE;
        } else if (this.recommendationId > that.recommendationId) {
            return AFTER;
        }

        if (this.itemAssocId < that.itemAssocId) {
            return BEFORE;
        } else if (this.itemAssocId > that.itemAssocId) {
            return AFTER;
        }

        if (this.predictionValue < that.predictionValue) {
            return BEFORE;
        } else if (this.predictionValue > that.predictionValue) {
            return AFTER;
        }

        // string
        if (this.explanation != null) {
            comparison = this.explanation.compareTo(that.explanation);
            if (comparison != EQUAL) {
                return comparison;
            }
        } else if (that.explanation != null) {
            return BEFORE;
        }

        assert this.equals(that) : "compareTo(...) inconsistent with equals(...)";

        return EQUAL;
    }
}
