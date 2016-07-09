<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script src="${pageContext.request.contextPath}/resources/js/eventNotifier.js"></script>

<div class="col-md-2 sidebar-offcanvas" id="sidebar" role="navigation">
    <ul class="nav">
        <li id="accessibleFiles">
            <a href="/document/accessible">Accessible files</a>
        </li>
        <li id="friends">
            <a href="/api/friends/">Friends</a>
        </li>
        <li id="events">
            <a href="/api/events">Events   <label id="unreadEventsCount"></label></a>
        </li>
        <li id="recover">
            <a href="/api/files/removed">Removed Files</a>
        </li>
    </ul>
</div>
