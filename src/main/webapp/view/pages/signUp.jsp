<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Sign Up</title>
    <jsp:include page="../include/include.jsp"/>
</head>
<body>
<div class="container">
    <form class="form-signin" action="/signUp" method="post">
        <h2 class="form-signin-heading">Please register</h2>
        <h4>${errorMessage}</h4>
        <label for="login" class="sr-only">Login</label>
        <input type="text" name="login" id="login" value="${login}" class="form-control" placeholder="Email address" required="" autofocus="">
        <label for="firstName" class="sr-only">First Name</label>
        <input type="text" name="firstName" id="firstName" value="${firstName}" class="form-control" placeholder="First Name" required="" autofocus="">
        <label for="lastName" class="sr-only">Last Name</label>
        <input type="text" name="lastName" id="lastName" value="${lastName}" class="form-control" placeholder="Last Name" required="" autofocus="">
        <label for="inputPassword" class="sr-only">Password</label>
        <input type="password" name="password" id="inputPassword" class="form-control" placeholder="Password" required="">
        <label for="confirmPassword" class="sr-only">Repeat password</label>
        <input type="password" name="confirmPassword" id="confirmPassword" class="form-control" placeholder="Password" required="">
        <button class="btn btn-lg btn-primary btn-block" type="submit">Register</button>
    </form>
</div>
</body>
</html>
