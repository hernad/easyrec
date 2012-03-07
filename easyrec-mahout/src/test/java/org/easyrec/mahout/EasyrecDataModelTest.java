package org.easyrec.mahout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.easyrec.mahout.model.EasyrecDataModel;
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
public class EasyrecDataModelTest {
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
        EasyrecDataModel easyrecDataModel = new EasyrecDataModel(TENANT_ID, RATE_ACTION_TYPE_ID, HAS_RATING_VALUES, mahoutDataModelMappingDAO);
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
        EasyrecDataModel easyrecDataModel = new EasyrecDataModel(TENANT_ID, RATE_ACTION_TYPE_ID, HAS_RATING_VALUES, mahoutDataModelMappingDAO);
        LongPrimitiveIterator userIds = easyrecDataModel.getUserIDs();

        String ids = "";
        while (userIds.hasNext()) {
            ids += userIds.next();
        }

        assertEquals("12", ids);

    }

    @Test
    public void testEasyrecDataModel_getItemIDsFromUser() throws TasteException {
        EasyrecDataModel easyrecDataModel = new EasyrecDataModel(TENANT_ID, RATE_ACTION_TYPE_ID, HAS_RATING_VALUES, mahoutDataModelMappingDAO);
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
        EasyrecDataModel easyrecDataModel = new EasyrecDataModel(TENANT_ID, RATE_ACTION_TYPE_ID, HAS_RATING_VALUES, mahoutDataModelMappingDAO);
        Float minPreference = easyrecDataModel.getMinPreference();

        assertEquals(new Float(0), minPreference);
    }

    @Test
    public void testEasyrecDataModel_getMaxPreference() throws TasteException {
        EasyrecDataModel easyrecDataModel = new EasyrecDataModel(TENANT_ID, RATE_ACTION_TYPE_ID, HAS_RATING_VALUES, mahoutDataModelMappingDAO);
        Float maxPreference = easyrecDataModel.getMaxPreference();

        assertEquals(new Float(10), maxPreference);
    }

    @Test
    public void testEasyrecDataModel_getNumUsers() throws TasteException {
        EasyrecDataModel easyrecDataModel = new EasyrecDataModel(TENANT_ID, RATE_ACTION_TYPE_ID, HAS_RATING_VALUES, mahoutDataModelMappingDAO);
        int numberOfUsers = easyrecDataModel.getNumUsers();

        assertEquals(2, numberOfUsers);
    }

    @Test
    public void testEasyrecDataModel_getNumItems() throws TasteException {
        EasyrecDataModel easyrecDataModel = new EasyrecDataModel(TENANT_ID, RATE_ACTION_TYPE_ID, HAS_RATING_VALUES, mahoutDataModelMappingDAO);
        int numberOfItems = easyrecDataModel.getNumItems();

        assertEquals(3, numberOfItems);
    }

    @Test
    public void testEasyrecDataModel_getNumUsersWithPreferenceFor() throws TasteException {
        EasyrecDataModel easyrecDataModel = new EasyrecDataModel(TENANT_ID, RATE_ACTION_TYPE_ID, HAS_RATING_VALUES, mahoutDataModelMappingDAO);
        int numberUsersWithPreference = easyrecDataModel.getNumUsersWithPreferenceFor(1);

        assertEquals(2, numberUsersWithPreference);
    }

    @Test
    public void testEasyrecDataModel_getNumUsersWithPreferenceForDual() throws TasteException {
        EasyrecDataModel easyrecDataModel = new EasyrecDataModel(TENANT_ID, RATE_ACTION_TYPE_ID, HAS_RATING_VALUES, mahoutDataModelMappingDAO);
        int numberUsersWithPreference = easyrecDataModel.getNumUsersWithPreferenceFor(1, 2);

        assertEquals(2, numberUsersWithPreference);
    }

    @Test
    public void testEasyrecDataModel_getPreferencesForItem() throws TasteException {
        EasyrecDataModel easyrecDataModel = new EasyrecDataModel(TENANT_ID, RATE_ACTION_TYPE_ID, HAS_RATING_VALUES, mahoutDataModelMappingDAO);
        PreferenceArray preferences = easyrecDataModel.getPreferencesForItem(1);

        String ids = "";
        for (long id : preferences.getIDs()) {
            ids += id;
        }

        assertEquals("12", ids);

    }

    @Test
    public void testEasyrecDataModel_getPreferencesFromUser() throws TasteException {
        EasyrecDataModel easyrecDataModel = new EasyrecDataModel(TENANT_ID, RATE_ACTION_TYPE_ID, HAS_RATING_VALUES, mahoutDataModelMappingDAO);
        PreferenceArray preferences = easyrecDataModel.getPreferencesFromUser(1);

        String ids = "";
        for (long id : preferences.getIDs()) {
            ids += id;
        }

        assertEquals("123", ids);
    }

    @Test
    public void testEasyrecDataModel_getPreferenceTime() throws TasteException {
        EasyrecDataModel easyrecDataModel = new EasyrecDataModel(TENANT_ID, RATE_ACTION_TYPE_ID, HAS_RATING_VALUES, mahoutDataModelMappingDAO);
        Long preferenceTime = easyrecDataModel.getPreferenceTime(1, 2);

        assertEquals(new Long(1176631920000L), preferenceTime);
    }

    @Test
    public void testEasyrecDataModel_getPreferenceValue() throws TasteException {
        EasyrecDataModel easyrecDataModel = new EasyrecDataModel(TENANT_ID, RATE_ACTION_TYPE_ID, HAS_RATING_VALUES, mahoutDataModelMappingDAO);
        Float preferenceValue = easyrecDataModel.getPreferenceValue(1, 2);

        assertEquals(new Float(5), preferenceValue);
    }

    @Test
    public void testEasyrecDataModel_hasPreferenceValues() throws TasteException {
        EasyrecDataModel easyrecDataModel = new EasyrecDataModel(TENANT_ID, RATE_ACTION_TYPE_ID, HAS_RATING_VALUES, mahoutDataModelMappingDAO);
        boolean hasPreferenceValues = easyrecDataModel.hasPreferenceValues();

        assertEquals(true, hasPreferenceValues);
    }


    @Test
    public void testEasyrecDataModel_getBooleanPreferencesForItem() throws TasteException {
        EasyrecDataModel easyrecDataModel = new EasyrecDataModel(TENANT_ID, BUY_ACTION_TYPE_ID, HAS_NO_RATING_VALUES, mahoutDataModelMappingDAO);
        PreferenceArray preferences = easyrecDataModel.getPreferencesForItem(10);

        String ids = "";
        for (long id : preferences.getIDs()) {
            ids += id;
        }

        assertEquals("12", ids);

    }

    @Test
    public void testEasyrecDataModel_getBooleanPreferencesFromUser() throws TasteException {
        EasyrecDataModel easyrecDataModel = new EasyrecDataModel(TENANT_ID, BUY_ACTION_TYPE_ID, HAS_NO_RATING_VALUES, mahoutDataModelMappingDAO);
        PreferenceArray preferences = easyrecDataModel.getPreferencesFromUser(1);

        String ids = "";
        for (long id : preferences.getIDs()) {
            ids += id;
        }

        assertEquals("102030", ids);
    }

    @Test
    public void testEasyrecDataModel_getBooleanPreferenceValue() throws TasteException {
        EasyrecDataModel easyrecDataModel = new EasyrecDataModel(TENANT_ID, BUY_ACTION_TYPE_ID, HAS_NO_RATING_VALUES, mahoutDataModelMappingDAO);
        Float preferenceValue = easyrecDataModel.getPreferenceValue(1, 20);

        assertEquals(new Float(1), preferenceValue);
    }


}
