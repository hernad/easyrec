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
package org.easyrec.store.dao.core.types;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.annotation.ExpectedDataSet;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import static junit.framework.Assert.*;

@RunWith(UnitilsJUnit4TestClassRunner.class)
@SpringApplicationContext({
        "spring/easyrecDataSource.xml",
        "spring/core/dao/types/ActionTypeDAO.xml"})
@DataSet(value = ActionTypeDAOTest.DATA_FILENAME)
public class ActionTypeDAOTest {
    // constants
    private final static Integer TENANT_ID = 0;

    // filenames of xml files
    public final static String DATA_FILENAME = "/dbunit/core/dao/types/actiontype.xml";
    public final static String DATA_FILENAME_ONE_LESS = "/dbunit/core/dao/types/actiontype_one_less.xml";
    public final static String DATA_FILENAME_CHANGED_IDS = "/dbunit/core/dao/types/actiontype_changed_ids.xml";

    // members
    @SpringBeanByName
    private ActionTypeDAO actionTypeDAO;

    @Test
    public void testGetActionTypeById() {
        String actionType = actionTypeDAO.getTypeById(TENANT_ID, 1);
        assertEquals("ADD_TO_PLAYLIST", actionType);

        actionType = actionTypeDAO.getTypeById(TENANT_ID, 2);
        assertEquals("BUY", actionType);

        try {
            actionType = actionTypeDAO.getTypeById(TENANT_ID, 12345);
            fail("IllegalArgumentException should have been thrown, since 'id' for actionType was unknown");
        } catch (IllegalArgumentException e) {
        }

        actionType = actionTypeDAO.getTypeById(TENANT_ID, null);
        assertNull(actionType);
    }

    @Test
    public void testGetIdOfActionType() {
        Integer id = actionTypeDAO.getIdOfType(TENANT_ID, "ADD_TO_PLAYLIST");
        assertEquals(1, (int)id);

        id = actionTypeDAO.getIdOfType(TENANT_ID, "BUY");
        assertEquals(2, (int)id);

        try {
            id = actionTypeDAO.getIdOfType(TENANT_ID, "ASDF");
            fail("IllegalArgumentException should have been thrown, since 'actionType' was unknown");
        } catch (IllegalArgumentException e) {
        }

        id = actionTypeDAO.getIdOfType(TENANT_ID, null);
        assertNull(id);
    }

    @Test
    @DataSet(DATA_FILENAME_ONE_LESS)
    @ExpectedDataSet(DATA_FILENAME)
    public void testInsertActionType() {
        actionTypeDAO.insertOrUpdate(TENANT_ID, "VIEW", 6);
    }

    @Test
    @ExpectedDataSet(DATA_FILENAME_CHANGED_IDS)
    public void testUpdateActionType() {
        actionTypeDAO.insertOrUpdate(TENANT_ID, "ADD_TO_PLAYLIST", 10);
        actionTypeDAO.insertOrUpdate(TENANT_ID, "BUY", 20);
        actionTypeDAO.insertOrUpdate(TENANT_ID, "PREVIEW", 30);
        actionTypeDAO.insertOrUpdate(TENANT_ID, "RATE", 40);
        actionTypeDAO.insertOrUpdate(TENANT_ID, "SEARCH", 50);
        actionTypeDAO.insertOrUpdate(TENANT_ID, "VIEW", 60);
    }

    @Test
    public void testExistsActionTypeTable() {
        assertTrue(actionTypeDAO.existsTable());
    }

    @Test
    public void testGetTypes() {
        Set<String> expectedTypes = new TreeSet<String>();
        expectedTypes.add("ADD_TO_PLAYLIST");
        expectedTypes.add("BUY");
        expectedTypes.add("PREVIEW");
        expectedTypes.add("RATE");
        expectedTypes.add("SEARCH");
        expectedTypes.add("VIEW");
        assertEquals(expectedTypes, actionTypeDAO.getTypes(TENANT_ID));
    }

    @Test
    public void testGetMapping() {
        HashMap<String, Integer> expectedMapping = new HashMap<String, Integer>();
        expectedMapping.put("ADD_TO_PLAYLIST", 1);
        expectedMapping.put("BUY", 2);
        expectedMapping.put("PREVIEW", 3);
        expectedMapping.put("RATE", 4);
        expectedMapping.put("SEARCH", 5);
        expectedMapping.put("VIEW", 6);

        HashMap<String, Integer> actualMapping = actionTypeDAO.getMapping(TENANT_ID);
        assertEquals(expectedMapping, actualMapping);
    }
}
