<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="utf-8">

    <title>Search</title>

    <jsp:include page="../include/include.jsp"/>
    <script src="${pageContext.request.contextPath}/resources/js/navbar.js"></script>
</head>

<body style="padding-top: 65px;">
<jsp:include page="../include/navbar.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<div id="wrapper" class="container col-md-10" style="width: 900px">

    <c:if test="${message != null}">
        <div class='alert alert-success'>
            <a href='#' class='close' data-dismiss='alert'>&times;</a>
                ${message}
        </div>
        <br>
    </c:if>

    <form action="/main/search" method="post">
        <input type="text" name="name" value="${name}" placeholder="Search by name" class="form-control">
        <br>
        <input type="text" name="country" value="${country}" placeholder="Search by country" class="form-control">
        <br>
        <input type="text" name="region" value="${region}" placeholder="Search by region" class="form-control">
        <br>
        <input type="text" name="city" value="${city}" placeholder="Search by city" class="form-control">
        <br>
        <input type="submit" value="Search" class="btn btn-default btn-sm">
    </form>

    <br><br>

    <div>
        <h5><strong>Count of results: ${countOrResults}</strong></h5>
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
