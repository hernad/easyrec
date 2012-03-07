<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script src="${webappPath}/js/register.js" type="text/javascript"></script>
<div class="upperbody">
    <span class="headlineBig">Update ${operatorId}'s Password</span><br/><br/>
    <table border="0" cellspacing="0" cellpadding="0">
        <tr class="registeroperator">
            <td>Old Password</td>
            <td><input type="password" id="oldPassword"/></td>
            <td class="red" id="fieldStatus-oldPassword"></td>
        </tr>
        <tr class="registeroperator">
            <td>New Password</td>
            <td><input type="password" id="newPassword"/></td>
            <td class="red" id="fieldStatus-newPassword"></td>
        </tr>
        <tr class="registeroperator">
            <td>Confirm Password&nbsp;&nbsp;&nbsp;</td>
            <td><input type="password" id="confirmPassword"/></td>
            <td class="red" id="fieldStatus-confirmPassword"></td>
        </tr>
    </table>
    <div style="padding-top: 15px;">
        <div style="float: left;">
            <a onclick="javascript:updatePassword('${operatorId}');" href="#">
                <img alt="update" src="${webappPath}/img/update.gif"/>
            </a>

            <div style="display: none;" id="PasswordMessage">
                            <span class="headlineBig">
					Password updated successfully!
                            </span>
                <img alt="success" style="padding-left: 20px;" src="${webappPath}/img/success.gif"/>
            </div>
        </div>
    </div>
</div>