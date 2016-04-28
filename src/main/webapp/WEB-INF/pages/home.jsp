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
    <script src="${pageContext.request.contextPath}/resources/js/templateHandler.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/navbar.js"></script>
    <style>
        .big-check-box {
            width: 18px;
            height: 18px;
        }
        .checkbox-table {
            border-spacing: 10px;
            border-collapse: separate;
        }
    </style>
</head>

<body style="padding-top: 65px;">

<jsp:include page="../include/navbar.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<div class="container col-md-10" style="width: 900px;">

    <div class='alert alert-danger' hidden>
        <a href='#' class='close' data-dismiss='alert'>&times;</a>
        <strong>Error!</strong> <p class="alert-text"></p>
    </div>

    <div class='alert alert-success' hidden>
        <a href='#' class='close' data-dismiss='alert'>&times;</a>
        <p class="alert-text"></p>
    </div>

    <form action="/document/upload" method="post" enctype="multipart/form-data">
        <label class="col-md-3 control-lable" for="files">Upload a document</label>
        <input type="file" multiple name="files[]" id="files" class="form-control input-sm"/>
        <br>
        <label class="col-md-3 control-lable" for="changedBy">Description</label>
        <input type="text" name="changedBy" id="changedBy" class="form-control input-sm"/>
        <br>
        <input type="hidden" value="" id="dirHashNameHidden" name="dirHashName">
        <div class="form-actions floatRight">
            <input type="submit" value="Upload" class="btn btn-primary btn-sm">

            <button type="button" class="btn btn-default btn-sm make-dir-btn"
                    data-toggle="modal" data-target="#makeDirDialog">Make dir</button>
        </div>
    </form>

    <div  class="back-links">
        <h3 id="location">${userLogin}</h3>
    </div>
    <div>
        <a href="" class="back-link"><-Back</a>
    </div>

    <div class="content">
        <c:forEach items="${tableNames}" var="tableName">
            <table class="table table-hover tbody tr:hover td doc-table ${tableName}" style=" font-size: 14px">
                <caption>
                    <h4 class="replace-message" hidden>Choose the folder in which you want to move files and press "Move here"</h4>
                    <h4 class="copy-message" hidden>Choose the folder in which you want to copy files and press "Copy here"</h4>

                    <a href="#" class="switch-btn all-href">All &nbsp</a>
                    <a href="#" class="switch-btn public-href">Public &nbsp</a>
                    <a href="#" class="switch-btn for-friends-href">For Friends &nbsp</a>
                    <a href="#" class="switch-btn private-href">Private</a>

                    <button class="btn btn-default delete-btn action-btn"
                            data-toggle="modal" data-target="#deleteDialog">Delete</button>

                    <button class="btn btn-default replace-btn action-btn">Replace</button>

                    <button class="btn btn-default copy-btn action-btn">Copy</button>

                    <button class="btn btn-default rename-btn action-btn single-selection"
                            data-toggle="modal" data-target="#renameDialog">Rename</button>

                    <button class="btn btn-default add-action-btn move-here-btn">Move here</button>
                    <button class="btn btn-default add-action-btn copy-here-btn">Copy here</button>
                    <button class="btn btn-default add-action-btn cancel-btn">Cancel</button>

                </caption>
                <tr class="table-head">
                    <th width="20">
                        <input type="checkbox" class="check-box big-check-box select-all"/>
                    </th>
                    <th id="file-name">Name</th>
                    <th>Size</th>
                    <th>Changed</th>
                    <th width="15"></th>
                    <th width="15"></th>
                </tr>
            </table>
        </c:forEach>
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
                <h4 class="modal-title make-dir-modal-title">Make directory</h4>
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
                <h4 class="modal-title share-modal-title"></h4>
            </div>

            <div class="modal-body">
                <div class="btn-group" data-toggle="buttons">
                    <input type="radio" name="access" id="PUBLIC" value="PUBLIC">Public
                    <input type="radio" name="access" id="FOR_FRIENDS" value="FOR_FRIENDS">For friends
                    <input type="radio" name="access" id="PRIVATE" value="PRIVATE" checked>Private
                </div>
                <table class="checkbox checkbox-table" id="friends-list">
                    <tr>
                        <th>Friends, who can read</th>
                        <th class="group-check-box">Friends, who can change</th>
                    </tr>
                    <c:forEach var="group" items="${friendsGroups}">
                        <tr class="group-${group.id}">
                            <td>
                                <label>
                                    <input type="checkbox" class="check-box readers-group-check-box" value="${group.id}">
                                        ${group.name}
                                </label>
                            </td>
                            <td class="group-check-box">
                                <label>
                                    <input type="checkbox" class="check-box editors-group-check-box" value="${group.id}">
                                        ${group.name}
                                </label>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:forEach var="friend" items="${friends}">
                        <tr class="group-${friend.id}">
                            <td>
                                <label>
                                    <input type="checkbox" class="check-box reader-check-box" value="${friend.id}">
                                        ${friend.firstName} ${friend.lastName}
                                </label>
                            </td>
                            <td class="group-check-box">
                                <label>
                                    <input type="checkbox" class="check-box editor-check-box" value="${friend.id}">
                                        ${friend.firstName} ${friend.lastName}
                                </label>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
            </div>
            <div class="modal-footer">
                <button type="button" id="shareDocument" class="btn btn-default" data-dismiss="modal">SHARE</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
            </div>
        </div>

    </div>
</div>

<div id="replaceDialog" class="modal fade" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title replace-modal-title">Replace document</h4>
            </div>
            <div class="modal-body">
                <p>Select directory in which document will be replace</p>
                <div id="dirTree">

                </div>
            </div>
            <div class="modal-footer">
                <button type="button" id="makeDir" class="btn btn-default" data-dismiss="modal">Make dir</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
            </div>
        </div>

    </div>
</div>

<div id="renameDialog" class="modal fade" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title rename-modal-title">Rename</h4>
            </div>
            <div class="modal-body">
                <p>Input new name: </p>
                <input type="text" id="newFileName" class="form-control group-name-input" autofocus="">
            </div>
            <div class="modal-footer">
                <button type="button" id="renameFile" class="btn btn-default" data-dismiss="modal">OK</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
            </div>
        </div>

    </div>
</div>

</body>
</html>