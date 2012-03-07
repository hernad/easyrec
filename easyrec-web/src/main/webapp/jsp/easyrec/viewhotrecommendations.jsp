<%@ taglib prefix="texta" uri="/WEB-INF/tagLib.tld" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net" %>
<%@ taglib prefix="esapi" uri="/WEB-INF/esapi.tld" %>

<%--@elvariable id="tenantId" type="java.lang.String"--%>
<%--@elvariable id="items" type="java.util.List<org.easyrec.model.web.Item>"--%>

<script src="${webappPath}/js/easyrec.js" type="text/javascript"></script>
<script src="${webappPath}/js/item.js" type="text/javascript"></script>

<div class="appendbody">
    <jsp:include page="menu.jsp"/>

    <jsp:include page="menubar.jsp"/>
    <span class="headlineBig">
        Hot Recommendations on "${tenantId}"
    </span>

    <p>
        The hot recommendations list shows the most popular recommended items, i. e. items that easyrec presented to
        the user as part of a recommendation and were then clicked on by a user.

        The hot recommendations list shows the most popular recommended items.
    </p>

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

                (${fn:substringBefore(row.value,".")} recommendation hits)
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

        <c:if test="${fn:length(items)==0}">
            <p>
                Notice: This feature only works if you use the URLs supplied by easyrec in the field url within
                recommendations
                supplied by the REST API. Otherwise easyrec has no means to track clicks on recommended items and you
                loose
                this valuable information.
            </p>
        </c:if>

    </div>
</div>

