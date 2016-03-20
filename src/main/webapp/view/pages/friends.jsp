<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="utf-8">

    <title>Friends</title>

    <jsp:include page="../include/include.jsp"/>
</head>
<body>

    <jsp:include page="../include/header.jsp"/>
    <jsp:include page="../include/sidebar.jsp"/>

    <div id="wrapper" class="container">
        <div id="page-content-wrapper">
            <table class="table table-hover tbody tr:hover td">
                <caption>
                    Groups <input type="button" class="btn btn-primary btn-sm" value="Add group"/>
                </caption>

                <c:forEach var="group" items="${groups}">
                    <c:url var="groupPage" value="/main/userpage/${group.id}"/>
                    <tr>
                        <td>
                            <a href="${groupPage}" class="btn btn-link"> ${group} </a>
                        </td>
                        <td>

                        </td>
                    </tr>
                </c:forEach>
            </table>

            <table class="table table-hover tbody tr:hover td">
                <caption>
                    Friends <a href="/main/search" class="btn btn-primary btn-sm">Add friends</a>
                </caption>

                <c:forEach var="friendEntry" items="${friends}">
                    <c:url var="friendPage" value="/main/userpage/${friendEntry.key.id}"/>
                    <tr>
                        <td>
                            <input type="checkbox">
                            <a href="${friendPage}" class="btn btn-link"> ${friendEntry.key} </a>
                        </td>
                        <td>
                            <c:forEach var="group" items="${friendEntry.value}">
                                <c:url var="groupPage" value="/main/userpage/${group.id}"/>
                                <a href="${groupPage}">${group}</a>
                            </c:forEach>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </div>
    </div>

    <jsp:include page="../include/footer.jsp"/>
</body>
</html>
