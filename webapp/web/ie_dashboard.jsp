<%@ page import="com.naukma.cauliflower.dao.UserRole" %>
<%@ page import="com.naukma.cauliflower.entities.User" %>
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

<%
    User user = (User)request.getSession().getAttribute(CauliflowerInfo.USER_ATTRIBUTE);
    if(user==null || (user!=null && !user.getUserRole().equals(UserRole.INSTALLATION_ENG.toString())))
        response.sendRedirect("home.jsp");
%>

<!DOCTYPE html>
<html lang="en" ng-app="IEDashboard">
<head>
    <title>CauliFlower | Installation Engineer Dashboard</title>
    <jsp:include page="head.jsp"/>
</head>
<body ng-controller="IEDashboardController">
    <jsp:include page="header.jsp"/>

    <div class="container">
        <h1 class="txt-center txt-bold">{{test}} CauliFlower</h1>
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
                            <form action="manageTask" method="POST">
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
                <tr ng-repeat="task in arrTaskSubscribed">
                    <td>{{$index + 1}}</td>
                    <td>{{task.taskId}}</td>
                    <td>{{task.taskName}}</td>
                    <td>{{task.taskStatus}}</td>
                    <td >
                        <form action="installationController" method="POST">
                            <input type="hidden" name="taskId" value="{{task.taskId}}"/>
                            <input type="hidden" name="serviceOrderId" value="{{task.serviceOrderId}}"/>
                            <button type="submit" class="btn btn-xs btn-success">Done</button>
                        </form>
                        <form action="manageTask" method="POST">
                            <input type="hidden" name="taskId" value="{{task.taskId}}"/>
                            <input type="hidden" name="taskStatus" value="{{task.taskStatus}}"/>
                            <button class="btn btn-xs btn-danger">Unsubscribe</button>
                        </form>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="footer.jsp"/>
</body>
</html>
