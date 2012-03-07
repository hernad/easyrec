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

var defaults = {
    drawingCallback: "drawRecommendationList",
    actionCallback: "void",
    userId:null,
    itemId:"-1",
    sessionId:easyrec_getSessionId(),
    itemUrl:"",
    itemDescription:"",
    itemImageUrl:"",
    timeRange:"ALL",
    numberOfResults:10,
    actionTime:null,
    strategy:"NEWEST",
    useFallback:false,
    itemType:"ITEM",
    requestedItemType:"ITEM",
    clusterId:null,
    basedOnActionType:"VIEW"
};

//////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////// Item Actions ////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////

function easyrec_sendAction(actionType, options) {
    var o = easyrec_extend(options, defaults);

    if (actionType != "buy" && actionType != "view" && actionType != "rate") {
        o.actionType = actionType;
        actionType = "sendaction";
    }

    easyrec_includeJavascript(easyrecApiUrl + actionType + "?" +
        "tenantid=" + tenantId +
        "&apikey=" + apiKey +
        ((o.userId) ? ("&userid=" + o.userId ) : "") +
        "&itemid=" + o.itemId +
        "&sessionid=" + o.sessionId +
        "&itemurl=" + encodeURIComponent(o.itemUrl) +
        "&itemdescription=" + encodeURIComponent(o.itemDescription) +
        "&itemimageurl=" + encodeURIComponent(o.itemImageUrl) +
        "&ratingvalue=" + o.ratingValue +
        "&callback=" + o.actionCallback +
        "&itemtype=" + o.itemType +
        ((o.actionType) ? ("&actiontype=" + o.actionType ) : "") +
        ((o.actionValue) ? ("&actionvalue=" + o.actionValue ) : "") +
        ((o.actionTime) ? ("&actiontime=" + o.actionTime ) : ""));
}

// This function tracks if a user purchases a specific item.
function easyrec_buy(options) {
    easyrec_sendAction("buy", options);
}

// This function tracks if a user views a specific item.
function easyrec_view(options) {
    easyrec_sendAction("view", options);
}

// This function tracks if a user rates a specific item.
function easyrec_rate(options) {
    easyrec_sendAction("rate", options);
}

// This function calls the recommender to retrieve a list of recommendations
function easyrec_getRecommendations(recommendationType, options) {
    var o = easyrec_extend(options, defaults);
    easyrec_includeJavascript(easyrecApiUrl + recommendationType + "?" +
        "tenantid=" + tenantId +
        "&apikey=" + apiKey +
        ((o.userId) ? ("&userid=" + o.userId ) : "") +
        "&itemid=" + o.itemId +
        "&itemtype=" + o.itemType +
        "&requesteditemtype=" + o.requestedItemType +
        "&actiontype=" + o.basedOnActionType +
        "&callback=" + o.drawingCallback +
        "&numberOfResults=" + o.numberOfResults +
        ((o.assocType) ? ("&assoctype=" + o.assocType ) : ""));
}

function easyrec_otherUsersAlsoViewed(options) {
    easyrec_getRecommendations("otherusersalsoviewed", options);
}

function easyrec_otherUsersAlsoBought(options) {
    easyrec_getRecommendations("otherusersalsobought", options);
}

function easyrec_itemsRatedGoodByOtherUsers(options) {
    easyrec_getRecommendations("itemsratedgoodbyotherusers", options);
}

function easyrec_recommendationsForUser(options) {
    easyrec_getRecommendations("recommendationsforuser", options);
}

function easyrec_relatedItems(options) {
    easyrec_getRecommendations("relateditems", options);
}

function easyrec_relatedItems(options) {
    easyrec_getRecommendations("actionhistoryforuser", options);
}
///////////////////////////////// Ranking functions /////////////////////////////////////
// for all Ranking functions:
// allowed values for timeRange are: DAY, WEEK, MONTH, ALL
// the functions retrieve the daily, weekly, monthly or all time most acted on items

function easyrec_getRankings(rankingType, options) {
    var o = easyrec_extend(options, defaults);

    easyrec_includeJavascript(easyrecApiUrl + rankingType + "?" +
        "tenantid=" + tenantId +
        "&apikey=" + apiKey +
        "&timeRange=" + o.timeRange +
        "&numberOfResults=" + o.numberOfResults +
        "&requesteditemtype=" + o.requestedItemType +
        "&callback=" + o.drawingCallback);
}

function easyrec_mostViewedItems(options) {
    easyrec_getRankings("mostvieweditems", options);
}

function easyrec_mostBoughtItems(options) {
    easyrec_getRankings("mostboughtitems", options);
}

function easyrec_mostRatedItems(options) {
    easyrec_getRankings("mostrateditems", options);
}

function easyrec_bestRatedItems(options) {
    easyrec_getRankings("bestrateditems", options);
}

