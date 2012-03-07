/*
 * Copyright 2010 Research Studios Austria Forschungsgesellschaft mBH
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

package org.easyrec.plugin.itemitem.store.dao.impl;

import org.easyrec.model.core.ItemVO;
import org.easyrec.model.core.RatingVO;
import org.easyrec.plugin.itemitem.store.dao.ActionDAO;

import java.util.*;

/**
 * Implementation of {@link ActionDAO} where the data is stored in-memory in a List. <p><b>Company:&nbsp;</b> SAT,
 * Research Studios Austria</p> <p><b>Copyright:&nbsp;</b> (c) 2007</p> <p><b>last modified:</b><br/> $Author$<br/>
 * $Date$<br/> $Revision$</p>
 *
 * @author Patrick Marschik
 */
public class ActionDAOMemoryImpl implements ActionDAO {
    // ------------------------------ FIELDS ------------------------------

    private List<RatingVO<Integer, Integer>> ratings = new LinkedList<RatingVO<Integer, Integer>>();
    private int actionTypeId;

    // --------------------------- CONSTRUCTORS ---------------------------

    public ActionDAOMemoryImpl(Collection<? extends RatingVO<Integer, Integer>> ratings,
                               int actionTypeId) {
        this.ratings.addAll(ratings);
        this.actionTypeId = actionTypeId;
    }

    // ------------------------ INTERFACE METHODS ------------------------


    // --------------------- Interface ActionDAO ---------------------

    public boolean didUserRateItem(final Integer userId, final ItemVO<Integer, Integer> item,
                                   final Integer actionTypeId) {
        if (actionTypeId != this.actionTypeId) return false;

        for (RatingVO<Integer, Integer> rating : ratings) {
            if (!rating.getUser().equals(userId)) continue;
            if (!rating.getItem().equals(item)) continue;

            return true;
        }

        return false;
    }

    public int generateActions(final Integer tenantId, final Date sinceLastAction) {
        return 0;
    }

    public List<ItemVO<Integer, Integer>> getAvailableItemsForTenant(final Integer tenantId,
                                                                              final Integer itemTypeId) {
        Set<ItemVO<Integer, Integer>> result = new HashSet<ItemVO<Integer, Integer>>();

        for (RatingVO<Integer, Integer> rating : ratings) {
            if (!tenantId.equals(rating.getItem().getTenant())) continue;
            if (!itemTypeId.equals(rating.getItem().getType())) continue;

            if (result.contains(rating.getItem())) continue;

            result.add(rating.getItem());
        }

        return new ArrayList<ItemVO<Integer, Integer>>(result);
    }

    public List<RatingVO<Integer, Integer>> getAverageRatingsForItem(final Integer tenantId,
                                                                                       final Integer itemTypeId) {
        Map<Integer, Double> sum = new HashMap<Integer, Double>();
        Map<Integer, Integer> count = new HashMap<Integer, Integer>();

        for (RatingVO<Integer, Integer> rating : ratings) {
            if (!tenantId.equals(rating.getItem().getTenant())) continue;
            if (!itemTypeId.equals(rating.getItem().getType())) continue;

            Integer key = rating.getItem().getItem();

            if (!sum.containsKey(key)) {
                sum.put(key, 0.0);
                count.put(key, 0);
            }

            sum.put(key, sum.get(key) + rating.getRatingValue());
            count.put(key, count.get(key) + 1);
        }

        List<RatingVO<Integer, Integer>> result = new ArrayList<RatingVO<Integer, Integer>>(
                sum.size());

        for (Integer key : sum.keySet()) {
            RatingVO<Integer, Integer> rating = new RatingVO<Integer, Integer>(
                    new ItemVO<Integer, Integer>(tenantId, key, itemTypeId), sum.get(key) / count.get(key),
                    count.get(key), null, null, null);

            result.add(rating);
        }

        return result;
    }

