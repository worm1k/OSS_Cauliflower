<%--
  Created by IntelliJ IDEA.
  User: Vladmyr
  Date: 10.12.14
  Time: 15:11
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en" ng-app="CSEDashboard">
<head>
    <title>CauliFlower | Customer User inforormation</title>
    <jsp:include page="head.jsp"/>

    <script>
        const customerUserId = ${customerUser.userId};
        console.log(customerUserId);
    </script>
</head>
<body ng-controller="CSEDashboardUserController">
    <jsp:include page="header.jsp"/>

    <div class="container">
        <h1 class="text-center txt-bold">CauliFlower OSS</h1>
        <h2 class="text-center">Customer User Information</h2>

        <div class="col-xs-6 border-right">
            <h4 class="txt-bold">About Customer User:</h4>
            <dl class="dl-horizontal">
                <dt>Id:</dt>
                <dd>{{customerUser.userId}}</dd>
                <dt>Name:</dt>
                <dd>{{customerUser.firstName}}</dd>
                <dt>Surname:</dt>
                <dd>{{customerUser.lastName}}</dd>
                <dt>Email:</dt>
                <dd>{{customerUser.email}}</dd>
                <dt>Phone:</dt>
                <dd>{{customerUser.phone}}</dd>
                <dt>Role:</dt>
                <dd>{{customerUser.userRole}}</dd>
                <dt>isBlocked:</dt>
                <dd>{{customerUser.blocked}}</dd>
            </dl>
        </div>

        <div class="col-xs-6">
            <h4 class="txt-bold">Change password:</h4>
            <form action="" method="">
                <h4>New password:</h4>
                <input type="password" class="form-control" name="" placeholder="secret-password">
                <button type="submit" class="btn btn-default btn-change-password btn-block">Change password</button>
            </form>
        </div>

        <div class="col-xs-12 border-top">
            <h4 class="txt-bold">Service Instance:</h4>
            <select class="form-control" ng-model="serviceInstance"
                    ng-options="item.serviceLocation.locationAddress for item in arrServiceInstance" ng-change="update()">
            </select>
            <h4 class="txt-bold">General:</h4>
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
            <h4 class="txt-bold">Service Orders:</h4>
            <div class="table-responsive">
                <table class="table table-striped">
                    <thead>
                    <tr>
                        <th>#</th>
                        <th>Id</th>
                        <th>Scenario</th>
                        <th>Status</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr ng-repeat="order in serviceInstance.arrServiceOrder">
                        <td>{{$index + 1}}</td>
                        <td>{{order.id}}</td>
                        <td>{{order.orderScenario}}</td>
                        <td>{{order.orderStatus}}</td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <jsp:include page="footer.jsp"/>
</body>
</html>