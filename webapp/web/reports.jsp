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

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" type="text/css" href="../../../../Downloads/OSS_Cauliflower-master/webapp/web/style/style.css" />
    <link href='http://fonts.googleapis.com/css?family=Lobster&subset=latin,cyrillic' rel='stylesheet' type='text/css'>
    <link href='http://fonts.googleapis.com/css?family=Droid+Sans' rel='stylesheet' type='text/css'>
    <link rel='stylesheet' href='../../../../Downloads/OSS_Cauliflower-master/webapp/web/style/bootstrap.min.css' type='text/css' media='all'>
    <script src="jquery/jquery-2.1.1.min.js" type="text/javascript"></script>
    <script src="jquery/bootstrap.min.js" type="text/javascript"></script>
    <script>
    </script>

    <script src="http://code.jquery.com/jquery-1.10.2.js"></script>
    <script src="http://code.jquery.com/ui/1.11.0/jquery-ui.js"></script>
    <link rel="stylesheet" href="http://code.jquery.com/ui/1.11.0/themes/smoothness/jquery-ui.css">

    <script>
        $(function(){
            $("#startDate").datepicker();
            $("#endDate").datepicker();
        });
    </script>

    <jsp:include page="head.jsp"/>

</head>



<body>
<jsp:include page="header.jsp"/>
<div class="main_wrapper">

    <div class="header">
        <a href="main.html"><div class="header_logo">
            <div class="header_logo_pic"><img src="../../../../Downloads/OSS_Cauliflower-master/webapp/web/pics/cauliflower.png"></div>
            <div class="header_logo_name">Cauliflower</div>
        </div></a>
        <div class="header_login"><a href=""> Administrator </a></div>
    </div>
    <div class="clear"></div>
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

</div>
</body>
</html>
