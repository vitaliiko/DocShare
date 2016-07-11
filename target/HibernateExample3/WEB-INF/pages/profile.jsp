<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="utf-8">

    <title>Profile</title>

    <jsp:include page="../include/include.jsp"/>
    <script src="${pageContext.request.contextPath}/resources/js/navbar.js"></script>
</head>

<body style="padding-top: 65px;">

<jsp:include page="../include/navbar.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<div class="container col-md-10">
    <div class="container" style="width: 600px;" align="center">
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

    <div class="container" style="width: 450px;" align="left">
        <h4>Set your account</h4>

        <form action="/api/profile" method="post">
            <label for="firstName">First name: </label>
            <input type="text" name="firstName" value="${user.firstName}" id="firstName"
                   class="form-control" required="" autofocus="">
            <br>
            <label for="lastName">Last name: </label>
            <input type="text" name="lastName" id="lastName" value="${user.lastName}"
                   class="form-control" required="" autofocus="">
            <br>
            <label for="login">Login: </label>
            <input type="text" name="login" value="${user.login}" id="login"
                   class="form-control" required="" autofocus="">
            <br>
            <label for="email">E-mail address: </label>
            <input type="text" name="email" id="email" value="${user.email}"
                   class="form-control" autofocus="">
            <br>
            <label for="country">Country: </label>
            <input type="text" name="country" value="${user.country}" id="country"
                   class="form-control" autofocus="">
            <br>
            <label for="state">State: </label>
            <input type="text" name="state" id="state" value="${user.state}"
                   class="form-control" autofocus="">
            <br>
            <label for="city">City: </label>
            <input type="text" name="city" value="${user.city}" id="city"
                   class="form-control" autofocus="">
            <br>
            <button class="btn btn-primary btn-sm" type="submit">Save</button>
        </form>
        <hr/>

        <form class="form-signin" action="/api/profile/password" method="post">
            <h4>To change password enter your current password</h4>

            <label for="currentPassword">Your current password: </label>
            <input type="password" name="currentPassword" id="currentPassword"
                   class="form-control" required="">
            <br>
            <label for="newPassword">New password: </label>
            <input type="password" name="newPassword" id="newPassword"
                   class="form-control" required="">
            <br>
            <label for="confirmNewPassword">Confirm new password: </label>
            <input type="password" name="confirmNewPassword" id="confirmNewPassword"
                   class="form-control" required="">
            <br>
            <button class="btn btn-primary btn-sm" type="submit">Save</button>
        </form>
        <hr/>

        <div>
            <h4>You can delete your account, just push the button</h4>
            <button name="delete-btn" class="btn btn-primary btn-sm"
                    data-toggle="modal" data-target="#deleteDialog">Delete your account</button>
        </div>
    </div>
</div>

<div id="deleteDialog" class="modal fade" role="dialog">
    <div class="modal-dialog">

        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Warning!</h4>
            </div>
            <div class="modal-body">
                <h4>Are you sure you want to delete your account?</h4>
                <h4>Your files will be removed irretrievably.</h4>
            </div>
            <div class="modal-footer">
                <form action="/api/profile/remove" method="post">
                    <input type="submit" id="deleteDocument" class="btn btn-success" value="YES">
                    <button type="button" class="btn btn-danger" data-dismiss="modal">NO</button>
                </form>
            </div>
        </div>

    </div>
</div>

</body>
</html>
