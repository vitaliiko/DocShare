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
    <style>
        .big-check-box {
            width: 18px;
            height: 18px;
        }
    </style>
</head>

<body>

<jsp:include page="../include/header.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<div class="container" style="width: 900px;">
    <form action="/document/upload" method="post" enctype="multipart/form-data">
        <label class="col-md-3 control-lable" for="files">Upload a document</label>
        <input type="file" multiple name="files[]" id="files" class="form-control input-sm"/>
        <br>
        <label class="col-md-3 control-lable" for="changedBy">Description</label>
        <input type="text" name="changedBy" id="changedBy" class="form-control input-sm"/>
        <br>
        <div class="form-actions floatRight">
            <input type="submit" value="Upload" class="btn btn-primary btn-sm">
        </div>
    </form>
</div>

<div class="container" style="width: 900px;">
    <c:forEach items="${tableNames}" var="tableName">
        <table class="table table-hover tbody tr:hover td ${tableName}">
            <caption>
                <button type="button" class="btn btn-default" data-toggle="modal" data-target="#makeDirDialog"
                        name="makeDirButton">Make dir</button>
                <h3></h3>
                <a href="#" class="all-href">All &nbsp</a>
                <a href="#" class="public-href">Public &nbsp</a>
                <a href="#" class="for-friends-href">For Friends &nbsp</a>
                <a href="#" class="private-href">Private</a>

                <button class="btn btn-default delete-btn action-btn"
                        data-toggle="modal" data-target="#deleteDialog">Delete</button>
                <button class="btn btn-default replace-btn action-btn">Replace</button>
                <button class="btn btn-default copy-btn action-btn">Copy</button>
                <button class="btn btn-default rename-btn action-btn single-selection">Rename</button>
            </caption>
            <tr>
                <th><input type="checkbox" class="check-box big-check-box select-all"/></th>
                <th id="file-name">Name</th>
                <th>Size</th>
                <th>Changed</th>
                <th width="15"></th>
                <th width="15"></th>
            </tr>

            <c:forEach items="${directoriesMap[tableName]}" var="dir">
                <tr class="tr-doc${dir.id}">
                    <td width="20">
                        <input type="checkbox" class="check-box select select-dir big-check-box" value="${dir.id}"/>
                    </td>
                    <td class="directory-name">
                        <a href="#">${dir.name}</a>
                    </td>
                    <td>--</td>
                    <td>--</td>
                    <td width="15"></td>
                    <td width="15">
                        <button type="button" class="btn btn-default btn-sm share-dir-btn"
                                data-toggle="modal" data-target="#shareDialog" value="${dir.id}">Share</button>
                    </td>
                </tr>
            </c:forEach>

            <c:forEach items="${documentsMap[tableName]}" var="doc" varStatus="counter">
                <tr class="tr-doc${doc.id}">
                    <td width="20">
                        <input type="checkbox" class="check-box select select-doc big-check-box" value="${doc.id}"/>
                    </td>
                    <td class="document-name">
                        <a href="/document/browse-${doc.id}">${doc.name}</a>
                    </td>
                    <td>${doc.size}</td>
                    <td class="document-date">
                        <fmt:formatDate type="date" timeStyle="short" dateStyle="short" value="${doc.lastModifyTime}"/>
                    </td>
                    <td width="15">
                        <a href="<c:url value='/document/download-${doc.id}' />"
                           class="btn btn-default btn-sm custom-width">Download</a>
                    </td>
                    <td width="15">
                        <button type="button" class="btn btn-default btn-sm share-doc-btn"
                                data-toggle="modal" data-target="#shareDialog" value="${doc.id}">Share</button>
                    </td>
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
                <h4 id="delete-dialog-text"></h4>
            </div>
            <div class="modal-footer">
                <button type="button" id="deleteDocument" class="btn btn-success" data-dismiss="modal">YES</button>
                <button type="button" class="btn btn-danger" data-dismiss="modal">NO</button>
            </div>
        </div>

    </div>
</div>

<div id="makeDirDialog" class="modal fade" role="dialog">
    <div class="modal-dialog">

        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Removing</h4>
            </div>
            <div class="modal-body">
                <p id="group-action">Input directory name</p>
                <input type="text" id="directoryName" class="form-control group-name-input"
                       placeholder="Directory Name" autofocus="">
            </div>
            <div class="modal-footer">
                <button type="button" id="makeDir" class="btn btn-default" data-dismiss="modal">Make dir</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
            </div>
        </div>

    </div>
</div>

<div id="shareDialog" class="modal fade" role="dialog">
    <div class="modal-dialog">

        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title"></h4>
            </div>

            <div class="modal-body">
                <div class="btn-group" data-toggle="buttons">
                    <input type="radio" name="access" id="PRIVATE" value="PRIVATE" checked>Private
                    <input type="radio" name="access" id="FOR_FRIENDS" value="FOR_FRIENDS">For friends
                    <input type="radio" name="access" id="PUBLIC" value="PUBLIC">Public
                </div>
                <div class="checkbox" id="friends-list">
                    <c:forEach var="group" items="${friendsGroups}">
                        <div class="group-${group.id}">
                            <label>
                                <input type="checkbox" class="check-box group-check-box" value="${group.id}">
                                    ${group.name}
                            </label>
                            <br>
                        </div>
                    </c:forEach>
                    <c:forEach var="friend" items="${friends}">
                        <div class="group-${friend.id}">
                            <label>
                                <input type="checkbox" class="check-box friend-check-box" value="${friend.id}">
                                    ${friend.firstName} ${friend.lastName}
                            </label>
                            <br>
                        </div>
                    </c:forEach>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" id="shareDocument" class="btn btn-default" data-dismiss="modal">SHARE</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
            </div>
        </div>

    </div>
</div>

</body>
</html>