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

package org.easyrec.plugin.itemitem.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyrec.model.core.AssociatedItemVO;
import org.easyrec.model.core.ItemVO;
import org.easyrec.model.core.transfer.IAConstraintVO;
import org.easyrec.plugin.itemitem.ItemItemService;
import org.easyrec.plugin.itemitem.PredictionComputationStrategy;
import org.easyrec.plugin.itemitem.SimilarityCalculationStrategy;
import org.easyrec.plugin.itemitem.model.ItemItemConfiguration;
import org.easyrec.plugin.itemitem.model.UserAssoc;
import org.easyrec.plugin.itemitem.store.dao.ActionDAO;
import org.easyrec.plugin.stats.GeneratorStatistics;
import org.easyrec.plugin.support.ExecutablePluginSupport;
import org.easyrec.service.core.ItemAssocService;

import java.util.Date;
import java.util.List;

/**
 * Implementation of the item-item algorithm. <p/> <p> <b>Company:&nbsp;</b> SAT, Research Studios Austria </p> <p/> <p>
 * <b>Copyright:&nbsp;</b> (c) 2009 </p> <p/> <p> <b>last modified:</b><br/> $Author$<br/> $Date$<br/> $Revision$ </p>
 *
 * @author Patrick Marschik
 */
public class ItemItemServiceImpl implements ItemItemService {
    // ------------------------------ FIELDS ------------------------------

    protected final Log logger = LogFactory.getLog(getClass());
    private final ItemAssocService itemAssocService;
    private final ActionDAO actionDao;

    private ItemItemConfiguration configuration;
    private PredictionComputationStrategy predictionComputationStrategy;
    private SimilarityCalculationStrategy similarityCalculationStrategy;

    // --------------------------- CONSTRUCTORS ---------------------------

    /**
     * Creates a new instance of {@link ItemItemServiceImpl}.
     *
     * @param actionDao        Latest action DAO.
     * @param itemAssocService Item association service.
     */
    public ItemItemServiceImpl(final ActionDAO actionDao, final ItemAssocService itemAssocService) {
        this.actionDao = actionDao;
        this.itemAssocService = itemAssocService;
    }

    // --------------------- GETTER / SETTER METHODS ---------------------

    public void setConfiguration(final ItemItemConfiguration configuration) {
        this.configuration = configuration;
    }

    public void setPredictionComputationStrategy(final PredictionComputationStrategy predictionComputationStrategy) {
        this.predictionComputationStrategy = predictionComputationStrategy;
    }

    public void setSimilarityCalculationStrategy(final SimilarityCalculationStrategy similarityCalculationStrategy) {
        this.similarityCalculationStrategy = similarityCalculationStrategy;
    }

    // ------------------------ INTERFACE METHODS ------------------------


    // --------------------- Interface ItemItemService ---------------------


    public void calculateSimilarity(final Integer tenantId, final Integer actionTypeId, final Integer itemTypeId,
                                    final Integer assocTypeId, final Integer viewTypeId, final Integer sourceTypeId,
                                    final Date changeDate, final GeneratorStatistics stats,
                                    final ExecutablePluginSupport.ExecutionControl control) {
        validateStrategies();

        if (logger.isInfoEnabled()) logger.info("Starting similarity computation.");

        Date start = new Date();

        int assocsCreated = similarityCalculationStrategy
                .calculateSimilarity(tenantId, actionTypeId, itemTypeId, assocTypeId, sourceTypeId, viewTypeId,
                        changeDate, control);
        stats.setNumberOfRulesCreated(assocsCreated);

        Date end = new Date();
        double time = (end.getTime() - start.getTime()) / 1000L;

        if (logger.isInfoEnabled())
            logger.info(String.format("Calculating USER-ITEM predictions for %d took %.2f seconds", tenantId, time));
    }

    public void predict(final Integer tenantId, final Integer actionTypeId, final Integer itemTypeId,
                        final Integer assocTypeId, final Integer viewTypeId, final Integer sourceTypeId,
                        final Date changeDate, final String sourceInfo, final Integer minRatingValue,
                        final Integer maxRatingValue, final ExecutablePluginSupport.ExecutionControl control) {
        validateStrategies();

        if (logger.isInfoEnabled()) logger.info("Starting prediction computation.");

        Date start = new Date();

        final List<Integer> users = actionDao.getUsersForTenant(tenantId);
        final List<ItemVO<Integer, Integer>> items = actionDao
                .getAvailableItemsForTenant(tenantId, itemTypeId);

        final ItemVO<Integer, Integer> itemSample = new ItemVO<Integer, Integer>(tenantId, null,
                itemTypeId);
        final UserAssoc sample = new UserAssoc(null, changeDate, itemSample, sourceTypeId, tenantId, null);

        predictionComputationStrategy
                .beginPrediction(sample, minRatingValue, maxRatingValue, configuration.isNormalizePredictions());

        final int TOTAL_STEPS = items.size();
        int currentStep = 0;

        final IAConstraintVO<Integer, Integer> constraints = new IAConstraintVO<Integer, Integer>(
                null, viewTypeId, sourceTypeId, sourceInfo, tenantId, true, null);

        for (final ItemVO<Integer, Integer> item : items) {
            if (control != null) control.updateProgress(
                    String.format("Calculating predictions %d/%d - %.2f%%", currentStep, TOTAL_STEPS,
                            ((double) currentStep / (double) TOTAL_STEPS) * 100.00));

            final List<AssociatedItemVO<Integer, Integer>> itemAssocs = itemAssocService
                    .getItemsFrom(itemTypeId, assocTypeId, item, constraints);

            for (final Integer user : users)
                if (!actionDao.didUserRateItem(user, item, actionTypeId))
                    predictionComputationStrategy.predictForUserAndItem(user, item, itemAssocs);
        }

        predictionComputationStrategy.endPrediction();

        Date end = new Date();
        double time = (end.getTime() - start.getTime()) / 1000L;

        if (logger.isInfoEnabled())
            logger.info(String.format("Calculating similarities for %d took %.2f seconds", tenantId, time));
    }

    // -------------------------- OTHER METHODS --------------------------

    private void validateStrategies() {
        if (similarityCalculationStrategy == null)
            throw new NullPointerException("similarityCalculationStrategy can't be null.");

        if (predictionComputationStrategy == null)
            throw new NullPointerException("predictionComputationStrategy can't be null.");
    }
}
