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
import java.util.List;

/**
 * This interface provides methods to store data into and read <code>RecommendedItem</code> entries from an easyrec database.
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
public interface BaseRecommendedItemDAO<RI, T> extends TableCreatingDAO {
    ////////////////////////////////////////////////////////////////////////////
    // constants
    public final static String DEFAULT_TABLE_NAME = "recommendeditem";

    public final static String DEFAULT_ID_COLUMN_NAME = "id";
    public final static String DEFAULT_ITEM_COLUMN_NAME = "itemId";
    public final static String DEFAULT_ITEM_TYPE_COLUMN_NAME = "itemTypeId";
    public final static String DEFAULT_RECOMMENDATION_COLUMN_NAME = "recommendationId";
    public final static String DEFAULT_ITEM_ASSOC_COLUMN_NAME = "itemAssocId";
    public final static String DEFAULT_PREDICTION_VALUE_COLUMN_NAME = "predictionValue";
    public final static String DEFAULT_EXPLANATION_COLUMN_NAME = "explanation";

    ////////////////////////////////////////////////////////////////////////////
    // generic methods

    /**
     * inserts a <code>recommended item</code> to the database
     */
    public int insertRecommendedItem(RI recommendedItem);

    /**
     * returns the <code>recommended item</code> belonging to the given recommendedItemId
     */
    public RI loadRecommendedItem(Integer recommendedItemId);

    /**
     * returns an iterator over recommended items, using the given bulk size (to prevent an out of memory error)
     * if the queries resultset is empty, an empty iterator will be returned containing no elements
     */
    public Iterator<RI> getRecommendedItemIterator(int bulkSize);

    /**
     * returns an iterator over recommended items, using the given bulk size (to prevent an out of memory error) and constraints
     * if the queries resultset is empty, an empty iterator will be returned containing no elements
     */
    public Iterator<RI> getRecommendedItemIterator(int bulkSize, TimeConstraintVO timeConstraints);

    /**
     * returns a list of recommended items using the given constraints
     * if the queries resultset is empty, an empty iterator will be returned containing no elements
     */
    public List<RI> getRecommendedItems(TimeConstraintVO timeConstraints);

    /**
     * returns a list of recommended items belonging to the given recomendationId
     * if the queries resultset is empty, an empty iterator will be returned containing no elements
     */
    public List<RI> getRecommendedItemsOfRecommendation(Integer recommendationId);

    /**
     * returns a list of recommended items belonging to the given recomendationId (passing a known tenantId to prevent a mysql join for performance reasons)
     * if the queries resultset is empty, an empty iterator will be returned containing no elements
     */
    public List<RI> getRecommendedItemsOfRecommendation(Integer recommendationId, T tenant);
}

