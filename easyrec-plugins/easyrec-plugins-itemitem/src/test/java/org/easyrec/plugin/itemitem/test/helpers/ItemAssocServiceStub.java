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

package org.easyrec.plugin.itemitem.test.helpers;

import org.easyrec.model.core.AssociatedItemVO;
import org.easyrec.model.core.ItemAssocVO;
import org.easyrec.model.core.ItemVO;
import org.easyrec.model.core.transfer.IAConstraintVO;
import org.easyrec.service.core.ItemAssocService;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Stub for {@link ItemAssocService} supporting only {@link #insertOrUpdateItemAssoc(org.easyrec.model.core.ItemAssocVO)}.
 * <p><b>Company:&nbsp;</b> SAT, Research Studios Austria</p> <p><b>Copyright:&nbsp;</b> (c) 2007</p> <p><b>last
 * modified:</b><br/> $Author$<br/> $Date$<br/> $Revision$</p>
 *
 * @author Patrick Marschik
 */
public class ItemAssocServiceStub implements ItemAssocService {
    // ------------------------------ FIELDS ------------------------------

    private List<ItemAssocVO<Integer,Integer>> itemAssocs;

    // --------------------------- CONSTRUCTORS ---------------------------

    public ItemAssocServiceStub() {
        itemAssocs = new LinkedList<ItemAssocVO<Integer,Integer>>();
    }

    // --------------------- GETTER / SETTER METHODS ---------------------

    public List<ItemAssocVO<Integer,Integer>> getItemAssocs() {
        return itemAssocs;
    }

    // ------------------------ INTERFACE METHODS ------------------------


    // --------------------- Interface BaseItemAssocService ---------------------

    public void importItemAssocsFromCSV(final String fileName) {}

    public int removeItemAssoc(final Integer itemAssocId) { return 0; }

    public int removeAllItemAssocs() { return 0; }

    public int insertItemAssoc(final ItemAssocVO<Integer,Integer> itemAssoc) {
        return 0;
    }

    public int insertOrUpdateItemAssoc(
            final ItemAssocVO<Integer,Integer> itemAssoc) {
        ItemAssocVO<Integer,Integer> clone;

        try {
            clone = itemAssoc.clone();
        } catch (CloneNotSupportedException e) {
            return 0;
        }

        ListIterator<ItemAssocVO<Integer,Integer>> it = itemAssocs.listIterator();

        while (it.hasNext()) {
            ItemAssocVO<Integer,Integer> ia = it.next();
            if (!ia.getTenant().equals(itemAssoc.getTenant())) continue;
            if (!ia.getItemFrom().getItem().equals(itemAssoc.getItemFrom().getItem())) continue;
            if (!ia.getItemFrom().getType().equals(itemAssoc.getItemFrom().getType())) continue;
            if (!ia.getItemTo().getItem().equals(itemAssoc.getItemTo().getItem())) continue;
            if (!ia.getItemTo().getType().equals(itemAssoc.getItemTo().getType())) continue;
            if (!ia.getAssocType().equals(itemAssoc.getAssocType())) continue;
            if (!ia.getSourceType().equals(itemAssoc.getSourceType())) continue;

            it.set(clone);
            return 1;
        }

        itemAssocs.add(clone);
        return 1;
    }

    public int insertOrUpdateItemAssocs(
            final List<ItemAssocVO<Integer,Integer>> itemAssocs) {
        return 0;
    }

    public boolean isActiveItemAssoc(final Integer itemAssocId) {return false;}

    public int activateItemAssoc(final Integer itemAssocId) {return 0;}

    public int deactivateItemAssoc(final Integer itemAssocId) {return 0;}

    public ItemAssocVO<Integer,Integer> loadItemAssoc(final Integer itemAssocId) {
        return null;
    }

    public Iterator<ItemAssocVO<Integer,Integer>> getItemAssocIterator(
            final int bulkSize) {return null;}

    public int removeItemAssocQBE(final ItemAssocVO<Integer,Integer> itemAssoc) {
        return 0;
    }

    public int removeAllItemAssocsFromTenant(final Integer tenant) {return 0;}

    public int removeAllItemAssocsFromSource(final Integer sourceType) { return 0; }

    public int removeAllItemAssocsFromSource(final Integer sourceType, final String sourceInfo) { return 0;}

    public void importItemAssocsFromCSV(final String fileName,
                                        final ItemAssocVO<Integer,Integer> defaults) {}

    public List<AssociatedItemVO<Integer, Integer>> getItemsFrom(final Integer itemFromType,
                                                                                   final Integer assocType,
                                                                                   final ItemVO<Integer, Integer> itemTo,
                                                                                   final IAConstraintVO<Integer, Integer> constraints) {
        return null;
    }

    public List<AssociatedItemVO<Integer, Integer>> getItemsTo(
            final ItemVO<Integer, Integer> itemFrom, final Integer assocTypeId, final Integer itemToType,
            final IAConstraintVO<Integer, Integer> constraints) { return null; }

    public List<ItemAssocVO<Integer,Integer>> getItemAssocs(
            final ItemVO<Integer, Integer> itemFrom, final Integer assocType,
            final ItemVO<Integer, Integer> itemTo,
            final IAConstraintVO<Integer, Integer> constraints) { return null; }

    public List<ItemAssocVO<Integer,Integer>> getItemAssocsFromTenant(
            final Integer tenant, final Integer numberOfResults) { return null; }

    public List<ItemAssocVO<Integer,Integer>> getItemAssocsForItem(
            final Integer tenant, final ItemVO<Integer, Integer> itemFrom, final Integer numberOfResults) {
        return null;
    }
}
