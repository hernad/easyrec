<%@ taglib prefix="texta" uri="/WEB-INF/tagLib.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net" %>
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

<%--@elvariable id="webappPath" type="java.lang.String"--%>
<%--@elvariable id="tenantId" type="java.lang.String"--%>
<%--@elvariable id="operatorId" type="java.lang.String"--%>
<%--@elvariable id="description" type="java.lang.String"--%>
<%--@elvariable id="itemId" type="java.lang.String"--%>
<%--@elvariable id="fromCreationDate" type="java.lang.Date"--%>
<%--@elvariable id="toCreationDate" type="java.lang.Date"--%>
<%--@elvariable id="itemTypes" type="java.lang.String[]"--%>
<%--@elvariable id="hasRules" type="java.lang.Boolean"--%>
<%--@elvariable id="availableAssocTypes" type="java.lang.Set<java.lang.String>"--%>
<%--@elvariable id="availableItemTypes" type="java.lang.Set<java.lang.String>"--%>
<%--@elvariable id="rulesOfType" type="java.lang.String"--%>
<%--@elvariable id="isActivated" type="java.lang.Boolean"--%>
<%--@elvariable id="pageSize" type="int"--%>
<%--@elvariable id="totalCount" type="int"--%>
<%--@elvariable id="rulesOfType" type="java.lang.String"--%>
<%--@elvariable id="rulesOfType" type="java.lang.String"--%>
<%--@elvariable id="rulesOfType" type="java.lang.String"--%>

<script src="${webappPath}/js/easyrec.js" type="text/javascript"></script>
<script src="${webappPath}/js/item.js" type="text/javascript"></script>

