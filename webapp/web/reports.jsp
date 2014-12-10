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
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
    User user = (User) request.getSession().getAttribute(CauliflowerInfo.USER_ATTRIBUTE);
    if (user == null || (user != null && user.getUserRole().equals(UserRole.CUSTOMER.toString())))
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

    <h2 class="txt-center">Reports</h2>
    <%--Server message shows here--%>
    <c:if test="${sessionScope.error ne null && not empty sessionScope.error}">
    <div class="alert alert-danger alert-dismissible" role="alert">
        <button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">×</span><span class="sr-only">Close</span>
        </button>
        <p><b>Server message:</b> ${sessionScope.error}</p>
    </div>
    </c:if>

    <c:if test="${sessionScope.ok ne null && not empty sessionScope.ok}">
    <div class="alert alert-success alert-dismissible" role="alert">
        <button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">×</span><span class="sr-only">Close</span>
        </button>
        <p><b>Server message:</b> ${sessionScope.ok}</p>
    </div>
    </c:if>

    <form action="reports" method="post">
        <div class="report_title">Reports</div>
        <input type="radio" name="extension" value="xls" checked> XLS
        <input type="radio" name="extension" value="csv"> CSV
        <div class="report_block">
            <div class="report_text">Get report about all installed devises</div>
            <div class="report_button"><input type="submit" class="btn btn-success" name="reportMethod" value="Devises"/></div>
        </div>
        <div class="clear"></div>
        <div class="report_block">
            <div class="report_text">Get report about current circuits</div>
            <div class="report_button"><input type="submit" class="btn btn-success" name="reportMethod" value="Circuits"/></div>
        </div>
        <div class="clear"></div>
        <div class="report_block">
            <div class="report_text">Get report about all available cables</div>
            <div class="report_button"><input type="submit" class="btn btn-success" name="reportMethod" value="Cables"/></div>
        </div>
        <div class="clear"></div>
        <div class="report_block">
            <div class="report_text">Get report about all existing ports</div>
            <div class="report_button"><input type="submit" class="btn btn-success" name="reportMethod" value="Ports"/></div>
        </div>
        <div class="clear"></div>
        <div class="report_block">
            <div class="report_text">Get report about most profitable routers/div>
                <div class="report_button"><input type="submit" class="btn btn-success" name="reportMethod" value="Profitable"/></div>
            </div>
            <div class="clear"></div>
            <div class="report_block">
                <div class="report_text">Get report about utilization and capacity</div>
                <div class="report_button"><input type="submit" class="btn btn-success" name="reportMethod" value="utilizationAndCapacity"/></div>
            </div>
            <div class="clear"></div>
            <div class="report_block">
                <div class="report_text">Get report about profitability</div>
                <div class="report_button"><input type="submit" class="btn btn-success" name="reportMethod" value="Profitability"/></div>
            </div>
            <p>start date: <input type="text" id="startDate" name="startDate"></p>
            <p>end date: <input type="text" id="endDate" name="endDate"></p>
            <div class="clear"></div>
            <div class="report_block">
                <div class="report_text">Get report about new orders per period</div>
                <div class="report_button"><input type="submit" class="btn btn-success" name="reportMethod" value="New"/></div>
            </div>
            <div class="clear"></div>
            <div class="report_block">
                <div class="report_text">Get report about disconnect orders per period</div>
                <div class="report_button"><input type="submit" class="btn btn-success" name="reportMethod" value="Disconnect"/></div>
            </div>
        </div>
    </form>


    <jsp:include page="footer.jsp"/>
    <script type="text/javascript" src="./js/indexSlavko.js"></script>
</body>
</html>
