package org.easyrec.mahout.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.easyrec.mahout.store.MahoutDataModelMappingDAO;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.util.Collection;
import java.util.Date;

/**
 *
 */
public class EasyrecDataModel extends JdbcDaoSupport implements DataModel {

    int tenantId;
    int actionTypeId;
    Date cutoffDate;
    boolean hasRatingValues;
    MahoutDataModelMappingDAO mahoutDataModelMappingDAO;

    private final Log logger = LogFactory.getLog(this.getClass());

    /**
     * This constructor can be used to create a Data Model which is usable for Mahout if you want to create a boolean recommender
     * just use a actionType without rating values.
     *
     * @param tenantId                  the tenantId of the dataset you want to load
     * @param actionTypeId              the INT id of the actionType you want to use for your recommender
     * @param hasRatingValues           a boolean which tells the DataModel about your Data Set having rating values or not.
     * @param mahoutDataModelMappingDAO an instance of  MahoutDataModelMappingDAO
     */
    public EasyrecDataModel(int tenantId, int actionTypeId, boolean hasRatingValues, MahoutDataModelMappingDAO mahoutDataModelMappingDAO) {
        this.tenantId = tenantId;
        this.actionTypeId = actionTypeId;
        this.mahoutDataModelMappingDAO = mahoutDataModelMappingDAO;
        this.hasRatingValues = hasRatingValues;
        cutoffDate = new Date();
    }

    /**
     * @return all user IDs in the model, in order
     * @throws TasteException if an error occurs while accessing the data
     */
    public LongPrimitiveIterator getUserIDs() throws TasteException {
        return mahoutDataModelMappingDAO.getUserIDs(tenantId, cutoffDate, actionTypeId);
    }

    /**
     * @param userID ID of user to get prefs for
     * @return user's preferences, ordered by item ID
     * @throws org.apache.mahout.cf.taste.common.NoSuchUserException
     *                        if the user does not exist
     * @throws TasteException if an error occurs while accessing the data
     */
    public PreferenceArray getPreferencesFromUser(long userID) throws TasteException {
        if (hasRatingValues) {
            return mahoutDataModelMappingDAO.getPreferencesFromUser(tenantId, cutoffDate, userID, actionTypeId);
        } else {
            return mahoutDataModelMappingDAO.getBooleanPreferencesFromUser(tenantId, cutoffDate, userID, actionTypeId);
        }
    }

    /**
     * @param userID ID of user to get prefs for
     * @return IDs of items user expresses a preference for
     * @throws org.apache.mahout.cf.taste.common.NoSuchUserException
     *                        if the user does not exist
     * @throws TasteException if an error occurs while accessing the data
     */
    public FastIDSet getItemIDsFromUser(long userID) throws TasteException {
        return mahoutDataModelMappingDAO.getItemIDsFromUser(tenantId, cutoffDate, userID, actionTypeId);
    }

    /**
     * @return a {@link LongPrimitiveIterator} of all item IDs in the model, in order
     * @throws TasteException if an error occurs while accessing the data
     */
    public LongPrimitiveIterator getItemIDs() throws TasteException {
        return mahoutDataModelMappingDAO.getItemIDs(tenantId, cutoffDate, actionTypeId);
    }

    /**
     * @param itemID item ID
     * @return all existing Preference's expressed for that item, ordered by user ID, as an array
     * @throws org.apache.mahout.cf.taste.common.NoSuchItemException
     *                        if the item does not exist
     * @throws TasteException if an error occurs while accessing the data
     */
    public PreferenceArray getPreferencesForItem(long itemID) throws TasteException {
        if (hasRatingValues) {
            return mahoutDataModelMappingDAO.getPreferencesForItem(tenantId, cutoffDate, itemID, actionTypeId);
        } else {
            return mahoutDataModelMappingDAO.getBooleanPreferencesForItem(tenantId, cutoffDate, itemID, actionTypeId);
        }
    }

    /**
     * Retrieves the preference value for a single user and item.
     *
     * @param userID user ID to get pref value from
     * @param itemID item ID to get pref value for
     * @return preference value from the given user for the given item or null if none exists
     * @throws org.apache.mahout.cf.taste.common.NoSuchUserException
     *                        if the user does not exist
     * @throws TasteException if an error occurs while accessing the data
     */
    public Float getPreferenceValue(long userID, long itemID) throws TasteException {
        if (hasRatingValues) {
            return mahoutDataModelMappingDAO.getPreferenceValue(tenantId, cutoffDate, userID, itemID, actionTypeId);
        } else {
            return mahoutDataModelMappingDAO.getBooleanPreferenceValue(tenantId, cutoffDate, userID, itemID, actionTypeId);
        }
    }

