<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<nav class="navbar navbar-default">
    <div class="container-fluid">
        <div class="navbar-header">
            <a class="navbar-brand" href="#">Exchange File System</a>
        </div>
        <ul class="nav navbar-nav">
            <li class="active"><a href="/document/upload">Home</a></li>
            <li><a href="/main/search">Search Friends</a></li>
            <li><a href="/profile/">Profile</a></li>

            <c:if test="${renderSettings}">
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true"
                       aria-expanded="false">Settings<span class="caret"></span></a>
                    <ul class="dropdown-menu">
                        <c:if test="${renderComments}">
                            <li class="showCommentLi">
                                <a href="#" class="comment-box-visible">Hide comment box</a>
                            </li>
                        </c:if>
                        <c:if test="${changeAbilityToComment != null}">
                            <li><a href="#" class="on-off-comments">${changeAbilityToComment}</a></li>
                            <li><a href="#" class="clear-comments">Delete all comments</a></li>
                        </c:if>
                    </ul>
                </li>
            </c:if>
        </ul>
        <form class="navbar-form navbar-left form-search" role="search" hidden>
            <div class="form-group">
                <input type="text" class="form-control" placeholder="Search files" id="filesSearch">
            </div>
            <button type="button" id="searchButton" class="btn btn-default">Submit</button>
        </form>
        <ul class="nav navbar-nav navbar-right">
            <li><a href="/main/sign_out">Sign Out</a></li>
        </ul>
    </div>
</nav>
</html>
