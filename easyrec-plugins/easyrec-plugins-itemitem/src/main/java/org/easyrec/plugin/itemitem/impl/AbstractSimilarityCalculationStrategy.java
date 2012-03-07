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

/**
 *
 */
package org.easyrec.plugin.itemitem.impl;

import org.easyrec.model.core.ItemAssocVO;
import org.easyrec.model.core.ItemVO;
import org.easyrec.model.core.RatingVO;
import org.easyrec.plugin.itemitem.SimilarityCalculationStrategy;
import org.easyrec.plugin.itemitem.store.dao.ActionDAO;
import org.easyrec.plugin.itemitem.store.dao.ActionDAO.RatedTogether;
import org.easyrec.plugin.support.ExecutablePluginSupport;
import org.easyrec.service.core.ItemAssocService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Common denominator for calculating similarities. Since each method for calculating similarities is of the form:<br/>
 * a = rating1 - x<br/> b = rating2 - y<br/> similarity = sum over all items( a * b ) / ( sqrt(sum over all items (a^2))
 * * sqrt(sum over all items (b^2)) )<br/> Where x or y might be 0 or an average only methods for providing x and y need
 * to be overridden. <p/> <p/> <p> <b>Company:&nbsp;</b> SAT, Research Studios Austria </p> <p/> <p>
 * <b>Copyright:&nbsp;</b> (c) 2009 </p> <p/> <p> <b>last modified:</b><br/> $Author$<br/> $Date$<br/> $Revision$ </p>
 *
 * @author Patrick Marschik
 */
abstract class AbstractSimilarityCalculationStrategy implements SimilarityCalculationStrategy {
    // ------------------------------ FIELDS ------------------------------

    private ItemAssocService itemAssocService;
    private ActionDAO actionDao;

    // --------------------------- CONSTRUCTORS ---------------------------

    protected AbstractSimilarityCalculationStrategy() {
    }

    protected AbstractSimilarityCalculationStrategy(final ActionDAO actionDao,
                                                    final ItemAssocService itemAssocService) {
        this.actionDao = actionDao;
        this.itemAssocService = itemAssocService;
    }

    // --------------------- GETTER / SETTER METHODS ---------------------

    public void setItemAssocService(final ItemAssocService itemAssocService) {
        this.itemAssocService = itemAssocService;
    }

    // ------------------------ INTERFACE METHODS ------------------------


    // --------------------- Interface SimilarityCalculationStrategy ---------------------

