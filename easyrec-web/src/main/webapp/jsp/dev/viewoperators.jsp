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

<script src="${webappPath}/js/dev.js" type="text/javascript"></script>
<div class="appendbody">
    <h1>Operators</h1>

    <div id="status"><br/></div>

    <div id="operators">
        <table width="100%">
            <tr>
                <th/>
                <th></th>
                <th></th>
                <th>Operator</th>
                <th>First Name</th>
                <th>Last Name</th>
                <th>Logins</th>
                <th>Last Login</th>
                <th>Creation Date</th>
                <th>API Key</th>
                <th>Active</th>
                <!--th>Admin</th-->
            </tr>
            <c:forEach var="operator" items="${operators}" varStatus="status">
                <tr <c:if test="${status.count % 2 == 1}">style="background-color:#eeeeee"</c:if>>

                    <td>
                        <c:if test="${operator.operatorId!='easyrec'}">
                            <div id="operatorremove-${operator.operatorId}">
                                <a href="javascript:removeOperator('${operator.operatorId}')">
                                    <img title="remove operator" alt="remove operator"
                                         src="${webappPath}/img/button_delete.png"/>
                                </a>
                            </div>
                        </c:if>
                    </td>
                    <td>
                        <a title="${operator.email}" href="mailto:${operator.email}">
                            <img title="mailto:${operator.email}" alt="mailto:${operator.email}"
                                 src="${webappPath}/img/button_mail.png"/>
                        </a>
                    </td>
                    <td>
                        <a title="${operator.ip}" onclick="window.open(this.href,'_blank'); return false;"
                           href="http://www.db.ripe.net/whois?searchtext=${operator.ip}">
                            <img title="ip:${operator.ip}" alt="ip:${operator.ip}"
                                 src="${webappPath}/img/button_globe.png"/>
                        </a>
                    </td>
                    <td>
                        <a title="view tenants of this operator"
                           href="viewtenants?operatorId=${operator.operatorId}&tenantId=${tenantId}">
                            <texta:stringabbreviator myString="${operator.operatorId}" maxLength="12"/>

                        </a>
                    </td>
                    <td>
                        <texta:stringabbreviator myString="${operator.firstName}" maxLength="12"/>
                    </td>
                    <td>
                        <texta:stringabbreviator myString="${operator.lastName}" maxLength="12"/>
                    </td>
                    <td>${operator.loginCount}</td>
                    <td>${operator.lastLoginDate}</td>
                    <td>${operator.creationDate}</td>
                    <td style="font-size:9px;">${operator.apiKey}</td>
                    <td align="center" style="font-size:9px;">
                        <c:if test="${operator.active}"><img title="yes" alt="yes"
                                                             src="${webappPath}/img/button_check.png"/></c:if>
                    </td>
                    <!--td align="center" style="font-size:9px;">
                <c:if test="${operator.accessLevel==1}"><img title="yes" alt="yes" src="${webappPath}/img/Check-icon.png"/></c:if>
            </td-->
                </tr>
            </c:forEach>
        </table>
        <c:if test="${operatorsTotal>50}">
            <br/>${pageMenuString}
        </c:if>
    </div>
</div>
