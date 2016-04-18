<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Friends documents</title>
    <jsp:include page="../include/include.jsp"/>
</head>
<body>

<jsp:include page="../include/header.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<div class="container" style="width: 900px;">
    <div class="content">
        <table class="table table-hover tbody tr:hover td doc-table ${tableName}">
            <tr class="table-head">
                <th id="file-name">Name</th>
                <th>Upload by</th>
                <th>Size</th>
                <th>Changed</th>
                <th width="15"></th>
            </tr>
        <c:forEach items="${documents}" var="version">
            <tr>
                <td><a href="/document/browse/${version.id}">${version.name}</a></td>
                <td>${version.ownerName}</td>
                <td>${version.size}</td>
                <td>${version.lastModifyTime}</td>
                <td>
                    <a href="/document/download/${version.id}" class="btn btn-default btn-sm custom-width">Download</a>
                </td>
            </tr>
        </c:forEach>
        </table>
    </div>
</div>
</body>
</html>
