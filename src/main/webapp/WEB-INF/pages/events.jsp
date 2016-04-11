<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Events</title>

    <jsp:include page="../include/include.jsp"/>
</head>
<body>

<jsp:include page="../include/header.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<div id="wrapper" class="container" style="width: 900px;">
    <c:if test="${events.size() > 0}">
        <form action="/event/clear">
            <input type="submit" class="btn btn-default" value="Clear events history">
        </form>
    </c:if>
    <h3>${message}</h3>
    <table class="table table-hover tbody tr:hover td">
        <c:forEach items="${events}" var="event">
            <tr>
                <td width="470px">
                    ${event.text}<br>
                    <c:if test="${event.linkText != null}">
                        <a href="${event.linkUrl}">${event.linkText}</a>
                    </c:if>
                </td>
                <td>
                    <fmt:formatDate type="both" timeStyle="medium" dateStyle="short" value="${event.date}"/>
                </td>
            </tr>
        </c:forEach>

    </table>
</div>

</body>
</html>
