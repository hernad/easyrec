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

package org.easyrec.plugin.slopeone.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyrec.model.core.ItemAssocVO;
import org.easyrec.model.core.RatingVO;
import org.easyrec.model.core.TenantVO;
import org.easyrec.plugin.slopeone.DeviationCalculationStrategy;
import org.easyrec.plugin.slopeone.SlopeOneService;
import org.easyrec.plugin.slopeone.model.*;
import org.easyrec.plugin.slopeone.store.dao.ActionDAO;
import org.easyrec.plugin.slopeone.store.dao.DeviationDAO;
import org.easyrec.plugin.support.ExecutablePluginSupport;
import org.easyrec.plugin.support.ExecutablePluginSupport.ExecutionControl;
import org.easyrec.store.dao.core.ItemAssocDAO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementation of SlopeOneService.<p><b>Company:&nbsp;</b> SAT, Research Studios Austria</p>
 * <p><b>Copyright:&nbsp;</b> (c) 2007</p> <p><b>last modified:</b><br/> $Author: dmann $<br/> $Date: 2011-12-20 15:22:22 +0100 (Di, 20 Dez 2011) $<br/> $Revision: 18685 $</p>
 *
 * @author Patrick Marschik
 */
public class SlopeOneServiceImpl implements SlopeOneService {
    private static final Log logger = LogFactory.getLog(SlopeOneServiceImpl.class);

    private ActionDAO actionDAO;
    private DeviationCalculationStrategy deviationCalculation;
    private DeviationDAO deviationDAO;
    private ItemAssocDAO itemAssocDAO;

    public SlopeOneServiceImpl(ItemAssocDAO itemAssocDAO, ActionDAO actionDAO, DeviationDAO deviationDAO,
                               DeviationCalculationStrategy deviationCalculation) {
        this.itemAssocDAO = itemAssocDAO;
        this.actionDAO = actionDAO;
        this.deviationDAO = deviationDAO;
        this.deviationCalculation = deviationCalculation;
    }

    public void calculateDeviations(SlopeOneIntegerConfiguration config, Date lastRun, SlopeOneStats stats,
                                    Set<TenantItem> changedItemIds,
                                    final ExecutablePluginSupport.ExecutionControl control) {
        long start = System.currentTimeMillis();

        // get only the users that did ratings since the last execution
        List<Integer> users = actionDAO.getUsers(config.getTenant(), config.getItemTypes(), lastRun);
        stats.setNoUsers(users.size());

        final int TOTAL_STEPS = users.size();
        int currentStep = 0;

        for (int userId : users) {
            if (control != null)
                control.updateProgress(String.format("Calculating deviations %d/%d", currentStep++, TOTAL_STEPS));

            // for each of these users get all his ratings
            List<RatingVO<Integer, Integer>> ratings =
                    actionDAO.getRatings(config.getTenant(), config.getItemTypes(), userId);
            stats.setNumberOfActionsConsidered(stats.getNumberOfActionsConsidered() + ratings.size());

            // and use them to calculate the new deviations (old deviations, i.e. deviations that were already
            // generated in a prior run are already filtered by the strategy.)
            // moreover a proxy strategy merges the deviations with the deviations in the database (i.e. numerator
            // and denominator are already summed to the current value.)
            DeviationCalculationResult result = deviationCalculation.calculate(userId, ratings, lastRun);
            List<Deviation> deviations = result.getDeviations();
            stats.setNoCreatedDeviations(stats.getNoCreatedDeviations() + result.getCreated());
            stats.setNoModifiedDeviations(stats.getNoModifiedDeviations() + result.getModified());

            if (changedItemIds != null) {
                for (Deviation deviation : deviations) {
                    changedItemIds.add(new TenantItem(deviation.getItem1Id(), deviation.getItem1TypeId()));
                    changedItemIds.add(new TenantItem(deviation.getItem2Id(), deviation.getItem1TypeId()));
                }
            }

            deviationDAO.insertDeviations(deviations);
        }

        if (logger.isDebugEnabled())
            logger.debug("finishing deviations calculation");

        // endUpdate hint to DAO, so that if the DAO is cached the cache has a chance to write through to the underlying
        // store
        deviationDAO.endUpdate();

        stats.setDeviationDuration(System.currentTimeMillis() - start);
    }

    public void generateActions(SlopeOneIntegerConfiguration config, LogEntry lastRun, SlopeOneStats stats) {
        long start = System.currentTimeMillis();

        if (logger.isDebugEnabled())
            logger.debug("starting action generation");

        actionDAO.generateActions(config.getTenant(), config.getItemTypes(), config.getActionType(),
                lastRun.getExecution());

        stats.setActionDuration(System.currentTimeMillis() - start);
    }

