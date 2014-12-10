<%--
  Created by IntelliJ IDEA.
  User: Eugene
  Date: 03.12.2014
  Time: 8:57
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.naukma.cauliflower.entities.User" %>
<%@ page import="com.naukma.cauliflower.info.CauliflowerInfo" %>
<%@ page import="com.naukma.cauliflower.dao.UserRole" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%
  User user = (User)request.getSession().getAttribute(CauliflowerInfo.USER_ATTRIBUTE);
  if(user==null || (user!=null && user.getUserRole().equals(UserRole.CUSTOMER.toString())))
    response.sendRedirect("home.jsp");
%>

<!DOCTYPE html>
<html lang="en">
<head>
  <title>CauliFlower | Reports </title>
  <jsp:include page="head.jsp"/>
</head>
<body>
<jsp:include page="header.jsp"/>

<div class="container">
  <h1 class="txt-center txt-bold">CauliFlower OSS</h1>
  <h2 class="txt-center">Provisioning Engineer Dashboard</h2>
  <%--Server message shows here--%>
  <c:if test="${sessionScope.error ne null && not empty sessionScope.error}">
    <div class="alert alert-danger alert-dismissible" role="alert">
      <button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">×</span><span class="sr-only">Close</span></button>
      <p><b>Server message:</b> ${sessionScope.error}</p>
    </div>
  </c:if>

  <c:if test="${sessionScope.ok ne null && not empty sessionScope.ok}">
    <div class="alert alert-success alert-dismissible" role="alert">
      <button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">×</span><span class="sr-only">Close</span></button>
      <p><b>Server message:</b> ${sessionScope.ok}</p>
    </div>
  </c:if>



<jsp:include page="footer.jsp"/>
<script type="text/javascript" src="./js/indexSlavko.js"></script>
</body>
</html>
