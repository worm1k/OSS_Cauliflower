<%--
  Created by IntelliJ IDEA.
  User: Артем
  Date: 06.12.2014
  Time: 22:01
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html lang="en" ng-app="IEDashboard">
<head>
    <title>CauliFlower | Installation Engineer Dashboard</title>
    <jsp:include page="head.jsp"/>
</head>
<body ng-controller="IEDashboardController">
    <jsp:include page="header.jsp"/>

    <div class="container">
        <h1 class="txt-center txt-bold">{{test}} CauliFlower OSS</h1>
        <h2 class="txt-center">Installation Engineer Dashboard</h2>
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
                <tr ng-repeat="task in arrTaskFree">
                    <td>{{$index + 1}}</td>
                    <td>{{task.taskId}}</td>
                    <td>{{task.taskName}}</td>
                    <td>{{task.taskStatus}}</td>
                    <td>
                        <form action="" method="POST">
                            <input type="hidden" name="taskId" value="{{task.taskId}}"/>
                            <input type="hidden" name="taskStatus" value="{{task.taskStatus}}"/>
                            <button class="btn btn-xs btn-info">Subscribe</button>
                        </form>
                    </td>
                </tr>
            </tbody>
          </table>
      </div>
      <div class="col-xs-6 border-left">
        <h2 class="txt-center">My active tasks</h2>
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
                <button class="btn btn-xs btn-default">Open</button>
                <button class="btn btn-xs btn-danger">Unsubscribe</button>
              </td>
            </tr>
            </tbody>
          </table>
        </form>
      </div>
    </div>

<jsp:include page="footer.jsp"/>
</body>
</html>
