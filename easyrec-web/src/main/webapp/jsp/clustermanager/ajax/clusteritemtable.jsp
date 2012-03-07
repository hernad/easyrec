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

<display:table name="items" class="tableData" id="row" pagesize="25"
               requestURI="${webappPath}/clustermanager/ajax/clusteritemtable">
    <display:column title="id" sortable="true">
        ${esapi:encodeForHTML(row.itemId)}
    </display:column>
    <display:column title="Description" sortable="true">
        <a href="javascript:void(0);"
           onclick="loadItem('${operatorId}','${tenantId}','${row.id}','${esapi:encodeForJavaScript(row.description)}');">
                ${esapi:encodeForHTML(row.description)}
        </a>
    </display:column>
    <display:column title="Type" sortable="true">
        ${esapi:encodeForHTML(row.itemType)}
    </display:column>
    <display:column style="width:20px;">
        <a href="javascript:void(0);" onclick="removeItemFromCluster('${row.itemType}','${row.itemId}')">
            <img title="remove item from cluster" src="${webappPath}/img/cluster_manager_minus.png"/>
        </a>
    </display:column>
</display:table>
