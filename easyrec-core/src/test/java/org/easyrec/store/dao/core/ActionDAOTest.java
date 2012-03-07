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

import org.easyrec.model.core.ActionVO;
import org.easyrec.model.core.ItemVO;
import org.easyrec.model.core.RankedItemVO;
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
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

@RunWith(UnitilsJUnit4TestClassRunner.class)
@SpringApplicationContext({
        "spring/easyrecDataSource.xml",
        "spring/core/TenantConfig_DEFAULT.xml",
        "spring/core/dao/AuthenticationDAO.xml",
        "spring/core/dao/types/ActionTypeDAO.xml",
        "spring/core/dao/types/AggregateTypeDAO.xml",
        "spring/core/dao/types/AssocTypeDAO.xml",
        "spring/core/dao/types/ItemTypeDAO.xml",
        "spring/core/dao/types/SourceTypeDAO.xml",
        "spring/core/dao/types/ViewTypeDAO.xml",
        "spring/core/dao/ItemAssocDAO.xml",
        "spring/core/dao/TenantDAO.xml",
        "spring/core/dao/ProfileDAO.xml",
        "spring/core/idMapping.xml",
        "spring/core/service/ItemAssocService.xml",
        "spring/core/service/TenantService.xml",
        "spring/core/service/ClusterService.xml",
        "spring/core/service/ClusterStrategies.xml",
        "spring/core/dao/ActionDAO.xml"})
@DataSet(value = ActionDAOTest.DATA_FILENAME)
public class ActionDAOTest {
    // constants
    // filenames of xml files
    public final static String DATA_FILENAME = "/dbunit/core/dao/action.xml";
    public final static String DATA_FILENAME_NO_ACTIONTIME = "/dbunit/core/dao/action_no_actiontime.xml";
    public final static String DATA_FILENAME_ONE_LESS = "/dbunit/core/dao/action_one_less.xml";
    public final static String DATA_FILENAME_SOME_MORE_WITH_ALL_FIELDS_SET = "/dbunit/core/dao/action_some_more_with_all_fields_set.xml";
    public final static String DATA_FILENAME_RANKINGS = "/dbunit/core/dao/action_rankings.xml";

    // members
    @SpringBeanByName
    private ActionDAO actionDAO;

    @Test
    @DataSet(DATA_FILENAME_ONE_LESS)
    @ExpectedDataSet(DATA_FILENAME_NO_ACTIONTIME)
    public void testInsertAction() {
        ActionVO<Integer, Integer> action = null;
        try {
            action = new ActionVO<Integer, Integer>(2, 2, "abc5", "127.0.0.1",
                    new ItemVO<Integer, Integer>(2, 1, 1), 1, null, null, null, null);
        } catch (Exception e) {
            fail("caught exception: " + e);
        }
        assertTrue(action.getId() == null);
        actionDAO.insertAction(action, false);

        assertThat(action.getId(), is(not(1)));
        assertThat(action.getId(), is(not(2)));
        assertThat(action.getId(), is(not(3)));
        assertThat(action.getId(), is(not(4)));
        assertThat(action.getId(), is(not(5)));
    }

    @Test
    public void testRemoveActionsByTenant() {
        actionDAO.removeActionsByTenant(1);
        Iterator<ActionVO<Integer, Integer>> actions = actionDAO.getActionIterator(5000);
        assertTrue(actions != null);
        List<ActionVO<Integer, Integer>> actionsList = iteratorToList(actions);
        assertEquals(2, actionsList.size());
    }

    @Test
    @ExpectedDataSet(DATA_FILENAME_SOME_MORE_WITH_ALL_FIELDS_SET)
    public void testInsertActionAllFieldsSet() {
        ActionVO<Integer, Integer> action = null;
        // search (failed)
        try {
            action = new ActionVO<Integer, Integer>(2, 2, "abc6", "192.168.124.1",
                    new ItemVO<Integer, Integer>(2, null, 2), 4, null, false, 0, "modana");
        } catch (Exception e) {
            fail("caught exception: " + e);
        }
        actionDAO.insertAction(action, false);

        // search (succeeded)
        try {
            action = new ActionVO<Integer, Integer>(2, 2, "abc6", "192.168.124.2",
                    new ItemVO<Integer, Integer>(2, 13, 2), 4, null, true, 1, "madonna");
        } catch (Exception e) {
            fail("caught exception: " + e);
        }
        actionDAO.insertAction(action, false);

        // ratingValue set
        try {
            action = new ActionVO<Integer, Integer>(2, 2, "abc6", "192.168.124.3",
                    new ItemVO<Integer, Integer>(2, 19, 1), 3, 7, null, null, null);
        } catch (Exception e) {
            fail("caught exception: " + e);
        }
        actionDAO.insertAction(action, false);
    }