    public List<RatingVO<Integer, Integer>> getAverageRatingsForUser(final Integer tenantId,
                                                                                       final Integer itemTypeId) {
        Map<Integer, Double> sum = new HashMap<Integer, Double>();
        Map<Integer, Integer> count = new HashMap<Integer, Integer>();

        for (RatingVO<Integer, Integer> rating : ratings) {
            if (!tenantId.equals(rating.getItem().getTenant())) continue;
            if (!itemTypeId.equals(rating.getItem().getType())) continue;

            Integer key = rating.getUser();

            if (!sum.containsKey(key)) {
                sum.put(key, 0.0);
                count.put(key, 0);
            }

            sum.put(key, sum.get(key) + rating.getRatingValue());
            count.put(key, count.get(key) + 1);
        }

        List<RatingVO<Integer, Integer>> result = new ArrayList<RatingVO<Integer, Integer>>(
                sum.size());

        for (Integer key : sum.keySet()) {
            RatingVO<Integer, Integer> rating = new RatingVO<Integer, Integer>(null,
                    sum.get(key) / count.get(key), count.get(key), null, key, null);

            result.add(rating);
        }

        return result;
    }

    public List<RatedTogether<Integer, Integer>> getItemsRatedTogether(final Integer tenantId,
                                                                                         final Integer itemTypeId,
                                                                                         final Integer item1Id,
                                                                                         final Integer item2Id,
                                                                                         final Integer actionTypeId) {
        if (actionTypeId != this.actionTypeId)
            return new ArrayList<RatedTogether<Integer, Integer>>(0);

        Map<Integer, List<RatingVO<Integer, Integer>>> userRatings = new HashMap<Integer, List<RatingVO<Integer, Integer>>>();

        for (RatingVO<Integer, Integer> rating : ratings) {
            if (!rating.getItem().getTenant().equals(tenantId)) continue;
            if (!rating.getItem().getType().equals(itemTypeId)) continue;
            if (!rating.getItem().getItem().equals(item1Id) && !rating.getItem().getItem().equals(item2Id)) continue;

            Integer user = rating.getUser();

            if (!userRatings.containsKey(user)) {
                userRatings.put(user, new LinkedList<RatingVO<Integer, Integer>>());
            }

            userRatings.get(user).add(rating);
        }

        List<RatedTogether<Integer, Integer>> result = new LinkedList<RatedTogether<Integer, Integer>>();

        for (Integer user : userRatings.keySet()) {
            List<RatingVO<Integer, Integer>> ur = userRatings.get(user);

            RatingVO<Integer, Integer> rating1 = null;
            RatingVO<Integer, Integer> rating2 = null;

            for (RatingVO<Integer, Integer> rating : ur) {
                if (rating.getItem().getItem().equals(item1Id)) {
                    rating1 = rating;
                } else if (rating.getItem().getItem().equals(item2Id)) {
                    rating2 = rating;
                }

                if (rating1 != null && rating2 != null) break;
            }

            if (rating1 == null || rating2 == null) continue;

            rating1 = new RatingVO<Integer, Integer>(rating1.getItem(), rating1.getRatingValue(),
                    rating1.getCount(), null, rating1.getUser(), null);
            rating2 = new RatingVO<Integer, Integer>(rating2.getItem(), rating2.getRatingValue(),
                    rating2.getCount(), null, rating2.getUser(), null);

            result.add(new RatedTogether<Integer, Integer>(rating1, rating2));
        }

        return result;
    }

    public List<RatingVO<Integer, Integer>> getLatestRatingsForTenant(final Integer tenantId,
                                                                                        final Integer itemTypeId,
                                                                                        final Integer itemId,
                                                                                        final Integer userId,
                                                                                        final Date since) {
        List<RatingVO<Integer, Integer>> result = new LinkedList<RatingVO<Integer, Integer>>();

        for (RatingVO<Integer, Integer> rating : ratings) {
            if (!rating.getItem().getTenant().equals(tenantId)) continue;
            if (!rating.getItem().getType().equals(itemTypeId)) continue;
            if (itemId != null && !rating.getItem().getItem().equals(itemId)) continue;
            if (userId != null && !rating.getUser().equals(userId)) continue;
            if (since != null && !rating.getLastActionTime().after(since)) continue;

            result.add(rating);
        }

        return result;
    }

    public List<Integer> getUsersForTenant(final Integer tenantId) {
        Set<Integer> result = new HashSet<Integer>();

        for (RatingVO<Integer, Integer> rating : ratings) {
            if (result.contains(rating.getUser())) continue;

            result.add(rating.getUser());
        }

        return new ArrayList<Integer>(result);
    }

    // --------------------- Interface TableCreatingDAO ---------------------

    public String getDefaultTableName() { return null; }

    public String getTableCreatingSQLScriptName() { return null; }

    public void createTable() {}

    public boolean existsTable() { return true; }

    // --------------------- Interface TableCreatingDroppingDAO ---------------------

    public void dropTable() {}
}
