<%@ taglib prefix="easyrec" uri="/WEB-INF/tagLib.tld" %>
<%--
  ~ Copyright 2011 Research Studios Austria Forschungsgesellschaft mBH
  ~
  ~ This file is part of easyrec.
  ~
  ~ easyrec is free software: you can redistribute it and/or modify
  ~ it under the terms of the GNU General Public License as published by
  ~ the Free Software Foundation, either version 3 of the License, or
  ~ (at your option) any later version.
  ~
  ~ easyrec is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ GNU General Public License for more details.
  ~
  ~ You should have received a copy of the GNU General Public License
  ~ along with easyrec.  If not, see <http://www.gnu.org/licenses/>.
  --%>

<div class="appendbody">
<h1>Recommendation API</h1>

<p>
    We provide full access to easyrec's functionality through a <easyrec:wikiLink name="REST API"
                                                                                  pageName="REST_API_v0.97"/>.
    Recommendations are returned in XML or JSON notation.You can also include a small snippet of
    <easyrec:wikiLink name="javascript code" pageName="JavaScript_API_v0.97"/> in your website to get
    recommendations. To get started with easyrec we recommend to read the <easyrec:wikiLink name="get started guide"/>
    and if you are completely lost feel free to ask us at the <a href="http://sourceforge.net/apps/phpbb/easyrec/"
                                                                 target="_blank">forums</a>.
</p>

<br/>

<br/><br/>
<table width="100%">
    <tr>
        <td colspan="2"><span class="headline">Actions</span>
            <hr>
        </td>
    </tr>
    <tr style="background-color: rgb(239, 239, 239);">
        <td>
            view
        </td>
        <td style="width:200px;height:40px;">
            <a target="_blank" href="http://sourceforge.net/apps/mediawiki/easyrec/index.php?title=REST_API_v0.97#view">
                <img style="" alt="wiki link" src="${webappPath}/img/button_wiki-rest.png"/>
            </a>
            <a target="_blank"
               href="http://sourceforge.net/apps/mediawiki/easyrec/index.php?title=JavaScript_API_v0.97#Sending_Actions">
                <img style="" alt="wiki link" src="${webappPath}/img/button_wiki-js.png"/>
            </a>
        </td>
    </tr>
    <tr>
        <td>
            buy
        </td>
        <td>
            <a target="_blank" href="http://sourceforge.net/apps/mediawiki/easyrec/index.php?title=REST_API_v0.97#buy">
                <img style="" alt="wiki link" src="${webappPath}/img/button_wiki-rest.png"/>
            </a>
            <a target="_blank"
               href="http://sourceforge.net/apps/mediawiki/easyrec/index.php?title=JavaScript_API_v0.97#Sending_Actions">
                <img style="" alt="wiki link" src="${webappPath}/img/button_wiki-js.png"/>
            </a>
        </td>
    </tr>
    <tr style="background-color: rgb(239, 239, 239);">
        <td>
            rate
        </td>
        <td>
            <a target="_blank" href="http://sourceforge.net/apps/mediawiki/easyrec/index.php?title=REST_API_v0.97#rate">
                <img style="" alt="wiki link" src="${webappPath}/img/button_wiki-rest.png"/>
            </a>
            <a target="_blank"
               href="http://sourceforge.net/apps/mediawiki/easyrec/index.php?title=JavaScript_API_v0.97#Sending_Actions">
                <img style="" alt="wiki link" src="${webappPath}/img/button_wiki-js.png"/>
            </a>
        </td>
    </tr>
    <tr>
        <td>
            sendaction
        </td>
        <td>
            <a target="_blank" href="http://sourceforge.net/apps/mediawiki/easyrec/index.php?title=REST_API_v0.97#sendaction">
                <img style="" alt="wiki link" src="${webappPath}/img/button_wiki-rest.png"/>
            </a>
            <a target="_blank"
               href="http://sourceforge.net/apps/mediawiki/easyrec/index.php?title=JavaScript_API_v0.97#Sending_Actions">
                <img style="" alt="wiki link" src="${webappPath}/img/button_wiki-js.png"/>
            </a>
        </td>
    </tr>
