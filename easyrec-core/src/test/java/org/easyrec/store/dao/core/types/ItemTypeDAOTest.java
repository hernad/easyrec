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

import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.fail;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(UnitilsJUnit4TestClassRunner.class)
@SpringApplicationContext({
        "spring/easyrecDataSource.xml",
        "spring/core/dao/types/ItemTypeDAO.xml"})
@DataSet(value = ItemTypeDAOTest.DATA_FILENAME)
public class ItemTypeDAOTest {
    // constants
    private final static Integer TENANT_ID = 0;

    // filenames of xml files
    public final static String DATA_FILENAME = "/dbunit/core/dao/types/itemtype.xml";
    public final static String DATA_FILENAME_ONE_LESS = "/dbunit/core/dao/types/itemtype_one_less.xml";
    public final static String DATA_FILENAME_CHANGED_IDS = "/dbunit/core/dao/types/itemtype_changed_ids.xml";

    // members
    @SpringBeanByName
    private ItemTypeDAO itemTypeDAO;

    @Test
    public void testGetItemTypeById() {
        String itemType = itemTypeDAO.getTypeById(TENANT_ID, 1);
        assertThat(itemType, is("ALBUM"));

        itemType = itemTypeDAO.getTypeById(TENANT_ID, 2);
        assertThat(itemType, is("ARTIST"));

        itemType = itemTypeDAO.getTypeById(TENANT_ID, 10);
        assertThat(itemType, is("INVISIBLE"));

        try {
            itemType = itemTypeDAO.getTypeById(TENANT_ID, 12345);
            fail("IllegalArgumentException should have been thrown, since 'id' for itemType was unknown");
        } catch (IllegalArgumentException e) {
        }

        itemType = itemTypeDAO.getTypeById(TENANT_ID, null);
        assertNull(itemType);
    }

    @Test
    public void testGetIdOfItemType() {
        Integer id = itemTypeDAO.getIdOfType(TENANT_ID, "ALBUM");
        assertThat(id, is(1));

        id = itemTypeDAO.getIdOfType(TENANT_ID, "ARTIST");
        assertThat(id, is(2));

        id = itemTypeDAO.getIdOfType(TENANT_ID, "INVISIBLE");
        assertThat(id, is(10));

        try {
            id = itemTypeDAO.getIdOfType(TENANT_ID, "ASDF");
            fail("IllegalArgumentException should have been thrown, since 'itemType' was unknown");
        } catch (IllegalArgumentException e) {
        }

        id = itemTypeDAO.getIdOfType(TENANT_ID, null);
        assertThat(id, is(nullValue()));
    }

    @Test
    @DataSet(DATA_FILENAME_ONE_LESS)
    @ExpectedDataSet(DATA_FILENAME)
    public void testInsertItemType() {
        itemTypeDAO.insertOrUpdate(TENANT_ID, "TRACK", 9);
        itemTypeDAO.insertOrUpdate(TENANT_ID, "INVISIBLE", 10, false);
    }

    @Test
    @ExpectedDataSet(DATA_FILENAME_CHANGED_IDS)
    public void testUpdateItemType() {
        itemTypeDAO.insertOrUpdate(TENANT_ID, "ALBUM", 11);
        itemTypeDAO.insertOrUpdate(TENANT_ID, "ARTIST", 21);
        itemTypeDAO.insertOrUpdate(TENANT_ID, "ASSET", 31);
        itemTypeDAO.insertOrUpdate(TENANT_ID, "CLUSTER", 41);
        itemTypeDAO.insertOrUpdate(TENANT_ID, "GENRE_CLUSTER", 51);
        itemTypeDAO.insertOrUpdate(TENANT_ID, "PLAYLIST", 61);
        itemTypeDAO.insertOrUpdate(TENANT_ID, "PROTOTYPE_TRACK", 71);
        itemTypeDAO.insertOrUpdate(TENANT_ID, "SOUND_CLUSTER", 81);
        itemTypeDAO.insertOrUpdate(TENANT_ID, "TRACK", 91);
        itemTypeDAO.insertOrUpdate(TENANT_ID, "INVISIBLE", 101);
    }

    @Test
    public void testExistsItemTypeTable() {
        assertThat(itemTypeDAO.existsTable(), is(true));
    }

    @Test
    public void testGetTypes() {
        Set<String> expectedTypes = new TreeSet<String>();
        expectedTypes.add("ALBUM");
        expectedTypes.add("ARTIST");
        expectedTypes.add("ASSET");
        expectedTypes.add("CLUSTER");
        expectedTypes.add("GENRE_CLUSTER");
        expectedTypes.add("PLAYLIST");
        expectedTypes.add("PROTOTYPE_TRACK");
        expectedTypes.add("SOUND_CLUSTER");
        expectedTypes.add("TRACK");
        expectedTypes.add("INVISIBLE");

        assertThat(itemTypeDAO.getTypes(TENANT_ID), is(expectedTypes));
    }

