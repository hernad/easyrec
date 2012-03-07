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

import org.dbunit.dataset.Column;
import org.dbunit.dataset.filter.IColumnFilter;
import org.easyrec.model.core.ItemVO;
import org.easyrec.model.core.RecommendationVO;
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.*;

@RunWith(UnitilsJUnit4TestClassRunner.class)
@SpringApplicationContext({
        "spring/easyrecDataSource.xml",
        "spring/core/dao/RecommendationDAO.xml",
        "spring/core/dao/RecommendedItemDAO.xml"})
@DataSet(value = RecommendationDAOTest.DATA_FILENAME)
public class RecommendationDAOTest {
    // filenames of xml files
    public final static String DATA_FILENAME = "/dbunit/core/dao/recommendation_and_recommended_items.xml";
    public final static String DATA_FILENAME_NO_TIME =
            "/dbunit/core/dao/recommendation_and_recommended_items_no_time.xml";
    public final static String DATA_FILENAME_ONE_LESS = "/dbunit/core/dao/recommendation_and_recommended_items_one_less.xml";

    // column filter used to skip actionTime column
    private final static IColumnFilter recommendationTimeColumnFilter = new IColumnFilter() {
        public boolean accept(String tableName, Column column) {
            if (tableName.equals(RecommendationDAO.DEFAULT_TABLE_NAME) &&
                    column.getColumnName().equals(RecommendationDAO.DEFAULT_RECOMMENDATION_TIME_COLUMN_NAME)) {
                return false;
            }
            return true;
        }
    };

    // members
    @SpringBeanByName
    private RecommendationDAO recommendationDAO;

    @Test
    public void testGetTenantIdOfRecommendationById() {
        assertEquals(1, recommendationDAO.getTenantIdOfRecommendationById(1, true).intValue());
        assertEquals(1, recommendationDAO.getTenantIdOfRecommendationById(2, true).intValue());
        assertEquals(1, recommendationDAO.getTenantIdOfRecommendationById(3, true).intValue());
        assertEquals(2, recommendationDAO.getTenantIdOfRecommendationById(4, true).intValue());
        assertEquals(1, recommendationDAO.getTenantIdOfRecommendationById(5, true).intValue());

        assertEquals(1, recommendationDAO.getTenantIdOfRecommendationById(1, false).intValue());
        assertEquals(1, recommendationDAO.getTenantIdOfRecommendationById(2, false).intValue());
        assertEquals(1, recommendationDAO.getTenantIdOfRecommendationById(3, false).intValue());
        assertEquals(2, recommendationDAO.getTenantIdOfRecommendationById(4, false).intValue());
        assertEquals(1, recommendationDAO.getTenantIdOfRecommendationById(5, false).intValue());
    }

    @Test
    @DataSet(DATA_FILENAME_ONE_LESS)
    @ExpectedDataSet(DATA_FILENAME_NO_TIME)
    public void testInsertRecommendation() {
        RecommendationVO<Integer, Integer> recommendation = null;
        try {
            List<RecommendedItemVO<Integer, Integer>> recommendedItems = new ArrayList<RecommendedItemVO<Integer, Integer>>();
            recommendedItems.add(new RecommendedItemVO<Integer, Integer>(
                    new ItemVO<Integer, Integer>(1, 33, 1), 0.89d, 1, "x"));
            recommendedItems.add(new RecommendedItemVO<Integer, Integer>(
                    new ItemVO<Integer, Integer>(1, 34, 1), 0.88d, 1, "x"));
            recommendation = new RecommendationVO<Integer, Integer>(1, 3, 1, 1, 1,
                    1, "a", "b", recommendedItems);
        } catch (Exception e) {
            fail("caught exception: " + e);
        }
        assertTrue(recommendation.getId() == null);
        recommendationDAO.insertRecommendation(recommendation);

        assertThat(recommendation.getId(), is(greaterThan(4)));
        assertThat(recommendation.getRecommendedItems().get(0).getId(), is(greaterThan(8)));
        assertThat(recommendation.getRecommendedItems().get(1).getId(), is(greaterThan(9)));
    }

    @Test
    public void testLoadRecommendation() {
        RecommendationVO<Integer, Integer> loadedRecommendation = recommendationDAO
                .loadRecommendation(5);
        List<RecommendedItemVO<Integer, Integer>> recommendedItems = new ArrayList<RecommendedItemVO<Integer, Integer>>();
        recommendedItems.add(
                new RecommendedItemVO<Integer, Integer>(9, new ItemVO<Integer, Integer>(1, 33, 1),
                        0.89d, 5, 1, "x"));
        recommendedItems.add(new RecommendedItemVO<Integer, Integer>(10,
                new ItemVO<Integer, Integer>(1, 34, 1), 0.88d, 5, 1, "x"));
        RecommendationVO<Integer, Integer> expectedRecommendation = new RecommendationVO<Integer, Integer>(
                5, 1, 3, 1, 1, 1, 1, "a", "b", new Date(new GregorianCalendar(2007, 3, 15, 12, 18).getTimeInMillis()),
                recommendedItems);
        assertEquals(expectedRecommendation, loadedRecommendation);
    }

