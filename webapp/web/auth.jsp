<%--
  Created by IntelliJ IDEA.
  User: Vladmyr
  Date: 02.12.14
  Time: 22:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="width=device-width, initial-scale=1">

    <title>CauliFlower | Operation Support System</title>

    <link rel="icon" href="">
    <link rel="stylesheet" href="vendor/bootstrap/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="vendor/bootstrap/dist/css/bootstrap-theme.min.css">
    <link rel="stylesheet" href="css/main.css">
</head>
<body>
<div class="navbar navbar-fixed-top">
    <div class="container">
        <div class="collapse navbar-collapse">
            <ul class="nav navbar-nav">
                <li><a href="#">navbtn1</a></li>
                <li><a href="#">navbtn2</a></li>
                <li><a href="#">navbtn3</a></li>
            </ul>
        </div>
    </div>
</div>

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
        <h2 class="txt-center">Registration</h2>
        <form class="form-signin" action="register" method="post">
            <h4>Email</h4>
            <input type="email" class="form-control" placeholder="you@example.com" name="email" required>
            <h4>Password</h4>
            <input type="password" class="form-control" name="password" placeholder="secret-password" required>
            <h4>Name</h4>
            <input type="text" class="form-control" name="name" placeholder="Name" required>
            <h4>Surname</h4>
            <input type="text" class="form-control" name="surname" placeholder="Surname" required>
            <h4>Phone number</h4>
            <input type="text" class="form-control" name="phone" placeholder="Phone" required>
            <input type="hidden" name="userRoleId" value="1" >
            <button class="btn-signin btn btn-lg btn-primary btn-block" type="submit">Sign Up</button>
        </form>
    </div>
</div>

<script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?key=AIzaSyCP497dnVu2vETnDTcY9ouPNOyCMhccFcs"></script>
<script type="text/javascript" src="./vendor/jquery/dist/jquery.min.js"></script>
<script type="text/javascript" src="./vendor/gmap3/dist/gmap3.min.js"></script>
<script type="text/javascript" src="./js/index.js"></script>

</body>
</html>