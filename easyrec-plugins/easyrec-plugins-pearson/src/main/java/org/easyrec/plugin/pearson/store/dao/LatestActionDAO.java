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

package org.easyrec.plugin.pearson.store.dao;

import org.easyrec.model.core.ItemVO;
import org.easyrec.model.core.RatingVO;
import org.easyrec.plugin.pearson.model.User;
import org.easyrec.utils.spring.store.dao.TableCreatingDAO;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * DOCUMENT ME!
 * <p/>
 * <p>
 * <b>Company:&nbsp;</b> SAT, Research Studios Austria
 * </p>
 * <p/>
 * <p>
 * <b>Copyright:&nbsp;</b> (c) 2007
 * </p>
 * <p/>
 * <p>
 * <b>last modified:</b><br/>
 * $Author$<br/>
 * $Date$<br/>
 * $Revision$
 * </p>
 *
 * @author Patrick Marschik
 */
public interface LatestActionDAO extends TableCreatingDAO {
    //~ Static fields/initializers /////////////////////////////////////////////////////////////////////////////////////

    static final String DEFAULT_ACTION_TIME_COLUMN_NAME = "actionTime";
    static final String DEFAULT_ACTION_TYPE_COLUMN_NAME = "actionTypeId";
    static final String DEFAULT_ID_COLUMN_NAME = "id";
    static final String DEFAULT_ITEM_COLUMN_NAME = "itemId";
    static final String DEFAULT_ITEM_TYPE_COLUMN_NAME = "itemTypeId";
    static final String DEFAULT_PREVIOUS_ACTION_TIME_COLUMN_NAME = "previousActionTime";
    static final String DEFAULT_PREVIOUS_RATING_VALUE_COLUMN_NAME = "previousRatingValue";
    static final String DEFAULT_RATING_VALUE_COLUMN_NAME = "ratingValue";
    static final String DEFAULT_TABLE_NAME = "p_latestaction";
    static final String DEFAULT_TENANT_COLUMN_NAME = "tenantId";
    static final String DEFAULT_USER_COLUMN_NAME = "userId";

    //~ Methods ////////////////////////////////////////////////////////////////////////////////////////////////////////

    List<ItemVO<Integer, Integer>> getAvailableItemsForTenant(Integer tenantId, Integer itemTypeId);

    List<RatingVO<Integer, Integer>> getAverageRatingsForItem(Integer tenantId, Integer itemTypeId);

    List<RatingVO<Integer, Integer>> getAverageRatingsForUser(Integer tenantId, Integer itemTypeId);

    List<ItemVO<Integer, Integer>> getItemsNotRatedByUser(Integer tenantId, Integer userId,
                                                                   Integer itemTypeId);

    List<RatedTogether<Integer, Integer>> getItemsRatedTogether(Integer tenantId, Integer itemTypeId,
                                                                                  Integer item1Id, Integer item2Id,
                                                                                  Integer actionTypeId);

    List<RatedTogether<Integer, Integer>> getItemsRatedTogetherByUsers(Integer tenantId,
                                                                                         Integer itemTypeId,
                                                                                         Integer user1Id,
                                                                                         Integer user2Id,
                                                                                         Integer actionTypeId);

    List<RatingVO<Integer, Integer>> getLatestRatingPage(int page, int tenantId, int itemTypeId,
                                                                           Date since);

    int getLatestRatingPageCount(int tenantId, int itemTypeId, Date since);

    Date getLatestRatingTimeForTenant(Integer tenantId);

    List<RatingVO<Integer, Integer>> getLatestRatingsForTenant(Integer tenantId, Integer itemTypeId,
                                                                                 Integer itemId, Integer userId,
                                                                                 Date since);

    List<RatingVO<Integer, Integer>> getUpdatedRatingsForTenant(Integer tenantId, Integer itemTypeId,
                                                                                  Integer actionTypeId, Date since);

    List<User> getUsersThatRatedItem(Integer tenantId, Integer itemId, Integer itemTypeId);

    boolean didUserRateItem(Integer userId, ItemVO<Integer, Integer> item, Integer actionTypeId);

    int generateLatestActionForTenant(Integer tenantId, Date sinceLastAction);

    //~ Inner Classes //////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * DOCUMENT ME!<p><b>Company:&nbsp;</b> SAT, Research Studios Austria</p>
     * <p><b>Copyright:&nbsp;</b> (c) 2007</p>
     * <p><b>last modified:</b><br/> $Author$<br/> $Date$<br/> $Revision$</p>
     *
     * @param <T>  DOCUMENT ME!
     * @param <I>  java type of ids
     * @author Patrick Marschik
     */
    public static class RatedTogether<I extends Comparable<I>, T extends Comparable<T>>
            implements Map.Entry<RatingVO<I,T>, RatingVO<I,T>> {
        private final RatingVO<I,T> key;
        private RatingVO<I,T> value;

        public RatedTogether(final RatingVO<I,T> key, final RatingVO<I,T> value) {
            this.key = key;
            this.value = value;
        }

        public RatingVO<I,T> getKey() {
            return key;
        }

        public RatingVO<I,T> getRating1() {
            return key;
        }

        public RatingVO<I,T> getRating2() {
            return value;
        }

        public RatingVO<I,T> setValue(final RatingVO<I,T> value) {
            final RatingVO<I,T> old = value;
            this.value = value;

            return old;
        }

        public RatingVO<I,T> getValue() {
            return value;
        }
    }
}
