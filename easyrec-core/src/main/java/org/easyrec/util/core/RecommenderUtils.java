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
package org.easyrec.util.core;

import org.easyrec.model.core.ItemVO;
import org.easyrec.model.core.RecommendedItemVO;

import java.math.BigDecimal;
import java.util.*;

/**
 * This class provides utility methods for the recommender service (eg: filterDuplicates, ...).
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
public class RecommenderUtils {

    /**
     * Creates a new list of RecommendedItemVO containing all items in the specified list in the same order but
     * without duplicates. If useAveragePredictionValues == true, the predictionValues of duplicates are averaged,
     * otherwise the first RecommendedItemVO is copied to the resulting list.
     *
     * @param recommendedItems
     * @param useAveragePredictionValues
     */
    public static List<RecommendedItemVO<Integer, Integer>> filterDuplicates(
            List<RecommendedItemVO<Integer, Integer>> recommendedItems, boolean useAveragePredictionValues) {
        // skip of recommendedItems is null or empty
        if (recommendedItems == null || recommendedItems.size() == 0) {
            return null;
        }

        List<RecommendedItemVO<Integer, Integer>> filtered = new ArrayList<RecommendedItemVO<Integer, Integer>>(recommendedItems.size());

        if (useAveragePredictionValues) {
            //1. generate a map [item -> (double,int)] containing the cumulative recommendation value and the number of recommendations
            //2. generate a temporary list of recommendedItems that doesn't contain duplicates
            //3. generate the final result by cloning each recommended item from the temporary list and replacing the recommendation value by the average

            //here are the map and the temporary list
            Map<ItemVO, Tuple<Double, Integer>> predictionValuesPerItem = new HashMap<ItemVO, Tuple<Double, Integer>>(recommendedItems.size());
            List<RecommendedItemVO<Integer, Integer>> tmpRecs = new LinkedList<RecommendedItemVO<Integer, Integer>>();

            //build both
            Iterator<RecommendedItemVO<Integer, Integer>> it = recommendedItems.iterator();
            while (it.hasNext()) {
                RecommendedItemVO<Integer, Integer> recItem = it.next();
                Tuple<Double, Integer> predictionValues = predictionValuesPerItem.get(recItem.getItem());
                if (predictionValues == null) {
                    predictionValues = new Tuple<Double, Integer>(0.0, 0);
                    tmpRecs.add(recItem);
                }
                predictionValues.set_1(predictionValues.get_1() + recItem.getPredictionValue());
                predictionValues.set_2(predictionValues.get_2() + 1);
                predictionValuesPerItem.put(recItem.getItem(), predictionValues);
            }
            //walk over temporary list, generating the final result
            for (RecommendedItemVO<Integer, Integer> recItem : tmpRecs) {
                Tuple<Double, Integer> cumulativePredictionValues = predictionValuesPerItem.get(recItem.getItem());
                if (cumulativePredictionValues.get_2().equals(1)) {
                    filtered.add(recItem);
                } else {
                    RecommendedItemVO<Integer, Integer> aggregatedRecItem = new RecommendedItemVO<Integer, Integer>(
                            recItem.getId(),
                            recItem.getItem(),
                            round(cumulativePredictionValues.get_1() / (double) cumulativePredictionValues.get_2(), 16),
                            recItem.getRecommendationId(),
                            recItem.getItemAssocId(),
                            recItem.getExplanation()
                    );
                    filtered.add(aggregatedRecItem);
                }
            }
            return filtered;
        } else {
            Set<ItemVO> itemsAlreadySeen = new HashSet<ItemVO>(recommendedItems.size());
            Iterator<RecommendedItemVO<Integer, Integer>> it = recommendedItems.iterator();
            while (it.hasNext()) {
                RecommendedItemVO<Integer, Integer> recItem = it.next();
                if (itemsAlreadySeen.contains(recItem.getItem())) continue;
                itemsAlreadySeen.add(recItem.getItem());
                filtered.add(recItem);
            }
            return filtered;
        }
    }

    /**
     * Modifies the specified list of RecommendedItem by removing all which reference items contained in
     * the list itemsActedOn.
     *
     * @param recommendedItems
     * @param itemsActedOn
     */
    public static void filterAlreadyActedOn(List<RecommendedItemVO<Integer, Integer>> recommendedItems,
                                            List<ItemVO<Integer, Integer>> itemsActedOn) {
        // skip of recommendedItems is null or empty
        if (recommendedItems == null || recommendedItems.size() == 0 || itemsActedOn == null || itemsActedOn.size() == 0) {
            return;
        }

        Iterator<RecommendedItemVO<Integer, Integer>> recommendedItemsIterator = recommendedItems.iterator();
        RecommendedItemVO<Integer, Integer> currentRecommendedItem;

        Set<ItemVO<Integer, Integer>> items = new HashSet<ItemVO<Integer, Integer>>(itemsActedOn);

        while (recommendedItemsIterator.hasNext()) {
            currentRecommendedItem = recommendedItemsIterator.next();
            if (items.contains(currentRecommendedItem.getItem())) {
                recommendedItemsIterator.remove();
            }
        }
    }

    private static double round(double d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Double.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }

    private static class Tuple<T1, T2> {
        private T1 _1;
        private T2 _2;

        public T1 get_1() {
            return _1;
        }

        public void set_1(T1 _1) {
            this._1 = _1;
        }

        public T2 get_2() {
            return _2;
        }

        public void set_2(T2 _2) {
            this._2 = _2;
        }

        private Tuple(T1 _1, T2 _2) {
            this._1 = _1;
            this._2 = _2;
        }
    }

}
