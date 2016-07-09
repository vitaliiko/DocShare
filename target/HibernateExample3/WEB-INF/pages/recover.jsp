<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Removed documents</title>
    <jsp:include page="../include/include.jsp"/>
    <script src="${pageContext.request.contextPath}/resources/js/sidebar.js"></script>
</head>

<body style="padding-top: 65px;">

<jsp:include page="../include/navbar.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<div class="container col-md-10" style="width: 900px;">
    <table class="table table-hover tbody tr:hover td" id="documentTable">
        <caption><h3>List of Removed Documents</h3></caption>
        <tr>
            <th id="file-name">Name</th>
            <th>Removed</th>
            <th width="100"></th>
        </tr>
        <c:forEach items="${directories}" var="dir">
            <tr>
                <td>${dir.name}</td>
                <td>
                    <fmt:formatDate type="date" timeStyle="short" dateStyle="short" value="${dir.removalDate}"/>
                    by ${dir.removerName}
                </td>
                <td><form action="/api/directory/${dir.id}/recover" method="post">
                    <input type="hidden" name="remDirId" value="">
                    <input type="submit" class="btn btn-primary custom-width" value="Recover">
                </form></td>
            </tr>
        </c:forEach>
        <c:forEach items="${documents}" var="version">
            <tr>
                <td>${version.name}</td>
                <td>
                    <fmt:formatDate type="date" timeStyle="short" dateStyle="short" value="${version.removalDate}"/>
                    by ${version.removerName}
                </td>
                <td><form action="/api/documents/${version.id}/recover" method="post">
                    <input type="submit" class="btn btn-primary custom-width" value="Recover">
                </form></td>
            </tr>
        </c:forEach>
    </table>
</div>
</body>
</html>