    public void nonPersonalizedRecommendations(final SlopeOneIntegerConfiguration config, final SlopeOneStats stats,
                                               final Date execution, final Set<TenantItem> changedItemIds,
                                               final ExecutionControl control) {
        final long start = System.currentTimeMillis();

        final int MAX_ITEMASSOCS = 50000;
        // atomic to support usage in changedItemIds.forEach(AnonymousClass)
        final AtomicInteger itemAssocCount = new AtomicInteger(0);

        final int TOTAL_STEPS = changedItemIds.size();
        // atomic to support usage in changedItemIds.forEach(AnonymousClass)
        final AtomicInteger currentStep = new AtomicInteger(0);

        Integer maxRecsPerItem = config.getMaxRecsPerItem();
        final List<ItemAssocVO<Integer,Integer>> itemAssocs =
                new ArrayList<ItemAssocVO<Integer,Integer>>(
                        Math.min(changedItemIds.size() * (maxRecsPerItem != null ? maxRecsPerItem : 10),
                                MAX_ITEMASSOCS));

        for (TenantItem changedItem : changedItemIds) {
            if (control != null) control.updateProgress(
                    String.format("Calculating non-personalized recommendations %d/%d", currentStep.getAndIncrement(),
                            TOTAL_STEPS));

            List<Deviation> deviations = deviationDAO.getDeviationsOrdered(config.getTenant(),
                    changedItem.getItemTypeId(), changedItem.getItemId(), config.getMinRatedCount(),
                    config.getMaxRecsPerItem());

            for (Deviation deviation : deviations) {
                ItemAssocVO<Integer,Integer> assoc =
                        new ItemAssocVO<Integer,Integer>(config.getTenant(),
                                deviation.getItem1(), config.getAssocType(), deviation.getDeviation(),
                                deviation.getItem2(), config.getSourceType(), config.getNonPersonalizedSourceInfo(),
                                config.getViewType(), Boolean.TRUE, execution);

                itemAssocs.add(assoc);
            }

            if (itemAssocs.size() >= MAX_ITEMASSOCS) {
                itemAssocCount.getAndAdd((itemAssocs.size()));
                itemAssocDAO.insertOrUpdateItemAssocs(itemAssocs);
                itemAssocs.clear();
            }
        }

        if (itemAssocs.size() > 0) {
            itemAssocCount.getAndAdd((itemAssocs.size()));
            itemAssocDAO.insertOrUpdateItemAssocs(itemAssocs);
            itemAssocs.clear();
        }

        itemAssocDAO.removeItemAssocByTenant(config.getTenant(), config.getAssocType(), config.getSourceType(),
                execution);

        stats.setNumberOfRulesCreated(itemAssocCount.get());
        stats.setNonPersonalizedDuration(System.currentTimeMillis() - start);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void personalizedRecommendations(TenantVO tenant, SlopeOneIntegerConfiguration config, SlopeOneStats stats,
                                            Date execution, Set<TenantItem> changedItemIds, boolean weighted,
                                            final ExecutionControl control) {
        List<Integer> userIds = actionDAO.getUsers(config.getTenant(), config.getItemTypes(), execution);

        final int TOTAL_STEPS = userIds.size();
        int currentStep = 0;

        for (int userId : userIds) {
            if (control != null) control.updateProgress(
                    String.format("Calculating personalized recommendations %d/%d", currentStep++, TOTAL_STEPS));

            List<RatingVO<Integer, Integer>> ratings = actionDAO.getRatings(
                    config.getTenant(), config.getItemTypes(), userId);

            for (RatingVO<Integer, Integer> rating : ratings) {
                int itemId = rating.getItem().getItem();
                int itemTypeId = rating.getItem().getType();

                if (!changedItemIds.contains(new TenantItem(itemId, itemTypeId))) continue;

                List<Deviation> deviations = deviationDAO.getDeviationsOrdered(config.getTenant(), itemTypeId, itemId,
                        config.getMinRatedCount(), config.getMaxRecsPerItem());

                double recommendation = weighted ? weightedRecommendations(rating, deviations)
                                                 : plainRecommendations(rating, deviations);

                if (logger.isDebugEnabled())
                    logger.debug("created recommendation " + recommendation);
                // TODO write to database
            }
        }
    }

    private double weightedRecommendations(final RatingVO<Integer, Integer> rating,
                                           final List<Deviation> deviations) {
        double numerator = 0.0;
        long denominator = 0L;

        int itemId = rating.getItem().getItem();

        for (Deviation deviation : deviations) {
            if (deviation.getItem2Id() == itemId) continue;

            numerator += (deviation.getDeviation() * rating.getRatingValue()) * deviation.getDenominator();
            denominator += deviation.getDenominator();
        }

        return numerator / denominator;
    }

    private double plainRecommendations(final RatingVO<Integer, Integer> rating,
                                        final List<Deviation> deviations) {
        double numerator = 0.0;
        long denominator = 0L;

        int itemId = rating.getItem().getItem();

        for (Deviation deviation : deviations) {
            if (deviation.getItem2Id() == itemId) continue;

            numerator += deviation.getDeviation() * rating.getRatingValue();
            denominator++;
        }

        return numerator / denominator;
    }
}
