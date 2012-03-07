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

    <span class="headlineBig">Create a new Tenant</span>

    <p>
        Here you can create a new <easyrec:wikiLink name="tenant"/>. The tenant name will be used in
        <a href="${webappPath}/API">API</a> calls to identify your website when you send
        actions or receive recommendations. If you want to test easyrec on your local machine
        use http://127.0.0.1/ as URL. If you are not sure how to start with easyrec read the
        <easyrec:wikiLink name="Getting Started"/> guide.
    </p>

    <div id="createTenantForm" class="contentContainer">
        <div id="error" class="error" style="display:none;"></div>
        <form id="registerOperator" action="">
            <label for="tenantId">tenant ID:</label>

            <p>
                The tenant ID is used to identify your website in API calls. This ID cannot be changed afterwards.<br>
                Use a machine readable name only containing the following characters: A-Z a-z 0-9 and _
            </p>
            <input id="tenantId" name="tenantId" type="text" size="40" maxlength="50"/>

            <label for="url">URL:</label>

            <p>
                Enter the URL of your website here. The easyrec service will automatically
                add this URL to relative URLs you send via the API calls. You will be able
                to edit this value later in the tenant configuration.
            </p>
            <input id="url" name="url" type="text" size="40" value="http://"/>

            <label for="easyrec_description">Description:</label>

            <p>
                Provide an optional description of your tenant (max 250 chars). You will be able
                to edit this value later in the tenant configuration.
            </p>

            <div id="charCountInfo">

            </div>
            <textarea id="easyrec_description" name="easyrec_description" type="text"></textarea>

            <a id="createTenantSubmit" onclick="registerTenant('${operatorId}')" href="#">
                <img alt="create" src="${webappPath}/img/button_create.gif"/>
            </a>
        </form>
    </div>
</div>


<div id="followingSteps" style="display:none;">
    <img src="${webappPath}/img/success.gif" style="float:left;"/>

    <p>
        Your tenant was created successfully! Now you can use your tenant ID and the API key to
        send actions to the easyrec service.
    </p>

     <jsp:include page="followingsteps.jsp"/>
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