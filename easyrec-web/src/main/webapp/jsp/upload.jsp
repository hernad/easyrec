<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="upperbody">
    <h1>Upload rules</h1>

    <form method="post" action="upload.form" enctype="multipart/form-data">
        <input type="file" name="file"/>
        <input type="submit"/>
    </form>
    <p>Download an example csv file <a href="${webappPath}/download/rules_example.csv">here</a></p>
</div>

