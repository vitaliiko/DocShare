<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Sign Up</title>
    <jsp:include page="../include/include.jsp"/>
</head>
<body>
    <div class="container" style="width: 600px;" align="center">
        <h2 class="form-signin-heading">Please register</h2>

        <c:if test="${errorMessage != null}">
            <div class='alert alert-danger'>
                <a href='#' class='close' data-dismiss='alert'>&times;</a>
                <strong>Error!</strong> ${errorMessage}
            </div>
            <br>
        </c:if>
    </div>

    <div class="container" style="width: 400px;" align="left">
        <form class="form-signin" action="/main/sign_up" method="post">
            <label for="firstName">First name: </label>
            <input type="text" name="firstName" id="firstName" value="${registrationInfo.firstName}"
                   class="form-control" required="" autofocus="">
            <br>
            <label for="lastName">Last name: </label>
            <input type="text" name="lastName" id="lastName" value="${registrationInfo.lastName}"
                   class="form-control" required="" autofocus="">
            <br>
            <label for="login">Login: </label>
            <input type="text" name="login" id="login" value="${registrationInfo.login}"
                   class="form-control" required="" autofocus="">
            <br>
            <label for="inputPassword">Password: </label>
            <input type="password" name="password" id="inputPassword" class="form-control" required="">
            <br>
            <label for="confirmPassword">Confirm password: </label>
            <input type="password" name="confirmationPassword" id="confirmPassword" class="form-control" required="">
            <br>
            <button class="btn btn-lg btn-primary btn-block" type="submit">Register</button>
        </form>
    </div>
</body>
</html>