    public int calculateSimilarity(final Integer tenantId, final Integer actionTypeId, final Integer itemTypeId,
                                   final Integer assocTypeId, final Integer sourceTypeId, final Integer viewTypeId,
                                   final Date changeDate, final ExecutablePluginSupport.ExecutionControl control) {
        validateState();

        final List<ItemVO<Integer, Integer>> allItems = actionDao
                .getAvailableItemsForTenant(tenantId, itemTypeId);

        final Map<Integer, RatingVO<Integer, Integer>> averageItemRatings = getAverageItemRatings(
                tenantId, itemTypeId);

        final int ITEM_ASSOC_BUFFER = 10000;
        int itemAssocsCreated = 0;
        List<ItemAssocVO<Integer,Integer>> itemAssocs = new ArrayList<ItemAssocVO<Integer,Integer>>(
                ITEM_ASSOC_BUFFER);

        final int TOTAL_STEPS = allItems.size();

        for (int i = 0; i < TOTAL_STEPS; i++) {
            if (control != null) control.updateProgress(
                    String.format("Calculating similarity %d/%d - %.2f%%", i, TOTAL_STEPS,
                            ((double) i / (double) TOTAL_STEPS) * 100.0));

            final ItemVO<Integer, Integer> item1 = allItems.get(i);

            for (int j = i + 1; j < allItems.size(); j++) {
                double numerator = 0.0;
                double denominator1 = 0.0;
                double denominator2 = 0.0;

                final ItemVO<Integer, Integer> item2 = allItems.get(j);

                final List<RatedTogether<Integer, Integer>> ratedTogether = actionDao
                        .getItemsRatedTogether(tenantId, itemTypeId, item1.getItem(), item2.getItem(), actionTypeId);

                for (final RatedTogether<Integer, Integer> rated : ratedTogether) {
                    final double averageItem1 = getAverage1(rated, averageItemRatings);
                    final double averageItem2 = getAverage2(rated, averageItemRatings);

                    final RatingVO<Integer, Integer> rating1 = rated.getRating1();
                    final RatingVO<Integer, Integer> rating2 = rated.getRating2();

                    final double rating1diff = rating1.getRatingValue() - averageItem1;
                    final double rating2diff = rating2.getRatingValue() - averageItem2;
                    final double rating1diffSquared = Math.pow(rating1diff, 2);
                    final double rating2diffSquared = Math.pow(rating2diff, 2);

                    numerator += rating1diff * rating2diff;
                    denominator1 += rating1diffSquared;
                    denominator2 += rating2diffSquared;
                }

                denominator1 = Math.sqrt(denominator1);
                denominator2 = Math.sqrt(denominator2);

                if (denominator1 == 0.0 || denominator2 == 0.0) continue;

                final double similarityValue = numerator / (denominator1 * denominator2);

                if (Double.isNaN(similarityValue)) continue;

                final ItemAssocVO<Integer,Integer> itemAssoc1 = new ItemAssocVO<Integer,Integer>(
                        tenantId, item1, assocTypeId, similarityValue, item2, sourceTypeId, getSourceInfo(), viewTypeId,
                        null, changeDate);
                final ItemAssocVO<Integer,Integer> itemAssoc2 = new ItemAssocVO<Integer,Integer>(
                        tenantId, item2, assocTypeId, similarityValue, item1, sourceTypeId, getSourceInfo(), viewTypeId,
                        null, changeDate);

                itemAssocs.add(itemAssoc1);
                itemAssocs.add(itemAssoc2);

                itemAssocsCreated += 2;

                if (itemAssocs.size() >= ITEM_ASSOC_BUFFER) {
                    itemAssocService.insertOrUpdateItemAssocs(itemAssocs);
                    itemAssocs.clear();
                }
            }
        }

        if (itemAssocs.size() > 0) itemAssocService.insertOrUpdateItemAssocs(itemAssocs);

        return itemAssocsCreated;
    }

    public void setActionDAO(final ActionDAO actionDao) {
        this.actionDao = actionDao;
    }

    // -------------------------- OTHER METHODS --------------------------

    /**
     * Get x as defined in the class documentation.
     *
     * @param ratedTogether  Two vector component of items to use for getting x.
     * @param averageRatings The map precalculated by {@link #getAverageItemRatings(Integer, Integer)}.
     * @return X.
     */
    protected abstract double getAverage1(RatedTogether<Integer, Integer> ratedTogether,
                                          Map<Integer, RatingVO<Integer, Integer>> averageRatings);

    /**
     * Get y as defined in the class documentation.
     *
     * @param ratedTogether  Two vector component of items to use for getting y.
     * @param averageRatings The map precalculated by {@link #getAverageItemRatings(Integer, Integer)}.
     * @return Y.
     */
    protected abstract double getAverage2(RatedTogether<Integer, Integer> ratedTogether,
                                          Map<Integer, RatingVO<Integer, Integer>> averageRatings);

    /**
     * Allows precalulation of the averages.
     *
     * @param tenantId   Tenant id.
     * @param itemTypeId Item type id.
     * @return A precalculated map mapping item id to average rating.
     */
    protected abstract Map<Integer, RatingVO<Integer, Integer>> getAverageItemRatings(
            Integer tenantId, Integer itemTypeId);

    protected ActionDAO getLatestActionDao() { return actionDao; }

    private void validateState() {
        if (actionDao == null || itemAssocService == null) throw new IllegalStateException("DAOs not initialized");
    }
}