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
package org.easyrec.store.dao.web;


/**
 * This manages importing rules or setting the active or inactive.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: phlavac $<br/>
 * $Date: 2010-04-24 16:58:03 +0200 (Sa, 24 Apr 2010) $<br/>
 * $Revision: 16095 $</p>
 *
 * @author <AUTHOR>
 */
public interface AssocDAO extends BasicDAO {


    /**
     * This function activates a rule.
     *
     * @param ruleId
     */
    public void activate(Integer tenantId, Integer itemFromId, Integer itemToId);

    /**
     * This function deactivates a rule.
     *
     * @param ruleId
     */
    public void deactivate(Integer tenantId, Integer itemFromId, Integer itemToId);


}
