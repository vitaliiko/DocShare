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
    <table class="table table-hover tbody tr:hover td">
        <caption>
            <h3>List of Documents</h3>
            <button class="btn btn-link all-href">All</button>
            <button class="btn btn-link public-href">Public</button>
            <button class="btn btn-link for-friends-href">For Friends</button>
            <button class="btn btn-link private-href">Private</button>

            <button class="btn btn-default delete-btn action-btn"
                    data-toggle="modal" data-target="#deleteDialog">Delete</button>
            <button class="btn btn-default replace-btn action-btn">Replace</button>
            <button class="btn btn-default copy-btn action-btn">Copy</button>
            <button class="btn btn-default rename-btn action-btn single-selection">Rename</button>
        </caption>
        <tr>
            <th><input type="checkbox" class="check-box select-all"/> No.</th>
            <th id="file-name">File Name</th>
            <th>Changed</th>
            <th width="80"></th>
        </tr>
    </table>

    <c:forEach items="${documentsMap}" var="documentsEntry">
        <table class="table table-hover tbody tr:hover td" id="${documentsEntry.key}">
        <c:forEach items="${documentsEntry.value}" var="doc" varStatus="counter">
            <tr id="${doc.id}">
                <td class="document-num">
                    <input type="checkbox" class="check-box" value="${doc.id}"/> ${counter.index + 1}
                </td>
                <td class="document-name">${doc.name}</td>
                <td class="document-date"><fmt:formatDate type="both" value="${doc.lastModifyTime}"/></td>
                <td><a href="<c:url value='/document/download-${doc.id}' />"
                       class="btn btn-success custom-width">Download</a></td>
            </tr>
        </c:forEach>
        </table>
    </c:forEach>
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