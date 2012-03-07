<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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

<img src="${webappPath}/img/search.png" style="float:left;"/>

<script type="text/javascript" src="${webappPath}/js/jquery/tagit.js"></script>
<script type="text/javascript" src="${webappPath}/js/jquery/autocomplete.js"></script>


<h2>Search Items</h2>

<form action="${webappPath}/clustermanager/ajax/searchresult" method="post" id="searchForm">
    <input type="hidden" name="tenantId" value="${tenantId}"/>
    <input type="hidden" name="operatorId" value="${operatorId}"/>
    <input type="hidden" name="d-16544-s" value="0"/>
    <input type="hidden" name="d-16544-p" value="1"/>

    <p>
        <label for="itemId">Id</label>
        <input name="itemId" id="itemId" type="text"/>
    </p>

    <p>
        <label for="description">Description</label>
        <input name="description" id="description" type="text"/>
    </p>

    <p>
        <label for="fromCreationDate">Creation Date (from/to)</label>
        <input name="fromCreationDate" id="fromCreationDate" type="text"/>
        <input name="toCreationDate" id="toCreationDate" type="text"/>
    </p>

    <div>
        <label for="itemType">Item Type</label>
    </div>
    <ul id="itemType" name="itemTypes"></ul>

    <input type="image" src="${webappPath}/img/button_search.png" id="submitSearch" name="submit" value="search"/>
</form>

<div id="searchresult">
</div>

<script type="text/javascript">
    var availableItemTypes = [<c:forEach items="${availableItemTypes}" var="itemType">"${itemType}",</c:forEach>""];

    $('#itemType').tagit({select:true, tagSource:availableItemTypes});
    $("#fromCreationDate").datepicker();
    $("#toCreationDate").datepicker();

    $("#searchForm").submit(function() {
        var params = {};

        $(this).find('input').each(function() {
            var name = $(this).attr('name');

            if (name.length != 0 && $(this).attr('type') != 'submit')
                params[name] = $(this).val();
        });

        var url = $(this).attr('action') + "?";


        for (var key in params) {
            url = url + key + "=" + encodeURIComponent(params[key]) + "&";
        }

        var itemTypes = $('#itemType').tagit('tags');
        for (var key in itemTypes) {
            url = url + "itemTypes=" + encodeURIComponent(itemTypes[key]) + "&";
        }

        loadResultsTable(url);

        return false;
    });

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