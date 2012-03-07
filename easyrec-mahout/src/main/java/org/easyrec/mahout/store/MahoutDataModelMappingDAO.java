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

package org.easyrec.mahout.store;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.model.PreferenceArray;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: dmann
 * Date: 13.09.11
 * Time: 11:49
 * To change this template use File | Settings | File Templates.
 */
public interface MahoutDataModelMappingDAO {

    /**
     * This function
     *
     * @param tenantId   internal tenant id
     * @param cutoffDate the maximal age of the data you plan to use. Use new Date() for the complete data.
     * @return all user IDs in the model, in order
     */
    LongPrimitiveIterator getUserIDs(int tenantId, Date cutoffDate, int actionTypeId);

    /**
     * This function
     *
     * @param tenantId   internal tenant id
     * @param cutoffDate the maximal age of the data you plan to use. Use new Date() for the complete data.
     * @param userID
     * @return user's preferences, ordered by item ID
     * @throws org.apache.mahout.cf.taste.common.NoSuchUserException
     *          if the user does not exist
     */
    PreferenceArray getPreferencesFromUser(int tenantId, Date cutoffDate, long userID, int actionTypeId) throws TasteException;


    /**
     * This function
     *
     * @param tenantId   internal tenant id
     * @param cutoffDate the maximal age of the data you plan to use. Use new Date() for the complete data.
     * @param userID
     * @return user's preferences, ordered by item ID
     * @throws org.apache.mahout.cf.taste.common.NoSuchUserException
     *          if the user does not exist
     */
    PreferenceArray getBooleanPreferencesFromUser(int tenantId, Date cutoffDate, long userID, int actionTypeId) throws TasteException;

    /**
     * This function
     *
     * @param tenantId   internal tenant id
     * @param cutoffDate the maximal age of the data you plan to use. Use new Date() for the complete data.
     * @param userID
     * @return IDs of items user expresses a preference for
     * @throws org.apache.mahout.cf.taste.common.NoSuchUserException
     *          if the user does not exist
     */
    FastIDSet getItemIDsFromUser(int tenantId, Date cutoffDate, long userID, int actionTypeId) throws TasteException;

    /**
     * This function
     *
     * @param tenantId   internal tenant id
     * @param cutoffDate the maximal age of the data you plan to use. Use new Date() for the complete data.
     * @return a {@link LongPrimitiveIterator} of all item IDs in the model, in order
     */
    LongPrimitiveIterator getItemIDs(int tenantId, Date cutoffDate, int actionTypeId);

    /**
     * This function
     *
     * @param tenantId   internal tenant id
     * @param cutoffDate the maximal age of the data you plan to use. Use new Date() for the complete data.
     * @param itemID
     * @return all existing Preference's expressed for that item, ordered by user ID, as an array
     * @throws org.apache.mahout.cf.taste.common.NoSuchItemException
     *          if the item does not exist
     */
    PreferenceArray getPreferencesForItem(int tenantId, Date cutoffDate, long itemID, int actionTypeId) throws TasteException;

    /**
     * This function
     *
     * @param tenantId   internal tenant id
     * @param cutoffDate the maximal age of the data you plan to use. Use new Date() for the complete data.
     * @param itemID
     * @return all existing Preference's expressed for that item, ordered by user ID, as an array
     * @throws org.apache.mahout.cf.taste.common.NoSuchItemException
     *          if the item does not exist
     */
    PreferenceArray getBooleanPreferencesForItem(int tenantId, Date cutoffDate, long itemID, int actionTypeId) throws TasteException;

    /**
     * @param tenantId   internal tenant id
     * @param cutoffDate the maximal age of the data you plan to use. Use new Date() for the complete data.
     * @param userID
     * @param itemID
     * @return preference value from the given user for the given item or null if none exists
     * @throws org.apache.mahout.cf.taste.common.NoSuchUserException
     *          if the user does not exist
     */
    Float getPreferenceValue(int tenantId, Date cutoffDate, long userID, long itemID, int actionTypeId) throws TasteException;

