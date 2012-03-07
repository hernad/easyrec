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
package org.easyrec.service;

import java.util.Iterator;
import java.util.List;

/**
 * Base interface for ItemAssocServices, describes methods to access item association rules (within the recommender engine).
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
public interface BaseItemAssocService<IA, ST, AI, IT, AT, I, C, T> {
    ////////////////////////////////////////////////////////////////////////////
    // constants

    // HINT: maybe move these constants to sat-util/CSVImport

    // csv import
    //    public static final String COMMAND = "command";
    //    public static final String COMMAND_DELIMITER = ":";
    //    public static final String COMMAND_INSERT = "insert";
    //    public static final String COMMAND_REMOVE = "remove";

    ////////////////////////////////////////////////////////////////////////////
    // non-typed methods
    public void importItemAssocsFromCSV(String fileName);

    public int removeItemAssoc(Integer itemAssocId);

    public int removeAllItemAssocs();

    ////////////////////////////////////////////////////////////////////////////
    // typed methods
    public int insertItemAssoc(IA itemAssoc);

    public int insertOrUpdateItemAssoc(IA itemAssoc);

    public int insertOrUpdateItemAssocs(List<IA> itemAssocs);

    public boolean isActiveItemAssoc(Integer itemAssocId);

    public int activateItemAssoc(Integer itemAssocId);

    public int deactivateItemAssoc(Integer itemAssocId);

    public IA loadItemAssoc(Integer itemAssocId);

    public Iterator<IA> getItemAssocIterator(int bulkSize);

    public int removeItemAssocQBE(IA itemAssoc);

    public int removeAllItemAssocsFromTenant(T tenant);

    public int removeAllItemAssocsFromSource(ST sourceType);

    public int removeAllItemAssocsFromSource(ST sourceType, String sourceInfo);

    public void importItemAssocsFromCSV(String fileName, IA defaults);

    public List<AI> getItemsFrom(IT itemFromType, AT assocType, I itemTo, C constraints);

    public List<AI> getItemsTo(I itemFrom, AT assocTypeId, IT itemToType, C constraints);

    public List<IA> getItemAssocs(I itemFrom, AT assocType, I itemTo, C constraints);

    public List<IA> getItemAssocsFromTenant(T tenant, Integer numberOfResults);

    public List<IA> getItemAssocsForItem(T tenant, I itemFrom, Integer numberOfResults);
}
