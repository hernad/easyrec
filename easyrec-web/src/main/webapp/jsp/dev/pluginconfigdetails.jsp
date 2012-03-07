<%@ page language="java" session="false" %>
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

<table class="configtable" >
    <tr hidden="true">
        <th class="configparam" ><strong>configuration</strong></th>
        <td class="configedit"></td>
        <td class="configvalue">
            <%-- TODO onchange make divs visable/invisible --%>
            <select id="plugin-configuration-select-${assocTypeId}" disabled="disabled" >
                <c:forEach var="configuration" items="${paramList}">
                    <option value="${configuration.key}">${configuration.key}</option>
                </c:forEach>
            </select>
        </td>
        <td class="confighelpbutton">
            <img class="clickable" alt="help" src="${webappPath}/img/button_help.png"
                 onclick="$('#div_${assocType.value}_plugin').slideToggle('slow')"/>
        </td>
        <td rowspan="1">
            <div class="pluginconfig_help" id="div_${assocType.value}_plugin">
                Select the configuration you want to use for this plugin
            </div>
        </td>
    </tr>
</table>


<h4>
    <a href="javascript:toggleConfigDetails('${assocTypeId}');">
        Settings
    </a>
</h4>

<div id="plugin-configuration-${assocTypeId}" style="display:none">
    <c:forEach var="configuration" items="${paramList}">
        <div id="plugin-configuration-${assocTypeId}-${fn:replace(configuration.key, ' ','')}">
            <table class="configtable">
                <c:forEach var="paramDetail" items="${configuration.value}">
                    <c:set var="configId"
                           value="${paramDetail.name}-${assocTypeId}-${fn:replace(configuration.key, ' ','')}"/>
                    <c:set var="configEditCall"
                           value="editPlugin('${operatorId}', '${tenantId}','${assocTypeId}','${configuration.key}','${paramDetail.name}','${configId}');"/>
                    <tr>
                        <td colspan="5">
                            <div id="plugin-error-${configId}"
                                 style="display:none;" class="msgbox error"></div>
                        </td>
                    </tr>
                    <tr>
                        <th class="configparam">
                                ${paramDetail.displayName}
                        </th>
                        <td class="configedit">
                            <a id="${configId}"
                               href="javascript:${configEditCall}">edit</a>
                        </td>
                        <td class="configvalue">
                            <div id="static-${configId}">${paramDetail.value}</div>
                            <input id="edit-${configId}" style="display:none;width:100%;" type="text"
                                   value="${paramDetail.value}" maxlength="50" size="5"/>
                        </td>
                        <td class="confighelpbutton">
                            <img class="clickable" alt="help" src="${webappPath}/img/button_help.png"
                                 onclick="$('#plugin-help-${configId}').slideToggle('slow')"/>
                        </td>
                        <td>
                            <div style="display:none;" class="pluginconfig_help" id="plugin-help-${configId}">
                                    ${paramDetail.description}
                            </div>
                        </td>
                    </tr>
                    <script type="text/javascript">
                        $('#edit-${configId}').keypress(function(event) {
                            if ((event.which && event.which == 13) || (event.keyCode && event.keyCode == 13)) {
                                ${configEditCall}
                            }
                        });
                    </script>
                </c:forEach>
            </table>
        </div>
    </c:forEach>
</div>