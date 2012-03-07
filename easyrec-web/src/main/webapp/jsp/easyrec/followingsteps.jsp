<%@ taglib prefix="easyrec" uri="/WEB-INF/tagLib.tld" %>
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

<p>
   Please select the appropriate tab fitting the technology you use for integrating easyrec
</p>

<div id="followingStepsTabs">
    <ul>
        <li><a href="#plugins"><span>Plugins</span></a></li>
        <li><a href="#jsApi"><span>JS API</span></a></li>
        <li><a href="#restApi"><span>REST API</span></a></li>
    </ul>

    <div id="plugins">
        <p>
            If you use a plugin or module to integrate easyrec into your website please read the
            documentation of your plugin/module to get started. Every plugin will most likely ask
            you for these three things to configure in your application:
        </p>


        <dl>
            <dt>easyrec Api Key:</dt>
            <dd>${apiKey}</dd>

            <dt>easyrec tenantId:</dt>
            <dd class="tenantId"></dd>

            <dt>easyrec URL:</dt>
            <dd>${extendedWebappPath}/</dd>
        </dl>


    </div>

    <div id="jsApi">
        <p>
            The <easyrec:wikiLink pageName="JavaScript_API_v0.96" name="JavaScript API"/> is a very easy to
            use way to integrate easyrec into your website. This solution uses a thin javascript layer on top of the
            REST API.
            You will need the following code on your website to start sending actions and receiving recommendations:
        </p>

        <code>
            &lt;script src="${extendedWebappPath}/api-js/easyrec.js" type="text/javascript"&gt;&lt;/script&gt;<br>

            &lt;script type="text/javascript"&gt;<br>
            &nbsp;&nbsp;&nbsp;var apiKey = "${apiKey}";<br>
            &nbsp;&nbsp;&nbsp;var tenantId = "<span class="tenantId"></span>";<br>
            &lt;/script&gt;
        </code>

    </div>

    <div id="restApi">
        <p>
            The <easyrec:wikiLink pageName="REST_API_v0.96" name="REST API"/> can be used in client-side code (as is
            the case with the javascript API) or in server side code.
            The following data is required for accessing the API:
        </p>

        <dl>
            <dt>easyrec Api Key:</dt>
            <dd>${apiKey}</dd>

            <dt>easyrec tenantId:</dt>
            <dd class="tenantId"></dd>

            <dt>easyrec URL:</dt>
            <dd>${extendedWebappPath}/</dd>
        </dl>
    </div>

</div>

<script type="text/javascript">
    $(document).ready(function() {
        $("#followingStepsTabs").tabs({ selected: 0 });
    });
</script>