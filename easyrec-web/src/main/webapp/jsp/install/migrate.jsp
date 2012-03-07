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
        <!--div style="float:right;position: absolute;right: 50px; top: 370px;">
            <img alt="people" src="img/home_ppl.gif"/>
        </div-->
        <div class="headline">Step 2/4</div>
        <div>
            <table class="install">
                <tr>
                    <td>
                        <img alt="success" src="${webappPath}/img/success.gif"/>
                    </td>
                    <td style="padding-left: 55px">
                        easyrec has tested the database
                        connection and stored your settings.
                        A database of a previous version of easyrec - v${installedVersion} - was found.<br/>
                        The new version of easyrec needs to make some changes to the database to work properly.
                        You can<br/>
                        <table width="95%">
                            <tr>
                                <td width="50%">
                                    <ul>
                                        <li>
                                            <b>update the existing easyrec database</b><br/><br/>
                                            By clicking on 'update database' easyrec will perform all the necessary
                                            changes on the database and keep your existing data.<br/>
                                            <b>!!! WARNING !!! Please make sure you have a BACKUP of your existing
                                                database before performing the update!!!</b><br/>
                                            <c:if test="${showBox}">
                                                <script type="text/javascript">
                                                    jAlert("The installer has detected an easyrec version older than 0.95.<br/>" +
                                                            "With version 0.95 easyrec switched to InnoDB as its default database engine. Please <b>make sure that InnoDB is enabled</b> on your MySQL installation.<br/>" +
                                                            "Also make sure to use the <b>appropriate settings</b> to ensure good performance." +
                                                            "The settings can be found at <br/><br/><b><i>http://sourceforge.net/apps/mediawiki/easyrec/index.php?title=InnoDB_settings</i></b><br/><br/>" +
                                                            "Furthermore, depending on your DB machine and DB size the migration <b>may take a long time</b> (bad case: 30min - 1h)!",
                                                            "easyrec InnoDB migration information");
                                                </script>
                                            </c:if>
                                            <br/>
                                            <button onclick="javascript:window.location='migrate'">update easyrec
                                            </button>
                                            <br/><br/>
                                        </li>
                                    </ul>
                                </td>
                                <td width="50%" valign="top">
                                    <ul>
                                        <li>
                                            <b>clear the existing database</b><br/><br/>
                                            Clicking on 'clear database' will discard all existing data
                                            and create a new empty easyrec database.<br/><br/>
                                            <b>!!! WARNING !!! All existing data in the database will be
                                                lost!!!</b><br/>
                                            <br/>
                                            <button onclick="javascript:window.location='create?sourcePage=migrate'">
                                                clear database
                                            </button>
                                        </li>
                                    </ul>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </table>
            <div class="red"> ${exceptionMessage}</div>
        </div>
    </div>
    <jsp:include page="footer.jsp"/>
</div>
</body>
</html>