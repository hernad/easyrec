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
package org.easyrec.service.core.impl;

import org.easyrec.model.core.RecommendationVO;
import org.easyrec.model.core.RecommendedItemVO;
import org.easyrec.model.core.transfer.TimeConstraintVO;
import org.easyrec.service.core.RecommendationHistoryService;
import org.easyrec.store.dao.core.RecommendationDAO;
import org.easyrec.store.dao.core.RecommendedItemDAO;

import java.util.Iterator;
import java.util.List;

/**
 * Implementation of the {@link org.easyrec.service.core.RecommendationHistoryService} interface.
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
public class RecommendationHistoryServiceImpl implements RecommendationHistoryService {
    private RecommendationDAO recommendationDAO;
    private RecommendedItemDAO recommendedItemDAO;

    public RecommendationHistoryServiceImpl(RecommendationDAO recommendationDAO,
                                            RecommendedItemDAO recommendedItemDAO) {
        this.recommendationDAO = recommendationDAO;
        this.recommendedItemDAO = recommendedItemDAO;
    }

    // interface 'RecommendedHistoryService' implementation
    public int insertRecommendation(
            RecommendationVO<Integer, Integer> recommendation) {
        return recommendationDAO.insertRecommendation(recommendation);
    }

    public RecommendationVO<Integer, Integer> loadRecommendation(
            Integer recommendationId) {
        return recommendationDAO.loadRecommendation(recommendationId);
    }

    public Iterator<RecommendationVO<Integer, Integer>> getRecommendationIterator(
            int bulkSize) {
        return recommendationDAO.getRecommendationIterator(bulkSize);
    }

    public Iterator<RecommendationVO<Integer, Integer>> getRecommendationIterator(
            int bulkSize, TimeConstraintVO timeConstraints) {
        return recommendationDAO.getRecommendationIterator(bulkSize, timeConstraints);
    }

    public Iterator<RecommendedItemVO<Integer, Integer>> getRecommendedItemIterator(int bulkSize) {
        return recommendedItemDAO.getRecommendedItemIterator(bulkSize);
    }

    public List<RecommendedItemVO<Integer, Integer>> getRecommendedItems(TimeConstraintVO timeConstraints) {
        return recommendedItemDAO.getRecommendedItems(timeConstraints);
    }

    public List<RecommendedItemVO<Integer, Integer>> getRecommendedItemsOfRecommendation(
            Integer recommendationId) {
        return recommendedItemDAO.getRecommendedItemsOfRecommendation(recommendationId);
    }
}
