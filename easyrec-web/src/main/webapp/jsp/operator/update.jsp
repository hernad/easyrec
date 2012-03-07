<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script src="js/register.js" type="text/javascript"></script>
<div class="upperbody">

    <c:if test="${operator!=null}">
        <div id="updateSuccess" style="display: none">
            <table>
                <tr>
                    <td style="padding-right: 20px;">
                        <span class="headlineBig">Update Successful</span>
                    </td>
                    <td>
                        <img alt="success" src="img/success.gif">
                    </td>
                </tr>
            </table>
            <br/>
        </div>
        <form id="updateOperator" action="">
            <span class="headlineBig">Update your Account "${operator.operatorId}"</span><br/><br/>
            <table border="0" cellspacing="0" cellpadding="0">
                <tr class="registeroperator">
                    <td>First Name</td>
                    <td><input value="${operator.firstName}" id="firstName" name="firstName" type="text" size="40"/>
                    </td>
                    <td class="red" id="fieldStatus-firstName"></td>
                </tr>
                <tr class="registeroperator">
                    <td>Last Name</td>
                    <td><input value="${operator.lastName}" id="lastName" name="lastName" type="text" size="40"/></td>
                    <td class="red" id="fieldStatus-lastName"></td>
                </tr>
                <tr class="registeroperator">
                    <td>eMail</td>
                    <td><input value="${operator.email}" id="email" name="email" type="text" size="40"/></td>
                    <td class="red" id="fieldStatus-email"></td>
                </tr>
                <tr class="registeroperator">
                    <td>Phone</td>
                    <td><input value="${operator.phone}" id="phone" name="phone" type="text" size="40"/>&nbsp;(optional)
                    </td>
                    <td></td>
                </tr>
                <tr class="registeroperator">
                    <td>Company Name&nbsp;</td>
                    <td><input value="${operator.company}" id="company" name="company" type="text" size="40"/>&nbsp;(optional)
                    </td>
                    <td></td>
                </tr>
                <tr class="registeroperator">
                    <td>Address</td>
                    <td><input value="${operator.address}" id="address" name="address" type="text" size="40"/>&nbsp;(optional)
                    </td>
                    <td></td>
                </tr>
                <tr class="registeroperator">
                    <td><a href="${webappPath}/operator/changePassword">Change Password...</a></td>
                    <td></td>
                    <td></td>
                </tr>
                <tr class="registeroperator">
                    <td><br/><br/>
                        <a href="#" onClick="updateUser('${operator.operatorId}');return false;">
                            <img alt="update" src="${webappPath}/img/update.gif"/>
                        </a>
                    </td>
                    <td/>
                    <td/>
                </tr>
            </table>
        </form>
    </c:if>
</div>	