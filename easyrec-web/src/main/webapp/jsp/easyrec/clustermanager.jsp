<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="easyrec" uri="/WEB-INF/tagLib.tld" %>
<%--
  ~ Copyright 2010 Research Studios Austria Forschungsgesellschaft mBH
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
<script src="${webappPath}/js/easyrec.js" type="text/javascript"></script>
<script type="text/javascript">
var tenantId = "${tenantId}";
var operatorId = "${operatorId}"
var selectedCluster = 0;
var draggedId = 0;
var draggedType = "";

function startDrag(id, type) {
    draggedId = id;
    draggedType = type;
}

function displayError(errorText) {

    $("#errorMessage").html("<img src='${webappPath}/img/error.png' style='float:left;'>" + errorText);

    $("#errorMessage").dialog(
            {
                modal:true,
                title:"Error",
                width:400,
                height:200,
                buttons: {
                    "Okay" : function() {
                        $(this).dialog('close');
                    }
                }
            });
}

function initResizeables() {
    if ($('#clusterTabs').size() > 0) {
        $("#clusterManagerViewItems").resizable("destroy");
        $("#clusterManagerViewItems").resizable({
            handles: 'e',
            maxWidth: $("#clustermanagerDialog").width() - $("#clusterManagerTreeView").width() - 300,
            resize: function(event, ui) {
                manageResize();
            }
        });
    }

}

function showImportForm() {
    $("#uploadForm").dialog(
            {
                modal:true,
                title:"Cluster Import",
                width:430,
                height:200,
                minHeight: 200,
                minWidth: 430
            });
}

function startClusterManager() {

    $.ajax({
        url: "${webappPath}/clustermanager/clustermanager",
        data: ({tenantId : tenantId , operatorId:operatorId}),
        cache: false,
        dataType: "html",
        success: function(data) {
            $("#clusterManager").html(data);
        }
    });

    $("#clusterManager").dialog(
            {
                modal:true,
                title:"Cluster Manager",
                width:"90%",
                height:700,
                minHeight: 700,
                minWidth: 1110,
                resize: function(event, ui) {
                    manageResize();
                    initResizeables();
                },
                focus: function(event, ui) {
                    manageResize();
                },
                open: function(event, ui) {
                    if (typeof window.reloadTree == 'function') {
                        reloadTree();
                    }

                    manageResize();
                }
            });
}

function manageResize() {
    $('#clusterManager')[0].parentNode.id = "clustermanagerDialog";

    $('#clusterManagerIndex').width($('#clustermanagerDialog').width() - 30);
    $('#clusterManagerIndex').height($('#clustermanagerDialog').height() - 50);

    var treeHeight = $('#clusterManagerIndex').height() - $('#clusterTreeMenu').height() - 15;
    $('#clusterTreeLoader').height(treeHeight + 15);
    $('#clusterTreeLoader').width($('#clusterTree').width() + 15);
    $('#clusterTree').height(treeHeight);

    var tabContentHeight = $('#clusterManagerIndex').height() - 80;
    $('#editCluster').height(tabContentHeight);
    $('#viewItems').height(tabContentHeight);

    if ($('#viewClusterItems').length != 0) {
        $('#viewClusterItems').height(tabContentHeight - $('#viewClusterItems')[0].offsetTop + 50);
    }
    if ($('#searchresult').length != 0) {
        $('#searchresult').height(tabContentHeight - $('#searchresult')[0].offsetTop + 70);
    }

    if ($("#clusterManagerViewItems").width() < 200) {
        $("#clusterManagerViewItems").width(0);
        $("#clusterTabs").hide();
    } else {
        $("#clusterTabs").show();
    }

    if ($("#clusterManagerViewItems").length != 0) {
        var usedSpace = $("#clusterManagerViewItems").width() + $("#clusterManagerViewItems")[0].offsetLeft + 10;
        $("#clusterManagerSearchItems").width($("#clusterManagerIndex").width() - usedSpace);
    }
}

function loadCluster(tenantId, clusterId) {
    if (clusterId != "CLUSTERS") {
        selectedCluster = clusterId;

        $.ajax({
            url: '${webappPath}/clustermanager/viewitems?clusterId=' + clusterId + '&tenantId=' + tenantId +
                    '&operatorId=' + operatorId,
            cache: false,
            dataType:'text',
            success: function(data) {
                $('#clusterManagerViewItems').html(data);
                manageResize();
            }
        });
    } else {
        selectedCluster = "";
        $('#clusterManagerViewItems').load('${webappPath}/clustermanager/help');
    }
}

function changeClusterParent(clusterId, newParent) {

    displayLoader();

    $.ajax({
        url: '${webappPath}/clustermanager/ajax/changeclusterparent?clusterId=' + clusterId + '&newParent=' +
                newParent + '&tenantId=' + tenantId + '&operatorId=' + operatorId,
        cache: false,
        success: function(data) {
            reloadTree();
        }
    });

}

function createCluster(clusterId, parent) {

    displayLoader();

    $.ajax({
        url: '${webappPath}/clustermanager/ajax/createcluster?clusterId=' + clusterId + '&parent=' + parent +
                '&tenantId=' + tenantId + '&operatorId=' + operatorId,
        cache: false,
        success: function(data) {
            reloadTree();
            if (data != "") {
                displayError(data);
            }
        }
    });
}

