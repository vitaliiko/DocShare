<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Login</title>
    <%--<link rel="stylesheet" href="/css/bootstrap.min.css" />--%>
</head>
<body>
<div class="container">
    <form class="form-signin" action="/signIn" method="post">
        <h2 class="form-signin-heading">Please sign in</h2>
        <h4 class="form-signin-heading">${errorMessage}</h4>
        <label for="inputEmail" class="sr-only">Login</label>
        <input type="text" name="login" id="inputEmail" class="form-control" placeholder="Email address" required="" autofocus="">
        <label for="inputPassword" class="sr-only">Password</label>
        <input type="password" name="password" id="inputPassword" class="form-control" placeholder="Password" required="">
        <button class="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>
    </form>

    <form action="/signUp">
        <input type="submit" value="Sign Up">
    </form>
</div>
</body>
</html>
