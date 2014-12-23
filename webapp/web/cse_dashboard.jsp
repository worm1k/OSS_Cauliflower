<%@ page import="com.naukma.cauliflower.info.CauliflowerInfo" %>
<%@ page import="com.naukma.cauliflower.dao.UserRole" %>
<%@ page import="com.naukma.cauliflower.entities.User" %>
<%--
  Created by IntelliJ IDEA.
  User: Vladmyr
  Date: 09.12.14
  Time: 20:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<!DOCTYPE html>
<html lang="en" ng-app="CSEDashboard">
<head>
    <title>Customer Support Engineer Dashboard | CauliFlower</title>
    <jsp:include page="head.jsp"/>
</head>
<body ng-controller="CSEDashboardController">
    <jsp:include page="header.jsp"/>

    <div class="container">
        <h1 class="txt-center txt-bold">Internet Provider "CauliFlower"</h1>
        <h2 class="txt-center">Customer Support Engineer Dashboard</h2>

        <table class="table table-striped">
            <thead>
            <tr>
                <th>#</th>
                <th>Name</th>
                <th>Surname</th>
                <th>Email</th>
                <th>Phone</th>
                <th>Role</th>
                <th>isBlocked</th>
                <th></th>
            </tr>
            </thead>
            <tbody>
            <tr ng-repeat="user in arrUser" class="ng-cloak">
                <td>{{$index + 1}}</td>
                <td>{{user.firstName}}</td>
                <td>{{user.lastName}}</td>
                <td>{{user.email}}</td>
                <td>{{user.phone}}</td>
                <td>{{user.userRole}}</td>
                <td>{{user.blocked}}</td>
                <td>
                    <form action="customer_info" method="POST">
                        <input type="hidden" name="userId" ng-value="{{user.userId}}"/>
                        <button type="submit" class="btn btn-xs btn-default">Open</button>
                    </form>
                </td>
            </tr>
            </tbody>
        </table>
    </div>
    <jsp:include page="footer.jsp"/>

    <%
        request.getSession().removeAttribute(CauliflowerInfo.TASK_PARAM);
        request.getSession().removeAttribute(CauliflowerInfo.SERVICE_ORDER_ATTRIBUTE);
        request.getSession().removeAttribute(CauliflowerInfo.SERVICE_INSTANCE_ATTRIBUTE);
        request.getSession().removeAttribute(CauliflowerInfo.SERVICE_ATTRIBUTE);
        request.getSession().removeAttribute(CauliflowerInfo.MODIFY_TO_SERVICE_ATTRIBUTE);
        request.getSession().removeAttribute(CauliflowerInfo.CUSTOMER_USER_ATTRIBUTE);
        request.getSession().removeAttribute(CauliflowerInfo.ACTION_ATTRIBUTE);
        request.getSession().removeAttribute(CauliflowerInfo.DASHBOARD_PARAM);
    %>
</body>
</html>