function updateCluster() {
    displayLoader();
    originalClusterId = $('#originalClusterId').val();
    clusterId = $('#clusterName').val();
    clusterDescription = $('#description').val();

    $.ajax({
        url: '${webappPath}/clustermanager/ajax/updatecluster?clusterId=' + clusterId + '&clusterDescription=' +
                clusterDescription + '&tenantId=' + tenantId + '&originalClusterId=' + originalClusterId +
                '&operatorId=' + operatorId,
        cache: false,
        success: function(data) {
            reloadTree();
            if (data != "") {
                displayError(data);
            } else {
                $('#originalClusterId').val(clusterId);
                $('#success').html("The changes were successfully saved.").show().delay(5000).fadeOut('slow');
            }
        }
    });
}

function deleteClusterWarning() {
    $("<div>Do you really want to remove this cluster?</div>").dialog({
        bgiframe: true,
        height: 180,
        modal: true,
        title: "Security Confirmation",
        buttons: {
            "yes, remove" : function() {
                $("#clusterTree").jstree("remove");
                $(this).dialog('close');
            },
            "abort": function() {
                $(this).dialog('close');
            }
        },
        open: function() {
            $('.ui-dialog-buttonset button').first().blur();
            $('.ui-dialog-buttonset button').last().focus();
        },
        close: function() {
            $(this).dialog("destroy");
        }



    });

}


function deleteCluster(clusterId) {

    displayLoader();

    $.ajax({
        url: '${webappPath}/clustermanager/ajax/deletecluster?clusterId=' + clusterId + '&tenantId=' + tenantId +
                '&operatorId=' + operatorId,
        cache: false,
        success: function(data) {
            reloadTree();
            if (data != "") {
                displayError(data);
            }
        }
    });

}

function addItemToCluster(itemType, itemId, clusterID) {

    if (typeof clusterID == "undefined") {
        clusterID = selectedCluster;
    }

    $.ajax({
        url: '${webappPath}/clustermanager/ajax/additemtocluster?clusterId=' + clusterID + '&tenantId=' + tenantId +
                '&itemId=' + itemId + '&itemType=' + itemType + '&operatorId=' + operatorId,
        cache: false,
        success: function(data) {
            reloadClusterItemTable();
            if (data != "") {
                displayError(data);
            }
        }
    });
}

function removeItemFromCluster(itemType, itemId, clusterID) {

    if (typeof clusterID == "undefined") {
        clusterID = selectedCluster;
    }

    $.ajax({
        url: '${webappPath}/clustermanager/ajax/removeitemfromcluster?clusterId=' + clusterID + '&tenantId=' +
                tenantId + '&itemId=' + itemId + '&itemType=' + itemType + '&operatorId=' + operatorId,
        cache: false,
        success: function(data) {
            reloadClusterItemTable();
            if (data != "") {
                displayError(data);
            }
        }
    });
}

function reloadClusterItemTable(href) {

    if (typeof href == "undefined") {
        href = "${webappPath}/clustermanager/ajax/clusteritemtable?clusterId=" + selectedCluster + "&tenantId=" +
                tenantId + '&operatorId=' + operatorId;
    }

    $.ajax({
        url: href,
        cache: false,
        dataType:'text',
        success: function(data) {
            $('#viewClusterItems').html(data);
            rewriteClusterItemSorting('#viewClusterItems');
        }
    });
}

function rewriteClusterItemSorting(containerDiv) {
    var table = $(containerDiv).find("table.tableData");

    var reloadInDiv = function() {
        $(this).click(function() {
            reloadClusterItemTable($(this).attr('href'));

            return false;
        })
    };

    table.find('th.sortable > a').each(reloadInDiv);
    $(containerDiv).find('span.pagelinks > a').each(reloadInDiv);
}

</script>

<div class="appendbody">
    <jsp:include page="menu.jsp"/>
    <jsp:include page="menubar.jsp"/>
    <h2>Cluster Manager</h2>

    <p>
        Use the <easyrec:wikiLink name="cluster manager" pageName="Cluster"/> to create, delete, move and populate clusters.
        You can use the clustering system to create recommendations based on your categories e.g. for special sales.
    </p>

    <p>
        Use the <easyrec:wikiLink name="cluster API" pageName="REST_API_v0.96#Cluster"/> to get recommendations based on the clusters here.
    </p>

    <p>
        If you want to import the content of a cluster you can
        <easyrec:wikiLink name="create a CSV file for the cluster upload" pageName="Cluster#Cluster_CSV_import"/>
        and upload it here.
    </p>


    <div style="text-align: center; padding-top: 20px">
        <span class="clusterManagerStartButton" style="margin-right: 50px;">
            <a href="javascript:void(0)" onclick="startClusterManager()">
                <img alt="start the cluster manager" src="${webappPath}/img/cluster_manager.png"/>
                <span>start the cluster manager</span>
            </a>
        </span>
        <span class="clusterManagerStartButton">
            <a href="javascript:void(0)" onclick="showImportForm()" style="clear:right">
                <!-- image by Bdate Kaspar/Franziska Sponsel -->
                <img alt="import clusters from CSV" src="${webappPath}/img/button_csv.png" style="margin-top: 81px"/>
                <span>populate clusters from CSV</span>
            </a>
        </span>
    </div>

    <div id="clusterManager"></div>
    <div id="errorMessage"></div>
    <div id="uploadForm" style="display:none;">
        <jsp:include page="../clustermanager/import/importForm.jsp"/>
    </div>
</div>