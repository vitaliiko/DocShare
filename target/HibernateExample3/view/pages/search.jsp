<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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

    <jsp:include page="../include/footer.jsp"/>
</body>
</html>