    /**
     * Retrieves the time at which a preference value from a user and item was set, if known.
     * Time is expressed in the usual way, as a number of milliseconds since the epoch.
     *
     * @param userID user ID for preference in question
     * @param itemID item ID for preference in question
     * @return time at which preference was set or null if no preference exists or its time is not known
     * @throws org.apache.mahout.cf.taste.common.NoSuchUserException
     *                        if the user does not exist
     * @throws TasteException if an error occurs while accessing the data
     */
    public Long getPreferenceTime(long userID, long itemID) throws TasteException {
        return mahoutDataModelMappingDAO.getPreferenceTime(tenantId, cutoffDate, userID, itemID, actionTypeId);
    }

    /**
     * @return total number of items known to the model. This is generally the union of all items preferred by
     *         at least one user but could include more.
     * @throws TasteException if an error occurs while accessing the data
     */
    public int getNumItems() throws TasteException {
        return mahoutDataModelMappingDAO.getNumItems(tenantId, cutoffDate, actionTypeId);
    }

    /**
     * @return total number of users known to the model.
     * @throws TasteException if an error occurs while accessing the data
     */
    public int getNumUsers() throws TasteException {
        return mahoutDataModelMappingDAO.getNumUsers(tenantId, cutoffDate, actionTypeId);
    }

    /**
     * @param itemID item ID to check for
     * @return the number of users who have expressed a preference for the item
     * @throws TasteException if an error occurs while accessing the data
     */
    public int getNumUsersWithPreferenceFor(long itemID) throws TasteException {
        return mahoutDataModelMappingDAO.getNumUsersWithPreferenceFor(tenantId, cutoffDate, itemID, actionTypeId);
    }

    /**
     * @param itemID1 first item ID to check for
     * @param itemID2 second item ID to check for
     * @return the number of users who have expressed a preference for the items
     * @throws TasteException if an error occurs while accessing the data
     */
    public int getNumUsersWithPreferenceFor(long itemID1, long itemID2) throws TasteException {
        return mahoutDataModelMappingDAO.getNumUsersWithPreferenceFor(tenantId, cutoffDate, itemID1, itemID2, actionTypeId);
    }

    /**
     * <p>
     * Sets a particular preference (item plus rating) for a user.
     * <b> Not implemented yet. </b>
     * </p>
     *
     * @param userID user to set preference for
     * @param itemID item to set preference for
     * @param value  preference value
     * @throws org.apache.mahout.cf.taste.common.NoSuchItemException
     *                        if the item does not exist
     * @throws org.apache.mahout.cf.taste.common.NoSuchUserException
     *                        if the user does not exist
     * @throws TasteException if an error occurs while accessing the data
     */
    public void setPreference(long userID, long itemID, float value) throws TasteException {
        throw new UnsupportedOperationException();
    }

    /**
     * <p>
     * Removes a particular preference for a user.
     * <b> Not implemented yet. </b>
     * </p>
     *
     * @param userID user from which to remove preference
     * @param itemID item to remove preference for
     * @throws org.apache.mahout.cf.taste.common.NoSuchItemException
     *                        if the item does not exist
     * @throws org.apache.mahout.cf.taste.common.NoSuchUserException
     *                        if the user does not exist
     * @throws TasteException if an error occurs while accessing the data
     */
    public void removePreference(long userID, long itemID) throws TasteException {
        throw new UnsupportedOperationException();
    }

    /**
     * @return true iff this implementation actually stores and returns distinct preference values;
     *         that is, if it is not a 'boolean' DataModel
     */
    public boolean hasPreferenceValues() {
        return hasRatingValues;
    }

    /**
     * @return the maximum preference value that is possible in the current problem domain being evaluated. For
     *         example, if the domain is movie ratings on a scale of 1 to 5, this should be 5. While a
     *         {@link org.apache.mahout.cf.taste.recommender.Recommender} may estimate a preference value above 5.0, it
     *         isn't "fair" to consider that the system is actually suggesting an impossible rating of, say, 5.4 stars.
     *         In practice the application would cap this estimate to 5.0. Since evaluators evaluate
     *         the difference between estimated and actual value, this at least prevents this effect from unfairly
     *         penalizing a {@link org.apache.mahout.cf.taste.recommender.Recommender}
     */
    public float getMaxPreference() {
        return mahoutDataModelMappingDAO.getMaxPreference(tenantId, cutoffDate, actionTypeId);
    }

    /**
     * @see #getMaxPreference()
     */
    public float getMinPreference() {
        return mahoutDataModelMappingDAO.getMinPreference(tenantId, cutoffDate, actionTypeId);
    }

    /**
     * <p>
     * Triggers "refresh" -- whatever that means -- of the implementation. The general contract is that any
     * should always leave itself in a consistent, operational state, and that the refresh
     * atomically updates internal state from old to new.
     * </p>
     *
     * @param alreadyRefreshed s that are known to have already been
     *                         refreshed as a result of an initial call to a  method on some
     *                         object. This ensure that objects in a refresh dependency graph aren't refreshed twice
     *                         needlessly.
     */
    public void refresh(Collection<Refreshable> alreadyRefreshed) {
        cutoffDate = new Date();
    }
}
