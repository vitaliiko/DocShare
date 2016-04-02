<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Home</title>
    <jsp:include page="../include/include.jsp"/>
    <script src="${pageContext.request.contextPath}/resources/js/managedocument.js"></script>
</head>

<body>

<jsp:include page="../include/header.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<div class="container" style="width: 900px;">
    <form action="/document/upload" method="POST" enctype="multipart/form-data">
        <label class="col-md-3 control-lable" for="file">Upload a document</label>
        <input type="file" multiple name="file" id="file" class="form-control input-sm"/>
        <br>
        <label class="col-md-3 control-lable" for="description">Description</label>
        <input type="text" name="description" id="description" class="form-control input-sm"/>
        <br>
        <div class="form-actions floatRight">
            <input type="submit" value="Upload" class="btn btn-primary btn-sm upload-btn">
        </div>
    </form>
</div>

<div class="container" style="width: 900px;">
    <table class="table table-hover tbody tr:hover td" id="documentTable">
        <caption><h3>List of Documents</h3></caption>
        <tr>
            <th>No.</th>
            <th id="file-name">File Name</th>
            <th>Changed</th>
            <th>Description</th>
            <th width="100"></th>
            <th width="100"></th>
        </tr>
        <tbody>
        <c:forEach items="${documents}" var="doc" varStatus="counter">
            <tr id="${doc.id}">
                <td>${counter.index + 1}</td>
                <td>${doc.name}</td>
                <td><fmt:formatDate type="both" value="${doc.lastModifyTime}"/></td>
                <td><a href="<c:url value='/document/download-${doc.id}' />"
                       class="btn btn-success custom-width">download</a></td>

                <td><button class="btn btn-primary custom-width delete-btn" id="${doc.id}" name="deleteButton"
                            data-toggle="modal" data-target="#deleteDialog">delete</button></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>

<div id="deleteDialog" class="modal fade" role="dialog">
    <div class="modal-dialog">

        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Removing</h4>
            </div>
            <div class="modal-body">
                <h4 id="dialog-text"></h4>
            </div>
            <div class="modal-footer">
                <button type="button" id="deleteDocument" class="btn btn-success">YES</button>
                <button type="button" class="btn btn-danger" data-dismiss="modal">NO</button>
            </div>
        </div>

    </div>
</div>

</body>
</html>