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
package org.easyrec.service.web;

import org.easyrec.model.web.Item;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * This Class is a Service for adding extra statistical Information
 * to Tenants. e.g. Userstatistics, number of items, number of actions,...
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
public interface ItemService {

    /**
     * This function activates an item. An item may be activated
     * by an administrator or by an operator that owns that item.
     * (an item is member of a tenant, which is member of an operator).
     *
     * @param request
     * @throws Exception if item does not exist or operator is not allowed to alter item.
     */
    public void activate(HttpServletRequest request) throws Exception;

    /**
     * This function deactivates an item. An item may be activated
     * by an administrator or by an operator that owns that item.
     * (an item is member of a tenant, which is member of an operator).
     *
     * @param request
     * @throws Exception if item does not exist or operator is not allowed to alter item.
     */
    public void deactivate(HttpServletRequest request) throws Exception;

    /**
     * This function returns an item. An item may be returned if requested
     * by an administrator or by an operator that owns that item.
     * (an item is member of a tenant, which is member of an operator).
     *
     * @param request
     * @throws Exception if item does not exist or operator is not allowed to alter item.
     */
    public Item get(HttpServletRequest request) throws Exception;


    /**
     * Filters a given list of items and returns only activated items.
     *
     * @param items
     * @return
     */
    public List<Item> filterDeactivatedItems(List<Item> items);


}
