<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>${doc.name}</title>
    <jsp:include page="../include/include.jsp"/>
    <link href="${pageContext.request.contextPath}/resources/bootstrap/css/comment-box.css" rel="stylesheet">
</head>
<body>
<jsp:include page="../include/header.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<div class="container" style="width: 900px;">
    ${doc.name} ${doc.size} <br>
    Changed ${doc.lastModifyTime}
    <a href="<c:url value='/document/download-${doc.id}' />" class="btn btn-success custom-width">Download</a>
    <br>
    <c:if test="${doc.description.isEmpty()}">
        Description: ${doc.description}
    </c:if>
    ${doc.content}
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
                            <p class="">${comment.text}</p> <span class="date sub-text">${comment.date}</span>
                        </div>
                    </li>
                </c:forEach>
                </ul>
                <form class="form-inline" role="form">
                    <div class="form-group">
                        <input class="form-control" type="text" placeholder="Your comments" />
                    </div>
                    <div class="form-group">
                        <button class="btn btn-default">Add</button>
                    </div>
                </form>
            </div>
        </div>
</div>

</body>
</html>