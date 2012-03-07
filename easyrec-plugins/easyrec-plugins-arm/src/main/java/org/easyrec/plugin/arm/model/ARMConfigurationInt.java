/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.easyrec.plugin.arm.model;

import java.util.List;
import org.easyrec.plugin.arm.model.enums.MetricTypes;

/**
 *
 * @author szavrel
 */
public class ARMConfigurationInt {

    private Integer tenantId;
    private Integer assocType;
    private Integer support;
    private List<Integer> itemTypes;
    private Integer actionType;
    private Double supportPrcnt;
    private Integer supportMinAbs;
    private Double confidencePrcnt;
    private Integer maxRulesPerItem;
    private Boolean excludeSingleItemBaskests = false;
    private Double ratingNeutral;
    private MetricTypes metricType;
    private Integer maxSizeL1;
    private Boolean doDeltaUpdate;

    public ARMConfigurationInt() {

    }

    public ARMConfigurationInt(Integer tenantId,
                               Integer assocType,
                               Integer support,
                               List<Integer> itemTypes,
                               Integer actionType,
                               Double supportPrcnt,
                               Integer supportMinAbs,
                               Double confidencePrcnt,
                               Integer maxRulesPerItem,
                               Double ratingNeutral,
                               MetricTypes metricType,
                               Integer maxSizeL1,
                               Boolean doDeltaUpdate)
    {
        this.tenantId = tenantId;
        this.support = support;
        this.itemTypes = itemTypes;
        this.actionType = actionType;
        this.supportPrcnt = supportPrcnt;
        this.supportMinAbs = supportMinAbs;
        this.confidencePrcnt = confidencePrcnt;
        this.maxRulesPerItem = maxRulesPerItem;
        this.ratingNeutral = ratingNeutral;
        this.metricType = metricType;
        this.maxSizeL1 = maxSizeL1;
        this.doDeltaUpdate = doDeltaUpdate;
        this.assocType = assocType;
    }

    


    public Integer getActionType() {
        return actionType;
    }

    public void setActionType(Integer actionType) {
        this.actionType = actionType;
    }

    public Double getConfidencePrcnt() {
        return confidencePrcnt;
    }

    public void setConfidencePrcnt(Double confidencePrcnt) {
        this.confidencePrcnt = confidencePrcnt;
    }

    public Boolean getDoDeltaUpdate() {
        return doDeltaUpdate;
    }

    public void setDoDeltaUpdate(Boolean doDeltaUpdate) {
        this.doDeltaUpdate = doDeltaUpdate;
    }

    public Boolean getExcludeSingleItemBaskests() {
        return excludeSingleItemBaskests;
    }

    public void setExcludeSingleItemBaskests(Boolean excludeSingleItemBaskests) {
        this.excludeSingleItemBaskests = excludeSingleItemBaskests;
    }

    public List<Integer> getItemTypes() {
        return itemTypes;
    }

    public void setItemTypes(List<Integer> itemTypes) {
        this.itemTypes = itemTypes;
    }

    public Integer getMaxRulesPerItem() {
        return maxRulesPerItem;
    }

    public void setMaxRulesPerItem(Integer maxRulesPerItem) {
        this.maxRulesPerItem = maxRulesPerItem;
    }

    public Integer getMaxSizeL1() {
        return maxSizeL1;
    }

    public void setMaxSizeL1(Integer maxSizeL1) {
        this.maxSizeL1 = maxSizeL1;
    }

    public Integer getSupport() {
        return support;
    }

    public void setSupport(Integer support) {
        this.support = support;
    }

    public Integer getSupportMinAbs() {
        return supportMinAbs;
    }

    public void setSupportMinAbs(Integer supportMinAbs) {
        this.supportMinAbs = supportMinAbs;
    }

    public Double getSupportPrcnt() {
        return supportPrcnt;
    }

    public void setSupportPrcnt(Double supportPrcnt) {
        this.supportPrcnt = supportPrcnt;
    }

    public Double getRatingNeutral() {
        return ratingNeutral;
    }

    public void setRatingNeutral(Double ratingNeutral) {
        this.ratingNeutral = ratingNeutral;
    }

    public MetricTypes getMetricType() {
        return metricType;
    }

    public void setMetricType(MetricTypes metricType) {
        this.metricType = metricType;
    }

    public Integer getAssocType() {
        return assocType;
    }

    public void setAssocType(Integer assocType) {
        this.assocType = assocType;
    }

    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }
    

}
