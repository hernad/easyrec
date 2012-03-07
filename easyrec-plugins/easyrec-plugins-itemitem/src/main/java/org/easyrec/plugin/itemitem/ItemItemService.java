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

package org.easyrec.plugin.itemitem;

import org.easyrec.plugin.itemitem.model.ItemItemConfiguration;
import org.easyrec.plugin.stats.GeneratorStatistics;
import org.easyrec.plugin.support.ExecutablePluginSupport;

import java.util.Date;

/**
 * Interface for a generator that produces predictions based on the item-item algorithm as described in [Sarwar et al,
 * 2001]. <p/> [Sarwar et al, 2001] Item-based collaborative filtering recommendation algorithms. In SIAM Data Mining
 * (WWW'01), New York, NY, USA, 2001. <p> <b>Company:&nbsp;</b> SAT, Research Studios Austria </p> <p/> <p>
 * <b>Copyright:&nbsp;</b> (c) 2009 </p> <p/> <p> <b>last modified:</b><br/> $Author$<br/> $Date$<br/> $Revision$ </p>
 *
 * @author Patrick Marschik
 */
public interface ItemItemService {
    // ------------------------------ FIELDS ------------------------------

    String SOURCE_INFO_COSINE = "itemitem-cosine";
    String SOURCE_INFO_COSINE_ADJUSTED = "itemitem-cosineadjusted";
    String SOURCE_INFO_PEARSON = "itemitem-pearson";

    // -------------------------- OTHER METHODS --------------------------

    /**
     * Calculate the similarity between each pair of items.
     *
     * @param tenantId     Tenant id.
     * @param actionTypeId Action type id.
     * @param itemTypeId   Item type id.
     * @param assocTypeId  Association type id.
     * @param viewTypeId   View type id.
     * @param sourceTypeId Source type id.
     * @param changeDate   Date to set for generated {@link org.easyrec.model.core.ItemAssocVO}s.
     * @param control      Control to update progress.
     */
    void calculateSimilarity(Integer tenantId, Integer actionTypeId, Integer itemTypeId, Integer assocTypeId,
                             Integer viewTypeId, Integer sourceTypeId, Date changeDate, GeneratorStatistics stats,
                             final ExecutablePluginSupport.ExecutionControl control);

    /**
     * Calculate user to item predictions (recommendations) for each user.
     *
     * @param tenantId       Tenant id.
     * @param actionTypeId   Action type id.
     * @param itemTypeId     Item type id.
     * @param assocTypeId    Assocation type id.
     * @param viewTypeId     View type id.
     * @param sourceTypeId   Source type id.
     * @param changeDate     Date to set for generated +{@link org.easyrec.plugin.itemitem.model.UserAssoc}s.
     * @param sourceInfo     Source info to set for generated {@link org.easyrec.plugin.itemitem.model.UserAssoc}s.
     * @param minRatingValue Minimum allowed rating value (used only when {@link org.easyrec.plugin.itemitem.model.ItemItemConfiguration#isNormalizePredictions()}
     *                       is {@code true}.)
     * @param maxRatingValue Maximum allowed rating value (used only when {@link org.easyrec.plugin.itemitem.model.ItemItemConfiguration#isNormalizePredictions()}
     *                       is {@code true}.)
     * @param control        Control to update progress.
     */
    void predict(Integer tenantId, Integer actionTypeId, Integer itemTypeId, Integer assocTypeId, Integer viewTypeId,
                 Integer sourceTypeId, Date changeDate, String sourceInfo, Integer minRatingValue,
                 Integer maxRatingValue, final ExecutablePluginSupport.ExecutionControl control);

    /**
     * Set the configuration used by the service.
     *
     * @param configuration Configuration to use.
     */
    void setConfiguration(ItemItemConfiguration configuration);

    /**
     * Set the prediction computation strategy to use in {@link #predict(Integer, Integer, Integer, Integer, Integer,
     * Integer, java.util.Date, String, Integer, Integer, org.easyrec.plugin.support.ExecutablePluginSupport.ExecutionControl)}.
     *
     * @param predictionComputationStrategy Set the prediction computation strategy to use in {@link #predict(Integer,
     *                                      Integer, Integer, Integer, Integer, Integer, java.util.Date, String,
     *                                      Integer, Integer, org.easyrec.plugin.support.ExecutablePluginSupport.ExecutionControl)}.
     */
    void setPredictionComputationStrategy(PredictionComputationStrategy predictionComputationStrategy);

    /**
     * Set the similarity calculation strategy to use in {@link #calculateSimilarity(Integer, Integer, Integer, Integer,
     * Integer, Integer, java.util.Date, org.easyrec.plugin.support.ExecutablePluginSupport.ExecutionControl)}.
     *
     * @param similarityCalculationStrategy Set the similarity calculation strategy to use in {@link
     *                                      #calculateSimilarity(Integer, Integer, Integer, Integer, Integer, Integer,
     *                                      java.util.Date, org.easyrec.plugin.support.ExecutablePluginSupport.ExecutionControl)}.
     */
    void setSimilarityCalculationStrategy(SimilarityCalculationStrategy similarityCalculationStrategy);
}
