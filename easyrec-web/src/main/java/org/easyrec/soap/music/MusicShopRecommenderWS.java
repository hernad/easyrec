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
package org.easyrec.soap.music;

import org.easyrec.model.core.transfer.TimeConstraintVO;
import org.easyrec.model.web.RankedItem;
import org.easyrec.model.web.Rating;
import org.easyrec.model.web.RecommendedItem;
import org.easyrec.soap.music.exception.MusicShopRecommenderException;

/**
 * Music Recommender Webservice wrapper interface (for the music domain)
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: pmarschik $<br/>
 * $Date: 2011-02-11 11:04:49 +0100 (Fr, 11 Feb 2011) $<br/>
 * $Revision: 17656 $</p>
 *
 * @author Roman Cerny
 */
public interface MusicShopRecommenderWS {
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Actions    
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * storing a 'purchase track' action in the recommender storage
     */
    public void purchaseTrack(Integer tenantId, String userId, String sessionId, String ip, String trackId,
                              String description) throws MusicShopRecommenderException;

    /**
     * storing a 'view artist' action in the recommender storage
     */
    public void viewArtist(Integer tenantId, String userId, String sessionId, String ip, String artistId,
                           String description) throws MusicShopRecommenderException;

    /**
     * storing a 'view genre' action in the recommender storage
     */
    public void viewGenre(Integer tenantId, String userId, String sessionId, String ip, String genreId,
                          String description) throws MusicShopRecommenderException;

    /**
     * storing a 'view track' action in the recommender storage
     */
    public void viewTrack(Integer tenantId, String userId, String sessionId, String ip, String trackId,
                          String description) throws MusicShopRecommenderException;

    /**
     * storing a 'rate artist' action in the recommender storage
     */
    public void rateArtist(Integer tenantId, String userId, String sessionId, String ip, String artistId,
                           Integer ratingValue, String description) throws MusicShopRecommenderException;

    /**
     * storing a 'rate track' action in the recommender storage
     */
    public void rateTrack(Integer tenantId, String userId, String sessionId, String ip, String trackId,
                          Integer ratingValue, String description) throws MusicShopRecommenderException;

    /**
     * storing a 'search artist' action in the recommender storage
     */
    public void searchArtist(Integer tenantId, String userId, String sessionId, String ip, String artistId,
                             Boolean searchSucceeded, Integer numberOfFoundArtists, String description)
            throws MusicShopRecommenderException;

    /**
     * storing a 'search track' action in the recommender storage
     */
    public void searchTrack(Integer tenantId, String userId, String sessionId, String ip, String trackId,
                            Boolean searchSucceeded, Integer numberOfFoundTracks, String description)
            throws MusicShopRecommenderException;

    /**
     * storing a 'preview track' action in the recommender storage
     */
    public void previewTrack(Integer tenantId, String userId, String sessionId, String ip, String trackId,
                             String description) throws MusicShopRecommenderException;

    /**
     * storing a 'add track to playlist' action in the recommender storage
     */
    public void addTrackToPlaylist(Integer tenantId, String userId, String sessionId, String ip, String trackId,
                                   String description) throws MusicShopRecommenderException;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Rankings
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * returns 'bought tracks' (for a given tenant) ranked by the frequency of 'buy' actions
     */
    public RankedItem[] mostBoughtTracks(Integer tenantId, Integer numberOfResults, TimeConstraintVO timeRange,
                                         Boolean sortDescending) throws MusicShopRecommenderException;

    /**
     * returns 'viewed artists' (for a given tenant) ranked by the frequency of 'view' actions
     */
    public RankedItem[] mostViewedArtists(Integer tenantId, Integer numberOfResults, TimeConstraintVO timeRange,
                                          Boolean sortDescending) throws MusicShopRecommenderException;

    /**
     * returns 'viewed genres' (for a given tenant) ranked by the frequency of 'view' actions
     */
    public RankedItem[] mostViewedGenres(Integer tenantId, Integer numberOfResults, TimeConstraintVO timeRange,
                                         Boolean sortDescending) throws MusicShopRecommenderException;

    /**
     * returns 'viewed tracks' (for a given tenant) ranked by the frequency of 'view' actions
     */
    public RankedItem[] mostViewedTracks(Integer tenantId, Integer numberOfResults, TimeConstraintVO timeRange,
                                         Boolean sortDescending) throws MusicShopRecommenderException;

    /**
     * returns 'rated artists' (for a given tenant) ranked by the frequency of 'rate' actions
     */
    public RankedItem[] mostRatedArtists(Integer tenantId, Integer numberOfResults, TimeConstraintVO timeRange,
                                         Boolean sortDescending) throws MusicShopRecommenderException;

