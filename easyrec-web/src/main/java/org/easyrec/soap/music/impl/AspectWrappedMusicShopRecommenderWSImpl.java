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
package org.easyrec.soap.music.impl;

import org.easyrec.model.core.ItemVO;
import org.easyrec.model.core.RecommendationVO;
import org.easyrec.model.core.transfer.TimeConstraintVO;
import org.easyrec.model.web.RankedItem;
import org.easyrec.model.web.Rating;
import org.easyrec.model.web.RecommendedItem;
import org.easyrec.service.domain.TypeMappingService;
import org.easyrec.service.domain.music.MusicActionService;
import org.easyrec.service.domain.music.MusicRecommenderService;
import org.easyrec.service.web.IDMappingService;
import org.easyrec.soap.music.MusicShopRecommenderWS;
import org.easyrec.soap.music.exception.MusicShopRecommenderException;
import org.easyrec.utils.spring.exception.annotation.MapThrowableToException;
import org.easyrec.utils.spring.log.annotation.IOLog;
import org.easyrec.utils.spring.profile.annotation.Profiled;
import org.easyrec.utils.spring.store.dao.IDMappingDAO;

import java.util.List;

/**
 * Wrapper to enable aspects on the webservice.
 * Delegate for the Music Recommender Webservice implementation (for the music domain)
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: dmann $<br/>
 * $Date: 2011-12-20 15:22:22 +0100 (Di, 20 Dez 2011) $<br/>
 * $Revision: 18685 $</p>
 *
 * @author Roman Cerny
 */
@MapThrowableToException(exceptionClazz = MusicShopRecommenderException.class)
public class AspectWrappedMusicShopRecommenderWSImpl implements MusicShopRecommenderWS {
    private MusicActionService musicActionService;
    private MusicRecommenderService musicRecommenderService;
    private TypeMappingService typeMappingService;
    private IDMappingDAO idMappingDAO;
    private IDMappingService idMappingService;


    ///////////////////////////////////////////////////////////////////////////////////////////////
    // interface "MusicShopRecommenderWS"   

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Actions
    ///////////////////////////////////////////////////////////////////////////////////////////////
    @IOLog
    @Profiled
    public void purchaseTrack(Integer tenantId, String userId, String sessionId, String ip, String trackId,
                              String description) throws MusicShopRecommenderException {
        musicActionService
                .purchaseTrack(tenantId, idMappingDAO.lookup(userId), sessionId, ip, idMappingDAO.lookup(trackId),
                        description);
    }

    @IOLog
    @Profiled
    public void viewArtist(Integer tenantId, String userId, String sessionId, String ip, String artistId,
                           String description) throws MusicShopRecommenderException {
        musicActionService
                .viewArtist(tenantId, idMappingDAO.lookup(userId), sessionId, ip, idMappingDAO.lookup(artistId),
                        description);
    }

    @IOLog
    @Profiled
    public void viewGenre(Integer tenantId, String userId, String sessionId, String ip, String genreId,
                          String description) throws MusicShopRecommenderException {
        musicActionService.viewGenre(tenantId, idMappingDAO.lookup(userId), sessionId, ip, idMappingDAO.lookup(genreId),
                description);
    }

    @IOLog
    @Profiled
    public void viewTrack(Integer tenantId, String userId, String sessionId, String ip, String trackId,
                          String description) throws MusicShopRecommenderException {
        musicActionService.viewTrack(tenantId, idMappingDAO.lookup(userId), sessionId, ip, idMappingDAO.lookup(trackId),
                description);
    }

    @IOLog
    @Profiled
    public void rateArtist(Integer tenantId, String userId, String sessionId, String ip, String artistId,
                           Integer ratingValue, String description) throws MusicShopRecommenderException {
        musicActionService
                .rateArtist(tenantId, idMappingDAO.lookup(userId), sessionId, ip, idMappingDAO.lookup(artistId),
                        ratingValue, description);
    }

    @IOLog
    @Profiled
    public void rateTrack(Integer tenantId, String userId, String sessionId, String ip, String trackId,
                          Integer ratingValue, String description) throws MusicShopRecommenderException {
        musicActionService.rateTrack(tenantId, idMappingDAO.lookup(userId), sessionId, ip, idMappingDAO.lookup(trackId),
                ratingValue, description);
    }

