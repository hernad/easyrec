/*
 * Copyright 2011 Research Studios Austria Forschungsgesellschaft mBH
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

package org.easyrec.plugin.pearson.model;

/**
 * <p>
 * Settings for the pearson algorithm.
 * </p>
 * <p>
 * <b>Company:&nbsp;</b> SAT, Research Studios Austria
 * </p>
 * <p/>
 * <p>
 * <b>Copyright:&nbsp;</b> (c) 2009
 * </p>
 * <p/>
 * <p>
 * <b>last modified:</b><br/>
 * $Author$<br/>
 * $Date$<br/>
 * $Revision$
 * </p>
 *
 * @author Patrick Marschik
 */
public class Settings {
    private String actionType = "RATE";
    private Double caseAmplification = 2.5;
    private String itemType = "ITEM";
    private boolean normalizePredictions = true;
    private String sourceType = "PEARSON";
    private Integer testDataSourceType = null;
    private boolean useInverseUserFrequency = true;

    /**
     * Initializes:<br/>
     * actionType = RATE<br/>
     * sourceType = PEARSON<br/>
     * normalizePredictions = true<br/>
     * useInverseUserFrequency = true<br/>
     * caseAmplification = 2.5<br/>
     * testDataSourceType = null
     */
    public Settings() {
        // for initializing as a bean
    }

    public Settings(final String actionType, final String itemType, final String sourceType,
                    final boolean normalizePredictions, final boolean useInverseUserFrequency,
                    final Double caseAmplification, final Integer testDataSourceType) {
        this.actionType = actionType;
        this.itemType = itemType;
        this.sourceType = sourceType;
        this.useInverseUserFrequency = useInverseUserFrequency;
        this.normalizePredictions = normalizePredictions;
        this.caseAmplification = caseAmplification;
    }

    public String getActionType() {
        return actionType;
    }

    public Double getCaseAmplification() {
        return caseAmplification;
    }

    public String getItemType() {
        return itemType;
    }

    public String getSourceType() {
        return sourceType;
    }

    public Integer getTestDataSourceType() {
        return testDataSourceType;
    }

    public boolean isNormalizePredictions() {
        return normalizePredictions;
    }

    public Boolean isUseInverseUserFrequency() {
        return useInverseUserFrequency;
    }

    public void setActionType(final String actionType) {
        this.actionType = actionType;
    }

    public void setCaseAmplification(final Double caseAmplification) {
        this.caseAmplification = caseAmplification;
    }

    public void setItemType(final String itemType) {
        this.itemType = itemType;
    }

    public void setNormalizePredictions(final boolean normalizePredictions) {
        this.normalizePredictions = normalizePredictions;
    }

    public void setSourceType(final String sourceType) {
        this.sourceType = sourceType;
    }

    public void setTestDataSourceType(final Integer testDataSourceType) {
        this.testDataSourceType = testDataSourceType;
    }

    public void setUseInverseUserFrequency(final boolean useInverseUserFrequency) {
        this.useInverseUserFrequency = useInverseUserFrequency;
    }

}
