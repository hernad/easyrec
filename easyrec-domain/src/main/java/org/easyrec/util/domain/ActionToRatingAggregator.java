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
package org.easyrec.util.domain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyrec.model.core.ActionVO;
import org.easyrec.model.core.RatingVO;
import org.easyrec.service.domain.TypeMappingService;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Methods to convert a bunch of <code>Actions</code> into a single <code>Rating</code>.
 * Provides various aggregation types.
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
public class ActionToRatingAggregator {
    // logging
    private static final Log logger = LogFactory.getLog(ActionToRatingAggregator.class);

    /**
     * aggregates multiple <code>ActionVO</code>s to a single <code>RatingVO</code>.
     * assumes (precondition) that all passed <code>ActionVO</code>s are from the same Tenant-User-Item triple.
     *
     * @param actionsForUserItemPair
     * @param actionMapping
     * @param aggregateType
     * @return
     */
    public static RatingVO<Integer, String> getRating(
            Collection<ActionVO<Integer, String>> actionsForTenantUserItemTriple,
            HashMap<String, Double> actionMapping, String aggregateType) {
        Iterator<ActionVO<Integer, String>> actionsIterator = actionsForTenantUserItemTriple
                .iterator();
        ActionVO<Integer, String> action = null;

        // check if collection is empty
        if (actionsForTenantUserItemTriple.size() == 0) {
            if (logger.isDebugEnabled()) {
                logger.debug("could not create TypedRatingVO, the collection of Actions was empty");
            }
            return null;
        }

        if (TypeMappingService.AGGREGATE_TYPE_AVERAGE.equals(aggregateType)) {
            Double sumCurrentRatingValues = new Double(0);
            int countCurrentActions = 0;
            for (ActionVO<Integer, String> currentAction : actionsForTenantUserItemTriple) {
                // do mapping & accumulate ratings
                sumCurrentRatingValues += actionMapping.get(currentAction.getActionType()).floatValue();
                countCurrentActions++;
                action = currentAction;
            }
            return new RatingVO<Integer, String>(action.getItem(),
                    ((double) (sumCurrentRatingValues / (double) countCurrentActions)),
                    actionsForTenantUserItemTriple.size(), action.getActionTime(), action.getUser());
        } else if (TypeMappingService.AGGREGATE_TYPE_FIRST.equals(aggregateType)) {
            // simply return the first action found
            if (actionsIterator.hasNext()) {
                action = actionsIterator.next();
                return new RatingVO<Integer, String>(action.getItem(),
                        actionMapping.get(action.getActionType()), actionsForTenantUserItemTriple.size(),
                        action.getActionTime(), action.getUser());
            }
        } else if (TypeMappingService.AGGREGATE_TYPE_MAXIMUM.equals(aggregateType)) {
            Double maxRatingValue = Double.MAX_VALUE * (-1);
            Double currentRatingValue;
            for (ActionVO<Integer, String> currentAction : actionsForTenantUserItemTriple) {
                currentRatingValue = actionMapping.get(currentAction.getActionType());
                if (currentRatingValue > maxRatingValue) {
                    maxRatingValue = currentRatingValue;
                    action = currentAction;
                }
            }
            return new RatingVO<Integer, String>(action.getItem(),
                    actionMapping.get(action.getActionType()), actionsForTenantUserItemTriple.size(),
                    action.getActionTime(), action.getUser());
        } else if (TypeMappingService.AGGREGATE_TYPE_MOST_FREQUENT.equals(aggregateType)) {
            // count different actionTypes
            HashMap<String, Integer> actionCounter = new HashMap<String, Integer>();
            for (ActionVO<Integer, String> currentAction : actionsForTenantUserItemTriple) {
                Integer currentCounter = actionCounter.get(currentAction.getActionType());
                if (currentCounter != null) {
                    currentCounter++;
                } else {
                    currentCounter = 1;
                }
                actionCounter.put(currentAction.getActionType(), currentCounter);
            }

            // select most frequent ActionType
            Integer maxCounter = Integer.MAX_VALUE * (-1);
            String actionType = null;
            for (String currentAction : actionCounter.keySet()) {
                if (actionCounter.get(currentAction) > maxCounter) {
                    maxCounter = actionCounter.get(currentAction);
                    actionType = currentAction;
                }
            }

            // return first action with corresponding ActionType
            for (ActionVO<Integer, String> currentAction : actionsForTenantUserItemTriple) {
                if (currentAction.getActionType() == actionType) {
                    return new RatingVO<Integer, String>(currentAction.getItem(),
                            actionMapping.get(currentAction.getActionType()), actionsForTenantUserItemTriple.size(),
                            action.getActionTime(), currentAction.getUser());
                }
            }
        } else if (TypeMappingService.AGGREGATE_TYPE_NEWEST.equals(aggregateType)) {
            // initialize with oldest possible date
            Date newestActionTime = new Date(0);
            for (ActionVO<Integer, String> currentAction : actionsForTenantUserItemTriple) {
                if (currentAction.getActionTime().after(newestActionTime)) {
                    newestActionTime = currentAction.getActionTime();
                    action = currentAction;
                }
            }
            return new RatingVO<Integer, String>(action.getItem(),
                    actionMapping.get(action.getActionType()), actionsForTenantUserItemTriple.size(),
                    action.getActionTime(), action.getUser());
        } else if (TypeMappingService.AGGREGATE_TYPE_OLDEST.equals(aggregateType)) {
            // initialize with now()
            Date oldestActionTime = new Date(System.currentTimeMillis());
            for (ActionVO<Integer, String> currentAction : actionsForTenantUserItemTriple) {
                if (currentAction.getActionTime().before(oldestActionTime)) {
                    oldestActionTime = currentAction.getActionTime();
                    action = currentAction;
                }
            }
            return new RatingVO<Integer, String>(action.getItem(),
                    actionMapping.get(action.getActionType()), actionsForTenantUserItemTriple.size(),
                    action.getActionTime(), action.getUser());
        }
        return null;
    }
}
