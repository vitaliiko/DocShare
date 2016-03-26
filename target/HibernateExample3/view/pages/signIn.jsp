<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Sign In</title>
    <jsp:include page="../include/include.jsp"/>
    <script src="${pageContext.request.contextPath}/view/js/signIn.js"></script>
</head>
<body>
    <div class="container" style="width: 300px;" align="center">
        <form class="form-signin">
            <h2 class="form-signin-heading">Please sign in</h2>

            <div id="danger-div"></div>

            <input type="text" name="login" class="form-control" placeholder="Email address" required="" autofocus="">
            <br>
            <input type="password" name="password" class="form-control" placeholder="Password" required="">
            <br>
            <button class="btn btn-lg btn-primary btn-block" type="button">Sign in</button>
        </form>

        <form action="/main/signUp">
            <input type="submit" class="btn btn-link" value="Sign Up">
        </form>
    </div>
</body>
</html>
