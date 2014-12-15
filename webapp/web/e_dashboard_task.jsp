<%@ page import="com.naukma.cauliflower.dao.UserRole" %>
<%@ page import="com.naukma.cauliflower.info.CauliflowerInfo" %>
<%@ page import="com.naukma.cauliflower.entities.User" %>
<%--
  Created by IntelliJ IDEA.
  User: Vladmyr
  Date: 14.12.14
  Time: 12:53
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%
    User user = (User)request.getSession().getAttribute(CauliflowerInfo.USER_ATTRIBUTE);
    if(user == null){
        response.sendRedirect("home.jsp");
    }else if(!user.getUserRole().equals(UserRole.INSTALLATION_ENG.toString()) && !user.getUserRole().equals(UserRole.PROVISIONING_ENG.toString())){
        response.sendRedirect("home.jsp");
    }
%>

<html>
<head>
    <title>Cauliflower | Task Information</title>
    <jsp:include page="head.jsp"/>
</head>
<body>
    <jsp:include page="header.jsp"/>

    <div class="container">
        <h1 class="text-center txt-bold">CauliFlower</h1>
        <h2 class="text-center">Task Information</h2>

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

        <div class="col-xs-6">
            <div class="col-xs-12">
                <h4 class="txt-bold">About Task:</h4>
                <dl class="dl-horizontal">
                    <dt>Task id:</dt>
                    <dd>${task.taskId}</dd>
                    <dt>Task name:</dt>
                    <dd>${task.taskName}</dd>
                    <dt>Task status:</dt>
                    <dd>${task.taskStatus}</dd>
                </dl>
            </div>
            <div class="col-xs-12">
                <h4 class="txt-bold">Customer User:</h4>
                <dl class="dl-horizontal">
                    <dt>User id:</dt>
                    <dd>${customerUser.userId}</dd>
                    <dt>User name:</dt>
                    <dd>${customerUser.firstName}</dd>
                    <dt>User surname:</dt>
                    <dd>${customerUser.lastName}</dd>
                    <dt>User email:</dt>
                    <dd>${customerUser.email}</dd>
                    <dt>User phone:</dt>
                    <dd>${customerUser.phone}</dd>
                </dl>
            </div>
        </div>
        <div class="col-xs-6 border-left">
            <div class="col-xs-12">
                <h4 class="txt-bold">Service Order:</h4>
                <dl class="dl-horizontal">
                    <dt>Order id:</dt>
                    <dd>${serviceOrder.serviceOrderId}</dd>
                    <dt>Order status:</dt>
                    <dd>${serviceOrder.orderStatus}</dd>
                    <dt>Order scenario:</dt>
                    <dd>${serviceOrder.orderScenario}</dd>
                </dl>
            </div>
            <div class="col-xs-12">
                <h4 class="txt-bold">Service Instance:</h4>
                <dl class="dl-horizontal">
                    <dt>Instance id:</dt>
                    <dd>${serviceInstance.instanceId}</dd>
                    <dt>Service Location:</dt>
                    <dd>${serviceInstance.serviceLocation.locationAddress}</dd>
                    <dt>Instance status:</dt>
                    <dd>${serviceInstance.instanceStatus}</dd>
                    <dt>Instance cable id:</dt>
                    <dd>${serviceInstance.cableId}</dd>
                </dl>
            </div>
            <div class="col-xs-12">
                <h4 class="txt-bold">Service:</h4>
                <dl class="dl-horizontal">
                    <dt>Id:</dt>
                    <dd>${service.serviceId}</dd>
                    <dt>Provider Location:</dt>
                    <dd>${service.locationAddress}</dd>
                    <dt>Service:</dt>
                    <dd>${service.serviceTypeName}</dd>
                    <dt>Service speed:</dt>
                    <dd>${service.serviceSpeed} Mbps</dd>
                    <dt>Service price:</dt>
                    <dd>${service.price}$</dd>
                </dl>
            </div>
            <c:if test="${newService ne null && not empty newService}">
                <div class="col-xs-12">
                    <h4 class="txt-bold">Modify to Service:</h4>
                    <dl class="dl-horizontal">
                        <dt>Id:</dt>
                        <dd>${newService.serviceId}</dd>
                        <dt>Service:</dt>
                        <dd>${newService.serviceTypeName}</dd>
                        <dt>Service speed:</dt>
                        <dd>${newService.serviceSpeed} Mbps</dd>
                        <dt>Service price:</dt>
                        <dd>${newService.price}$</dd>
                    </dl>
                </div>
            </c:if>
        </div>
        <div class="col-xs-12">
            <c:choose>
                <c:when test="${task.taskStatus == 'FREE'}">
                    <form class="" action="manageTask" method="POST">
                        <input type="hidden" name="taskId" value="${task.taskId}"/>
                        <input type="hidden" name="taskStatus" value="${task.taskStatus}"/>
                        <button type="submit" class="btn btn-lg btn-info btn-task-done btn-block">Assign to me</button>
                    </form>
                </c:when>
                <c:otherwise>
                    <form class="" action="${action}" method="POST">
                        <input type="hidden" name="taskId" value="${task.taskId}"/>
                        <input type="hidden" name="serviceOrderId" value="${serviceOrder.serviceOrderId}"/>
                        <button type="submit" class="btn btn-lg btn-success btn-task-done btn-block">Done</button>
                    </form>
                </c:otherwise>
            </c:choose>
            <a href="${dashboard}" class="btn-block text-center">Back to Dashboard</a>
        </div>
    </div>

    <jsp:include page="footer.jsp"/>
</body>
</html>