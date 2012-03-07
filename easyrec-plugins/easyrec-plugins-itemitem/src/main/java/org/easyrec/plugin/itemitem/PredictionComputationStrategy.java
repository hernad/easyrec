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

import org.easyrec.model.core.AssociatedItemVO;
import org.easyrec.model.core.ItemVO;
import org.easyrec.plugin.itemitem.model.UserAssoc;
import org.easyrec.plugin.itemitem.store.dao.ActionDAO;
import org.easyrec.plugin.itemitem.store.dao.UserAssocDAO;

import java.util.List;

/**
 * Strategy for computing predictions.
 *
 * @author Patrick Marschik
 */
public interface PredictionComputationStrategy {
    // -------------------------- OTHER METHODS --------------------------

    /**
     * Indicates the beginning of a cycle of calls to {@link #predictForUserAndItem(Integer,
     * org.easyrec.model.core.ItemVO, java.util.List)} and allows implementations to perform preliminary actions.
     *
     * @param sample               Sample user association to use for quering and storing {@link
     *                             org.easyrec.plugin.itemitem.model.UserAssoc}s. The properties {@code itemFromType}, {@code
     *                             tenant}, {@code changeDate} and {@code sourceType} must be set.
     * @param minRatingValue       Minimum rating value where predictions will be cut-off if {@code
     *                             normalizePredictions} is {@code true}.
     * @param maxRatingValue       Maximum rating value where predictions will be cut-off if {@code
     *                             normalizePredictions} is {@code true}.
     * @param normalizePredictions When {@code true} generated predictions will be truncated inside the range defined by
     *                             {@code minRatingVluae} and {@code maxRatingValue}.
     */
    void beginPrediction(UserAssoc sample, int minRatingValue, int maxRatingValue, boolean normalizePredictions);

    /**
     * Indicates that the cycle of calls to {@link #predictForUserAndItem(Integer, org.easyrec.model.core.ItemVO,
     * java.util.List)} is over and allow implementations to perform cleanup tasks.
     */
    void endPrediction();

    /**
     * Calculates the prediction what {@code userId} would have rated {@code item} based on the {@code itemAssocs} of
     * items similar to {@code item}.
     *
     * @param userId     User to use for prediction.
     * @param item       Item to use for prediction.
     * @param itemAssocs Items similar to {@code item}.
     */
    void predictForUserAndItem(Integer userId, ItemVO<Integer, Integer> item,
                               List<AssociatedItemVO<Integer, Integer>> itemAssocs);

    /**
     * Set the action DAO to use for quering actions.
     *
     * @param actionDao Set the action DAO to use for quering actions.
     */
    void setActionDAO(ActionDAO actionDao);

    /**
     * Set the user assocation DAO for storing generated predictions.
     *
     * @param userAssocDao Set the user assocation DAO for storing generated predictions.
     */
    void setUserAssocDAO(UserAssocDAO userAssocDao);
}
