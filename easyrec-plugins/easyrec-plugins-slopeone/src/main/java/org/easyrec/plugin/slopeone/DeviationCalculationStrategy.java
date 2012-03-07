/*
 * Copyright 2010 Research Studios Austria Forschungsgesellschaft mBH
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

package org.easyrec.plugin.slopeone;

import org.easyrec.model.core.RatingVO;
import org.easyrec.plugin.slopeone.model.DeviationCalculationResult;

import java.util.Date;
import java.util.List;


/**
 * Interface defining methods needed for calculating the (average) deviations used in Slope One. <p/>
 * <p><b>Company:&nbsp;</b> SAT, Research Studios Austria</p> <p/> <p><b>Copyright:&nbsp;</b> (c) 2007</p> <p/>
 * <p><b>last modified:</b><br/> $Author: dmann $<br/> $Date: 2011-12-20 15:22:22 +0100 (Di, 20 Dez 2011) $<br/> $Revision: 18685 $</p>
 *
 * @author Patrick Marschik
 */
public interface DeviationCalculationStrategy {
    // -------------------------- OTHER METHODS --------------------------

    /**
     * Calculates all possible deviations for a user.
     * <p/>
     * Deviations are calculated by using ratings x ratings, if both ratings are before {@code oldRatingsBeforeThisDate}
     * the deviation is't calculated since this would duplicate eralier calculations.
     *
     * @param userId                   User the deviations are calculated for.
     * @param ratings                  All ratings of that user.
     * @param oldRatingsBeforeThisDate If ratings from the {@code ratings} List are both older than this date the
     *                                 deviation will not be calculated.
     * @return Deviations calculated for this user.
     */
    DeviationCalculationResult calculate(int userId, List<RatingVO<Integer, Integer>> ratings,
                                         Date oldRatingsBeforeThisDate);
}
