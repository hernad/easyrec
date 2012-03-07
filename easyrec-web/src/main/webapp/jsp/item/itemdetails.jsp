<%@ page language="java" session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="esapi" uri="/WEB-INF/esapi.tld" %>
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

<%--@elvariable id="webappPath" type="java.lang.String"--%>
<%--@elvariable id="detailBoxUniqueId" type="java.lang.String"--%>
<%--@elvariable id="editEnabled" type="boolean"--%>
<%--@elvariable id="operatorId" type="java.lang.String"--%>
<%--@elvariable id="tenantId" type="java.lang.String"--%>
<%--@elvariable id="tenant" type="org.easyrec.model.web.RemoteTenant"--%>
<%--@elvariable id="item" type="org.easyrec.model.web.Item"--%>
<%--@elvariable id="clusters" type="java.util.List<org.easyrec.model.core.ClusterVO>"--%>

<div id="itemTabs${detailBoxUniqueId}" class="itemDetailTabContainer">
    <ul>
        <c:if test="${editEnabled}">
            <li><a href="#editItem${detailBoxUniqueId}"><span>Edit</span></a></li>
        </c:if>
        <li><a href="#viewItem${detailBoxUniqueId}"><span>Details</span></a></li>
        <li>
            <a href="${webappPath}/tenant/rulestoitem?operatorId=${operatorId}&tenantId=${tenantId}&itemId=${item.id}"><span>Rules</span></a>
        </li>
        <li>
            <a href="${webappPath}/item/viewitemstatistics?operatorId=${operatorId}&tenantId=${tenantId}&itemId=${item.id}"><span>Statistics</span></a>
        </li>
    </ul>

    <c:if test="${editEnabled}">
        <div id="editItem${detailBoxUniqueId}">
            <div style="height:165px;">
                <div>
                    The url of the item can be relative or absolute. If you want to use an absolute url,
                    the url may start with <strong>"http://"</strong> or <strong>"https://"</strong>.
                    If you are using a relative url it will be concatenated with the current tenant url:
                    <a href="${tenant.url}" target="_blank">${tenant.url}</a> If the tenant url is part
                    of the item url, only the relative url will be displayed.
                </div>
                <div class="success" style="display:none;">

                </div>
            </div>

            <label for="editItemDescription${item.id}">description</label>
            <input id="editItemDescription${item.id}" name="editItemDescription${item.id}" type="text"
                   value="${esapi:encodeForHTMLAttribute(item.description)}"/>

            <label for="editItemUrl${item.id}">item url</label>
            <input id="editItemUrl${item.id}" name="editItemUrl${item.id}" type="text"
                   value="${esapi:encodeForHTMLAttribute(item.relativeUrl)}"/>

            <label for="editItemImageUrl${item.id}">item image url</label>
            <input id="editItemImageUrl${item.id}" name="editItemImageUrl${item.id}" type="text"
                   value="${esapi:encodeForHTMLAttribute(item.relativeImageUrl)}"/>

            <a style="float:right;" href="javascript:void(0);"
               onclick="saveItem('${operatorId}','${tenantId}','${item.id}', '${tenant.url}','${detailBoxUniqueId}')">
                <img src="${webappPath}/img/button_save.png" alt="save"/>
            </a>
        </div>
    </c:if>

    <div id="viewItem${detailBoxUniqueId}">
        <div class="itemDetailImage">
            <img id="image${item.id}" src="${esapi:encodeForHTMLAttribute(item.imageUrl)}" alt="detail image for the item">
        </div>

        <p style="height:120px;">
            Here you find some details about the selected item.
            Use the Edit tab to modify the values.
        </p>

        <dl>
            <dt>ID:</dt>
            <dd>${esapi:encodeForHTML(item.itemId)}</dd>

            <dt>Type:</dt>
            <dd>${item.itemType}</dd>

            <dt>Description:</dt>
            <dd id="itemDescription${item.id}">${esapi:encodeForHTML(item.description)}</dd>

            <dt>Creation Date:</dt>
            <dd>${item.creationDate}</dd>

            <dt>URL:</dt>
            <dd id="itemUrl${item.id}">
                <a href="${esapi:encodeForHTMLAttribute(item.url)}" target="_blank">
                    ${esapi:encodeForHTML(item.url)}
                </a>
            </dd>

            <dt>Image URL:</dt>
            <dd id="itemImageUrl${item.id}">
                <a href="${esapi:encodeForHTMLAttribute(item.imageUrl)}" target="_blank">
                    ${esapi:encodeForHTML(item.imageUrl)}
                </a>
            </dd>

            <dt>Active:</dt>
            <dd id="itemActive">${item.active}</dd>

            <dt>Clusters:</dt>
            <dd id="itemClusters">
                <c:choose>
                    <c:when test="${clusters == null || fn:length(clusters) == 0}">
                        None
                    </c:when>
                    <c:otherwise>
                        <ul style="display:inline">
                            <c:forEach items="${clusters}" var="cluster">
                                <li><span title="${cluster.description}">${cluster.name}</span></li>
                            </c:forEach>
                        </ul>
                    </c:otherwise>
                </c:choose>
            </dd>
        </dl>

    </div>

</div>
