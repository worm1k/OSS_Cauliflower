<%--
  Created by IntelliJ IDEA.
  User: Eugene
  Date: 03.12.2014
  Time: 8:57
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>

<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel="stylesheet" type="text/css" href="style/style.css" />
  <link href='http://fonts.googleapis.com/css?family=Lobster&subset=latin,cyrillic' rel='stylesheet' type='text/css'>
  <link href='http://fonts.googleapis.com/css?family=Droid+Sans' rel='stylesheet' type='text/css'>
  <link rel='stylesheet' href='style/bootstrap.min.css' type='text/css' media='all'>
  <script src="jquery/jquery-2.1.1.min.js" type="text/javascript"></script>
  <script src="jquery/bootstrap.min.js" type="text/javascript"></script>


</head>



<body>
<div class="main_wrapper">

  <div class="header">
    <a href="main.html"><div class="header_logo">
      <div class="header_logo_pic"><img src="pics/cauliflower.png"></div>
      <div class="header_logo_name">Cauliflower</a></div>
</div></a>
<div class="header_login"><a href=""> Administrator </a></div>
</div>
<div class="clear"></div>
<form action="reports" method="post">
<div class="report_title">Reports</div>
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
</form>

</div>
</body>
</html>