    @IOLog
    @Profiled
    public void searchArtist(Integer tenantId, String userId, String sessionId, String ip, String artistId,
                             Boolean searchSucceeded, Integer numberOfFoundArtists, String description)
            throws MusicShopRecommenderException {
        musicActionService
                .searchArtist(tenantId, idMappingDAO.lookup(userId), sessionId, ip, idMappingDAO.lookup(artistId),
                        searchSucceeded, numberOfFoundArtists, description);
    }

    @IOLog
    @Profiled
    public void searchTrack(Integer tenantId, String userId, String sessionId, String ip, String trackId,
                            Boolean searchSucceeded, Integer numberOfFoundTracks, String description)
            throws MusicShopRecommenderException {
        musicActionService
                .searchTrack(tenantId, idMappingDAO.lookup(userId), sessionId, ip, idMappingDAO.lookup(trackId),
                        searchSucceeded, numberOfFoundTracks, description);
    }

    @IOLog
    @Profiled
    public void previewTrack(Integer tenantId, String userId, String sessionId, String ip, String trackId,
                             String description) throws MusicShopRecommenderException {
        musicActionService
                .previewTrack(tenantId, idMappingDAO.lookup(userId), sessionId, ip, idMappingDAO.lookup(trackId),
                        description);
    }

    @IOLog
    @Profiled
    public void addTrackToPlaylist(Integer tenantId, String userId, String sessionId, String ip, String trackId,
                                   String description) throws MusicShopRecommenderException {
        musicActionService
                .addTrackToPlaylist(tenantId, idMappingDAO.lookup(userId), sessionId, ip, idMappingDAO.lookup(trackId),
                        description);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Rankings
    ///////////////////////////////////////////////////////////////////////////////////////////////
    @IOLog
    @Profiled
    public RankedItem[] mostBoughtTracks(Integer tenantId, Integer numberOfResults, TimeConstraintVO timeRange,
                                         Boolean sortDescending) throws MusicShopRecommenderException {
        List<RankedItem> rankedItems = idMappingService.convertListOfRankedItemVOs(
                musicActionService.mostBoughtTracks(tenantId, numberOfResults, timeRange, sortDescending));
        if (rankedItems != null && rankedItems.size() > 0) {
            return rankedItems.toArray(new RankedItem[rankedItems.size()]);
        } else {
            return new RankedItem[0];
        }
    }

    @IOLog
    @Profiled
    public RankedItem[] mostViewedArtists(Integer tenantId, Integer numberOfResults, TimeConstraintVO timeRange,
                                          Boolean sortDescending) throws MusicShopRecommenderException {
        List<RankedItem> rankedItems = idMappingService.convertListOfRankedItemVOs(
                musicActionService.mostViewedArtists(tenantId, numberOfResults, timeRange, sortDescending));
        if (rankedItems != null && rankedItems.size() > 0) {
            return rankedItems.toArray(new RankedItem[rankedItems.size()]);
        } else {
            return new RankedItem[0];
        }
    }

    @IOLog
    @Profiled
    public RankedItem[] mostViewedGenres(Integer tenantId, Integer numberOfResults, TimeConstraintVO timeRange,
                                         Boolean sortDescending) throws MusicShopRecommenderException {
        List<RankedItem> rankedItems = idMappingService.convertListOfRankedItemVOs(
                musicActionService.mostViewedGenres(tenantId, numberOfResults, timeRange, sortDescending));
        if (rankedItems != null && rankedItems.size() > 0) {
            return rankedItems.toArray(new RankedItem[rankedItems.size()]);
        } else {
            return new RankedItem[0];
        }
    }

    @IOLog
    @Profiled
    public RankedItem[] mostViewedTracks(Integer tenantId, Integer numberOfResults, TimeConstraintVO timeRange,
                                         Boolean sortDescending) throws MusicShopRecommenderException {
        List<RankedItem> rankedItems = idMappingService.convertListOfRankedItemVOs(
                musicActionService.mostViewedTracks(tenantId, numberOfResults, timeRange, sortDescending));
        if (rankedItems != null && rankedItems.size() > 0) {
            return rankedItems.toArray(new RankedItem[rankedItems.size()]);
        } else {
            return new RankedItem[0];
        }
    }

    @IOLog
    @Profiled
    public RankedItem[] mostRatedArtists(Integer tenantId, Integer numberOfResults, TimeConstraintVO timeRange,
                                         Boolean sortDescending) throws MusicShopRecommenderException {
        List<RankedItem> rankedItems = idMappingService.convertListOfRankedItemVOs(
                musicActionService.mostRatedArtists(tenantId, numberOfResults, timeRange, sortDescending));
        if (rankedItems != null && rankedItems.size() > 0) {
            return rankedItems.toArray(new RankedItem[rankedItems.size()]);
        } else {
            return new RankedItem[0];
        }
    }

    @IOLog
    @Profiled
    public RankedItem[] mostRatedTracks(Integer tenantId, Integer numberOfResults, TimeConstraintVO timeRange,
                                        Boolean sortDescending) throws MusicShopRecommenderException {
        List<RankedItem> rankedItems = idMappingService.convertListOfRankedItemVOs(
                musicActionService.mostRatedTracks(tenantId, numberOfResults, timeRange, sortDescending));
        if (rankedItems != null && rankedItems.size() > 0) {
            return rankedItems.toArray(new RankedItem[rankedItems.size()]);
        } else {
            return new RankedItem[0];
        }
    }

    @IOLog
    @Profiled
    public RankedItem[] mostSearchedArtists(Integer tenantId, Integer numberOfResults, TimeConstraintVO timeRange,
                                            Boolean sortDescending) throws MusicShopRecommenderException {
        List<RankedItem> rankedItems = idMappingService.convertListOfRankedItemVOs(
                musicActionService.mostSearchedArtists(tenantId, numberOfResults, timeRange, sortDescending));
        if (rankedItems != null && rankedItems.size() > 0) {
            return rankedItems.toArray(new RankedItem[rankedItems.size()]);
        } else {
            return new RankedItem[0];
        }
    }

    @IOLog
    @Profiled
    public RankedItem[] mostSearchedTracks(Integer tenantId, Integer numberOfResults, TimeConstraintVO timeRange,
                                           Boolean sortDescending) throws MusicShopRecommenderException {
        List<RankedItem> rankedItems = idMappingService.convertListOfRankedItemVOs(
                musicActionService.mostSearchedTracks(tenantId, numberOfResults, timeRange, sortDescending));
        if (rankedItems != null && rankedItems.size() > 0) {
            return rankedItems.toArray(new RankedItem[rankedItems.size()]);
        } else {
            return new RankedItem[0];
        }
    }

    @IOLog
    @Profiled
    public RankedItem[] mostPreviewedTracks(Integer tenantId, Integer numberOfResults, TimeConstraintVO timeRange,
                                            Boolean sortDescending) throws MusicShopRecommenderException {
        List<RankedItem> rankedItems = idMappingService.convertListOfRankedItemVOs(
                musicActionService.mostPreviewedTracks(tenantId, numberOfResults, timeRange, sortDescending));
        if (rankedItems != null && rankedItems.size() > 0) {
            return rankedItems.toArray(new RankedItem[rankedItems.size()]);
        } else {
            return new RankedItem[0];
        }
    }

    @IOLog
    @Profiled
    public RankedItem[] mostAddedToPlaylistTracks(Integer tenantId, Integer numberOfResults, TimeConstraintVO timeRange,
                                                  Boolean sortDescending) throws MusicShopRecommenderException {
        List<RankedItem> rankedItems = idMappingService.convertListOfRankedItemVOs(
                musicActionService.mostAddedToPlaylistTracks(tenantId, numberOfResults, timeRange, sortDescending));
        if (rankedItems != null && rankedItems.size() > 0) {
            return rankedItems.toArray(new RankedItem[rankedItems.size()]);
        } else {
            return new RankedItem[0];
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Ratings
    ///////////////////////////////////////////////////////////////////////////////////////////////
    @IOLog
    @Profiled
    public Rating[] artistRatings(Integer tenantId, String userId, String sessionId, Integer numberOfResults,
                                  TimeConstraintVO timeRange) throws MusicShopRecommenderException {
        List<Rating> artistRatings = idMappingService.convertListOfRatingVOs(musicActionService
                .artistRatings(tenantId, idMappingDAO.lookup(userId), sessionId, numberOfResults, timeRange));
        if (artistRatings != null && artistRatings.size() > 0) {
            return artistRatings.toArray(new Rating[artistRatings.size()]);
        } else {
            return new Rating[0];
        }
    }

    @IOLog
    @Profiled
    public Rating[] badArtistRatings(Integer tenantId, String userId, String sessionId, Integer numberOfResults,
                                     TimeConstraintVO timeRange) throws MusicShopRecommenderException {
        List<Rating> artistRatings = idMappingService.convertListOfRatingVOs(musicActionService
                .badArtistRatings(tenantId, idMappingDAO.lookup(userId), sessionId, numberOfResults, timeRange));
        if (artistRatings != null && artistRatings.size() > 0) {
            return artistRatings.toArray(new Rating[artistRatings.size()]);
        } else {
            return new Rating[0];
        }
    }

    @IOLog
    @Profiled
    public Rating[] goodArtistRatings(Integer tenantId, String userId, String sessionId, Integer numberOfResults,
                                      TimeConstraintVO timeRange) throws MusicShopRecommenderException {
        List<Rating> artistRatings = idMappingService.convertListOfRatingVOs(musicActionService
                .goodArtistRatings(tenantId, idMappingDAO.lookup(userId), sessionId, numberOfResults, timeRange));
        if (artistRatings != null && artistRatings.size() > 0) {
            return artistRatings.toArray(new Rating[artistRatings.size()]);
        } else {
            return new Rating[0];
        }
    }

    @IOLog
    @Profiled
    public Rating[] lastGoodArtistRatings(Integer tenantId, String userId, String sessionId, Integer numberOfResults)
            throws MusicShopRecommenderException {
        List<Rating> artistRatings = idMappingService.convertListOfRatingVOs(musicActionService
                .lastGoodArtistRatings(tenantId, idMappingDAO.lookup(userId), sessionId, numberOfResults));
        if (artistRatings != null && artistRatings.size() > 0) {
            return artistRatings.toArray(new Rating[artistRatings.size()]);
        } else {
            return new Rating[0];
        }
    }

    @IOLog
    @Profiled
    public Rating[] trackRatings(Integer tenantId, String userId, String sessionId, Integer numberOfResults,
                                 TimeConstraintVO timeRange) throws MusicShopRecommenderException {
        List<Rating> trackRatings = idMappingService.convertListOfRatingVOs(musicActionService
                .trackRatings(tenantId, idMappingDAO.lookup(userId), sessionId, numberOfResults, timeRange));
        if (trackRatings != null && trackRatings.size() > 0) {
            return trackRatings.toArray(new Rating[trackRatings.size()]);
        } else {
            return new Rating[0];
        }
    }

    @IOLog
    @Profiled
    public Rating[] badTrackRatings(Integer tenantId, String userId, String sessionId, Integer numberOfResults,
                                    TimeConstraintVO timeRange) throws MusicShopRecommenderException {
        List<Rating> trackRatings = idMappingService.convertListOfRatingVOs(musicActionService
                .badTrackRatings(tenantId, idMappingDAO.lookup(userId), sessionId, numberOfResults, timeRange));
        if (trackRatings != null && trackRatings.size() > 0) {
            return trackRatings.toArray(new Rating[trackRatings.size()]);
        } else {
            return new Rating[0];
        }
    }

    @IOLog
    @Profiled
    public Rating[] goodTrackRatings(Integer tenantId, String userId, String sessionId, Integer numberOfResults,
                                     TimeConstraintVO timeRange) throws MusicShopRecommenderException {
        List<Rating> trackRatings = idMappingService.convertListOfRatingVOs(musicActionService
                .goodTrackRatings(tenantId, idMappingDAO.lookup(userId), sessionId, numberOfResults, timeRange));
        if (trackRatings != null && trackRatings.size() > 0) {
            return trackRatings.toArray(new Rating[trackRatings.size()]);
        } else {
            return new Rating[0];
        }
    }

    @IOLog
    @Profiled
    public Rating[] lastGoodTrackRatings(Integer tenantId, String userId, String sessionId, Integer numberOfResults)
            throws MusicShopRecommenderException {
        List<Rating> trackRatings = idMappingService.convertListOfRatingVOs(musicActionService
                .lastGoodTrackRatings(tenantId, idMappingDAO.lookup(userId), sessionId, numberOfResults));
        if (trackRatings != null && trackRatings.size() > 0) {
            return trackRatings.toArray(new Rating[trackRatings.size()]);
        } else {
            return new Rating[0];
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Recommendations
    ///////////////////////////////////////////////////////////////////////////////////////////////
    @IOLog
    @Profiled
    public RecommendedItem[] alsoBoughtTracks(Integer tenantId, String userId, String sessionId, String trackId)
            throws MusicShopRecommenderException {
        RecommendationVO<Integer, String> recommendation = musicRecommenderService
                .alsoBoughtTracks(tenantId, idMappingDAO.lookup(userId), sessionId,
                        new ItemVO<Integer, String>(tenantId, idMappingDAO.lookup(trackId),
                                TypeMappingService.ITEM_TYPE_TRACK));
        List<RecommendedItem> recommendedItems = idMappingService
                .convertListOfRecommendedItemVOs(recommendation.getRecommendedItems());
        if (recommendedItems != null && recommendedItems.size() > 0) {
            return recommendedItems.toArray(new RecommendedItem[recommendedItems.size()]);
        } else {
            return new RecommendedItem[0];
        }
    }

    @IOLog
    @Profiled
    public RecommendedItem[] alsoSearchedArtists(Integer tenantId, String userId, String sessionId, String itemId,
                                                 String itemType) throws MusicShopRecommenderException {
        RecommendationVO<Integer, String> recommendation = musicRecommenderService
                .alsoSearchedArtists(tenantId, idMappingDAO.lookup(userId), sessionId,
                        new ItemVO<Integer, String>(tenantId, idMappingDAO.lookup(itemId), itemType));
        List<RecommendedItem> recommendedItems = idMappingService
                .convertListOfRecommendedItemVOs(recommendation.getRecommendedItems());
        if (recommendedItems != null && recommendedItems.size() > 0) {
            return recommendedItems.toArray(new RecommendedItem[recommendedItems.size()]);
        } else {
            return new RecommendedItem[0];
        }
    }

    @IOLog
    @Profiled
    public RecommendedItem[] alsoSearchedTracks(Integer tenantId, String userId, String sessionId, String itemId,
                                                String itemType) throws MusicShopRecommenderException {
        RecommendationVO<Integer, String> recommendation = musicRecommenderService
                .alsoSearchedTracks(tenantId, idMappingDAO.lookup(userId), sessionId,
                        new ItemVO<Integer, String>(tenantId, idMappingDAO.lookup(itemId), itemType));
        List<RecommendedItem> recommendedItems = idMappingService
                .convertListOfRecommendedItemVOs(recommendation.getRecommendedItems());
        if (recommendedItems != null && recommendedItems.size() > 0) {
            return recommendedItems.toArray(new RecommendedItem[recommendedItems.size()]);
        } else {
            return new RecommendedItem[0];
        }
    }

    @IOLog
    @Profiled
    public RecommendedItem[] alsoViewedArtists(Integer tenantId, String userId, String sessionId, String itemId,
                                               String itemType) throws MusicShopRecommenderException {
        RecommendationVO<Integer, String> recommendation = musicRecommenderService
                .alsoViewedArtists(tenantId, idMappingDAO.lookup(userId), sessionId,
                        new ItemVO<Integer, String>(tenantId, idMappingDAO.lookup(itemId), itemType));
        List<RecommendedItem> recommendedItems = idMappingService
                .convertListOfRecommendedItemVOs(recommendation.getRecommendedItems());
        if (recommendedItems != null && recommendedItems.size() > 0) {
            return recommendedItems.toArray(new RecommendedItem[recommendedItems.size()]);
        } else {
            return new RecommendedItem[0];
        }
    }

    @IOLog
    @Profiled
    public RecommendedItem[] alsoViewedGenres(Integer tenantId, String userId, String sessionId, String itemId,
                                              String itemType) throws MusicShopRecommenderException {
        RecommendationVO<Integer, String> recommendation = musicRecommenderService
                .alsoViewedGenres(tenantId, idMappingDAO.lookup(userId), sessionId,
                        new ItemVO<Integer, String>(tenantId, idMappingDAO.lookup(itemId), itemType));
        List<RecommendedItem> recommendedItems = idMappingService
                .convertListOfRecommendedItemVOs(recommendation.getRecommendedItems());
        if (recommendedItems != null && recommendedItems.size() > 0) {
            return recommendedItems.toArray(new RecommendedItem[recommendedItems.size()]);
        } else {
            return new RecommendedItem[0];
        }
    }

    @IOLog
    @Profiled
    public RecommendedItem[] alsoViewedTracks(Integer tenantId, String userId, String sessionId, String itemId,
                                              String itemType) throws MusicShopRecommenderException {
        RecommendationVO<Integer, String> recommendation = musicRecommenderService
                .alsoSearchedTracks(tenantId, idMappingDAO.lookup(userId), sessionId,
                        new ItemVO<Integer, String>(tenantId, idMappingDAO.lookup(itemId), itemType));
        List<RecommendedItem> recommendedItems = idMappingService
                .convertListOfRecommendedItemVOs(recommendation.getRecommendedItems());
        if (recommendedItems != null && recommendedItems.size() > 0) {
            return recommendedItems.toArray(new RecommendedItem[recommendedItems.size()]);
        } else {
            return new RecommendedItem[0];
        }
    }

    @IOLog
    @Profiled
    public RecommendedItem[] artistsBasedOnSearchingHistory(Integer tenantId, String userId, String sessionId,
                                                            String consideredItemType,
                                                            Integer numberOfLastActionsConsidered, String assocType)
            throws MusicShopRecommenderException {
        RecommendationVO<Integer, String> recommendation = musicRecommenderService
                .artistsBasedOnSearchingHistory(tenantId, idMappingDAO.lookup(userId), sessionId, consideredItemType,
                        numberOfLastActionsConsidered, assocType);
        List<RecommendedItem> recommendedItems = idMappingService
                .convertListOfRecommendedItemVOs(recommendation.getRecommendedItems());
        if (recommendedItems != null && recommendedItems.size() > 0) {
            return recommendedItems.toArray(new RecommendedItem[recommendedItems.size()]);
        } else {
            return new RecommendedItem[0];
        }
    }

    @IOLog
    @Profiled
    public RecommendedItem[] artistsBasedOnViewingHistory(Integer tenantId, String userId, String sessionId,
                                                          String consideredItemType,
                                                          Integer numberOfLastActionsConsidered, String assocType)
            throws MusicShopRecommenderException {
        RecommendationVO<Integer, String> recommendation = musicRecommenderService
                .artistsBasedOnViewingHistory(tenantId, idMappingDAO.lookup(userId), sessionId, consideredItemType,
                        numberOfLastActionsConsidered, assocType);
        List<RecommendedItem> recommendedItems = idMappingService
                .convertListOfRecommendedItemVOs(recommendation.getRecommendedItems());
        if (recommendedItems != null && recommendedItems.size() > 0) {
            return recommendedItems.toArray(new RecommendedItem[recommendedItems.size()]);
        } else {
            return new RecommendedItem[0];
        }
    }

    @IOLog
    @Profiled
    public RecommendedItem[] genresBasedOnViewingHistory(Integer tenantId, String userId, String sessionId,
                                                         String consideredItemType,
                                                         Integer numberOfLastActionsConsidered, String assocType)
            throws MusicShopRecommenderException {
        RecommendationVO<Integer, String> recommendation = musicRecommenderService
                .genresBasedOnViewingHistory(tenantId, idMappingDAO.lookup(userId), sessionId, consideredItemType,
                        numberOfLastActionsConsidered, assocType);
        List<RecommendedItem> recommendedItems = idMappingService
                .convertListOfRecommendedItemVOs(recommendation.getRecommendedItems());
        if (recommendedItems != null && recommendedItems.size() > 0) {
            return recommendedItems.toArray(new RecommendedItem[recommendedItems.size()]);
        } else {
            return new RecommendedItem[0];
        }
    }

    @IOLog
    @Profiled
    public RecommendedItem[] tracksBasedOnPurchaseHistory(Integer tenantId, String userId, String sessionId,
                                                          Integer numberOfLastActionsConsidered, String assocType)
            throws MusicShopRecommenderException {
        RecommendationVO<Integer, String> recommendation = musicRecommenderService
                .tracksBasedOnPurchaseHistory(tenantId, idMappingDAO.lookup(userId), sessionId,
                        numberOfLastActionsConsidered, assocType);
        List<RecommendedItem> recommendedItems = idMappingService
                .convertListOfRecommendedItemVOs(recommendation.getRecommendedItems());
        if (recommendedItems != null && recommendedItems.size() > 0) {
            return recommendedItems.toArray(new RecommendedItem[recommendedItems.size()]);
        } else {
            return new RecommendedItem[0];
        }
    }

    @IOLog
    @Profiled
    public RecommendedItem[] tracksBasedOnSearchingHistory(Integer tenantId, String userId, String sessionId,
                                                           String consideredItemType,
                                                           Integer numberOfLastActionsConsidered, String assocType)
            throws MusicShopRecommenderException {
        RecommendationVO<Integer, String> recommendation = musicRecommenderService
                .tracksBasedOnSearchingHistory(tenantId, idMappingDAO.lookup(userId), sessionId, consideredItemType,
                        numberOfLastActionsConsidered, assocType);
        List<RecommendedItem> recommendedItems = idMappingService
                .convertListOfRecommendedItemVOs(recommendation.getRecommendedItems());
        if (recommendedItems != null && recommendedItems.size() > 0) {
            return recommendedItems.toArray(new RecommendedItem[recommendedItems.size()]);
        } else {
            return new RecommendedItem[0];
        }
    }

    @IOLog
    @Profiled
    public RecommendedItem[] tracksBasedOnViewingHistory(Integer tenantId, String userId, String sessionId,
                                                         String consideredItemType,
                                                         Integer numberOfLastActionsConsidered, String assocType)
            throws MusicShopRecommenderException {
        RecommendationVO<Integer, String> recommendation = musicRecommenderService
                .tracksBasedOnViewingHistory(tenantId, idMappingDAO.lookup(userId), sessionId, consideredItemType,
                        numberOfLastActionsConsidered, assocType);
        List<RecommendedItem> recommendedItems = idMappingService
                .convertListOfRecommendedItemVOs(recommendation.getRecommendedItems());
        if (recommendedItems != null && recommendedItems.size() > 0) {
            return recommendedItems.toArray(new RecommendedItem[recommendedItems.size()]);
        } else {
            return new RecommendedItem[0];
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Utility methods
    ///////////////////////////////////////////////////////////////////////////////////////////////    
    @IOLog
    @Profiled
    public String[] getAssocTypes(Integer tenantId) throws MusicShopRecommenderException {
        return typeMappingService.getAssocTypes(tenantId).toArray(new String[0]);
    }

    @IOLog
    @Profiled
    public String[] getItemTypes(Integer tenantId) throws MusicShopRecommenderException {
        return typeMappingService.getItemTypes(tenantId).toArray(new String[0]);
    }

    // getter/setter
    public MusicActionService getMusicActionService() {
        return musicActionService;
    }

    public void setMusicActionService(MusicActionService musicActionService) {
        this.musicActionService = musicActionService;
    }

    public MusicRecommenderService getMusicRecommenderService() {
        return musicRecommenderService;
    }

    public void setMusicRecommenderService(MusicRecommenderService musicRecommenderService) {
        this.musicRecommenderService = musicRecommenderService;
    }

    public TypeMappingService getTypeMappingService() {
        return typeMappingService;
    }

    public void setTypeMappingService(TypeMappingService typeMappingService) {
        this.typeMappingService = typeMappingService;
    }

    public IDMappingDAO getIdMappingDAO() {
        return idMappingDAO;
    }

    public void setIdMappingDAO(IDMappingDAO idMappingDAO) {
        this.idMappingDAO = idMappingDAO;
    }

    public IDMappingService getIdMappingService() {
        return idMappingService;
    }

    public void setIdMappingService(IDMappingService idMappingService) {
        this.idMappingService = idMappingService;
    }


}
