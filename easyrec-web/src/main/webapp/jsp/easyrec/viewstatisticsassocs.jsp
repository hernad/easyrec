<%@ taglib prefix="texta" uri="/WEB-INF/tagLib.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%--
  ~ Copyright 2010 Research Studios Austria Forschungsgesellschaft mBH
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

<script language="javascript" type="text/javascript" src="${webappPath}/js/jquery/jquery.visualize.js"></script>
<script language="javascript" type="text/javascript" src="${webappPath}/js/enhance.js"></script>
<script language="javascript" type="text/javascript" src="${webappPath}/js/easyrec.js"></script>
<!--[if IE]><script language="javascript" type="text/javascript" src="${webappPath}/js/jquery/excanvas.min.js"></script><![endif]-->

<div class="appendbody">
<jsp:include page="menu.jsp"/>
<table>
<tr>
<td>
<jsp:include page="menubar.jsp"/>
<span class="headlineBig">Statistics for "${tenantId}"</span>

<p>
    The following tables show the activities for the selected tenant.

    <br/><b>Note: </b>The data presented here is automatically updated after every rule generator execution normally scheduled once a day, so it may be a little outdated.<br/>
    <span id="tenantrefreshstatistics">If you want to, you can <a href="javascript:void(0);"
                                                onclick="refreshStatistics('${remoteTenant.stringId}', '${remoteTenant.operatorId}')">refresh the statistics now</a>.</span>
</p>
<table class="stat">
<tr>
    <td><span class="headline">Overview</span>
        <hr/>
    </td>
</tr>
<tr>
    <td>
        <table width="100%">
            <tr style="background-color:#efefef;">
                <td width="68%">
                    number of clicks on recommendations (<span class="conversions">conversions</span>)
                </td>
                <td align="right">
                    ${tenantStatistics.backtracks} (<span
                        class="conversions">${conversionStatistics.recommendationToBuyCount}</span>)
                </td>
                <td width="25px" align="right" style="padding-right: 2px">
                    <img class="clickable" alt="help" src="${webappPath}/img/button_help.png"
                         onclick="$('#div_conversion_help').slideToggle('slow')"/>
                </td>
            </tr>
            <tr class="help">
                <td colspan="3">
                    <div id="div_conversion_help" style="display: none">
                        Clicks on recommendations by users are tracked by easyrec.<br/>
                        In case a user buys an item that he clicked on in a recommendation<br/>
                        before, the number of <span class="conversions">conversions</span> is incremented. These metrics<br/>
                        show the direct impact of easyrec increasing your sales and extending<br/>
                        the time of visits by your customers.
                        <hr/>
                    </div>
                </td>
            </tr>
            <tr>
                <td>number of total actions (this month)</td>
                <td align="right">${tenantStatistics.actions} (${remoteTenant.monthlyActions})</td>
                <td width="25px" align="right" style="padding-right: 2px">
                    <img class="clickable" alt="help" src="${webappPath}/img/button_help.png"
                         onclick="$('#div_monthly_help').slideToggle('slow')"/>
                </td>
            </tr>
            <tr class="help">
                <td colspan="3">
                    <div id="div_monthly_help" style="display: none">
                        These values show the number of total actions (view, buy &amp; rate) and <br/>
                        the number of actions for the current month of this tenant.<br/>
                        Actions older than
                        <c:if test="${remoteTenant.autoArchiverTimeRange%365>0}">
                            <fmt:formatNumber value="${remoteTenant.autoArchiverTimeRange/365 }" maxFractionDigits="2"/>
                        </c:if>
                        <c:if test="${remoteTenant.autoArchiverTimeRange%365==0}">
                            <fmt:formatNumber value="${remoteTenant.autoArchiverTimeRange/365 }" maxFractionDigits="0"/>
                        </c:if>
                        years are considered outdated and moved to the action<br/>
                        archive. Actions in the archive will not be analyzed by the plugins for use<br/>
                        in recommendations.
                        <hr/>
                    </div>
                </td>
            </tr>
            <tr style="background-color:#efefef;">
                <td>number of maximum allowed actions per month</td>
                <td align="right">
                    <c:if test="${remoteTenant.maxActions>0}">
                        ${remoteTenant.maxActions}
                    (${remoteTenant.limitReachedBy}% reached)
                </td>
                </c:if>
                <c:if test="${remoteTenant.maxActions==0}">unlimited</c:if>
                <td width="25px" align="right" style="padding-right: 2px">
                    <img class="clickable" alt="help" src="${webappPath}/img/button_help.png"
                         onclick="$('#div_maximum_help').slideToggle('slow')"/>
                </td>
            </tr>
            <tr class="help">
                <td colspan="3">
                    <div id="div_maximum_help" style="display: none">
                        This value shows the percentage reached of maximum allowed<br/>
                        actions per month. If the limit is exceeded, easyrec will not display <br/>
                        recommendations until the end of the next month.<br/>
                        You may then download easyrec from<br/>
                        <a target="_blank" href="${updateUrl}">${updateUrl}</a><br/>
                        and host easyrec on your own server.<%-- or consult <a target="_blank" href="${easyrecBiz}">${easyrecBiz}</a> for<br />
                        upgrading options.--%>
                        <hr/>
                    </div>
                </td>
            </tr>
            <tr>
                <td>number of total items</td>
                <td align="right">${tenantStatistics.items}</td>
                <td width="25px" align="right" style="padding-right: 2px">
                    <img class="clickable" alt="help" src="${webappPath}/img/button_help.png"
                         onclick="$('#div_items_help').slideToggle('slow')"/>
                </td>
            </tr>
            <tr class="help">
                <td colspan="3">
                    <div id="div_items_help" style="display: none">
                        The number of unique item ids that were submitted<br/>
                        with action calls through the
                        <a href="${webappPath}/API">REST-API</a>.
                        <hr/>
                    </div>
                </td>
            </tr>
            <tr style="background-color:#efefef;">
                <td>number of total users</td>
                <td align="right">${tenantStatistics.users}</td>
                <td width="25px" align="right" style="padding-right: 2px">
                    <img class="clickable" alt="help" src="${webappPath}/img/button_help.png"
                         onclick="$('#div_users_help').slideToggle('slow')"/>
                </td>
            </tr>
            <tr class="help">
                <td colspan="3">
                    <div id="div_users_help" style="display: none">
                        The number of unique user ids that were submitted<br/>
                        with action calls through the
                        <a href="${webappPath}/API">REST-API</a>.
                        <hr/>
                    </div>
                </td>
            </tr>
            <tr>
                <td>average actions per user</td>
                <td align="right">${tenantStatistics.averageActionsPerUser}</td>
                <td width="25px" align="right" style="padding-right: 2px">
                    <img class="clickable" alt="help" src="${webappPath}/img/button_help.png"
                         onclick="$('#div_averageActionsPerUser_help').slideToggle('slow')"/>
                </td>
            </tr>
            <tr class="help">
                <td colspan="3">
                    <div id="div_averageActionsPerUser_help" style="display: none">
                        The is the number of average actions a user does, when visiting your website.
                        <hr/>
                    </div>
                </td>
            </tr>
            <tr style="background-color:#efefef;">
                <td>recommendation coverage</td>
                <td align="right">${tenantStatistics.recommendationCoverage} %</td>
                <td width="25px" align="right" style="padding-right: 2px">
                    <img class="clickable" alt="help" src="${webappPath}/img/button_help.png"
                         onclick="$('#div_recommendationCoverage_help').slideToggle('slow')"/>
                </td>
            </tr>
            <tr class="help">
                <td colspan="3">
                    <div id="div_recommendationCoverage_help" style="display: none">
                        The recommendation coverage is the probability that if a random user views an item,<br/>
                        he will also get recommendations for the viewed item.<br/>
                        <table style="width: 500px;">
                            <tr>
                                <td>recommendation coverage (%) = 100 *</td>
                                <td>
                                    <table style="width: 300px;">
                                        <tr style="border-bottom: solid 1px #454545;">
                                            <td valign="baseline" align="center">number of total actions on items with
                                                rules
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="top" align="center">number of total actions</td>
                                        </tr>
                                    </table>
                                </td>
                            </tr>
                        </table>
                        <hr/>
                    </div>
                </td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td>&nbsp;</td>
