<%@ page import="com.naukma.cauliflower.info.CauliflowerInfo" %>
<%--
  Created by IntelliJ IDEA.
  User: Vladmyr
  Date: 02.12.14
  Time: 22:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html lang="en" ng-app="NgApp">
<head>
    <title>CauliFlower | Operation Support System</title>

    <jsp:include page="head.jsp" />
</head>
<body>
    <jsp:include page="header.jsp" />

    <div class="container">
        <h1 class="txt-center txt-bold">CauliFlower</h1>
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


        <div class="col-xs-6">
            <h2 class="txt-center">Login</h2>
            <form id="auth_log_form" class="form-signin" action="login" method="post" autocomplete="off">
                <div class="form-group" id="auth_log_email">
                    <h4>Email <span class="required"></span></h4>
                    <input type="text" class="form-control" placeholder="you@example.com" data-trigger="manual" data-placement="bottom" data-content="Email address is not valid" name="username" required>
                </div>
                <div class="form-group" id="auth_log_password">
                    <h4>Password <span class="required"></span></h4>
                    <input type="password" class="form-control" data-placement="bottom" data-trigger="manual" data-content="Password must contain at least 6 symbols" name="password" placeholder="secret-password" required>
                    <%--<a href="#">Forgot your password?</a>--%>
                </div>
                <button id="auth_log_submit" class="btn-signin btn btn-lg btn-primary btn-block" type="submit">Sign In</button>
            </form>
        </div>
        <div class="col-xs-6 border-left">
            <h2 class="txt-center">Registration</h2>
            <form class="form-signin" action="register" method="post" id="auth_reg_form" autocomplete="off">
                <div class="form-group" id="auth_reg_email">
                    <h4>Email <span class="required"></span></h4>
                    <input type="text" class="form-control" placeholder="you@example.com" data-trigger="manual" data-placement="bottom" data-content="Email address is not valid" name="email" required>
                </div>
                <div class="form-group" id="auth_reg_password">
                    <h4>Password <span class="required"></span></h4>
                    <input type="password" class="form-control" data-placement="bottom" data-trigger="manual" data-content="Password must contain at least 6 symbols" name="password" placeholder="secret-password" required>
                </div>
                <div class="form-group" id="auth_reg_Name">
                    <h4>Name <span class="required"></span></h4>
                    <input type="text" class="form-control" data-placement="bottom" data-trigger="manual" data-content="Name is too short" name="name" placeholder="Name" required>
                </div>
                <div class="form-group" id="auth_reg_Surname">
                    <h4>Surname <span class="required"></span></h4>
                    <input type="text" class="form-control" data-placement="bottom" data-trigger="manual" data-content="Surname is too short" name="surname" placeholder="Surname" required>
                </div>
                <div class="form-group" id="auth_reg_Phone">
                    <h4>Phone number <span class="required"></span></h4>
                    <input type="text" class="form-control" data-placement="bottom" data-trigger="manual" data-content="Phone is not valid" name="phone" placeholder="Phone" required>
                </div>
                <input type="hidden" name="userRole" value="CUSTOMER" >
                <button class="btn-signin btn btn-lg btn-primary btn-block" id="auth_reg_submit" type="submit">Sign Up</button>
            </form>
        </div>
    </div>

    
<jsp:include page="footer.jsp" />

</body>
</html>