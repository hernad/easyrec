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

import org.easyrec.model.core.transfer.TimeConstraintVO;
import org.easyrec.utils.spring.store.dao.TableCreatingDAO;

import java.util.Iterator;

/**
 * This interface provides methods to store data into and read <code>Recommendation</code> entries from an easyrec database.
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
 * $Date: 2011-08-12 16:46:14 +0200 (Fr, 12 Aug 2011) $<br/>
 * $Revision: 113 $</p>
 *
 * @author Roman Cerny
 */
public interface BaseRecommendationDAO<R> extends TableCreatingDAO {
    ///////////////////////////////////////////////////////////////////////////
    // constants
    public final static String DEFAULT_TABLE_NAME = "recommendation";

    public final static String DEFAULT_ID_COLUMN_NAME = "id";
    public final static String DEFAULT_TENANT_COLUMN_NAME = "tenantId";
    public final static String DEFAULT_USER_COLUMN_NAME = "userId";

    public final static String DEFAULT_QUERIED_ITEM_COLUMN_NAME = "queriedItemId";
    public final static String DEFAULT_QUERIED_ITEM_TYPE_COLUMN_NAME = "queriedItemTypeId";
    public final static String DEFAULT_QUERIED_ASSOC_TYPE_COLUMN_NAME = "queriedAssocTypeId";
    public final static String DEFAULT_RELATED_ACTION_TYPE_COLUMN_NAME = "relatedActionTypeId";

    public final static String DEFAULT_RECOMMENDATION_STRATEGY_COLUMN_NAME = "recommendationStrategy";
    public final static String DEFAULT_EXPLANATION_COLUMN_NAME = "explanation";
    public final static String DEFAULT_RECOMMENDATION_TIME_COLUMN_NAME = "recommendationTime";

    ///////////////////////////////////////////////////////////////////////////
    // non-generic methods

    /**
     * returns the tenantId of the <code>recommendation</code> belonging to the given recommendationId
     *
     * @param useCache if set, an internal cache is used, else each call a new query is executed
     */
    public Integer getTenantIdOfRecommendationById(Integer recommendationId, boolean useCache);

    ///////////////////////////////////////////////////////////////////////////
    // generic methods

    /**
     * inserts a <code>recommendation</code> to the database
     */
    public int insertRecommendation(R recommendation);

    /**
     * returns the <code>recommendation</code> belonging to the given recommendationId
     */
    public R loadRecommendation(Integer recommendationId);

    /**
     * returns an iterator over recommendations, using the given bulk size (to prevent an out of memory error)
     * if the queries resultset is empty, an empty iterator will be returned containing no elements
     */
    public Iterator<R> getRecommendationIterator(int bulkSize);

    /**
     * returns an iterator over recommendations, using the given bulk size (to prevent an out of memory error) and constraints
     * if the queries resultset is empty, an empty iterator will be returned containing no elements
     */
    public Iterator<R> getRecommendationIterator(int bulkSize, TimeConstraintVO timeConstraints);
}
