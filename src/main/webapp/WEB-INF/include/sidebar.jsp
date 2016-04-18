<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<script src="${pageContext.request.contextPath}/resources/js/eventNotifier.js"></script>
<div id="sidebar-wrapper">
    <ul class="sidebar-nav nav-pills nav-stacked" id="menu">
        <li>
            <a href="/document/upload">
            <span class="fa-stack fa-lg pull-left">
                <i class="fa fa-cloud-download fa-stack-1x "></i>
            </span>Files</a>
        </li>
        <li>
            <a href="/document/accessible">
            <span class="fa-stack fa-lg pull-left">
                <i class="fa fa-cart-plus fa-stack-1x "></i>
            </span>Accessible files</a>
        </li>
        <li>
            <a href="/friends/">
            <span class="fa-stack fa-lg pull-left">
                <i class="fa fa-cart-plus fa-stack-1x "></i>
            </span>Friends</a>
        </li>
        <li>
            <a href="/event/browse">
            <span class="fa-stack fa-lg pull-left">
                <i class="fa fa-cart-plus fa-stack-1x "></i>
            </span>Events<label id="unreadEventsCount"></label></a>
        </li>
        <li>
            <a href="/document/recover">
            <span class="fa-stack fa-lg pull-left">
                <i class="fa fa-cart-plus fa-stack-1x "></i>
            </span>Removed Files</a>
        </li>
    </ul>
</div>
</html>
