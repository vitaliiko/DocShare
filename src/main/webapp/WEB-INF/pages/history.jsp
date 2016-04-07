<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>History</title>
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
            <th>Size</th>
            <th>Changed</th>
            <th>Description</th>
            <th width="15"></th>
            <th width="15"></th>
        </tr>
        <c:forEach items="${versions}" var="doc">
            <tr>
                <td>${doc.name}</td>
                <td>${doc.size}</td>
                <td><fmt:formatDate type="date" timeStyle="short" dateStyle="short" value="${doc.lastModifyTime}"/></td>
                <td>${doc.description}</td>
                <td><a href="<c:url value='/document/version-recover-${doc.id}' />"
                       class="btn btn-primary custom-width">Recover</a></td>
                <td><a href="<c:url value='/document/download-${doc.id}' />"
                       class="btn btn-primary custom-width">Download</a></td>
            </tr>
        </c:forEach>
    </table>
</div>
</body>
</html>
