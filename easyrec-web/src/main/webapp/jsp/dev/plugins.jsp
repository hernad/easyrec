<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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

<div class="appendbody">
    <h1>Plugins</h1>
    <a href="pluginupload?tenantId=${tenantId}&operatorId=${operatorId}">Upload new plugin</a>
    <br/>
    <table width="100%" cellpadding="10">
        <tr>
            <th>Id</th>
            <th>displayName</th>
            <th>pluginId</th>
            <th>version</th>
            <th>state</th>
            <th>execution State</th>
            <th>progress</th>
        </tr>
        <c:forEach var="plugin" items="${pluginList}" varStatus="status">
            <tr <c:if test="${status.count % 2 == 1}">style="background-color:#eeeeee"</c:if>>
                <td>${plugin.id}</td>
                <td>${plugin.displayName}</td>
                <td>${plugin.pluginId.uri}</td>
                <td>${plugin.pluginId.version}</td>
                <td width="100">
                        ${plugin.state}
                </td>

                <c:set var="internalId" value="${plugin.pluginId}"></c:set>
                <td width="100">
                        ${executionStates[internalId]}
                    <c:if test="${executionStates[internalId] == 'RUNNING'}">
                        <a href="javascript:void(0);"
                           onclick="stopPlugin('${plugin.pluginId.uri}','${plugin.pluginId.version}', '${operatorId}', '${tenantId}')">
                            <img title="stop plugin" alt="stop plugin" src="${webappPath}/img/button_plugin_stop.png"/>
                        </a>
                    </c:if>

                </td>
                <td width="140">
                    <c:if test="${executionStates[internalId]!='STOPPED'}">
                        ${progresses[internalId].currentSteps}/${progresses[internalId].totalSteps}  ${progresses[internalId].message}
                    </c:if>
                </td>
                <td></td>
                <td width="140">
                    <c:if test="${plugin.pluginId.uri != 'http://www.easyrec.org/plugins/ARM'}">
                    <c:choose>
                        <c:when test="${plugin.state=='NOT_INSTALLED' || plugin.state=='INSTALLED'}">
                            <div style="float:left;padding-top:3px;">
                                <input type="checkbox"
                                       id="enablePluginCheckbox-${plugin.id}"
                                       onchange="pluginChangeState(${plugin.id},'${plugin.pluginId.uri}','${plugin.pluginId.version}','${operatorId}', '${tenantId}')"
                                       class="yesno">
                            </div>
                        </c:when>
                        <c:when test="${plugin.state=='INSTALL_FAILED'}">
                            <img title="plugin install failed" alt="plugin failed to install"
                                 src="${webappPath}/img/icon_warning.png"/>
                        </c:when>
                        <c:when test="${plugin.state=='INITIALIZED'}">
                            <div style="float:left;padding-top:3px;">
                                <input type="checkbox"
                                       id="enablePluginCheckbox-${plugin.id}"
                                       onchange="pluginChangeState(${plugin.id},'${plugin.pluginId.uri}','${plugin.pluginId.version}', '${operatorId}', '${tenantId}')"
                                       class="yesno" checked="">

                            </div>
                        </c:when>
                        <c:otherwise>
                            <img title="plugin installed" alt="plugin installed"
                                 src="${webappPath}/img/button_check.png"/>
                        </c:otherwise>
                    </c:choose>
                    <a style="float:left;margin-left:5px;" href="javascript:void(0);"
                       onclick="deletePlugin('${plugin.pluginId.uri}','${plugin.pluginId.version}', '${operatorId}','${tenantId}')">
                        <img title="delete plugin" alt="delete plugin"
                             src="${webappPath}/img/button_plugin_delete.png"/>
                    </a>
                    </c:if>
                    <c:if test="${plugin.pluginId.uri == 'http://www.easyrec.org/plugins/ARM' && plugin.state=='NOT_INSTALLED'}">
                            <div style="float:left;padding-top:3px;">
                                <input type="checkbox"
                                       id="enablePluginCheckbox-${plugin.id}"
                                       onchange="pluginChangeState(${plugin.id},'${plugin.pluginId.uri}','${plugin.pluginId.version}','${operatorId}', '${tenantId}')"
                                       class="yesno">
                            </div>
                    </c:if>
                    <c:if test="${plugin.state=='INITIALIZED'}">
                        <img class="clickable" alt="help" src="${webappPath}/img/button_help.png" style="float:right;"
                             onclick="$('#div_pluginInfo_help_${plugin.id}').slideToggle('slow')"/>
                    </c:if>
                </td>
            </tr>
            <tr class="help">
                <td colspan="9">
                    <div id="div_pluginInfo_help_${plugin.id}" style="display: none">
                        <script>
                            showPluginDescription('${operatorId}', '${tenantId}', '${plugin.pluginId.uri}',
                                    '${plugin.pluginId.version}', 'div_pluginInfo_help_${plugin.id}');
                        </script>
                    </div>
                </td>
            </tr>
        </c:forEach>
    </table>
</div>

<script type="text/javascript">
    $(document).ready(function() {
        $(':checkbox').iphoneStyle();
    });
</script>

