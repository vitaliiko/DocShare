<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Sign Up</title>
    <jsp:include page="../include/include.jsp"/>
    <script src="${pageContext.request.contextPath}/view/bootstrap/js/jquery-1.12.2.min.js"></script>
    <script src="${pageContext.request.contextPath}/view/bootstrap/js/bootstrap.min.js"></script>
</head>
<body>
<div class="container" style="width: 500px;" align="center">
    <form class="form-signin" action="/signUp" method="post">
        <h2 class="form-signin-heading">Please register</h2>
        <div class="alert alert-danger alert-dismissable">
            <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
            ${errorMessage}
        </div>
        <input type="text" name="login" id="login" value="${login}" class="form-control" placeholder="Email address" required="" autofocus="">
        <input type="text" name="firstName" id="firstName" value="${firstName}" class="form-control" placeholder="First Name" required="" autofocus="">
        <input type="text" name="lastName" id="lastName" value="${lastName}" class="form-control" placeholder="Last Name" required="" autofocus="">
        <input type="password" name="password" id="inputPassword" class="form-control" placeholder="Password" required="">
        <input type="password" name="confirmPassword" id="confirmPassword" class="form-control" placeholder="Confirm Password" required="">
        <button class="btn btn-lg btn-primary btn-block" type="submit">Register</button>
    </form>
</div>
</body>
</html>
