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

import org.easyrec.model.core.AssociatedItemVO;
import org.easyrec.model.core.ItemAssocVO;
import org.easyrec.model.core.ItemVO;
import org.easyrec.model.core.transfer.IAConstraintVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.core.Unitils;
import org.unitils.database.DatabaseModule;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.annotation.ExpectedDataSet;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

@RunWith(UnitilsJUnit4TestClassRunner.class)
@SpringApplicationContext({
        "spring/easyrecDataSource.xml",
        "spring/core/dao/ItemAssocDAO.xml"})
@DataSet(value = ItemAssocDAOTest.DATA_FILENAME)
public class ItemAssocDAOTest {
    // filenames of xml files
    public final static String DATA_FILENAME = "/dbunit/core/dao/itemassoc.xml";
    public final static String DATA_FILENAME_NO_CHANGEDATE = "/dbunit/core/dao/itemassoc_no_changedate.xml";
    public final static String DATA_FILENAME_ONE_LESS = "/dbunit/core/dao/itemassoc_one_less.xml";
    public final static String DATA_FILENAME_UPDATED = "/dbunit/core/dao/itemassoc_updated.xml";
    public final static String DATA_FILENAME_REMOVE_SOURCETTYPE_ONE = "/dbunit/core/dao/itemassoc_remove_source_type_id_1.xml";

    // members
    @SpringBeanByName
    private ItemAssocDAO itemAssocDAO;

    @Test
    public void testRemoveAllItemAssocs() {
        // check if all item associations are still there
        assertEquals(1, itemAssocDAO.loadItemAssocByPrimaryKey(1).getId().intValue());
        assertEquals(2, itemAssocDAO.loadItemAssocByPrimaryKey(2).getId().intValue());
        assertEquals(3, itemAssocDAO.loadItemAssocByPrimaryKey(3).getId().intValue());
        assertEquals(4, itemAssocDAO.loadItemAssocByPrimaryKey(4).getId().intValue());
        assertEquals(5, itemAssocDAO.loadItemAssocByPrimaryKey(5).getId().intValue());
        assertEquals(6, itemAssocDAO.loadItemAssocByPrimaryKey(6).getId().intValue());

        // remove all entries
        int rowsAffected = itemAssocDAO.removeAllItemAssocs();

        assertEquals(6, rowsAffected);

        ItemAssocVO<Integer,Integer> itemAssoc;

        // check that no entry returns a result anymore
        itemAssoc = itemAssocDAO.loadItemAssocByPrimaryKey(1);
        assertNull(itemAssoc);

        itemAssoc = itemAssocDAO.loadItemAssocByPrimaryKey(2);
        assertNull(itemAssoc);

        itemAssoc = itemAssocDAO.loadItemAssocByPrimaryKey(3);
        assertNull(itemAssoc);

        itemAssoc = itemAssocDAO.loadItemAssocByPrimaryKey(4);
        assertNull(itemAssoc);

        itemAssoc = itemAssocDAO.loadItemAssocByPrimaryKey(5);
        assertNull(itemAssoc);

        itemAssoc = itemAssocDAO.loadItemAssocByPrimaryKey(6);
        assertNull(itemAssoc);
    }

    @Test
    @DataSet(DATA_FILENAME_ONE_LESS)
    @ExpectedDataSet(DATA_FILENAME_NO_CHANGEDATE)
    public void testInsertItemAssoc() {
        ItemAssocVO<Integer,Integer> itemAssoc = null;
        try {
            itemAssoc = new ItemAssocVO<Integer,Integer>(1,
                    new ItemVO<Integer, Integer>(1, 2, 1), 1, 0.1d,
                    new ItemVO<Integer, Integer>(1, 7, 1), 2, "def", 1, true);
        } catch (Exception e) {
            fail("caught exception: " + e);
        }
        assertTrue(itemAssoc.getId() == null);
        itemAssocDAO.insertItemAssoc(itemAssoc);

        assertThat(itemAssoc.getId(), is(not(1)));
        assertThat(itemAssoc.getId(), is(not(2)));
        assertThat(itemAssoc.getId(), is(not(3)));
        assertThat(itemAssoc.getId(), is(not(4)));
        assertThat(itemAssoc.getId(), is(not(5)));
    }

