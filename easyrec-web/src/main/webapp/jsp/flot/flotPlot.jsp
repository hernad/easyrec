<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>easyrec :: plot</title>
    <!--[if IE]>
    <script language="javascript" type="text/javascript" src="${webappPath}/js/flot/excanvas.min.js"></script>
    <![endif]-->
    <script language="javascript" type="text/javascript" src="js/jquery/jquery.min.js"></script>
    <script language="javascript" type="text/javascript" src="js/jquery/jquery.flot.js"></script>
</head>
<body>
<div id="placeholder" style="width:300px;height:200px;"></div>
<script language="javascript" type="text/javascript">
    $(function ()
    {
        var data = ${data};
        var plotarea = $("#plotarea");
        plotarea.css("height", "200px");
        plotarea.css("width", "300px");
        $.plot($("#placeholder"), data);
    });
</script>
</body>
</html>