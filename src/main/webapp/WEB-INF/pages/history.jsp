<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>History</title>
    <jsp:include page="../include/include.jsp"/>
</head>

<body style="padding-top: 65px;">

<jsp:include page="../include/navbar.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<div class="container col-md-10" style="width: 900px;">
    <table class="table table-hover tbody tr:hover td" id="documentTable">
        <caption>
            <h3>List of Previous Versions Documents  |  <a href="/api/documents/${currentVersion.id}">Back</a> </h3>
        </caption>
        <tr>
            <th id="file-name">File Name</th>
            <th>Size</th>
            <th>Changed</th>
            <th width="15"></th>
            <th width="15"></th>
        </tr>
        <tr>
            <td>${currentVersion.name}</td>
            <td>${currentVersion.size}</td>
            <td>
                <fmt:formatDate type="both" pattern="dd.MM.yy | h.mm" dateStyle="short" value="${currentVersion.lastModifyTime}"/>
                by ${currentVersion.modifiedBy}
            </td>
            <td>
                <form action="/api/documents/${currentVersion.id}/download" method="get">
                    <input type="submit" class="btn btn-default custom-width" value="Download">
                </form>
            </td>
        </tr>
        <c:forEach items="${versions}" var="doc">
            <tr>
                <td>${doc.name}</td>
                <td>${doc.size}</td>
                <td>
                    <fmt:formatDate type="both" pattern="dd.MM.yy | h.mm" dateStyle="short" value="${doc.lastModifyTime}"/>
                     by ${doc.changedBy}
                </td>
                <td>
                    <form action="/api/documents/${currentVersion.id}/versions/${doc.id}/recover" method="post">
                        <input type="submit" class="btn btn-default custom-width" value="Recover">
                    </form>
                </td>
                <td>
                    <form action="/api/documents/${currentVersion.id}/versions/${doc.id}/download" method="get">
                        <input type="submit" class="btn btn-default custom-width" value="Download">
                    </form>
                </td>
            </tr>
        </c:forEach>
    </table>
</div>
</body>
</html>
