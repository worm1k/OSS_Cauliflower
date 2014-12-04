<%--
  Created by IntelliJ IDEA.
  User: Vladmyr
  Date: 02.12.14
  Time: 23:37
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<header class="navbar navbar-fixed-top">
    <div class="container">
        <img src="img/logo_xs.png" class="img-responsive img-header-logo pull-left" alt="Cauliflower"/>
        <!-- <div class="navbar-header">
            <div class="input-group">
                <a href="#" class="navbar-brand">Cauliflower</a>
            </div>
        </div> -->
        <div class="collapse navbar-collapse">
            <ul class="nav navbar-nav">
                <li><a class="txt-uppercase" href="home.jsp">Home</a></li>
                <li><a class="txt-uppercase" href="order.jsp">Order</a></li>
            </ul>
            <ul class="nav navbar-nav navbar-right" ng-controller="UserLoginController">
                <!-- user is logged in -->
                <li>
                    <a href="dashboard.jsp" class="item-visible-{{userIsLogged}}" style="display: none;"><span class="nav-user-icon glyphicon glyphicon-user glyphicon-small"></span>{{user.email}}</a>
                    <a href="auth.jsp" class="item-visible-{{!userIsLogged}}" ></span>User Cabinet</a>
                </li>
            </ul>
        </div>
    </div>
</header>