<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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

<div class="appendbody">
    <h1>${tenantId} configuration <span style="font-size: 0.7em; font-weight: normal; float: right;">operated by ${operatorId}</span></h1>
    <c:if test="${isGenerator}">
        <span id="plugin-${operatorId}-${tenantId}">
            <a href="javascript:startPlugin('${operatorId}','${tenantId}');">
                <img title="start plugins" alt="start plugins" src="${webappPath}/img/button_plugin_start.png"
                     style="vertical-align: text-bottom;"/>
                start plugins
            </a>
        </span>
    </c:if>
    &nbsp;
    <a href="viewpluginlogs?tenantId=${tenantId}&operatorId=${operatorId}">
        <img title="show plugin logs" alt="logs" src="${webappPath}/img/button_preview.png" style="vertical-align: text-bottom;"/>
        logs
    </a>
    &nbsp;
    <a href="${webappPath}/tenant/statistics?tenantId=${tenantId}&operatorId=${operatorId}">
        <img title="show tenant statistics" alt="statistics" src="${webappPath}/img/button_statistics.png" style="vertical-align: text-bottom;"/>
        statistics
    </a>
    <div id="status"></div>
    <jsp:include page="pluginconfig.jsp"/>
</div>
