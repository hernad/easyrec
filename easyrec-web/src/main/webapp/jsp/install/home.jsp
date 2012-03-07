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

        <div class="headline">Step 1/4</div>
        <div>
            This is the first time you are running easyrec.
            Please set the database connection and user credentials first.
        </div>
        <div style="float:right;position: absolute;right: 50px; top: 380px;">
            <img alt="people" src="img/home_ppl.gif"/>
        </div>
        <br/>

        <div>
            <form action="connect" method="post">
                <table class="install">
                    <tr>
                        <td/>
                        <td>Database Host:</td>
                        <td/>
                        <td>Database Name:</td>
                    </tr>
                    <tr>
                        <td>jdbc:mysql://</td>
                        <td><input style="margin-left:0px" name="db_host" value="${db_host}" type="text"/></td>
                        <td>/</td>
                        <td><input style="margin-left:0px" name="db_name" value="${db_name}" type="text"/></td>
                    </tr>
                    <tr>
                        <td>User Name:</td>
                        <td><input style="margin-left:0px" name="db_username" value="${db_username}" type="text"/></td>
                        <td/>
                        <td><p>
                            Please use an account with sufficient privileges (SELECT, INSERT, UPDATE <br/>
                            and DELETE as well as CREATE Table permissions) on the database
                        </p>
                        </td>
                    </tr>
                    <tr>
                        <td>Password:</td>
                        <td><input style="margin-left:0px" name="db_password" value="${db_password}" type="password"/>
                        </td>
                        <td/>
                        <td>
                            It is strongly recommended that you do not use a superuser<br/>
                            account since the user password is stored in plain text.
                        </td>
                    </tr>
                    <tr>
                        <td><br/><input style="margin-left:0px" type="submit" value="continue"/></td>
                    </tr>
                </table>
                <div class="red"> ${exceptionMessage}</div>
            </form>
        </div>

        <c:forEach var="prop" items="${props}">
            <c:out value="${prop}"/>
            <br/>
        </c:forEach>
    </div>
    <jsp:include page="footer.jsp"/>
</div>
</body>
</html>