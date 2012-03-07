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

<div class="menu">

    <div style="position:absolute; left:13px; top:25px; padding: 10px;">
        <a href="${webappPath}/easyrec/overview?<c:if test="${signedInOperatorId!=null}">operatorId=${signedInOperatorId}</c:if>">
            <span class="logo">easyrec<sup style="font-size: 9px">BETA</sup></span>
        </a>
    </div>

    <div id="signInMenu" <c:if test="${!signedIn}">style="display:none;"</c:if>>
        ${signedInOperatorId}
        <c:if test="${isDeveloper && signedInOperatorId!=operatorId && operatorId!=null}">
            (as ${operatorId})
        </c:if>
        |
        <a id="signin" onclick="signoutUser();" href="#">Sign Out</a>
        &nbsp;&bull;&nbsp;
        <a id="register" href="${webappPath}/updateform?<c:if
            test="${tenantId!=null}">tenantId=${tenantId}&</c:if><c:if test="${operatorId!=null}">operatorId=${operatorId}</c:if>">Update
            Account</a>
        <c:if test="${isDeveloper}">
            &nbsp;&bull;&nbsp;&bull;&nbsp;&bull;&nbsp;&nbsp;<a href="${webappPath}/dev/viewalltenants?tenantId=${tenantId}&operatorId=${operatorId}">Administration</a>
        </c:if>
    </div>

    <div id="menu">
        <c:if test="${signedIn}">
            <ul>
                <c:if test="${operatorId == ''}">
                    <c:set var="operatorId" value="${signedInOperatorId}"/>
                </c:if>
                <li<c:if test="${selectedMenu == 'home' }"> class="selectedMenu"</c:if>><a
                        href="${webappPath}/home?operatorId=${signedInOperatorId}"
                        title="Home"><span>Home</span></a></li>
                <li<c:if test="${selectedMenu == 'myEasyrec' }"> class="selectedMenu"</c:if>><a
                        href="${webappPath}/easyrec/overview?operatorId=${signedInOperatorId}&tenantId=EASYREC_DEMO"
                        title="my easyrec"><span>Management</span></a></li>
                <li<c:if test="${selectedMenu == 'api' }"> class="selectedMenu"</c:if>><a
                        href="${webappPath}/API?operatorId=${signedInOperatorId}"
                        title="How to implement"><span>API</span></a></li>
            </ul>
        </c:if>
    </div>
</div>