<%@ taglib prefix="texta" uri="/WEB-INF/tagLib.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="esapi" uri="/WEB-INF/esapi.tld" %>
<p>
    This view shows the rules for the currently selected item. Rules associate items with each other. They are
    generated on a regular basis by rule generators.
</p>

<c:forEach var="current" items="${assocs}" varStatus="status">
    <c:if test="${fn:length(current.value)>0}">

        <div class="assocHeader">
            <img title="rules for this item" src="${webappPath}/img/button_rules.png"/>
            ${fn:replace(fn:toLowerCase(current.key), "_", " ")}&nbsp;with ( ${fn:length(current.value)} )

            <div style="float:right;">
                assoc value
            </div>

        </div>

        <c:forEach var="assoc" items="${current.value}" varStatus="c">

            <div class="assocDetail"
                 title="Generator: ${assoc.sourceType}, Source: ${assoc.viewType}, association value: <fmt:formatNumber value="${assoc.value}" maxFractionDigits="2"/>,  source info: ${assoc.sourceInfo}"
                 <c:if test="${!assoc.itemTo.active}">style="background: #ffcccc"</c:if>
                    >

                    <span style="width:550px;display: inline-block;">
                         <a href="javascript:void(0);"
                            onclick="loadItem('${operatorId}','${tenantId}','${assoc.itemTo.id}', '${esapi:encodeForJavaScript(assoc.itemTo.description)}')">
                             <texta:stringabbreviator myString="${assoc.itemTo.description}" maxLength="80"/>
                         </a>
                        <c:if test="${assoc.itemTo.value!=null}">&nbsp;(${fn:substringBefore(assoc.itemTo.value,".")} hits)</c:if>
                    </span>

                <div style="float:right;line-height: 17px;">
                    <fmt:formatNumber value="${assoc.value}" maxFractionDigits="2"/>

                    <a href="${assoc.itemTo.url}" target="_blank">
                        <img title="open this item in new tab" src="${webappPath}/img/button_globe.png"/>
                    </a>
                </div>
            </div>


            <c:if test="${c.count==5 && fn:length(current.value)>5}">
                <div id="moreviewed${current.key}_${item.id}" style="display:none">
            </c:if>
            <c:if test="${fn:length(current.value)>5 && c.count== fn:length(current.value)}">
                </div>
            </c:if>

        </c:forEach>

        <div class="assocFooter">
            <c:if test="${fn:length(current.value)>5}">
                <div style="float:right;font-weight:bold;">
                    <a id="more${current.key}_${item.id}"
                       href="javascript:void(0);"
                       onclick="javascript:toggleMore($('#moreviewed${current.key}_${item.id}'), this);">more...</a>
                </div>
            </c:if>
        </div>

    </c:if>
</c:forEach>

<script type='text/javascript'>
    /*
     * Toggle between more and less
     */
    function toggleMore(more, label) {
        more.slideToggle('slow');

        if (label.innerHTML == "more...") {
            label.innerHTML = "less...";
        } else {
            label.innerHTML = "more...";
        }
    }
</script>