<%@ page import="com.naukma.cauliflower.info.CauliflowerInfo" %>
<%@ page import="com.naukma.cauliflower.entities.User" %>
<%@ page import="com.naukma.cauliflower.dao.UserRole" %>
<%--
  Created by IntelliJ IDEA.
  User: Vladmyr
  Date: 02.12.14
  Time: 23:37
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<header class="navbar navbar-default navbar-fixed-top">
    <div class="container">
        <div class="navbar-header">
            <a class="navbar-brand" href="#">
                <img class="img-header-logo pull-left" alt="Brand" src="img/logo_xs.png">
            </a>
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar-collapse">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#"></a>
        </div>

        <div class="collapse navbar-collapse" id="navbar-collapse">
            <ul class="nav navbar-nav">
                <li><a class="txt-uppercase" href="home.jsp">Home</a></li>
                <%
                    User us = (User)request.getSession().getAttribute(CauliflowerInfo.USER_ATTRIBUTE);
                    if(us==null ||(us!=null && us.getUserRole().equals(UserRole.CUSTOMER.toString())))
                        out.print( "<li><a class=\"txt-uppercase\" href=\"order.jsp\">Order</a></li>");
                %>

            </ul>
            <ul class="nav navbar-nav navbar-right">
                <c:choose>
                    <c:when test="${sessionScope.user ne null && not empty sessionScope.user}">
                        <li class="dropdown">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown" aria-expanded="false"><span class="nav-user-icon glyphicon glyphicon-user glyphicon-small"></span>${sessionScope.user.email} <span class="caret"></span></a>
                            <ul class="dropdown-menu" role="menu">
                                <%
                                    //User us = (User)request.getSession().getAttribute(CauliflowerInfo.USER_ATTRIBUTE);
                                    if(us==null) out.print("<li><a href=\"auth.jsp\">Dashboard</a></li>");
                                    if(us!=null && us.getUserRole().equals(UserRole.CUSTOMER.toString()))
                                        out.print("<li><a href=\"dashboard.jsp\">Dashboard</a></li>");
                                    if(us!=null && us.getUserRole().equals(UserRole.ADMINISTRATOR.toString()))
                                        out.print("<li><a href=\"admdashboard.jsp\">Dashboard</a></li>");
                                    if(us!=null && us.getUserRole().equals(UserRole.INSTALLATION_ENG.toString()))
                                        out.print("<li><a href=\"ie_dashboard.jsp\">Dashboard</a></li>");
                                    if(us!=null && us.getUserRole().equals(UserRole.PROVISIONING_ENG.toString()))
                                        out.print("<li><a href=\"pe_dashboard.jsp\">Dashboard</a></li>");
                                    if(us!=null && us.getUserRole().equals(UserRole.CUST_SUP_ENG.toString()))
                                        out.print("<li><a href=\"cse_dashboard.jsp\">Dashboard</a></li>");
                                    if(us!=null && !us.getUserRole().equals(UserRole.CUSTOMER.toString()) && !us.getUserRole().equals(UserRole.CUST_SUP_ENG.toString()))
                                        out.print("<li><a href=\"reports.jsp\">Get reports</a></li>");
                                %>
                                <li class="divider"></li>
                                <li><a id="js-logout-lnk" href="#">Logout</a>
                                    <form id="js-logout-form" class="item-visible-false" action="logout" method="POST"></form>
                                </li>
                            </ul>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li><a href="auth.jsp"></span>User Cabinet</a></li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </div>
    </div>
</header>