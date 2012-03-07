<%--@elvariable id="operatorId" type="java.lang.String"--%>
<%--@elvariable id="tenantId" type="java.lang.String"--%>
<%--@elvariable id="webappPath" type="java.lang.String"--%>
<%--@elvariable id="backtrackingEnabled" type="java.lang.Boolean"--%>
<%--@elvariable id="schedulerEnabled" type="java.lang.Boolean"--%>
<%--@elvariable id="activePlugins" type="java.util.Map"--%>
<%--@elvariable id="assocTypes" type="java.util.Map"--%>
<%--@elvariable id="pluginList" type="java.util.List"--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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

<script src="${webappPath}/js/pluginconfig.js" type="text/javascript"></script>

<style type="text/css">
    h2 {
        margin-top: 1.5em;
        padding-bottom: .25em;
        border-bottom: medium solid silver;
    }

    h3 {
        border-bottom: thin solid silver;
        margin-right: 25%;
        padding-bottom: .25em;
    }
</style>

<h2>back tracking</h2>
<table class="configtable">
    <tr>
        <th class="configparam">enabled</th>
        <td class="configedit">
            <a id="backtracking" href="javascript:backtracking('${operatorId}', '${tenantId}');">change</a>
        </td>
        <td class="configvalue" id="edit-backtracking">
            <div>${backtrackingEnabled}</div>
        </td>
        <td class="confighelpbutton">
            <img class="clickable" alt="help" src="${webappPath}/img/button_help.png"
                 onclick="$('#div_backtrack').slideToggle('slow');"/>
        </td>
        <td rowspan="1">
            <div class="pluginconfig_help" id="div_backtrack">
                Back tracking enables a feedback mechanism, that stores every click on a recommendation by the user.
                This feature enables you to see, if users really click on the items recommended by easyrec.
                Have a look in the <a href="${webappPath}/tenant/statistics?tenantId=EASYREC_DEMO">statistic</a>
                section to see the actual numbers of clicks on recommendations.
                If this number stays zero you might enable this feature and make sure that recommendations are
                displayed remarkable on your website.
            </div>
        </td>
    </tr>
    <tr id="layer-backtrackingURL" style="display:table-row">
        <th class="configparam">back tracking URL</th>
        <td class="configedit">
            <a id="backtrackingURL" href="javascript:changeBackTrackingURL('${operatorId}', '${tenantId}');">edit</a>
        </td>
        <td class="configvalue">
            <div id="static-backtrackingURL">${backtrackingURL}</div>
            <input id="edit-backtrackingURL" style="display:none" type="text" value="${backtrackingURL}"
                   maxlength="500" size="50"/>
        </td>
    </tr>
</table>

<h2>plugin scheduler</h2>
<table class="configtable">
    <tr>
        <th class="configparam">enabled</th>
        <td class="configedit">
            <a id="scheduler" href="javascript:scheduler('${operatorId}', '${tenantId}');">change</a>
        </td>
        <td class="configvalue" id="edit-scheduler">
            <div>${schedulerEnabled}</div>
        </td>
        <td class="confighelpbutton">
            <img class="clickable" alt="help" src="${webappPath}/img/button_help.png"
                 onclick="$('#div_autorule_help').slideToggle('slow')"/>
        </td>
        <td rowspan="2">
            <div class="pluginconfig_help" id="div_autorule_help">
                Schedules the generators to compute recommendations on a daily basis with a given execution time.
                Turn this feature off, if you want to start the generators manually.
                To run the generators goto to the <a href="${webappPath}/dev/viewalltenants">tenant</a> section and
                click on the <img alt="compute rules" src="${webappPath}/img/icon_synchronize.png"/> icon of a
                specific tenant or click on the start generators link in the submenu above.
                If the execution time is left blank, the default execution time is 02:00.
            </div>
        </td>
    </tr>
    <tr id="layer-executiontime" style="display:table-row">
        <th class="configparam">daily excecution time <em>hh:mm (e.g. 23:15)</em></th>
        <td class="configedit">
            <a id="excecutiontime" href="javascript:changeExecutionTime('${operatorId}', '${tenantId}');">edit</a>
        </td>
        <td class="configvalue">
            <div id="static-excecutiontime">${schedulerExecutionTime}</div>
            <input id="edit-excecutiontime" style="display:none" type="text" value="${schedulerExecutionTime}"
                   maxlength="5" size="5"/>
        </td>
    </tr>
</table>