</tr>
<tr>
    <td><span class="headline">Number of Users who made</span>
        <hr/>
    </td>
</tr>
<tr>
    <td>
        <table width="100%">
            <tr style="background-color:#efefef;">
                <td width="68%">1 action</td>
                <td align="right">${userStatistics.users_with_1_action}</td>
                <td width="25px;"></td>
            </tr>
            <tr>
                <td>2 actions</td>
                <td align="right">${userStatistics.users_with_2_actions}</td>
                <td></td>
            </tr>
            <tr style="background-color:#efefef;">
                <td>3 - 10 actions</td>
                <td align="right">${userStatistics.users_with_3_10_actions}</td>
                <td></td>
            </tr>
            <tr>
                <td>11 - 100 actions</td>
                <td align="right">${userStatistics.users_with_11_100_actions}</td>
                <td></td>
            </tr>
            <tr style="background-color:#efefef;">
                <td>101 and more actions</td>
                <td align="right">${userStatistics.users_with_101_and_more_actions}</td>
                <td></td>
            </tr>
        </table>
        <br/>
        <table id="users">
            <thead>
            <tr>
                <td></td>
            </tr>
            </thead>
            <tbody>
            <tr>
                <th scope="row">1 action</th>
                <td>${userStatistics.users_with_1_action}</td>
            </tr>
            <tr>
                <th scope="row">2 actions</th>
                <td>${userStatistics.users_with_2_actions}</td>
            </tr>
            <tr>
                <th scope="row">3-10</th>
                <td>${userStatistics.users_with_3_10_actions}</td>
            </tr>
            <tr>
                <th scope="row">11-100</th>
                <td>${userStatistics.users_with_11_100_actions}</td>
            </tr>
            <tr>
                <th scope="row">&gt; 100 actions</th>
                <td>${userStatistics.users_with_101_and_more_actions}</td>
            </tr>
            </tbody>
        </table>
    </td>
