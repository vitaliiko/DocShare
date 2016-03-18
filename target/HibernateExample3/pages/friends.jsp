<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Friends</title>
</head>
<body>
    <div id="wrapper" class="container">
        <jsp:include page="sidebar.jsp"/>

        <h3>You haven't friends yet</h3>
        <form action="/main/search">
            <input type="submit" class="btn btn-link" value="Search friends">
        </form>
    </div>
</body>
</html>
