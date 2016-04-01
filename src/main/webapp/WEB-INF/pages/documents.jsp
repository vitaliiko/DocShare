<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Documents</title>
</head>
<body>
    <c:forEach var="document" items="${documents}">
        <form action="/document/download/${document.getId}">
            <p>${document.getName}</p><input type="submit" value="download">
        </form>
    </c:forEach>

    <form action="/document/upload" method="post">
        <div>Upload a file</div>
        <input type="file" name="document">
        <input type="submit" value="send">
    </form>
</body>
</html>
