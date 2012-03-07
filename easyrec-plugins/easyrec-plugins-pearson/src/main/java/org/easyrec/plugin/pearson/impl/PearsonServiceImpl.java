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

package org.easyrec.plugin.pearson.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyrec.model.core.ItemVO;
import org.easyrec.model.core.RatingVO;
import org.easyrec.model.core.TenantVO;
import org.easyrec.plugin.pearson.PearsonService;
import org.easyrec.plugin.pearson.model.Settings;
import org.easyrec.plugin.pearson.model.User;
import org.easyrec.plugin.pearson.model.UserAssoc;
import org.easyrec.plugin.pearson.model.Weight;
import org.easyrec.plugin.pearson.store.dao.LatestActionDAO;
import org.easyrec.plugin.pearson.store.dao.LatestActionDAO.RatedTogether;
import org.easyrec.plugin.pearson.store.dao.UserAssocDAO;
import org.easyrec.plugin.pearson.store.dao.UserDAO;
import org.easyrec.plugin.pearson.store.dao.WeightDAO;
import org.easyrec.service.core.TenantService;
import org.easyrec.service.domain.TypeMappingService;

import java.util.*;

public class PearsonServiceImpl implements PearsonService {
    private final LatestActionDAO latestActionDao;
    private final Settings settings;
    private final TenantService tenantService;
    private final TypeMappingService typeMappingService;
    private final UserAssocDAO userAssocDao;
    private final UserDAO userDao;
    private final WeightDAO weightDao;
    protected final Log logger = LogFactory.getLog(getClass());

    public PearsonServiceImpl(final Settings settings, final WeightDAO weightDao, final UserAssocDAO userAssocDao,
                              final UserDAO userDao, final LatestActionDAO latestActionDao,
                              final TenantService tenantService, final TypeMappingService typeMappingService) {
        this.settings = settings;
        this.weightDao = weightDao;
        this.userAssocDao = userAssocDao;
        this.userDao = userDao;
        this.latestActionDao = latestActionDao;
        this.tenantService = tenantService;
        this.typeMappingService = typeMappingService;
    }

    public void perform(final Integer tenantId) {
        List<TenantVO> tenants;

        if (tenantId != null) {
            final TenantVO tenant = tenantService.getTenantById(tenantId);

            tenants = new Vector<TenantVO>(1);
            tenants.add(tenant);
        } else tenants = tenantService.getAllTenants();

        for (final TenantVO tenant : tenants)
            performForTenant(tenant);
    }

    private void calculateWeights(final Integer tenantId, final Integer actionTypeId, final Integer itemTypeId,
                                  final List<User> users, final Map<Integer, Double> averageRatings) {
        final int userCount = users.size();

        final int perc25 = (int) (userCount * 0.25);
        final int perc50 = (int) (userCount * 0.5);
        final int perc75 = (int) (userCount * 0.75);

        for (int i = 0; i < userCount; i++) {
            final User activeUser = users.get(i);
            final double averageActive = averageRatings.get(activeUser.getUser());

            if (logger.isInfoEnabled()) {
                if (i == perc25) logger.info("Weight calculation at 25%");
                if (i == perc50) logger.info("Weight calculation at 50%");
                if (i == perc75) logger.info("Weight calculation at 75%");

                if (i % 10 == 0) logger.info(String.format("Weight calculation at user %d of %d", i, userCount));
            }

            for (int j = i + 1; j < userCount; j++) {
                final User otherUser = users.get(j);
                final List<RatedTogether<Integer, Integer>> ratedTogether = latestActionDao
                        .getItemsRatedTogetherByUsers(tenantId, itemTypeId, activeUser.getUser(), otherUser.getUser(),
                                actionTypeId);

                // users don't have common rated items
                if (ratedTogether.size() == 0) continue;

                final double averageOther = averageRatings.get(otherUser.getUser());
                double frequency = 1.0;

                if (settings.isUseInverseUserFrequency()) {
                    frequency = userCount / ratedTogether.size();
                    frequency = Math.log10(frequency);

                    if (frequency == 0.0) continue;
                }

                double frequencySum = 0.0;
                double expectedBoth = 0.0;
                double expectedActive = 0.0;
                double expectedOther = 0.0;
                double expectedActiveSquare = 0.0;
                double expectedOtherSquare = 0.0;

                for (final RatedTogether<Integer, Integer> rating : ratedTogether) {
                    final double ratingActive = rating.getRating1().getRatingValue();
                    final double ratingOther = rating.getRating2().getRatingValue();

                    frequencySum += frequency;
                    expectedBoth += frequency * ratingActive * ratingOther;
                    expectedActive += frequency * ratingActive;
                    expectedOther += frequency * ratingOther;
                    expectedActiveSquare += frequency * Math.pow(ratingActive, 2.0);
                    expectedOtherSquare += frequency * Math.pow(ratingOther, 2.0);
                }

                // TODO replace EX^2 - (EX)^2 with E((X-EX)^2) for better stability
                final double varianceActive = frequencySum * expectedActiveSquare - Math.pow(expectedActive, 2.0);
                final double varianceOther = frequencySum * expectedOtherSquare - Math.pow(expectedOther, 2.0);

                double numerator1 = frequencySum * expectedBoth;
                double numerator2 = expectedActive * expectedOther;
                final double denominator = Math.sqrt(varianceActive * varianceOther);

                numerator1 /= denominator;
                numerator2 /= denominator;

                final double weight = numerator1 - numerator2;

                if (Double.isNaN(weight) || Double.isInfinite(weight)) {
                    if (logger.isWarnEnabled()) logger.warn(String.format(
                            "Weight is %s for users %d and %d (vAct=%.2f, vOth=%.2f, Eact2=%.2f, Eoth2=%.2f, " +
                                    "Ebot=%.2f, Eact=%.2f, Eoth=%.2f, fre=%.2f fsum=%.2f, num1=%.2f, " +
                                    "numer2=%.2f, den=%.2f)", Double.isNaN(weight) ? "NaN" : "Inf", i, j,
                            varianceActive, varianceOther, expectedActiveSquare, expectedOtherSquare, expectedBoth,
                            expectedActive, expectedOther, frequency, frequencySum, numerator1, numerator2,
                            denominator));

                    continue;
                }

                final Weight weightObj = new Weight(activeUser, otherUser, weight);

                weightDao.insertOrUpdateWeightSymmetric(weightObj);
            }
        }
    }

