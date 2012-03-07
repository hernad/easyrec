<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="easyrec" uri="/WEB-INF/tagLib.tld" %>

<div class="upperbody">
    <div id="operatoractivation">
        <c:choose>
            <c:when test="${success}">
                <table>
                    <tr>
                        <td style="padding-top:15px">
                            <span class="headlineBig">Activation Successful</span><br/><br/>
                        </td>
                        <td><img alt="success" src="img/success.gif"/></td>
                    </tr>
                </table>
                <span class="headline">Welcome ${operator.firstName} ${operator.lastName}!</span><br/>

                <p>
                    Your account has been activated!
                    Continue by creating a tenant for your website and sending actions to the easyrec service!
                </p>

                <p>
                    To get started with easyrec we recommend to read the <easyrec:wikiLink name="get started guide"/>
                    if you already understand the concept of easyrec visit our <a href="implement">API</a> section.
                </p>

                <p>Head over to the <a href="easyrec/overview?tenantId=EASYREC_DEMO">management</a> section to create a
                   tenant for your website.</p>
                <script type="text/javascript">
                    showLoggedIn();
                </script>
            </c:when>
            <c:otherwise>
                <table>
                    <tr>
                        <td style="padding-top:15px">
                            <span class="headlineBig">Activation Failed </span><br/><br/>
                        </td>
                        <td><img alt="error" src="img/error.png"/></td>
                    </tr>
                </table>
                <br/><br/>

                <p>A problem occurred during the activation of your account!</p>
                <br/><br/>

                <p>Please contact our support <a href="mailto:office@easyrec.org">office@easyrec.org</a>!</p>
            </c:otherwise>
        </c:choose>
    </div>
</div>	