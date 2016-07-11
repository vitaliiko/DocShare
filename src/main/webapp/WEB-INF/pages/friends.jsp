<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="utf-8">

    <title>Friends</title>

    <jsp:include page="../include/include.jsp"/>
    <script src="${pageContext.request.contextPath}/resources/js/friends.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/templateHandler.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/sidebar.js"></script>
</head>

<body style="padding-top: 65px;">

<jsp:include page="../include/navbar.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<div id="wrapper" class="container col-md-10">

    <div class='alert alert-danger' hidden>
        <a href='#' class='close' data-dismiss='alert'>&times;</a>
        <strong>Error!</strong> <p class="alert-text"></p>
    </div>

    <div class='alert alert-success' hidden>
        <a href='#' class='close' data-dismiss='alert'>&times;</a>
        <p class="alert-text"></p>
    </div>

    <div id="page-content-wrapper">
        <table id="groupTable" class="table table-hover tbody tr:hover td info-table group-table">
            <caption>
                Groups
                <button type="button" id="addGroupButton" class="btn btn-primary btn-sm"
                        data-toggle="modal" data-target="#groupInfo">Add group</button>
            </caption>
        </table>

        <table id="friendsTable" class="table table-hover tbody tr:hover td info-table friend-table">
            <caption>
                Friends <a href="/api/search/page" class="btn btn-primary btn-sm">Add friends</a>
            </caption>

            <c:forEach var="friendEntry" items="${friends}">
                <c:url var="friendPage" value="/api/userpage/${friendEntry.key.id}"/>
                <tr class="friend${friendEntry.key.id}">
                    <td>
                        <a href="${friendPage}" class="btn btn-link"> ${friendEntry.key} </a>
                    </td>
                    <td class="td td-friend${friendEntry.key.id}">
                        <c:forEach var="group" items="${friendEntry.value}">
                            <a href="#" name="groupInfoButton" data-toggle="modal" data-target="#groupInfo"
                                    id="${group.id}" class="group-info-btn group${group.id}">
                                    ${group.name}
                            </a>
                        </c:forEach>
                    </td>
                    <td>
                        <input type="button" class="btn btn-default removeFriendButton"
                               id="${friendEntry.key.id}" value="Remove from friends">
                    </td>
                </tr>
            </c:forEach>
        </table>
    </div>

    <div id="groupInfo" class="modal fade" role="dialog">
        <div class="modal-dialog">

            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                    <h4 class="modal-title">New group</h4>
                </div>
                <div class="modal-body">
                    <p id="group-action">Input group name</p>
                    <input type="text" id="groupName" class="form-control group-name-input"
                           placeholder="Group Name" autofocus="">

                    <div class="checkbox" id="friends-list">
                        <c:forEach var="friendEntry" items="${friends}">
                            <div class="friend${friendEntry.key.id}">
                                <label>
                                    <input type="checkbox" class="check-box" value="${friendEntry.key.id}">
                                    ${friendEntry.key}
                                </label>
                                <br>
                            </div>
                        </c:forEach>
                    </div>

                </div>
                <div class="modal-footer">
                    <button type="button" id="saveGroupButton" class="btn btn-primary" data-dismiss="modal">Save</button>
                    <button type="button" id="updateGroupButton" class="btn btn-primary" data-dismiss="modal">Save</button>
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                </div>
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
                <h4 id="delete-dialog-text"></h4>
            </div>
            <div class="modal-footer">
                <button type="button" id="deleteGroup" class="btn btn-success" data-dismiss="modal">YES</button>
                <button type="button" class="btn btn-danger" data-dismiss="modal">NO</button>
            </div>
        </div>

    </div>
</div>

</body>
</html>
