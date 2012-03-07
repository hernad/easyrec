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

    var createDialog = $("#createNewItemTypeForm").dialog(
            {
                modal:true,
                title:"Create a new Item Type",
                width:430,
                height:300,
                resizable:false,
                autoOpen:false
            });

    function showCreateNewItemTypeForm() {
       createDialog.dialog("open")
    }

    function submitItemTypeForm() {
        var parameters = jQuery.param($(".ui-dialog:visible #itemTypeForm input"));
        $(".ui-tabs-panel:not(.ui-tabs-hide)").load("${webappPath}/tenant/itemtypes?" + parameters);
        createDialog.dialog("close")
    }

</script>

<div>
    <h2>Item Type Manager</h2>

    <p>
        Use the item type manager to create <easyrec:wikiLink name="Item types" pageName="item type"/>.
        Item types are the main categories of your items, e.g. Books, Music or Movies.
        After creating an item type you are ready to use it in API calls.
    </p>

    <p>
        (Clusters can be managed in the
        <a href="${webappPath}/tenant/clustermanager?tenantId=${tenantId}&operatorId=${operatorId}">Cluster Manager</a>
        and describe custom collections of items. Use it to create subcategories of item types
        (like "Pop", "Rock", "Alternative" as subcategories of Music items) or to create special recommendations like
        "Christmas" or "Spring Collection".)
    </p>


    <div class="info">
        <b>Note:</b> Once an itemtype has been created it cannot be removed!
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
    </display:table>


    <div id="createNewItemTypeForm" style="display:none;">
        <h1>Create a new item type</h1>

        <p>
            Here you can create a new item type for your tenant. After creating it here you are ready to
            use it in API calls.
        </p>

        <div>
            <form id="itemTypeForm" onsubmit="submitItemTypeForm(); return false;">
                <input type="hidden" name="tenantId" value="${tenantId}"/>
                <input type="hidden" name="operatorId" value="${operatorId}"/>

                <label for="itemTypeName"> Enter the name of the new item type </label>
                <input type="text" id="itemTypeName" name="itemTypeName"/>

                <a style="float:right;" href="javascript:void(0)" onclick="submitItemTypeForm();">
                    <img src="${webappPath}/img/button_create.gif">
                </a>
            </form>
        </div>
    </div>

</div>