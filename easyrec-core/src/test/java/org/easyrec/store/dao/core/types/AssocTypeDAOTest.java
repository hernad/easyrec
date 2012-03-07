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
import org.springframework.dao.EmptyResultDataAccessException;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.annotation.ExpectedDataSet;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import static junit.framework.Assert.fail;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(UnitilsJUnit4TestClassRunner.class)
@SpringApplicationContext({
        "spring/easyrecDataSource.xml",
        "spring/core/dao/types/AssocTypeDAO.xml"})
@DataSet(value = AssocTypeDAOTest.DATA_FILENAME)
public class AssocTypeDAOTest {
    // constants
    private final static Integer TENANT_ID = 0;

    // filenames of xml files
    public final static String DATA_FILENAME = "/dbunit/core/dao/types/assoctype.xml";
    public final static String DATA_FILENAME_ONE_LESS = "/dbunit/core/dao/types/assoctype_one_less.xml";
    public final static String DATA_FILENAME_CHANGED_IDS = "/dbunit/core/dao/types/assoctype_changed_ids.xml";

    // members
    @SpringBeanByName
    private AssocTypeDAO assocTypeDAO;

    @Test
    public void testGetAssocTypeById() {
        String assocType = assocTypeDAO.getTypeById(TENANT_ID, 1);
        assertThat(assocType, is("BOUGHT_TOGETHER"));

        assocType = assocTypeDAO.getTypeById(TENANT_ID, 2);
        assertThat(assocType, is("COLL_TOGETHER"));

        assocType = assocTypeDAO.getTypeById(TENANT_ID, 8);
        assertThat(assocType, is("INVISIBLE"));

        try {
            assocTypeDAO.getTypeById(TENANT_ID, 12345);
            fail("IllegalArgumentException should have been thrown, since 'id' for assocType was unknown");
        } catch (IllegalArgumentException e) {
        }

        assocType = assocTypeDAO.getTypeById(TENANT_ID, null);
        assertThat(assocType, is(nullValue()));
    }

    @Test
    public void testGetIdOfAssocType() {
        Integer id = assocTypeDAO.getIdOfType(TENANT_ID, "BOUGHT_TOGETHER");
        assertThat(id, is(1));

        id = assocTypeDAO.getIdOfType(TENANT_ID, "COLL_TOGETHER");
        assertThat(id, is(2));

        id = assocTypeDAO.getIdOfType(TENANT_ID, "INVISIBLE");
        assertThat(id, is(8));

        try {
            assocTypeDAO.getIdOfType(TENANT_ID, "ASDF");
            fail("IllegalArgumentException should have been thrown, since 'assocType' was unknown");
        } catch (IllegalArgumentException e) {
        }

        id = assocTypeDAO.getIdOfType(TENANT_ID, null);
        assertThat(id, is(nullValue()));
    }

    @Test
    @DataSet(DATA_FILENAME_ONE_LESS)
    @ExpectedDataSet(DATA_FILENAME)
    public void testInsertAssocType() {
        assocTypeDAO.insertOrUpdate(TENANT_ID, "VIEWED_TOGETHER", 7);
        assocTypeDAO.insertOrUpdate(TENANT_ID, "INVISIBLE", 8, false);
    }

    @Test
    @ExpectedDataSet(DATA_FILENAME_CHANGED_IDS)
    public void testUpdateAssocType() {
        assocTypeDAO.insertOrUpdate(TENANT_ID, "BOUGHT_TOGETHER", 10);
        assocTypeDAO.insertOrUpdate(TENANT_ID, "COLL_TOGETHER", 20);
        assocTypeDAO.insertOrUpdate(TENANT_ID, "IS_ELEMENT_OF", 30);
        assocTypeDAO.insertOrUpdate(TENANT_ID, "IS_SIMILAR_TO", 40);
        assocTypeDAO.insertOrUpdate(TENANT_ID, "RATED_TOGETHER", 50);
        assocTypeDAO.insertOrUpdate(TENANT_ID, "SOUNDS_SIMILAR", 60);
        assocTypeDAO.insertOrUpdate(TENANT_ID, "VIEWED_TOGETHER", 70);
        assocTypeDAO.insertOrUpdate(TENANT_ID, "INVISIBLE", 80);
    }

    @Test
    public void testExistsAssocTypeTable() {
        assertThat(assocTypeDAO.existsTable(), is(true));
    }

    @Test
    public void testGetTypes() {
        Set<String> expectedTypes = new TreeSet<String>();
        expectedTypes.add("BOUGHT_TOGETHER");
        expectedTypes.add("COLL_TOGETHER");
        expectedTypes.add("IS_ELEMENT_OF");
        expectedTypes.add("IS_SIMILAR_TO");
        expectedTypes.add("RATED_TOGETHER");
        expectedTypes.add("SOUNDS_SIMILAR");
        expectedTypes.add("VIEWED_TOGETHER");
        expectedTypes.add("INVISIBLE");

        assertThat(assocTypeDAO.getTypes(TENANT_ID), is(expectedTypes));
    }

