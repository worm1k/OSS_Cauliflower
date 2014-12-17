<%--
  Created by IntelliJ IDEA.
  User: Eugene
  Date: 17.12.2014
  Time: 0:44
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Reports</title>
  <jsp:include page="head.jsp"/>
</head>
<%
  String reportMethod = request.getParameter("reportMethod");
%>
<body>
<jsp:include page="header.jsp"/>
<input type="hidden" id="reportMethod" value= "<%=reportMethod%>" >
<div id = "place_for_info_ReportPaging"></div>
<jsp:include page="footer.jsp"/>
<script type="text/javascript" src="./js/scriptsForReportPaging.js"></script>
</body>
</html>
