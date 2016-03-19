<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <title>Search</title>

    <link href="${pageContext.request.contextPath}/view/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/view/bootstrap/css/simple-sidebar.css" rel="stylesheet">

    <script src="${pageContext.request.contextPath}/view/bootstrap/js/jquery-1.12.2.min.js"></script>
    <script src="${pageContext.request.contextPath}/view/bootstrap/js/bootstrap.min.js"></script>
    <script src="${pageContext.request.contextPath}/view/bootstrap/js/sidebar_menu.js"></script>
</head>
<body>
    <jsp:include page="header.jsp"/>
    <jsp:include page="sidebar.jsp"/>

    <div id="wrapper" class="container">
        <c:forEach var="userEntry" items="${usersMap}">
            <form>
                <c:url var="userPage" value="/main/userpage/${userEntry.key.id}"/>
                <a href="${userPage}" class="btn btn-link">${userEntry.key}</a>
                <c:if test="${userEntry.value}">
                    <input type="button" class="btn btn-primary" value="Add to friend"/>
                </c:if>
                <hr>
            </form>
        </c:forEach>

    </div>
</body>
</html>
