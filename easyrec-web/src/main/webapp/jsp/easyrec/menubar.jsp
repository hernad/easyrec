<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h1>${tenantId}</h1>

<c:choose>
    <c:when test="${menubar=='tenant'}">
        <span class="bullmenu">Overview</span>&nbsp;&bull;
    </c:when>
    <c:otherwise>
        <a href="${webappPath}/easyrec/overview?menu=x&tenantId=${tenantId}&operatorId=${operatorId}">Overview</a>&nbsp;&bull;
    </c:otherwise>
</c:choose>

<c:choose>
    <c:when test="${menubar=='viewItems'}">
        <span class="bullmenu">Items &amp; Rules</span>&nbsp;&bull;
    </c:when>
    <c:otherwise>
        <a href="${webappPath}/tenant/items?tenantId=${tenantId}&operatorId=${operatorId}">Items &amp; Rules</a>&nbsp;&bull;
    </c:otherwise>
</c:choose>

<c:choose>
    <c:when test="${menubar=='viewStatistics'}">
        <span class="bullmenu">Statistics</span>&nbsp;&bull;
    </c:when>
    <c:otherwise>
        <a href="${webappPath}/tenant/statistics?tenantId=${tenantId}&operatorId=${operatorId}">Statistics</a>&nbsp;&bull;
    </c:otherwise>
</c:choose>

<c:choose>
    <c:when test="${menubar=='viewMostViewedItems'}">
        <span class="bullmenu">Top Ranked</span>&nbsp;&bull;
    </c:when>
    <c:otherwise>
        <a href="${webappPath}/tenant/viewmostvieweditems?tenantId=${tenantId}&operatorId=${operatorId}">Top Ranked</a>&nbsp;&bull;
    </c:otherwise>
</c:choose>

<c:choose>
    <c:when test="${menubar=='viewHotRecommendations'}">
        <span class="bullmenu">Hot Recommendations</span> &nbsp;&bull;
    </c:when>
    <c:otherwise>
        <a href="${webappPath}/tenant/viewhotrecommendations?tenantId=${tenantId}&operatorId=${operatorId}">Hot
            Recommendations</a>  &nbsp;&bull;
    </c:otherwise>
</c:choose>

<c:choose>
    <c:when test="${menubar=='clustermanager'}">
        <span class="bullmenu">Cluster Manager</span> &nbsp;&bull;
    </c:when>
    <c:otherwise>
        <a href="${webappPath}/tenant/clustermanager?tenantId=${tenantId}&operatorId=${operatorId}">Cluster Manager</a> &nbsp;&bull;
    </c:otherwise>
</c:choose>

<c:choose>
    <c:when test="${menubar=='itemtypes'}">
        <span class="bullmenu">Type Manager</span>
    </c:when>
    <c:otherwise>
        <a href="${webappPath}/tenant/typemanager?tenantId=${tenantId}&operatorId=${operatorId}">Type Manager</a>
    </c:otherwise>
</c:choose>

<br/><br/>