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

import org.easyrec.model.core.ActionVO;
import org.easyrec.model.core.ItemVO;
import org.easyrec.model.core.RankedItemVO;
import org.easyrec.model.core.RatingVO;
import org.easyrec.model.core.transfer.TimeConstraintVO;
import org.easyrec.service.BaseActionService;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;

/**
 * This interface defines methods to access Actions.
 * Domain specific version, supporting java enums for types.
 * This is a concrete (enum) typed interface of the generic {@link org.easyrec.service.BaseActionService} interface.
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
public interface DomainActionService extends
        BaseActionService<ActionVO<Integer, String>, ItemVO<Integer, String>, String, String, Integer, Integer> {
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Actions
    ///////////////////////////////////////////////////////////////////////////////////////////////   
    public void purchaseItem(Integer tenant, Integer user, String sessionId, String ip,
                             ItemVO<Integer, String> item, String description);

    public void purchaseItem(Integer tenant, Integer user, String sessionId, String ip,
                             ItemVO<Integer, String> item, String description, Date actionTime);

    public void viewItem(Integer tenant, Integer user, String sessionId, String ip,
                         ItemVO<Integer, String> item, String description);

    public void viewItem(Integer tenant, Integer user, String sessionId, String ip,
                         ItemVO<Integer, String> item, String description, Date actionTime);

    public void rateItem(Integer tenant, Integer user, String sessionId, String ip,
                         ItemVO<Integer, String> item, Integer ratingValue, String description);

    public void rateItem(Integer tenant, Integer user, String sessionId, String ip,
                         ItemVO<Integer, String> item, Integer ratingValue, String description,
                         Date actionTime);
    
    public void insertAction(Integer tenant, Integer user, String sessionId, String ip,
                         ItemVO<Integer, String> item, String actionType, Integer actionValue, String description);
    
    public void insertAction(Integer tenant, Integer user, String sessionId, String ip,
                         ItemVO<Integer, String> item, String actionType, Integer actionValue, String description,
                         Date actionTime);

    public void searchItem(Integer tenant, Integer user, String sessionId, String ip,
                           ItemVO<Integer, String> item, Boolean searchSucceeded, Integer numberOfFoundItems,
                           String description);

    public void searchItem(Integer tenant, Integer user, String sessionId, String ip,
                           ItemVO<Integer, String> item, Boolean searchSucceeded, Integer numberOfFoundItems,
                           String description, Date actionTime);

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Rankings
    ///////////////////////////////////////////////////////////////////////////////////////////////   
    public List<RankedItemVO<Integer, String>> mostBoughtItems(Integer tenant,
                                                                                @Nullable String itemType,
                                                                                Integer numberOfResults,
                                                                                @Nullable TimeConstraintVO timeRange,
                                                                                Boolean sortDescending);

    public List<RankedItemVO<Integer, String>> mostViewedItems(Integer tenant,
                                                                                @Nullable String itemType,
                                                                                Integer numberOfResults,
                                                                                @Nullable TimeConstraintVO timeRange,
                                                                                Boolean sortDescending);

    public List<RankedItemVO<Integer, String>> mostRatedItems(Integer tenant,
                                                                               @Nullable String itemType,
                                                                               Integer numberOfResults,
                                                                               @Nullable TimeConstraintVO timeRange,
                                                                               Boolean sortDescending);

    public List<RankedItemVO<Integer, String>> mostSearchedItems(Integer tenant,
                                                                                  @Nullable String itemType,
                                                                                  Integer numberOfResults,
                                                                                  @Nullable TimeConstraintVO timeRange,
                                                                                  Boolean sortDescending);

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Ratings
    ///////////////////////////////////////////////////////////////////////////////////////////////   
    public List<RatingVO<Integer, String>> itemRatings(Integer tenant, Integer user,
                                                                         @Nullable String sessionId,
                                                                         @Nullable String itemType,
                                                                         Integer numberOfResults,
                                                                         @Nullable TimeConstraintVO timeRange);

    public List<RatingVO<Integer, String>> badItemRatings(Integer tenant, Integer user,
                                                                            @Nullable String sessionId,
                                                                            @Nullable String itemType,
                                                                            Integer numberOfResults,
                                                                            @Nullable TimeConstraintVO timeRange);

    public List<RatingVO<Integer, String>> goodItemRatings(Integer tenant, Integer user,
                                                                             @Nullable String sessionId,
                                                                             String itemType, Integer numberOfResults,
                                                                             @Nullable TimeConstraintVO timeRange);

    public List<RatingVO<Integer, String>> lastGoodItemRatings(Integer tenant, Integer user,
                                                                                 @Nullable String sessionId,
                                                                                 @Nullable String itemType,
                                                                                 Integer numberOfResults);
}
