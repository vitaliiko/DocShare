<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>${doc.name}</title>
    <jsp:include page="../include/include.jsp"/>
    <link href="${pageContext.request.contextPath}/resources/bootstrap/css/comment-box.css" rel="stylesheet">
    <script src="${pageContext.request.contextPath}/resources/js/document.js"></script>
</head>
<body>
<jsp:include page="../include/header.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<div class="container" style="width: 900px;">
    ${doc.name} <br>
    ${doc.size} <br>
    Changed: <fmt:formatDate type="both" timeStyle="short" dateStyle="long" value="${doc.lastModifyTime}"/>
    <a href="<c:url value='/document/download-${doc.id}' />" class="btn btn-default custom-width">Download</a>
    <a href="<c:url value='/document/history-${doc.id}' />" class="btn btn-default custom-width">Previous versions</a>
    <br>
    <c:if test="${doc.description != null}">
        Description: ${doc.description}
    </c:if>
    <br>
    <br>
    <br>
    <br>
    <div class="detailBox">
        <div class="titleBox">
            <label>Comment Box</label>
            <button type="button" class="close" aria-hidden="true">&times;</button>
        </div>
        <div class="actionBox">
            <ul class="commentList">
            <c:forEach items="${doc.comments}" var="comment">
                <li>
                    <%--<div class="commenterImage">--%>
                        <%--<img src="http://lorempixel.com/50/50/people/6" />--%>
                    <%--</div>--%>
                    <div class="commentText">
                        <p class=""><strong>${comment.owner}</strong></p>
                        <p class="">${comment.text}</p> <span class="date sub-text">
                        <fmt:formatDate type="both" dateStyle="long" timeStyle="short" value="${comment.date}"/></span>
                    </div>
                </li>
            </c:forEach>
            </ul>
            <form class="form-inline" role="form">
                <div class="form-group">
                    <input class="form-control comment-text" type="text" placeholder="Your comments" />
                </div>
                <div class="form-group">
                    <input type="hidden" class="doc-id" value="${doc.id}">
                    <button type="button" class="btn btn-default add-comment">Add</button>
                </div>
            </form>
        </div>
    </div>
</div>

</body>
</html>
