<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="easyrec" uri="/WEB-INF/tagLib.tld" %>
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

<div class="leftsubmenu">
    <span class="headline">Tenant Management</span>
    <ul id="tenantMenu" class="subMenu">
        <c:if test="${operatorId==signedInOperatorId}">
            <li <c:if test="${create}">class="selectedTenant"</c:if>>
                <a href="${webappPath}/easyrec/create?tenantId=${tenantId}&operatorId=${operatorId}">Create a new
                    Tenant...</a>
            </li>
        </c:if>
        <c:forEach var="tenant" items="${tenants}">
            <li <c:if test="${tenantId==tenant.stringId}">class="selectedTenant"</c:if>>
                <a href="${webappPath}/easyrec/overview?tenantId=${tenant.stringId}&operatorId=${operatorId}"
                   title="${tenant.stringId}">
                    <easyrec:stringabbreviator myString="${tenant.stringId}" maxLength="25"/>
                </a>
                <a class="setting_button"
                   href="${webappPath}/easyrec/update?operatorId=${operatorId}&tenantId=${tenant.stringId}">
                    <img class="clickable" src="${webappPath}/img/button_settings.png"
                         alt="Edit ${tenant.stringId}"
                         title="Edit ${tenant.stringId}"/>
                </a>
            </li>


        </c:forEach>
    </ul>
    <br/>


    <c:if test="${signedIn}">

        <table width="74%">
            <tr>
                <td width="68%">
                    <span class="headline">API Key</span>
                    <img class="clickable" alt="help" src="${webappPath}/img/button_help.png"
                         onclick="$('#div_apikey_help').slideToggle('slow')"/>
                </td>
            </tr>
            <tr class="help">
                <td colspan="3">
                    <div id="div_apikey_help" style="display: none;width: 200px;">
                        Sending actions or receiving recommendations from easyrec
                        requires this API Key to be send in every request.
                        Visit the
                        <a href="${webappPath}/API">REST-API</a>
                        section for more details.
                    </div>
                </td>
            </tr>
        </table>
        <div style="padding-bottom: 10px;"></div>
        <ul class="subMenu">
            <li>
                <div style="padding-top: 5px; border-top: 1px solid #90CBE1; font-size: 10px;">${apiKey}</div>
            </li>
        </ul>
        <br/>

        <table width="74%">
            <tr>
                <td width="68%">
                    <span class="headline">Token</span>
                    <img class="clickable" alt="help" src="${webappPath}/img/button_help.png"
                         onclick="$('#div_token_help').slideToggle('slow')"/>
                </td>
            </tr>
            <tr class="help">
                <td colspan="3">
                    <div id="div_token_help" style="display: none;width: 200px;">
                        For importing rules use this security
                        token in every request.
                        This token is only valid for the current session.
                        Visit the
                        <a href="${webappPath}/API">REST-API</a> Import
                        section for more details.
                    </div>
                </td>
            </tr>
        </table>
        <div style="padding-bottom: 10px;"></div>
        <ul class="subMenu">
            <li>
                <div style="padding-top: 5px; border-top: 1px solid #90CBE1; font-size: 10px; ">${securityToken}</div>
            </li>
        </ul>

        <br/>

        <table width="74%">
            <tr>
                <td width="68%">
                    <span class="headline">Further reading</span>
                </td>
            </tr>
        </table>
        <div style="padding-bottom: 10px;"></div>
        <ul class="subMenu">
            <li><easyrec:wikiLink name="FAQ"/></li>
            <li><easyrec:wikiLink name="Documentation" pageName="Documentation_v0.96"/></li>
        </ul>

        <c:if test="${heapsize<640}">
            <br/><br/>
            <table width="74%">
                <tr>
                    <td width="68%">
                        <span class="headline">Warning</span>
                        <img src="${webappPath}/img/icon_warning.png"/>

                        <div style="padding-bottom: 10px;"></div>
                        <ul class="subMenu">
                            <li>
                                <div style="padding-top: 5px; border-top: 1px solid #90CBE1; "></div>
                            </li>
                        </ul>
                    </td>
                </tr>
                <tr class="help">
                    <td colspan="3">
                        <div id="div_heap_help">
                            The virtual machine (VM) you are running easyrec in should at least
                            provide a heapsize of 640 MB. The heap size currently found
                            is ${heapsize} MB.
                            Increase the heap size to ensure smooth operation of easyrec
                            in production environments.You can do this by setting the
                            java vm options.
                            e.g. JAVA_OPTS"=-Xms256m -Xmx640m"
                        </div>

                    </td>
                </tr>
            </table>
        </c:if>

    </c:if>


</div>