<%@ page import="com.naukma.cauliflower.info.CauliflowerInfo" %>
<%@ page import="com.naukma.cauliflower.entities.User" %>
<%@ page import="com.naukma.cauliflower.dao.UserRole" %>
<%--
  Created by IntelliJ IDEA.
  User: Артем
  Date: 05.12.2014
  Time: 21:19
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>




<!DOCTYPE html>
<html lang="en" ng-app="NgApp">
<head>
  <title>Administrator | CauliFlower</title>
  <jsp:include page="head.jsp"/>
</head>
<body>
<jsp:include page="header.jsp"/>

<div class="container">
  <h1 class="text-center txt-bold">Internet Provider "CauliFlower"</h1>
    <h2 class="text-center">Administrator Dashboard</h2>
  <%--Server message shows here--%>
  <c:if test="${sessionScope.error ne null && not empty sessionScope.error}">
    <div class="alert alert-danger alert-dismissible" role="alert">
      <button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">×</span><span class="sr-only">Close</span></button>
      <p><b>Server message:</b> ${sessionScope.error}</p>
    </div>
    <%
      request.getSession().removeAttribute(CauliflowerInfo.ERROR_ATTRIBUTE);
    %>
  </c:if>

  <c:if test="${sessionScope.ok ne null && not empty sessionScope.ok}">
    <div class="alert alert-success alert-dismissible" role="alert">
      <button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">×</span><span class="sr-only">Close</span></button>
      <p><b>Server message:</b> ${sessionScope.ok}</p>
    </div>
    <%
      request.getSession().removeAttribute(CauliflowerInfo.OK_ATTRIBUTE);
    %>
  </c:if>

  <div class="col-xs-6">
    <h2 class="txt-center">Registration of new employee</h2>
    <form id="auth_reg_form" class="form-signin" action="register" method="post" autocomplete="off">
      <div class="form-group" id="auth_reg_role">
        <h4>Role <span class="required"></span></h4>
        <div >
          <select id="eng_role" name="userRole" class="form-control">
            <option value="CUST_SUP_ENG">Customer Support Engineer</option>
            <option value="PROVISIONING_ENG">Provisioning Engineer</option>
            <option value="INSTALLATION_ENG">Installation Engineer</option>
          </select>
        </div>
      </div>
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
      <button id="auth_reg_submit" class="btn-signin btn btn-lg btn-primary btn-block" type="submit">Register</button>
    </form>
  </div>
  <div class="col-xs-6 border-left">
    <h2 class="txt-center">Block account</h2>
    <form class="form-signin" action="blockacc" method="post" id="auth_block_form" autocomplete="off">
      <div class="form-group" id="auth_block_email">
        <h4>Email <span class="required"></span></h4>
        <input type="text" id="js-email-to-block" class="form-control" placeholder="you@example.com" data-trigger="manual" data-placement="bottom" data-content="Email address is not valid" name="email" required>
      </div>
      <button class="btn-signin btn btn-lg btn-primary btn-block" id="auth_block_submit" type="submit">Block</button>
    </form>
  </div>
</div>

<jsp:include page="footer.jsp"/>
</body>
</html>