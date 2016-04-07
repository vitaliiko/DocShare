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

    <div class="container" style="width: 300px;" align="left">
        <form action="/profile/changeName" method="post">
            <h4>Set your account</h4>

            <input type="text" name="firstName" value="${firstName}" id="firstName"
                   class="form-control" placeholder="First Name" required="" autofocus="">
            <br>
            <input type="text" name="lastName" id="lastName" value="${lastName}"
                   class="form-control" placeholder="Last Name" required="" autofocus="">
            <br>
            <input type="text" name="login" value="${login}" id="login"
                   class="form-control" placeholder="Login" required="" autofocus="">
            <br>
            <input type="text" name="lastName" id="email" value="${email}"
                   class="form-control" placeholder="Email Address" autofocus="">
            <br>
            <button class="btn btn-primary btn-sm" type="submit">Save</button>
        </form>
        <hr/>

        <form class="form-signin" action="/profile/changePassword" method="post">
            <h4>To change password enter your current password</h4>

            <input type="password" name="currentPassword" id="currentPassword"
                   class="form-control" placeholder="Password" required="">
            <br>
            <input type="password" name="newPassword" id="newPassword"
                   class="form-control" placeholder="Password" required="">
            <br>
            <input type="password" name="confirmNewPassword" id="confirmNewPassword"
                   class="form-control" placeholder="Password" required="">
            <br>
            <button class="btn btn-primary btn-sm" type="submit">Save</button>
        </form>
        <hr/>

        <form action="/profile/changeLocation" method="post">
            <h4>Set your location</h4>

            <input type="text" name="country" value="${country}" id="country"
                   class="form-control" placeholder="Country" required="" autofocus="">
            <br>
            <input type="text" name="state" id="state" value="${state}"
                   class="form-control" placeholder="State" required="" autofocus="">
            <br>
            <input type="text" name="city" value="${city}" id="city"
                   class="form-control" placeholder="City" required="" autofocus="">
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
                    <form action="/profile/removeAccount">
                        <input type="submit" id="deleteDocument" class="btn btn-success" value="YES">
                        <button type="button" class="btn btn-danger" data-dismiss="modal">NO</button>
                    </form>
                </div>
            </div>

        </div>
    </div>

</body>
<footer>
    <jsp:include page="../include/footer.jsp"/>
</footer>
</html>
