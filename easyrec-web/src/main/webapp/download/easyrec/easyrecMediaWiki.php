<?php

/*

$Author: phlavac $
$Revision: 13590 $
$Date: 2009-02-27 13:27:30 +0100 (Fr, 27 Feb 2009) $

MediaWiki easyRec Plugin

Author : David Mann, http://sat.researchstudio.at/

Version : 0.9.51


This library is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public
License as published by the Free Software Foundation; either
version 3.0 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
*/


// Set extension properties.
$wgExtensionCredits['other'][] = array("name" => "easyRec", "author" => "David Mann <david.mann@researchstudio.at>",
    "description" => "Interface for sending and receiving recommendations");

$wgHooks['BeforePageDisplay'][] = 'renderEasyRec';

$easyRecText = array();
$easyRecText['en'] = "People who read this article were also interested in";
$easyRecText['fr'] = "Personnes ayant lu cet article ont �galement lu";
$easyRecText['de'] = "Leute die diesen Artikel gelesen haben, haben sich auch f&uuml;r folgende Artikel interessiert";
$easyRecText['pt'] = "Clientes que pesquisaram este �tem(produto), tamb�m pesquisaram";
$easyRecText['pt-br'] = "Clientes que pesquisaram este �tem(produto), tamb�m pesquisaram";
$easyRec_max_recommendations = 5;

if (!isset($_COOKIE['wikidbUserID'])) {
    $_COOKIE['wikidbUserID'] = "";
}
if (!isset($_COOKIE['easyRec_sessionid'])) {
    $_COOKIE['easyRec_sessionid'] = "";
}


// This function displays articles that were viewed by other users on current page.
//
function renderEasyRec($out, $sk = null) {

    if ($out->isArticle()) {
        includeRecommenderJavaScript($out);
        easyRec_action_view($out, $out->mPagetitle, $out->mPagetitle);
        easyRec_otherUsersAlsoViewed($out);
    }
    return true;
}


// This function is used to anonmynize user id and session id before
// it is send to easyRec. The ids are anonmynized with the MD5
// hash and salted with your easyrec_id.
//
// Feel free to replace this function with your own "anomynizer"
function anonymizeData($data) {
    global $easyrec_id;
    if ($data != "") {
        return urlencode(md5($data . "��" . $easyrec_id));
    }
    return $data;
}


// =========================================================================================================================================================
//			DO NOT CHANGE ANYTHING HERE
// =========================================================================================================================================================


//Config Stuff
$recommenderJsUrl = $easyrec_api . "?mediaWikiPluginVersion=" . urlencode("\$Revision: 13590 $");


function includeRecommenderJavaScript($out) {
    global $recommenderJsUrl, $easyrec_id, $easyrec_apiKey;
    //$out->addHTML("<script src='$recommenderJsUrl' type='text/javascript'></script>");
    $out->addHTML("<script type='text/javascript'>");
    $out->addHTML("var tenantId='$easyrec_id';");
    $out->addHTML("var apiKey='$easyrec_apiKey';");
    $out->addHTML("function addLoadEvent(func) {");
    $out->addHTML("  var oldonload = window.onload;");
    $out->addHTML("  if (typeof window.onload != 'function') {");
    $out->addHTML("    window.onload = func;");
    $out->addHTML("  } else {");
    $out->addHTML("    window.onload = function() {");
    $out->addHTML("     if (oldonload) {");
    $out->addHTML("      oldonload();");
    $out->addHTML("     }");
    $out->addHTML("     func();");
    $out->addHTML("    }");
    $out->addHTML("  }");
    $out->addHTML("}\n");


    $out->addHTML("function loadJs(){");
    $out->addHTML("var newJs = document.createElement('script');");
    $out->addHTML("newJs.type = 'text/javascript';");
    $out->addHTML("newJs.src = '$recommenderJsUrl';");
    $out->addHTML("document.getElementsByTagName('head')[0].appendChild(newJs);");
    $out->addHTML("}\n");
    $out->addHTML("addLoadEvent(loadJs);\n");
    $out->addHTML("</script>");
}

// This functions lets easyrec know, which article was viewed.
function easyRec_action_view($out, $itemId, $itemDescription) {

    $userId = getUserName();
    $itemId = urlencode($itemId);
    $sessionId = getSessionId();
    $itemUrl = $_SERVER["REQUEST_URI"];
    $itemDescription = urlencode($itemDescription);
    $itemImageUrl = "";

    $out->addHTML("<script type='text/javascript'>");
    $out->addHTML("function callView(){");
    $out->addHTML("setTimeout(\"viewItem('$userId','$itemUrl','$sessionId','$itemUrl','$itemDescription','$itemImageUrl')\",100);");
    $out->addHTML("}\n");
    $out->addHTML("addLoadEvent(callView);");
    $out->addHTML("</script>");
}

// This functions asks easyrec, if there are articles viewed by other users to this one.
function easyRec_otherUsersAlsoViewed($out) {

    global $wgLang, $easyRecText;

    $itemUrl = $_SERVER["REQUEST_URI"];

    $easyRecHeadline = $easyRecText[$wgLang->getCode()];
    if ($easyRecHeadline == "") {
        $easyRecHeadline = $easyRecText['en'];
    }
    $out->addHTML("<div id='recommenderDiv' style='display:none;'>");
    $out->addHTML("<br/><br/><hr/><h3><span class='mw-headline'> " . $easyRecHeadline . " </span></h3>");
    $out->addHTML("</div>");

    $userId = getUserName();
    $out->addHTML("<script type='text/javascript'>");
    $out->addHTML("function callViewSimilar(){");
    $out->addHTML("setTimeout(\"callViewSimilarItems('$userId','$itemUrl')\",100);");
    $out->addHTML("}\n");
    $out->addHTML("addLoadEvent(callViewSimilar);");
    $out->addHTML("</script>");

}


// This function returns the session id.
//
function getSessionId() {
    $returnValue = session_id();

    if ($_COOKIE['wikidbUserID'] == "" && $returnValue != "") {
        //user not logged in	but has a session
        $returnValue = "";
    }
    if ($_COOKIE['wikidbUserID'] != "") {
        setcookie("easyRec_sessionid", "", -3600);
    }

    if ($returnValue == "") {
        $returnValue = $_COOKIE['easyRec_sessionid'];
        if ($returnValue == "") {
            $returnValue = md5(time());
            // create cookie that is valid for one session
            setcookie("easyRec_sessionid", $returnValue);
        }
    }
    return anonymizeData($returnValue . getUserName());
}


// This function returns the anomynized user id,
// if the user is logged in, otherwise an empty string.
//
function getUserName() {
    global $wgUser;
    $returnValue = $wgUser->getName();

    if ($_COOKIE['wikidbUserID'] == "" && $returnValue != "") {
        //user not logged in	but has a session
        $returnValue = "";
    }
    return anonymizeData($returnValue);
}


?>