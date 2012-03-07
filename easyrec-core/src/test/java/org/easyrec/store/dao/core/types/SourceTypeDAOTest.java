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
        "spring/core/dao/types/SourceTypeDAO.xml"})
@DataSet(value = SourceTypeDAOTest.DATA_FILENAME)
public class SourceTypeDAOTest {
    // constants
    private final static Integer TENANT_ID = 0;

    // filenames of xml files
    public final static String DATA_FILENAME = "/dbunit/core/dao/types/sourcetype.xml";
    public final static String DATA_FILENAME_ONE_LESS = "/dbunit/core/dao/types/sourcetype_one_less.xml";
    public final static String DATA_FILENAME_CHANGED_IDS = "/dbunit/core/dao/types/sourcetype_changed_ids.xml";

    // members
    @SpringBeanByName
    private SourceTypeDAO sourceTypeDAO;

    @Test
    public void testGetSourceTypeById() {
        String sourceType = sourceTypeDAO.getTypeById(TENANT_ID, 1);
        assertEquals("AMG", sourceType);

        sourceType = sourceTypeDAO.getTypeById(TENANT_ID, 2);
        assertEquals("FE", sourceType);

        try {
            sourceType = sourceTypeDAO.getTypeById(TENANT_ID, 12345);
            fail("IllegalArgumentException should have been thrown, since 'id' for sourceType was unknown");
        } catch (IllegalArgumentException e) {
        }

        sourceType = sourceTypeDAO.getTypeById(TENANT_ID, null);
        assertNull(sourceType);
    }

    @Test
    public void testGetIdOfSourceType() {
        Integer id = sourceTypeDAO.getIdOfType(TENANT_ID, "AMG");
        assertEquals(new Integer(1), id);

        id = sourceTypeDAO.getIdOfType(TENANT_ID, "FE");
        assertEquals(new Integer(2), id);

        id = sourceTypeDAO.getIdOfType(TENANT_ID, "UM");
        assertEquals(new Integer(3), id);

        try {
            id = sourceTypeDAO.getIdOfType(TENANT_ID, "ASDF");
            fail("IllegalArgumentException should have been thrown, since 'sourceType' was unknown");
        } catch (IllegalArgumentException e) {
        }

        id = sourceTypeDAO.getIdOfType(TENANT_ID, null);
        assertNull(id);
    }

    @Test
    @DataSet(DATA_FILENAME_ONE_LESS)
    @ExpectedDataSet(DATA_FILENAME)
    public void testInsertSourceType() {
        sourceTypeDAO.insertOrUpdate(TENANT_ID, "UM", 3);
    }

    @Test
    @ExpectedDataSet(DATA_FILENAME_CHANGED_IDS)
    public void testUpdateSourceType() {
        sourceTypeDAO.insertOrUpdate(TENANT_ID, "AMG", 10);
        sourceTypeDAO.insertOrUpdate(TENANT_ID, "FE", 20);
        sourceTypeDAO.insertOrUpdate(TENANT_ID, "UM", 30);
    }

    @Test
    public void testExistsSourceTypeTable() {
        assertTrue(sourceTypeDAO.existsTable());
    }

    @Test
    public void testGetTypes() {
        Set<String> expectedTypes = new TreeSet<String>();
        expectedTypes.add("AMG");
        expectedTypes.add("FE");
        expectedTypes.add("UM");
        assertEquals(expectedTypes, sourceTypeDAO.getTypes(TENANT_ID));
    }

    @Test
    public void testGetMapping() {
        HashMap<String, Integer> expectedMapping = new HashMap<String, Integer>();
        expectedMapping.put("AMG", 1);
        expectedMapping.put("FE", 2);
        expectedMapping.put("UM", 3);

        HashMap<String, Integer> actualMapping = sourceTypeDAO.getMapping(TENANT_ID);
        assertEquals(expectedMapping, actualMapping);
    }
}