    @Test
    public void getRecommendationIterator() {
        Iterator<RecommendationVO<Integer, Integer>> recommendations = null;
        try {
            recommendations = recommendationDAO.getRecommendationIterator(5000);
        } catch (Exception e) {
            fail("caught exception: " + e);
        }
        assertTrue(recommendations != null);
        List<RecommendationVO<Integer, Integer>> recommendationsList = iteratorToList(
                recommendations);
        assertEquals(5, recommendationsList.size());
        assertEquals(2, recommendationsList.get(0).getRecommendedItems().size());
        assertEquals(2, recommendationsList.get(1).getRecommendedItems().size());
        assertEquals(1, recommendationsList.get(2).getRecommendedItems().size());
        assertEquals(3, recommendationsList.get(3).getRecommendedItems().size());
        assertEquals(2, recommendationsList.get(4).getRecommendedItems().size());

        // HINT: hardcoded check if list equals expected list (Mantis Issue: #721)
    }

    @Test
    public void getRecommendationIteratorConstraintsFrom() {
        Iterator<RecommendationVO<Integer, Integer>> recommendations = null;
        try {
            recommendations = recommendationDAO.getRecommendationIterator(5000,
                    new TimeConstraintVO(new Date(new GregorianCalendar(2007, 3, 15, 12, 15).getTimeInMillis()), null));
        } catch (Exception e) {
            fail("caught exception: " + e);
        }
        assertTrue(recommendations != null);
        List<RecommendationVO<Integer, Integer>> recommendationsList = iteratorToList(
                recommendations);
        assertEquals(3, recommendationsList.size());
        assertEquals(1, recommendationsList.get(0).getRecommendedItems().size());
        assertEquals(3, recommendationsList.get(1).getRecommendedItems().size());
        assertEquals(2, recommendationsList.get(2).getRecommendedItems().size());

        // HINT: hardcoded check if list equals expected list (Mantis Issue: #721)
    }

    @Test
    public void getRecommendationIteratorConstraintsTo() {
        Iterator<RecommendationVO<Integer, Integer>> recommendations = null;
        try {
            recommendations = recommendationDAO.getRecommendationIterator(5000,
                    new TimeConstraintVO(null, new Date(new GregorianCalendar(2007, 3, 15, 12, 14).getTimeInMillis())));
        } catch (Exception e) {
            fail("caught exception: " + e);
        }
        assertTrue(recommendations != null);
        List<RecommendationVO<Integer, Integer>> recommendationsList = iteratorToList(
                recommendations);
        assertEquals(2, recommendationsList.size());
        assertEquals(2, recommendationsList.get(0).getRecommendedItems().size());
        assertEquals(2, recommendationsList.get(1).getRecommendedItems().size());

        // HINT: hardcoded check if list equals expected list (Mantis Issue: #721)
    }

    @Test
    public void getRecommendationIteratorConstraintsBoth() {
        Iterator<RecommendationVO<Integer, Integer>> recommendations = null;
        try {
            recommendations = recommendationDAO.getRecommendationIterator(5000,
                    new TimeConstraintVO(new Date(new GregorianCalendar(2007, 3, 15, 12, 12).getTimeInMillis()),
                            new Date(new GregorianCalendar(2007, 3, 15, 12, 17).getTimeInMillis())));
        } catch (Exception e) {
            fail("caught exception: " + e);
        }
        assertTrue(recommendations != null);
        List<RecommendationVO<Integer, Integer>> recommendationsList = iteratorToList(
                recommendations);
        assertEquals(3, recommendationsList.size());
        assertEquals(2, recommendationsList.get(0).getRecommendedItems().size());
        assertEquals(1, recommendationsList.get(1).getRecommendedItems().size());
        assertEquals(3, recommendationsList.get(2).getRecommendedItems().size());

        // HINT: hardcoded check if list equals expected list (Mantis Issue: #721)
    }

    // private methods
    private List<RecommendationVO<Integer, Integer>> iteratorToList(
            Iterator<RecommendationVO<Integer, Integer>> recommendations) {
        List<RecommendationVO<Integer, Integer>> recommendationsList = new ArrayList<RecommendationVO<Integer, Integer>>();
        while (recommendations.hasNext()) {
            recommendationsList.add(recommendations.next());
        }
        return recommendationsList;
    }
}
