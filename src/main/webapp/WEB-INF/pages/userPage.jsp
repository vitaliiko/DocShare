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
            <c:forEach items="${documents}" var="doc">
                <tr>
                    <td><a href="/document/browse-${doc.id}">${doc.name}</a></td>
                    <td>${doc.ownerName}</td>
                    <td>${doc.size}</td>
                    <td>${doc.lastModifyTime}</td>
                    <td>
                        <a href="/document/download-${doc.id}" class="btn btn-default btn-sm custom-width">Download</a>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </div>
</div>

</body>

<footer>
    <jsp:include page="../include/footer.jsp"/>
</footer>
</html>
