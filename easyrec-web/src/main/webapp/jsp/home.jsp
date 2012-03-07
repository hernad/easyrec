<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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

<div class="upperbody">
    <c:if test="${!signedIn}">
        <div id="loginBox" style="background-color: #ffffff">
            <span class="headline">Sign In</span>

            <div style="border: 1px solid #ccc;padding-bottom: 15px;padding-left: 10px; background-color: #eef9ff">
                <div style="padding-top: 15px;">
                    <div style="float:left;width: 80px;">User</div>
                    <div><input type="text" id="signInOperatorId" size="15" value=""/></div>
                </div>
                <div style="padding-top: 5px;">
                    <div style="float:left;width: 80px;">Password</div>
                    <div><input type="password" id="signInPassword" size="15" value=""/></div>
                </div>
            </div>
            <div style="padding-top: 15px;">
                <div style="float: left;">
                    <a onclick="signinUser();" href="#">
                        <img alt="sign in" src="${webappPath}/img/button_signin.gif"/>
                    </a>
                </div>
                <div style="float: right; padding-top: 7px;">
                    <div id="signinProgress"></div>
                </div>
                <br/><br/>

                <div class="red" style="padding-top: 10px;" id="signInMessage">
                </div>
            </div>
        </div>
    </c:if>
    <c:if test="${signedIn}">
        <div id="loginBox" style="background-color: #ffffff">
            <c:if test="${signedIn && (signedInOperator.firstName!='' || signedInOperator.lastName!='') }">
                <div class="headline">Hello ${signedInOperator.firstName} ${signedInOperator.lastName}!</div>
            </c:if>
            <div style="border: 1px solid #ccc;padding-bottom: 15px;padding-left: 10px; height:100px; background-color: #eef9ff">
                <div style="padding-top: 15px;">
                    <div style="float:left;height:100px">
                        You are signed in as '${signedInOperator.operatorId}'.<br/><br/>
                        Click <a href="javascript:signoutUser();">here</a> to sign out.
                    </div>
                </div>
            </div>
        </div>
    </c:if>

    <div class="blog">
        <br/><br/>

        <div id="elArticles"></div>
        <div style="width:500px" id="update"></div>
    </div>

    <div style="float:right;position: absolute;right: 50px; top: 350px;">
        <img alt="easyrec people" src="img/home_ppl.gif"/>
    </div>
</div>
<script src="${webappPath}/js/newsfeed.js" type="text/javascript"></script>
<script type="text/javascript">
    $(function()
    {
        $("input:text:visible:first").focus();

        // Check for easyrec updates
        checkUpdate('${updateUrl}', '${updateToken}');

        // Load news Feed
        showBlog();
    });
</script>