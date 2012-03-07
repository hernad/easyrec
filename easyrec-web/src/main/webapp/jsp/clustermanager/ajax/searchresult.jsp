<%@ taglib prefix="display" uri="http://displaytag.sf.net" %>
<%@ taglib prefix="esapi" uri="/WEB-INF/esapi.tld" %>
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

<%--@elvariable id="webappPath" type="java.lang.String"--%>
<%--@elvariable id="operatorId" type="java.lang.String"--%>
<%--@elvariable id="tenantId" type="java.lang.String"--%>
<%--@elvariable id="totalCount" type="java.lang.Integer"--%>

<display:table name="itemSearchResult" class="tableData" id="row"
               requestURI="${webappPath}/clustermanager/ajax/searchresult" pagesize="25" sort="external"
               partialList="true" size="${totalCount}">
    <%--
    <display:setProperty name="pagination.sort.param" value="sort"/>
    <display:setProperty name="pagination.sortdirection.param" value="dir"/>
    <display:setProperty name="pagination.pagenumber.param" value="page"/>
      --%>

    <display:column title="Id" sortable="true">
        ${esapi:encodeForHTML(row.itemId)}
    </display:column>
    <display:column title="Description (Type)" sortable="true">
        <a href="javascript:void(0);"
           onclick="loadItem('${operatorId}','${tenantId}','${row.id}','${esapi:encodeForJavaScript(row.description)}');">
                ${esapi:encodeForHTML(row.description)}</a> (${esapi:encodeForHTML(row.itemType)})
    </display:column>
    <display:column style="width:20px;">
        <a href="javascript:void(0);" onclick="addItemToCluster('${row.itemType}','${row.itemId}')">
            <img title="add item to selected cluster" alt="add item to selected cluster"
                 src="${webappPath}/img/cluster_manager_plus.png"/>
        </a>
    </display:column>
    <display:column style="width:20px;">
        <a href="javascript:void(0);" class="dragItem" id="drag-${row.itemType}-${row.itemId}">
            <img title="drag this item" alt="drag this item" src="${webappPath}/img/drag.png"/>
        </a>
        <script type="text/javascript">
            $("#drag-${row.itemType}-${row.itemId}").draggable({
                cursor: "move",
                cursorAt: { top: -12, left: -20 },
                helper: function(event) {
                    startDrag('${row.itemId}', '${row.itemType}');
                    return $("<div class='ui-widget-header'>${row.description}</div>");
                }
            });
        </script>
    </display:column>
</display:table>
