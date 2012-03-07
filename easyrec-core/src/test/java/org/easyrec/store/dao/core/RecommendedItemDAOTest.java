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
package org.easyrec.store.dao.core;

import org.easyrec.model.core.ItemVO;
import org.easyrec.model.core.RecommendedItemVO;
import org.easyrec.model.core.transfer.TimeConstraintVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.annotation.ExpectedDataSet;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

import java.util.*;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@RunWith(UnitilsJUnit4TestClassRunner.class)
@SpringApplicationContext({
        "spring/easyrecDataSource.xml",
        "spring/core/dao/RecommendedItemDAO.xml"})
@DataSet(value = RecommendedItemDAOTest.DATA_FILENAME)
public class RecommendedItemDAOTest {
    // constants
    // filenames of xml files
    public final static String DATA_FILENAME = "/dbunit/core/dao/recommendation_and_recommended_items.xml";
    public final static String DATA_FILENAME_ONE_LESS = "/dbunit/core/dao/recommendation_and_recommended_items_one_less.xml";
    private final static String DATA_FILENAME_ONE_LESS_RECOMMENDED_ITEM = "/dbunit/core/dao/recommended_items_one_less.xml";

    private final static String[] TABLES = new String[]{RecommendedItemDAO.DEFAULT_TABLE_NAME};

    // members
    @SpringBeanByName
    private RecommendedItemDAO recommendedItemDAO;

    @Test
    @DataSet(DATA_FILENAME_ONE_LESS_RECOMMENDED_ITEM)
    @ExpectedDataSet(DATA_FILENAME_ONE_LESS)
    public void testInsertRecommendedItem() {
        RecommendedItemVO<Integer, Integer> recommendedItem = null;
        try {
            recommendedItem = new RecommendedItemVO<Integer, Integer>(
                    new ItemVO<Integer, Integer>(1, 34, 1), 0.88d, 1, "x");
            recommendedItem.setRecommendationId(5);
        } catch (Exception e) {
            fail("caught exception: " + e);
        }
        assertTrue(recommendedItem.getId() == null);
        recommendedItemDAO.insertRecommendedItem(recommendedItem);

        assertThat(recommendedItem.getId(), is(greaterThan(9)));
    }

