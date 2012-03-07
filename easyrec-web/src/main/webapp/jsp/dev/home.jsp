<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<div class="appendbody">
    <h1>Info</h1>
    <br/>
    <table>
        <tr>
            <td><b>${easyrecName}</b></td>
            <td>${easyrecVersion}</td>
        </tr>
        <tr>
            <td><b>Operator</b></td>
            <td>${signedinOperatorId}</td>
        </tr>
        <tr>
            <td><b>Operation Mode</b></td>
            <td>${operationMode}</td>
        </tr>
        <tr>
            <td><br/></td>
        </tr>
        <tr>
            <td><b>Webapp-Path</b></td>
            <td>'${webappPath}'</td>
        </tr>
        <tr>
            <td><b>Extended Webapp-Path</b></td>
            <td>'${extendedWebappPath}'</td>
        </tr>
        <tr>
            <td><br/></td>
        </tr>
        <tr>
            <td><b>DB-Name</b></td>
            <td>'${dbName}'</td>
        </tr>
        <tr>
            <td><b>DB-User</b></td>
            <td>'${dbUserName}'</td>
        </tr>
        <tr>
            <td><br/><b>Used Memory/Heapsize</b><br/>(refreshed every second)</td>
            <td id="usedmem">${usedmemory} MB/${heapsize} MB (<fmt:formatNumber value="${(usedmemory/heapsize)*100}"
                                                                                maxFractionDigits="2"/>% used)
            </td>
        </tr>
        <c:if test="${freespace!=null}">
            <tr>
                <td><b>Free Disc Space</b></td>
                <td>${freespace} GB</td>
            </tr>
        </c:if>
        <tr>
            <td><b>Total daily plugin <br/>computation time</b></td>
            <td>${dailyComputationTime}</td>
        </tr>


        <tr>
            <td>
                <br/>
                ${operatorCount} <a href="viewoperators">Operator(s)</a>
                ${tenantCount} <a href="viewalltenants">Tenant(s)</a>
            </td>
        </tr>


    </table>
</div>
<script type="text/javascript">
    $(function()
    {
        pullusedmem(${heapsize}, 0);
    });
</script>
