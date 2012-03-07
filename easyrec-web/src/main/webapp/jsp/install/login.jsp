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
        <div>
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
                            Now, proceed to the <a href="javascript:login();">login page</a>.
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
    </div>
    <jsp:include page="footer.jsp"/>
</div>
</body>
</html>