<h2>archive actions</h2>
<table class="configtable">
    <tr>
        <th class="configparam">enabled</th>
        <td class="configedit">
            <a id="archiving" href="javascript:archiving('${operatorId}', '${tenantId}');">change</a>
        </td>
        <td class="configvalue" id="edit-archiving">
            <div>${archivingEnabled}</div>
        </td>
        <td class="confighelpbutton">
            <img class="clickable" alt="help" src="${webappPath}/img/button_help.png"
                 onclick="$('#div_autoarchive_help').slideToggle('slow')"/>
        </td>
        <td rowspan="2">
            <div class="pluginconfig_help" id="div_autoarchive_help">
                The longer you will use easyrec the more user actions will be recorded.
                This features moves actions older than the given days into the archive table.
                Actions in the archive table will not be consulted by the generators.
                Enable this feature or decrease the number of days, if you notice performance loss which may be weak
                response times when calling the API (see <a href="${webappPath}/dev/jamonreport">performance</a> section)
                or long rule miner computions tasks (goto the <a href="${webappPath}/dev/viewpluginlogs">plugin log</a>
                section and click details. The duration is in [ms].)
                You can neglect this feature, if your action table will not exceed the number of 10.000.000 entries
                per year.
            </div>
        </td>
    </tr>
    <tr id="layer-archivingtime" style="display:table-row">
        <th class="configparam">archive actions older than X days <em>(e.g. 365)</em></th>
        <td class="configedit">
            <a id="archivingtime" href="javascript:changeArchivingTime('${operatorId}', '${tenantId}');">edit</a>
        </td>
        <td class="configvalue">
            <div id="static-archivingtime">${archivingTime}</div>
            <input id="edit-archivingtime" style="display:none" type="text" value="${archivingTime}" maxlength="5"
                   size="5"/>
        </td>
    </tr>
</table>

<h2>limit actions</h2>
<table class="configtable">
    <tr id="layer-maxactions" style="display:table-row">
        <th class="configparam">maximum actions per month <em>(e.g. 100000)</em></th>
        <td class="configedit">
            <a id="maxactions" href="javascript:changeMaxActions('${operatorId}', '${tenantId}');">edit</a>
        </td>
        <td class="configvalue">
            <div id="static-maxactions">${maxActions}</div>
            <input id="edit-maxactions" style="display:none" type="text" value="${maxActions}" maxlength="10" size="8"/>
        </td>
        <td class="confighelpbutton">
            <img class="clickable" alt="help" src="${webappPath}/img/button_help.png"
                 onclick="$('#div_maxactions_help').slideToggle('slow')"/>
        </td>
        <td rowspan="1">
            <div class="pluginconfig_help" id="div_maxactions_help">
                Limit the number of maximum actions per month.
                If the limit is reached, no more incoming actions are accepted.
            </div>
        </td>
    </tr>
</table>

<h2>association type settings</h2>
<c:choose>
    <c:when test="${assocTypes != null && not empty assocTypes}">
        <c:forEach items="${assocTypes}" var="assocType">
            <h3>${assocType.key}</h3>
            <table class="configtable">
                <tr>
                    <th class="configparam">plugin</th>
                    <td class="configedit"></td>
                    <td class="configvalue">
                        <select id="plugin-select-${assocType.value}"
                                name="pluginType"
                                onchange="javascript:showPluginSettings('${operatorId}','${tenantId}', ${assocType.value})"
                                <c:if test="${assocType.key == 'VIEWED_TOGETHER' || assocType.key == 'BOUGHT_TOGETHER' || assocType.key == 'GOOD_RATED_TOGETHER'}">
                                    disabled="true" </c:if>>
                                <option value="" />
                            <c:forEach var="plugin" items="${pluginList}">
                                <c:set var="tempStr" value="${plugin.pluginId}"/>
                                <option value="${tempStr}"
                                        <c:if test="${activePlugins[assocType.key] == plugin.pluginId}">selected="true"</c:if>>
                                        ${plugin.pluginId.uri} (v${plugin.pluginId.version})
                                </option>
                            </c:forEach>
                        </select>
                    </td>
                    <td class="confighelpbutton">
                        <img class="clickable" alt="help" src="${webappPath}/img/button_help.png"
                             onclick="$('#div_${assocType.value}_plugin').slideToggle('slow')"/>
                    </td>
                    <td rowspan="1">
                        <div class="pluginconfig_help" id="div_${assocType.value}_plugin">
                            Select the plugin you want to run for this association type.
                        </div>
                    </td>
                </tr>
            </table>
            <div id="plugin-details-${assocType.value}" style="width:100%"></div>
            <script type="text/javascript">showPluginSettingsEx('${operatorId}', '${tenantId}', '${assocType.value}',
                    '${activePlugins[assocType.key]}');</script>
        </c:forEach>
    </c:when>
    <c:otherwise>
        No association types defined for tenant
        <%-- TODO add ability to add assoc types --%>
    </c:otherwise>
</c:choose>

<script type="text/javascript">
    $('#edit-excecutiontime').keypress(function(event) {
        if ((event.which && event.which == 13) || (event.keyCode && event.keyCode == 13)) {
            changeExecutionTime('${operatorId}', '${tenantId}', 'excecutiontime');
        }
    });
    $('#edit-archivingtime').keypress(function(event) {
        if ((event.which && event.which == 13) || (event.keyCode && event.keyCode == 13)) {
            changeArchivingTime('${operatorId}', '${tenantId}', 'archivingtime');
        }
    });
    $('#edit-maxactions').keypress(function(event) {
        if ((event.which && event.which == 13) || (event.keyCode && event.keyCode == 13)) {
            changeMaxActions('${operatorId}', '${tenantId}', 'maxactions');
        }
    });
</script>
