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
package org.easyrec.service.domain;

import org.easyrec.model.core.ItemVO;
import org.easyrec.model.core.RecommendationVO;
import org.easyrec.service.BaseRecommenderService;

/**
 * This interface defines a typed <code>Recommender</code> for any domain.
 * Domain specific version, supporting java enums for types.
 * This is a concrete (enum) typed interface of the generic {@link org.easyrec.service.BaseRecommenderService} interface.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: sat-rsa $<br/>
 * $Date: 2012-02-23 16:01:50 +0100 (Do, 23 Feb 2012) $<br/>
 * $Revision: 140 $</p>
 *
 * @author Roman Cerny
 */
public interface DomainRecommenderService extends
        BaseRecommenderService<RecommendationVO<Integer, String>, ItemVO<Integer, String>, String, String, String, Integer, Integer> {
    // Recommendations        
    public RecommendationVO<Integer, String> itemsBasedOnPurchaseHistory(
            Integer tenant, Integer user, String sessionId, String consideredItemType,
            Integer numberOfLastActionsConsidered, String assocType, String requestedItemType);

    public RecommendationVO<Integer, String> itemsBasedOnViewingHistory(
            Integer tenant, Integer user, String sessionId, String consideredItemType,
            Integer numberOfLastActionsConsidered, String assocType, String requestedItemType);

    public RecommendationVO<Integer, String> itemsBasedOnSearchingHistory(
            Integer tenant, Integer user, String sessionId, String consideredItemType,
            Integer numberOfLastActionsConsidered, String assocType, String requestedItemType);

    public RecommendationVO<Integer, String> alsoBoughtItems(Integer tenant,
                                                           Integer user,
                                                           String sessionId,
                                                           ItemVO<Integer, String> item,
                                                           String requestedItemType);

    public RecommendationVO<Integer, String> alsoViewedItems(Integer tenant,
                                                           Integer user,
                                                           String sessionId,
                                                           ItemVO<Integer, String> item,
                                                           String requestedItemType);

    public RecommendationVO<Integer, String> alsoSearchedItems(Integer tenant,
                                                             Integer user,
                                                             String sessionId,
                                                             ItemVO<Integer, String> item,
                                                             String requestedItemType);

    public RecommendationVO<Integer, String> alsoGoodRatedItems(Integer tenant,
                                                              Integer user,
                                                              String sessionId,
                                                              ItemVO<Integer, String> item,
                                                              String requestedItemType);
}
