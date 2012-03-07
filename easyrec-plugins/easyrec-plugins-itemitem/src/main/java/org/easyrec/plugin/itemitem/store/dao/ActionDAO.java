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

package org.easyrec.plugin.itemitem.store.dao;

import com.google.common.base.Objects;
import org.easyrec.model.core.ItemVO;
import org.easyrec.model.core.RatingVO;
import org.easyrec.utils.spring.store.dao.TableCreatingDroppingDAO;

import java.util.Date;
import java.util.List;


/**
 * Stores only the latest actions of a user ignoring doubly rated items. <p/> <p> <b>Company:&nbsp;</b> SAT, Research
 * Studios Austria </p> <p/> <p> <b>Copyright:&nbsp;</b> (c) 2007 </p> <p/> <p> <b>last modified:</b><br/> $Author$<br/>
 * $Date$<br/> $Revision$ </p>
 *
 * @author Patrick Marschik
 */
public interface ActionDAO extends TableCreatingDroppingDAO {
    // ------------------------------ FIELDS ------------------------------

    static final String TABLE_NAME = "ii_action";
    static final String COLUMN_ID = "id";
    static final String COLUMN_ACTIONTIME = "actionTime";
    static final String COLUMN_ACTIONTYPEID = "actionTypeId";
    static final String COLUMN_ITEMID = "itemId";
    static final String COLUMN_ITEMTYPEID = "itemTypeId";
    static final String COLUMN_RATINGVALUE = "ratingValue";
    static final String COLUMN_TENANTID = "tenantId";
    static final String COLUMN_USERID = "userId";

    // -------------------------- OTHER METHODS --------------------------

    /**
     * Tests the datasource wether the user has rated a specific item.
     *
     * @param userId       User to test.
     * @param item         Item to test.
     * @param actionTypeId Action type id of actions to consider.
     * @return {@code true} if the user rated the item, {@code false} otherwise.
     */
    boolean didUserRateItem(Integer userId, ItemVO<Integer, Integer> item, Integer actionTypeId);

    /**
     * Copies actions from easyrec to the plugins own action table.
     *
     * @param tenantId        Tenant to copy actions for.
     * @param sinceLastAction Starting date of actions to be copied.
     * @return Number of actions copied.
     */
    int generateActions(Integer tenantId, Date sinceLastAction);

    /**
     * Gets a list of all items a tenant has (based on the actions performed by users.)
     *
     * @param tenantId   The tenant to get items for.
     * @param itemTypeId The type of items to get.
     * @return List of items.
     */
    List<ItemVO<Integer, Integer>> getAvailableItemsForTenant(Integer tenantId, Integer itemTypeId);

    /**
     * Gets the average rating of all items.
     *
     * @param tenantId   Tenant id.
     * @param itemTypeId Item type id.
     * @return List of ratings containing the average rating of each item.
     */
    List<RatingVO<Integer,Integer>> getAverageRatingsForItem(Integer tenantId, Integer itemTypeId);

    /**
     * Gets the average rating of all users.
     *
     * @param tenantId   Tenant id.
     * @param itemTypeId Item type id.
     * @return List of ratings containing the average rating of each user.
     */
    List<RatingVO<Integer,Integer>> getAverageRatingsForUser(Integer tenantId, Integer itemTypeId);

    /**
     * Gets all instances where {@code item1Id} and {@code item2Id} were rated together by the same user.
     *
     * @param tenantId     Tenant id.
     * @param itemTypeId   Item type id.
     * @param item1Id      Item 1 id.
     * @param item2Id      Item 2 id.
     * @param actionTypeId Action type id.
     * @return List of ratings containing all occurrences where the same user rated both {@code item1Id} and {@code
     *         item2Id}.
     */
    List<RatedTogether<Integer, Integer>> getItemsRatedTogether(Integer tenantId, Integer itemTypeId,
                                                                                  Integer item1Id, Integer item2Id,
                                                                                  Integer actionTypeId);

    /**
     * Get all ratings that happend after {@code since}.
     *
     * @param tenantId   Tenant id.
     * @param itemTypeId Item type id.
     * @param itemId     Item id.
     * @param userId     User id.
     * @param since      Threshold for {@code actionTime}.
     * @return List of ratings containing all ratings that happened after {@code since}.
     */
    List<RatingVO<Integer,Integer>> getLatestRatingsForTenant(Integer tenantId, Integer itemTypeId,
                                                                                 Integer itemId, Integer userId,
                                                                                 Date since);

    /**
     * Get a distinct list of users that perfromed an action.
     *
     * @param tenantId Tenant id.
     * @return List of user ids.
     */
    List<Integer> getUsersForTenant(Integer tenantId);

    // -------------------------- INNER CLASSES --------------------------

    /**
     * Stores a pair of ratings.<p><b>Company:&nbsp;</b> SAT, Research Studios Austria</p> <p><b>Copyright:&nbsp;</b>
     * (c) 2007</p> <p><b>last modified:</b><br/> $Author$<br/> $Date$<br/> $Revision$</p>
     *
     * @param <I>  java type of ids
     * @param <T>  tava type of types (item type etc)
     * @author Patrick Marschik
     */
    public static class RatedTogether<I extends Comparable<I>, T extends Comparable<T>> {
        // ------------------------------ FIELDS ------------------------------

        private final RatingVO<I,T> rating1;
        private RatingVO<I,T> rating2;

        // --------------------------- CONSTRUCTORS ---------------------------

        public RatedTogether(final RatingVO<I,T> rating1, final RatingVO<I,T> rating2) {
            if (rating1 == null || rating2 == null) throw new NullPointerException("ratings mustn't be null.");

            this.rating1 = rating1;
            this.rating2 = rating2;
        }

        // --------------------- GETTER / SETTER METHODS ---------------------

        public RatingVO<I,T> getRating1() {
            return rating1;
        }

        public RatingVO<I,T> getRating2() {
            return rating2;
        }

        // ------------------------ CANONICAL METHODS ------------------------

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (!(o instanceof RatedTogether)) return false;

            final RatedTogether that = (RatedTogether) o;

            if (!rating1.equals(that.rating1)) return false;
            if (!rating2.equals(that.rating2)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = rating1.hashCode();
            result = 31 * result + rating2.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this).add("rating1", rating1).add("rating2", rating2).toString();
        }

        // -------------------------- OTHER METHODS --------------------------

        public RatingVO<I,T> setValue(final RatingVO<I,T> value) {
            final RatingVO<I,T> old = this.rating2;
            this.rating2 = value;

            return old;
        }
    }
}