    @Test
    public void testInsertOrUpdateItemAssocs() {
        List<ItemAssocVO<Integer,Integer>> itemAssocs = new ArrayList<ItemAssocVO<Integer,Integer>>();

        Date date = new Date();
        // shave off milliseconds since they can't be stored in mysql
        long time = (long) Math.floor(date.getTime() / 1000) * 1000L;
        date = new Date(time);

        itemAssocs.add(new ItemAssocVO<Integer,Integer>(null, 1,
                new ItemVO<Integer, Integer>(1, 1, 1), 1, 1.0, new ItemVO<Integer, Integer>(1, 2, 1),
                1, "sourceInfo", 1, true, date));
        itemAssocs.add(new ItemAssocVO<Integer,Integer>(null, 1,
                new ItemVO<Integer, Integer>(1, 2, 1), 1, 1.0, new ItemVO<Integer, Integer>(1, 3, 1),
                1, "sourceInfo\t\n\r\bwith escape characters\\", 1, true, date));
        itemAssocs.add(new ItemAssocVO<Integer,Integer>(null, 1,
                new ItemVO<Integer, Integer>(1, 3, 1), 1, 1.0, new ItemVO<Integer, Integer>(1, 4, 1),
                1, "sourceInfo", 1, null, date));
        itemAssocs.add(new ItemAssocVO<Integer,Integer>(null, 1,
                new ItemVO<Integer, Integer>(1, 4, 1), 1, 1.0, new ItemVO<Integer, Integer>(1, 5, 1),
                1, "sourceInfo", 1, false, date));

        int noRowsAdded = itemAssocDAO.insertOrUpdateItemAssocs(itemAssocs);

        assertEquals(itemAssocs.size(), noRowsAdded);

        Iterator<ItemAssocVO<Integer,Integer>> it = itemAssocDAO
                .getItemAssocIterator(5000);
        int itemsEqual = 0;

        // need to set active since null gets the default value true when storing
        itemAssocs.get(2).setActive(true);

        while (it.hasNext()) {
            ItemAssocVO<Integer,Integer> itemAssocGot = it.next();
            itemAssocGot.setId(null);

            if (itemAssocs.contains(itemAssocGot)) itemsEqual++;
        }

        assertEquals(itemAssocs.size(), itemsEqual);
    }

    @Test
    @DataSet(DATA_FILENAME_ONE_LESS)
    @ExpectedDataSet(DATA_FILENAME_NO_CHANGEDATE)
    public void testInsertItemAssocActiveNull() {
        ItemAssocVO<Integer,Integer> itemAssoc = null;

        try {
            itemAssoc = new ItemAssocVO<Integer,Integer>(1,
                    new ItemVO<Integer, Integer>(1, 2, 1), 1, 0.1d,
                    new ItemVO<Integer, Integer>(1, 7, 1), 2, "def", 1, null);
        } catch (Exception e) {
            fail("caught exception: " + e);
        }

        assertTrue(itemAssoc.getId() == null);
        itemAssocDAO.insertItemAssoc(itemAssoc);

        assertThat(itemAssoc.getId(), is(not(1)));
        assertThat(itemAssoc.getId(), is(not(2)));
        assertThat(itemAssoc.getId(), is(not(3)));
        assertThat(itemAssoc.getId(), is(not(4)));
        assertThat(itemAssoc.getId(), is(not(5)));
    }

    @Test
    @DataSet(DATA_FILENAME_ONE_LESS)
    public void testInsertItemAssocAssocValueNull() {
        ItemAssocVO<Integer,Integer> itemAssoc = null;
        try {
            itemAssoc = new ItemAssocVO<Integer,Integer>(1,
                    new ItemVO<Integer, Integer>(1, 2, 1), 1, null,
                    new ItemVO<Integer, Integer>(1, 7, 1), 2, "def", 1, true);
        } catch (Exception e) {
            fail("caught exception: " + e);
        }
        assertTrue(itemAssoc.getId() == null);
        try {
            itemAssocDAO.insertItemAssoc(itemAssoc);
            fail("exception should be thrown, since 'assocValue' is missing");
        } catch (Exception e) {
            assertTrue(
                    "unexpected exception during insertion of item association: " + itemAssoc + ", " + e.getMessage(),
                    e instanceof IllegalArgumentException);
        }
    }

