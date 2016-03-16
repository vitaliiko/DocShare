<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Edit</title>
</head>
<body>
    <div class="container">
        <form class="form-signin" action="/changeName" method="post">
            <h2 class="form-signin-heading">Set your account</h2>
            <h4 class="form-signin-heading">${errorMessage}</h4>
            <h4 class="form-signin-heading">${message}</h4>
            <label for="login" class="sr-only">Login</label>
            <input type="text" name="login" value="${login}" id="login" class="form-control" placeholder="Email address" required="" autofocus="">
            <label for="firstName" class="sr-only">First Name</label>
            <input type="text" name="firstName" value="${firstName}" id="firstName" class="form-control" placeholder="First Name" required="" autofocus="">
            <label for="lastName" class="sr-only">Last Name</label>
            <input type="text" name="lastName" id="lastName" value="${lastName}" class="form-control" placeholder="Last Name" required="" autofocus="">
            <button class="btn btn-lg btn-primary btn-block" type="submit">Save</button>
        </form>
        <hr/>

        <form class="form-signin" action="/changePassword" method="post">
            <h3 class="form-signin-heading">To change password enter your current password</h3>
            <label for="currentPassword" class="sr-only">Current password</label>
            <input type="password" name="currentPassword" id="currentPassword" class="form-control" placeholder="Password" required="">
            <label for="newPassword" class="sr-only">New password</label>
            <input type="password" name="newPassword" id="newPassword" class="form-control" placeholder="Password" required="">
            <label for="confirmNewPassword" class="sr-only">Repeat new password</label>
            <input type="password" name="confirmNewPassword" id="confirmNewPassword" class="form-control" placeholder="Password" required="">
            <button class="btn btn-lg btn-primary btn-block" type="submit">Save</button>
        </form>
        <hr/>

        <form class="form-signin" action="/removeAccount" method="get">
            <h3 class="form-signin-heading">You can delete your account, just push the button</h3>
            <button class="btn btn-lg btn-primary btn-block" type="submit">Delete your account</button>
        </form>
    </div>
    <a href="/index">Back to chat</a>
    <jsp:include page="signOut.jsp"/>
</body>
</html>