<div class="appendbody">
    <jsp:include page="menu.jsp"/>
    <jsp:include page="menubar.jsp"/>
    <span class="headlineBig">Items collected by "${tenantId}"</span>
    <br/>

    <p>
        The list below contains all items submitted to easyrec via actions sent from your website. You can view item
        details
        by clicking on the item description.
    </p>

    <div class="contentContainer">
        <script type="text/javascript" src="${webappPath}/js/jquery/tagit.js"></script>
        <script type="text/javascript" src="${webappPath}/js/jquery/autocomplete.js"></script>

        <form action="${webappPath}/tenant/items" method="get" id="searchForm">
            <input type="hidden" name="tenantId" value="${tenantId}"/>
            <input type="hidden" name="operatorId" value="${operatorId}"/>
            <input type="hidden" name="d-16544-s" value="0"/>
            <input type="hidden" name="d-16544-p" value="1"/>

            <p>
                <label for="description">Description</label>
                <input name="description" id="description" type="text" value="${description}" style="width:80%;"/>
                <a href="javascript:void(0);"
                   onclick="toggleAdvancedSearch('#searchAdvanced');"
                   style="float:right;padding-top:4px;clear:right;">
                    Advanced Search
                </a>
            </p>

            <div id="searchAdvanced"
                 <c:if test="${itemId == '' and fromCreationDate == '' and toCreationDate == '' and
                 fn:length(itemTypes) == 0 and isActivated == null}">style="display:none"</c:if>>
                <p>
                    <label for="itemId">Id</label>
                    <input name="itemId" id="itemId" type="text" value="${itemId}"/>
                </p>

                <p>
                    <label for="fromCreationDate">Creation Date (from/to)</label>
                    <input name="fromCreationDate" id="fromCreationDate" type="text" value="${fromCreationDate}"/>
                    <label for="toCreationDate" style="display:none">Creation Date (from/to)</label>
                    <input name="toCreationDate" id="toCreationDate" type="text" value="${toCreationDate}"/>
                </p>

                <p>
                    <label for="isActivated">Activated</label>
                    <span>Display</span>
                    <select id="isActivated" name="isActivated">
                        <option value="null" <c:if test="${isActivated == null}">selected="selected"</c:if>>
                            all
                        </option>
                        <option value="true"
                                <c:if test="${isActivated == 'true'}">selected="selected"</c:if>>
                            only activated
                        </option>
                        <option value="false"
                                <c:if test="${isActivated != null && isActivated != 'true'}">selected="selected"</c:if>>
                            only deactivated
                        </option>
                    </select>
                    <span> items.</span>
                </p>

                <div>
                    <label for="itemType">Item Type</label>
                </div>
                <%--suppress HtmlUnknownAttribute --%>
                <ul id="itemType" name="itemTypes">
                    <c:forEach items="${itemTypes}" var="itemType">
                        <li>${itemType}</li>
                    </c:forEach>
                </ul>
            </div>

            <div style="float:left; clear:left;">
                <p style="width:350px;">
                    <label for="hasRules" style="display:inline;">Has Rules</label>
                    <input type="checkbox" id="hasRules" name="hasRules" style="width:14px;display:inline"
                           <c:if test="${hasRules == true}">checked="checked"</c:if>/>
                    <label for="rulesOfType" style="display:inline">of type</label>
                    <select id="rulesOfType" name="rulesOfType"
                            <c:if test="${hasRules == false}">disabled="disabled"</c:if>>
                        <c:forEach items="${availableAssocTypes}" var="assocType">
                            <option <c:if test="${assocType == rulesOfType}">selected="selected"</c:if>
                                    value="${assocType}">${assocType}</option>
                        </c:forEach>
                    </select>
                </p>
            </div>

            <input type="image" src="${webappPath}/img/button_search.png" id="submitSearch" name="submit"
                   value="search"/>
        </form>

        <script type="text/javascript">
            var availableItemTypes = [<c:forEach items="${availableItemTypes}" var="itemType">"${itemType}",
                </c:forEach>""];

            $('#itemType').tagit({select:true, tagSource:availableItemTypes});
            $("#fromCreationDate").datepicker();
            $("#toCreationDate").datepicker();

            $("#hasRules").click(function() {
                $("#rulesOfType").attr("disabled", !$(this).attr("checked"));
            });

            function toggleAdvancedSearch(id) {
                $(id).slideToggle('fast');
            }

            function loadResultsTable(url) {
                $.ajax({
                            url: url,
                            cache: false,
                            dataType:'text',
                            success: function(data) {
                                $('#searchresult').html(data);
                                rewriteSorting('#searchresult');
                            }
                        });
            }

            function rewriteSorting(containerDiv) {
                var table = $(containerDiv).find("table.tableData");

                var reloadInDiv = function() {
                    $(this).click(function() {
                        loadResultsTable($(this).attr('href'));

                        return false;
                    })
                };

                table.find('th.sortable > a').each(reloadInDiv);
                $(containerDiv).find('span.pagelinks > a').each(reloadInDiv);
            }
        </script>
    </div>

    <div style="width:707px;float:right;">
        <display:table name="itemSearchResult" class="tableData" id="row"
                       requestURI="${webappPath}/tenant/items?tenantId=${tenantId}&operatorId=${operatorId}"
                       pagesize="${pageSize}" sort="external"
                       partialList="true" size="${totalCount}">
            <display:setProperty name="paging.banner.group_size" value="24"/>
            <display:column title="Id" sortable="true">
                ${esapi:encodeForHTML(row.itemId)}
            </display:column>
            <display:column title="Description" sortable="true">
                <a href="javascript:void(0);"
                   onclick="loadItem('${operatorId}','${tenantId}','${row.id}','${esapi:encodeForJavaScript(row.description)}', true, undefined);">
                        ${esapi:encodeForHTML(row.description)}
                </a>
            </display:column>
            <display:column title="Type" sortable="true">
                ${esapi:encodeForHTML(row.itemType)}
            </display:column>
            <display:column style="width:120px;">     <%-- hint: the easyrec:absoluteUrl tag also does esapi encoding for the url!!!--%>
                <a href="<easyrec:absoluteUrl operatorId="${operatorId}" tenantId="${tenantId}" itemUrl="${row.url}"/>" target="_blank">
                    <img alt="open item in new tab" title="open this item in new tab" src="${webappPath}/img/button_globe.png"/>
                </a>
                <a href="javascript:void(0);"
                   onclick="loadItem('${operatorId}','${tenantId}','${row.id}','${esapi:encodeForJavaScript(row.description)}', true, 2)">
                    <img alt="show rules" title="show the rules for this item" src="${webappPath}/img/button_rules.png"/>
                </a>

                <c:choose>
                    <c:when test="${row.active}">
                        <a id="item_status-${item.id}"
                           href="javascript:deactivateItem('${operatorId}','${tenantId}','${row.id}')">
                            <img alt="deactivate" title="deactivate this item" src="${webappPath}/img/button_deactivate.png"/>
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a id="item_status-${item.id}"
                           href="javascript:activateItem('${operatorId}','${tenantId}','${row.id}')">
                            <img alt="activate" title="activate this item" src="${webappPath}/img/button_activate.png"/>
                        </a>
                    </c:otherwise>
                </c:choose>

            </display:column>
        </display:table>
        <h3>Legend</h3>
        <table>
            <tr>
                <td style="width:100px"><img alt="open item" title="open item in new tab" src="${webappPath}/img/button_globe.png"/></td>
                <td>Navigate to the item in a new tab (uses stored item URL)</td>
            </tr>
            <tr>
                <td style="width:100px"><img alt="show rules" title="show the rules for an item" src="${webappPath}/img/button_rules.png"/></td>
                <td>Show rules for an item</td>
            </tr>
            <tr>
                <td style="width:100px"><img alt="deactivate" title="deactivate item" src="${webappPath}/img/button_deactivate.png"/></td>
                <td>Toggle active flag for an item. Inactive items are not included in results of API calls</td>
            </tr>
        </table>

    </div>

</div>


