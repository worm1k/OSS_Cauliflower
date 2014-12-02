<%--
  Created by IntelliJ IDEA.
  User: Vladmyr
  Date: 01.12.14
  Time: 10:07
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en" ng-app="MapOrder">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="width=device-width, initial-scale=1">

    <title>CauliFlower | Operation Support System</title>

    <link rel="icon" href="">
    <link rel="stylesheet" href="vendor/bootstrap/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="vendor/bootstrap/dist/css/bootstrap-theme.min.css">
    <link rel="stylesheet" href="css/main.css">
</head>
<!-- <body ng-controller="OrderController"> -->
<body ng-controller="MapOrderController">
    <header class="navbar navbar-fixed-top">
        <div class="container">
            <div class="collapse navbar-collapse">
                <ul class="nav navbar-nav">
                    <li><a class="txt-uppercase" href="#">navbtn1</a></li>
                    <li><a class="txt-uppercase" href="#">navbtn2</a></li>
                    <li><a class="txt-uppercase" href="#">navbtn3</a></li>
                </ul>
                <ul class="nav navbar-nav navbar-right">
                    <li><a class="nav-user"><span class="nav-user-icon glyphicon glyphicon-user glyphicon-small"></span>khytsky.vladimir@gmail.com</a></li>
                </ul>
            </div>
        </div>
    </header>

    <div class="container">
        <h1 class="txt-center txt-bold">CauliFlower OSS</h1>
        <h2 class="txt-center">Service order</h2>
        <div class="row search-container">
            <div class="col-xs-12 search">
                <input type="text" id="js-address" class="form-control" placeholder="Search by address" ng-model="serviceLocationAddress">
            </div>
        </div>
    </div>
    <div id="js-map" class="google-map"></div>

    <div class="container">
        <h2 class="txt-center">Your current orders</h2>
        <div class="table-responsive">
            <table class="table table-striped">
                <thead>
                <tr>
                    <th>#</th>
                    <th>Service Location</th>
                    <th>Provider Location</th>
                    <th>Service</th>
                    <th>Price, $</th>
                    <th>Status</th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>1</td>
                    <td>вул. Закревського 49а, кв. 140</td>
                    <td>вул. Закревського 56, офіс 4</td>
                    <td>Platinum</td>
                    <td>35</td>
                    <td>Entering</td>
                    <td>
                        <button class="btn btn-xs btn-default"><span class="glyphicon glyphicon-cog"></span></button>
                        <button class="btn btn-xs btn-default"><span class="glyphicon glyphicon-trash"></span></button>
                    </td>
                </tr>
                <tr>
                    <td>2</td>
                    <td>вул. Закревського 49а, кв. 140</td>
                    <td>вул. Закревського 56, офіс 4</td>
                    <td>Platinum</td>
                    <td>35</td>
                    <td>Entering</td>
                    <td>
                        <button class="btn btn-xs btn-default"><span class="glyphicon glyphicon-cog"></span></button>
                        <button class="btn btn-xs btn-default"><span class="glyphicon glyphicon-trash"></span></button>
                    </td>
                </tr>
                <tr>
                    <td>3</td>
                    <td>вул. Закревського 49а, кв. 140</td>
                    <td>вул. Закревського 56, офіс 4</td>
                    <td>Platinum</td>
                    <td>35</td>
                    <td>Entering</td>
                    <td>
                        <button class="btn btn-xs btn-default"><span class="glyphicon glyphicon-cog"></span></button>
                        <button class="btn btn-xs btn-default"><span class="glyphicon glyphicon-trash"></span></button>
                    </td>
                </tr>
                </tbody>
            </table>

            <button type="button" class="btn btn-primary btn-lg center-block btn-proceed">Proceed to Order</button>
        </div>
    </div>

    <div class="infobox-wrapper item-visible-false">
        <div id="js-infobox" class="infobox">
            <p><b>Provider Location address:</b> {{providerLocationAddress}}</p>
            <p><b>Customer Location address:</b> {{serviceLocationAddress}}</p>
            <p><b>Available services:</b></p>
            <ul id="js-service-list" class="unstyled">
                <li ng-repeat="service in arrService">
                    <label class="radio">
                        <input type="radio" name="service" value="1" checked>
                        <span>{{service.getServiceTypeName()}}</span>,
                        <span>{{service.getServiceSpeed()}} Mbps</span>,
                        <span>{{service.getPrice()}}$</span>
                    </label>
                </li>
            </ul>
            <button type="button" class="btn btn-sm btn-primary center-block">Add Order</button>
        </div>
    </div>

    <footer class="footer">
        <div class="container">
            <ul class="list-inline">
                <li><a href="#">navlink1</a></li>
                <li><a href="#">navlink2</a></li>
                <li><a href="#">navlink3</a></li>
            </ul>
        </div>
    </footer>

    <script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?key=AIzaSyCP497dnVu2vETnDTcY9ouPNOyCMhccFcs&libraries=places"></script>
    <script type="text/javascript" src="./vendor/angularjs/angular.min.js"></script>
    <script type="text/javascript" src="./vendor/jquery/dist/jquery.min.js"></script>
    <script type="text/javascript" src="./vendor/gmap3/dist/gmap3.min.js"></script>
    <script type="text/javascript" src="./vendor/google-infobox/google-infobox.js"></script>
    <script type="text/javascript" src="./vendor/geocomplete/jquery.geocomplete.min.js"></script>
    <!-- custom scripts -->
    <script type="text/javascript" src="./js/index.js"></script>
</body>
</html>