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

    var createDialog = $("#createNewActionTypeForm").dialog(
            {
                modal:true,
                title:"Create a new Action Type",
                width:430,
                height:300,
                resizable:false,
                autoOpen:false
            });

    function showCreateNewActionTypeForm() {
       createDialog.dialog("open")
    }

    function submitActionTypeForm() {
        var parameters = jQuery.param($.merge($( ".ui-dialog:visible" ).find("#actionTypeHasValue:checked"),$(".ui-dialog:visible #actionTypeForm input:not([type=checkbox])")));
        $(".ui-tabs-panel:not(.ui-tabs-hide)").load("${webappPath}/tenant/actiontypes?" + parameters);
        createDialog.dialog("close")
    }
</script>
<div>

    <h2>Action Type Manager</h2>

    <p>
        Use the item type manager to create <easyrec:wikiLink name="Action types" pageName="action type"/>.
        Action types are the types of your actions you send to easyrec.
        After creating an actions type you are ready to use it in API calls.
    </p>


        <div class="info">
            <b>Note:</b> Once an actiontype has been created it cannot be removed!
        </div>

        <c:if test="${error!= null}">
            <div class="error">${error}</div>
        </c:if>

        <br>

        <h3>Item Types for ${tenantId}</h3>
        <a href="javascript:void(0)" onclick="showCreateNewItemTypeForm()">
            <img src="${webappPath}/img/cluster_manager_plus.png"/>
            add new Item Type
        </a>

        <display:table name="itemTypes" class="tableData" id="row" pagesize="0">
            <display:column title="Item Type Name" sortable="false">
                ${row}
            </display:column>
            <display:column title="Has Value" sortable="false">
                ${valueMap[row]}
            </display:column>
        </display:table>


    <div id="createNewActionTypeForm" style="display:none;">
        <h1>Create a new action type</h1>

        <p>
            Here you can create a new action types for your tenant. After creating it here you are ready to
            use it in API calls.
        </p>

        <div>
            <form id="actionTypeForm" onsubmit="submitActionTypeForm(); return false;">
                <input type="hidden" name="tenantId" value="${tenantId}"/>
                <input type="hidden" name="operatorId" value="${operatorId}"/>

                <label for="actionTypeName"> Enter the name of the new item type </label>
                <input type="text" id="actionTypeName" name="actionTypeName"/>

                <br>

                <label for="actionTypeHasValue"> Do you want to use action type values? </label>
                <input type="checkbox" id="actionTypeHasValue" name="actionTypeHasValue" value="true"/> Yes


                <a style="float:right;margin-top:90px;" href="javascript:void(0)" onclick="submitActionTypeForm();">
                    <img src="${webappPath}/img/button_create.gif">
                </a>
            </form>
        </div>
    </div>

</div>
