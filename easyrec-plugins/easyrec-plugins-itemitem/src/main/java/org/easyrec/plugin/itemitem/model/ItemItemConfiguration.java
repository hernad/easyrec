/*
 * Copyright 2010 Research Studios Austria Forschungsgesellschaft mBH
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

package org.easyrec.plugin.itemitem.model;

import org.easyrec.plugin.configuration.PluginParameter;
import org.easyrec.plugin.generator.GeneratorConfiguration;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Configuration for the item-item algorithm. <p/> <p> <b>Company:&nbsp;</b> SAT, Research Studios Austria </p> <p/> <p>
 * <b>Copyright:&nbsp;</b> (c) 2009 </p> <p/> <p> <b>last modified:</b><br/> $Author$<br/> $Date$<br/> $Revision$ </p>
 *
 * @author Patrick Marschik
 */
@XmlRootElement
@SuppressWarnings({"UnusedDeclaration"})
public class ItemItemConfiguration extends GeneratorConfiguration {
    // ------------------------------ FIELDS ------------------------------

    public static final String DEFAULT_ACTIONTYPE = "RATE";
    public static final String DEFAULT_ITEMTYPE = "ITEM";
    public static final String DEFAULT_VIEWTYPE = "COMMUNITY";
    public static final boolean DEFAULT_NORMALIZEPREDICTIONS = false;
    public static final PredictionComputationType DEFAULT_PREDICTIONCOMPUTATIONTYPE = PredictionComputationType.WEIGHTED;
    public static final SimilarityCalculationType DEFAULT_SIMILARITYCALCULATIONTYPE = SimilarityCalculationType.PEARSON;

    @PluginParameter(displayName = "similarityType",
            description = "Method used for calculating similarities (PEARSON, COSINE and ADJUSTED_COSINE)",
            shortDescription = "")
    private SimilarityCalculationType similarityType;

    //    @PluginParameter(displayName = "predictionType",
    //        description = "Method used for calculating predictions (currently only WEIGHTED).",
    //        shortDescription = "")
    private PredictionComputationType predictionType;

    //    @PluginParameter(displayName = "normalizePredictions",
    //        description = "When true force the generated predictions to fit in the tenant-defined rating-range.",
    //        shortDescription = "")
    private boolean normalizePredictions;

    // Changing this parameter in the scope of easyrec makes no sense -> annotation removed
    //    @PluginParameter(displayName = "actionType",
    //        description = "The actions to consider for calculating similarities.",
    //        shortDescription = "")
    private String actionType;

    // Changing this parameter in the scope of easyrec makes no sense -> annotation removed
    //    @PluginParameter(displayName = "itemType",
    //        description = "The items to consider for calculating similarities.",
    //        shortDescription = "")
    private String itemType;

    //    @PluginParameter(displayName = "viewType",
    //        description = "The view type to consider for calculating similarities.",
    //        shortDescription = "")
    private String viewType;

    // --------------------------- CONSTRUCTORS ---------------------------

    public ItemItemConfiguration() {
        this(DEFAULT_ACTIONTYPE, DEFAULT_ITEMTYPE, DEFAULT_VIEWTYPE, DEFAULT_SIMILARITYCALCULATIONTYPE,
                DEFAULT_PREDICTIONCOMPUTATIONTYPE, DEFAULT_NORMALIZEPREDICTIONS);
    }

    public ItemItemConfiguration(final String actionType, final String itemType, final String viewType,
                                 final SimilarityCalculationType similarityType,
                                 final PredictionComputationType predictionType, final boolean normalizePredictions) {
        this.actionType = actionType;
        this.itemType = itemType;
        this.viewType = viewType;
        this.similarityType = similarityType;
        this.predictionType = predictionType;
        this.normalizePredictions = normalizePredictions;
    }

    // --------------------- GETTER / SETTER METHODS ---------------------

    /**
     * Action type used when querying the action table.
     *
     * @return Action type used when querying the action table.
     */
    public String getActionType() { return actionType; }

    public void setActionType(final String actionType) { this.actionType = actionType; }

    /**
     * Item type used when querying the action table and storing to the ii_userassoc and itemassoc tables.
     *
     * @return Item type used when querying the action table and storing to the ii_userassoc and itemassoc tables.
     */
    public String getItemType() { return itemType; }

    public void setItemType(final String itemType) { this.itemType = itemType; }

    /**
     * Method used to calculate predictions, at the moment only {@code WEIGHTED} is available.
     * <p/>
     * {@code REGRESSION} might be added in the future.
     *
     * @return Method used to calculate predictions, at the moment only {@code WEIGHTED} is available.
     */
    public PredictionComputationType getPredictionType() { return predictionType; }

    public void setPredictionType(final PredictionComputationType predictionType) {
        this.predictionType = predictionType;
    }

    /**
     * Method used to calculate similarities, can be either {@code PEARSON}, {@code COSINE} or {@code ADJUSTED_COSINE}.
     *
     * @return Method used to calculate similarities, can be either {@code PEARSON}, {@code COSINE} or {@code
     *         ADJUSTED_COSINE}.
     */
    public SimilarityCalculationType getSimilarityType() { return similarityType; }

    public void setSimilarityType(final SimilarityCalculationType similarityType) {
        this.similarityType = similarityType;
    }

    /**
     * View type used when writing to the itemassoc table.
     *
     * @return View type used when writing to the itemassoc table.
     */
    public String getViewType() { return viewType; }

    public void setViewType(final String viewType) { this.viewType = viewType; }

    /**
     * When set to {@code true} all generated predictions will be cut-off in the range of valid ratings as defined by
     * the tenant configuration.
     *
     * @return When set to {@code true} all generated predictions will be cut-off in the range of valid ratings as
     *         defined by the tenant configuration.
     */
    public boolean isNormalizePredictions() { return normalizePredictions; }

    public void setNormalizePredictions(final boolean normalizePredictions) {
        this.normalizePredictions = normalizePredictions;
    }
}
