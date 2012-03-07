<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="easyrec" uri="/WEB-INF/tagLib.tld" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net" %>
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

<script type="text/javascript">

    var createDialog = $("#createNewAssocTypeForm").dialog(
            {
                modal:true,
                title:"Create a new Assoc Type",
                width:430,
                height:300,
                resizable:false,
                autoOpen:false
            });

    function showCreateNewAssocTypeForm() {
       createDialog.dialog("open")
    }

    function submitAssocTypeForm() {
        var parameters = jQuery.param($(".ui-dialog:visible #assocTypeForm input"));
        $(".ui-tabs-panel:not(.ui-tabs-hide)").load("${webappPath}/tenant/assoctypes?" + parameters);
        createDialog.dialog("close")
    }

</script>


<h2>Assoc Type Manager</h2>

<p>
    Use the assoc type manager to create <easyrec:wikiLink name="Assoc Types" pageName="assoc type"/>.
    Assoc types are used to define the relation between two items. As an Administrator you can set specific
    Plugins for each assoc type.
</p>


    <div class="info">
        <b>Note:</b> Once an Assoc Type has been created it cannot be removed!
    </div>

    <c:if test="${error!= null}">
        <div class="error">${error}</div>
    </c:if>

    <br>

    <h3>Assoc Types for ${tenantId}</h3>
    <a href="javascript:void(0)" onclick="showCreateNewAssocTypeForm()">
        <img src="${webappPath}/img/cluster_manager_plus.png"/>
        add new Assoc Type
    </a>

    <display:table name="assocTypes" class="tableData" id="row" pagesize="0">
        <display:column title="Assoc Type Name" sortable="false">
            ${row}
        </display:column>
    </display:table>


<div id="createNewAssocTypeForm" style="display:none;">
    <h1>Create a new assoc type</h1>

    <p>
        Here you can create a new assoc type for your tenant. After creating it here you are ready to
        use it in API calls.
    </p>

    <div>
        <form id="assocTypeForm" onsubmit="submitAssocTypeForm(); return false;">
            <input type="hidden" name="tenantId" value="${tenantId}"/>
            <input type="hidden" name="operatorId" value="${operatorId}"/>

            <label for="assocTypeName"> Enter the name of the new assoc type </label>
            <input type="text" id="assocTypeName" name="assocTypeName"/>

            <a style="float:right;" href="javascript:void(0)" onclick="submitAssocTypeForm();">
                <img src="${webappPath}/img/button_create.gif">
            </a>
        </form>
    </div>
</div>