    @Test
    public void testGetTypesVisible() {
        Set<String> expectedTypes = new TreeSet<String>();
        expectedTypes.add("ALBUM");
        expectedTypes.add("ARTIST");
        expectedTypes.add("ASSET");
        expectedTypes.add("CLUSTER");
        expectedTypes.add("GENRE_CLUSTER");
        expectedTypes.add("PLAYLIST");
        expectedTypes.add("PROTOTYPE_TRACK");
        expectedTypes.add("SOUND_CLUSTER");
        expectedTypes.add("TRACK");

        assertThat(itemTypeDAO.getTypes(TENANT_ID, true), is(expectedTypes));
    }

    @Test
    public void testGetTypesInvisible() {
        Set<String> expectedTypes = new TreeSet<String>();
        expectedTypes.add("INVISIBLE");

        assertThat(itemTypeDAO.getTypes(TENANT_ID, false), is(expectedTypes));
    }

    @Test
    public void testGetMapping() {
        HashMap<String, Integer> expectedMapping = new HashMap<String, Integer>();
        expectedMapping.put("ALBUM", 1);
        expectedMapping.put("ARTIST", 2);
        expectedMapping.put("ASSET", 3);
        expectedMapping.put("CLUSTER", 4);
        expectedMapping.put("GENRE_CLUSTER", 5);
        expectedMapping.put("PLAYLIST", 6);
        expectedMapping.put("PROTOTYPE_TRACK", 7);
        expectedMapping.put("SOUND_CLUSTER", 8);
        expectedMapping.put("TRACK", 9);
        expectedMapping.put("INVISIBLE", 10);

        assertThat(itemTypeDAO.getMapping(TENANT_ID), is(expectedMapping));
    }

    @Test
    public void testGetMappingVisible() {
        HashMap<String, Integer> expectedMapping = new HashMap<String, Integer>();
        expectedMapping.put("ALBUM", 1);
        expectedMapping.put("ARTIST", 2);
        expectedMapping.put("ASSET", 3);
        expectedMapping.put("CLUSTER", 4);
        expectedMapping.put("GENRE_CLUSTER", 5);
        expectedMapping.put("PLAYLIST", 6);
        expectedMapping.put("PROTOTYPE_TRACK", 7);
        expectedMapping.put("SOUND_CLUSTER", 8);
        expectedMapping.put("TRACK", 9);

        assertThat(itemTypeDAO.getMapping(TENANT_ID, true), is(expectedMapping));
    }

    @Test
    public void testGetMappingInvisible() {
        HashMap<String, Integer> expectedMapping = new HashMap<String, Integer>();
        expectedMapping.put("INVISIBLE", 10);

        assertThat(itemTypeDAO.getMapping(TENANT_ID, false), is(expectedMapping));
    }

    @Test
    public void isVisible_shouldReturnCorrectValue() {
        assertThat(itemTypeDAO.isVisible(TENANT_ID, 1), is(true));
        assertThat(itemTypeDAO.isVisible(TENANT_ID, 2), is(true));
        assertThat(itemTypeDAO.isVisible(TENANT_ID, 3), is(true));
        assertThat(itemTypeDAO.isVisible(TENANT_ID, 4), is(true));
        assertThat(itemTypeDAO.isVisible(TENANT_ID, 5), is(true));
        assertThat(itemTypeDAO.isVisible(TENANT_ID, 6), is(true));
        assertThat(itemTypeDAO.isVisible(TENANT_ID, 7), is(true));
        assertThat(itemTypeDAO.isVisible(TENANT_ID, 8), is(true));
        assertThat(itemTypeDAO.isVisible(TENANT_ID, 9), is(true));
        assertThat(itemTypeDAO.isVisible(TENANT_ID, 10), is(false));

        try {
            assertThat(itemTypeDAO.isVisible(TENANT_ID, 999), is(true));
            fail("EmptyResultDataAccessException expected");
        } catch (EmptyResultDataAccessException expected) {}

        assertThat(itemTypeDAO.isVisible(TENANT_ID, "ALBUM"), is(true));
        assertThat(itemTypeDAO.isVisible(TENANT_ID, "ARTIST"), is(true));
        assertThat(itemTypeDAO.isVisible(TENANT_ID, "ASSET"), is(true));
        assertThat(itemTypeDAO.isVisible(TENANT_ID, "CLUSTER"), is(true));
        assertThat(itemTypeDAO.isVisible(TENANT_ID, "GENRE_CLUSTER"), is(true));
        assertThat(itemTypeDAO.isVisible(TENANT_ID, "PLAYLIST"), is(true));
        assertThat(itemTypeDAO.isVisible(TENANT_ID, "PROTOTYPE_TRACK"), is(true));
        assertThat(itemTypeDAO.isVisible(TENANT_ID, "SOUND_CLUSTER"), is(true));
        assertThat(itemTypeDAO.isVisible(TENANT_ID, "TRACK"), is(true));
        assertThat(itemTypeDAO.isVisible(TENANT_ID, "INVISIBLE"), is(false));

        try {
            assertThat(itemTypeDAO.isVisible(TENANT_ID, "ASDF"), is(false));
            fail("EmptyResultDataAccessException expected");
        } catch (EmptyResultDataAccessException expected) {}

        try {
            itemTypeDAO.isVisible(TENANT_ID, (String) null);
            fail("NPE expected");
        } catch (NullPointerException expected) {}

        try {
            itemTypeDAO.isVisible(TENANT_ID, (Integer) null);
            fail("NPE expected");
        } catch (NullPointerException expected) {}
    }
}