    /**
     * @param tenantId   internal tenant id
     * @param cutoffDate the maximal age of the data you plan to use. Use new Date() for the complete data.
     * @param userID
     * @param itemID
     * @return preference value from the given user for the given item or null if none exists
     * @throws org.apache.mahout.cf.taste.common.NoSuchUserException
     *          if the user does not exist
     */
    Float getBooleanPreferenceValue(int tenantId, Date cutoffDate, long userID, long itemID, int actionTypeId) throws TasteException;

    /**
     * This function retrieves the time at which a preference value from a user and item was set, if known.
     * Time is expressed in the usual way, as a number of milliseconds since the epoch.
     *
     * @param tenantId   internal tenant id
     * @param cutoffDate the maximal age of the data you plan to use. Use new Date() for the complete data.
     * @param userID
     * @param itemID
     * @return time at which preference was set or null if no preference exists or its time is not known
     * @throws org.apache.mahout.cf.taste.common.NoSuchUserException
     *          if the user does not exist
     */
    Long getPreferenceTime(int tenantId, Date cutoffDate, long userID, long itemID, int actionTypeId) throws TasteException;

    /**
     * This function
     *
     * @param tenantId   internal tenant id
     * @param cutoffDate the maximal age of the data you plan to use. Use new Date() for the complete data.
     * @return total number of items known to the model. This is generally the union of all items preferred by
     *         at least one user but could include more.
     */
    int getNumItems(int tenantId, Date cutoffDate, int actionTypeId);

    /**
     * This function
     *
     * @param tenantId   internal tenant id
     * @param cutoffDate the maximal age of the data you plan to use. Use new Date() for the complete data.
     * @return total number of users known to the model.
     */
    int getNumUsers(int tenantId, Date cutoffDate, int actionTypeId);

    /**
     * This function
     *
     * @param tenantId   internal tenant id
     * @param cutoffDate the maximal age of the data you plan to use. Use new Date() for the complete data.
     * @param itemID
     * @return the number of users who have expressed a preference for the item
     */
    int getNumUsersWithPreferenceFor(int tenantId, Date cutoffDate, long itemID, int actionTypeId);

    /**
     * This function
     *
     * @param tenantId   internal tenant id
     * @param cutoffDate the maximal age of the data you plan to use. Use new Date() for the complete data.
     * @param itemID1
     * @param itemID2
     * @return the number of users who have expressed a preference for the items
     */
    int getNumUsersWithPreferenceFor(int tenantId, Date cutoffDate, long itemID1, long itemID2, int actionTypeId);

    /**
     * This function
     *
     * @param tenantId   internal tenant id
     * @param cutoffDate the maximal age of the data you plan to use. Use new Date() for the complete data.
     * @return true iff this implementation actually stores and returns distinct preference values;
     *         that is, if it is not a 'boolean' DataModel
     */
    boolean hasPreferenceValues(int tenantId, Date cutoffDate, int actionTypeId);

    /**
     * This function
     *
     * @param tenantId   internal tenant id
     * @param cutoffDate the maximal age of the data you plan to use. Use new Date() for the complete data.
     * @return the maximum preference value that is possible in the current problem domain being evaluated. For
     *         example, if the domain is movie ratings on a scale of 1 to 5, this should be 5. While a
     *         {@link org.apache.mahout.cf.taste.recommender.Recommender} may estimate a preference value above 5.0, it
     *         isn't "fair" to consider that the system is actually suggesting an impossible rating of, say, 5.4 stars.
     *         In practice the application would cap this estimate to 5.0. Since evaluators evaluate
     *         the difference between estimated and actual value, this at least prevents this effect from unfairly
     *         penalizing a {@link org.apache.mahout.cf.taste.recommender.Recommender}
     */
    float getMaxPreference(int tenantId, Date cutoffDate, int actionTypeId);

    /**
     * This function
     *
     * @param tenantId   internal tenant id
     * @param cutoffDate the maximal age of the data you plan to use. Use new Date() for the complete data.
     * @return @see #getMaxPreference()
     */
    float getMinPreference(int tenantId, Date cutoffDate, int actionTypeId);

}