    private Map<Integer, Double> getAverageUserRatingMap(final Integer tenantId, final Integer itemTypeId) {
        final List<RatingVO<Integer, Integer>> averageRatings = latestActionDao
                .getAverageRatingsForUser(tenantId, itemTypeId);
        final Map<Integer, Double> result = new HashMap<Integer, Double>(averageRatings.size());

        for (final RatingVO<Integer, Integer> averageRating : averageRatings)
            result.put(averageRating.getUser(), averageRating.getRatingValue());

        return result;
    }

    private void performForTenant(final TenantVO tenant) {
        if (tenant == null) throw new IllegalArgumentException("tenant is null");

        final Integer tenantId = tenant.getId();
        final Integer actionTypeId = typeMappingService.getIdOfActionType(tenantId, settings.getActionType());
        final Integer itemTypeId = typeMappingService.getIdOfItemType(tenantId, settings.getItemType());
        final Integer sourceTypeId = typeMappingService.getIdOfSourceType(tenantId, settings.getSourceType());
        final Integer minRatingValue = tenant.getRatingRangeMin();
        final Integer maxRatingValue = tenant.getRatingRangeMax();
        final Date changeDate = new Date();

        final List<User> users = userDao.getUsersForTenant(tenantId);
        final Map<Integer, Double> averageRatings = getAverageUserRatingMap(tenantId, itemTypeId);

        logger.info("Starting weight calculation.");
        Date start = new Date();

        // calculateWeights(tenantId, actionTypeId, itemTypeId, users, averageRatings);

        Date end = new Date();
        double time = (end.getTime() - start.getTime()) / 1000L;
        logger.info(String.format("Calculating weights for %s took %.2f seconds", tenant.getStringId(), time));

        logger.info("Starting predictions.");
        start = new Date();

        predict(tenantId, actionTypeId, itemTypeId, sourceTypeId, changeDate, users, averageRatings, minRatingValue,
                maxRatingValue);

        end = new Date();
        time = (end.getTime() - start.getTime()) / 1000L;
        logger.info(String.format("Calculating USER-ITEM predictions for %s took %.2f seconds", tenant.getStringId(),
                time));
    }

