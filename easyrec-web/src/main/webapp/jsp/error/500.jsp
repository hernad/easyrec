<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<%@ page isErrorPage="true" %>
<head>
    <title>easyrec :: error</title>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/easyrec.css"/>
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/img/favicon.ico"/>
    <link rel="icon" href="<%=request.getContextPath()%>/img/favicon.ico"/>
    <!--[if IE 6]>
    <style type="text/css">@import url(<%=request.getContextPath()%>/css/easyrec_ie6.css);</style>
    <![endif]-->
    <!--[if IE 8]>
    <style type="text/css">@import url(<%=request.getContextPath()%>/css/easyrec_ie8.css);</style>
    <![endif]-->
</head>
<body>
<div class="fixed">
    <div class="menu">
        <div style="padding: 10px; position: absolute; left: 13px; top: 25px;">
            <a href="<%=request.getContextPath()%>/">
                <span class="logo">easyrec<sup style="font-size: 9px">BETA</sup></span>
            </a>
        </div>
    </div>
    <div class="upperbody">
        <div class="contentSize1">
            <br/><br/>

            <div class="headline">
                An error occurred!
            </div>
            <br/>Click <a href="javascript:history.go(-1);">here</a> to go back.
        </div>
        <div style="float: right; position: absolute; right: 420px; top: 300px;">
            <img alt="people" src="<%=request.getContextPath()%>/img/home_ppl.gif"/>
        </div>
    </div>
    <div class="footer">
        <div style="float: left;">
            <a href="<%=request.getContextPath()%>/home">Home</a> |
            <a href="<%=request.getContextPath()%>/contact">Contact</a>
        </div>
    </div>
</div>
</body>
</html>