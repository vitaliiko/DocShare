<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>${pageOwner.firstName} ${pageOwner.lastName}</title>

    <link href="${pageContext.request.contextPath}/view/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/view/bootstrap/css/simple-sidebar.css" rel="stylesheet">

    <script src="${pageContext.request.contextPath}/view/bootstrap/js/jquery-1.12.2.min.js"></script>
    <script src="${pageContext.request.contextPath}/view/bootstrap/js/bootstrap.min.js"></script>
    <script src="${pageContext.request.contextPath}/view/bootstrap/js/sidebar_menu.js"></script>
</head>
<body>
    <jsp:include page="sidebar.jsp"/>
    <h3>${pageOwner.firstName} ${pageOwner.lastName}</h3>
</body>
</html>