    @Test
    @DataSet(DATA_FILENAME_ONE_LESS)
    public void testInsertItemAssocViewTypeNull() {
        ItemAssocVO<Integer,Integer> itemAssoc = null;
        try {
            itemAssoc = new ItemAssocVO<Integer,Integer>(1,
                    new ItemVO<Integer, Integer>(1, 2, 1), 1, 0.1d,
                    new ItemVO<Integer, Integer>(1, 7, 1), 2, "def", null, true);
        } catch (Exception e) {
            fail("caught exception: " + e);
        }
        assertTrue(itemAssoc.getId() == null);
        try {
            itemAssocDAO.insertItemAssoc(itemAssoc);
            fail("exception should be thrown, since 'viewType' is missing");
        } catch (Exception e) {
            assertTrue(
                    "unexpected exception during insertion of item association: " + itemAssoc + ", " + e.getMessage(),
                    e instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testInsertItemAssocMissingConstraint() {
        ItemAssocVO<Integer,Integer> itemAssoc = null;
        try {
            itemAssoc = new ItemAssocVO<Integer,Integer>(null,
                    new ItemVO<Integer, Integer>(1, 1, 1), 1, 0.1d,
                    new ItemVO<Integer, Integer>(1, 7, 1), 2, "def", 1, true);
            itemAssocDAO.insertItemAssoc(itemAssoc);
            fail("exception should be thrown, since 'tenantId' is missing");
        } catch (Exception e) {
            assertTrue(
                    "unexpected exception during insertion of item association: " + itemAssoc + ", " + e.getMessage(),
                    e instanceof IllegalArgumentException);
        }
    }

    @Test
    @ExpectedDataSet(DATA_FILENAME_UPDATED)
    public void testUpdateItemAssocUsingPrimaryKey() {
        ItemAssocVO<Integer,Integer> itemAssoc = itemAssocDAO
                .loadItemAssocByPrimaryKey(1);
        itemAssoc.setAssocValue(1.2d);
        itemAssocDAO.updateItemAssocUsingPrimaryKey(itemAssoc);

        itemAssoc = itemAssocDAO.loadItemAssocByPrimaryKey(2);
        itemAssoc.setViewType(7);
        itemAssoc.setActive(false);
        itemAssocDAO.updateItemAssocUsingPrimaryKey(itemAssoc);

        itemAssoc = itemAssocDAO.loadItemAssocByPrimaryKey(3);
        itemAssoc.setAssocValue(3.0d);
        itemAssoc.setActive(false);
        itemAssocDAO.updateItemAssocUsingPrimaryKey(itemAssoc);
    }

    @Test
    @ExpectedDataSet(DATA_FILENAME_NO_CHANGEDATE)
    public void testUpdateItemAssocUsingPrimaryKeyActiveNull() {
        ItemAssocVO<Integer,Integer> itemAssoc = itemAssocDAO
                .loadItemAssocByPrimaryKey(1);
        itemAssoc.setActive(null);
        // setActive(null) shouldn't be called at all, therefore the dataset shouldn't change
        itemAssocDAO.updateItemAssocUsingPrimaryKey(itemAssoc);
    }

    @Test
    @ExpectedDataSet(DATA_FILENAME_UPDATED)
    public void testUpdateItemAssocUsingUniqueKey() {
        ItemAssocVO<Integer,Integer> itemAssoc = itemAssocDAO
                .loadItemAssocByPrimaryKey(1);
        itemAssoc.setAssocValue(1.2d);
        itemAssocDAO.updateItemAssocUsingUniqueKey(itemAssoc);

        itemAssoc = itemAssocDAO.loadItemAssocByPrimaryKey(2);
        itemAssoc.setViewType(7);
        itemAssoc.setActive(false);
        itemAssocDAO.updateItemAssocUsingUniqueKey(itemAssoc);

        itemAssoc = itemAssocDAO.loadItemAssocByPrimaryKey(3);
        itemAssoc.setAssocValue(3.0d);
        itemAssoc.setActive(false);
        itemAssocDAO.updateItemAssocUsingUniqueKey(itemAssoc);
    }

    @Test
    @ExpectedDataSet(DATA_FILENAME_NO_CHANGEDATE)
    public void testUpdateItemAssocUsingUniqueKeyActiveNull() {
        ItemAssocVO<Integer,Integer> itemAssoc = itemAssocDAO
                .loadItemAssocByPrimaryKey(1);
        itemAssoc.setActive(null);
        // setActive(null) shouldn't be called at all, therefore the dataset shouldn't change
        itemAssocDAO.updateItemAssocUsingUniqueKey(itemAssoc);
    }

    @Test
    public void testLoadItemAssocByUniqueKey() {
        ItemAssocVO<Integer,Integer> queryAssoc = itemAssocDAO
                .loadItemAssocByPrimaryKey(1);
        ItemAssocVO<Integer,Integer> resultAssoc = itemAssocDAO
                .loadItemAssocByUniqueKey(queryAssoc);
        assertEquals(queryAssoc, resultAssoc);
    }

    @Test
    public void testLoadItemAssocByUniqueKeyEmptyDB() {
        Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class).cleanSchemas();

        ItemAssocVO<Integer,Integer> queryAssoc = new ItemAssocVO<Integer,Integer>(
                1, new ItemVO<Integer, Integer>(1, 1, 1), 1, null,
                new ItemVO<Integer, Integer>(1, 2, 1), 1, "abc", 1, null);
        ItemAssocVO<Integer,Integer> resultAssoc = itemAssocDAO
                .loadItemAssocByUniqueKey(queryAssoc);
        assertNull(resultAssoc);
    }

    @Test
    public void testLoadItemAssocByPrimaryKey() {
        ItemAssocVO<Integer,Integer> queryAssoc = new ItemAssocVO<Integer,Integer>(
                1, new ItemVO<Integer, Integer>(1, 1, 1), 1, null,
                new ItemVO<Integer, Integer>(1, 2, 1), 1, "abc", 1, null);
        ItemAssocVO<Integer,Integer> resultAssoc = itemAssocDAO
                .loadItemAssocByUniqueKey(queryAssoc);
        ItemAssocVO<Integer,Integer> resultAssoc2 = itemAssocDAO
                .loadItemAssocByPrimaryKey(resultAssoc.getId());

        assertEquals(resultAssoc, resultAssoc2);
    }

    @Test
    public void testLoadItemAssocByPrimaryKeyEmptyDB() {
        Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class).cleanSchemas();

        ItemAssocVO<Integer,Integer> resultAssoc = itemAssocDAO
                .loadItemAssocByPrimaryKey(1);
        assertNull(resultAssoc);
    }

    @Test
    public void testGetItemAssocIterator() {
        Iterator<ItemAssocVO<Integer,Integer>> itemAssocs = itemAssocDAO
                .getItemAssocIterator(5000);
        assertTrue(itemAssocs != null);
        List<ItemAssocVO<Integer,Integer>> itemAssocsList = iteratorToList(
                itemAssocs);
        assertEquals(6, itemAssocsList.size());

        // HINT: hardcoded check if list equals expected list (Mantis Issue: #721)
    }

    @Test
    public void testGetItemAssocIteratorEmptyDB() {
        Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class).cleanSchemas();

        Iterator<ItemAssocVO<Integer,Integer>> itemAssocs = itemAssocDAO
                .getItemAssocIterator(5000);
        assertFalse(itemAssocs.hasNext());
    }

