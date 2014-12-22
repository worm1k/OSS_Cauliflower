<%@ page import="com.naukma.cauliflower.entities.User" %>
<%@ page import="com.naukma.cauliflower.info.CauliflowerInfo" %>
<%@ page import="com.naukma.cauliflower.dao.UserRole" %>
<%--
  Created by IntelliJ IDEA.
  User: Eugene
  Date: 17.12.2014
  Time: 0:44
  To change this template use File | Settings | File Templates.
--%>


<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%

    User user = (User) request.getSession().getAttribute(CauliflowerInfo.USER_ATTRIBUTE);
    String reportParameter = request.getParameter("reportMethod");
    if(user==null || user.getUserRole().equals(UserRole.CUSTOMER) || reportParameter==null)
        response.sendRedirect(CauliflowerInfo.HOME_LINK);
%>

<!DOCTYPE html>
<html ng-app="ReportView">
<head>
    <title>Report view | Cauliflower</title>
    <jsp:include page="head.jsp"/>
    <script>
        const reportMethod = '${param.reportMethod}';

        <c:choose>
            <c:when test="${param.startDate ne null && not empty param.startDate}">
        const startDate = '${param.startDate}';
            </c:when>
            <c:otherwise>
        const startDate = null;
            </c:otherwise>
        </c:choose>
        <c:choose>
            <c:when test="${param.endDate ne null && not empty param.endDate}">
        const endDate = '${param.endDate}';
            </c:when>
            <c:otherwise>
        const endDate = null;
            </c:otherwise>
        </c:choose>
    </script>
</head>
<body ng-controller="ReportViewController">
    <jsp:include page="header.jsp"/>
    <div class="container">
        <h1 class="text-center txt-bold">Internet Provider "CauliFlower"</h1>
        <h2 class="text-center">View "${param.reportMethod}" report</h2>

        <ul id="pagination"></ul>

        <table class="table table-striped">
            <thead>
                <tr>
                    <th>#</th>
                    <th ng-repeat="(key, val) in json[0]" class="ng-cloak">{{key}}</th>
                </tr>
            </thead>
            <tbody>
                <tr ng-repeat="item in json" class="ng-cloak">
                    <td>{{(page - 1) * linesOnPage + $index + 1}}</td>
                    <td ng-repeat="(key, val) in item">{{val}}</td>
                </tr>
            </tbody>
        </table>
    </div>

    <jsp:include page="footer.jsp"/>
</body>
</html>