    /**
     * returns 'rated tracks' (for a given tenant) ranked by the frequency of 'rate' actions
     */
    public RankedItem[] mostRatedTracks(Integer tenantId, Integer numberOfResults, TimeConstraintVO timeRange,
                                        Boolean sortDescending) throws MusicShopRecommenderException;

    /**
     * returns 'searched artists' (for a given tenant) ranked by the frequency of 'search' actions
     */
    public RankedItem[] mostSearchedArtists(Integer tenantId, Integer numberOfResults, TimeConstraintVO timeRange,
                                            Boolean sortDescending) throws MusicShopRecommenderException;

    /**
     * returns 'searched tracks' (for a given tenant) ranked by the frequency of 'search' actions
     */
    public RankedItem[] mostSearchedTracks(Integer tenantId, Integer numberOfResults, TimeConstraintVO timeRange,
                                           Boolean sortDescending) throws MusicShopRecommenderException;

    /**
     * returns 'previewed Tracks' (for a given tenant) ranked by the frequency of 'preview' actions
     */
    public RankedItem[] mostPreviewedTracks(Integer tenantId, Integer numberOfResults, TimeConstraintVO timeRange,
                                            Boolean sortDescending) throws MusicShopRecommenderException;

    /**
     * returns 'tracks that have been added to a playlist' (for a given tenant) ranked by the frequency of 'add to playlist' actions
     */
    public RankedItem[] mostAddedToPlaylistTracks(Integer tenantId, Integer numberOfResults, TimeConstraintVO timeRange,
                                                  Boolean sortDescending) throws MusicShopRecommenderException;

    // HINT: use tenant specific threshold 'ratingRangeNeutral' to decide between good and bad ratings (Mantis Issue: #666)
    // public RankedItem[] mostBadRatedArtists(Integer tenantId, Integer numberOfResults, TimeConstraintVO timeRange, Boolean sortDescending) throws MusicShopRecommenderException;
    // public RankedItem[] mostGoodRatedArtists(Integer tenantId, Integer numberOfResults, TimeConstraintVO timeRange, Boolean sortDescending) throws MusicShopRecommenderException;
    // public RankedItem[] mostBadRatedTracks(Integer tenantId, Integer numberOfResults, TimeConstraintVO timeRange, Boolean sortDescending) throws MusicShopRecommenderException;
    // public RankedItem[] mostGoodRatedTracks(Integer tenantId, Integer numberOfResults, TimeConstraintVO timeRange, Boolean sortDescending) throws MusicShopRecommenderException;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Ratings
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * returns 'ratings' over artists (for a given tenant, optionally user and session) ordered by the 'ratingValue'
     */
    public Rating[] artistRatings(Integer tenantId, String userId, String sessionId, Integer numberOfResults,
                                  TimeConstraintVO timeRange) throws MusicShopRecommenderException;

    /**
     * returns 'bad ratings' over artists (for a given tenant, optionally user and session) ordered by the 'ratingValue'
     * only returns ratings with a value LOWER or EQUAL than the tenant specific threshold 'ratingRangeNeutral'
     */
    public Rating[] badArtistRatings(Integer tenantId, String userId, String sessionId, Integer numberOfResults,
                                     TimeConstraintVO timeRange) throws MusicShopRecommenderException;

    /**
     * returns 'good ratings' over artists (for a given tenant, optionally user and session) ordered by the 'ratingValue'
     * only returns ratings with a value HIGHER than the tenant specific threshold 'ratingRangeNeutral'
     */
    public Rating[] goodArtistRatings(Integer tenantId, String userId, String sessionId, Integer numberOfResults,
                                      TimeConstraintVO timeRange) throws MusicShopRecommenderException;

    /**
     * returns the 'last good ratings' over artists (for a given tenant, optionally user and session) ordered by the 'actionTime'
     * only returns ratings with a value HIGHER than the tenant specific threshold 'ratingRangeNeutral'
     */
    public Rating[] lastGoodArtistRatings(Integer tenantId, String userId, String sessionId, Integer numberOfResults)
            throws MusicShopRecommenderException;

    /**
     * returns 'ratings' over tracks (for a given tenant, optionally user and session) ordered by the 'ratingValue'
     */
    public Rating[] trackRatings(Integer tenantId, String userId, String sessionId, Integer numberOfResults,
                                 TimeConstraintVO timeRange) throws MusicShopRecommenderException;

    /**
     * returns 'bad ratings' over tracks (for a given tenant, optionally user and session) ordered by the 'ratingValue'
     * only returns ratings with a value LOWER or EQUAL than the tenant specific threshold 'ratingRangeNeutral'
     */
    public Rating[] badTrackRatings(Integer tenantId, String userId, String sessionId, Integer numberOfResults,
                                    TimeConstraintVO timeRange) throws MusicShopRecommenderException;

