<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="utf-8">

    <title>Profile</title>

    <jsp:include page="../include/include.jsp"/>
</head>
<body>

    <jsp:include page="../include/header.jsp"/>
    <jsp:include page="../include/sidebar.jsp"/>

    <div class="container" style="width: 300px;" align="left">
        <form action="/profile/changeName" method="post">
            <h4 class="form-signin-heading">Set your account</h4>
            <h4 class="form-signin-heading">${errorMessage}</h4>
            <h4 class="form-signin-heading">${message}</h4>

            <input type="text" name="login" value="${login}" id="login"
                   class="form-control" placeholder="Email address" required="" autofocus="">

            <input type="text" name="firstName" value="${firstName}" id="firstName"
                   class="form-control" placeholder="First Name" required="" autofocus="">

            <input type="text" name="lastName" id="lastName" value="${lastName}"
                   class="form-control" placeholder="Last Name" required="" autofocus="">

            <button class="btn btn-lg btn-primary btn-md" type="submit">Save</button>
        </form>
        <hr/>

        <form class="form-signin" action="/profile/changePassword" method="post">
            <h4 class="form-signin-heading">To change password enter your current password</h4>

            <input type="password" name="currentPassword" id="currentPassword"
                   class="form-control" placeholder="Password" required="">

            <input type="password" name="newPassword" id="newPassword"
                   class="form-control" placeholder="Password" required="">

            <input type="password" name="confirmNewPassword" id="confirmNewPassword"
                   class="form-control" placeholder="Password" required="">

            <button class="btn btn-lg btn-primary btn-md" type="submit">Save</button>
        </form>
        <hr/>

        <form class="form-signin" action="/profile/removeAccount" method="get">
            <h4 class="form-signin-heading">You can delete your account, just push the button</h4>
            <button class="btn btn-lg btn-primary btn-md" type="submit">Delete your account</button>
        </form>
    </div>

    <jsp:include page="../include/footer.jsp"/>
</body>
</html>