    @Test
    @ExpectedDataSet(DATA_FILENAME_ONE_LESS)
    public void testRemoveItemAssocsQBE() {
        ItemAssocVO<Integer,Integer> itemAssoc = null;
        try {
            itemAssoc = new ItemAssocVO<Integer,Integer>(1,
                    new ItemVO<Integer, Integer>(1, 2, 1), 1, 0.1d,
                    new ItemVO<Integer, Integer>(1, 7, 1), 2, "def", 1, true);
        } catch (Exception e) {
            fail("caught exception: " + e);
        }
        itemAssocDAO.removeItemAssocsQBE(itemAssoc);
    }

    @Test
    @ExpectedDataSet(DATA_FILENAME_REMOVE_SOURCETTYPE_ONE)
    public void testRemoveItemAssocsQBESourceTypeOne() {
        ItemAssocVO<Integer,Integer> itemAssoc = null;
        try {
            itemAssoc = new ItemAssocVO<Integer,Integer>(null, null, null, null,
                    null, 1, null, null, null);
        } catch (Exception e) {
            fail("caught exception: " + e);
        }
        itemAssocDAO.removeItemAssocsQBE(itemAssoc);
    }

    @Test
    @ExpectedDataSet(DATA_FILENAME_ONE_LESS)
    public void testRemoveItemAssocsQBESourceTypeTwo() {
        ItemAssocVO<Integer,Integer> itemAssoc = null;
        try {
            itemAssoc = new ItemAssocVO<Integer,Integer>(null, null, null, null,
                    null, 2, null, null, null);
        } catch (Exception e) {
            fail("caught exception: " + e);
        }
        itemAssocDAO.removeItemAssocsQBE(itemAssoc);
    }

