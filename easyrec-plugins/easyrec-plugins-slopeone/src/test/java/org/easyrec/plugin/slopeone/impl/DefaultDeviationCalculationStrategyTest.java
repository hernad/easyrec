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

import com.google.common.collect.Lists;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.easyrec.model.core.ItemVO;
import org.easyrec.model.core.RatingVO;
import org.easyrec.plugin.slopeone.model.Deviation;
import org.easyrec.plugin.slopeone.model.DeviationCalculationResult;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.easyrec.plugin.slopeone.test.matchers.Matchers.equalToDeviation;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;


/**
 * Test for {@link DefaultDeviationCalculationStrategy}.
 * <p><b>Company:&nbsp;</b> SAT, Research Studios Austria</p>
 * <p><b>Copyright:&nbsp;</b> (c) 2007</p>
 * <p><b>last modified:</b><br/> $Author: dmann $<br/> $Date: 2011-12-20 15:22:22 +0100 (Di, 20 Dez 2011) $<br/> $Revision: 18685 $</p>
 *
 * @author Patrick Marschik
 */
public class DefaultDeviationCalculationStrategyTest {
    private static TIntObjectMap<List<RatingVO<Integer, Integer>>> lemireRatingsByUser;
    private DefaultDeviationCalculationStrategy strategy;

    @BeforeClass
    public static void beforeClass() {
        List<RatingVO<Integer, Integer>> lemireRatings = Lists.newArrayList();

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

        lemireRatingsByUser = new TIntObjectHashMap<List<RatingVO<Integer, Integer>>>(4);

        for (RatingVO<Integer, Integer> rating : lemireRatings) {
            lemireRatingsByUser
                    .putIfAbsent(rating.getUser(), new ArrayList<RatingVO<Integer, Integer>>());

            lemireRatingsByUser.get(rating.getUser()).add(rating);
        }
    }

    @Before
    public void setUp() {
        strategy = new DefaultDeviationCalculationStrategy();
    }

    private static RatingVO<Integer, Integer> createRating(int itemId, double ratingValue,
                                                                             int userId) {
        return new RatingVO<Integer, Integer>(createItem(itemId), ratingValue, 1, new Date(), userId,
                null);
    }

    private static Matcher<Iterable<Deviation>> doesNotContainSymmetricDifference(int item1Id, int item2Id) {
        return not(containsSymmetricDifference(item1Id, item2Id, 0.0, 0));
    }

    @SuppressWarnings("unchecked")
    private static Matcher<Iterable<Deviation>> containsSymmetricDifference(int item1Id, int item2Id, double difference,
                                                                            int count) {
        Deviation a = new Deviation(createItem(item1Id), createItem(item2Id), difference, count);
        Deviation b = new Deviation(createItem(item2Id), createItem(item1Id), -difference, count);

        return anyOf(hasItem(equalToDeviation(a)), hasItem(equalToDeviation(b)));
    }

    protected static ItemVO<Integer, Integer> createItem(int itemId) {
        return new ItemVO<Integer, Integer>(1, itemId, 1);
    }

    @Test
    public void calculate_withLemireData() {
        Date old = new Date(0);

        DeviationCalculationResult result = strategy.calculate(1, lemireRatingsByUser.get(1), old);
        List<Deviation> deviations = result.getDeviations();

        assertThat(result.getCreated(), is(3));
        assertThat(result.getModified(), is(0));

        assertThat(deviations.size(), is(3));
        assertThat(deviations, containsSymmetricDifference(1, 2, 5.0, 1));
        assertThat(deviations, containsSymmetricDifference(1, 4, 9.0, 1));
        assertThat(deviations, containsSymmetricDifference(2, 4, 4.0, 1));

        result = strategy.calculate(2, lemireRatingsByUser.get(2), old);
        deviations = result.getDeviations();

        assertThat(result.getCreated(), is(3));
        assertThat(result.getModified(), is(0));

        assertThat(deviations.size(), is(3));
        assertThat(deviations, containsSymmetricDifference(1, 3, 5.0, 1));
        assertThat(deviations, containsSymmetricDifference(1, 4, 8.0, 1));
        assertThat(deviations, containsSymmetricDifference(3, 4, 3.0, 1));

        result = strategy.calculate(3, lemireRatingsByUser.get(3), old);
        deviations = result.getDeviations();

        assertThat(result.getCreated(), is(6));
        assertThat(result.getModified(), is(0));

        assertThat(deviations.size(), is(6));
        assertThat(deviations, containsSymmetricDifference(1, 2, 5.0, 1));
        assertThat(deviations, containsSymmetricDifference(1, 3, 4.0, 1));
        assertThat(deviations, containsSymmetricDifference(1, 4, 8.0, 1));
        assertThat(deviations, containsSymmetricDifference(2, 3, -1.0, 1));
        assertThat(deviations, containsSymmetricDifference(2, 4, 3.0, 1));
        assertThat(deviations, containsSymmetricDifference(3, 4, 4.0, 1));

        result = strategy.calculate(4, lemireRatingsByUser.get(4), old);
        deviations = result.getDeviations();

        assertThat(result.getCreated(), is(3));
        assertThat(result.getModified(), is(0));

        assertThat(deviations.size(), is(3));
        assertThat(deviations, containsSymmetricDifference(1, 4, -9.0, 1));
        assertThat(deviations, containsSymmetricDifference(1, 5, -3.0, 1));
        assertThat(deviations, containsSymmetricDifference(4, 5, 6.0, 1));
    }
}
