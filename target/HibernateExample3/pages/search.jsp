<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Search</title>
</head>
<body>
    <div id="wrapper" class="container">
        <jsp:include page="sidebar.jsp"/>

        <table>

            <c:forEach var="user" items="users">
                <tr>
                    <td>${user.firstName} ${user.lastName}</td>
                    <td></td>
                </tr>
            </c:forEach>

        </table>
    </div>
</body>
</html>
