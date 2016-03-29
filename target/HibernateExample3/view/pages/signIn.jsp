<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Sign In</title>
    <jsp:include page="../include/include.jsp"/>
    <script src="${pageContext.request.contextPath}/view/js/signIn.js"></script>
</head>
<body>
    <div class="container" style="width: 600px;" align="center">
        <h2 class="form-signin-heading">Please sign in</h2>

        <c:if test="${errorMessage != null}">
            <div class='alert alert-danger'>
                <a href='#' class='close' data-dismiss='alert'>&times;</a>
                <strong>Error!</strong> ${errorMessage}
            </div>
            <br>
        </c:if>

        <c:if test="${message != null}">
            <div class='alert alert-success'>
                <a href='#' class='close' data-dismiss='alert'>&times;</a>
                    ${message}
            </div>
            <br>
        </c:if>
    </div>

    <c:url value="/j_spring_security_check" var="loginUrl" />
    <div class="container" style="width: 300px;" align="center">
        <form class="form-signin" action="/main/sign_in" method="post">
            <input type="text" name="login" class="form-control" placeholder="Email address" required="" autofocus="">
            <br>
            <input type="password" name="password" class="form-control" placeholder="Password" required="">
            <br>
            <button class="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>
        </form>

        <form action="/main/sign_up">
            <input type="submit" class="btn btn-link" value="Sign Up">
        </form>
    </div>
</body>
</html>