function easyrec_worstRatedItems(options) {
    easyrec_getRankings("worstrateditems", options);
}

function easyrec_itemsInCluster(options) {
    var o = easyrec_extend(options, defaults);

    easyrec_includeJavascript(easyrecApiUrl + "itemsincluster?" +
        "tenantId=" + tenantId +
        "&apikey=" + apiKey +
        "&clusterId=" + o.clusterId +
        "&strategy=" + o.strategy +
        "&useFallback=" + o.useFallback +
        "&numberOfResults=" + o.numberOfResults +
        "&requesteditemtype=" + o.requestedItemType +
        "&callback" + o.drawingCallback);
}

function easyrec_cluster(options) {
    var o = easyrec_extend(options, defaults);

    easyrec_includeJavascript(easyrecApiUrl + "clusters?" +
        "tenantId=" + tenantId +
        "&apikey=" + apiKey +
        "&callback" + o.drawingCallback);
}

function easyrec_itemtypes(options) {
    var o = easyrec_extend(options, defaults);

    easyrec_includeJavascript(easyrecApiUrl + "itemtypes?" +
        "tenantId=" + tenantId +
        "&apikey=" + apiKey +
        "&callback" + o.drawingCallback);
}

//////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////// Recommendation DRAWING CALLBACK //////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////

function drawRecommendationList(json) {
    drawRecommendationListToDiv(json, "recommenderDiv");
}

function drawRecommendationListToDiv(json, recommenderDiv) {
    if ("undefined" == typeof(json.error)) { // if no error show recommendations

        try {
            var items = json.recommendeditems.item;
        } catch(e) {
            return;
        }

        /* when the object is already in array format, this block will not execute */
        if ("undefined" == typeof(items.length)) {
            items = new Array(items);
        }

// display recommendations in the DIV layer 'recommendation'
        if (items.length > 0) {
            listString = "<ul>";

            for (x = 0; x < items.length; x++) {
                listString +=
                    "<li><a href=\"" + items[x].url + "\">"
                        + items[x].description +
                        "</a>" +
                        "</li>";
            }
            document.getElementById(recommenderDiv).innerHTML += listString + "</ul>";

        }
    }
}

function drawRecommendationListWithPictures(json) {
    drawRecommendationListWithPicturesToDiv(json, "recommenderDiv");
}

function drawRecommendationListWithPicturesToDiv(json, recommenderDiv) {
    if ("undefined" == typeof(json.error)) { // if no error show recommendations

        try {
            var items = json.recommendeditems.item;
        } catch(e) {
            return;
        }

        /* when the object is already in array format, this block will not execute */
        if ("undefined" == typeof(items.length)) {
            items = new Array(items);
        }

// display recommendations in the DIV layer 'recommendation'
        if (items.length > 0) {
            for (x = 0; x < items.length
                ; x++) {
                document.getElementById(recommenderDiv).innerHTML +=
                    "<div style=\"width:170px;padding:5px;float:left;text-align:center;\">" +
                        "<a href=\"" + items[x].url + "\">" +
                        "<img style=\"width:150px;border:0px;\" alt=\"" + items[x].description + "\"" +
                        " src=\"" + items[x].imageUrl + "\"/><br/>"
                        + items[x].description +
                        "</a>" +
                        "</div>";
            }

        }
    }
}

//////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////// Java Script HELPERS ///////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////

// This function creates a new<SCRIPT>tag with
// the given adress a source.
// (=Dynamic javascript generation)
//
function easyrec_includeJavascript(jsAdr) {
    var newJs = document.createElement('script');
    newJs.type = 'text/javascript';
    newJs.src = jsAdr;
    document.getElementsByTagName('head')[0].appendChild(newJs);
}

function easyrec_extend(options, defaults) {
    var target = options;

    for (var propertyName in defaults) {
        src = target[ propertyName ];
        copy = defaults[ propertyName ];

        if (src != null) {
            continue;
        } else if (copy !== undefined) {
            target[ propertyName ] = copy;
        }
    }
    // Return the modified object
    return target;
}


//Session functions
function easyrec_createSessionId() {
    var name = "easyrec_sessionVar";
    var value = easyrec_generateSessionId(15);
    String((new Date()).getTime()).replace(/\D/gi, '');
    document.cookie = name + "=" + value + "; path=/";
    return value;
}

function easyrec_generateSessionId(length) {
    chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    returnValue = "";

    for (x = 0; x < length; x++) {
        i = Math.floor(Math.random() * 62);
        returnValue += chars.charAt(i);
    }

    return "JS_" + returnValue;
}

function easyrec_getSessionId() {
    var nameEQ = "easyrec_sessionVar=";
    var ca = document.cookie.split(';');
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') c = c.substring(1, c.length);
        if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length, c.length);
    }
    return easyrec_createSessionId();
}