    @Test
    public void testInsertRecommendedItemMissingConstraint() {
        RecommendedItemVO<Integer, Integer> recommendedItem = null;
        try {
            recommendedItem = new RecommendedItemVO<Integer, Integer>(
                    new ItemVO<Integer, Integer>(1, 34, 1), 0.88d, 1, "x");
        } catch (Exception e) {
            fail("caught exception: " + e);
        }
        assertTrue(recommendedItem.getId() == null);
        try {
            recommendedItemDAO.insertRecommendedItem(recommendedItem);
            fail("exception should be thrown, since 'recommendationId' is missing");
        } catch (Exception e) {
            assertTrue("unexpected exception during insertion of recommended item: " + recommendedItem + ", " +
                    e.getMessage(), e instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testLoadRecommendedItem() {
        RecommendedItemVO<Integer, Integer> loadedRecommendedItem = recommendedItemDAO.loadRecommendedItem(9);
        RecommendedItemVO<Integer, Integer> expectedRecommendedItem = new RecommendedItemVO<Integer, Integer>(
                9, new ItemVO<Integer, Integer>(1, 33, 1), 0.89d, 5, 1, "x");
        assertEquals(expectedRecommendedItem, loadedRecommendedItem);

        loadedRecommendedItem = recommendedItemDAO.loadRecommendedItem(10);
        expectedRecommendedItem = new RecommendedItemVO<Integer, Integer>(10,
                new ItemVO<Integer, Integer>(1, 34, 1), 0.88d, 5, 1, "x");
        assertEquals(expectedRecommendedItem, loadedRecommendedItem);
    }

    @Test
    public void testGetRecommendedItemIterator() {
        Iterator<RecommendedItemVO<Integer, Integer>> recommendedItems = null;
        try {
            recommendedItems = recommendedItemDAO.getRecommendedItemIterator(5000);
        } catch (Exception e) {
            fail("caught exception: " + e);
        }
        assertTrue(recommendedItems != null);
        List<RecommendedItemVO<Integer, Integer>> recommendedItemList = iteratorToList(recommendedItems);
        assertEquals(10, recommendedItemList.size());

        // HINT: hardcoded check if list equals expected list (Mantis Issue: #721)
    }

    @Test
    public void testGetRecommendedItemIteratorConstraintFrom() {
        Iterator<RecommendedItemVO<Integer, Integer>> recommendedItems = null;
        try {
            recommendedItems = recommendedItemDAO.getRecommendedItemIterator(5000,
                    new TimeConstraintVO(new Date(new GregorianCalendar(2007, 3, 15, 12, 15).getTimeInMillis()), null));
        } catch (Exception e) {
            fail("caught exception: " + e);
        }
        assertTrue(recommendedItems != null);
        List<RecommendedItemVO<Integer, Integer>> recommendedItemList = iteratorToList(recommendedItems);
        assertEquals(6, recommendedItemList.size());

        // HINT: hardcoded check if list equals expected list (Mantis Issue: #721)
    }

    @Test
    public void testGetRecommendedItemIteratorConstraintTo() {
        Iterator<RecommendedItemVO<Integer, Integer>> recommendedItems = null;
        try {
            recommendedItems = recommendedItemDAO.getRecommendedItemIterator(5000,
                    new TimeConstraintVO(null, new Date(new GregorianCalendar(2007, 3, 15, 12, 15).getTimeInMillis())));
        } catch (Exception e) {
            fail("caught exception: " + e);
        }
        assertTrue(recommendedItems != null);
        List<RecommendedItemVO<Integer, Integer>> recommendedItemList = iteratorToList(recommendedItems);
        assertEquals(5, recommendedItemList.size());

        // HINT: hardcoded check if list equals expected list (Mantis Issue: #721)
    }

    @Test
    public void testGetRecommendedItemIteratorConstraintBoth() {
        Iterator<RecommendedItemVO<Integer, Integer>> recommendedItems = null;
        try {
            recommendedItems = recommendedItemDAO.getRecommendedItemIterator(5000,
                    new TimeConstraintVO(new Date(new GregorianCalendar(2007, 3, 15, 12, 13).getTimeInMillis()),
                            new Date(new GregorianCalendar(2007, 3, 15, 12, 17).getTimeInMillis())));
        } catch (Exception e) {
            fail("caught exception: " + e);
        }
        assertTrue(recommendedItems != null);
        List<RecommendedItemVO<Integer, Integer>> recommendedItemList = iteratorToList(recommendedItems);
        assertEquals(4, recommendedItemList.size());

        // HINT: hardcoded check if list equals expected list (Mantis Issue: #721)
    }

    @Test
    public void testGetRecommendedItemsFrom() {
        List<RecommendedItemVO<Integer, Integer>> recommendedItems = null;
        try {
            recommendedItems = recommendedItemDAO.getRecommendedItems(
                    new TimeConstraintVO(new Date(new GregorianCalendar(2007, 3, 15, 12, 15).getTimeInMillis()), null));
        } catch (Exception e) {
            fail("caught exception: " + e);
        }
        assertTrue(recommendedItems != null);
        assertEquals(6, recommendedItems.size());

        // HINT: hardcoded check if list equals expected list (Mantis Issue: #721)
    }

    @Test
    public void testGetRecommendedItemsTo() {
        List<RecommendedItemVO<Integer, Integer>> recommendedItems = null;
        try {
            recommendedItems = recommendedItemDAO.getRecommendedItems(
                    new TimeConstraintVO(null, new Date(new GregorianCalendar(2007, 3, 15, 12, 15).getTimeInMillis())));
        } catch (Exception e) {
            fail("caught exception: " + e);
        }
        assertTrue(recommendedItems != null);
        assertEquals(5, recommendedItems.size());

        // HINT: hardcoded check if list equals expected list (Mantis Issue: #721)
    }

    @Test
    public void testGetRecommendedItemsBoth() {
        List<RecommendedItemVO<Integer, Integer>> recommendedItems = null;
        try {
            recommendedItems = recommendedItemDAO.getRecommendedItems(
                    new TimeConstraintVO(new Date(new GregorianCalendar(2007, 3, 15, 12, 13).getTimeInMillis()),
                            new Date(new GregorianCalendar(2007, 3, 15, 12, 17).getTimeInMillis())));
        } catch (Exception e) {
            fail("caught exception: " + e);
        }
        assertTrue(recommendedItems != null);
        assertEquals(4, recommendedItems.size());

        // HINT: hardcoded check if list equals expected list (Mantis Issue: #721)
    }

    @Test
    public void testGetRecommendedItemsOfRecommendation() {
        List<RecommendedItemVO<Integer, Integer>> expectedRecommendedItems = new ArrayList<RecommendedItemVO<Integer, Integer>>();
        expectedRecommendedItems.add(new RecommendedItemVO<Integer, Integer>(9,
                new ItemVO<Integer, Integer>(1, 33, 1), 0.89d, 5, 1, "x"));
        expectedRecommendedItems.add(new RecommendedItemVO<Integer, Integer>(10,
                new ItemVO<Integer, Integer>(1, 34, 1), 0.88d, 5, 1, "x"));
        List<RecommendedItemVO<Integer, Integer>> loadedRecommendedItems = recommendedItemDAO
                .getRecommendedItemsOfRecommendation(5);
        assertEquals(expectedRecommendedItems, loadedRecommendedItems);
    }

    @Test
    public void testGetRecommendedItemsOfRecommendationKnowingTenant() {
        List<RecommendedItemVO<Integer, Integer>> expectedRecommendedItems = new ArrayList<RecommendedItemVO<Integer, Integer>>();
        expectedRecommendedItems.add(new RecommendedItemVO<Integer, Integer>(9,
                new ItemVO<Integer, Integer>(1, 33, 1), 0.89d, 5, 1, "x"));
        expectedRecommendedItems.add(new RecommendedItemVO<Integer, Integer>(10,
                new ItemVO<Integer, Integer>(1, 34, 1), 0.88d, 5, 1, "x"));
        List<RecommendedItemVO<Integer, Integer>> loadedRecommendedItems = recommendedItemDAO
                .getRecommendedItemsOfRecommendation(5, 1);
        assertEquals(expectedRecommendedItems, loadedRecommendedItems);
    }

    // private methods
    private List<RecommendedItemVO<Integer, Integer>> iteratorToList(
            Iterator<RecommendedItemVO<Integer, Integer>> recommendedItems) {
        List<RecommendedItemVO<Integer, Integer>> recommendedItemList = new ArrayList<RecommendedItemVO<Integer, Integer>>();
        while (recommendedItems.hasNext()) {
            recommendedItemList.add(recommendedItems.next());
        }
        return recommendedItemList;
    }
}