</tr>

<tr>
    <td><br/><span class="headlineBig">Rule Statistics</span></td>
</tr>

 <%----
         The automated Plugins statistics are generated here we use a taglib to generate a table from a XML string (log entry)

         the help data is loaded via an AJAX call.
   ----%>

   <c:forEach var="statistic" items="${assocTypeToStatistic}">
        <table class="stat">
        <tr>
            <td>
                &nbsp;
            </td>
            <td>
                &nbsp;
            </td>
        </tr>
        <tr style="border-bottom: 2px solid #C0C0C0;height:29px;">
            <td>
                <span class="headline">${fn:replace(fn:toLowerCase(statistic.key), "_", " ")} (provided by ${pluginRealName[statistic.key]})</span>
            </td>
            <td width="25px" align="right" style="padding-right: 2px">
                <img class="clickable" alt="help" src="${webappPath}/img/button_help.png"
                     onclick="$('#div_${statistic.key}_help').slideToggle('slow')"/>
            </td>
        </tr>
        <tr class="help">
            <td colspan="3">
                <div id="div_${statistic.key}_help" style="display: none;width:512px;">
                    <script>
                        showPluginDescription('${operatorId}', '${tenantId}', '${assocTypeToPlugin[statistic.key].uri}',
                                '${assocTypeToPlugin[statistic.key].version}', 'div_${statistic.key}_help');
                    </script>
                    <hr/>
                </div>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <texta:generatorXmlStats xml="<verbose>${statistic.value}</verbose>"/>
            </td>
        </tr>
    </table>

   </c:forEach>

 <%---- end  ----%>


<table class="stat">
    <tr>
        <td><br/><span class="headline">distribution of association values</span>
            <hr/>
        </td>
    </tr>
    <tr>
        <td><span>number of items with rules that have a association value:</span></td>
    </tr>
    <tr>
        <td>

            <table width="100%">
                <tr style="background-color:#efefef;">
                    <td width="23px;">&lt;</td>
                    <td>20 %</td>
                    <td align="right">${ruleMinerStatistics.group1}</td>
                    <td width="25px;"></td>
                </tr>
                <tr>
                    <td>20 -</td>
                    <td>40 %</td>
                    <td align="right">${ruleMinerStatistics.group2}</td>
                    <td/>
                </tr>
                <tr style="background-color:#efefef;">
                    <td>40 -</td>
                    <td>60 %</td>
                    <td align="right">${ruleMinerStatistics.group3}</td>
                    <td/>
                </tr>
                <tr>
                    <td>60 -</td>
                    <td>80 %</td>
                    <td align="right">${ruleMinerStatistics.group4}</td>
                    <td/>
                </tr>
                <tr style="background-color:#efefef;">
                    <td>&gt;</td>
                    <td>80 %</td>
                    <td align="right">${ruleMinerStatistics.group5}</td>
                    <td/>
                </tr>
            </table>
        </td>
    </tr>
</table>
</td>
</tr>
<tr>
    <td><br/>
        <table id="rules">
            <thead>
            <tr>
                <td></td>
            </tr>
            </thead>
            <tbody>
            <tr>
                <th scope="row">&lt; 20%</th>
                <td>${ruleMinerStatistics.group1}</td>
            </tr>
            <tr>
                <th scope="row">20-40%</th>
                <td>${ruleMinerStatistics.group2}</td>
            </tr>
            <tr>
                <th scope="row">40-60%</th>
                <td>${ruleMinerStatistics.group3}</td>
            </tr>
            <tr>
                <th scope="row">60-80%</th>
                <td>${ruleMinerStatistics.group4}</td>
            </tr>
            <tr>
                <th scope="row">&gt; 80%</th>
                <td>${ruleMinerStatistics.group5}</td>
            </tr>
            </tbody>
        </table>
    </td>
</tr>
<tr>
    <td><span>
                * The association value defines the 'quality' of a rule.
        </span>
    </td>
</tr>
</table>


<script type="text/javascript">
    // http://www.filamentgroup.com/lab/update_to_jquery_visualize_accessible_charts_with_html5_from_designing_with
    $(function()
    {

        $('#rules').hide();
        $('#rules').visualize({
            type: 'pie',
            height: '300px',
            width: '590px',
            colors: ['#cf2c2c','#cf622c','#cfb32c','#9ccf2c','#3ccf66']
        });

        $('#users').hide();
        $('#users').visualize({
            type: 'pie',
            height: '300px',
            width: '590px',
            colors: ['#cf2c2c','#cf622c','#cfb32c','#9ccf2c','#3ccf66']
        });
    });
</script>
<br/><br/><br/><br/><br/><br/><br/>
</div>
 