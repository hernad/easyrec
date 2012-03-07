<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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

<script language="javascript" type="text/javascript" src="${webappPath}/js/jquery/jquery.flot.js"></script>
<!--[if IE]><script language="javascript" type="text/javascript" src="${webappPath}/js/jquery/excanvas.min.js"></script><![endif]-->


<span class="headlineBig">Tenant Management</span><br/>

<p>
    This chart shows the number of user actions per day for the selected tenant. Use the drop down menu to select the time
    range of the chart. For advanced statistics about your tenant check 'statistics' in the menu above.
    The <a href="javascript:followingSteps('${tenant.stringId}')">integration information</a> box bundles all you need to integrate easyrec into your website.
</p>

<c:if test="${displayingDefaultTenant}">
    <div class="info">
        <b>Note:</b> This is your automatically generated sandbox tenant. Use this tenant to play around with easyrec. Once you
        understand the basic principles of easyrec, create your own tenant and integrate it into your website.

        You can use the following links to generate actions on your sandbox tenant:

        <a href="javascript:simulateViewAction()">generate a view action</a> |
        <a href="javascript:simulateBuyAction()">generate a buy action</a> |
        <a href="javascript:simulateRateAction()">generate a rate action</a>.

        The <easyrec:wikiLink name="getting started guide" pageName="Getting Started" /> gives you a quick overview on how
        to set up a tenant.

    </div>
</c:if>

<dl>
    <dt>tenant ID:</dt>
    <dd>${tenant.stringId}</dd>

    <dt>creation date:</dt>
    <dd> ${tenant.creationDate}</dd>

    <dt>website of the tenant:</dt>
    <dd><a target="_blank" href="${tenant.url}">${tenant.url}</a></dd>
</dl>

<h3>Description</h3>
<c:if test="${tenant.description!=''}">
    <p style="overflow:auto;width:700px">${tenant.description}</p>
</c:if>


<br>
Showing
<select
        id="actionType"
        name="actionType"
        onchange="javascript:updateTenantFlot(${tenant.id})">
    <option value="">all actions</option>
    <option value="VIEW">view actions</option>
    <option value="BUY">buy actions</option>
    <option value="RATE">rate actions</option>
    <option value="CLICKS_ON_RECS">clicks on recommendations</option>
    <option value="CLICKS_ON_CHARTS">clicks on rankings</option>
</select>
in
<select
        id="month"
        name="month"
        onchange="javascript:updateTenantFlot(${tenant.id})">
    <option
            <c:if test="${currentMonthName=='January'}">selected="selected"</c:if> value="0">January
    </option>
    <option
            <c:if test="${currentMonthName=='February'}">selected="selected"</c:if> value="1">
        February
    </option>
    <option
            <c:if test="${currentMonthName=='March'}">selected="selected"</c:if> value="2">March
    </option>
    <option
            <c:if test="${currentMonthName=='April'}">selected="selected"</c:if> value="3">April
    </option>
    <option
            <c:if test="${currentMonthName=='May'}">selected="selected"</c:if> value="4">May
    </option>
    <option
            <c:if test="${currentMonthName=='June'}">selected="selected"</c:if> value="5">June
    </option>
    <option
            <c:if test="${currentMonthName=='July'}">selected="selected"</c:if> value="6">July
    </option>
    <option
            <c:if test="${currentMonthName=='August'}">selected="selected"</c:if> value="7">August
    </option>
    <option
            <c:if test="${currentMonthName=='September'}">selected="selected"</c:if> value="8">
        September
    </option>
    <option
            <c:if test="${currentMonthName=='October'}">selected="selected"</c:if> value="9">October
    </option>
    <option
            <c:if test="${currentMonthName=='November'}">selected="selected"</c:if> value="10">
        November
    </option>
    <option
            <c:if test="${currentMonthName=='December'}">selected="selected"</c:if> value="11">
        December
    </option>
</select>
<select
        id="year"
        name="year"
        onchange="javascript:updateTenantFlot(${tenant.id})">
    <option value="${currentYear}">${currentYear}</option>
    <option value="${currentYear-1}">${currentYear-1}</option>
    <option value="${currentYear-2}">${currentYear-2}</option>
    <option value="${currentYear-3}">${currentYear-3}</option>
    <option value="${currentYear-4}">${currentYear-4}</option>
</select>
<img id="updateflotwait" alt="wait" src="${webappPath}/img/blank.gif"/>

<!-- begin flot chart -->
<table>
    <tr>
        <td id="legend_actions" colspan="2"
            style="font-size:11px;font-family:sans-serif;color:#666666;display: none;"><br/>#actions
        </td>
    </tr>
    <tr>
        <td colspan="2" style="font-size:11px;font-family:sans-serif;color:#666666" valign="bottom">
            <div id="placeholder${tenant.id}" style="width:670px;height:200px;"></div>
        </td>
        <td id="legend_days" colspan="2"
            style="font-size:11px;font-family:sans-serif;color:#666666;display: none;" valign="bottom">days
        </td>
    </tr>
    <!-- end flot chart -->
    <tr>
        <td style="height: 20px">&nbsp;</td>
    </tr>
</table>

<div id="legendContainer">

</div>

<script type="text/javascript">
    $(function () {
        updateTenantFlot(${tenant.id});
    });

    <c:if test="${displayingDefaultTenant}">
        function simulateViewAction() {

            $.ajax({
                url:webappPath +
                        "api/1.0/view?apikey=${apiKey}&tenantid=EASYREC_DEMO&itemid=42&itemdescription=Fatboy Slim - The Rockafeller Skank&itemurl=/item/fatboyslim&itemimageurl=${extendedWebappPath}/img/covers/fatboyslim.jpg&userid=B&sessionid=b",
                cache: false,
                success: function() {
                    window.location.reload();
                }
            });
        }

        function simulateBuyAction() {
            $.ajax({
                url:webappPath +
                        "api/1.0/buy?apikey=${apiKey}&tenantid=EASYREC_DEMO&itemid=43&itemdescription=Beastie Boys - Intergalactic&itemurl=/item/fatboyslim&itemimageurl=${extendedWebappPath}/img/covers/beastieboys.jpg&userid=B&sessionid=b",
                cache: false,
                success: function() {
                    window.location.reload();
                }
            });
        }

        function simulateRateAction() {
            $.ajax({
                url:webappPath +
                        "api/1.0/rate?apikey=${apiKey}&tenantid=EASYREC_DEMO&itemid=44&itemdescription=Gorillaz - Clint Eastwood&itemurl=/item/fatboyslim&itemimageurl=${extendedWebappPath}/img/covers/gorillaz.jpg&userid=B&sessionid=b&ratingvalue=10",
                cache: false,
                success: function() {
                    window.location.reload();
                }
            });
        }
        <c:if test="${tenant.maxActionLimitAlmostExceeded && !tenant.maxActionLimitExceeded}">
        popup(limitAlmostExceeded);
        </c:if>
        <c:if test="${tenant.maxActionLimitExceeded}">
        popup(limitExceeded);
        </c:if>
    </c:if>

</script>


<div id="followingSteps" style="display:none;">
     <jsp:include page="followingsteps.jsp"/>
</div>