<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>${doc.name}</title>
    <jsp:include page="../include/include.jsp"/>
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
</div>

</body>
</html>
