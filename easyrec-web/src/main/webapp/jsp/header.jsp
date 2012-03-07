<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<head>
    <title>${title}</title>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
    <meta name="robots" content="noindex,nofollow"/>
    <meta http-equiv="cache-control" content="no-cache"/>
    <meta http-equiv="Pragma" content="no-cache"/>
    <meta http-equiv="Expires" content="-1"/>


    <link rel="shortcut icon" href="${webappPath}/img/favicon.ico"/>
    <link rel="icon" href="${webappPath}/img/favicon.ico"/>
    <link rel="stylesheet" type="text/css" href="${webappPath}/css/easyrec.css"/>
    <!--[if IE 6]>
    <style type="text/css">@import url(${webappPath}/css/easyrec_ie6.css);</style><![endif]-->
    <!--[if IE 8]>
    <style type="text/css">@import url(${webappPath}/css/easyrec_ie8.css);</style><![endif]-->

    <link rel="stylesheet" href="${webappPath}/css/custom-theme/jquery.ui.base.css" type="text/css" media="screen"/>
    <link rel="stylesheet" href="${webappPath}/css/custom-theme/jquery-ui-1.8.6.custom.css" type="text/css"
          media="screen"/>
    <link rel="stylesheet" type="text/css" href="${webappPath}/css/footer.css"/>

    <script type="text/javascript">
        webappPath = '${webappPath}/';
        extendedWebappPath = '${extendedWebAppPath}/';
        easyrecVersion = '${easyrecVersion}';
    </script>

    <script src="${webappPath}/js/jquery/jquery-1.4.2.min.js" type="text/javascript"></script>

    <script src="${webappPath}/js/jquery/jquery-ui-1.8.6.custom.min.js"></script>
    <script src="${webappPath}/js/jquery/jquery.ui.dialog.js"></script>
    <script src="${webappPath}/js/jquery/jquery.simplemodal.js" type="text/javascript"></script>
    <script src="${webappPath}/js/jquery/jquery.xml2json.pack.js" type="text/javascript"></script>
    <script src="${webappPath}/js/jquery/jquery.jstree.js" type="text/javascript"></script>
    <script src="${webappPath}/js/jquery/jquery.hotkeys.js" type="text/javascript"></script>
    <script src="${webappPath}/js/jquery/jquery.cookie.js" type="text/javascript"></script>

    <script src="${webappPath}/js/preload.js" type="text/javascript"></script>
    <script src="${webappPath}/js/paging.js" type="text/javascript"></script>
    <script src="${webappPath}/js/signin.js" type="text/javascript"></script>
    <c:if test="${tenant!='EASYREC_DEMO'}">
        <script type="text/javascript">
            maxActions = '${tenant.maxActions}';
            tenantId = '${tenant.stringId}';
        </script>
        <script src="${webappPath}/js/limit.js" type="text/javascript"></script>
    </c:if>
</head>