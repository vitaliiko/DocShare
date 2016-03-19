<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>Friends</title>

    <link href="${pageContext.request.contextPath}/pages/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/pages/bootstrap/css/simple-sidebar.css" rel="stylesheet">

    <script src="${pageContext.request.contextPath}/pages/bootstrap/js/jquery-1.12.2.min.js"></script>
    <script src="${pageContext.request.contextPath}/pages/bootstrap/js/bootstrap.min.js"></script>
    <script src="${pageContext.request.contextPath}/pages/bootstrap/js/sidebar_menu.js"></script>
</head>
<body>
    <div id="wrapper" class="container">
        <jsp:include page="sidebar.jsp"/>

        <div id="page-content-wrapper">
            <table class="table">
                <c:forEach var="friend" items="${friends}">
                    <c:url var="friendPage" value="/main/userpage/${friend.id}"/>
                    <tr>
                        <td>
                            <input type="checkbox">
                            <a href="${friendPage}"> ${friend.firstName} ${friend.lastName} </a>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </div>
    </div>
</body>
</html>
