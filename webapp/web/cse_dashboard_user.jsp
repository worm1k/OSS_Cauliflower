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

<%
    User user = (User)request.getSession().getAttribute(CauliflowerInfo.USER_ATTRIBUTE);
    if(user==null || (user!=null && !user.getUserRole().equals(UserRole.CUST_SUP_ENG.toString())))
        response.sendRedirect("home.jsp");
%>

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

        <div class="col-xs-6 border-right">
            <h4 class="txt-bold">About Customer User:</h4>
            <dl class="dl-horizontal">
                <dt>Id:</dt>
                <%
                    User us = (User)request.getSession().getAttribute("customerUser");
                    out.print("<dd>" + us.getUserId()+"</dd>");
                %>
                <dt>Name:</dt>
                <%
                    //us = (User)request.getSession().getAttribute("customerUser");
                    out.print("<dd>" + us.getFirstName()+"</dd>");
                %>
                <dt>Surname:</dt>
                <%
                    //us = (User)request.getSession().getAttribute("customerUser");
                    out.print("<dd>" + us.getLastName()+"</dd>");
                %>
                <dt>Email:</dt>
                <%
                    //us = (User)request.getSession().getAttribute("customerUser");
                    out.print("<dd>" + us.getEmail()+"</dd>");
                %>
                <dt>Phone:</dt>
                <%
                    //us = (User)request.getSession().getAttribute("customerUser");
                    out.print("<dd>" + us.getPhone()+"</dd>");
                %>
                <dt>Role:</dt>
                <%
                    //us = (User)request.getSession().getAttribute("customerUser");
                    out.print("<dd>" + us.getUserRole()+"</dd>");
                %>
                <dt>isBlocked:</dt>
                <%
                    //us = (User)request.getSession().getAttribute("customerUser");
                    out.print("<dd>" + us.isBlocked()+"</dd>");
                %>
            </dl>
        </div>

        <div class="col-xs-6">
            <h4 class="txt-bold">Change password:</h4>
            <form action="changepass" method="POST">
                <h4>New password:</h4>
                <input type="hidden" name="userIdForNewPass" value=${customerUser.userId}>
                <input type="password" class="form-control" name="newPassword" placeholder="secret-password">
                <button type="submit" class="btn btn-default btn-change-password btn-block">Change password</button>
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