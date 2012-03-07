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

package org.easyrec.plugin.mahout;

import org.easyrec.plugin.configuration.PluginConfigurationValidator;
import org.easyrec.plugin.configuration.PluginParameter;
import org.easyrec.plugin.generator.GeneratorConfiguration;

/**
 * Configuration object for the demo plugin. <p/> This class contains all parameters that can be configured and are
 * needed for the plugin to work correctly. <p/> <p><b>Company:&nbsp;</b> SAT, Research Studios Austria</p>
 * <p><b>Copyright:&nbsp;</b> (c) 2007</p> <p><b>last modified:</b><br/> $Author$<br/> $Date$<br/> $Revision$</p>
 *
 * @author David Mann
 */
@PluginConfigurationValidator(validatorClass = MahoutBooleanGeneratorConfigValidator.class)
public class MahoutBooleanGeneratorConfig extends GeneratorConfiguration {
    // ------------------------------ FIELDS ------------------------------

    // each configuration value needs to be annotaed with @PluginParameter.
    // displayName      - is the string that will be displayed for the value in the administration tool.
    // shortDescription - will be the first paragrah of the description when the help button is pressed in the admin tool.
    // description      - is the second paragraph displayed in the admin tool.
    //
    // each config value should be initialized with a default value. when a new configuration object is created
    // all config values are initialized with the default values and the configuration is named "Default Configuration" in
    // the superclass (GeneratorConfiguration.)

    @PluginParameter(description = "Set this value to 1 if you want to Cache the Data in your Memory.Could raise a out of memory exception on large dataSets but speeds up the whole process ALOT",
            displayName = "cacheDataInMemory",
            shortDescription = "Cache the Data in Memory",
            displayOrder = 0)
    private int cacheDataInMemory = 1; // DEFAULT VALUE

    public int getCacheDataInMemory() {
        return cacheDataInMemory;
    }

    public void setCacheDataInMemory(int cacheDataInMemory) {
        this.cacheDataInMemory = cacheDataInMemory;
    }

    @PluginParameter(description = "The number of item associations calculated for each item.",
            displayName = "numberOfRecs",
            shortDescription = "Number of recommendations per item",
            displayOrder = 0)
    private int numberOfRecs = 10; // DEFAULT VALUE

    @PluginParameter(description = "The action type to use for generating item associations.",
            displayName = "actionType",
            shortDescription = "The action type to use.",
            displayOrder = 1)
    private String actionType = "BUY";

    private String viewType = "SYSTEM";

    @PluginParameter(description = "The User Similarity Method you want to use. <br> 1 => LogLikelihoodSimilarity <br>  2 => TanimotoCoefficientSimilarity <br> 3 => SpearmanCorrelationSimilarity <br>  4 => CityBlockSimilarity  <br>  ",
            displayName = "userSimilarityMethod",
            shortDescription = "The User Similarity Method you want to use.",
            displayOrder = 2)
    private int userSimilarityMethod = 1;

    @PluginParameter(description = "The User Neighborhood Method you want to use. <br> 1 => ThresholdUserNeighborhood <br> 2 => NearestNUserNeighborhood <br>",
            displayName = "UN: userNeighborhoodMethod",
            shortDescription = "The User Neighborhood Method you want to use.",
            displayOrder = 4)
    private int userNeighborhoodMethod = 1;

    @PluginParameter(description = "percentage of users to consider when building neighborhood -- decrease to trade quality for performance.  The value must be between 0 and 1.",
            displayName = "[UN*] userNeighborhoodSamplingRate",
            shortDescription = "percentage of users to consider when building neighborhood -- decrease to trade quality for performance. The value must be between 0 and 1.",
            displayOrder = 5)
    private double userNeighborhoodSamplingRate = 1.0;

    @PluginParameter(description = "!! Attention !! This parameter is only for userNeighborhood #1. <br>The User Neighborhood similarity Threshold.",
            displayName = "[UN1] userNeighborhoodThreshold",
            shortDescription = "The User Neighborhood similarity Threshold.",
            displayOrder = 6)
    private double userNeighborhoodThreshold = 0.9;


    @PluginParameter(description = "!! Attention !! This parameter is only for userNeighborhood #2. <br> A neighborhood size; capped at the number of users in the data model.",
            displayName = "[UN2] neighborhoodSize",
            shortDescription = "n neighborhood size; capped at the number of users in the data model.",
            displayOrder = 7)
    private int userNeighborhoodSize = 2;


    @PluginParameter(description = "!! Attention !! This parameter is only for userNeighborhood #2. <br> Minimal similarity required for neighbors.  The value must be between 0 and 1.",
            displayName = "[UN2] minSimilarity",
            shortDescription = "Minimal similarity required for neighbors.",
            displayOrder = 8)
    private double userNeighborhoodMinSimilarity = Double.NEGATIVE_INFINITY;


    // --------------------- GETTER / SETTER METHODS ---------------------


    public int getNumberOfRecs() {
        return numberOfRecs;
    }

    public void setNumberOfRecs(int numberOfRecs) {
        this.numberOfRecs = numberOfRecs;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getViewType() {
        return viewType;
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }

    public int getUserSimilarityMethod() {
        return userSimilarityMethod;
    }

    public void setUserSimilarityMethod(int userSimilarityMethod) {
        this.userSimilarityMethod = userSimilarityMethod;
    }

    public int getUserNeighborhoodMethod() {
        return userNeighborhoodMethod;
    }

    public void setUserNeighborhoodMethod(int userNeighborhoodMethod) {
        this.userNeighborhoodMethod = userNeighborhoodMethod;
    }

    public double getUserNeighborhoodSamplingRate() {
        return userNeighborhoodSamplingRate;
    }

    public void setUserNeighborhoodSamplingRate(double userNeighborhoodSamplingRate) {
        this.userNeighborhoodSamplingRate = userNeighborhoodSamplingRate;
    }

    public double getUserNeighborhoodThreshold() {
        return userNeighborhoodThreshold;
    }

    public void setUserNeighborhoodThreshold(double userNeighborhoodThreshold) {
        this.userNeighborhoodThreshold = userNeighborhoodThreshold;
    }

    public int getUserNeighborhoodSize() {
        return userNeighborhoodSize;
    }

    public void setUserNeighborhoodSize(int userNeighborhoodSize) {
        this.userNeighborhoodSize = userNeighborhoodSize;
    }

    public double getUserNeighborhoodMinSimilarity() {
        return userNeighborhoodMinSimilarity;
    }

    public void setUserNeighborhoodMinSimilarity(double userNeighborhoodMinSimilarity) {
        this.userNeighborhoodMinSimilarity = userNeighborhoodMinSimilarity;
    }


}
