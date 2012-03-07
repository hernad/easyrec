<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<jsp:include page="../header.jsp"/>
<body>
<script src="${webappPath}/js/register.js" type="text/javascript"></script>
<div class="fixed">
    <jsp:include page="../menu.jsp"/>
    <div class="install">
        <div style="float:right;position: absolute;right: 50px; top: 380px;">
            <img alt="people" src="img/home_ppl.gif"/>
        </div>
        <div id="registerSuccess" style="display: none">
            <table class="install">
                <tr>
                    <td style="padding-top:15px">
                        <span class="headlineBig">easyrec setup complete!</span><br/><br/>
                    </td>
                    <td>
                        <img alt="people" src="${webappPath}/img/success.gif"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        <p id="redirect">
                            Now, proceed to the <a href="javascript:redirect();">management section</a> of easyrec.
                        </p>
                    </td>
                </tr>
                <tr>
                    <td>
                        Once again: make sure to check out the recommended innodb settings at <a target="_blank"
                                                                                                 href="http://sourceforge.net/apps/mediawiki/easyrec/index.php?title=InnoDB_settings">
                        our Wiki!</a>
                    </td>
                </tr>
            </table>
        </div>
        <div id="registerOperator">
            <div class="headlineBig">Welcome to the easyrec installer v${currentVersion}</div>
            <br/>

            <div class="headline">Step 4/4</div>
            <table class="install">
                <tr>
                    <td><img alt="success" src="${webappPath}/img/success.gif"/></td>
                    <td style="padding-left: 55px">
                        easyrec has been configured successfully!<br/>
                        Finally please provide a user name and a
                        password you want to use to login.
                    </td>
                </tr>
            </table>
            <table border="0" cellspacing="0" cellpadding="0" class="install">
                <tr class="registeroperator">
                    <td/>
                    <td/>
                    <td class="red" id="register-error"></td>
                </tr>
                <tr class="registeroperator">
                    <td>User Name:</td>
                    <td><input id="operatorId" name="operatorId" type="text" size="40"/></td>
                    <td class="red" id="fieldStatus-operatorId"></td>
                </tr>
                <tr class="registeroperator">
                    <td>Password:</td>
                    <td><input id="password" name="password" type="password" size="40"/>&nbsp;(min. 5 chars)</td>
                    <td class="red" id="fieldStatus-password"></td>
                </tr>
                <tr class="registeroperator">
                    <td>Password Confirmation:</td>
                    <td><input id="passwordConfirm" name="passwordConfirm" type="password" size="40"/></td>
                    <td class="red" id="fieldStatus-passwordConfirm"></td>
                </tr>
                <tr class="registeroperator">
                    <td>First Name:</td>
                    <td><input id="firstName" name="firstName" type="text" size="40"/>&nbsp;(optional)</td>
                    <td class="red" id="fieldStatus-firstName"></td>
                </tr>
                <tr class="registeroperator">
                    <td>Last Name:</td>
                    <td><input id="lastName" name="lastName" type="text" size="40"/>&nbsp;(optional)</td>
                    <td class="red" id="fieldStatus-lastName"></td>
                </tr>
            </table>
            <br/>
            <button onclick="javascript:registerOperator();">create account</button>
            <div class="red"> ${exceptionMessage}</div>
        </div>
    </div>
    <jsp:include page="footer.jsp"/>
</div>
</body>
</html>