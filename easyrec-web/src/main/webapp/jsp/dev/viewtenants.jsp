<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="texta" uri="/WEB-INF/tagLib.tld" %>
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
    <h1>${operatorId}'s tenants</h1>

    <div id="status"><br/></div>
    <c:choose>
        <c:when test="${remoteTenants!=null}">
            <div id="remotetenants">
                <table width="100%">
                    <tr>
                        <th>Id</th>
                        <th/>
                        <th/>
                        <th></th>
                        <th>Tenant</th>
                        <th>Description</th>
                        <th title="total number of actions of the tenant">Actions</th>
                        <th title="number of rules computed by the plugins">Rules</th>
                        <th title="shows the next execution time of the plugins, if the scheduler is enabled">
                            Scheduler
                        </th>
                        <th title="creation date of the tenant">Creation Date</th>
                        <th>Management</th>
                        <th/>
                        <th/>

                    </tr>
                    <c:forEach var="remoteTenant" items="${remoteTenants}" varStatus="status">
                        <tr
                                <c:if test="${tenantId==remoteTenant.stringId && operatorId==remoteTenant.operatorId}">style="background-color:#EEF9FF;border-top: solid 1px #90CBE1;border-bottom: solid 1px #90CBE1;"</c:if>
                                <c:if test="${status.count % 2 == 1}">style="background-color:#eeeeee"</c:if>

                                >
                            <td>${remoteTenant.id}</td>
                            <c:choose>
                                <c:when test="${remoteTenant.operatorId=='easyrec' && remoteTenant.stringId=='EASYREC_DEMO'}">
                                    <td/>
                                </c:when>
                                <c:otherwise>
                                    <td id="tenantremove-${remoteTenant.operatorId}-${remoteTenant.stringId}">
                                        <a href="javascript:removeTenant('${remoteTenant.operatorId}','${remoteTenant.stringId}');">
                                            <img title="remove tenant" alt="remove tenant"
                                                 src="${webappPath}/img/button_delete.png"/>
                                        </a>
                                    </td>
                                </c:otherwise>
                            </c:choose>
                            <td id="tenantreset-${remoteTenant.operatorId}-${remoteTenant.stringId}">
                                <a href="javascript:resetTenant('${remoteTenant.operatorId}','${remoteTenant.stringId}');">
                                    <img title="reset tenant" alt="reset tenant"
                                         src="${webappPath}/img/button_new.png"/>
                                </a>
                            </td>
                            <td>
                                <a onclick="window.open(this.href,'_blank'); return false;" href="${remoteTenant.url}">
                                    <img title="${remoteTenant.url}" alt="${remoteTenant.url}"
                                         src="${webappPath}/img/button_globe.png"/>
                                </a>
                            </td>
                            <td>
                                <a title="manage tenant"
                                   href="${webappPath}/easyrec/tenant?menu=x&operatorId=${remoteTenant.operatorId}&tenantId=${remoteTenant.stringId}">
                                    <texta:stringabbreviator myString="${remoteTenant.stringId}" maxLength="33"/>
                                </a>
                            </td>
                            <td><texta:stringabbreviator myString="${remoteTenant.description}" maxLength="30"/></td>

                            <td title="total number of actions of the tenant"
                                id="actions${remoteTenant.operatorId}-${remoteTenant.stringId}">${remoteTenant.actions}</td>
                            <td title="number of rules computed by the plugins"
                                id="rules${remoteTenant.operatorId}-${remoteTenant.stringId}">${remoteTenant.rules}</td>
                            <td title="next execution time of the plugins">
                                <c:if test="${remoteTenant.schedulerEnabled}">
                                    ${remoteTenant.schedulerExecutionTime}
                                    <a title="set execution time"
                                       href="viewpluginconfig?operatorId=${remoteTenant.operatorId}&tenantId=${remoteTenant.stringId}">
                                        <img alt="" src="${webappPath}/img/button_clock.png"/>
                                    </a>
                                </c:if>
                            </td>
                            <td title="creation date of the tenant">${remoteTenant.creationDate}</td>
                            <td id="plugin-${remoteTenant.operatorId}-${remoteTenant.stringId}">
                                <c:choose>
                                    <c:when test="${remoteTenant.id==runningTenantId}">
                                        <img title="plugins running..." alt="plugins running..."
                                             src="${webappPath}/img/wait16.gif"/>
                                    </c:when>
                                    <c:otherwise>
                                        <c:if test="${isGenerator}">
                                            <a href="javascript:startPlugin('${remoteTenant.operatorId}','${remoteTenant.stringId}');">
                                                <img title="start plugins" alt="start plugins"
                                                     src="${webappPath}/img/button_plugin_start.png"/>
                                            </a>
                                        </c:if>
                                    </c:otherwise>
                                </c:choose>
                                <a href="viewpluginconfig?operatorId=${remoteTenant.operatorId}&tenantId=${remoteTenant.stringId}">
                                    <img title="configure tenant" alt="config tenant"
                                         src="${webappPath}/img/button_settings.png"/></a>
                                <a href="viewpluginlogs?operatorId=${remoteTenant.operatorId}&tenantId=${remoteTenant.stringId}">
                                    <img title="show ${remoteTenant.stringId}'s plugin logs" alt="logs..."
                                         src="${webappPath}/img/button_preview.png"/>
                                </a>
                                <a href="${webappPath}/tenant/statistics?operatorId=${remoteTenant.operatorId}&tenantId=${remoteTenant.stringId}">
                                    <img title="show tenant statistics" alt="statistics"
                                         src="${webappPath}/img/button_statistics.png"/>
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
            </div>
        </c:when>
        <c:otherwise>
            <p>There are no tenants for this operator.</p>
        </c:otherwise>
    </c:choose>
</div>