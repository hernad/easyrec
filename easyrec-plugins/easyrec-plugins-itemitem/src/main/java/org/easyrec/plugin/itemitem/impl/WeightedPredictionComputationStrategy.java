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
import org.easyrec.model.core.RatingVO;
import org.easyrec.plugin.itemitem.PredictionComputationStrategy;
import org.easyrec.plugin.itemitem.model.UserAssoc;
import org.easyrec.plugin.itemitem.store.dao.ActionDAO;
import org.easyrec.plugin.itemitem.store.dao.UserAssocDAO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Strategy for calculating predictions for users using a weighted sum. <p/> See [Sarwar et al, 2001]. <p/> [Sarwar et
 * al, 2001] Item-based collaborative filtering recommendation algorithms. In SIAM Data Mining (WWW'01), New York, NY,
 * USA, 2001. <p/> <p> <b>Company:&nbsp;</b> SAT, Research Studios Austria </p> <p/> <p> <b>Copyright:&nbsp;</b> (c)
 * 2009 </p> <p/> <p> <b>last modified:</b><br/> $Author$<br/> $Date$<br/> $Revision$ </p>
 *
 * @author Patrick Marschik
 */
public class WeightedPredictionComputationStrategy implements PredictionComputationStrategy {
    // ------------------------------ FIELDS ------------------------------

    private ActionDAO actionDao;
    private final Log logger = LogFactory.getLog(getClass());
    private double maxRatingValue;
    private double minRatingValue;
    private boolean normalizePredictions;
    private UserAssoc sample;
    private UserAssocDAO userAssocDao;

    // --------------------------- CONSTRUCTORS ---------------------------

    public WeightedPredictionComputationStrategy() {
    }

    // ------------------------ INTERFACE METHODS ------------------------


    // --------------------- Interface PredictionComputationStrategy ---------------------

    public void beginPrediction(final UserAssoc sample, final int minRatingValue, final int maxRatingValue,
                                final boolean normalizePredictions) {
        if (actionDao == null) throw new IllegalStateException("DAOs not set");

        if (this.sample != null) throw new IllegalStateException("endPrediction not called");

        this.sample = checkNotNull(sample);
        this.minRatingValue = minRatingValue;
        this.maxRatingValue = maxRatingValue;
        this.normalizePredictions = normalizePredictions;
    }

    public void endPrediction() {
        // userAssocDao.deleteAlreadyVotedAssocs(sample.getTenant(), sample.getSourceType());

        sample = null;
    }

    public void predictForUserAndItem(final Integer userId, final ItemVO<Integer, Integer> item,
                                      final List<AssociatedItemVO<Integer, Integer>> itemAssocs) {
        if (actionDao == null || userAssocDao == null) throw new IllegalStateException("DAOs not set");

        if (sample == null) throw new IllegalStateException("beginPrediction not called");

        final Map<Integer, RatingVO<Integer, Integer>> ratingsOfUserMap = getRatingsOfUserMap(userId);

        double numerator = 0.0;
        double denominator = 0.0;

        // now for each similar item ...
        for (final AssociatedItemVO<Integer, Integer> itemAssoc : itemAssocs) {
            // get the rating of the user of that similar item
            final RatingVO<Integer, Integer> ratingOfUser = ratingsOfUserMap
                    .get(itemAssoc.getItem().getItem());

            // user didn't rate the other item so a rating of 0 is assumed
            if (ratingOfUser == null) continue;

            double similarity = itemAssoc.getAssocValue();
            // similarity is in [-1.0, 1.0] -> move it to [0.0, 2.0] because negative similarities might cancel other similarities
            similarity += 1.0;

            double userRating = ratingOfUser.getRatingValue();

            numerator += similarity * userRating;
            denominator += Math.abs(similarity);
        }

        if (denominator == 0) return;

        double prediction = numerator / denominator;

        if (normalizePredictions) {
            prediction = Math.max(prediction, minRatingValue);
            prediction = Math.min(prediction, maxRatingValue);
        }

        final UserAssoc userAssoc = new UserAssoc(prediction, sample.getChangeDate(), item, sample.getSourceTypeId(),
                sample.getTenantId(), userId);

        userAssocDao.insertOrUpdateUserAssoc(userAssoc);
    }

    public void setActionDAO(final ActionDAO actionDao) {
        this.actionDao = actionDao;
    }

    public void setUserAssocDAO(final UserAssocDAO userAssocDao) {
        this.userAssocDao = userAssocDao;
    }

    // -------------------------- OTHER METHODS --------------------------

    /**
     * Creates a mapping of item IDs for which the user has rated to the ratings of the user
     *
     * @param userId The user to create the mapping for.
     * @return The mapping itemId -> RatingVO.
     */
    private Map<Integer, RatingVO<Integer, Integer>> getRatingsOfUserMap(final Integer userId) {
        final List<RatingVO<Integer, Integer>> ratingsOfUser = actionDao
                .getLatestRatingsForTenant(sample.getTenantId(), sample.getItemTo().getType(), null, userId, null);
        final Map<Integer, RatingVO<Integer, Integer>> ratingsOfUserMap = new HashMap<Integer, RatingVO<Integer, Integer>>();

        for (final RatingVO<Integer, Integer> rating : ratingsOfUser) {
            if (ratingsOfUserMap.containsKey(rating.getItem().getItem())) {
                if (logger.isWarnEnabled()) logger.warn("The user should have only voted once for the same item.");

                continue;
            }

            ratingsOfUserMap.put(rating.getItem().getItem(), rating);
        }
        return ratingsOfUserMap;
    }
}