    private void predict(final Integer tenantId, final Integer actionTypeId, final Integer itemTypeId,
                         final Integer sourceTypeId, final Date changeDate, final List<User> users,
                         final Map<Integer, Double> averageRatings, final Integer minRatingValue,
                         final Integer maxRatingValue) {
        // final List<ItemVO<Integer, Integer>> items = latestActionDao.getAvailableItemsForTenant(tenantId,
        // itemTypeId);

        final double caseAmplification = settings.getCaseAmplification();
        final boolean useCaseAmplification = settings.getCaseAmplification() != null;

        int cur = 0;
        final int perc25 = (int) (users.size() * 0.25);
        final int perc50 = (int) (users.size() * 0.5);
        final int perc75 = (int) (users.size() * 0.75);

        for (final User activeUser : users) {
            if (logger.isInfoEnabled()) {
                if (cur == perc25) logger.info("Predictions at 25%");
                if (cur == perc50) logger.info("Predictions at 50%");
                if (cur == perc75) logger.info("Predictions at 75%");

                logger.info(String.format("Predictions at user %d of %d", cur, users.size()));
            }

            cur++;

            // final List<Weight> weights = weightDao.getWeightsForUser1(tenantId, activeUser.getUser());

            // if (weights.size() == 0) {
            // if (logger.isInfoEnabled())
            // logger.info(String.format(
            // "Couldn't calculate prediction for user %d because no weights were present",
            // activeUser.getUser()));
            //
            // continue;
            // }

            final List<ItemVO<Integer, Integer>> items;

            if (settings.getTestDataSourceType() == null)
                items = latestActionDao.getItemsNotRatedByUser(tenantId, activeUser.getUser(), itemTypeId);
            else
                // we are only predicting for the test-set
                items = userAssocDao.getItemsAssociatedToUser(tenantId, activeUser.getUser(), itemTypeId,
                        settings.getTestDataSourceType());

            for (final ItemVO<Integer, Integer> item : items) {
                if (latestActionDao.didUserRateItem(activeUser.getUser(), item, actionTypeId)) continue;

                double kappa = 0.0;
                double weightedRatings = 0.0;

                final List<Weight> weights = weightDao
                        .getWeightsForUser1AndItem(tenantId, activeUser.getUser(), item.getItem(), item.getType());

                if (weights.size() == 0) {
                    if (logger.isInfoEnabled()) logger.info(String.format(
                            "Couldn't calculate prediction for user %d and item %d because no weights were present",
                            activeUser.getUser(), item.getItem()));

                    continue;
                }

                // TODO get users that rated item
                // TODO get weights for users that rated item
                for (final Weight weight : weights) {
                    double currentWeight = weight.getWeight();
                    final User otherUser = weight.getUser2();

                    final List<RatingVO<Integer, Integer>> ratingsOther = latestActionDao
                            .getLatestRatingsForTenant(tenantId, itemTypeId, item.getItem(), otherUser.getUser(), null);

                    if (ratingsOther.size() > 1 && logger.isWarnEnabled())
                        logger.warn("    There shouldn't be more than 1 rating");

                    double ratingOther = 0.0;

                    if (ratingsOther.size() == 1) ratingOther = ratingsOther.get(0).getRatingValue();
                    else
                        // the other user didn't rate the current item
                        continue;

                    if (useCaseAmplification)
                        if (currentWeight >= 0) currentWeight = Math.pow(currentWeight, caseAmplification);
                        else currentWeight = -Math.pow(-currentWeight, caseAmplification);

                    final double averageRatingOther = averageRatings.get(otherUser.getUser());
                    kappa += Math.abs(currentWeight);
                    weightedRatings += currentWeight * (ratingOther - averageRatingOther);
                }

                if (kappa == 0.0 || Double.isNaN(kappa) || Double.isInfinite(kappa)) {
                    if (logger.isInfoEnabled()) logger.info(
                            String.format("    Prediction for user %d item %d failed (kappa=%f)", activeUser.getUser(),
                                    item.getItem(), kappa));

                    continue;
                }

                double prediction = weightedRatings / kappa;

                if (Double.isNaN(weightedRatings) || Double.isInfinite(weightedRatings)) {
                    if (logger.isInfoEnabled()) logger.info(String.format(
                            "    Prediction for user %d item %d failed (prediction=%f, weightedRatings=%f, kappa=%f)",
                            activeUser.getUser(), item.getItem(), prediction, weightedRatings, kappa));

                    continue;
                }

                final double averageRatingActive = averageRatings.get(activeUser.getUser());
                prediction += averageRatingActive;

                if (settings.isNormalizePredictions()) {
                    prediction = Math.max(prediction, minRatingValue);
                    prediction = Math.min(prediction, maxRatingValue);
                }

                final UserAssoc userAssoc = new UserAssoc(prediction, changeDate, item, sourceTypeId, tenantId,
                        activeUser.getUser());
                userAssocDao.insertOrUpdateUserAssoc(userAssoc);
            }
        }
    }

}
