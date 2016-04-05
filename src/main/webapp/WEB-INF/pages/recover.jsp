<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Removed documents</title>
    <jsp:include page="../include/include.jsp"/>
</head>
<body>

<jsp:include page="../include/header.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<div class="container" style="width: 900px;">
    <table class="table table-hover tbody tr:hover td" id="documentTable">
        <caption><h3>List of Removed Documents</h3></caption>
        <tr>
            <th id="file-name">File Name</th>
            <th>Removed</th>
            <th width="100"></th>
        </tr>
        <c:forEach items="${documents}" var="doc">
            <tr>
                <td>${doc.userFile.name}</td>
                <td><fmt:formatDate type="date" timeStyle="short" dateStyle="short" value="${doc.removalDate}"/></td>
                <td><a href="<c:url value='/document/recover-${doc.id}' />"
                       class="btn btn-primary custom-width">Recover</a></td>
            </tr>
        </c:forEach>
    </table>
</div>
</body>
</html>
