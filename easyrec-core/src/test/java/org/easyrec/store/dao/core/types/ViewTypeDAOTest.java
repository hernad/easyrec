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
        "spring/core/dao/types/ViewTypeDAO.xml"})
@DataSet(value = ViewTypeDAOTest.DATA_FILENAME)
public class ViewTypeDAOTest {
    // constants
    private final static Integer TENANT_ID = 0;

    // filenames of xml files
    public final static String DATA_FILENAME = "/dbunit/core/dao/types/viewtype.xml";
    public final static String DATA_FILENAME_ONE_LESS = "/dbunit/core/dao/types/viewtype_one_less.xml";
    public final static String DATA_FILENAME_CHANGED_IDS = "/dbunit/core/dao/types/viewtype_changed_ids.xml";

    // members
    @SpringBeanByName
    private ViewTypeDAO viewTypeDAO;

    @Test
    public void testGetViewTypeById() {
        String viewType = viewTypeDAO.getTypeById(TENANT_ID, 1);
        assertEquals("ADMIN", viewType);

        viewType = viewTypeDAO.getTypeById(TENANT_ID, 2);
        assertEquals("COMMUNITY", viewType);

        try {
            viewType = viewTypeDAO.getTypeById(TENANT_ID, 12345);
            fail("IllegalArgumentException should have been thrown, since 'id' for viewType was unknown");
        } catch (IllegalArgumentException e) {
        }

        viewType = viewTypeDAO.getTypeById(TENANT_ID, null);
        assertNull(viewType);
    }

    @Test
    public void testGetIdOfViewType() {
        Integer id = viewTypeDAO.getIdOfType(TENANT_ID, "ADMIN");
        assertEquals(new Integer(1), id);

        id = viewTypeDAO.getIdOfType(TENANT_ID, "COMMUNITY");
        assertEquals(new Integer(2), id);

        try {
            id = viewTypeDAO.getIdOfType(TENANT_ID, "ASDF");
            fail("IllegalArgumentException should have been thrown, since 'viewType' was unknown");
        } catch (IllegalArgumentException e) {
        }

        id = viewTypeDAO.getIdOfType(TENANT_ID, null);
        assertNull(id);
    }

    @Test
    @DataSet(DATA_FILENAME_ONE_LESS)
    @ExpectedDataSet(DATA_FILENAME)
    public void testInsertViewType() {
        viewTypeDAO.insertOrUpdate(TENANT_ID, "SYSTEM", 3);
    }

    @Test
    @ExpectedDataSet(DATA_FILENAME_CHANGED_IDS)
    public void testUpdateViewType() {
        viewTypeDAO.insertOrUpdate(TENANT_ID, "ADMIN", 10);
        viewTypeDAO.insertOrUpdate(TENANT_ID, "COMMUNITY", 20);
        viewTypeDAO.insertOrUpdate(TENANT_ID, "SYSTEM", 30);
    }

    @Test
    public void testExistsViewTypeTable() {
        assertTrue(viewTypeDAO.existsTable());
    }

    @Test
    public void testGetTypes() {
        Set<String> expectedTypes = new TreeSet<String>();
        expectedTypes.add("ADMIN");
        expectedTypes.add("COMMUNITY");
        expectedTypes.add("SYSTEM");
        assertEquals(expectedTypes, viewTypeDAO.getTypes(TENANT_ID));
    }

    @Test
    public void testGetMapping() {
        HashMap<String, Integer> expectedMapping = new HashMap<String, Integer>();
        expectedMapping.put("ADMIN", 1);
        expectedMapping.put("COMMUNITY", 2);
        expectedMapping.put("SYSTEM", 3);

        HashMap<String, Integer> actualMapping = viewTypeDAO.getMapping(TENANT_ID);
        assertEquals(expectedMapping, actualMapping);
    }
}
