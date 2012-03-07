/*
 * Copyright 2011 Research Studios Austria Forschungsgesellschaft mBH
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

package org.easyrec.plugin.slopeone.impl;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import org.easyrec.model.core.RatingVO;
import org.easyrec.plugin.slopeone.DeviationCalculationStrategy;
import org.easyrec.plugin.slopeone.model.Deviation;
import org.easyrec.plugin.slopeone.model.DeviationCalculationResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Calculates the deviations of a single user in-memory.<p><b>Company:&nbsp;</b> SAT, Research Studios Austria</p>
 * <p><b>Copyright:&nbsp;</b> (c) 2007</p> <p><b>last modified:</b><br/> $Author: dmann $<br/> $Date: 2011-12-20 15:22:22 +0100 (Di, 20 Dez 2011) $<br/> $Revision: 18685 $</p>
 *
 * @author Patrick Marschik
 */
public class DefaultDeviationCalculationStrategy implements DeviationCalculationStrategy {
    public DeviationCalculationResult calculate(int userId, List<RatingVO<Integer, Integer>> ratings,
                                                Date oldRatingsBeforeThisDate) {
        if ((ratings == null) || ratings.isEmpty())
            throw new IllegalArgumentException("Cannot calculate for empty/null rating list.");

        int approximateSize = (ratings.size() * (ratings.size() - 1)) / 2;

        final TLongObjectMap<Deviation> deviations = new TLongObjectHashMap<Deviation>(approximateSize);

        for (int i = 0; i < ratings.size(); i++) {
            RatingVO<Integer, Integer> rating1 = ratings.get(i);
            boolean rating1IsOld = rating1.getLastActionTime().before(oldRatingsBeforeThisDate);

            for (int j = i + 1; j < ratings.size(); j++) {
                RatingVO<Integer, Integer> rating2 = ratings.get(j);

                // if both ratings happened before the specified date threshold value we are not to
                // calculate and store the deviation.
                if (rating1IsOld && rating2.getLastActionTime().before(oldRatingsBeforeThisDate))
                    continue;

                int item1 = rating1.getItem().getItem();
                int item2 = rating2.getItem().getItem();
                int item1TypeId = rating1.getItem().getType();
                int item2TypeId = rating2.getItem().getType();
                boolean swapped = false;

                // swap item order because we only calculate deviation tuples (a,b) where a < b.
                if (item1 > item2) {
                    int itemTmp = item1;
                    item1 = item2;
                    item2 = itemTmp;
                    swapped = true;
                }

                long hashOfItems = Deviation.hashCodeOfItems(item1, item1TypeId, item2, item2TypeId);
                Deviation deviation = deviations.get(hashOfItems);

                if (deviation == null) {
                    deviation = new Deviation(rating1.getItem().getTenant(), item1, item1TypeId, item2, item2TypeId,
                            0.0, 0L);
                    deviations.put(hashOfItems, deviation);
                }

                deviation.setDenominator(deviation.getDenominator() + 1);

                double diff;

                diff = swapped ? rating1.getRatingValue() + rating2.getRatingValue()
                               : rating1.getRatingValue() - rating2.getRatingValue();

                deviation.setNumerator(deviation.getNumerator() + diff);
            }
        }

        return new DeviationCalculationResult(new ArrayList<Deviation>(deviations.valueCollection()), deviations.size(),
                0);
    }
}
