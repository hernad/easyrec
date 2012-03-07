<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<div class="appendbody">
    <h1>Upload plugin</h1>
    <br><br>

    <form id="uploadForm" method="post" action="pluginupload.form?tenantId=${tenantId}&operatorId=${operatorId}"
          enctype="multipart/form-data">
        <input type="file" name="file"/><br>
        <img alt="upload" src="${webappPath}/img/button_upload.png" onclick="$('#uploadForm').submit()"
             style="cursor:pointer;">
    </form>
</div>

