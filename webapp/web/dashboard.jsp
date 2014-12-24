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
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html lang="en" ng-app="MapDashboard">
<head>
    <title>Dashboard | CauliFlower</title>
    <jsp:include page="head.jsp"/>
</head>
<body ng-controller="MapDashboardController">
    <jsp:include page="header.jsp"/>

    <div class="container">
        <h1 class="txt-center txt-bold">Internet Provider "CauliFlower"</h1>
        <h2 class="txt-center">Dashboard</h2>
        <%-- Server message shows here --%>
        <c:if test="${sessionScope.error ne null && not empty sessionScope.error}">
            <div class="alert alert-danger alert-dismissible" role="alert">
                <button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">×</span><span class="sr-only">Close</span></button>
                <p><b>Server message:</b> ${sessionScope.error}</p>
            </div>
            <%
                request.getSession().removeAttribute(CauliflowerInfo.ERROR_ATTRIBUTE);
            %>
        </c:if>
    </div>

    <div id="js-map" class="google-map"></div>

    <div class="container ng-cloak" ng-class="hasServiceInstance? 'item-visible-true' : 'item-visible-false'">
        <div class="col-xs-12">
            <h3>Service Instance:</h3>
            <select class="form-control" ng-model="serviceInstance"
                    ng-options="item.serviceLocation.locationAddress for item in arrServiceInstance" ng-change="update()">
            </select>
            <dl class="dl-horizontal ng-cloak">
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
        <div class="row border-top" ng-class="serviceInstance.isBlocked || serviceInstance.instanceStatus == 'DISCONNECTED'? 'item-visible-false' : 'item-visible-true'">
            <div class="col-xs-8 border-right margin-bottom">
                <h3>Modify Service:</h3>
                <p ng-class="arrAvailableServices.length > 0? 'item-visible-false' : 'item-visible-true'">There are no available services.</p>
                <form action="proceed" method="POST" ng-class="arrAvailableServices.length > 0? 'item-visible-true' : 'item-visible-false'">
                    <ul>
                        <li ng-repeat="service in arrAvailableServices" class="ng-cloak">
                            <label class="radio font-regular">
                                <input type="radio" name="serviceId" value="{{service.id}}" ng-checked="$index == 0? true : false">
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
                <form action="proceed" method="POST">
                    <input type="hidden" name="instanceId" value="{{serviceInstance.id}}">
                    <input type="hidden" name="scenario" value="DISCONNECT">
                    <button type="submit" class="btn btn-danger">Disconnect</button>
                </form>
            </div>
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
                        <th>Scenario</th>
                        <th>Status</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr ng-repeat="order in serviceInstance.arrServiceOrder" class="ng-cloak">
                        <td>{{$index + 1}}</td>
                        <td>{{order.orderScenario}}</td>
                        <td>{{order.orderStatus}}</td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <div class="container ng-cloak" ng-class="hasServiceInstance? 'item-visible-false' : 'item-visible-true'">
        <h3 class="text-center">There are no orders yet</h3>
        <p class="text-center">You have not created any one so far. You can start by clicking <a href="order.jsp">here</a>.</p>
    </div>

    <jsp:include page="footer.jsp"/>
</body>
</html>