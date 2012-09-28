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
package org.easyrec.service.domain.music;

import org.easyrec.model.core.ItemVO;
import org.easyrec.model.core.RecommendationVO;
import org.easyrec.service.domain.DomainRecommenderService;

/**
 * This interface defines a <code>Recommender</code> for the music domain.
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

public interface MusicRecommenderService extends DomainRecommenderService {
    public RecommendationVO<Integer, String> tracksBasedOnPurchaseHistory(
            Integer tenant, Integer user, String sessionId, Integer numberOfLastActionsConsidered, String assocType);

    public RecommendationVO<Integer, String> artistsBasedOnViewingHistory(
            Integer tenant, Integer user, String sessionId, String consideredItemType,
            Integer numberOfLastActionsConsidered, String assocType);

    public RecommendationVO<Integer, String> genresBasedOnViewingHistory(
            Integer tenant, Integer user, String sessionId, String consideredItemType,
            Integer numberOfLastActionsConsidered, String assocType);

    public RecommendationVO<Integer, String> tracksBasedOnViewingHistory(
            Integer tenant, Integer user, String sessionId, String consideredItemType,
            Integer numberOfLastActionsConsidered, String assocType);

    public RecommendationVO<Integer, String> artistsBasedOnSearchingHistory(
            Integer tenant, Integer user, String sessionId, String consideredItemType,
            Integer numberOfLastActionsConsidered, String assocType);

    public RecommendationVO<Integer, String> tracksBasedOnSearchingHistory(
            Integer tenant, Integer user, String sessionId, String consideredItemType,
            Integer numberOfLastActionsConsidered, String assocType);

    public RecommendationVO<Integer, String> alsoBoughtTracks(Integer tenant,
                                                                                                Integer user,
                                                                                                String sessionId,
                                                                                                ItemVO<Integer, String> track);

    public RecommendationVO<Integer, String> alsoViewedArtists(Integer tenant,
                                                                                                 Integer user,
                                                                                                 String sessionId,
                                                                                                 ItemVO<Integer, String> item);

    public RecommendationVO<Integer, String> alsoViewedGenres(Integer tenant,
                                                                                                Integer user,
                                                                                                String sessionId,
                                                                                                ItemVO<Integer, String> item);

    public RecommendationVO<Integer, String> alsoViewedTracks(Integer tenant,
                                                                                                Integer user,
                                                                                                String sessionId,
                                                                                                ItemVO<Integer, String> item);

    public RecommendationVO<Integer, String> alsoSearchedArtists(Integer tenant,
                                                                                                   Integer user,
                                                                                                   String sessionId,
                                                                                                   ItemVO<Integer, String> item);

    public RecommendationVO<Integer, String> alsoSearchedTracks(Integer tenant,
                                                                                                  Integer user,
                                                                                                  String sessionId,
                                                                                                  ItemVO<Integer, String> item);
}