</table>

<br/>

<table width="100%">
    <tr>
        <td colspan="2"><span class="headline">Recommendations</span>
            <hr>
        </td>
    </tr>
    <tr style="background-color: rgb(239, 239, 239);">
        <td>
            other users also viewed
        </td>
        <td style="width:200px;">
            <a target="_blank"
               href="http://sourceforge.net/apps/mediawiki/easyrec/index.php?title=REST_API_v0.97#other_users_also_viewed">
                <img style="" alt="wiki link" src="${webappPath}/img/button_wiki-rest.png"/>
            </a>
            <a target="_blank"
               href="http://sourceforge.net/apps/mediawiki/easyrec/index.php?title=JavaScript_API_v0.97#Receiving_Recommendations">
                <img style="" alt="wiki link" src="${webappPath}/img/button_wiki-js.png"/>
            </a>
        </td>
    </tr>
    <tr>
        <td>
            other users also bought
        </td>
        <td>
            <a target="_blank"
               href="http://sourceforge.net/apps/mediawiki/easyrec/index.php?title=REST_API_v0.97#other_users_also_bought">
                <img style="" alt="wiki link" src="${webappPath}/img/button_wiki-rest.png"/>
            </a>
            <a target="_blank"
               href="http://sourceforge.net/apps/mediawiki/easyrec/index.php?title=JavaScript_API_v0.97#Receiving_Recommendations">
                <img style="" alt="wiki link" src="${webappPath}/img/button_wiki-js.png"/>
            </a>
        </td>
    </tr>
    <tr style="background-color: rgb(239, 239, 239);">
        <td>
            items rated good by other users
        </td>
        <td>
            <a target="_blank"
               href="http://sourceforge.net/apps/mediawiki/easyrec/index.php?title=REST_API_v0.97#items_rated_good_by_other_users">
                <img style="" alt="wiki link" src="${webappPath}/img/button_wiki-rest.png"/>
            </a>
            <a target="_blank"
               href="http://sourceforge.net/apps/mediawiki/easyrec/index.php?title=JavaScript_API_v0.97#Receiving_Recommendations">
                <img style="" alt="wiki link" src="${webappPath}/img/button_wiki-js.png"/>
            </a>
        </td>
    </tr>
    <tr>
        <td>
            recommendations for user
        </td>
        <td>
            <a target="_blank"
               href="http://sourceforge.net/apps/mediawiki/easyrec/index.php?title=REST_API_v0.97#recommendations_for_user">
                <img style="" alt="wiki link" src="${webappPath}/img/button_wiki-rest.png"/>
            </a>
            <a target="_blank"
               href="http://sourceforge.net/apps/mediawiki/easyrec/index.php?title=JavaScript_API_v0.97#Receiving_Recommendations">
                <img style="" alt="wiki link" src="${webappPath}/img/button_wiki-js.png"/>
            </a>
        </td>
    </tr>
    <tr style="background-color: rgb(239, 239, 239);">
        <td>
            related items
        </td>
        <td>
            <a target="_blank"
               href="http://sourceforge.net/apps/mediawiki/easyrec/index.php?title=REST_API_v0.97#related_items">
                <img style="" alt="wiki link" src="${webappPath}/img/button_wiki-rest.png"/>
            </a>
            <a target="_blank"
               href="http://sourceforge.net/apps/mediawiki/easyrec/index.php?title=JavaScript_API_v0.97#Receiving_Recommendations">
                <img style="" alt="wiki link" src="${webappPath}/img/button_wiki-js.png"/>
            </a>
        </td>
    </tr>
    <tr>
        <td>
            action history for user
        </td>
        <td>
            <a target="_blank"
               href="http://sourceforge.net/apps/mediawiki/easyrec/index.php?title=REST_API_v0.97#action_history_for_user">
                <img style="" alt="wiki link" src="${webappPath}/img/button_wiki-rest.png"/>
            </a>
            <a target="_blank"
               href="http://sourceforge.net/apps/mediawiki/easyrec/index.php?title=JavaScript_API_v0.97#Receiving_Recommendations">
                <img style="" alt="wiki link" src="${webappPath}/img/button_wiki-js.png"/>
            </a>
        </td>
    </tr>
