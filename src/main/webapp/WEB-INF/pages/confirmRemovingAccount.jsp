<%--
  Created by IntelliJ IDEA.
  User: User
  Date: 10.02.2016
  Time: 14:03
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Remove Account</title>
</head>
<body>
    <form action="/removeAccount" method="post">
        <h2>Do you want to remove your account?</h2>
        <input type="submit" name="yes" value="Yes">
        <input type="submit" name="no" value="NO">
    </form>

</body>
</html>
