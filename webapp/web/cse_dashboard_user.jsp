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

        <div class="col-xs-12 jumbotron">
            <h3>Service Instance:</h3>
            ${customerUser.userId}
            <table class="table table-striped">
                <thead>
                    <tr>
                        <th></th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td></td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>

    <jsp:include page="footer.jsp"/>
</body>
</html>