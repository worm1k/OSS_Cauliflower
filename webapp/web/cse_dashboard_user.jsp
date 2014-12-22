<%@ page import="com.naukma.cauliflower.info.CauliflowerInfo" %>
<%@ page import="com.naukma.cauliflower.dao.UserRole" %>
<%@ page import="com.naukma.cauliflower.entities.User" %>
<%@ page import="com.naukma.cauliflower.entities.ServiceInstance" %>
<%@ page import="java.util.List" %>

<%--
  Created by IntelliJ IDEA.
  User: Vladmyr
  Date: 10.12.14
  Time: 15:11
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>



<!DOCTYPE html>
<html lang="en" ng-app="CSEDashboard">
<head>
    <title>Customer User Information | CauliFlower</title>
    <jsp:include page="head.jsp"/>

    <script>
        const customerUserId = ${customerUser.userId};
        console.log(customerUserId);
    </script>
</head>
<body ng-controller="CSEDashboardUserController">
    <jsp:include page="header.jsp"/>

    <div class="container">
        <h1 class="text-center txt-bold">Internet Provider "CauliFlower"</h1>
        <h2 class="text-center">Customer User Information</h2>
        <%--Server message shows here--%>
        <c:if test="${sessionScope.error ne null && not empty sessionScope.error}">
            <div class="alert alert-danger alert-dismissible" role="alert">
                <button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">×</span><span class="sr-only">Close</span></button>
                <p><b>Server message:</b> ${sessionScope.error}</p>
            </div>
            <%
                request.getSession().removeAttribute(CauliflowerInfo.ERROR_ATTRIBUTE);
            %>
        </c:if>

        <c:if test="${sessionScope.ok ne null && not empty sessionScope.ok}">
            <div class="alert alert-success alert-dismissible" role="alert">
                <button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">×</span><span class="sr-only">Close</span></button>
                <p><b>Server message:</b> ${sessionScope.ok}</p>
            </div>
            <%
                request.getSession().removeAttribute(CauliflowerInfo.OK_ATTRIBUTE);
            %>
        </c:if>


        <div class="col-xs-6 border-right">
            <h4 class="txt-bold">About Customer User:</h4>
            <dl class="dl-horizontal">
                <dt>Name:</dt>
                <%
                    User us = (User)request.getSession().getAttribute("customerUser");
                    if(us!=null)out.print("<dd>" + us.getFirstName()+"</dd>");
                %>
                <dt>Surname:</dt>
                <%
                    //us = (User)request.getSession().getAttribute("customerUser");
                    if(us!=null)out.print("<dd>" + us.getLastName()+"</dd>");
                %>
                <dt>Email:</dt>
                <%
                    //us = (User)request.getSession().getAttribute("customerUser");
                    if(us!=null)out.print("<dd>" + us.getEmail()+"</dd>");
                %>
                <dt>Phone:</dt>
                <%
                    //us = (User)request.getSession().getAttribute("customerUser");
                    if(us!=null)out.print("<dd>" + us.getPhone()+"</dd>");
                %>
                <dt>Role:</dt>
                <%
                    //us = (User)request.getSession().getAttribute("customerUser");
                    if(us!=null)out.print("<dd>" + us.getUserRole()+"</dd>");
                %>
                <dt>isBlocked:</dt>
                <%
                    //us = (User)request.getSession().getAttribute("customerUser");
                    if(us!=null)out.print("<dd>" + us.isBlocked()+"</dd>");
                %>
            </dl>
        </div>

        <div class="col-xs-6">
            <h4 class="txt-bold">Change password:</h4>
            <form id="js-form-change-pass" action="changepass" method="POST">
                <h4>New password:</h4>
                <input type="hidden" name="userIdForNewPass" value=${customerUser.userId}>
                <div class="form-group" id="js-change-pass">
                    <input type="password" class="form-control" name="newPassword" placeholder="secret-password" data-placement="bottom" data-trigger="manual" data-content="Password must contain at least 6 symbols" required>
                </div>
                <button type="submit" id="js-act-change-pass" class="btn btn-default btn-change-password btn-block">Change password</button>
            </form>
        </div>

        <div class="col-xs-12 border-top">
            <h4 class="txt-bold">Service Instance:</h4>
            <select class="form-control" ng-model="serviceInstance"
                    ng-options="item.serviceLocation.locationAddress for item in arrServiceInstance" ng-change="update()">
                <%--<%--%>
                    <%--final List<ServiceInstance> listInstance = (List<ServiceInstance>)request.getSession().getAttribute("lstServiceInstance");--%>
                    <%--for(int i=0;i<listInstance.size();i++){--%>
                        <%--out.print("ng-options=\"istInstance.get(i).getServiceLocation()\" ng-change = \"update()\"");--%>
                    <%--}--%>
                <%--%>--%>
            </select>
            <h4 class="txt-bold">General:</h4>
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
            <h4 class="txt-bold">Service Orders:</h4>
            <div class="table-responsive">
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

    <jsp:include page="footer.jsp"/>
</body>
</html>