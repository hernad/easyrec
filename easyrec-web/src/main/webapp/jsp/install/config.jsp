<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<jsp:include page="../header.jsp"/>
<body>
<div class="fixed">
    <jsp:include page="../menu.jsp"/>
    <div class="install">
        <div class="headlineBig">Welcome to the easyrec installer v${currentVersion}</div>
        <br/>

        <div style="float:right;position: absolute;right: 50px; top: 380px;">
            <img alt="people" src="img/home_ppl.gif"/>
        </div>
        <div class="headline">Step 3/4</div>
        <div>
            <table class="install">
                <tr>
                    <td><img alt="success" src="${webappPath}/img/success.gif"/></td>
                    <td style="padding-left: 55px">
                        easyrec has successfully initialized the database!<br/>
                        Please choose the easyrec features you <br/>
                        want enabled on your easyrec server.
                    </td>
                </tr>
            </table>

            <form action="${action}" method="post" id="configform">
                <br/>
                <table class="install">
                    <tr>
                        <td>REST API:</td>
                        <td><input name="rest" type="checkbox" checked/></td>
                        <td>This is the standard API used to communicate<br/>
                            with the easyrec server through your web
                            application
                        </td>
                    </tr>
                    <tr>
                        <td>SOAP API:</td>
                        <td><input name="soap" type="checkbox"/></td>
                        <td>
                            Enable this API if you want to integrate easyrec as a SOAP
                            service in your webapplication.
                            <br/>

                        </td>
                    </tr>
                    <tr>
                        <td>DEV mode:</td>
                        <td><input name="dev" type="checkbox"/></td>
                        <td>
                            This mode enables detailed debug output and
                            enhanced monitoring at the cost of performance.<br/>
                            Activate only on easyrec instances intended for development use.
                            <br/>Use in production environments is strongly discouraged!
                        </td>
                    </tr>
                </table>
                <br/>
                <button onclick="javascript:loadcontext();">continue</button>
                <div style="background-color: #ffffff;margin-top: 5px" id="progress"></div>
            </form>
            <br/>

            <div class="red">${exceptionMessage}</div>
        </div>
    </div>
    <jsp:include page="footer.jsp"/>
</div>
<script type="text/javascript">
    function loadcontext()
    {
        $('#progress')
                .html('<p style="color:#586168"><img src="img/wait16.gif"/>&nbsp;Setting up easyrec server....</p>');
        $('#configform').submit();
    }
</script>
</body>
</html>