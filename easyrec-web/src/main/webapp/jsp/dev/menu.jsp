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

<div class="menu_admin">
    <div style="position:absolute; left:13px; top:25px; padding: 10px;">
        <a href="${webappPath}/home">
            <span class="logo">easyrec<sup style="font-size: 9px">BETA</sup> :: Administration</span>
        </a>
    </div>

    <div id="signInMenu" <c:if test="${!signedIn}">style="display:none;"</c:if>>
        ${signedInOperatorId} |
        <a id="signin" onclick="signoutUser();" href="#">Sign Out</a>
        &nbsp;&bull;&nbsp;
        <a id="register" href="${webappPath}/updateform">Update Account</a>
        <c:if test="${isDeveloper}">
            &nbsp;&bull;&nbsp;&bull;&nbsp;&bull;&nbsp;&nbsp;<a href="${webappPath}/easyrec/tenant?menu=x&tenantId=${tenantId}&operatorId=${operatorId}">Management</a>
        </c:if>
    </div>

    <div align="right" style="padding-top: 20px;padding-right: 17px;">
        <a class="menu_admin" href="viewalltenants?tenantId=${tenantId}&operatorId=${operatorId}">Tenants</a>
        | <a class="menu_admin" href="viewoperators?tenantId=${tenantId}&operatorId=${operatorId}">Operators</a>
        | <a class="menu_admin" href="jamonreport?tenantId=${tenantId}&operatorId=${operatorId}">Performance</a>
        | <a class="menu_admin" href="viewpluginlogs?tenantId=${tenantId}&operatorId=${operatorId}">Plugin Logs</a>
        <!--
             | <a class="menu_admin" href="viewruledemo?tenantId=${tenantId}&operatorId=${operatorId}">Rule Demo</a>
         -->
        | <a class="menu_admin" href="plugins?tenantId=${tenantId}&operatorId=${operatorId}">Plugins</a>
        | <a class="menu_admin" href="home?tenantId=${tenantId}&operatorId=${operatorId}">Info</a>
    </div>

</div>