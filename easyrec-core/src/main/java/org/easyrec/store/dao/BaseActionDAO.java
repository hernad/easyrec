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
package org.easyrec.store.dao;

import org.easyrec.model.core.ItemVO;
import org.easyrec.model.core.transfer.TimeConstraintVO;
import org.easyrec.utils.spring.store.dao.TableCreatingDAO;

import javax.sql.DataSource;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * This interface provides methods to store data into and read <code>Action</code> entries from an easyrec database.
 * Provides base methods and constants.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: sat-rsa $<br/>
 * $Date: 2011-08-12 16:46:14 +0200 (Fri, 12 Aug 2011) $<br/>
 * $Revision: 113 $</p>
 *
 * @author Roman Cerny
 */
public interface BaseActionDAO<A, RI, AT, IT, I, RAT, T, U> extends TableCreatingDAO {
    ///////////////////////////////////////////////////////////////////////////
    // constants
    public final static String DEFAULT_TABLE_NAME = "action";

    public final static String DEFAULT_ID_COLUMN_NAME = "id";
    public final static String DEFAULT_TENANT_COLUMN_NAME = "tenantId";
    public final static String DEFAULT_USER_COLUMN_NAME = "userId";
    public final static String DEFAULT_SESSION_COLUMN_NAME = "sessionId";
    public final static String DEFAULT_IP_COLUMN_NAME = "ip";
    public final static String DEFAULT_ITEM_COLUMN_NAME = "itemId";
    public final static String DEFAULT_ITEM_TYPE_COLUMN_NAME = "itemTypeId";
    public final static String DEFAULT_ACTION_TYPE_COLUMN_NAME = "actionTypeId";
    public final static String DEFAULT_RATING_VALUE_COLUMN_NAME = "ratingValue";
    public final static String DEFAULT_SEARCH_SUCCEEDED_COLUMN_NAME = "searchSucceeded";
    public final static String DEFAULT_NUMBER_OF_FOUND_ITEMS = "numberOfFoundItems";
    public final static String DEFAULT_DESCRIPTION_COLUMN_NAME = "description";
    public final static String DEFAULT_ACTION_TIME_COLUMN_NAME = "actionTime";

    ///////////////////////////////////////////////////////////////////////////
    // non-generic methods

    /**
     * returns the <code>actionDate</code> of the latest action
     */
    public Date getNewestActionDate();

    public void setDataSource(DataSource datasource);

    public DataSource getDataSource();

    ///////////////////////////////////////////////////////////////////////////
    // generic methods

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Utility
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * returns the <code>actionDate</code> of the latest action, for a given tenant (and optional userId, and sessionId)
     */
    public Date getNewestActionDate(T tenant, U user, String sessionId);

    /**
     * Removes all actions by the given tenant.
     *
     * @param tenant the id of the tenant whichs action should be removed
     * @return number of actions that were removed
     */
    public int removeActionsByTenant(T tenant);

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Actions
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * inserts an <code>action</code> to the database
     *
     * @param useDateFromVO if set, use the given <code>actionTime</code>, else generate on database level
     */
    public int insertAction(A action, boolean useDateFromVO);

    /**
     * returns an iterator over actions, using the given bulk size (to prevent an out of memory error)
     * if the queries resultset is empty, an empty iterator will be returned containing no elements
     */
    public Iterator<A> getActionIterator(int bulkSize);

    /**
     * returns an iterator over actions, using the given bulk size (to prevent an out of memory error) and constraints
     * if the queries resultset is empty, an empty iterator will be returned containing no elements
     */
    public Iterator<A> getActionIterator(int bulkSize, TimeConstraintVO timeConstraints);

    /**
     * returns a list of <code>ActionVO</code>s from a given tenant (optionally: user, session)
     *
     * @param user      if set to <code>null</code> , retrieve all users (from tenant and session, or tenant only)
     * @param sessionId if set to <code>null</code>, retrieve all sessions (from user and/or tenant)
     */
    public List<A> getActionsFromUser(T tenant, U user, String sessionId);

    /**
     * returns a distinct list of items of a tenant.
     * only items on which an action has been performed are included in the result.
     *
     * @param tenant             the tenant.
     * @param consideredItemType the item type id.
     * @return list of items.
     */
    public List<I> getItemsOfTenant(T tenant, IT consideredItemType);

    public List<I> getItemsByUserActionAndType(T tenant, U user, String sessionId, AT consideredActionType,
                                               IT consideredItemType, Integer numberOfLastActionsConsidered);

    public List<I> getItemsByUserActionAndType(T tenantId, U userId, String sessionId, AT consideredActionTypeId,
                                               IT consideredItemTypeId, Double ratingThreshold, Integer numberOfLastActionsConsidered);

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Rankings
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public List<RI> getRankedItemsByActionType(T tenant, AT actionType, IT itemType, Integer numberOfResults,
                                               TimeConstraintVO timeConstraints, Boolean sortDesc);

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Ratings
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public List<RAT> getDirectItemRatings(T tenant, U user, String sessionId, IT itemType, Integer numberOfResults,
                                          TimeConstraintVO timeRange, Boolean sortByRatingInsteadOfActionTime,
                                          Boolean goodRatingsOnly, Integer tenantSpecificIdForRatingAction);

    // HINT: implement method that retrieves aggregated item ratings (using ActionToRatingAggregator) (Mantis Issue: #692)
    // public List<RAT> getAggregatedItemRatings(T tenant, U user, String sessionId, IT itemType, Integer numberOfResults, TimeConstraintVO timeRange, Boolean sortDescending, Boolean goodRatingsOnly);
}
