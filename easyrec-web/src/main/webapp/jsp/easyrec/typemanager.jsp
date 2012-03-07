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


<script>
    $(function() {
        var $tabs = $("#typeManagerTabs").tabs({
            ajaxOptions: {
                error: function(xhr, status, index, anchor) {
                    $(anchor.hash).html(
                            "Couldn't load this tab. We'll try to fix this as soon as possible. ");
                }
            }
        });
    });
</script>


<div class="appendbody">
    <jsp:include page="menu.jsp"/>
    <jsp:include page="menubar.jsp"/>

    <h2>Type Manager</h2>

    <p>
        Use the <easyrec:wikiLink name="type manager" pageName="type_manager"/> to create different types used within
        your easyrec instance.
    </p>

    <div class="contentContainer">
        <div id="typeManagerTabs">
            <ul>
                <li>
                    <a href="${webappPath}/tenant/itemtypes?tenantId=${tenantId}&operatorId=${operatorId}">
                        Item Types
                    </a>
                </li>
                <li>
                    <a href="${webappPath}/tenant/actiontypes?tenantId=${tenantId}&operatorId=${operatorId}">
                        Action Types
                    </a>
                </li>
                <li>
                    <a href="${webappPath}/tenant/assoctypes?tenantId=${tenantId}&operatorId=${operatorId}">Assoc
                        Types
                    </a>
                </li>
            </ul>
        </div>
    </div>
</div>
