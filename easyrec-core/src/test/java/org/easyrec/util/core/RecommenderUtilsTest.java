/**Copyright 2010 Research Studios Austria Forschungsgesellschaft mBH
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
package org.easyrec.util.core;

import org.easyrec.model.core.ItemVO;
import org.easyrec.model.core.RecommendedItemVO;
import org.easyrec.utils.spring.profile.Stopwatch;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.easyrec.util.core.RecommenderUtils.filterAlreadyActedOn;
import static org.easyrec.util.core.RecommenderUtils.filterDuplicates;
import static org.junit.Assert.assertTrue;

public class RecommenderUtilsTest {
    private RecommendedItemVO<Integer, Integer> rItem1_1 = new RecommendedItemVO<Integer, Integer>(
            new ItemVO<Integer, Integer>(1, 1, 1), 0.1d);
    private RecommendedItemVO<Integer, Integer> rItem1_2 = new RecommendedItemVO<Integer, Integer>(
            new ItemVO<Integer, Integer>(1, 1, 1), 0.2d);
    private RecommendedItemVO<Integer, Integer> rItem1_3 = new RecommendedItemVO<Integer, Integer>(
            new ItemVO<Integer, Integer>(1, 1, 1), 0.3d);
    private RecommendedItemVO<Integer, Integer> rItem1_avg123 = new RecommendedItemVO<Integer, Integer>(
            new ItemVO<Integer, Integer>(1, 1, 1), 0.2d);
    private RecommendedItemVO<Integer, Integer> rItem2_1 = new RecommendedItemVO<Integer, Integer>(
            new ItemVO<Integer, Integer>(1, 2, 1), 0.1d);
    private RecommendedItemVO<Integer, Integer> rItem2_2 = new RecommendedItemVO<Integer, Integer>(
            new ItemVO<Integer, Integer>(1, 2, 1), 0.2d);
    private RecommendedItemVO<Integer, Integer> rItem2_avg12 = new RecommendedItemVO<Integer, Integer>(
            new ItemVO<Integer, Integer>(1, 2, 1), 0.15d);
    private RecommendedItemVO<Integer, Integer> rItem2_3 = new RecommendedItemVO<Integer, Integer>(
            new ItemVO<Integer, Integer>(1, 2, 1), 0.3d);
    private RecommendedItemVO<Integer, Integer> rItem2_avg123 = new RecommendedItemVO<Integer, Integer>(
            new ItemVO<Integer, Integer>(1, 2, 1), 0.2d);
    private RecommendedItemVO<Integer, Integer> rItem3_1 = new RecommendedItemVO<Integer, Integer>(
            new ItemVO<Integer, Integer>(1, 3, 1), 0.1d);
    private RecommendedItemVO<Integer, Integer> rItem3_2 = new RecommendedItemVO<Integer, Integer>(
            new ItemVO<Integer, Integer>(1, 3, 1), 0.2d);
    private RecommendedItemVO<Integer, Integer> rItem3_3 = new RecommendedItemVO<Integer, Integer>(
            new ItemVO<Integer, Integer>(1, 3, 1), 0.3d);
    private RecommendedItemVO<Integer, Integer> rItem3_avg123 = new RecommendedItemVO<Integer, Integer>(
            new ItemVO<Integer, Integer>(1, 3, 1), 0.2d);
    private RecommendedItemVO<Integer, Integer> rItem4_1 = new RecommendedItemVO<Integer, Integer>(
            new ItemVO<Integer, Integer>(1, 4, 1), 0.1d);
    private RecommendedItemVO<Integer, Integer> rItem4_2 = new RecommendedItemVO<Integer, Integer>(
            new ItemVO<Integer, Integer>(1, 4, 1), 0.2d);
    private RecommendedItemVO<Integer, Integer> rItem4_3 = new RecommendedItemVO<Integer, Integer>(
            new ItemVO<Integer, Integer>(1, 4, 1), 0.3d);
    private RecommendedItemVO<Integer, Integer> rItem4_avg123 = new RecommendedItemVO<Integer, Integer>(
            new ItemVO<Integer, Integer>(1, 4, 1), 0.2d);
    private RecommendedItemVO<Integer, Integer> rItem5_1 = new RecommendedItemVO<Integer, Integer>(
            new ItemVO<Integer, Integer>(1, 5, 1), 0.1d);
    private RecommendedItemVO<Integer, Integer> rItem5_2 = new RecommendedItemVO<Integer, Integer>(
            new ItemVO<Integer, Integer>(1, 5, 1), 0.2d);
    private RecommendedItemVO<Integer, Integer> rItem5_3 = new RecommendedItemVO<Integer, Integer>(
            new ItemVO<Integer, Integer>(1, 5, 1), 0.3d);
    private RecommendedItemVO<Integer, Integer> rItem5_avg123 = new RecommendedItemVO<Integer, Integer>(
            new ItemVO<Integer, Integer>(1, 5, 1), 0.2d);

    private ItemVO<Integer, Integer> item1 = new ItemVO<Integer, Integer>(1, 1, 1);
    private ItemVO<Integer, Integer> item2 = new ItemVO<Integer, Integer>(1, 2, 1);
    private ItemVO<Integer, Integer> item3 = new ItemVO<Integer, Integer>(1, 3, 1);
    private ItemVO<Integer, Integer> item4 = new ItemVO<Integer, Integer>(1, 4, 1);
    private ItemVO<Integer, Integer> item5 = new ItemVO<Integer, Integer>(1, 5, 1);

    @Test
    public void testFilterDuplicates_No_Duplicates() {
        List<RecommendedItemVO<Integer, Integer>> recommendedItems = new ArrayList<RecommendedItemVO<Integer, Integer>>();
        List<RecommendedItemVO<Integer, Integer>> expectedFilteredRecommendedItems, actualFilteredRecommendedItems;
        recommendedItems.add(rItem1_1);
        recommendedItems.add(rItem2_1);
        recommendedItems.add(rItem3_1);
        recommendedItems.add(rItem4_1);
        recommendedItems.add(rItem5_1);
        expectedFilteredRecommendedItems = recommendedItems;

        actualFilteredRecommendedItems = filterDuplicates(recommendedItems, false);
        assertTrue(expectedFilteredRecommendedItems.containsAll(actualFilteredRecommendedItems));
    }

    @Test
    public void testFilterDuplicates_No_Duplicates2() {
        List<RecommendedItemVO<Integer, Integer>> recommendedItems = new ArrayList<RecommendedItemVO<Integer, Integer>>();
        List<RecommendedItemVO<Integer, Integer>> expectedFilteredRecommendedItems, actualFilteredRecommendedItems;
        recommendedItems.add(rItem1_1);
        recommendedItems.add(rItem2_1);
        recommendedItems.add(rItem3_1);
        recommendedItems.add(rItem4_1);
        recommendedItems.add(rItem5_1);
        expectedFilteredRecommendedItems = recommendedItems;

        actualFilteredRecommendedItems = filterDuplicates(recommendedItems, true);
        assertTrue(expectedFilteredRecommendedItems.containsAll(actualFilteredRecommendedItems));
    }

    @Test
    public void testFilterDuplicates_Some_Duplicates_No_Averages() {
        List<RecommendedItemVO<Integer, Integer>> recommendedItems = new ArrayList<RecommendedItemVO<Integer, Integer>>();
        List<RecommendedItemVO<Integer, Integer>> expectedFilteredRecommendedItems, actualFilteredRecommendedItems;
        recommendedItems.add(rItem1_1);
        recommendedItems.add(rItem1_2);
        recommendedItems.add(rItem1_3);
        recommendedItems.add(rItem2_1);
        recommendedItems.add(rItem2_2);
        recommendedItems.add(rItem3_1);
        recommendedItems.add(rItem4_1);
        recommendedItems.add(rItem5_1);
        expectedFilteredRecommendedItems = new ArrayList<RecommendedItemVO<Integer, Integer>>();
        expectedFilteredRecommendedItems.add(rItem1_1);
        expectedFilteredRecommendedItems.add(rItem2_1);
        expectedFilteredRecommendedItems.add(rItem3_1);
        expectedFilteredRecommendedItems.add(rItem4_1);
        expectedFilteredRecommendedItems.add(rItem5_1);

        actualFilteredRecommendedItems = filterDuplicates(recommendedItems, false);
        assertTrue(expectedFilteredRecommendedItems.containsAll(actualFilteredRecommendedItems));
    }

    @Test
    public void testFilterDuplicates_Some_Duplicates_Use_Averages() {
        List<RecommendedItemVO<Integer, Integer>> recommendedItems = new ArrayList<RecommendedItemVO<Integer, Integer>>();
        List<RecommendedItemVO<Integer, Integer>> expectedFilteredRecommendedItems, actualFilteredRecommendedItems;
        recommendedItems.add(rItem1_1);
        recommendedItems.add(rItem1_2);
        recommendedItems.add(rItem1_3);
        recommendedItems.add(rItem2_1);
        recommendedItems.add(rItem2_2);
        recommendedItems.add(rItem3_1);
        recommendedItems.add(rItem4_1);
        recommendedItems.add(rItem5_1);
        expectedFilteredRecommendedItems = new ArrayList<RecommendedItemVO<Integer, Integer>>();
        expectedFilteredRecommendedItems.add(rItem1_avg123);
        expectedFilteredRecommendedItems.add(rItem2_avg12);
        expectedFilteredRecommendedItems.add(rItem3_1);
        expectedFilteredRecommendedItems.add(rItem4_1);
        expectedFilteredRecommendedItems.add(rItem5_1);

        actualFilteredRecommendedItems = filterDuplicates(recommendedItems, true);
        StringBuilder msg = new StringBuilder("expected: ");
        msg.append(expectedFilteredRecommendedItems);
        msg.append(", actual: ");
        msg.append(actualFilteredRecommendedItems);
        assertTrue(msg.toString(), expectedFilteredRecommendedItems.containsAll(actualFilteredRecommendedItems));
    }

    @Test
    public void testFilterDuplicates_All_Duplicates_No_Averages() {
        List<RecommendedItemVO<Integer, Integer>> recommendedItems = new ArrayList<RecommendedItemVO<Integer, Integer>>();
        List<RecommendedItemVO<Integer, Integer>> expectedFilteredRecommendedItems, actualFilteredRecommendedItems;
        recommendedItems.add(rItem1_1);
        recommendedItems.add(rItem1_2);
        recommendedItems.add(rItem1_3);
        recommendedItems.add(rItem2_1);
        recommendedItems.add(rItem2_2);
        recommendedItems.add(rItem2_3);
        recommendedItems.add(rItem3_1);
        recommendedItems.add(rItem3_2);
        recommendedItems.add(rItem3_3);
        recommendedItems.add(rItem4_1);
        recommendedItems.add(rItem4_2);
        recommendedItems.add(rItem4_3);
        recommendedItems.add(rItem5_1);
        recommendedItems.add(rItem5_2);
        recommendedItems.add(rItem5_3);
        expectedFilteredRecommendedItems = new ArrayList<RecommendedItemVO<Integer, Integer>>();
        expectedFilteredRecommendedItems.add(rItem1_1);
        expectedFilteredRecommendedItems.add(rItem2_1);
        expectedFilteredRecommendedItems.add(rItem3_1);
        expectedFilteredRecommendedItems.add(rItem4_1);
        expectedFilteredRecommendedItems.add(rItem5_1);

        actualFilteredRecommendedItems = filterDuplicates(recommendedItems, false);
        assertTrue(expectedFilteredRecommendedItems.containsAll(actualFilteredRecommendedItems));
    }

    @Test
    public void testFilterDuplicates_All_Duplicates_Use_Averages() {
        List<RecommendedItemVO<Integer, Integer>> recommendedItems = new ArrayList<RecommendedItemVO<Integer, Integer>>();
        List<RecommendedItemVO<Integer, Integer>> expectedFilteredRecommendedItems, actualFilteredRecommendedItems;
        recommendedItems.add(rItem1_1);
        recommendedItems.add(rItem1_2);
        recommendedItems.add(rItem1_3);
        recommendedItems.add(rItem2_1);
        recommendedItems.add(rItem2_2);
        recommendedItems.add(rItem2_3);
        recommendedItems.add(rItem3_1);
        recommendedItems.add(rItem3_2);
        recommendedItems.add(rItem3_3);
        recommendedItems.add(rItem4_1);
        recommendedItems.add(rItem4_2);
        recommendedItems.add(rItem4_3);
        recommendedItems.add(rItem5_1);
        recommendedItems.add(rItem5_2);
        recommendedItems.add(rItem5_3);
        expectedFilteredRecommendedItems = new ArrayList<RecommendedItemVO<Integer, Integer>>();
        expectedFilteredRecommendedItems.add(rItem1_avg123);
        expectedFilteredRecommendedItems.add(rItem2_avg123);
        expectedFilteredRecommendedItems.add(rItem3_avg123);
        expectedFilteredRecommendedItems.add(rItem4_avg123);
        expectedFilteredRecommendedItems.add(rItem5_avg123);

        actualFilteredRecommendedItems = filterDuplicates(recommendedItems, true);
        StringBuilder msg = new StringBuilder("expected: ");
        msg.append(expectedFilteredRecommendedItems);
        msg.append(", actual: ");
        msg.append(actualFilteredRecommendedItems);
        assertTrue(msg.toString(), expectedFilteredRecommendedItems.containsAll(actualFilteredRecommendedItems));
    }

    @Test
    public void testFilterAlreadyActedOn_Nothing_To_Filter_Null() {
        List<RecommendedItemVO<Integer, Integer>> recommendedItems = new ArrayList<RecommendedItemVO<Integer, Integer>>();
        List<RecommendedItemVO<Integer, Integer>> expectedFilteredRecommendedItems;
        List<ItemVO<Integer, Integer>> actedOnItems = null;
        recommendedItems.add(rItem1_1);
        recommendedItems.add(rItem2_1);
        recommendedItems.add(rItem3_1);
        recommendedItems.add(rItem4_1);
        recommendedItems.add(rItem5_1);
        expectedFilteredRecommendedItems = recommendedItems;

        filterAlreadyActedOn(recommendedItems, actedOnItems);
        assertTrue(expectedFilteredRecommendedItems.containsAll(recommendedItems));
    }

    @Test
    public void testFilterAlreadyActedOn_Nothing_To_Filter_Empty() {
        List<RecommendedItemVO<Integer, Integer>> recommendedItems = new ArrayList<RecommendedItemVO<Integer, Integer>>();
        List<RecommendedItemVO<Integer, Integer>> expectedFilteredRecommendedItems;
        List<ItemVO<Integer, Integer>> actedOnItems = new ArrayList<ItemVO<Integer, Integer>>();
        recommendedItems.add(rItem1_1);
        recommendedItems.add(rItem2_1);
        recommendedItems.add(rItem3_1);
        recommendedItems.add(rItem4_1);
        recommendedItems.add(rItem5_1);
        expectedFilteredRecommendedItems = recommendedItems;

        filterAlreadyActedOn(recommendedItems, actedOnItems);
        assertTrue(expectedFilteredRecommendedItems.containsAll(recommendedItems));
    }

    @Test
    public void testFilterAlreadyActedOn() {
        List<RecommendedItemVO<Integer, Integer>> recommendedItems = new ArrayList<RecommendedItemVO<Integer, Integer>>();
        List<RecommendedItemVO<Integer, Integer>> expectedFilteredRecommendedItems = new ArrayList<RecommendedItemVO<Integer, Integer>>();
        List<ItemVO<Integer, Integer>> actedOnItems = new ArrayList<ItemVO<Integer, Integer>>();
        recommendedItems.add(rItem1_1);
        recommendedItems.add(rItem2_1);
        recommendedItems.add(rItem3_1);
        recommendedItems.add(rItem4_1);
        recommendedItems.add(rItem5_1);
        actedOnItems.add(item1);
        actedOnItems.add(item3);
        expectedFilteredRecommendedItems.add(rItem2_1);
        expectedFilteredRecommendedItems.add(rItem4_1);
        expectedFilteredRecommendedItems.add(rItem5_1);

        filterAlreadyActedOn(recommendedItems, actedOnItems);
        assertTrue(expectedFilteredRecommendedItems.containsAll(recommendedItems));
    }

    @Test
    public void testFilterAlreadyActedOn_All() {
        List<RecommendedItemVO<Integer, Integer>> recommendedItems = new ArrayList<RecommendedItemVO<Integer, Integer>>();
        List<RecommendedItemVO<Integer, Integer>> expectedFilteredRecommendedItems = new ArrayList<RecommendedItemVO<Integer, Integer>>();
        List<ItemVO<Integer, Integer>> actedOnItems = new ArrayList<ItemVO<Integer, Integer>>();
        recommendedItems.add(rItem1_1);
        recommendedItems.add(rItem2_1);
        recommendedItems.add(rItem3_1);
        recommendedItems.add(rItem4_1);
        recommendedItems.add(rItem5_1);
        actedOnItems.add(item1);
        actedOnItems.add(item2);
        actedOnItems.add(item3);
        actedOnItems.add(item4);
        actedOnItems.add(item5);

        filterAlreadyActedOn(recommendedItems, actedOnItems);
        assertTrue(expectedFilteredRecommendedItems.containsAll(recommendedItems));
    }

    @Test
    @Ignore
    public void testFilterAlreadyActedOnPerformance(){
        List<RecommendedItemVO<Integer, Integer>> recItems = generateRecommendedItems1ToNNoDuplicates(1000);
        List<ItemVO<Integer, Integer>> items = generateItems1ToNNoDuplicates(500);
        Stopwatch sw = new Stopwatch();
        sw.start();
        for (int i = 0; i < 1000; i++) {
            RecommenderUtils.filterAlreadyActedOn(recItems,items);
        }
        System.out.println("time for 1000 filtering steps: " + sw.stop());
    }

    @Test
    @Ignore
    public void testFilterDuplicatesPerformance(){
        List<RecommendedItemVO<Integer, Integer>> recItems = generateRecommendedItems1ToNNoDuplicates(1000);
        List<RecommendedItemVO<Integer, Integer>> recItemsWithDuplicates = generateRecommendedItems1ToNWithDuplicates(1000,200);
        Stopwatch sw = new Stopwatch();
        sw.start();
        for (int i = 0; i < 1000; i++) {
            RecommenderUtils.filterDuplicates(recItems, false);
        }
        System.out.println("time for 1000 filtering steps (no duplicates, average:false): " + sw.stop());
        sw.start();
        for (int i = 0; i < 1000; i++) {
            RecommenderUtils.filterDuplicates(recItems, true);
        }
        System.out.println("time for 1000 filtering steps (no duplicates, average:true): " + sw.stop());
        sw.start();
        for (int i = 0; i < 1000; i++) {
            RecommenderUtils.filterDuplicates(recItemsWithDuplicates, false);
        }
        System.out.println("time for 1000 filtering steps (with duplicates, average:false): " + sw.stop());
        sw.start();
        for (int i = 0; i < 1000; i++) {
            RecommenderUtils.filterDuplicates(recItemsWithDuplicates, true);
        }
        System.out.println("time for 1000 filtering steps (with duplicates, average:true): " + sw.stop());

    }

    private List<RecommendedItemVO<Integer, Integer>> generateRecommendedItems1ToNNoDuplicates(int count) {
        List<RecommendedItemVO<Integer, Integer>> recommendedItems = new ArrayList<RecommendedItemVO<Integer, Integer>>(count);
        for (int i = 0; i < count; i++) {
            ItemVO<Integer, Integer> item = new ItemVO<Integer, Integer>(i,i,i);
            RecommendedItemVO<Integer, Integer> recItem = new RecommendedItemVO<Integer, Integer>(item, 0.5);
            recommendedItems.add(recItem);
        }
        return recommendedItems;
    }

    private List<RecommendedItemVO<Integer, Integer>> generateRecommendedItems1ToNWithDuplicates(int count, int modul) {
        List<RecommendedItemVO<Integer, Integer>> recommendedItems = new ArrayList<RecommendedItemVO<Integer, Integer>>(count);
        Random rnd = new Random(System.currentTimeMillis());
        for (int i = 0; i < count; i++) {
            ItemVO<Integer, Integer> item = new ItemVO<Integer, Integer>(i% modul ,i % modul,i % modul);
            double value = rnd.nextDouble();
            RecommendedItemVO<Integer, Integer> recItem = new RecommendedItemVO<Integer, Integer>(item, value);
            recommendedItems.add(recItem);
        }
        return recommendedItems;
    }

    private List<ItemVO<Integer, Integer>> generateItems1ToNNoDuplicates(int count) {
        List<ItemVO<Integer, Integer>> recommendedItems = new ArrayList<ItemVO<Integer, Integer>>(count);
        for (int i = 0; i < count; i++) {
            ItemVO<Integer, Integer> item = new ItemVO<Integer, Integer>(i,i,i);
            recommendedItems.add(item);
        }
        return recommendedItems;
    }

}