    /**
     * returns 'good ratings' over tracks (for a given tenant, optionally user and session) ordered by the 'ratingValue'
     * only returns ratings with a value HIGHER than the tenant specific threshold 'ratingRangeNeutral'
     */
    public Rating[] goodTrackRatings(Integer tenantId, String userId, String sessionId, Integer numberOfResults,
                                     TimeConstraintVO timeRange) throws MusicShopRecommenderException;

    /**
     * returns the 'last good ratings' over tracks (for a given tenant, optionally user and session) ordered by the 'actionTime'
     * only returns ratings with a value HIGHER than the tenant specific threshold 'ratingRangeNeutral'
     */
    public Rating[] lastGoodTrackRatings(Integer tenantId, String userId, String sessionId, Integer numberOfResults)
            throws MusicShopRecommenderException;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Recommendations
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * returns {@link RecommendedItem}s, based on the 'purchase' history of track items,
     * taking 'numberOfLastActionsConsidered' actions into consideration,
     * looking for business rules (item assocs) with the given 'assocType' and track items in the result
     */
    public RecommendedItem[] tracksBasedOnPurchaseHistory(Integer tenantId, String userId, String sessionId,
                                                          Integer numberOfLastActionsConsidered, String assocType)
            throws MusicShopRecommenderException;

    /**
     * returns {@link RecommendedItem}s, based on the 'view' history of items with the given 'consideredItemType',
     * taking 'numberOfLastActionsConsidered' actions into consideration,
     * looking for business rules (item assocs) with the given 'assocType' and artist items in the result
     */
    public RecommendedItem[] artistsBasedOnViewingHistory(Integer tenantId, String userId, String sessionId,
                                                          String consideredItemType,
                                                          Integer numberOfLastActionsConsidered, String assocType)
            throws MusicShopRecommenderException;

    /**
     * returns {@link RecommendedItem}s, based on the 'view' history of items with the given 'consideredItemType',
     * taking 'numberOfLastActionsConsidered' actions into consideration,
     * looking for business rules (item assocs) with the given 'assocType' and genre items in the result
     */
    public RecommendedItem[] genresBasedOnViewingHistory(Integer tenantId, String userId, String sessionId,
                                                         String consideredItemType,
                                                         Integer numberOfLastActionsConsidered, String assocType)
            throws MusicShopRecommenderException;

    /**
     * returns {@link RecommendedItem}s, based on the 'view' history of items with the given 'consideredItemType',
     * taking 'numberOfLastActionsConsidered' actions into consideration,
     * looking for business rules (item assocs) with the given 'assocType' and track items in the result
     */
    public RecommendedItem[] tracksBasedOnViewingHistory(Integer tenantId, String userId, String sessionId,
                                                         String consideredItemType,
                                                         Integer numberOfLastActionsConsidered, String assocType)
            throws MusicShopRecommenderException;

    /**
     * returns {@link RecommendedItem}s, based on the 'search' history of items with the given 'consideredItemType',
     * taking 'numberOfLastActionsConsidered' actions into consideration,
     * looking for business rules (item assocs) with the given 'assocType' and artist items in the result
     */
    public RecommendedItem[] artistsBasedOnSearchingHistory(Integer tenantId, String userId, String sessionId,
                                                            String consideredItemType,
                                                            Integer numberOfLastActionsConsidered, String assocType)
            throws MusicShopRecommenderException;

    /**
     * returns {@link RecommendedItem}s, based on the 'search' history of items with the given 'consideredItemType',
     * taking 'numberOfLastActionsConsidered' actions into consideration,
     * looking for business rules (item assocs) with the given 'assocType' and tracks items in the result
     */
    public RecommendedItem[] tracksBasedOnSearchingHistory(Integer tenantId, String userId, String sessionId,
                                                           String consideredItemType,
                                                           Integer numberOfLastActionsConsidered, String assocType)
            throws MusicShopRecommenderException;

    // HINT: implement some additional music recommendations (concerning playlists and previewing) (Mantis Issue: #667)
    // public RecommendedItem[] tracksBasedOnAddingToPlaylistHistory(Integer tenantId, String userId, String sessionId, Integer numberOfLastActionsConsidered, String assocType) throws MusicShopRecommenderException;
    // public RecommendedItem[] tracksBasedOnPreviewingHistory(Integer tenantId, String userId, String sessionId, Integer numberOfLastActionsConsidered, String assocType) throws MusicShopRecommenderException;

