<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="utf-8">

    <title>Friends</title>

    <jsp:include page="../include/include.jsp"/>
    <script src="${pageContext.request.contextPath}/view/js/friends.js"></script>
</head>
<body>

    <jsp:include page="../include/header.jsp"/>
    <jsp:include page="../include/sidebar.jsp"/>

    <div id="wrapper" class="container">
        <div id="page-content-wrapper">
            <table id="groupTable" class="table table-hover tbody tr:hover td">
                <caption>
                    Groups
                    <button type="button" id="addGroupButton" class="btn btn-primary btn-sm"
                            data-toggle="modal" data-target="#groupInfo">Add group</button>
                </caption>

                <c:forEach var="group" items="${groups}">
                    <c:url var="groupPage" value="/main/userpage/${group.id}"/>
                    <tr>
                        <td>
                            <button type="button" name="groupButton" class="btn btn-link group-info"
                                    data-toggle="modal" data-target="#groupInfo"> ${group.name} </button>
                        </td>
                        <td>

                        </td>
                    </tr>
                </c:forEach>
            </table>

            <table id="friendsTable" class="table table-hover tbody tr:hover td">
                <caption>
                    Friends <a href="/main/search" class="btn btn-primary btn-sm">Add friends</a>
                </caption>

                <c:forEach var="friendEntry" items="${friends}">
                    <c:url var="friendPage" value="/main/userpage/${friendEntry.key.id}"/>
                    <tr id="${friendEntry.key.id}">
                        <td>
                            <a href="${friendPage}" class="btn btn-link"> ${friendEntry.key} </a>
                        </td>
                        <td>
                            <c:forEach var="group" items="${friendEntry.value}">
                                <button type="button" name="groupButton" class="btn btn-link group-info"
                                        data-toggle="modal" data-target="#groupInfo"> ${group.name} </button>
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
                        <input type="text" id="groupName" class="form-control"
                               placeholder="Group Name" required="" autofocus="">

                        <div id="friends-list"></div>

                    </div>
                    <div class="modal-footer">
                        <button type="button" id="saveGroup" class="btn btn-primary">Save</button>
                        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                    </div>
                </div>

            </div>
        </div>
    </div>



    <jsp:include page="../include/footer.jsp"/>
</body>
</html>