    @Test
    public void testRemoveItemAssocsQBEMissingConstraints() {
        ItemAssocVO<Integer,Integer> itemAssoc = null;
        try {
            itemAssoc = new ItemAssocVO<Integer,Integer>(null, null, null, null,
                    null, null, null, null, null);
        } catch (Exception e) {
            fail("caught exception: " + e);
        }
        try {
            itemAssocDAO.removeItemAssocsQBE(itemAssoc);
            fail("exception should be thrown, since VO has no properties set");
        } catch (Exception e) {
            assertTrue(
                    "unexpected exception during insertion of item association: " + itemAssoc + ", " + e.getMessage(),
                    e instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testGetItemsFrom() {
        List<AssociatedItemVO<Integer, Integer>> associatedItems = itemAssocDAO
                .getItemsFrom(1, 1, new ItemVO<Integer, Integer>(1, 2, 1),
                        new IAConstraintVO<Integer, Integer>(500));
        assertTrue(associatedItems != null);
        assertEquals(1, associatedItems.size());

        // HINT: hardcoded check if list equals expected list (Mantis Issue: #721)
    }

    @Test
    public void testGetItemsFromEmptyDB() {
        Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class).cleanSchemas();

        List<AssociatedItemVO<Integer, Integer>> associatedItems = itemAssocDAO
                .getItemsFrom(1, 1, new ItemVO<Integer, Integer>(1, 1, 1),
                        new IAConstraintVO<Integer, Integer>(500));
        assertTrue(associatedItems.isEmpty());
        assertEquals(0, associatedItems.size());
    }

    @Test
    public void testGetItemsTo() {
        List<AssociatedItemVO<Integer, Integer>> associatedItems = itemAssocDAO
                .getItemsTo(new ItemVO<Integer, Integer>(1, 1, 1), 1, 1,
                        new IAConstraintVO<Integer, Integer>(500));
        assertTrue(associatedItems != null);
        assertEquals(5, associatedItems.size());

        // HINT: hardcoded check if list equals expected list (Mantis Issue: #721)
    }

    @Test
    public void testGetItemsToEmptyDB() {
        Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class).cleanSchemas();

        List<AssociatedItemVO<Integer, Integer>> associatedItems = itemAssocDAO
                .getItemsTo(new ItemVO<Integer, Integer>(1, 1, 1), 1, 1,
                        new IAConstraintVO<Integer, Integer>(500));
        assertTrue(associatedItems.isEmpty());
        assertEquals(0, associatedItems.size());
    }

    @Test
    public void testGetItemAssocs() {
        List<ItemAssocVO<Integer,Integer>> itemAssocs = itemAssocDAO
                .getItemAssocs(new ItemVO<Integer, Integer>(1, 1, 1), 1,
                        new ItemVO<Integer, Integer>(1, 3, 1),
                        new IAConstraintVO<Integer, Integer>(500));
        assertTrue(itemAssocs != null);
        assertEquals(1, itemAssocs.size());

        // HINT: hardcoded check if list equals expected list (Mantis Issue: #721)
    }

    @Test
    public void testGetItemAssocsEmptyDB() {
        Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class).cleanSchemas();

        List<ItemAssocVO<Integer,Integer>> itemAssocs = itemAssocDAO
                .getItemAssocs(new ItemVO<Integer, Integer>(1, 1, 1), 1,
                        new ItemVO<Integer, Integer>(1, 2, 1),
                        new IAConstraintVO<Integer, Integer>(500));
        assertTrue(itemAssocs.isEmpty());
        assertEquals(0, itemAssocs.size());
    }

    // private methods
    private List<ItemAssocVO<Integer,Integer>> iteratorToList(
            Iterator<ItemAssocVO<Integer,Integer>> itemAssocs) {
        List<ItemAssocVO<Integer,Integer>> itemAssocsList = new ArrayList<ItemAssocVO<Integer,Integer>>();
        while (itemAssocs.hasNext()) {
            itemAssocsList.add(itemAssocs.next());
        }
        return itemAssocsList;
    }
}
