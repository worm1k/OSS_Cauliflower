<%@ page import="com.naukma.cauliflower.info.CauliflowerInfo" %>
<%@ page import="com.naukma.cauliflower.entities.User" %>
<%@ page import="com.naukma.cauliflower.dao.UserRole" %>
<%--
  Created by IntelliJ IDEA.
  User: Vladmyr
  Date: 01.12.14
  Time: 10:07
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>



<!DOCTYPE html>
<html lang="en" ng-app="NgApp">
<head>
    <title>Connect | CauliFlower</title>
    <jsp:include page="head.jsp"/>
</head>
<body ng-controller="MapOrderController">

<jsp:include page="header.jsp"/>

<div class="container">
    <h1 class="txt-center txt-bold">Internet Provider "CauliFlower"</h1>
    <h2 class="txt-center">Connect to the internet</h2>
    <div class="row search-container item-visible-false">
        <div id="js-address-search" class="input-group search">
            <input type="text" id="js-address" class="form-control" placeholder="Search by address" ng-model="serviceLocationAddress" data-trigger="manual" data-placement="bottom" data-content="No result :(">
					<span class="input-group-btn">
						<button type="button" class="btn btn-primary" ng-click="mapSetActiveMarkerLocationByAddress()"><span class="glyphicon glyphicon-search"></span></button>
					</span>
        </div>
    </div>
</div>
<div id="js-map" class="google-map"></div>

<div class="infobox-wrapper item-visible-false">
    <div id="js-infobox" class="infobox">
        <p><b>Provider Location address:</b> {{providerLocationAddress}}</p>
        <p><b>Customer Location address:</b> {{serviceLocationAddress}}</p>
        <p><b>Available services:</b></p>
        <ul id="js-service-list" class="unstyled">
            <li ng-repeat="service in arrService">
                <label class="radio font-regular">
                    <input type="radio" name="serviceId" value="{{service.getId()}}" checked>
                    <span>{{service.getServiceTypeName()}}</span>,
                    <span>{{service.getServiceSpeed()}} Mbps</span>,
                    <span>{{service.getPrice()}}$</span>
                </label>
            </li>
        </ul>
        <form id="js-order-form" action="new" method="POST">
            <%-- serviceLocation data --%>
            <input type="hidden" name="serviceLocationAddress" value="">
            <input type="hidden" name="serviceLocationLongtitude" value="">
            <input type="hidden" name="serviceLocationLatitude" value="">
            <input type="hidden" name="scenario" value="NEW">
            <%-- service data --%>
            <input type="hidden" name="serviceId" value="">

            <button type="button" id="js-proceed-to-order" class="btn btn-sm btn-primary center-block">Add Order</button>
        </form>
    </div>
</div>

<jsp:include page="footer.jsp"/>

</body>
</html>