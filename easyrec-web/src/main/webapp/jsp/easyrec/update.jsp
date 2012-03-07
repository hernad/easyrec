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

<script src="${webappPath}/js/item.js" type="text/javascript"></script>
<script src="${webappPath}/js/easyrec.js" type="text/javascript"></script>

<div class="appendbody" style="min-height: 400px;">
    <jsp:include page="menu.jsp"/>

    <span class="headlineBig">Edit '${tenant.stringId}'</span>

    <p>
        Here you can edit the URL and description of your <easyrec:wikiLink name="tenant"/>.
    </p>

    <div id="createTenantForm" class="contentContainer">
        <div id="error" class="error" style="display:none;"></div>
        <form id="registerOperator" action="" onsubmit="javascript:return false;">
            <label for="url">URL:</label>

            <p>
                Enter the URL of your website here. The easyrec service will automatically
                add this URL to relative URLs you send via the API calls. You will be able
                to edit this value later in the tenant configuration.
            </p>

            <p>
                If you send absolute item URLs via the API, this setting does not affect the URLs stored for each
                item and it will be harder to move to a different domain. Use relative URLs for your items unless
                you have a good reason not to.
            </p>
            <input id="url" name="url" type="text" size="40" value="${tenant.url}"/>

            <label for="easyrec_description">Description:</label>

            <p>
                Provide an optional description of your tenant (max 250 chars). You will be able
                to edit this value later in the tenant configuration.
            </p>

            <div id="charCountInfo">

            </div>
            <textarea id="easyrec_description" name="easyrec_description" type="text">${tenant.description}</textarea>

            <a id="createTenantSubmit" onclick="updateTenant('${operatorId}', '${tenant.stringId}')" href="#">
                <img alt="update" src="${webappPath}/img/button_update.png"/>
            </a>
        </form>
    </div>
</div>


<script language="javascript">
    function limitChars(textid, limit, infodiv) {
        var text = $('#' + textid).val();
        var textlength = text.length;
        if (textlength > limit) {
            $('#' + infodiv).html('You cannot write more then ' + limit + ' characters!');
            $('#' + textid).val(text.substr(0, limit));
            return false;
        }
        else {
            $('#' + infodiv).html('You have ' + (limit - textlength) + ' characters left.');
            return true;
        }
    }

    $(function() {
        $('#easyrec_description').keyup(function() {
            limitChars('easyrec_description', 250, 'charCountInfo');
        })
        var textlength = $('#easyrec_description').val().length;
        $('#charCountInfo').html('You have ' + (250 - textlength) + ' characters left.');
    });
</script>

