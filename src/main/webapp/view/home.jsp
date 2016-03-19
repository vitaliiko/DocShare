<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <title>Simple Sidebar - Start Bootstrap Template</title>

    <link href="${pageContext.request.contextPath}/view/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/view/bootstrap/css/simple-sidebar.css" rel="stylesheet">

    <script src="${pageContext.request.contextPath}/view/bootstrap/js/jquery-1.12.2.min.js"></script>
    <script src="${pageContext.request.contextPath}/view/bootstrap/js/bootstrap.min.js"></script>
    <script src="${pageContext.request.contextPath}/view/bootstrap/js/sidebar_menu.js"></script>
</head>
<body>

    <jsp:include page="header.jsp"/>
    <jsp:include page="sidebar.jsp"/>

    <div id="wrapper" class="container">
        <div id="page-content-wrapper">
            <div class="container-fluid xyz">
                <div class="row">
                    <div class="col-lg-12">
                        <h1>Simple Sidebar With Bootstrap 3 by <a href="http://seegatesite.com/create-simple-cool-sidebar-menu-with-bootstrap-3/" >Seegatesite.com</a></h1>
                        <p>This sidebar is adopted from start bootstrap simple sidebar startboostrap.com, which I modified slightly to be more cool. For tutorials and how to create it , you can read from my site here <a href="http://seegatesite.com/create-simple-cool-sidebar-menu-with-bootstrap-3/">create cool simple sidebar menu with boostrap 3</a></p>
                    </div>
                </div>
            </div>
        </div>
    </div>

</body>
</html>
