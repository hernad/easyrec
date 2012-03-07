<%@ taglib prefix="texta" uri="/WEB-INF/tagLib.tld" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net" %>
<%@ taglib prefix="esapi" uri="/WEB-INF/esapi.tld" %>

<script src="${webappPath}/js/easyrec.js" type="text/javascript"></script>
<script src="${webappPath}/js/item.js" type="text/javascript"></script>

<div class="appendbody">

    <jsp:include page="menu.jsp"/>

    <jsp:include page="menubar.jsp"/>

    <h2>Top Ranked Items on "${tenantId}"</h2>

    <p>
        Top Ranked Items shows the items most often acted on within the selected time range.<br/>
    </p>

    <p>
        <b>Notice:</b> Rankings are updated every 24 hours.<br/><br/>
    </p>

    showing&nbsp;
    <select id="assoc" name="assoc" onchange="changeView('${operatorId}','${tenantId}')">
        <option
                <c:if test="${param.assoc=='mostviewed'}">selected="selected"</c:if>
                value="mostviewed">Most Viewed Items
        </option>
        <option
                <c:if test="${param.assoc=='mostbought'}">selected="selected"</c:if>
                value="mostbought">Most Bought Items
        </option>
        <option
                <c:if test="${param.assoc=='mostrated'}">selected="selected"</c:if>
                value="mostrated">Most Rated Items
        </option>
        <option
                <c:if test="${param.assoc=='bestrated'}">selected="selected"</c:if>
                value="bestrated">Best Rated Items
        </option>
        <option
                <c:if test="${param.assoc=='worstrated'}">selected="selected"</c:if>
                value="worstrated">Worst Rated Items
        </option>
    </select>&nbsp;from&nbsp;
    <select id="timerange" name="time" onchange="changeView('${operatorId}','${tenantId}')">
        <option
                <c:if test="${param.timerange=='MONTH'}">selected="selected"</c:if>
                value="MONTH">last 31 days
        </option>
        <option
                <c:if test="${param.timerange=='WEEK'}">selected="selected"</c:if> value="WEEK">
            last 7 days
        </option>
        <option
                <c:if test="${param.timerange=='DAY'}">selected="selected"</c:if> value="DAY">
            last 24 hours
        </option>
        <option
                <c:if test="${param.timerange=='ALL'}">selected="selected"</c:if> value="ALL">
            all time
        </option>
    </select>
    <img id="redirect" alt="wait" src="${webappPath}/img/blank.gif"/>
    <hr/>
    <div class="contentContainer">
        <display:table name="items" class="tableData" id="row"
                       requestURI="${webappPath}/tenant/viewmostvieweditems?tenantId=${tenantId}&operatorId=${operatorId}"
                       pagesize="0">
            <display:column title="id">
                ${esapi:encodeForHTML(row.itemId)}
            </display:column>
            <display:column title="Description">
                <a href="javascript:void(0);"
                   onclick="loadItem('${operatorId}','${tenantId}','${row.id}','${esapi:encodeForJavaScript(row.description)}', true);">
                        ${esapi:encodeForHTML(row.description)}
                </a>
            </display:column>
            <display:column title="Type">
                ${esapi:encodeForHTML(row.itemType)}
            </display:column>
            <display:column style="width:20px;">
                <a href="${esapi:encodeForHTMLAttribute(row.url)}" target="_blank">
                    <img title="open this item in new tab" src="${webappPath}/img/button_globe.png"/>
                </a>
            </display:column>
        </display:table>
    </div>
</div>