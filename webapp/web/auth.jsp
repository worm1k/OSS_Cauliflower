<%--
  Created by IntelliJ IDEA.
  User: Vladmyr
  Date: 02.12.14
  Time: 22:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en" ng-app="NgApp">
<head>
    <title>CauliFlower | Operation Support System</title>

    <jsp:include page="head.jsp" />
</head>
<body>
    <jsp:include page="header.jsp" />

    <div class="container">
        <h1 class="txt-center txt-bold">CauliFlower OSS</h1>
        <div class="col-xs-6">
            <h2 class="txt-center">Login</h2>
            <form class="form-signin" action="login" method="post">
                <h4>Email</h4>
                <input type="email" class="form-control" placeholder="you@example.com" required autofocus name="username">
                <h4>Password</h4>
                <input type="password" class="form-control" placeholder="secret-password" required name="password">
                <a href="#">Forgot your password?</a>
                <button class="btn-signin btn btn-lg btn-primary btn-block" type="submit">Sign In</button>
            </form>
        </div>
        <div class="col-xs-6 border-left">
            <h2 class="txt-center">Registration <br> all fields are required</h2>
            <form class="form-signin" action="register" method="post" id="auth_reg_form">
                <h4><div  id="auth_email_text">Email</div></h4>
                <input type="email" class="form-control" placeholder="you@example.com" name="email" id="auth_reg_email" required>
                <h4>Password</h4>
                <input type="password" class="form-control" name="password" id="auth_reg_password"  placeholder="secret-password" required>
                <h4>Name</h4>
                <input type="text" class="form-control" name="name" id="auth_reg_Name" placeholder="Name" required>
                <h4>Surname</h4>
                <input type="text" class="form-control" name="surname" id="auth_reg_Surname" placeholder="Surname" required>
                <h4>Phone number</h4>
                <input type="text" class="form-control" name="phone" id="auth_reg_Phone" placeholder="Phone" required>
                <input type="hidden" name="userRoleId" value="1" >
                <button class="btn-signin btn btn-lg btn-primary btn-block" id="auth_reg_submit" type="submit">Sign Up</button>
            </form>
        </div>
    </div>

    
<jsp:include page="footer.jsp" />

</body>
</html>