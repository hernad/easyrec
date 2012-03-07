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


<div id="clusterTabs">
    <ul>
        <li><a href="#editCluster"><span>Edit</span></a></li>
        <li><a href="#viewItems"><span>Details</span></a></li>
    </ul>

    <div id="editCluster">
           <jsp:include page="editcluster.jsp"/>
    </div>

    <div id="viewItems">
        <h2>View Items in cluster "${cluster.name}"</h2>

        <div style="height:100px">
            This view displays all items currently associated with the cluster. You can add or remove
            items using the buttons <img src="${webappPath}/img/cluster_manager_plus.png" alt="button plus"> / <img src="${webappPath}/img/cluster_manager_minus.png" alt="button minus"> or by
            drag&drop using the drag handle <img src="${webappPath}/img/drag.png" alt="button drag"> from the search box on the right.
        </div>

        <div id="viewClusterItems">

        </div>

    </div>


</div>

<script type="text/javascript">
  $(document).ready(function() {
    $("#clusterTabs").tabs({ selected: 1 });
  });

  $( "#viewItems" ).droppable({
			activeClass: "drag-target",
			hoverClass: "drag-hover",
            accept:".dragItem",
			drop: function( event, ui ) {
                console.log(event);
                console.log(ui);
                addItemToCluster(draggedType,draggedId);
			}
  })

  initResizeables();
  reloadClusterItemTable();
</script>
