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

import org.easyrec.model.core.AssociatedItemVO;
import org.easyrec.model.core.ItemVO;
import org.easyrec.model.core.RatingVO;
import org.easyrec.plugin.itemitem.model.UserAssoc;
import org.easyrec.plugin.itemitem.store.dao.impl.ActionDAOMemoryImpl;
import org.easyrec.plugin.itemitem.store.dao.impl.UserAssocDAOMemoryImpl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;

import static org.easyrec.plugin.itemitem.test.matchers.IsCloseToUserAssocMatcher.closeToUserAssoc;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link org.easyrec.plugin.itemitem.impl.WeightedPredictionComputationStrategy} <p><b>Company:&nbsp;</b> SAT,
 * Research Studios Austria</p> <p><b>Copyright:&nbsp;</b> (c) 2007</p> <p><b>last modified:</b><br/> $Author$<br/>
 * $Date$<br/> $Revision$</p>
 *
 * @author Patrick Marschik
 */
public class WeightedPredictionComputationStrategyTest {
    // ------------------------------ FIELDS ------------------------------

    private static final int ASSOC_TYPE = 99;
    private static final int SOURCE_TYPE = 98;
    private static final int ITEM_TYPE = 1;
    private static final int ACTION_TYPE = 1;
    private static final int TENANT = 1;

    private static List<RatingVO<Integer, Integer>> lemireRatings;
    private static Map<Integer, List<AssociatedItemVO<Integer, Integer>>> associatedItems;

    private UserAssocDAOMemoryImpl userAssocDAO;
    private ActionDAOMemoryImpl actionDAO;
    private WeightedPredictionComputationStrategy strategy;

    // -------------------------- STATIC METHODS --------------------------

    @BeforeClass
    public static void beforeClass() {
        lemireRatings = new ArrayList<RatingVO<Integer, Integer>>();

        lemireRatings.add(createRating(1, 10.0, 1));
        lemireRatings.add(createRating(2, 5.0, 1));
        lemireRatings.add(createRating(4, 1.0, 1));

        lemireRatings.add(createRating(1, 10.0, 2));
        lemireRatings.add(createRating(3, 5.0, 2));
        lemireRatings.add(createRating(4, 2.0, 2));

        lemireRatings.add(createRating(1, 9.0, 3));
        lemireRatings.add(createRating(2, 4.0, 3));
        lemireRatings.add(createRating(3, 5.0, 3));
        lemireRatings.add(createRating(4, 1.0, 3));

        lemireRatings.add(createRating(1, 1.0, 4));
        lemireRatings.add(createRating(4, 10.0, 4));
        lemireRatings.add(createRating(5, 4.0, 4));

        associatedItems = new HashMap<Integer, List<AssociatedItemVO<Integer, Integer>>>();

        List<AssociatedItemVO<Integer, Integer>> item1s = new ArrayList<AssociatedItemVO<Integer, Integer>>(
                4);
        item1s.add(createAssoc(2, -0.91557180812291200000));
        item1s.add(createAssoc(3, -0.42262153619390100000));
        item1s.add(createAssoc(4, -0.98788278773686600000));
        item1s.add(createAssoc(5, 1.0));

        List<AssociatedItemVO<Integer, Integer>> item2s = new ArrayList<AssociatedItemVO<Integer, Integer>>(
                4);
        item2s.add(createAssoc(1, -0.91557180812291200000));
        item2s.add(createAssoc(3, -1.0));
        item2s.add(createAssoc(4, 0.90508621498992000000));

        List<AssociatedItemVO<Integer, Integer>> item3s = new ArrayList<AssociatedItemVO<Integer, Integer>>(
                4);
        item3s.add(createAssoc(1, -0.42262153619390100000));
        item3s.add(createAssoc(4, 0.40354863494078700000));
        item3s.add(createAssoc(2, -1.0));

        List<AssociatedItemVO<Integer, Integer>> item4s = new ArrayList<AssociatedItemVO<Integer, Integer>>(
                4);
        item4s.add(createAssoc(1, -0.98788278773686600000));
        item4s.add(createAssoc(2, 0.90508621498992000000));
        item4s.add(createAssoc(3, 0.40354863494078700000));
        item4s.add(createAssoc(5, -1.0));

        List<AssociatedItemVO<Integer, Integer>> item5s = new ArrayList<AssociatedItemVO<Integer, Integer>>(
                4);
        item5s.add(createAssoc(1, 1.0));
        item5s.add(createAssoc(4, -1.0));

        associatedItems.put(1, item1s);
        associatedItems.put(2, item2s);
        associatedItems.put(3, item3s);
        associatedItems.put(4, item4s);
        associatedItems.put(5, item5s);
    }

