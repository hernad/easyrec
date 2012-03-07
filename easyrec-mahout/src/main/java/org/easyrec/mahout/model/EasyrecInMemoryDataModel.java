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

package org.easyrec.mahout.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.AbstractDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: dmann
 * Date: 18.10.11
 * Time: 17:24
 * To change this template use File | Settings | File Templates.
 */
public class EasyrecInMemoryDataModel extends AbstractDataModel {
    private Log logger = LogFactory.getLog(getClass());
    private DataModel inMemoryDelegate;
    private DataModel easyrecDataModelDelegate;

    public EasyrecInMemoryDataModel(DataModel easyrecDataModelDelegate) {
        this.easyrecDataModelDelegate = easyrecDataModelDelegate;
        intializeDelegate();
    }

    @Override
    public void refresh(Collection<Refreshable> alreadyRefreshed) {
        easyrecDataModelDelegate.refresh(alreadyRefreshed);
        intializeDelegate();
    }

    private void intializeDelegate() {
        try {
        //iterate over all user ids
        LongPrimitiveIterator it = easyrecDataModelDelegate.getUserIDs();
        FastByIDMap<PreferenceArray> preferences = new FastByIDMap<PreferenceArray>(easyrecDataModelDelegate.getNumUsers());
        FastByIDMap<FastByIDMap<Long>> timestamps = new FastByIDMap<FastByIDMap<Long>>(easyrecDataModelDelegate.getNumUsers());
        while (it.hasNext()) {
            Long userId = it.next();
            //get preferences for each user
            PreferenceArray prefs = easyrecDataModelDelegate.getPreferencesFromUser(userId);
            preferences.put(userId,prefs);
            //get preference times for each user
            FastByIDMap<Long> timestampsForUser = new FastByIDMap<Long>(prefs.getIDs().length);
            for (Long itemId: prefs.getIDs()) {
                timestampsForUser.put(itemId,easyrecDataModelDelegate.getPreferenceTime(userId, itemId));
            }
            timestamps.put(userId, timestampsForUser);
        }
        //generate GenericDataModel
        setMaxPreference(easyrecDataModelDelegate.getMaxPreference());
        setMinPreference(easyrecDataModelDelegate.getMinPreference());
        this.inMemoryDelegate = new GenericDataModel(preferences,timestamps);
        } catch (TasteException e) {
            logger.warn("caught exception while reading preference data", e);
        }
    }

    @Override
    public LongPrimitiveIterator getUserIDs() throws TasteException {
        return inMemoryDelegate.getUserIDs();
    }

    @Override
    public PreferenceArray getPreferencesFromUser(long userID) throws TasteException {
        return inMemoryDelegate.getPreferencesFromUser(userID);
    }

    @Override
    public FastIDSet getItemIDsFromUser(long userID) throws TasteException {
        return inMemoryDelegate.getItemIDsFromUser(userID);
    }

    @Override
    public LongPrimitiveIterator getItemIDs() throws TasteException {
        return inMemoryDelegate.getItemIDs();
    }

    @Override
    public PreferenceArray getPreferencesForItem(long itemID) throws TasteException {
        return inMemoryDelegate.getPreferencesForItem(itemID);
    }

    @Override
    public Float getPreferenceValue(long userID, long itemID) throws TasteException {
        return inMemoryDelegate.getPreferenceValue(userID, itemID);
    }

    @Override
    public Long getPreferenceTime(long userID, long itemID) throws TasteException {
        return inMemoryDelegate.getPreferenceTime(userID, itemID);
    }

    @Override
    public int getNumItems() throws TasteException {
        return inMemoryDelegate.getNumItems();
    }

    @Override
    public int getNumUsers() throws TasteException {
        return inMemoryDelegate.getNumUsers();
    }

    @Override
    public int getNumUsersWithPreferenceFor(long itemID) throws TasteException {
        return inMemoryDelegate.getNumUsersWithPreferenceFor(itemID);
    }

    @Override
    public int getNumUsersWithPreferenceFor(long itemID1, long itemID2) throws TasteException {
        return inMemoryDelegate.getNumUsersWithPreferenceFor(itemID1, itemID2);
    }

    @Override
    public void setPreference(long userID, long itemID, float value) throws TasteException {
        inMemoryDelegate.setPreference(userID, itemID, value);
    }

    @Override
    public void removePreference(long userID, long itemID) throws TasteException {
        inMemoryDelegate.removePreference(userID, itemID);
    }

    @Override
    public boolean hasPreferenceValues() {
        return inMemoryDelegate.hasPreferenceValues();
    }

}