</table>

<br/>

<table width="100%">
    <tr>
        <td colspan="2"><span class="headline">Item Type</span>
            <hr>
        </td>
    </tr>
    <tr style="background-color: rgb(239, 239, 239);">
        <td>
            item types
        </td>
        <td style="width:200px;">
            <a target="_blank"
               href="http://sourceforge.net/apps/mediawiki/easyrec/index.php?title=REST_API_v0.97#item_types">
                <img style="" alt="wiki link" src="${webappPath}/img/button_wiki-rest.png"/>
            </a>
            <a target="_blank"
               href="http://sourceforge.net/apps/mediawiki/easyrec/index.php?title=JavaScript_API_v0.97#Receiving_Itemtypes">
                <img style="" alt="wiki link" src="${webappPath}/img/button_wiki-js.png"/>
            </a>
        </td>
    </tr>
</table>

<br/>

<table width="100%">
    <tr>
        <td colspan="2"><span class="headline">Cluster</span>
            <hr>
        </td>
    </tr>
    <tr style="background-color: rgb(239, 239, 239);">
        <td>
            clusters
        </td>
        <td style="width:200px;">
            <a target="_blank"
               href="http://sourceforge.net/apps/mediawiki/easyrec/index.php?title=REST_API_v0.97#clusters">
                <img style="" alt="wiki link" src="${webappPath}/img/button_wiki-rest.png"/>
            </a>
            <a target="_blank"
               href="http://sourceforge.net/apps/mediawiki/easyrec/index.php?title=JavaScript_API_v0.97#Receiving_Cluster_Related_Information">
                <img style="" alt="wiki link" src="${webappPath}/img/button_wiki-js.png"/>
            </a>
        </td>
    </tr>

    <tr>
        <td>
            items of cluster
        </td>
        <td style="width:200px;">
            <a target="_blank"
               href="http://sourceforge.net/apps/mediawiki/easyrec/index.php?title=REST_API_v0.97#items_of_cluster">
                <img style="" alt="wiki link" src="${webappPath}/img/button_wiki-rest.png"/>
            </a>
            <a target="_blank"
               href="http://sourceforge.net/apps/mediawiki/easyrec/index.php?title=JavaScript_API_v0.97#Receiving_Cluster_Related_Information">
                <img style="" alt="wiki link" src="${webappPath}/img/button_wiki-js.png"/>
            </a>
        </td>
    </tr>
</table>

<br/>

