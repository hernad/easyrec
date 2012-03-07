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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyrec.model.core.transfer.TimeConstraintVO;
import org.easyrec.model.web.RankedItem;
import org.easyrec.model.web.Rating;
import org.easyrec.model.web.RecommendedItem;
import org.easyrec.soap.music.MusicShopRecommenderWS;
import org.easyrec.soap.music.exception.MusicShopRecommenderException;
import org.easyrec.soap.service.AuthenticationDispatcher;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

/**
 * Music Recommender Webservice implementation (for the music domain)
 * <p/>
 * Note: Since web services and aspects do not work properly on the same class, a wrapper for the functionality of this class is introduced.<br/>
 * Attention: Do NOT put any functionality in this class. All functionality shall be located in the wrapper class.
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

@WebService
public class MusicShopRecommenderWSImpl {
    @Resource
    public WebServiceContext wsContext;

    private final Log logger = LogFactory.getLog(this.getClass());
    private MusicShopRecommenderWS aspectWrappedMusicShopRecommenderWS;
    private AuthenticationDispatcher authenticationDispatcher;
    private String serviceName = this.getClass().getSimpleName();

    // default constructor for WS
    public MusicShopRecommenderWSImpl() {
        if (logger.isDebugEnabled()) {
            logger.debug("called default constructor MusicShopRecommenderWSImpl()");
        }
    }

    public MusicShopRecommenderWSImpl(MusicShopRecommenderWS aspectWrappedMusicShopRecommenderWS,
                                      AuthenticationDispatcher authenticationDispatcher) {

        if (logger.isDebugEnabled()) {
            logger.debug(
                    "called constructor MusicShopRecommenderWSImpl(aspectWrappedMusicShopRecommenderWS, authenticationDispatcher)");
        }
        this.aspectWrappedMusicShopRecommenderWS = aspectWrappedMusicShopRecommenderWS;
        this.authenticationDispatcher = authenticationDispatcher;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // interface "MusicShopRecommenderWS"   

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Actions
    ///////////////////////////////////////////////////////////////////////////////////////////////
    @WebMethod
    public void purchaseTrack(@WebParam(name = "tenant") String tenant, @WebParam(name = "userId") String userId,
                              @WebParam(name = "sessionId") String sessionId, @WebParam(name = "ip") String ip,
                              @WebParam(name = "trackId") String trackId,
                              @WebParam(name = "description") String description) throws MusicShopRecommenderException {
        aspectWrappedMusicShopRecommenderWS
                .purchaseTrack(authenticate(tenant), userId, sessionId, ip, trackId, description);
    }

    @WebMethod
    public void viewArtist(@WebParam(name = "tenant") String tenant, @WebParam(name = "userId") String userId,
                           @WebParam(name = "sessionId") String sessionId, @WebParam(name = "ip") String ip,
                           @WebParam(name = "artistId") String artistId,
                           @WebParam(name = "description") String description) throws MusicShopRecommenderException {
        aspectWrappedMusicShopRecommenderWS
                .viewArtist(authenticate(tenant), userId, sessionId, ip, artistId, description);
    }

    @WebMethod
    public void viewGenre(@WebParam(name = "tenant") String tenant, @WebParam(name = "userId") String userId,
                          @WebParam(name = "sessionId") String sessionId, @WebParam(name = "ip") String ip,
                          @WebParam(name = "genreId") String genreId,
                          @WebParam(name = "description") String description) throws MusicShopRecommenderException {
        aspectWrappedMusicShopRecommenderWS
                .viewGenre(authenticate(tenant), userId, sessionId, ip, genreId, description);
    }

    @WebMethod
    public void viewTrack(@WebParam(name = "tenant") String tenant, @WebParam(name = "userId") String userId,
                          @WebParam(name = "sessionId") String sessionId, @WebParam(name = "ip") String ip,
                          @WebParam(name = "trackId") String trackId,
                          @WebParam(name = "description") String description) throws MusicShopRecommenderException {
        aspectWrappedMusicShopRecommenderWS
                .viewTrack(authenticate(tenant), userId, sessionId, ip, trackId, description);
    }

    @WebMethod
    public void rateArtist(@WebParam(name = "tenant") String tenant, @WebParam(name = "userId") String userId,
                           @WebParam(name = "sessionId") String sessionId, @WebParam(name = "ip") String ip,
                           @WebParam(name = "artistId") String artistId,
                           @WebParam(name = "ratingValue") Integer ratingValue,
                           @WebParam(name = "description") String description) throws MusicShopRecommenderException {
        aspectWrappedMusicShopRecommenderWS
                .rateArtist(authenticate(tenant), userId, sessionId, ip, artistId, ratingValue, description);
    }

    @WebMethod
    public void rateTrack(@WebParam(name = "tenant") String tenant, @WebParam(name = "userId") String userId,
                          @WebParam(name = "sessionId") String sessionId, @WebParam(name = "ip") String ip,
                          @WebParam(name = "trackId") String trackId,
                          @WebParam(name = "ratingValue") Integer ratingValue,
                          @WebParam(name = "description") String description) throws MusicShopRecommenderException {
        aspectWrappedMusicShopRecommenderWS
                .rateTrack(authenticate(tenant), userId, sessionId, ip, trackId, ratingValue, description);
    }

    @WebMethod
    public void searchArtist(@WebParam(name = "tenant") String tenant, @WebParam(name = "userId") String userId,
                             @WebParam(name = "sessionId") String sessionId, @WebParam(name = "ip") String ip,
                             @WebParam(name = "artistId") String artistId,
                             @WebParam(name = "searchSucceeded") Boolean searchSucceeded,
                             @WebParam(name = "numberOfFoundArtists") Integer numberOfFoundArtists,
                             @WebParam(name = "description") String description) throws MusicShopRecommenderException {
        aspectWrappedMusicShopRecommenderWS
                .searchArtist(authenticate(tenant), userId, sessionId, ip, artistId, searchSucceeded,
                        numberOfFoundArtists, description);
    }

    @WebMethod
    public void searchTrack(@WebParam(name = "tenant") String tenant, @WebParam(name = "userId") String userId,
                            @WebParam(name = "sessionId") String sessionId, @WebParam(name = "ip") String ip,
                            @WebParam(name = "trackId") String trackId,
                            @WebParam(name = "searchSucceeded") Boolean searchSucceeded,
                            @WebParam(name = "numberOfFoundTracks") Integer numberOfFoundTracks,
                            @WebParam(name = "description") String description) throws MusicShopRecommenderException {
        aspectWrappedMusicShopRecommenderWS
                .searchTrack(authenticate(tenant), userId, sessionId, ip, trackId, searchSucceeded, numberOfFoundTracks,
                        description);
    }

    @WebMethod
    public void previewTrack(@WebParam(name = "tenant") String tenant, @WebParam(name = "userId") String userId,
                             @WebParam(name = "sessionId") String sessionId, @WebParam(name = "ip") String ip,
                             @WebParam(name = "trackId") String trackId,
                             @WebParam(name = "description") String description) throws MusicShopRecommenderException {
        aspectWrappedMusicShopRecommenderWS
                .previewTrack(authenticate(tenant), userId, sessionId, ip, trackId, description);
    }

    @WebMethod
    public void addTrackToPlaylist(@WebParam(name = "tenant") String tenant, @WebParam(name = "userId") String userId,
                                   @WebParam(name = "sessionId") String sessionId, @WebParam(name = "ip") String ip,
                                   @WebParam(name = "trackId") String trackId,
                                   @WebParam(name = "description") String description)
            throws MusicShopRecommenderException {
        aspectWrappedMusicShopRecommenderWS
                .addTrackToPlaylist(authenticate(tenant), userId, sessionId, ip, trackId, description);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Rankings
    ///////////////////////////////////////////////////////////////////////////////////////////////
    @WebMethod
    public RankedItem[] mostBoughtTracks(@WebParam(name = "tenant") String tenant,
                                         @WebParam(name = "numberOfResults") Integer numberOfResults,
                                         @WebParam(name = "timeRange") TimeConstraintVO timeRange,
                                         @WebParam(name = "sortDescending") Boolean sortDescending)
            throws MusicShopRecommenderException {
        return aspectWrappedMusicShopRecommenderWS
                .mostBoughtTracks(authenticate(tenant), numberOfResults, timeRange, sortDescending);
    }

    @WebMethod
    public RankedItem[] mostViewedArtists(@WebParam(name = "tenant") String tenant,
                                          @WebParam(name = "numberOfResults") Integer numberOfResults,
                                          @WebParam(name = "timeRange") TimeConstraintVO timeRange,
                                          @WebParam(name = "sortDescending") Boolean sortDescending)
            throws MusicShopRecommenderException {
        return aspectWrappedMusicShopRecommenderWS
                .mostViewedArtists(authenticate(tenant), numberOfResults, timeRange, sortDescending);
    }

    @WebMethod
    public RankedItem[] mostViewedGenres(@WebParam(name = "tenant") String tenant,
                                         @WebParam(name = "numberOfResults") Integer numberOfResults,
                                         @WebParam(name = "timeRange") TimeConstraintVO timeRange,
                                         @WebParam(name = "sortDescending") Boolean sortDescending)
            throws MusicShopRecommenderException {
        return aspectWrappedMusicShopRecommenderWS
                .mostViewedGenres(authenticate(tenant), numberOfResults, timeRange, sortDescending);
    }

    @WebMethod
    public RankedItem[] mostViewedTracks(@WebParam(name = "tenant") String tenant,
                                         @WebParam(name = "numberOfResults") Integer numberOfResults,
                                         @WebParam(name = "timeRange") TimeConstraintVO timeRange,
                                         @WebParam(name = "sortDescending") Boolean sortDescending)
            throws MusicShopRecommenderException {
        return aspectWrappedMusicShopRecommenderWS
                .mostViewedTracks(authenticate(tenant), numberOfResults, timeRange, sortDescending);
    }

    @WebMethod
    public RankedItem[] mostRatedArtists(@WebParam(name = "tenant") String tenant,
                                         @WebParam(name = "numberOfResults") Integer numberOfResults,
                                         @WebParam(name = "timeRange") TimeConstraintVO timeRange,
                                         @WebParam(name = "sortDescending") Boolean sortDescending)
            throws MusicShopRecommenderException {
        return aspectWrappedMusicShopRecommenderWS
                .mostRatedArtists(authenticate(tenant), numberOfResults, timeRange, sortDescending);
    }

    @WebMethod
    public RankedItem[] mostRatedTracks(@WebParam(name = "tenant") String tenant,
                                        @WebParam(name = "numberOfResults") Integer numberOfResults,
                                        @WebParam(name = "timeRange") TimeConstraintVO timeRange,
                                        @WebParam(name = "sortDescending") Boolean sortDescending)
            throws MusicShopRecommenderException {
        return aspectWrappedMusicShopRecommenderWS
                .mostRatedTracks(authenticate(tenant), numberOfResults, timeRange, sortDescending);
    }

    @WebMethod
    public RankedItem[] mostSearchedArtists(@WebParam(name = "tenant") String tenant,
                                            @WebParam(name = "numberOfResults") Integer numberOfResults,
                                            @WebParam(name = "timeRange") TimeConstraintVO timeRange,
                                            @WebParam(name = "sortDescending") Boolean sortDescending)
            throws MusicShopRecommenderException {
        return aspectWrappedMusicShopRecommenderWS
                .mostSearchedArtists(authenticate(tenant), numberOfResults, timeRange, sortDescending);
    }

    @WebMethod
    public RankedItem[] mostSearchedTracks(@WebParam(name = "tenant") String tenant,
                                           @WebParam(name = "numberOfResults") Integer numberOfResults,
                                           @WebParam(name = "timeRange") TimeConstraintVO timeRange,
                                           @WebParam(name = "sortDescending") Boolean sortDescending)
            throws MusicShopRecommenderException {
        return aspectWrappedMusicShopRecommenderWS
                .mostSearchedTracks(authenticate(tenant), numberOfResults, timeRange, sortDescending);
    }

    @WebMethod
    public RankedItem[] mostPreviewedTracks(@WebParam(name = "tenant") String tenant,
                                            @WebParam(name = "numberOfResults") Integer numberOfResults,
                                            @WebParam(name = "timeRange") TimeConstraintVO timeRange,
                                            @WebParam(name = "sortDescending") Boolean sortDescending)
            throws MusicShopRecommenderException {
        return aspectWrappedMusicShopRecommenderWS
                .mostPreviewedTracks(authenticate(tenant), numberOfResults, timeRange, sortDescending);
    }

    @WebMethod
    public RankedItem[] mostAddedToPlaylistTracks(@WebParam(name = "tenant") String tenant,
                                                  @WebParam(name = "numberOfResults") Integer numberOfResults,
                                                  @WebParam(name = "timeRange") TimeConstraintVO timeRange,
                                                  @WebParam(name = "sortDescending") Boolean sortDescending)
            throws MusicShopRecommenderException {
        return aspectWrappedMusicShopRecommenderWS
                .mostAddedToPlaylistTracks(authenticate(tenant), numberOfResults, timeRange, sortDescending);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Ratings
    ///////////////////////////////////////////////////////////////////////////////////////////////
    @WebMethod
    public Rating[] artistRatings(@WebParam(name = "tenant") String tenant, @WebParam(name = "userId") String userId,
                                  @WebParam(name = "sessionId") String sessionId,
                                  @WebParam(name = "numberOfResults") Integer numberOfResults,
                                  @WebParam(name = "timeRange") TimeConstraintVO timeRange)
            throws MusicShopRecommenderException {
        return aspectWrappedMusicShopRecommenderWS
                .artistRatings(authenticate(tenant), userId, sessionId, numberOfResults, timeRange);
    }

    @WebMethod
    public Rating[] badArtistRatings(@WebParam(name = "tenant") String tenant, @WebParam(name = "userId") String userId,
                                     @WebParam(name = "sessionId") String sessionId,
                                     @WebParam(name = "numberOfResults") Integer numberOfResults,
                                     @WebParam(name = "timeRange") TimeConstraintVO timeRange)
            throws MusicShopRecommenderException {
        return aspectWrappedMusicShopRecommenderWS
                .badArtistRatings(authenticate(tenant), userId, sessionId, numberOfResults, timeRange);
    }

    @WebMethod
    public Rating[] goodArtistRatings(@WebParam(name = "tenant") String tenant,
                                      @WebParam(name = "userId") String userId,
                                      @WebParam(name = "sessionId") String sessionId,
                                      @WebParam(name = "numberOfResults") Integer numberOfResults,
                                      @WebParam(name = "timeRange") TimeConstraintVO timeRange)
            throws MusicShopRecommenderException {
        return aspectWrappedMusicShopRecommenderWS
                .goodArtistRatings(authenticate(tenant), userId, sessionId, numberOfResults, timeRange);
    }

    @WebMethod
    public Rating[] lastGoodArtistRatings(@WebParam(name = "tenant") String tenant,
                                          @WebParam(name = "userId") String userId,
                                          @WebParam(name = "sessionId") String sessionId,
                                          @WebParam(name = "numberOfResults") Integer numberOfResults)
            throws MusicShopRecommenderException {
        return aspectWrappedMusicShopRecommenderWS
                .lastGoodArtistRatings(authenticate(tenant), userId, sessionId, numberOfResults);
    }

    @WebMethod
    public Rating[] trackRatings(@WebParam(name = "tenant") String tenant, @WebParam(name = "userId") String userId,
                                 @WebParam(name = "sessionId") String sessionId,
                                 @WebParam(name = "numberOfResults") Integer numberOfResults,
                                 @WebParam(name = "timeRange") TimeConstraintVO timeRange)
            throws MusicShopRecommenderException {
        return aspectWrappedMusicShopRecommenderWS
                .trackRatings(authenticate(tenant), userId, sessionId, numberOfResults, timeRange);
    }

    @WebMethod
    public Rating[] badTrackRatings(@WebParam(name = "tenant") String tenant, @WebParam(name = "userId") String userId,
                                    @WebParam(name = "sessionId") String sessionId,
                                    @WebParam(name = "numberOfResults") Integer numberOfResults,
                                    @WebParam(name = "timeRange") TimeConstraintVO timeRange)
            throws MusicShopRecommenderException {
        return aspectWrappedMusicShopRecommenderWS
                .badTrackRatings(authenticate(tenant), userId, sessionId, numberOfResults, timeRange);
    }

    @WebMethod
    public Rating[] goodTrackRatings(@WebParam(name = "tenant") String tenant, @WebParam(name = "userId") String userId,
                                     @WebParam(name = "sessionId") String sessionId,
                                     @WebParam(name = "numberOfResults") Integer numberOfResults,
                                     @WebParam(name = "timeRange") TimeConstraintVO timeRange)
            throws MusicShopRecommenderException {
        return aspectWrappedMusicShopRecommenderWS
                .goodTrackRatings(authenticate(tenant), userId, sessionId, numberOfResults, timeRange);
    }

    @WebMethod
    public Rating[] lastGoodTrackRatings(@WebParam(name = "tenant") String tenant,
                                         @WebParam(name = "userId") String userId,
                                         @WebParam(name = "sessionId") String sessionId,
                                         @WebParam(name = "numberOfResults") Integer numberOfResults)
            throws MusicShopRecommenderException {
        return aspectWrappedMusicShopRecommenderWS
                .lastGoodTrackRatings(authenticate(tenant), userId, sessionId, numberOfResults);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Recommendations
    ///////////////////////////////////////////////////////////////////////////////////////////////
    @WebMethod
    public RecommendedItem[] alsoBoughtTracks(@WebParam(name = "tenant") String tenant,
                                              @WebParam(name = "userId") String userId,
                                              @WebParam(name = "sessionId") String sessionId,
                                              @WebParam(name = "trackId") String trackId)
            throws MusicShopRecommenderException {
        return aspectWrappedMusicShopRecommenderWS.alsoBoughtTracks(authenticate(tenant), userId, sessionId, trackId);
    }

    @WebMethod
    public RecommendedItem[] alsoSearchedArtists(@WebParam(name = "tenant") String tenant,
                                                 @WebParam(name = "userId") String userId,
                                                 @WebParam(name = "sessionId") String sessionId,
                                                 @WebParam(name = "itemId") String itemId,
                                                 @WebParam(name = "itemType") String itemType)
            throws MusicShopRecommenderException {
        return aspectWrappedMusicShopRecommenderWS
                .alsoSearchedArtists(authenticate(tenant), userId, sessionId, itemId, itemType);
    }

    @WebMethod
    public RecommendedItem[] alsoSearchedTracks(@WebParam(name = "tenant") String tenant,
                                                @WebParam(name = "userId") String userId,
                                                @WebParam(name = "sessionId") String sessionId,
                                                @WebParam(name = "itemId") String itemId,
                                                @WebParam(name = "itemType") String itemType)
            throws MusicShopRecommenderException {
        return aspectWrappedMusicShopRecommenderWS
                .alsoSearchedTracks(authenticate(tenant), userId, sessionId, itemId, itemType);
    }

    @WebMethod
    public RecommendedItem[] alsoViewedArtists(@WebParam(name = "tenant") String tenant,
                                               @WebParam(name = "userId") String userId,
                                               @WebParam(name = "sessionId") String sessionId,
                                               @WebParam(name = "itemId") String itemId,
                                               @WebParam(name = "itemType") String itemType)
            throws MusicShopRecommenderException {
        return aspectWrappedMusicShopRecommenderWS
                .alsoViewedArtists(authenticate(tenant), userId, sessionId, itemId, itemType);
    }

    @WebMethod
    public RecommendedItem[] alsoViewedGenres(@WebParam(name = "tenant") String tenant,
                                              @WebParam(name = "userId") String userId,
                                              @WebParam(name = "sessionId") String sessionId,
                                              @WebParam(name = "itemId") String itemId,
                                              @WebParam(name = "itemType") String itemType)
            throws MusicShopRecommenderException {
        return aspectWrappedMusicShopRecommenderWS
                .alsoViewedGenres(authenticate(tenant), userId, sessionId, itemId, itemType);
    }

    @WebMethod
    public RecommendedItem[] alsoViewedTracks(@WebParam(name = "tenant") String tenant,
                                              @WebParam(name = "userId") String userId,
                                              @WebParam(name = "sessionId") String sessionId,
                                              @WebParam(name = "itemId") String itemId,
                                              @WebParam(name = "itemType") String itemType)
            throws MusicShopRecommenderException {
        return aspectWrappedMusicShopRecommenderWS
                .alsoViewedTracks(authenticate(tenant), userId, sessionId, itemId, itemType);
    }

    @WebMethod
    public RecommendedItem[] artistsBasedOnSearchingHistory(@WebParam(name = "tenant") String tenant,
                                                            @WebParam(name = "userId") String userId,
                                                            @WebParam(name = "sessionId") String sessionId, @WebParam(
                    name = "consideredItemType") String consideredItemType, @WebParam(
                    name = "numberOfLastActionsConsidered") Integer numberOfLastActionsConsidered,
                                                            @WebParam(name = "assocType") String assocType)
            throws MusicShopRecommenderException {
        return aspectWrappedMusicShopRecommenderWS
                .artistsBasedOnSearchingHistory(authenticate(tenant), userId, sessionId, consideredItemType,
                        numberOfLastActionsConsidered, assocType);
    }

    @WebMethod
    public RecommendedItem[] artistsBasedOnViewingHistory(@WebParam(name = "tenant") String tenant,
                                                          @WebParam(name = "userId") String userId,
                                                          @WebParam(name = "sessionId") String sessionId, @WebParam(
                    name = "consideredItemType") String consideredItemType, @WebParam(
                    name = "numberOfLastActionsConsidered") Integer numberOfLastActionsConsidered,
                                                          @WebParam(name = "assocType") String assocType)
            throws MusicShopRecommenderException {
        return aspectWrappedMusicShopRecommenderWS
                .artistsBasedOnViewingHistory(authenticate(tenant), userId, sessionId, consideredItemType,
                        numberOfLastActionsConsidered, assocType);
    }

    @WebMethod
    public RecommendedItem[] genresBasedOnViewingHistory(@WebParam(name = "tenant") String tenant,
                                                         @WebParam(name = "userId") String userId,
                                                         @WebParam(name = "sessionId") String sessionId, @WebParam(
                    name = "consideredItemType") String consideredItemType, @WebParam(
                    name = "numberOfLastActionsConsidered") Integer numberOfLastActionsConsidered,
                                                         @WebParam(name = "assocType") String assocType)
            throws MusicShopRecommenderException {
        return aspectWrappedMusicShopRecommenderWS
                .genresBasedOnViewingHistory(authenticate(tenant), userId, sessionId, consideredItemType,
                        numberOfLastActionsConsidered, assocType);
    }

    @WebMethod
    public RecommendedItem[] tracksBasedOnPurchaseHistory(@WebParam(name = "tenant") String tenant,
                                                          @WebParam(name = "userId") String userId,
                                                          @WebParam(name = "sessionId") String sessionId, @WebParam(
                    name = "numberOfLastActionsConsidered") Integer numberOfLastActionsConsidered,
                                                          @WebParam(name = "assocType") String assocType)
            throws MusicShopRecommenderException {
        return aspectWrappedMusicShopRecommenderWS
                .tracksBasedOnPurchaseHistory(authenticate(tenant), userId, sessionId, numberOfLastActionsConsidered,
                        assocType);
    }

    @WebMethod
    public RecommendedItem[] tracksBasedOnSearchingHistory(@WebParam(name = "tenant") String tenant,
                                                           @WebParam(name = "userId") String userId,
                                                           @WebParam(name = "sessionId") String sessionId, @WebParam(
                    name = "consideredItemType") String consideredItemType, @WebParam(
                    name = "numberOfLastActionsConsidered") Integer numberOfLastActionsConsidered,
                                                           @WebParam(name = "assocType") String assocType)
            throws MusicShopRecommenderException {
        return aspectWrappedMusicShopRecommenderWS
                .tracksBasedOnSearchingHistory(authenticate(tenant), userId, sessionId, consideredItemType,
                        numberOfLastActionsConsidered, assocType);
    }

    @WebMethod
    public RecommendedItem[] tracksBasedOnViewingHistory(@WebParam(name = "tenant") String tenant,
                                                         @WebParam(name = "userId") String userId,
                                                         @WebParam(name = "sessionId") String sessionId, @WebParam(
                    name = "consideredItemType") String consideredItemType, @WebParam(
                    name = "numberOfLastActionsConsidered") Integer numberOfLastActionsConsidered,
                                                         @WebParam(name = "assocType") String assocType)
            throws MusicShopRecommenderException {
        return aspectWrappedMusicShopRecommenderWS
                .tracksBasedOnViewingHistory(authenticate(tenant), userId, sessionId, consideredItemType,
                        numberOfLastActionsConsidered, assocType);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Utility methods
    ///////////////////////////////////////////////////////////////////////////////////////////////    
    @WebMethod
    public String[] getAssocTypes(@WebParam(name = "tenant") String tenant) throws MusicShopRecommenderException {
        return aspectWrappedMusicShopRecommenderWS.getAssocTypes(authenticate(tenant));
    }

    @WebMethod
    public String[] getItemTypes(@WebParam(name = "tenant") String tenant) throws MusicShopRecommenderException {
        return aspectWrappedMusicShopRecommenderWS.getItemTypes(authenticate(tenant));
    }

    // private methods
    private Integer authenticate(String tenant) throws MusicShopRecommenderException {

        try {
            MessageContext mc = wsContext.getMessageContext();
            HttpServletRequest req = (HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST);
            Integer tenantId = authenticationDispatcher.authenticateTenant(tenant, serviceName, req);
            if (tenantId == null) {
                throw new MusicShopRecommenderException("Unauthorized access!");
            }
            return tenantId;
        } catch (Exception e) {
            throw new MusicShopRecommenderException(e.getMessage());
        }
    }

    // getter/setter
    //    public MusicShopRecommenderWS getAspectWrappedMusicShopRecommenderWS()
    //    {
    //        return aspectWrappedMusicShopRecommenderWS;
    //    }
    //
    //    public void setAspectWrappedMusicShopRecommenderWS(MusicShopRecommenderWS aspectWrappedMusicShopRecommenderWS)
    //    {
    //        this.aspectWrappedMusicShopRecommenderWS = aspectWrappedMusicShopRecommenderWS;
    //    }
    //
    //    public AuthenticationDispatcher getAuthenticationDispatcher()
    //    {
    //        return authenticationDispatcher;
    //    }
    //
    //    public void setAuthenticationDispatcher(AuthenticationDispatcher authenticationDispatcher)
    //    {
    //        this.authenticationDispatcher = authenticationDispatcher;
    //    }


}