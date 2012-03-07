/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.easyrec.plugin.arm.model;

import java.io.Serializable;
import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;
import org.easyrec.plugin.arm.model.enums.MetricTypes;
import org.easyrec.plugin.stats.GeneratorStatistics;

/**
 *
 * @author szavrel
 */
@XmlRootElement
public class ARMStatistics extends GeneratorStatistics implements Serializable {

    // ------------------------- FIELDS --------------------
    
    private static final long serialVersionUID = 5535763649070204018L;

    private String sessionName;
    private Integer nrBaskets;
    private Integer nrProducts;
    private Integer sizeRules;
    private Integer sizeL1;
    private Integer sizeL2;
    private Integer sizeCountMap;
    private long duration;
    private Integer lastSupport;
    private double lastConf;
    private MetricTypes metricType;
    private String exception = null;

    public ARMStatistics() {

    }

    // ------------------------- GETTER / SETTER METHODS --------------------

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public double getLastConf() {
        return lastConf;
    }

    public void setLastConf(double lastConf) {
        this.lastConf = lastConf;
    }

    public Integer getLastSupport() {
        return lastSupport;
    }

    public void setLastSupport(Integer lastSupport) {
        this.lastSupport = lastSupport;
    }

    public MetricTypes getMetricType() {
        return metricType;
    }

    public void setMetricType(MetricTypes metricType) {
        this.metricType = metricType;
    }

    public Integer getNrBaskets() {
        return nrBaskets;
    }

    public void setNrBaskets(Integer nrBaskets) {
        this.nrBaskets = nrBaskets;
    }

    public Integer getNrProducts() {
        return nrProducts;
    }

    public void setNrProducts(Integer nrProducts) {
        this.nrProducts = nrProducts;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public Integer getSizeCountMap() {
        return sizeCountMap;
    }

    public void setSizeCountMap(Integer sizeCountMap) {
        this.sizeCountMap = sizeCountMap;
    }

    public Integer getSizeL1() {
        return sizeL1;
    }

    public void setSizeL1(Integer sizeL1) {
        this.sizeL1 = sizeL1;
    }

    public Integer getSizeL2() {
        return sizeL2;
    }

    public void setSizeL2(Integer sizeL2) {
        this.sizeL2 = sizeL2;
    }

    public Integer getSizeRules() {
        return sizeRules;
    }

    public void setSizeRules(Integer sizeRules) {
        this.sizeRules = sizeRules;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

}
