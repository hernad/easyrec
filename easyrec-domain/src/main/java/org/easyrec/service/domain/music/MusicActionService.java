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

import org.easyrec.model.core.RankedItemVO;
import org.easyrec.model.core.RatingVO;
import org.easyrec.model.core.transfer.TimeConstraintVO;
import org.easyrec.service.domain.DomainActionService;

import java.util.List;

/**
 * This interface defines an <code>ActionService</code> for the music domain.
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
public interface MusicActionService extends DomainActionService {
    // actions
    public void purchaseTrack(Integer tenant, Integer user, String sessionId, String ip, Integer track,
                              String description);

    public void viewArtist(Integer tenant, Integer user, String sessionId, String ip, Integer artist,
                           String description);

    public void viewGenre(Integer tenant, Integer user, String sessionId, String ip, Integer genre, String description);

    public void viewTrack(Integer tenant, Integer user, String sessionId, String ip, Integer track, String description);

    public void rateArtist(Integer tenant, Integer user, String sessionId, String ip, Integer artist,
                           Integer ratingValue, String description);

    public void rateTrack(Integer tenant, Integer user, String sessionId, String ip, Integer track, Integer ratingValue,
                          String description);

    public void searchArtist(Integer tenant, Integer user, String sessionId, String ip, Integer artist,
                             Boolean searchSucceeded, Integer numberOfFoundArtists, String description);

    public void searchTrack(Integer tenant, Integer user, String sessionId, String ip, Integer track,
                            Boolean searchSucceeded, Integer numberOfFoundTracks, String description);

    public void previewTrack(Integer tenant, Integer user, String sessionId, String ip, Integer track,
                             String description);

    public void addTrackToPlaylist(Integer tenant, Integer user, String sessionId, String ip, Integer track,
                                   String description);

    // rankings
    public List<RankedItemVO<Integer, String>> mostBoughtTracks(Integer tenant,
                                                                                 Integer numberOfResults,
                                                                                 TimeConstraintVO timeRange,
                                                                                 Boolean sortDescending);

    public List<RankedItemVO<Integer, String>> mostViewedArtists(Integer tenant,
                                                                                  Integer numberOfResults,
                                                                                  TimeConstraintVO timeRange,
                                                                                  Boolean sortDescending);

    public List<RankedItemVO<Integer, String>> mostViewedGenres(Integer tenant,
                                                                                 Integer numberOfResults,
                                                                                 TimeConstraintVO timeRange,
                                                                                 Boolean sortDescending);

    public List<RankedItemVO<Integer, String>> mostViewedTracks(Integer tenant,
                                                                                 Integer numberOfResults,
                                                                                 TimeConstraintVO timeRange,
                                                                                 Boolean sortDescending);

    public List<RankedItemVO<Integer, String>> mostRatedArtists(Integer tenant,
                                                                                 Integer numberOfResults,
                                                                                 TimeConstraintVO timeRange,
                                                                                 Boolean sortDescending);

    public List<RankedItemVO<Integer, String>> mostRatedTracks(Integer tenant, Integer numberOfResults,
                                                                                TimeConstraintVO timeRange,
                                                                                Boolean sortDescending);

    public List<RankedItemVO<Integer, String>> mostSearchedArtists(Integer tenant,
                                                                                    Integer numberOfResults,
                                                                                    TimeConstraintVO timeRange,
                                                                                    Boolean sortDescending);

    public List<RankedItemVO<Integer, String>> mostSearchedTracks(Integer tenant,
                                                                                   Integer numberOfResults,
                                                                                   TimeConstraintVO timeRange,
                                                                                   Boolean sortDescending);

    public List<RankedItemVO<Integer, String>> mostPreviewedTracks(Integer tenant,
                                                                                    Integer numberOfResults,
                                                                                    TimeConstraintVO timeRange,
                                                                                    Boolean sortDescending);

    public List<RankedItemVO<Integer, String>> mostAddedToPlaylistTracks(Integer tenant,
                                                                                          Integer numberOfResults,
                                                                                          TimeConstraintVO timeRange,
                                                                                          Boolean sortDescending);

    // ratings
    public List<RatingVO<Integer, String>> artistRatings(Integer tenant, Integer user,
                                                                           String sessionId, Integer numberOfResults,
                                                                           TimeConstraintVO timeRange);

    public List<RatingVO<Integer, String>> badArtistRatings(Integer tenant, Integer user,
                                                                              String sessionId, Integer numberOfResults,
                                                                              TimeConstraintVO timeRange);

    public List<RatingVO<Integer, String>> goodArtistRatings(Integer tenant, Integer user,
                                                                               String sessionId,
                                                                               Integer numberOfResults,
                                                                               TimeConstraintVO timeRange);

    public List<RatingVO<Integer, String>> lastGoodArtistRatings(Integer tenant, Integer user,
                                                                                   String sessionId,
                                                                                   Integer numberOfResults);

    public List<RatingVO<Integer, String>> trackRatings(Integer tenant, Integer user,
                                                                          String sessionId, Integer numberOfResults,
                                                                          TimeConstraintVO timeRange);

    public List<RatingVO<Integer, String>> badTrackRatings(Integer tenant, Integer user,
                                                                             String sessionId, Integer numberOfResults,
                                                                             TimeConstraintVO timeRange);

    public List<RatingVO<Integer, String>> goodTrackRatings(Integer tenant, Integer user,
                                                                              String sessionId, Integer numberOfResults,
                                                                              TimeConstraintVO timeRange);

    public List<RatingVO<Integer, String>> lastGoodTrackRatings(Integer tenant, Integer user,
                                                                                  String sessionId,
                                                                                  Integer numberOfResults);
}