    @Test
    public void testGetTypesVisible() {
        Set<String> expectedTypes = new TreeSet<String>();
        expectedTypes.add("BOUGHT_TOGETHER");
        expectedTypes.add("COLL_TOGETHER");
        expectedTypes.add("IS_ELEMENT_OF");
        expectedTypes.add("IS_SIMILAR_TO");
        expectedTypes.add("RATED_TOGETHER");
        expectedTypes.add("SOUNDS_SIMILAR");
        expectedTypes.add("VIEWED_TOGETHER");

        assertThat(assocTypeDAO.getTypes(TENANT_ID, true), is(expectedTypes));
    }

    @Test
    public void testGetTypesInvisible() {
        Set<String> expectedTypes = new TreeSet<String>();
        expectedTypes.add("INVISIBLE");

        assertThat(assocTypeDAO.getTypes(TENANT_ID, false), is(expectedTypes));
    }

    @Test
    public void testGetMapping() {
        HashMap<String, Integer> expectedMapping = new HashMap<String, Integer>();
        expectedMapping.put("BOUGHT_TOGETHER", 1);
        expectedMapping.put("COLL_TOGETHER", 2);
        expectedMapping.put("IS_ELEMENT_OF", 3);
        expectedMapping.put("IS_SIMILAR_TO", 4);
        expectedMapping.put("RATED_TOGETHER", 5);
        expectedMapping.put("SOUNDS_SIMILAR", 6);
        expectedMapping.put("VIEWED_TOGETHER", 7);
        expectedMapping.put("INVISIBLE", 8);

        assertThat(assocTypeDAO.getMapping(TENANT_ID), is(expectedMapping));
    }

    @Test
    public void testGetMappingVisible() {
        HashMap<String, Integer> expectedMapping = new HashMap<String, Integer>();
        expectedMapping.put("BOUGHT_TOGETHER", 1);
        expectedMapping.put("COLL_TOGETHER", 2);
        expectedMapping.put("IS_ELEMENT_OF", 3);
        expectedMapping.put("IS_SIMILAR_TO", 4);
        expectedMapping.put("RATED_TOGETHER", 5);
        expectedMapping.put("SOUNDS_SIMILAR", 6);
        expectedMapping.put("VIEWED_TOGETHER", 7);

        assertThat(assocTypeDAO.getMapping(TENANT_ID, true), is(expectedMapping));
    }

    @Test
    public void testGetMappingInvisible() {
        HashMap<String, Integer> expectedMapping = new HashMap<String, Integer>();
        expectedMapping.put("INVISIBLE", 8);

        assertThat(assocTypeDAO.getMapping(TENANT_ID, false), is(expectedMapping));
    }

    @Test
    public void isVisible_shouldReturnCorrectValue() {
        assertThat(assocTypeDAO.isVisible(TENANT_ID, 1), is(true));
        assertThat(assocTypeDAO.isVisible(TENANT_ID, 2), is(true));
        assertThat(assocTypeDAO.isVisible(TENANT_ID, 3), is(true));
        assertThat(assocTypeDAO.isVisible(TENANT_ID, 4), is(true));
        assertThat(assocTypeDAO.isVisible(TENANT_ID, 5), is(true));
        assertThat(assocTypeDAO.isVisible(TENANT_ID, 6), is(true));
        assertThat(assocTypeDAO.isVisible(TENANT_ID, 7), is(true));
        assertThat(assocTypeDAO.isVisible(TENANT_ID, 8), is(false));

        try {
            assertThat(assocTypeDAO.isVisible(TENANT_ID, 999), is(true));
            fail("EmptyResultDataAccessException expected");
        } catch (EmptyResultDataAccessException expected) {}

        assertThat(assocTypeDAO.isVisible(TENANT_ID, "BOUGHT_TOGETHER"), is(true));
        assertThat(assocTypeDAO.isVisible(TENANT_ID, "COLL_TOGETHER"), is(true));
        assertThat(assocTypeDAO.isVisible(TENANT_ID, "IS_ELEMENT_OF"), is(true));
        assertThat(assocTypeDAO.isVisible(TENANT_ID, "IS_SIMILAR_TO"), is(true));
        assertThat(assocTypeDAO.isVisible(TENANT_ID, "RATED_TOGETHER"), is(true));
        assertThat(assocTypeDAO.isVisible(TENANT_ID, "SOUNDS_SIMILAR"), is(true));
        assertThat(assocTypeDAO.isVisible(TENANT_ID, "VIEWED_TOGETHER"), is(true));
        assertThat(assocTypeDAO.isVisible(TENANT_ID, "INVISIBLE"), is(false));

        try {
            assertThat(assocTypeDAO.isVisible(TENANT_ID, "ASDF"), is(false));
            fail("EmptyResultDataAccessException expected");
        } catch (EmptyResultDataAccessException expected) {}

        try {
            assocTypeDAO.isVisible(TENANT_ID, (String) null);
            fail("NPE expected");
        } catch (NullPointerException expected) {}

        try {
            assocTypeDAO.isVisible(TENANT_ID, (Integer) null);
            fail("NPE expected");
        } catch (NullPointerException expected) {}
    }
}
