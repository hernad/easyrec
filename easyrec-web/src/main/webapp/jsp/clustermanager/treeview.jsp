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


<div style="height:100%;">
    <div id="clusterTreeMenu">
        <a href="javascript:void(0);" onclick="createClusterTreeItem()"><img
                src="${webappPath}/img/cluster_manager_plus.png"/> add cluster</a>
        <a href="javascript:void(0);" onclick="deleteClusterTreeItem()"><img
                src="${webappPath}/img/cluster_manager_minus.png"/> remove cluster</a>
    </div>

    <div id="clusterTreeLoader" style="display:none;">
        &nbsp;
    </div>


    <div id="clusterTree">
    </div>
</div>

<script type="text/javascript">

    $.jstree._themes = "${webappPath}/css/";
    var JSON_DATA = "";
    var creatingItem=false;

    function displayLoader() {
        //$("#clusterTree").hide();
        $("#clusterTreeLoader").show();
    }

    function hideLoader() {
        //$("#clusterTree").show();
        $("#clusterTreeLoader").hide();
    }

    function reloadTree() {
        $("#clusterTree").jstree("destroy");
        $("#clusterTree").html("");

        $.ajax({
            url: webappPath + 'clustermanager/ajax/loadtreedata?tenantId=${tenantId}&operatorId='+operatorId,
            cache: false,
            dataType:'text',
            success: function(data) {
                JSON_DATA = $.parseJSON(data);
                createTree();
            }
        });


    }

    function createTree() {
        $(function () {
            $("#clusterTree").jstree({
                "ui" : {
                    "select_limit" : 1 ,
                    "initially_select" : [ "CLUSTERS" ]
                },
                "types" : {
                    "valid_children" : [ "root" ],
                    "types" : {
                        "root" : {
                            "valid_children" : [ "default" ],
                            "start_drag" : false,
                            "move_node" : false,
                            "delete_node" : false,
                            "remove" : false  ,
                            "icon" : {
                                "image" : "${webappPath}/img/home.png"
                            }
                        },
                        "default" : {
                            "valid_children" : [ "default" ]
                        }
                    }
                },
                "cookies":{
                    "save_opened": "open_${tenantId}",
                    "save_selected":"selected_${tenantId}"
                },
                "dnd" : {
                    "drag_check" : function (data) {
                        if (data.r.attr("id") == "phtml_1") {
                            return false;
                        }
                        return {
                            after : false,
                            before : false,
                            inside : true
                        };
                    }
                },
                "themes" : {
                    "theme" : "tree-theme",
                    "dots" : true,
                    "icons" : true
                },
                "json_data" : {
                    "data" : [ JSON_DATA ]
                },
                 "core" : { "initially_open" : [ "CLUSTERS" ] },
                "plugins" : [ "themes", "json_data", "dnd","ui" ,"crrm","cookies","types"]
            });
        });

        //initiate the javascript tree functions

        $("#clusterTree").bind("move_node.jstree", function(event, data) {
            changeClusterParent(data.rslt.o.attr("id"), data.rslt.o.parent().parent().attr("id"));
        })

        $("#clusterTree").bind("create.jstree", function(event, data) {
            var newName = data.rslt.name;
            var parentName = data.rslt.obj.parent().parent().attr("id");

            createCluster(newName, parentName);
        })

        $("#clusterTree").bind("remove.jstree", function(event, data) {
            var name = data.rslt.obj[0].id;
            deleteCluster(name);
        })

        hideLoader();
        setTimeout(finishClusterCreation, 100);
    }



    function finishClusterCreation() {
       creatingItem=false;
    }

    function createClusterTreeItem() {
        if(!creatingItem){
            creatingItem=true;
            $("#clusterTree").jstree("create", null, "last",  { "data" : "NEW_CLUSTER" }, null, false);
        }
    }

    function deleteClusterTreeItem() {
        deleteClusterWarning();
    }


    reloadTree();

    $("#clusterTree").delegate("a", "click", function(e) {
            var id = $(this).parent().attr("id");
            loadCluster("${tenantId}", id);
    });

    $( "#clusterTree" ).droppable({
			activeClass: "drag-target",
			hoverClass: "drag-hover",
            accept:".dragItem",
			drop: function( event, ui ) {
                var clusterId = "";
                var eventObject = event.originalEvent.target;

                if(eventObject.nodeName == "A"){
                   clusterId =   eventObject.parentNode.id;
                }else if(eventObject.nodeName == "INS"){
                   clusterId = eventObject.parentNode.parentNode.id;
                }else{
                    return false;
                }

                if(clusterId == ""){
                    return false;
                }


                addItemToCluster(draggedType,draggedId,clusterId);
			}
  })

</script>