    @Test
    public void testInsertActionMissingConstraint() {
        ActionVO<Integer, Integer> action = null;
        try {
            action = new ActionVO<Integer, Integer>(2, 2, "abc5", "127.0.0.1",
                    new ItemVO<Integer, Integer>(2, 1, 1), null, null, null, null, null);
            actionDAO.insertAction(action, false);
            fail("exception should be thrown, since 'actionTypeId' is missing");
        } catch (Exception e) {
            assertTrue("unexpected exception during insertion of action: " + action + ", " + e.getMessage(),
                    e instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testGetActionIterator() {
        Iterator<ActionVO<Integer, Integer>> actions = actionDAO.getActionIterator(5000);
        assertTrue(actions != null);
        List<ActionVO<Integer, Integer>> actionsList = iteratorToList(actions);
        assertEquals(6, actionsList.size());

        // HINT: hardcoded check if list equals expected list (Mantis Issue: #721)
    }

    @Test
    public void testGetActionIteratorConstraintsFrom() {
        Iterator<ActionVO<Integer, Integer>> actions = null;
        try {
            actions = actionDAO.getActionIterator(5000,
                    new TimeConstraintVO(new Date(new GregorianCalendar(2007, 3, 15, 12, 14).getTimeInMillis()), null));
        } catch (Exception e) {
            fail("caught exception: " + e);
        }
        assertTrue(actions != null);
        List<ActionVO<Integer, Integer>> actionsList = iteratorToList(actions);
        assertEquals(3, actionsList.size());

        // HINT: hardcoded check if list equals expected list (Mantis Issue: #721)
    }

    @Test
    public void testGetActionIteratorConstraintsTo() {
        Iterator<ActionVO<Integer, Integer>> actions = null;
        try {
            actions = actionDAO.getActionIterator(5000,
                    new TimeConstraintVO(null, new Date(new GregorianCalendar(2007, 3, 15, 12, 15).getTimeInMillis())));
        } catch (Exception e) {
            fail("caught exception: " + e);
        }
        assertTrue(actions != null);
        List<ActionVO<Integer, Integer>> actionsList = iteratorToList(actions);
        assertEquals(5, actionsList.size());

        // HINT: hardcoded check if list equals expected list (Mantis Issue: #721)
    }

    @Test
    public void testGetActionIteratorConstraintsBoth() {
        Iterator<ActionVO<Integer, Integer>> actions = null;
        try {
            actions = actionDAO.getActionIterator(5000,
                    new TimeConstraintVO(new Date(new GregorianCalendar(2007, 3, 15, 12, 12).getTimeInMillis()),
                            new Date(new GregorianCalendar(2007, 3, 15, 12, 15).getTimeInMillis())));
        } catch (Exception e) {
            fail("caught exception: " + e);
        }
        assertTrue(actions != null);
        List<ActionVO<Integer, Integer>> actionsList = iteratorToList(actions);
        assertEquals(4, actionsList.size());

        // HINT: hardcoded check if list equals expected list (Mantis Issue: #721)
    }

    @Test
    public void testGetNewestActionDate() {
        try {
            assertEquals(new Date(new GregorianCalendar(2007, 3, 15, 12, 16).getTimeInMillis()),
                    actionDAO.getNewestActionDate());
        } catch (Exception e) {
            fail("caught exception: " + e);
        }
    }

    @Test
    @DataSet(DATA_FILENAME_RANKINGS)
    public void testGetRankedItemsByActionType() {
        List<RankedItemVO<Integer, Integer>> retList = actionDAO
                .getRankedItemsByActionType(1, 1, 1, 500, null, true);
        assertEquals(6, retList.size());

        retList = actionDAO.getRankedItemsByActionType(1, 2, 1, 500, null, true);
        assertEquals(6, retList.size());

        retList = actionDAO.getRankedItemsByActionType(1, 3, 1, 500, null, true);
        assertEquals(5, retList.size());

        retList = actionDAO.getRankedItemsByActionType(1, 4, 1, 500, null, true);
        assertEquals(4, retList.size());

        retList = actionDAO.getRankedItemsByActionType(1, 1, 2, 500, null, true);
        assertEquals(3, retList.size());

        // HINT: hardcoded check if lists equal expected lisst (Mantis Issue: #721)
    }

    @Test
    public void testGetItemsOfTenant() {
        List<ItemVO<Integer, Integer>> result = actionDAO.getItemsOfTenant(1, 1);

        assertEquals(1, result.size());
        assertEquals((long) result.get(0).getItem(), 1L);
        assertEquals((long) result.get(0).getType(), 1L);
        assertEquals((long) result.get(0).getTenant(), 1L);
    }

    // private methods
    private List<ActionVO<Integer, Integer>> iteratorToList(
            Iterator<ActionVO<Integer, Integer>> actions) {
        List<ActionVO<Integer, Integer>> actionsList = new ArrayList<ActionVO<Integer, Integer>>();
        while (actions.hasNext()) {
            actionsList.add(actions.next());
        }
        return actionsList;
    }
}
