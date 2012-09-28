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
package org.easyrec.service.domain.music.impl;

import org.easyrec.model.core.ActionVO;
import org.easyrec.model.core.ItemVO;
import org.easyrec.model.core.RankedItemVO;
import org.easyrec.model.core.RatingVO;
import org.easyrec.model.core.transfer.TimeConstraintVO;
import org.easyrec.service.core.ActionService;
import org.easyrec.service.domain.TypeMappingService;
import org.easyrec.service.domain.impl.DomainActionServiceImpl;
import org.easyrec.service.domain.music.MusicActionService;
import org.easyrec.store.dao.domain.TypedActionDAO;

import java.util.List;

/**
 * Implementation of the {@link org.easyrec.service.domain.music.MusicActionService} interface.
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
public class MusicActionServiceImpl extends DomainActionServiceImpl implements MusicActionService {
    // constructor
    public MusicActionServiceImpl(ActionService actionService, TypedActionDAO typedActionDAO,
                                  TypeMappingService typeMappingService) {
        super(actionService, typedActionDAO, typeMappingService);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // interface "MusicActionService" implementation

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Actions
    ///////////////////////////////////////////////////////////////////////////////////////////////   
    public void purchaseTrack(Integer tenant, Integer user, String sessionId, String ip, Integer trackId,
                              String description) {
        purchaseItem(tenant, user, sessionId, ip,
                new ItemVO<Integer, String>(tenant, trackId, TypeMappingService.ITEM_TYPE_TRACK), description);
    }

    public void viewArtist(Integer tenant, Integer user, String sessionId, String ip, Integer artistId,
                           String description) {
        viewItem(tenant, user, sessionId, ip,
                new ItemVO<Integer, String>(tenant, artistId, TypeMappingService.ITEM_TYPE_ARTIST),
                description);
    }

    public void viewGenre(Integer tenant, Integer user, String sessionId, String ip, Integer genreId,
                          String description) {
        viewItem(tenant, user, sessionId, ip,
                new ItemVO<Integer, String>(tenant, genreId, TypeMappingService.ITEM_TYPE_GENRE_CLUSTER),
                description);
    }

    public void viewTrack(Integer tenant, Integer user, String sessionId, String ip, Integer trackId,
                          String description) {
        viewItem(tenant, user, sessionId, ip,
                new ItemVO<Integer, String>(tenant, trackId, TypeMappingService.ITEM_TYPE_TRACK), description);
    }

    public void rateArtist(Integer tenant, Integer user, String sessionId, String ip, Integer artistId,
                           Integer ratingValue, String description) {
        rateItem(tenant, user, sessionId, ip,
                new ItemVO<Integer, String>(tenant, artistId, TypeMappingService.ITEM_TYPE_ARTIST),
                ratingValue, description);
    }

    public void rateTrack(Integer tenant, Integer user, String sessionId, String ip, Integer trackId,
                          Integer ratingValue, String description) {
        rateItem(tenant, user, sessionId, ip,
                new ItemVO<Integer, String>(tenant, trackId, TypeMappingService.ITEM_TYPE_TRACK), ratingValue,
                description);
    }

    public void searchArtist(Integer tenant, Integer user, String sessionId, String ip, Integer artistId,
                             Boolean searchSucceeded, Integer numberOfFoundArtists, String description) {
        searchItem(tenant, user, sessionId, ip,
                new ItemVO<Integer, String>(tenant, artistId, TypeMappingService.ITEM_TYPE_ARTIST),
                searchSucceeded, numberOfFoundArtists, description);
    }

    public void searchTrack(Integer tenant, Integer user, String sessionId, String ip, Integer trackId,
                            Boolean searchSucceeded, Integer numberOfFoundTracks, String description) {
        searchItem(tenant, user, sessionId, ip,
                new ItemVO<Integer, String>(tenant, trackId, TypeMappingService.ITEM_TYPE_TRACK),
                searchSucceeded, numberOfFoundTracks, description);
    }

    public void previewTrack(Integer tenant, Integer user, String sessionId, String ip, Integer trackId,
                             String description) {
        insertAction(new ActionVO<Integer, String>(tenant, user, sessionId, ip,
                new ItemVO<Integer, String>(tenant, trackId, TypeMappingService.ITEM_TYPE_TRACK),
                TypeMappingService.ACTION_TYPE_PREVIEW, null, null, null, description));
    }

    public void addTrackToPlaylist(Integer tenant, Integer user, String sessionId, String ip, Integer trackId,
                                   String description) {
        insertAction(new ActionVO<Integer, String>(tenant, user, sessionId, ip,
                new ItemVO<Integer, String>(tenant, trackId, TypeMappingService.ITEM_TYPE_TRACK),
                TypeMappingService.ACTION_TYPE_ADD_TO_PLAYLIST, null, null, null, description));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Rankings
    ///////////////////////////////////////////////////////////////////////////////////////////////       
    public List<RankedItemVO<Integer, String>> mostBoughtTracks(Integer tenant,
                                                                                 Integer numberOfResults,
                                                                                 TimeConstraintVO timeRange,
                                                                                 Boolean sortDescending) {
        return mostBoughtItems(tenant, TypeMappingService.ITEM_TYPE_TRACK, numberOfResults, timeRange, sortDescending);
    }

    public List<RankedItemVO<Integer, String>> mostViewedArtists(Integer tenant,
                                                                                  Integer numberOfResults,
                                                                                  TimeConstraintVO timeRange,
                                                                                  Boolean sortDescending) {
        return mostViewedItems(tenant, TypeMappingService.ITEM_TYPE_ARTIST, numberOfResults, timeRange, sortDescending);
    }

    public List<RankedItemVO<Integer, String>> mostViewedGenres(Integer tenant,
                                                                                 Integer numberOfResults,
                                                                                 TimeConstraintVO timeRange,
                                                                                 Boolean sortDescending) {
        return mostViewedItems(tenant, TypeMappingService.ITEM_TYPE_GENRE_CLUSTER, numberOfResults, timeRange,
                sortDescending);
    }

    public List<RankedItemVO<Integer, String>> mostViewedTracks(Integer tenant,
                                                                                 Integer numberOfResults,
                                                                                 TimeConstraintVO timeRange,
                                                                                 Boolean sortDescending) {
        return mostViewedItems(tenant, TypeMappingService.ITEM_TYPE_TRACK, numberOfResults, timeRange, sortDescending);
    }

    public List<RankedItemVO<Integer, String>> mostRatedArtists(Integer tenant,
                                                                                 Integer numberOfResults,
                                                                                 TimeConstraintVO timeRange,
                                                                                 Boolean sortDescending) {
        return mostRatedItems(tenant, TypeMappingService.ITEM_TYPE_ARTIST, numberOfResults, timeRange, sortDescending);
    }

    public List<RankedItemVO<Integer, String>> mostRatedTracks(Integer tenant, Integer numberOfResults,
                                                                                TimeConstraintVO timeRange,
                                                                                Boolean sortDescending) {
        return mostRatedItems(tenant, TypeMappingService.ITEM_TYPE_TRACK, numberOfResults, timeRange, sortDescending);
    }

    public List<RankedItemVO<Integer, String>> mostSearchedArtists(Integer tenant,
                                                                                    Integer numberOfResults,
                                                                                    TimeConstraintVO timeRange,
                                                                                    Boolean sortDescending) {
        return mostSearchedItems(tenant, TypeMappingService.ITEM_TYPE_ARTIST, numberOfResults, timeRange,
                sortDescending);
    }

    public List<RankedItemVO<Integer, String>> mostSearchedTracks(Integer tenant,
                                                                                   Integer numberOfResults,
                                                                                   TimeConstraintVO timeRange,
                                                                                   Boolean sortDescending) {
        return mostSearchedItems(tenant, TypeMappingService.ITEM_TYPE_TRACK, numberOfResults, timeRange,
                sortDescending);
    }

    public List<RankedItemVO<Integer, String>> mostPreviewedTracks(Integer tenant,
                                                                                    Integer numberOfResults,
                                                                                    TimeConstraintVO timeRange,
                                                                                    Boolean sortDescending) {
        return typedActionDAO.getRankedItemsByActionType(tenant, TypeMappingService.ACTION_TYPE_PREVIEW,
                TypeMappingService.ITEM_TYPE_TRACK, numberOfResults, timeRange, sortDescending);
    }

    public List<RankedItemVO<Integer, String>> mostAddedToPlaylistTracks(Integer tenant,
                                                                                          Integer numberOfResults,
                                                                                          TimeConstraintVO timeRange,
                                                                                          Boolean sortDescending) {
        return typedActionDAO.getRankedItemsByActionType(tenant, TypeMappingService.ACTION_TYPE_ADD_TO_PLAYLIST,
                TypeMappingService.ITEM_TYPE_TRACK, numberOfResults, timeRange, sortDescending);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Ratings
    ///////////////////////////////////////////////////////////////////////////////////////////////       
    public List<RatingVO<Integer, String>> artistRatings(Integer tenant, Integer user,
                                                                           String sessionId, Integer numberOfResults,
                                                                           TimeConstraintVO timeRange) {
        return itemRatings(tenant, user, sessionId, TypeMappingService.ITEM_TYPE_ARTIST, numberOfResults, timeRange);
    }

    public List<RatingVO<Integer, String>> badArtistRatings(Integer tenant, Integer user,
                                                                              String sessionId, Integer numberOfResults,
                                                                              TimeConstraintVO timeRange) {
        return badItemRatings(tenant, user, sessionId, TypeMappingService.ITEM_TYPE_ARTIST, numberOfResults, timeRange);
    }

    public List<RatingVO<Integer, String>> goodArtistRatings(Integer tenant, Integer user,
                                                                               String sessionId,
                                                                               Integer numberOfResults,
                                                                               TimeConstraintVO timeRange) {
        return goodItemRatings(tenant, user, sessionId, TypeMappingService.ITEM_TYPE_ARTIST, numberOfResults,
                timeRange);
    }

    public List<RatingVO<Integer, String>> lastGoodArtistRatings(Integer tenant, Integer user,
                                                                                   String sessionId,
                                                                                   Integer numberOfResults) {
        return lastGoodItemRatings(tenant, user, sessionId, TypeMappingService.ITEM_TYPE_ARTIST, numberOfResults);
    }

    public List<RatingVO<Integer, String>> trackRatings(Integer tenant, Integer user,
                                                                          String sessionId, Integer numberOfResults,
                                                                          TimeConstraintVO timeRange) {
        return itemRatings(tenant, user, sessionId, TypeMappingService.ITEM_TYPE_TRACK, numberOfResults, timeRange);
    }

    public List<RatingVO<Integer, String>> badTrackRatings(Integer tenant, Integer user,
                                                                             String sessionId, Integer numberOfResults,
                                                                             TimeConstraintVO timeRange) {
        return badItemRatings(tenant, user, sessionId, TypeMappingService.ITEM_TYPE_TRACK, numberOfResults, timeRange);
    }

    public List<RatingVO<Integer, String>> goodTrackRatings(Integer tenant, Integer user,
                                                                              String sessionId, Integer numberOfResults,
                                                                              TimeConstraintVO timeRange) {
        return goodItemRatings(tenant, user, sessionId, TypeMappingService.ITEM_TYPE_TRACK, numberOfResults, timeRange);
    }

    public List<RatingVO<Integer, String>> lastGoodTrackRatings(Integer tenant, Integer user,
                                                                                  String sessionId,
                                                                                  Integer numberOfResults) {
        return lastGoodItemRatings(tenant, user, sessionId, TypeMappingService.ITEM_TYPE_TRACK, numberOfResults);
    }
}
