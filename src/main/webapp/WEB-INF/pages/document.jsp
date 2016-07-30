<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>${doc.name}</title>
    <jsp:include page="../include/include.jsp"/>
    <link href="${pageContext.request.contextPath}/resources/bootstrap/css/comment-box.css" rel="stylesheet">
    <script src="${pageContext.request.contextPath}/resources/js/document.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/comments.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/templateHandler.js"></script>
</head>

<body style="padding-top: 65px;">
<jsp:include page="../include/navbar.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<c:if test="${isOwner}">
    <jsp:include page="../include/shareDialog.jsp"/>
</c:if>

<div class="container col-md-10" style="width: 900px;">

    <div class='alert alert-danger' hidden>
        <a href='#' class='close' data-dismiss='alert'>&times;</a>
        <strong>Error!</strong> <p class="alert-text"></p>
    </div>

    <div class='alert alert-success' hidden>
        <a href='#' class='close' data-dismiss='alert'>&times;</a>
        <p class="alert-text"></p>
    </div>

    <c:choose>
        <c:when test="${linkHash != null}">
            <c:set var='downloadLink' value='/api/documents/link/${linkHash}/download'/>
            <c:set var='uploadLink' value='/api/documents/link/${linkHash}/upload'/>
        </c:when>
        <c:otherwise>
            <c:set var='downloadLink' value='/api/documents/${doc.id}/download'/>
            <c:set var='uploadLink' value='/api/documents/${doc.id}/upload'/>
        </c:otherwise>
    </c:choose>

    <input type="hidden" class="doc-id" value="${doc.id}">
    <input type="hidden" class="link-hash" value="${linkHash}">
    <p>
        <h4 id="docName">${location}${doc.name}</h4>
        <a href="${downloadLink}" class="btn btn-default custom-width">
            Download (${doc.size})
        </a>

        <c:if test="${canUpload}">
            <a href="${uploadLink}" class="btn btn-default custom-width">Upload new version</a>
        </c:if>

        <c:if test="${isOwner}">
            <a href="/api/documents/${doc.id}/history" class="btn btn-default custom-width">
                Previous versions
            </a>

            <button type="button" class="btn btn-default btn-sm share-doc-btn table-btn"
                    data-toggle="modal" data-target="#shareDialog" value="${doc.id}">
                Share
            </button>

            <button class="btn btn-default rename-btn action-btn single-selection"
                    data-toggle="modal" data-target="#renameDialog">
                Rename
            </button>

            <button class="btn btn-default delete-btn action-btn"
                    data-toggle="modal" data-target="#deleteDialog">
                Delete
            </button>
        </c:if>

    <h5>
        Changed: ${doc.lastModifyTime} by
        <c:if test="${!isOwner}">
            <a href="/api/userpage/${doc.modifiedById}">
        </c:if>
        ${doc.modifiedBy}</a>
    </h5>
    <br>
    <br>
    <br>
    <br>
    <c:if test="${renderComments}">
        <div class="detailBox commentBox">
            <div class="titleBox">
                <label>Comment Box</label>
                <button type="button" class="close close-comments" aria-hidden="true">&times;</button>
            </div>
            <div class="actionBox">
                <ul class="commentList"></ul>
                <form class="form-inline" role="form">
                    <div class="form-group">
                        <input class="form-control comment-text" type="text" placeholder="Your comments" />
                    </div>
                    <div class="form-group">
                        <button type="button" class="btn btn-default add-comment">Add</button>
                    </div>
                </form>
            </div>
        </div>
    </c:if>
</div>

<div id="renameDialog" class="modal fade" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title rename-modal-title">Rename ${doc.name}</h4>
            </div>
            <div class="modal-body">
                <p>Input new name: </p>
                <input type="text" id="newFileName" class="form-control group-name-input" autofocus="">
            </div>
            <div class="modal-footer">
                <button type="submit" id="renameFile" class="btn btn-default" data-dismiss="modal">OK</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
            </div>
        </div>

    </div>
</div>

<div id="deleteDialog" class="modal fade" role="dialog">
    <div class="modal-dialog">

        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title delete-modal-title">Removal</h4>
            </div>
            <div class="modal-body">
                <h4 id="delete-dialog-text">
                    Are you sure you wand to move to trash ${doc.name}?
                </h4>
            </div>
            <div class="modal-footer">
                <form action="/api/documents/${doc.id}/move-to-trash" method="post">
                    <input type="submit" class="btn btn-success" value="YES"/>
                    <button type="button" class="btn btn-danger" data-dismiss="modal">NO</button>
                </form>
            </div>
        </div>

    </div>
</div>

</body>
</html>
