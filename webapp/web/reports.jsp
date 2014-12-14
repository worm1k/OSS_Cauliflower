<%--
  Created by IntelliJ IDEA.
  User: Slavko_O
  Date: 10.12.2014
  Time: 20:48
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.naukma.cauliflower.entities.User" %>
<%@ page import="com.naukma.cauliflower.info.CauliflowerInfo" %>
<%@ page import="com.naukma.cauliflower.dao.UserRole" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%

    String error = (String) request.getSession().getAttribute(CauliflowerInfo.ERROR_ATTRIBUTE);
    request.getSession().removeAttribute(CauliflowerInfo.ERROR_ATTRIBUTE);
%>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <jsp:include page="head.jsp"/>
</head>


<body>
<jsp:include page="header.jsp"/>
<div class="container">
    <h1 class="txt-center txt-bold"> CauliFlower</h1>

    <h2 class="txt-center">Reports <%=error%></h2>

    <div class="clear"></div>
    <div class="col-xs-6">
    <form action="reports" method="post">

        <div class="panel panel-primary">
            <div class="panel-heading">Choose one
                <div class="radio-inline">
                    <label>
                        <input type="radio" name="extension" id="optionsRadios1" value="xls" checked>
                        XLS
                    </label>
                </div>
                <div class="radio-inline">
                    <label>
                        <input type="radio" name="extension" id="optionsRadios2" value="csv">
                        CSV
                    </label>
                </div>
            </div>


            <table class="table">

                <tr>
                    <div class="report_block">

                        <td>
                            <div class="report_text">1) Report about all installed devices</div>
                        </td>
                        <td>
                            <div class="report_button"><input type="submit" class="btn btn-primary" name="reportMethod"
                                                              value="Devices"/></div>
                        </td>
                    </div>
                    <div class="clear"></div>
                </tr>
                <tr>
                    <div class="report_block">
                        <td>
                            <div class="report_text">2) Report about current circuits</div>
                        </td>
                        <td>
                            <div class="report_button"><input type="submit" class="btn btn-primary" name="reportMethod"
                                                              value="Circuits"/></div>
                        </td>
                    </div>
                    <div class="clear"></div>
                </tr>
                <tr>
                    <div class="report_block">
                        <td>
                            <div class="report_text">3) Report about all available cables</div>
                        </td>
                        <td></t>
                            <div class="report_button"><input type="submit" class="btn btn-primary" name="reportMethod"
                                                              value="Cables"/>
                            </div>
                        </td>
                    </div>
                    <div class="clear"></div>
                </tr>
                <tr>

                    <div class="report_block">
                        <td>
                            <div class="report_text">4) Report about all existing ports</div>
                        </td>
                        <td></t>
                            <div class="report_button"><input type="submit" class="btn btn-primary" name="reportMethod"
                                                              value="Ports"/>
                            </div>
                        </td>
                    </div>
                    <div class="clear"></div>
                </tr>
                <tr>
                    <div class="report_block">
                        <td>
                            <div class="report_text">5) Report about most profitable routers</div>
                        </td>
                        <td>
                            <div class="report_button"><input type="submit" class="btn btn-primary" name="reportMethod"
                                                              value="Profitable"/></div>
                        </td>
                    </div>
                    <div class="clear"></div>
                </tr>
                <tr>
                    <div class="report_block">
                        <td>
                            <div class="report_text">6) Report about utilization and capacity</div>
                        </td>
                        <td>
                            <div class="report_button"><input type="submit" class="btn btn-primary" name="reportMethod"
                                                              value="utilizationAndCapacity"/></div>
                        </td>
                    </div>
                    <div class="clear"></div>
                </tr>
                <tr>
                    <div class="report_block">
                        <td>
                            <div class="report_text">7) Report about profitability</div>
                        </td>
                        <td>
                            <div class="report_button"><input type="submit" class="btn btn-primary" name="reportMethod"
                                                              value="Profitability"/></div>
                        </td>
                    </div>

            </table>
        </div>
    </form>
        </div>

    <div class="col-xs-6 border-left">
        <form action="reports" method="post">

            <div class="panel panel-primary">
                <div class="panel-heading">Choose one
                    <div class="radio-inline">
                        <label>
                            <input type="radio" name="extension" id="optionsRadios3" value="xls" checked>
                            XLS
                        </label>
                    </div>
                    <div class="radio-inline">
                        <label>
                            <input type="radio" name="extension" id="optionsRadios4" value="csv">
                            CSV
                        </label>
                    </div>
                </div>


                <table class="table">

    <tr>
        <td>Start date: </td><td><input type="text" id="startDate" name="startDate"></td></tr>

            <tr><td>End date: </td><td><input type="text" id="endDate" name="endDate"></td></tr>

        <div class="clear"></div>
    </tr>
    </tr>
    <tr>
        <div class="report_block">
            <td>
                <div class="report_text">1) Report about new orders per period</div>
            </td>
            <td>
                <div class="report_button"><input type="submit" class="btn btn-primary" name="reportMethod"
                                                  value="New"/></div>
            </td>
        </div>
        <div class="clear"></div>
    </tr>
    <tr>
        <div class="report_block">
            <td>
                <div class="report_text">2) Report about disconnect orders per period</div>
            </td>
            <td>
                <div class="report_button"><input type="submit" class="btn btn-primary" name="reportMethod"
                                                  value="Disconnect"/></div>
            </td>
        </div>
    </tr>
                </table>
            </div>
        </form></div>

</div>
<jsp:include page="footer.jsp"/>
<script type="text/javascript">
    $(document).ready(function(){
        if($("#startDate").length > 0) $("#startDate").datepicker();
        if($("#endDate").length > 0) $("#endDate").datepicker();
    });
</script>
</body>
</html>
