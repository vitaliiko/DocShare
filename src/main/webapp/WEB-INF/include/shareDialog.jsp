<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

<style>
    .checkbox-table {
        border-spacing: 10px;
        border-collapse: separate;
    }
</style>

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
                        <tr class="friend-${friend.id}">
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
</html>
