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

import javax.xml.bind.annotation.XmlRootElement;
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
@XmlRootElement
public class RecommendedItem implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -5878765745530258063L;

    private String tenant;
    private String itemId;
    private String itemType;
    private Double predictionValue;
    private String explanation;

    public RecommendedItem() {

    }

    public RecommendedItem(ItemVO<String, String> item, Double predictionValue, String explanation) {
        this.tenant = item.getTenant();
        this.itemId = item.getItem();
        this.itemType = item.getType();
        this.predictionValue = predictionValue;
        this.explanation = explanation;
    }

    public RecommendedItem(String tenant, String itemId, String itemType, Double predictionValue, String explanation) {
        this.tenant = tenant;
        this.itemId = itemId;
        this.itemType = itemType;
        this.predictionValue = predictionValue;
        this.explanation = explanation;
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

    public Double getPredictionValue() {
        return predictionValue;
    }

    public void setPredictionValue(Double predictionValue) {
        this.predictionValue = predictionValue;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

}
