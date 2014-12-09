<%@ page import="com.naukma.cauliflower.entities.Task" %>
<%@ page import="java.util.List" %>
<%@ page import="com.naukma.cauliflower.info.CauliflowerInfo" %>
<%--
  Created by IntelliJ IDEA.
  User: Артем
  Date: 06.12.2014
  Time: 22:01
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%--<%--%>
  <%--System.out.println("dashboard");--%>
  <%--List<Task> tasks=(List<Task>)request.getSession().getAttribute(CauliflowerInfo.TASKS_PARAM);--%>
  <%--//System.out.println(tasks);--%>
  <%--if(tasks==null) response.sendRedirect("gettasks");--%>
  <%--else System.out.println(tasks);--%>
  <%--//request.getRequestDispatcher("/gettasks").forward(request, response);--%>
<%--%>--%>

<!DOCTYPE html>
<html lang="en" ng-app="NgApp">
<head>
  <title></title>
  <jsp:include page="head.jsp"/>
</head>
<body>
<jsp:include page="header.jsp"/>

<div class="container">
  <h1 class="txt-center txt-bold">CauliFlower OSS</h1>
  <%--Server message shows here--%>
  <c:if test="${sessionScope.error ne null && not empty sessionScope.error}">
    <div class="alert alert-danger alert-dismissible" role="alert">
      <button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">×</span><span class="sr-only">Close</span></button>
      <p><b>Server message:</b> ${sessionScope.error}</p>
    </div>
  </c:if>

  <c:if test="${sessionScope.ok ne null && not empty sessionScope.ok}">
    <div class="alert alert-success alert-dismissible" role="alert">
      <button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">×</span><span class="sr-only">Close</span></button>
      <p><b>Server message:</b> ${sessionScope.ok}</p>
    </div>
  </c:if>

  <div class="col-xs-6">
    <h2 class="txt-center">New tasks</h2>
    <form class="form-proc_tasks" action="" method="post" id="form-ent_tasks" autocomplete="off">
      <table class="table table-striped">
        <thead>
        <tr>
          <th>#</th>
          <th>Id</th>
          <th>Scenario</th>
          <th>Status</th>
          <th></th>
        </tr>
        </thead>
        <tbody>
        <%--<tr ng-repeat="order in serviceInstance.arrServiceOrder">--%>
        <tr>
          <td>1</td>
          <td>1</td>
          <td>Add</td>
          <td>Entering</td>
          <td>
            <!-- <button class="btn btn-xs btn-default"><span class="glyphicon glyphicon-cog"></span></button> -->
            <button class="btn btn-xs btn-default"><span class="glyphicon glyphicon-trash"></span></button>
          </td>
        </tr>
        </tbody>
      </table>
    </form>
  </div>
  <div class="col-xs-6 border-left">
    <h2 class="txt-center">Processing tasks</h2>
    <form class="form-proc_tasks" action="" method="post" id="form-proc_tasks" autocomplete="off">
      <table class="table table-striped">
        <thead>
        <tr>
          <th>#</th>
          <th>Id</th>
          <th>Scenario</th>
          <th>Status</th>
          <th></th>
        </tr>
        </thead>
        <tbody>
        <%--<tr ng-repeat="order in serviceInstance.arrServiceOrder">--%>
        <tr>
          <td>1</td>
          <td>1</td>
          <td>Add</td>
          <td>Entering</td>
          <td>
            <!-- <button class="btn btn-xs btn-default"><span class="glyphicon glyphicon-cog"></span></button> -->
            <button class="btn btn-xs btn-default"><span class="glyphicon glyphicon-trash"></span></button>
            <button class="btn btn-xs btn-default"><span class="glyphicon glyphicon-trash"></span></button>
          </td>
        </tr>
        </tbody>
      </table>
    </form>
  </div>
</div>

<jsp:include page="footer.jsp"/>
<script type="text/javascript" src="./js/indexSlavko.js"></script>
</body>
</html>
