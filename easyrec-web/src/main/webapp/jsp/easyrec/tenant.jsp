<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="appendbody">

    <jsp:include page="menu.jsp"/>
    <script src="${webappPath}/js/easyrec.js" type="text/javascript"></script>
    <script src="${webappPath}/js/item.js" type="text/javascript"></script>
    <table>
        <tr>
            <td>
                <jsp:include page="menubar.jsp"/>
                <div id="myeasyrec"></div>
                <jsp:include page="/tenant/view?operatorId=${operatorId}&tenantId=${tenantId}"></jsp:include>
            </td>
        </tr>
    </table>
</div>
<script type="text/javascript">

    <c:if test="${tenant.maxActionLimitAlmostExceeded && !tenant.maxActionLimitExceeded}">
    popup(limitAlmostExceeded);
    </c:if>
    <c:if test="${tenant.maxActionLimitExceeded}">
    popup(limitExceeded);
    </c:if>
</script>