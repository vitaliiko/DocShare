<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>${doc.name}</title>
    <jsp:include page="../include/include.jsp"/>
    <link href="${pageContext.request.contextPath}/resources/bootstrap/css/comment-box.css" rel="stylesheet">
    <script src="${pageContext.request.contextPath}/resources/js/document.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/templateHandler.js"></script>
</head>
<body>
<jsp:include page="../include/header.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<div class="container" style="width: 900px;">
    <h4>${location}${doc.name}</h4>
    <h5>
        ${doc.size} &nbsp&nbsp Changed: ${doc.lastModifyTime}
        <a href="<c:url value='/document/download/${doc.id}' />" class="btn btn-default custom-width">Download</a>
        <c:if test="${historyLink != null}">
            <a href="${historyLink}" class="btn btn-default custom-width">
                Previous versions
            </a>
        </c:if>
    </h5>
    <c:if test="${doc.description != null}">
        Description: ${doc.description}
    </c:if>
    <br>
    <br>
    <br>
    <br>
    <c:if test="${renderComments}">
        <div class="detailBox commentBox" ${showComments}>
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
                        <input type="hidden" class="doc-id" value="${doc.id}">
                        <button type="button" class="btn btn-default add-comment">Add</button>
                    </div>
                </form>
            </div>
        </div>
    </c:if>
</div>

</body>
</html>
