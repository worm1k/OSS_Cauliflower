<%@ page import="com.naukma.cauliflower.entities.User" %>
<%@ page import="com.naukma.cauliflower.info.CauliflowerInfo" %>
<%@ page import="com.naukma.cauliflower.dao.UserRole" %>
<%--
  Created by IntelliJ IDEA.
  User: Vladmyr
  Date: 04.12.14
  Time: 11:50
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    User user = (User)request.getSession().getAttribute(CauliflowerInfo.USER_ATTRIBUTE);
    if(user==null || (user!=null && !user.getUserRole().equals(UserRole.CUSTOMER.toString())))
        response.sendRedirect("home.jsp");
%>


<!DOCTYPE html>
<html lang="en" ng-app="MapDashboard">
<head>
    <title>CauliFlower | Dashboard</title>
    <jsp:include page="head.jsp"/>
</head>
<body ng-controller="MapDashboardController">
    <jsp:include page="header.jsp"/>

    <div class="container">
        <h1 class="txt-center txt-bold">CauliFlower OSS</h1>
        <h2 class="txt-center">Dashboard</h2>
    </div>

    <div id="js-map" class="google-map"></div>

    <div class="container">
        <div class="col-xs-12 border-bottom">
            <h3>Service Instance:</h3>
            <select class="form-control" ng-model="serviceInstance"
                    ng-options="item.serviceLocation.locationAddress for item in arrServiceInstance" ng-change="update()">
            </select>
            <dl class="dl-horizontal">
                <dt>Id:</dt>
                <dd>{{serviceInstance.id}}</dd>
                <dt>Service Location:</dt>
                <dd>{{serviceInstance.serviceLocation.locationAddress}}</dd>
                <dt>Provider Location:</dt>
                <dd>{{serviceInstance.providerLocation.locationAddress}}</dd>
                <dt>Service:</dt>
                <dd>{{service.serviceTypeName}}</dd>
                <dt>Service speed:</dt>
                <dd>{{service.serviceSpeed}} Mbps</dd>
                <dt>Service price:</dt>
                <dd>{{service.price}}$</dd>
                <dt>Status:</dt>
                <dd>{{serviceInstance.instanceStatus}}</dd>
            </dl>
        </div>
        <div class="col-xs-8 border-right margin-bottom">
            <h3>Modify Service:</h3>
            <form action="services" method="POST">
                <ul>
                    <li ng-repeat="service in serviceInstance.providerLocation.arrService">
                        <label class="radio font-regular">
                            <input type="radio" name="serviceId" value="{{service.id}}" checked>
                            <span>{{service.serviceTypeName}}</span>,
                            <span>{{service.serviceSpeed}} Mbps</span>,
                            <span>{{service.price}}$</span>
                        </label>
                    </li>
                </ul>

                <%-- serviceLocation data --%>
                <input type="hidden" name="instanceId" value="{{serviceInstance.id}}">
                <input type="hidden" name="scenario" value="MODIFY">

                <button type="submit" class="btn btn-success">Modify</button>
            </form>
        </div>
        <div class="col-xs-4 margin-bottom">
            <h3>Disconnect:</h3>
            <form action="services" method="POST">
                <input type="hidden" name="instanceId" value="{{serviceInstance.id}}">
                <input type="hidden" name="scenario" value="DISCONNECT">
                <button type="submit" class="btn btn-danger">Disconnect</button>
            </form>
        </div>
        <div class="col-xs-12 border-top">
            <h3>Service Orders:</h3>
            <div class="table-responsive">
                <table class="table">
                </table>

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
                    <tr ng-repeat="order in serviceInstance.arrServiceOrder">
                        <td>{{$index + 1}}</td>
                        <td>{{order.id}}</td>
                        <td>{{order.orderScenario}}</td>
                        <td>{{order.orderStatus}}</td>
                        <td>
                            <!-- <button class="btn btn-xs btn-default"><span class="glyphicon glyphicon-cog"></span></button> -->
                            <button class="btn btn-xs btn-default"><span class="glyphicon glyphicon-trash"></span></button>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
        <%--<div class="col-xs-12 jumbotron" ng-class="{hasServiceInstance: item-visible-false}">--%>
            <%--<h3 class="text-center txt-bold">There are no Service Instaces</h3>--%>
        <%--</div>--%>
    </div>

    <jsp:include page="footer.jsp"/>
</body>
</html>