    // HINT: use tenant specific threshold 'ratingRangeNeutral' to decide between good and bad ratings (Mantis Issue: #666)
    // public RecommendedItem[] artistsBasedOnRatingHistory(Integer tenantId, String userId, String sessionId, String consideredItemType, Integer numberOfLastActionsConsidered, String assocType) throws MusicShopRecommenderException;
    // public RecommendedItem[] artistsBasedOnBadRatingHistory(Integer tenantId, String userId, String sessionId, String consideredItemType, Integer numberOfLastActionsConsidered, String assocType) throws MusicShopRecommenderException;
    // public RecommendedItem[] artistsBasedOnGoodRatingHistory(Integer tenantId, String userId, String sessionId, String consideredItemType, Integer numberOfLastActionsConsidered, String assocType) throws MusicShopRecommenderException;
    // public RecommendedItem[] tracksBasedOnRatingHistory(Integer tenantId, String userId, String sessionId, String consideredItemType, Integer numberOfLastActionsConsidered, String assocType) throws MusicShopRecommenderException;
    // public RecommendedItem[] tracksBasedOnBadRatingHistory(Integer tenantId, String userId, String sessionId, String consideredItemType, Integer numberOfLastActionsConsidered, String assocType) throws MusicShopRecommenderException;
    // public RecommendedItem[] tracksBasedOnGoodRatingHistory(Integer tenantId, String userId, String sessionId, String consideredItemType, Integer numberOfLastActionsConsidered, String assocType) throws MusicShopRecommenderException;

    /**
     * returns {@link RecommendedItem}s, based on business rules that identify tracks as 'bought together'
     */
    public RecommendedItem[] alsoBoughtTracks(Integer tenantId, String userId, String sessionId, String trackId)
            throws MusicShopRecommenderException;

    /**
     * returns {@link RecommendedItem}s, based on business rules that identify artists as 'viewed together' with items of the given item type
     */
    public RecommendedItem[] alsoViewedArtists(Integer tenantId, String userId, String sessionId, String itemId,
                                               String itemType) throws MusicShopRecommenderException;

    /**
     * returns {@link RecommendedItem}s, based on business rules that identify genres as 'viewed together' with items of the given item type
     */
    public RecommendedItem[] alsoViewedGenres(Integer tenantId, String userId, String sessionId, String itemId,
                                              String itemType) throws MusicShopRecommenderException;

    /**
     * returns {@link RecommendedItem}s, based on business rules that identify tracks as 'viewed together' with items of the given item type
     */
    public RecommendedItem[] alsoViewedTracks(Integer tenantId, String userId, String sessionId, String itemId,
                                              String itemType) throws MusicShopRecommenderException;

    /**
     * returns {@link RecommendedItem}s, based on business rules that identify artists as 'searched together' with items of the given item type
     */
    public RecommendedItem[] alsoSearchedArtists(Integer tenantId, String userId, String sessionId, String itemId,
                                                 String itemType) throws MusicShopRecommenderException;

    /**
     * returns {@link RecommendedItem}s, based on business rules that identify tracks as 'searched together' with items of the given item type
     */
    public RecommendedItem[] alsoSearchedTracks(Integer tenantId, String userId, String sessionId, String itemId,
                                                String itemType) throws MusicShopRecommenderException;

    // HINT: implement some additional music recommendations (concerning playlists and previewing) (Mantis Issue: #667)
    //public RecommendedItem[] alsoToPlaylistAddedTracks(Integer tenantId, String userId, String sessionId, String trackId) throws MusicShopRecommenderException;
    //public RecommendedItem[] alsoPreviewedTracks(Integer tenantId, String userId, String sessionId, String trackId) throws MusicShopRecommenderException;

    // HINT: use tenant specific threshold 'ratingRangeNeutral' to decide between good and bad ratings (Mantis Issue: #666)
    //public RecommendedItem[] alsoRatedArtists(Integer tenantId, String userId, String sessionId, String artistId, String itemType) throws MusicShopRecommenderException; 
    //public RecommendedItem[] alsoBadRatedArtists(Integer tenantId, String userId, String sessionId, String artistId, String itemType) throws MusicShopRecommenderException;
    //public RecommendedItem[] alsoGoodRatedArtists(Integer tenantId, String userId, String sessionId, String artistId, String itemType) throws MusicShopRecommenderException;
    //public RecommendedItem[] alsoRatedTracks(Integer tenantId, String userId, String sessionId, String artistId, String itemType) throws MusicShopRecommenderException;
    //public RecommendedItem[] alsoBadRatedTracks(Integer tenantId, String userId, String sessionId, String artistId, String itemType) throws MusicShopRecommenderException; 
    //public RecommendedItem[] alsoGoodRatedTracks(Integer tenantId, String userId, String sessionId, String artistId, String itemType) throws MusicShopRecommenderException;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Utility methods
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * returns the possible item types of the given tenant
     */
    public String[] getItemTypes(Integer tenant) throws MusicShopRecommenderException;

    /**
     * returns the possible association types of the given tenant
     */
    public String[] getAssocTypes(Integer tenant) throws MusicShopRecommenderException;
}
