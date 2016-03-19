<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Sign In</title>
    <jsp:include page="../include/include.jsp"/>
</head>
<body>
<div class="container" style="width: 300px;" align="center">
    <form class="form-signin" action="/main/signIn" method="post">
        <h2 class="form-signin-heading">Please sign in</h2>
        <h4 class="form-signin-heading">${errorMessage}</h4>
        <%--<div class="alert alert-danger">--%>
            <%--${errorMessage}--%>
        <%--</div>--%>
        <input type="text" name="login" class="form-control" placeholder="Email address" required="" autofocus="">
        <input type="password" name="password" class="form-control" placeholder="Password" required="">
        <button class="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>
    </form>

    <form action="/main/signUp">
        <input type="submit" class="btn btn-link" value="Sign Up">
    </form>
</div>
</body>
</html>
