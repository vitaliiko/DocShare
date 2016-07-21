<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Friends documents</title>
    <jsp:include page="../include/include.jsp"/>
    <script src="${pageContext.request.contextPath}/resources/js/sidebar.js"></script>
</head>

<body style="padding-top: 65px;">

<jsp:include page="../include/navbar.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<div class="container col-md-10" style="width: 900px;">
    <div class="content">
        <table class="table table-hover tbody tr:hover td doc-table ${tableName}">
            <tr class="table-head">
                <th id="file-name">Name</th>
                <th>Upload by</th>
                <th>Size</th>
                <th>Changed</th>
                <th width="15"></th>
            </tr>
        <c:forEach items="${documents}" var="doc">
            <tr>
                <td><a href="/api/documents/${doc.id}">${doc.name}</a></td>
                <td>${doc.ownerName}</td>
                <td>${doc.size}</td>
                <td>${doc.lastModifyTime}</td>
                <td>
                    <a href="/api/documents/${doc.id}/download" class="btn btn-default btn-sm custom-width">Download</a>
                </td>
            </tr>
        </c:forEach>
        </table>
    </div>
</div>
</body>
</html>
