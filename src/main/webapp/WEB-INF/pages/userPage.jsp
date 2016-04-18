<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="utf-8">

    <title>${pageOwner.firstName} ${pageOwner.lastName}</title>

    <jsp:include page="../include/include.jsp"/>
</head>
<body>
<jsp:include page="../include/header.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<div class="container" style="width: 900px">
    <div class="container">
        <img src="/profile/imageDisplay?id=${pageOwner.id}"/>
        <h3>${pageOwner.firstName} ${pageOwner.lastName}</h3>
    </div>

    <div class="content">
        <table class="table table-hover tbody tr:hover td doc-table ${tableName}">
            <tr class="table-head">
                <th id="file-name">Name</th>
                <th>Size</th>
                <th>Changed</th>
                <th width="15"></th>
            </tr>
            <c:forEach items="${documents}" var="version">
                <tr>
                    <td><a href="/document/browse/${version.id}">${version.name}</a></td>
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
