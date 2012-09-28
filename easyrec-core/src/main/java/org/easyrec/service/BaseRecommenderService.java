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

import java.util.List;


/**
 * Base interface for RecommenderServices, describes methods to retrieve recommendations (from the recommender engine).
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: sat-rsa $<br/>
 * $Date: 2012-02-23 16:01:50 +0100 (Thu, 23 Feb 2012) $<br/>
 * $Revision: 140 $</p>
 *
 * @author Roman Cerny
 */
public interface BaseRecommenderService<R, I, ACT, IT, AT, T, U> {
    // Recommendations
    
    public List<I> getActionHistory(Integer tenantId, 
                                   Integer userId, 
                                   String sessionId, 
                                   AT consideredActionTypeId,
                                   IT consideredItemTypeId,
                                   Double ratingThreshold, 
                                   Integer numberOfLastActionsConsidered);
    
    public R getItemsBasedOnActionHistory(T tenant, U user, String sessionId, ACT consideredActionType,
                                          IT consideredItemType, Double ratingThreshold, Integer numberOfLastActionsConsidered, AT assocType,
                                          IT requestedItemType);

    public R getAlsoActedItems(T tenant, U user, String sessionId, AT assocType, I item, ACT filteredActionType,
                               IT requestedItemType);


    // HINT: The following method will be used (later) for online predictions (eg: for rating-based recommenders like the SlopeOne) (Mantis Issue: #472)
    // HINT: maybe add ItemType (as 2nd parameter), just in case someone might want to search for different itemTypes than the one of the passed item
    public R recommend(T tenant, I item, AT assocType);
}
