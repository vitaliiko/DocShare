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
            <th width="15"></th>
            <th width="15"></th>
        </tr>
        <c:forEach items="${versions}" var="version">
            <tr>
                <td>${version.name}</td>
                <td>${version.size}</td>
                <td>
                    <fmt:formatDate type="date" timeStyle="short" dateStyle="short" value="${version.lastModifyTime}"/>
                     by ${version.changedBy}
                </td>
                <td><a href="<c:url value='/document/version_recover/${version.id}' />"
                       class="btn btn-primary custom-width">Recover</a></td>
                <td><a href="<c:url value='/document/download-${version.id}' />"
                       class="btn btn-primary custom-width">Download</a></td>
            </tr>
        </c:forEach>
    </table>
</div>
</body>
</html>
