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

package org.easyrec.plugin.slopeone.impl;

import org.easyrec.model.core.RatingVO;
import org.easyrec.plugin.slopeone.DeviationCalculationStrategy;
import org.easyrec.plugin.slopeone.model.Deviation;
import org.easyrec.plugin.slopeone.model.DeviationCalculationResult;
import org.easyrec.plugin.slopeone.store.dao.DeviationDAO;

import java.util.Date;
import java.util.List;

/**
 * Calls the specified child strategy to get the deviations of a single user and then takes the generated deviations
 * writes them to the specified DAO. The DAO must take care of merging old and new values.
 * <p/>
 * <p><b>Company:&nbsp;</b> SAT, Research Studios Austria</p>
 * <p><b>Copyright:&nbsp;</b> (c) 2007</p>
 * <p><b>last modified:</b><br/> $Author$<br/> $Date$<br/> $Revision$</p>
 *
 * @author Patrick Marschik
 */
public class UnmergedDaoProxyDeviationCalculationStrategy implements DeviationCalculationStrategy {
    private DeviationCalculationStrategy childStrategy;
    private DeviationDAO deviationDAO;

    public UnmergedDaoProxyDeviationCalculationStrategy(DeviationCalculationStrategy childStrategy,
                                                        DeviationDAO deviationDAO) {
        this.childStrategy = childStrategy;
        this.deviationDAO = deviationDAO;
    }

    public DeviationCalculationResult calculate(int userId, List<RatingVO<Integer, Integer>> ratings,
                                                Date oldRatingsBeforeThisDate) {
        if ((ratings == null) || ratings.isEmpty())
            throw new IllegalArgumentException("Can't calculate deviations for null or empyty ratings list.");

        int tenantId = ratings.get(0).getItem().getTenant();
        int itemTypeId = ratings.get(0).getItem().getType();

        DeviationCalculationResult result = childStrategy.calculate(userId, ratings, oldRatingsBeforeThisDate);
        List<Deviation> deviations = result.getDeviations();

        deviationDAO.insertDeviations(deviations);

        return new DeviationCalculationResult(deviations, deviations.size(), deviations.size());
    }
}
