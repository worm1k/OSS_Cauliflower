<%--
  Created by IntelliJ IDEA.
  User: Артем
  Date: 20.11.2014
  Time: 14:30
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang=en>
<head>
  <meta charset=utf-8>
  <meta http-equiv=X-UA-Compatible content="width=device-width, initial-scale=1">
  <title>CauliFlower | Operation Support System</title>
  <link rel=icon>
  <link rel=stylesheet href=vendor/bootstrap/dist/css/bootstrap.min.css>
  <link rel=stylesheet href=vendor/bootstrap/dist/css/bootstrap-theme.min.css>
  <link rel=stylesheet href=css/main.css>
</head>
<body>
<script type=text/javascript src="https://maps.googleapis.com/maps/api/js?key=AIzaSyCP497dnVu2vETnDTcY9ouPNOyCMhccFcs">

</script>
<script type=text/javascript src=./vendor/angularjs/angular.min.js></script>
<script type=text/javascript src=./vendor/jquery/dist/jquery.min.js>

</script>
<script type=text/javascript src=./vendor/gmap3/dist/gmap3.min.js>

</script>
<script type=text/javascript src=./js/index.js>

</script>

<div class="navbar navbar-fixed-top">
  <div class=container><div class="collapse navbar-collapse">
    <ul class="nav navbar-nav">
      <li>
        <a href=#>create order testing</a>
      </li>
      <li>
        <a href=#>navbtn2</a>
      </li>
      <li>
        <a href=#>navbtn3</a>
      </li>
    </ul>
  </div>
  </div>
</div>
<div class=container>
  <h1 class="txt-center txt-bold">CauliFlower OSS</h1>
  <h2 class=txt-center>Login to the System</h2>
  <form class=form-signin action="login" method="post">
    <h4>Email</h4>
    <input type=email class=form-control placeholder=you@example.com required autofocus name="username">
    <h4>Password</h4>
    <input type=password class=form-control placeholder=secret-password required name="password">
    <button class="btn-signin btn btn-lg btn-success btn-block" type=submit>Sign In</button>
  </form>

  <form action="reports">
  <input type="submit" value="GetReport">
</form>

  <form action="mail">
    <input type="submit" value="GetMail">
  </form>

</div>



</body>
</html>
