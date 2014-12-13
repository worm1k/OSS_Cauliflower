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

<%
    User user = (User)request.getSession().getAttribute(CauliflowerInfo.USER_ATTRIBUTE);
    if(user==null || (user!=null && !user.getUserRole().equals(UserRole.CUST_SUP_ENG.toString())))
        response.sendRedirect("home.jsp");
%>

<!DOCTYPE html>
<html lang="en" ng-app="CSEDashboard">
<head>
    <title>CauliFlower | Customer Support Engineer Dashboard</title>
    <jsp:include page="head.jsp"/>
</head>
<body ng-controller="CSEDashboardController">
    <jsp:include page="header.jsp"/>

    <div class="container">
        <h1 class="txt-center txt-bold">CauliFlower</h1>
        <h2 class="txt-center">Customer Support Engineer Dashboard</h2>

        <table class="table table-striped">
            <thead>
            <tr>
                <th>#</th>
                <th>Id</th>
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
                <td>{{user.userId}}</td>
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
</body>
</html>