    private static RatingVO<Integer, Integer> createRating(int itemId, double ratingValue,
                                                                             int userId) {
        return new RatingVO<Integer, Integer>(createItem(itemId), ratingValue, 1, new Date(), userId,
                null);
    }

    private static ItemVO<Integer, Integer> createItem(int itemId) {
        return new ItemVO<Integer, Integer>(TENANT, itemId, ITEM_TYPE);
    }

    private static AssociatedItemVO<Integer, Integer> createAssoc(int itemTo, double assocValue) {
        return new AssociatedItemVO<Integer, Integer>(createItem(itemTo), assocValue, null,
                ASSOC_TYPE);
    }

    // -------------------------- OTHER METHODS --------------------------

    @Before
    public void before() {
        userAssocDAO = new UserAssocDAOMemoryImpl();
        actionDAO = new ActionDAOMemoryImpl(lemireRatings, ACTION_TYPE);

        strategy = new WeightedPredictionComputationStrategy();
        strategy.setUserAssocDAO(userAssocDAO);
        strategy.setActionDAO(actionDAO);
    }

    @Test
    public void predict_isCorrect() {
        final Date now = new Date();
        final int MIN_RATING = 0;
        final int MAX_RATING = 10;

        // all values that are not null are either used by the strategy to either query DAOs or create new UserAssocs based on the provided sample.
        UserAssoc sample = new UserAssoc(null, now, new ItemVO<Integer, Integer>(TENANT, null, ITEM_TYPE),
                SOURCE_TYPE, TENANT, null);

        strategy.beginPrediction(sample, MIN_RATING, MAX_RATING, false);

        strategy.predictForUserAndItem(1, createItem(3), associatedItems.get(3));
        strategy.predictForUserAndItem(1, createItem(5), associatedItems.get(5));

        strategy.predictForUserAndItem(2, createItem(2), associatedItems.get(2));
        strategy.predictForUserAndItem(2, createItem(5), associatedItems.get(5));

        strategy.predictForUserAndItem(3, createItem(5), associatedItems.get(5));

        strategy.predictForUserAndItem(4, createItem(2), associatedItems.get(2));
        strategy.predictForUserAndItem(4, createItem(3), associatedItems.get(3));

        strategy.endPrediction();

        List<UserAssoc> result = userAssocDAO.getUserAssocs();
        final double delta = 1.0e-6;

        assertThat(result.size(), is(7));
        assertThat(result,
                hasItem(closeToUserAssoc(createUserAssoc(1, 3, 3.62321928835346000000, sample.getChangeDate()),
                        delta)));
        assertThat(result, hasItem(closeToUserAssoc(createUserAssoc(1, 5, 10.0, sample.getChangeDate()), delta)));
        assertThat(result,
                hasItem(closeToUserAssoc(createUserAssoc(2, 2, 2.33949265845244000000, sample.getChangeDate()),
                        delta)));
        assertThat(result, hasItem(closeToUserAssoc(createUserAssoc(2, 5, 10.0, sample.getChangeDate()), delta)));
        assertThat(result, hasItem(closeToUserAssoc(createUserAssoc(3, 5, 9.0, sample.getChangeDate()), delta)));
        assertThat(result,
                hasItem(closeToUserAssoc(createUserAssoc(4, 2, 9.61807075924101000000, sample.getChangeDate()),
                        delta)));
        assertThat(result,
                hasItem(closeToUserAssoc(createUserAssoc(4, 3, 7.37678071164654000000, sample.getChangeDate()),
                        delta)));
    }

    private static UserAssoc createUserAssoc(int userFrom, int itemTo, double assocValue, Date changeDate) {
        return new UserAssoc(assocValue, changeDate, createItem(itemTo), SOURCE_TYPE, TENANT, userFrom);
    }
}
