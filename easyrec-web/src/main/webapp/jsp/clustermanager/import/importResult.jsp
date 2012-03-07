<%@ taglib  prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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


<div class="appendbody">

    <h1> Import Report </h1>

    <c:forEach var="error" items="${validationErrors}">
        <c:if test="${fn:containsIgnoreCase(error, 'ERROR')}">
            <img src="${webappPath}/img/error.png" width="16px"/>
        </c:if>
        ${error}<br/>
    </c:forEach>

    <img src='${webappPath}/img/success.gif'> File Import Completed.

    <br><br><br>

    added ${numberOfItems} items to ${numberOfTouchedClusters} different clusters.
    <br><br>
    click <a href="${webappPath}/tenant/clustermanager?tenantId=${tenantId}&operatorId=${signedInOperatorId}">here</a> to view your clusters

</div>