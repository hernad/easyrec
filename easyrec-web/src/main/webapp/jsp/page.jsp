<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<jsp:include page="header.jsp"/>
<body>
<div style="height: 840px; width: 1280px; display: none;" id="dialog-overlay"></div>
<div style="top: 131px; left: 476px; display: none;" id="dialog-box">
    <div class="dialog-content">
        <div id="dialog-message"></div>
        <a href="#" class="button">Close</a></div>
</div>
<div class="fixed">
    <jsp:include page="menu.jsp"/>
    <jsp:include page="${page}.jsp"/>
    <jsp:include page="footer.jsp"/>
</div>
</body>
</html>