<table width="100%">
    <tr>
        <td colspan="2"><span class="headline">Community Rankings</span>
            <hr>
        </td>
    </tr>
    <tr style="background-color: rgb(239, 239, 239);">
        <td>
            most viewed items
        </td>
        <td style="width:200px;">
            <a target="_blank"
               href="http://sourceforge.net/apps/mediawiki/easyrec/index.php?title=REST_API_v0.97#most_viewed_items">
                <img style="" alt="wiki link" src="${webappPath}/img/button_wiki-rest.png"/>
            </a>
            <a target="_blank"
               href="http://sourceforge.net/apps/mediawiki/easyrec/index.php?title=JavaScript_API_v0.97#Receiving_Rankings">
                <img style="" alt="wiki link" src="${webappPath}/img/button_wiki-js.png"/>
            </a>
        </td>
    </tr>
    <tr>
        <td>
            most bought items
        </td>
        <td>
            <a target="_blank"
               href="http://sourceforge.net/apps/mediawiki/easyrec/index.php?title=REST_API_v0.97#most_bought_items">
                <img style="" alt="wiki link" src="${webappPath}/img/button_wiki-rest.png"/>
            </a>
            <a target="_blank"
               href="http://sourceforge.net/apps/mediawiki/easyrec/index.php?title=JavaScript_API_v0.97#Receiving_Rankings">
                <img style="" alt="wiki link" src="${webappPath}/img/button_wiki-js.png"/>
            </a>
        </td>
    </tr>
    <tr style="background-color: rgb(239, 239, 239);">
        <td>
            most rated items
        </td>
        <td>
            <a target="_blank"
               href="http://sourceforge.net/apps/mediawiki/easyrec/index.php?title=REST_API_v0.97#most_rated_items">
                <img style="" alt="wiki link" src="${webappPath}/img/button_wiki-rest.png"/>
            </a>
            <a target="_blank"
               href="http://sourceforge.net/apps/mediawiki/easyrec/index.php?title=JavaScript_API_v0.97#Receiving_Rankings">
                <img style="" alt="wiki link" src="${webappPath}/img/button_wiki-js.png"/>
            </a>
        </td>
    </tr>
    <tr>
        <td>
            best rated items
        </td>
        <td>
            <a target="_blank"
               href="http://sourceforge.net/apps/mediawiki/easyrec/index.php?title=REST_API_v0.97#best_rated_items">
                <img style="" alt="wiki link" src="${webappPath}/img/button_wiki-rest.png"/>
            </a>
            <a target="_blank"
               href="http://sourceforge.net/apps/mediawiki/easyrec/index.php?title=JavaScript_API_v0.97#Receiving_Rankings">
                <img style="" alt="wiki link" src="${webappPath}/img/button_wiki-js.png"/>
            </a>
        </td>
    </tr>
    <tr style="background-color: rgb(239, 239, 239);">
        <td>
            worst rated items
        </td>
        <td>
            <a target="_blank"
               href="http://sourceforge.net/apps/mediawiki/easyrec/index.php?title=REST_API_v0.97#worst_rated_items">
                <img style="" alt="wiki link" src="${webappPath}/img/button_wiki-rest.png"/>
            </a>
            <a target="_blank"
               href="http://sourceforge.net/apps/mediawiki/easyrec/index.php?title=JavaScript_API_v0.97#Receiving_Rankings">
                <img style="" alt="wiki link" src="${webappPath}/img/button_wiki-js.png"/>
            </a>
        </td>
    </tr>
</table>

<br/>
<table width="100%">
    <tr>
        <td colspan="2"><span class="headline">Import API</span>
            <hr>
        </td>
    </tr>
    <tr style="background-color: rgb(239, 239, 239);">
        <td>
            Import rule
        </td>
        <td style="width:200px;">
            <a target="_blank"
               href="http://sourceforge.net/apps/mediawiki/easyrec/index.php?title=REST_API_v0.97#Import_rule">
                <img style="" alt="wiki link" src="${webappPath}/img/button_wiki-rest.png"/>
            </a>
        </td>
    </tr>
    <tr>
        <td>
            Import/update item
        </td>
        <td>
            <a target="_blank"
               href="http://sourceforge.net/apps/mediawiki/easyrec/index.php?title=REST_API_v0.97#Import.2Fupdate_item">
                <img style="" alt="wiki link" src="${webappPath}/img/button_wiki-rest.png"/>
            </a>
        </td>
    </tr>
    <tr style="background-color: rgb(239, 239, 239);">
        <td>
            set item active
        </td>
        <td>
            <a target="_blank"
               href="http://sourceforge.net/apps/mediawiki/easyrec/index.php?title=REST_API_v0.97#set_item_active">
                <img style="" alt="wiki link" src="${webappPath}/img/button_wiki-rest.png"/>
            </a>
        </td>
    </tr>
</table>

</div>

