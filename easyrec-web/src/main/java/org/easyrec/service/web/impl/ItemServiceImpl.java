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
package org.easyrec.service.web.impl;


import org.easyrec.model.web.Item;
import org.easyrec.model.web.RemoteTenant;
import org.easyrec.service.web.ItemService;
import org.easyrec.store.dao.web.ItemDAO;
import org.easyrec.store.dao.web.RemoteTenantDAO;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * This Class is a Service for manipulating the item associations
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: phlavac $<br/>
 * $Date: 2010-04-05 21:45:48 +0200 (Mo, 05 Apr 2010) $<br/>
 * $Revision: 15919 $</p>
 *
 * @author Peter Hlavac
 */
public class ItemServiceImpl implements ItemService {

    ItemDAO itemDAO;
    RemoteTenantDAO remoteTenantDAO;

    public void setItemDAO(ItemDAO itemDAO) {
        this.itemDAO = itemDAO;
    }

    public void setRemoteTenantDAO(RemoteTenantDAO remoteTenantDAO) {
        this.remoteTenantDAO = remoteTenantDAO;
    }


    public ItemServiceImpl() {}

    /**
     * This function activates an item. An item may be activated
     * by an administrator or by a operator that owns that item.
     * (an item is member of a tenant, which is member of an operator).
     *
     * @param request
     * @throws Exception if item does not exist or operator is not allowed to alter item.
     */
    @Override
    public void activate(HttpServletRequest request) throws Exception {

        RemoteTenant r = remoteTenantDAO.get(request);
        if (r != null) {
            try {
                Integer i = Integer.parseInt(request.getParameter("itemId"));
                Item item = itemDAO.get(i);
                itemDAO.activate(r.getId(), item.getItemId(), item.getItemType());
            } catch (Exception e) {
                throw new Exception("Item does not exist." + e.getMessage());
            }
        } else throw new Exception("Operation not allowed.");
    }

    /**
     * This function deactivates an item. An item may be activated
     * by an administrator or by a operator that owns that item.
     * (an item is member of a tenant, which is member of an operator).
     *
     * @param request
     * @throws Exception if item does not exist or operator is not allowed to alter item.
     */
    @Override
    public void deactivate(HttpServletRequest request) throws Exception {
        RemoteTenant r = remoteTenantDAO.get(request);
        if (r != null) {
            try {
                Integer i = Integer.parseInt(request.getParameter("itemId"));
                Item item = itemDAO.get(i);
                itemDAO.deactivate(r.getId(), item.getItemId(), item.getItemType());
            } catch (Exception e) {
                throw new Exception("Item does not exist." + e.getMessage());
            }
        } else throw new Exception("Operation not allowed.");
    }

    /**
     * This function returns an item. An item may be returned if requested
     * by an administrator or by an operator that owns that item.
     * (an item is member of a tenant, which is member of an operator).
     *
     * @param request
     * @throws Exception if item does not exist or operator is not allowed to alter item.
     */
    @Override
    public Item get(HttpServletRequest request) throws Exception {
        RemoteTenant r = remoteTenantDAO.get(request);
        if (r != null) {
            try {
                Integer i = Integer.parseInt(request.getParameter("itemId"));
                Item item = itemDAO.get(i);
                return itemDAO.get(r, item.getItemId(), item.getItemType());
            } catch (Exception e) {
                throw new Exception("Item does not exist." + e.getMessage());
            }
        } else throw new Exception("Operation not allowed.");
    }

    /**
     * Filters a given list of items and returns only activated items.
     * The method retrieves cached items. If an item is loaded from the db it
     * might changed its status: active/deactivated. This is why a list of items
     * may not be appropiate.
     *
     * @param items
     * @return
     */
    @Override
    public List<Item> filterDeactivatedItems(List<Item> items) {
        List<Item> returnedItems = new ArrayList<Item>();
        for (Item item : items) {
            Item i = itemDAO.get(remoteTenantDAO.get(item.getTenantId()), item.getItemId(), item.getItemType());
            if (i != null && i.isActive()) {
                i.setValue(item.getValue());
                returnedItems.add(i);
            }
        }
        return returnedItems;
    }

}