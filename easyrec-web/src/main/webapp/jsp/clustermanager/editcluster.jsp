<%@ taglib prefix="easyrec" uri="/WEB-INF/tagLib.tld" %>
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
<div id="editCluster">
    <div id="success" style="display:none;"></div>


        Here you can change the name and description of the cluster. CAUTION: if you change a cluster's name you must also
        adjust your API calls! For every tenant each cluster name must be unique. You may find the
        <easyrec:wikiLink name="cluster naming tips" pageName="Cluster#Cluster_naming"/> helpful.


    <form id="editForm">
        <input type="hidden" id="originalClusterId" value="${esapi:encodeForHTMLAttribute(cluster.name)}">

        <label for="clusterName">Name</label>
        <input name="clusterName" id="clusterName" type="text" value="${esapi:encodeForHTMLAttribute(cluster.name)}"/>
        <br>

        <label for="description">Description</label>
        <textarea name="description" id="description">${esapi:encodeForHTML(cluster.description)}</textarea> <br>

        <input type="image" src="${webappPath}/img/button_save.png" id="submitCluster" value="save"/>
    </form>
</div>

<script>
    $("#editForm").submit(function() {
        updateCluster();
        return false;
    });
</script>