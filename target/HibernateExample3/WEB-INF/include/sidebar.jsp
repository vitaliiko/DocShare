<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script src="${pageContext.request.contextPath}/resources/js/eventNotifier.js"></script>

<div class="col-md-2 sidebar-offcanvas" id="sidebar" role="navigation">
    <ul class="nav">
        <li id="accessibleFiles">
            <a href="/document/accessible">Accessible files</a>
        </li>
        <li id="friends">
            <a href="/friends/">Friends</a>
        </li>
        <li id="events">
            <a href="/event/browse">Events   <label id="unreadEventsCount"></label></a>
        </li>
        <li id="recover">
            <a href="/document/recover">Removed Files</a>
        </li>
    </ul>
</div>
