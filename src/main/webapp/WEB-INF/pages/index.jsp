<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<html>
  <head>
    <title>Chat</title>
    <%--<link rel="stylesheet" href="/css/bootstrap.min.css" />--%>
  </head>
  <body>

    <c:forEach var="message" items="${messages}">
        ${message.user.firstName} ${message.user.lastName} (${message.date}): ${message.text}<br/>
    </c:forEach>
    <hr/>
    <h4>${errorMessage}</h4>
    <form action="/index" method="post">
        <label for="text">Text:</label><br/>
        <textarea id="text" name="text" cols="100" rows="3" maxlength="256"></textarea><br/>
        <input type="submit" value="Submit" />
    </form>

    <form action="/profile">
        <input type="submit" value="Set your account">
    </form>

    <jsp:include page="signOut.jsp"/>
  </body>
</html>
