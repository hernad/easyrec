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

import org.easyrec.utils.spring.store.dao.TableCreatingDAO;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * This interface provides methods to access {@link org.easyrec.model.core.ItemAssocVO} entries in an easyrec database.
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
public interface BaseItemAssocDAO<IA, AI, IT, AT, I, C> extends TableCreatingDAO {
    ///////////////////////////////////////////////////////////////////////////////
    // constants
    public final static String DEFAULT_TABLE_NAME = "itemassoc";

    public final static String DEFAULT_ID_COLUMN_NAME = "id";
    public final static String DEFAULT_TENANT_COLUMN_NAME = "tenantId";
    public final static String DEFAULT_ITEM_FROM_COLUMN_NAME = "itemFromId";
    public final static String DEFAULT_ITEM_FROM_TYPE_COLUMN_NAME = "itemFromTypeId";
    public final static String DEFAULT_ASSOC_TYPE_COLUMN_NAME = "assocTypeId";
    public final static String DEFAULT_ASSOC_VALUE_COLUMN_NAME = "assocValue";
    public final static String DEFAULT_ITEM_TO_COLUMN_NAME = "itemToId";
    public final static String DEFAULT_ITEM_TO_TYPE_COLUMN_NAME = "itemToTypeId";
    public final static String DEFAULT_SOURCE_TYPE_COLUMN_NAME = "sourceTypeId";
    public final static String DEFAULT_SOURCE_INFO_COLUMN_NAME = "sourceInfo";
    public final static String DEFAULT_VIEW_TYPE_COLUMN_NAME = "viewTypeId";
    public final static String DEFAULT_ACTIVE_COLUMN_NAME = "active";
    public final static String DEFAULT_CHANGE_DATE_COLUMN_NAME = "changeDate";

    ////////////////////////////////////////////////////////////////////////////
    // non-generic methods

    /**
     * removes all item associations of the database
     */
    public int removeAllItemAssocs();

    ////////////////////////////////////////////////////////////////////////////
    // generic methods

    /**
     * inserts an item association to the database
     */
    public int insertItemAssoc(IA itemAssoc);

    /**
     * Inserts a list of item associations to the database, if some of them already exited the old ones get overriden.
     *
     * @param itemAssocs List of item associations to be inserted or updated.
     * @return Number of item associations inserted or updated
     */
    public int insertOrUpdateItemAssocs(List<IA> itemAssocs);

    /**
     * updates the given item association (using the primary key)
     */
    public int updateItemAssocUsingPrimaryKey(IA itemAssoc);

    /**
     * updates the given item association (using the unique key)
     */
    public int updateItemAssocUsingUniqueKey(IA itemAssoc);

    /**
     * returns an item association (using the unique key)
     */
    public IA loadItemAssocByUniqueKey(IA itemAssoc);

    /**
     * returns an item association (using the primary key)
     */
    public IA loadItemAssocByPrimaryKey(Integer itemAssocId);

    /**
     * returns an iterator over item associations, using the given bulk size (to prevent an out of memory error)
     * if the queries resultset is empty, an empty iterator will be returned containing no elements
     */
    public Iterator<IA> getItemAssocIterator(int bulkSize);

    /**
     * This is a QBE Implementation for removing ItemAssocs-Entries. Removes several ItemAssoc-Entries that match the given
     * Example. Attributes that are left out (set to NULL) will act like a wildcard. Meaning if you for example just
     * pass a sourceInfo="16" and an ItemToType="track", all itemassociations pointing to tracks with sourceInfo="16" will
     * be removed from the DB.
     */
    public int removeItemAssocsQBE(IA itemAssoc);

    /**
     * This Removes all ItemAssocs for a given tenant. In addition the assocType, sourceType and
     * a changeDate can be specified. All ItemAssocs matching the given criteria and older than the
     * specified changeDate will be removed.
     */
    public int removeItemAssocByTenant(Integer tenantId, AT assocType, Integer sourceType, Date changeDate);

    /**
     * returns a list of associated items for a specified <code>itemFromType</code>, <code>assocType</code> and <code>itemTo</code>
     * if the queries resultset is empty, an empty iterator will be returned containing no elements
     */
    public List<AI> getItemsFrom(IT itemFromType, AT assocType, I itemTo, C constraints);

    /**
     * returns a list of associated items for a specified <code>itemFrom</code>, <code>assocType</code> and <code>itemToType</code>
     * if the queries resultset is empty, an empty iterator will be returned containing no elements
     */
    public List<AI> getItemsTo(I itemFrom, AT assocType, IT itemToType, C constraints);

    /**
     * returns a list of item associations for a specified <code>itemFrom</code>, <code>assocType</code> and <code>itemTo</code>
     * if the queries resultset is empty, an empty iterator will be returned containing no elements
     */
    public List<IA> getItemAssocs(I itemFrom, AT assocType, I itemTo, C constraints);

    /**
     * returns a list of item associations matching the passed parameter values.
     * if the queries resultset is empty, an empty iterator will be returned containing no elements
     */
    public List<IA> getItemAssocsQBE(I itemFrom, AT assocType, I itemTo, C constraints);
}
