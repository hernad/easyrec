/*
 * Copyright 2011 Research Studios Austria Forschungsgesellschaft mBH
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

package org.easyrec.mahout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.easyrec.mahout.model.EasyrecDataModel;
import org.easyrec.mahout.model.EasyrecInMemoryDataModel;
import org.easyrec.mahout.store.MahoutDataModelMappingDAO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for simple App.
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
@SpringApplicationContext({
        "spring/easyrecDataSource.xml",
        "spring/easyrec-easyrecDataModel-test.xml"})
@DataSet("/dbunit/easyrecDataModelTest.xml")
public class EasyrecInMemoryDataModelTest {
    @SpringBeanByName
    protected MahoutDataModelMappingDAO mahoutDataModelMappingDAO;

    private static int TENANT_ID = 1;
    private static int RATE_ACTION_TYPE_ID = 2;
    private static int BUY_ACTION_TYPE_ID = 3;
    private static boolean HAS_RATING_VALUES = true;
    private static boolean HAS_NO_RATING_VALUES = false;
    private final Log logger = LogFactory.getLog(this.getClass());

    @Test
    public void testEasyrecDataModel_getItemIDs() throws TasteException {
        DataModel easyrecDataModel = new EasyrecDataModel(TENANT_ID, RATE_ACTION_TYPE_ID, HAS_RATING_VALUES, mahoutDataModelMappingDAO);
        easyrecDataModel = new EasyrecInMemoryDataModel(easyrecDataModel);
        LongPrimitiveIterator itemIds = easyrecDataModel.getItemIDs();

        String ids = "";
        while (itemIds.hasNext()) {
            ids += itemIds.next();
        }

        assertEquals("123", ids);

        //Test peek function of the iterator
        itemIds = easyrecDataModel.getItemIDs();


        ids = "";
        ids += itemIds.peek();

        while (itemIds.hasNext()) {
            ids += itemIds.peek() + "-" + itemIds.next();
            if (itemIds.hasNext()) {
                ids += "-" + itemIds.peek() + "-" + itemIds.peek() + "#";
            } else {
                ids += "#";
            }
        }
        assertEquals("11-1-2-2#2-2-3-3#3-3#", ids);
    }

    @Test
    public void testEasyrecDataModel_getUserIDs() throws TasteException {
        DataModel easyrecDataModel = new EasyrecDataModel(TENANT_ID, RATE_ACTION_TYPE_ID, HAS_RATING_VALUES, mahoutDataModelMappingDAO);
        easyrecDataModel = new EasyrecInMemoryDataModel(easyrecDataModel);

        LongPrimitiveIterator userIds = easyrecDataModel.getUserIDs();

        String ids = "";
        while (userIds.hasNext()) {
            ids += userIds.next();
        }

        assertEquals("12", ids);

    }

    @Test
    public void testEasyrecDataModel_getItemIDsFromUser() throws TasteException {
        DataModel easyrecDataModel = new EasyrecDataModel(TENANT_ID, RATE_ACTION_TYPE_ID, HAS_RATING_VALUES, mahoutDataModelMappingDAO);
        easyrecDataModel = new EasyrecInMemoryDataModel(easyrecDataModel);

        FastIDSet itemIds = easyrecDataModel.getItemIDsFromUser(1);
        long[] itemIdsArray = itemIds.toArray();
        String ids = "";

        for (long itemId : itemIdsArray) {
            ids += itemId;
        }

        assertEquals("123", ids);
    }

    @Test
    public void testEasyrecDataModel_getMinPreference() throws TasteException {
        DataModel easyrecDataModel = new EasyrecDataModel(TENANT_ID, RATE_ACTION_TYPE_ID, HAS_RATING_VALUES, mahoutDataModelMappingDAO);
        easyrecDataModel = new EasyrecInMemoryDataModel(easyrecDataModel);

        Float minPreference = easyrecDataModel.getMinPreference();

        assertEquals(new Float(0), minPreference);
    }

    @Test
    public void testEasyrecDataModel_getMaxPreference() throws TasteException {
        DataModel easyrecDataModel = new EasyrecDataModel(TENANT_ID, RATE_ACTION_TYPE_ID, HAS_RATING_VALUES, mahoutDataModelMappingDAO);
        easyrecDataModel = new EasyrecInMemoryDataModel(easyrecDataModel);
        Float maxPreference = easyrecDataModel.getMaxPreference();


        assertEquals(new Float(10), maxPreference);
    }

    @Test
    public void testEasyrecDataModel_getNumUsers() throws TasteException {
        DataModel easyrecDataModel = new EasyrecDataModel(TENANT_ID, RATE_ACTION_TYPE_ID, HAS_RATING_VALUES, mahoutDataModelMappingDAO);
        easyrecDataModel = new EasyrecInMemoryDataModel(easyrecDataModel);

        int numberOfUsers = easyrecDataModel.getNumUsers();

        assertEquals(2, numberOfUsers);
    }

    @Test
    public void testEasyrecDataModel_getNumItems() throws TasteException {
        DataModel easyrecDataModel = new EasyrecDataModel(TENANT_ID, RATE_ACTION_TYPE_ID, HAS_RATING_VALUES, mahoutDataModelMappingDAO);
        easyrecDataModel = new EasyrecInMemoryDataModel(easyrecDataModel);

        int numberOfItems = easyrecDataModel.getNumItems();

        assertEquals(3, numberOfItems);
    }

    @Test
    public void testEasyrecDataModel_getNumUsersWithPreferenceFor() throws TasteException {
        DataModel easyrecDataModel = new EasyrecDataModel(TENANT_ID, RATE_ACTION_TYPE_ID, HAS_RATING_VALUES, mahoutDataModelMappingDAO);
        easyrecDataModel = new EasyrecInMemoryDataModel(easyrecDataModel);

        int numberUsersWithPreference = easyrecDataModel.getNumUsersWithPreferenceFor(1);

        assertEquals(2, numberUsersWithPreference);
    }

    @Test
    public void testEasyrecDataModel_getNumUsersWithPreferenceForDual() throws TasteException {
        DataModel easyrecDataModel = new EasyrecDataModel(TENANT_ID, RATE_ACTION_TYPE_ID, HAS_RATING_VALUES, mahoutDataModelMappingDAO);
        easyrecDataModel = new EasyrecInMemoryDataModel(easyrecDataModel);

        int numberUsersWithPreference = easyrecDataModel.getNumUsersWithPreferenceFor(1, 2);

        assertEquals(2, numberUsersWithPreference);
    }

    @Test
    public void testEasyrecDataModel_getPreferencesForItem() throws TasteException {
        DataModel easyrecDataModel = new EasyrecDataModel(TENANT_ID, RATE_ACTION_TYPE_ID, HAS_RATING_VALUES, mahoutDataModelMappingDAO);
        easyrecDataModel = new EasyrecInMemoryDataModel(easyrecDataModel);

        PreferenceArray preferences = easyrecDataModel.getPreferencesForItem(1);

        String ids = "";
        for (long id : preferences.getIDs()) {
            ids += id;
        }

        assertEquals("12", ids);

    }

    @Test
    public void testEasyrecDataModel_getPreferencesFromUser() throws TasteException {
        DataModel easyrecDataModel = new EasyrecDataModel(TENANT_ID, RATE_ACTION_TYPE_ID, HAS_RATING_VALUES, mahoutDataModelMappingDAO);
        easyrecDataModel = new EasyrecInMemoryDataModel(easyrecDataModel);

        PreferenceArray preferences = easyrecDataModel.getPreferencesFromUser(1);

        String ids = "";
        for (long id : preferences.getIDs()) {
            ids += id;
        }

        assertEquals("123", ids);
    }

    @Test
    public void testEasyrecDataModel_getPreferenceTime() throws TasteException {
        DataModel easyrecDataModel = new EasyrecDataModel(TENANT_ID, RATE_ACTION_TYPE_ID, HAS_RATING_VALUES, mahoutDataModelMappingDAO);
        easyrecDataModel = new EasyrecInMemoryDataModel(easyrecDataModel);

        Long preferenceTime = easyrecDataModel.getPreferenceTime(1, 2);

        assertEquals(new Long(1176631920000L), preferenceTime);
    }

    @Test
    public void testEasyrecDataModel_getPreferenceValue() throws TasteException {
        DataModel easyrecDataModel = new EasyrecDataModel(TENANT_ID, RATE_ACTION_TYPE_ID, HAS_RATING_VALUES, mahoutDataModelMappingDAO);
        easyrecDataModel = new EasyrecInMemoryDataModel(easyrecDataModel);

        Float preferenceValue = easyrecDataModel.getPreferenceValue(1, 2);

        assertEquals(new Float(5), preferenceValue);
    }

    @Test
    public void testEasyrecDataModel_hasPreferenceValues() throws TasteException {
        DataModel easyrecDataModel = new EasyrecDataModel(TENANT_ID, RATE_ACTION_TYPE_ID, HAS_RATING_VALUES, mahoutDataModelMappingDAO);
        easyrecDataModel = new EasyrecInMemoryDataModel(easyrecDataModel);

        boolean hasPreferenceValues = easyrecDataModel.hasPreferenceValues();

        assertEquals(true, hasPreferenceValues);
    }


    @Test
    public void testEasyrecDataModel_getBooleanPreferencesForItem() throws TasteException {
        DataModel easyrecDataModel = new EasyrecDataModel(TENANT_ID, BUY_ACTION_TYPE_ID, HAS_NO_RATING_VALUES, mahoutDataModelMappingDAO);
        easyrecDataModel = new EasyrecInMemoryDataModel(easyrecDataModel);

        PreferenceArray preferences = easyrecDataModel.getPreferencesForItem(10);

        String ids = "";
        for (long id : preferences.getIDs()) {
            ids += id;
        }

        assertEquals("12", ids);

    }

    @Test
    public void testEasyrecDataModel_getBooleanPreferencesFromUser() throws TasteException {
        DataModel easyrecDataModel = new EasyrecDataModel(TENANT_ID, BUY_ACTION_TYPE_ID, HAS_NO_RATING_VALUES, mahoutDataModelMappingDAO);
        easyrecDataModel = new EasyrecInMemoryDataModel(easyrecDataModel);

        PreferenceArray preferences = easyrecDataModel.getPreferencesFromUser(1);

        String ids = "";
        for (long id : preferences.getIDs()) {
            ids += id;
        }

        assertEquals("102030", ids);
    }

    @Test
    public void testEasyrecDataModel_getBooleanPreferenceValue() throws TasteException {
        DataModel easyrecDataModel = new EasyrecDataModel(TENANT_ID, BUY_ACTION_TYPE_ID, HAS_NO_RATING_VALUES, mahoutDataModelMappingDAO);
        easyrecDataModel = new EasyrecInMemoryDataModel(easyrecDataModel);

        Float preferenceValue = easyrecDataModel.getPreferenceValue(1, 20);

        assertEquals(new Float(1), preferenceValue);
    }


}
