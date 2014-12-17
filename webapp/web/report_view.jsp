<%--
  Created by IntelliJ IDEA.
  User: Eugene
  Date: 17.12.2014
  Time: 0:44
  To change this template use File | Settings | File Templates.
--%>


<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

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

        console.log(reportMethod);
        console.log(startDate);
        console.log(endDate);
    </script>
</head>
<body ng-controller="ReportViewController">
    <jsp:include page="header.jsp"/>
    <div class="container">
        <h1 class="text-center txt-bold">Cauliflower</h1>
        <h2 class="text-center">View "${param.reportMethod}" report</h2>

        <table class="table table-striped" class="ng-cloak">
            <thead>
                <tr>
                    <th>#</th>
                    <th ng-repeat="(key, val) in json[0]">{{key}}</th>
                </tr>
            </thead>
            <tbody>
                <tr ng-repeat="item in json">
                    <td>{{$index + 1}}</td>
                    <td ng-repeat="(key, val) in item">{{val}}</td>
                </tr>
            </tbody>
        </table>
    </div>

    <jsp:include page="footer.jsp"/>
</body>
</html>
