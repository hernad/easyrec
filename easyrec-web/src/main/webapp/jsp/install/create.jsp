<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<jsp:include page="../header.jsp"/>
<script src="${webappPath}/js/jquery/jquery.alerts.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" href="${webappPath}/css/custom-theme/jquery.alerts.css"/>
<body>
<div class="fixed">
    <jsp:include page="../menu.jsp"/>
    <div class="install">
        <div class="headlineBig">Welcome to the easyrec installer v${currentVersion}</div>
        <br/>

        <div style="float:right;position: absolute;right: 50px; top: 380px;">
            <img alt="people" src="img/home_ppl.gif"/>
        </div>
        <div class="headline">Step 2/4</div>
        <div>
            <table class="install">
                <tr>
                    <td valign="top">
                        <img alt="success" src="${webappPath}/img/success.gif"/>
                    </td>
                    <td style="padding-left: 55px">
                        easyrec has tested the database
                        connection and stored your settings.<br/>
                        <c:if test="${installedVersion != 0}">
                            easyrec has found an existing database (v${installedVersion})
                            You can keep the existing data and click on<br /><br />
                            <button onclick="javascript:window.location='existing'">use existing database</button>
                            <br/><br/></c:if>
                        Click on 'initialize database' to
                        create the default database scheme from scratch<br/><br/>
                        <button onclick="javascript:window.location='create?sourcePage=create'">&nbsp;&nbsp;initialize
                            database&nbsp;&nbsp;</button>
                        <br/><br/>
                        <c:if test="${installedVersion != 0}"><b>!!! WARNING !!! Initializing the database '${db_name}'
                            erases all existing data!!!</b></c:if>
                    </td>
                </tr>
            </table>
            <br/><br/>

            <div class="red">${exceptionMessage}</div>
        </div>
    </div>
    <jsp:include page="footer.jsp"/>
</div>
<script type="text/javascript">
    jAlert("With version 0.95 easyrec switched to InnoDB as its default database engine. Please <b>make sure that InnoDB is enabled</b> on your MySQL installation.<br/>" +
            "Also make sure to use the <b>appropriate settings</b> to ensure good performance." +
            "The settings can be found at <br/><br/><b><i>http://sourceforge.net/apps/mediawiki/easyrec/index.php?title=InnoDB_settings</i></b><br/>",
            "easyrec InnoDB migration information");
</script>
</body>
</html>