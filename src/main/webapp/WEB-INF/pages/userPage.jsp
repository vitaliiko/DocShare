<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="utf-8">

    <title>${pageOwner.firstName} ${pageOwner.lastName}</title>

    <jsp:include page="../include/include.jsp"/>
</head>
<body>
    <jsp:include page="../include/header.jsp"/>
    <jsp:include page="../include/sidebar.jsp"/>

    <div class="container">
        <h3>${pageOwner.firstName} ${pageOwner.lastName}</h3>
    </div>

    <jsp:include page="../include/footer.jsp"/>
</body>
</html>
