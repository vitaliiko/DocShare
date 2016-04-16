<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="utf-8">

    <title>Search</title>

    <jsp:include page="../include/include.jsp"/>
</head>
<body>
<jsp:include page="../include/header.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<div id="wrapper" class="container" style="width: 900px">

    <form action="/main/search" method="post">
        <input type="text" name="searchParameter" placeholder="Search by name" class="form-control">
        <input type="submit" value="Search" class="btn btn-default btn-sm">
    </form>

    <br><br>

    <div>
        <table class="table table-hover tbody tr:hover td doc-table">
        <c:forEach var="userEntry" items="${usersMap}">
            <tr>
                <td>
                    <c:url var="userPage" value="/main/userpage/${userEntry.key.id}"/>
                    <a href="${userPage}" class="btn btn-link">${userEntry.key}</a>
                </td>
                <td><form action="/friends/send_to_friend_event">
                    <c:if test="${userEntry.value}">
                        <input type="hidden" name="friendId" value="${userEntry.key.id}">
                        <input type="submit" class="btn btn-default" value="Add to friend"/>
                    </c:if>
                </form></td>
            </tr>
        </c:forEach>
        </table>
    </div>
</div>


</body>
</html>
