<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

<head>
  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
  <title>Upload/Download/Delete Documents</title>
  <jsp:include page="../include/include.jsp"/>
</head>

<body>

<jsp:include page="../include/header.jsp"/>
<jsp:include page="../include/sidebar.jsp"/>

<div class="container" style="width: 900px;">

    <div>
        <div class="panel-heading"><span class="lead">Upload New Document</span>
            <form action="/document/upload" method="POST" enctype="multipart/form-data">
                <label class="col-md-3 control-lable" for="file">Upload a document</label>
                <input type="file" name="file" id="file" class="form-control input-sm"/>
                <label class="col-md-3 control-lable" for="description">Description</label>
                <input type="text" name="description" id="description" class="form-control input-sm"/>
                <div class="form-actions floatRight">
                    <input type="submit" value="Upload" class="btn btn-primary btn-sm">
                </div>
            </form>
        </div>
    </div>

          </form>
      </div>
</div>
    <div class="panel panel-default">
        <div class="panel-heading"><span class="lead">List of Documents </span></div>
        <div class="tablecontainer">
            <table class="table table-hover tbody tr:hover td">
                <thead>
                    <tr>
                        <th>No.</th>
                        <th>File Name</th>
                        <th>Type</th>
                        <th>Description</th>
                        <th width="100"></th>
                        <th width="100"></th>
                    </tr>
                </thead>
                <tbody>
                <c:forEach items="${documents}" var="doc" varStatus="counter">
                    <tr>
                        <td>${counter.index + 1}</td>
                        <td>${doc.name}</td>
                        <td>${doc.type}</td>
                        <td>${doc.description}</td>
                        <td><a href="<c:url value='/document/download-${doc.id}' />"
                               class="btn btn-success custom-width">download</a></td>
                        <td><a href="<c:url value='/document/delete-${doc.id}' />"
                               class="btn btn-primary custom-width">delete</a></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>


    <div class="well">
      <%--Go to <a href="<c:url value='/list' />">Users List</a>--%>
    </div>
</div>
</body>
</html>