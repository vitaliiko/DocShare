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
        <input type="text" name="searchParameter" placeholder="Search by name">
        <input type="submit" value="Search">
    </form>

    <div>
    <c:forEach var="userEntry" items="${usersMap}">
        <form action="/friends/add_friend">
            <c:url var="userPage" value="/main/userpage/${userEntry.key.id}"/>
            <a href="${userPage}" class="btn btn-link">${userEntry.key}</a>
            <c:if test="${userEntry.value}">
                <input type="hidden" name="friendId" value="${userEntry.key.id}">
                <input type="submit" class="btn btn-primary" value="Add to friend"/>
            </c:if>
            <hr>
        </form>
    </c:forEach>
    </div>
</div>


</body>
</html>
