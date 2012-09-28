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

import org.easyrec.model.core.transfer.TimeConstraintVO;

import java.util.Iterator;
import java.util.List;

/**
 * Base interface for ActionServices, describes methods to access actions (within the recommender engine).
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
public interface BaseActionService<A, I, AT, IT, T, U> {
    ////////////////////////////////////////////////////////////////////////////
    // non-typed methods
    public void importActionsFromCSV(String fileName);

    ////////////////////////////////////////////////////////////////////////////
    // typed methods
    public int insertAction(A action);

    public int insertAction(A action, boolean useDateFromVO);

    public int removeActionsByTenant(T tenant);

    public Iterator<A> getActionIterator(int bulkSize);

    public Iterator<A> getActionIterator(int bulkSize, TimeConstraintVO timeConstraints);

    public List<A> getActionsFromUser(T tenant, U user, String sessionId);

    public List<I> getItemsOfTenant(T tenant, IT consideredItemType);

    public List<I> getItemsByUserActionAndType(T tenant, U user, String sessionId, AT consideredActionType,
                                               IT consideredItemType, Double ratingThreshold, Integer numberOfLastActionsConsidered);

    public void importActionsFromCSV(String fileName, A defaults);
}
