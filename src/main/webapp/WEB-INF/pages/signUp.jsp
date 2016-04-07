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

    <div class="container" style="width: 500px;" align="center">
        <form class="form-signin" action="/main/sign_up" method="post">
            <input type="text" name="firstName" id="firstName" value="${firstName}"
                   class="form-control" placeholder="First Name" required="" autofocus="">
            <br>
            <input type="text" name="lastName" id="lastName" value="${lastName}"
                   class="form-control" placeholder="Last Name" required="" autofocus="">
            <br>
            <input type="text" name="login" id="login" value="${login}"
                   class="form-control" placeholder="Login" required="" autofocus="">
            <br>
            <input type="password" name="password" id="inputPassword"
                   class="form-control" placeholder="Password" required="">
            <br>
            <input type="password" name="confirmPassword" id="confirmPassword"
                   class="form-control" placeholder="Confirm Password" required="">
            <br>
            <button class="btn btn-lg btn-primary btn-block" type="submit">Register</button>
        </form>
    </div>
</body>
</html>
