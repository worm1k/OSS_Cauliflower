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

//    String error = (String) request.getSession().getAttribute(CauliflowerInfo.ERROR_ATTRIBUTE);
//    request.getSession().removeAttribute(CauliflowerInfo.ERROR_ATTRIBUTE);
%>

<!DOCTYPE html>
<html lang="en" ng-app="Reports">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <jsp:include page="head.jsp"/>
</head>
<body ng-controller="ReportsController">
    <jsp:include page="header.jsp"/>
    <div class="container">
        <h1 class="txt-center txt-bold">CauliFlower</h1>

        <h2 class="txt-center">Reports</h2>

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

        <div class="row ng-cloak">
        <div class="col-xs-12">
            <table class="table table-striped">
                <tbody>
                <tr>
                    <td>
                        <ul class="list-inline margin-zero">
                            <li><b>Choose download format:</b></li>
                            <li><input type="radio" ng-model="reportFormat" ng-click="update()" name="extension" id="optionsRadios1" value="xls"> <b>XLS</b></li>
                            <li><input type="radio" ng-model="reportFormat" ng-click="update()" name="extension" id="optionsRadios2" value="csv"> <b>CSV</b></li>
                        </ul>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
        <div class="col-xs-6">
                <table class="table table-striped">
                    <tbody>
                    <c:if test="${sessionScope.user != null && (sessionScope.user.userRole == \"ADMINISTRATOR\" || sessionScope.user.userRole == \"INSTALLATION_ENG\")}">
                    <tr>
                        <div class="report_block">
                            <td>
                                <div class="report_text">Report about all installed devices</div>
                            </td>
                            <td>
                                <form class="form-inline inline" action="" method="post">
                                    <input type="hidden" name="reportMethod" value="Devices">
                                    <button type="submit" class="btn btn-xs btn-default">View</button>
                                </form>
                                <form class="form-inline inline" action="reports" method="post">
                                    <input type="hidden" name="reportMethod" value="Devices">
                                    <input type="hidden" name="extension" value="xls">
                                    <button type="submit" class="btn btn-xs btn-primary">Download</button>
                                </form>

                            </td>
                        </div>
                        <div class="clear"></div>
                    </tr>
                    <tr>
                        <div class="report_block">
                            <td>
                                <div class="report_text"> Report about current circuits</div>
                            </td>
                            <td>
                                <form class="form-inline inline" action="" method="post">
                                    <input type="hidden" name="reportMethod" value="Circuits">
                                    <button type="submit" class="btn btn-xs btn-default">View</button>
                                </form>
                                <form class="form-inline inline" action="reports" method="post">
                                    <input type="hidden" name="reportMethod" value="Circuits">
                                    <input type="hidden" name="extension" value="xls">
                                    <button type="submit" class="btn btn-xs btn-primary">Download</button>
                                </form>

                            </td>
                        </div>
                        <div class="clear"></div>
                    </tr>
                    <tr>
                        <div class="report_block">
                            <td>
                                <div class="report_text"> Report about all available cables</div>
                            </td>
                            <td>
                                <form class="form-inline inline" action="" method="post">
                                    <input type="hidden" name="reportMethod" value="Cables">
                                    <button type="submit" class="btn btn-xs btn-default">View</button>
                                </form>
                                <form class="form-inline inline" action="reports" method="post">
                                    <input type="hidden" name="reportMethod" value="Cables">
                                    <input type="hidden" name="extension" value="xls">
                                    <button type="submit" class="btn btn-xs btn-primary">Download</button>
                                </form>

                            </td>
                        </div>
                        <div class="clear"></div>
                    </tr>
                    <tr>
                        <div class="report_block">
                            <td>
                                <div class="report_text"> Report about all existing ports</div>
                            </td>
                            <td>
                                <form class="form-inline inline" action="" method="post">
                                    <input type="hidden" name="reportMethod" value="Ports">
                                    <button type="submit" class="btn btn-xs btn-default">View</button>
                                </form>
                                <form class="form-inline inline" action="reports" method="post">
                                    <input type="hidden" name="reportMethod" value="Ports">
                                    <input type="hidden" name="extension" value="xls">
                                    <button type="submit" class="btn btn-xs btn-primary">Download</button>
                                </form>

                            </td>
                        </div>
                        <div class="clear"></div>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.user != null && (sessionScope.user.userRole == \"ADMINISTRATOR\"
                                || sessionScope.user.userRole == \"PROVISIONING_ENG\" || sessionScope.user.userRole == \"CUST_SUP_ENG\")}">
                        <tr>
                            <div class="report_block">
                                <td>
                                    <div class="report_text"> Impact Propagation Tree</div>
                                </td>
                                <td>
                                    <form class="form-inline inline" action="" method="post">
                                        <input type="hidden" name="reportMethod" value="Tree">
                                        <button type="submit" class="btn btn-xs btn-default">View</button>
                                    </form>
                                    <form class="form-inline inline" action="reports" method="post">
                                        <input type="hidden" name="reportMethod" value="Tree">
                                        <input type="hidden" name="extension" value="xls">
                                        <button type="submit" class="btn btn-xs btn-primary">Download</button>
                                    </form>

                                </td>
                            </div>
                        </tr>
                    </c:if>
                    <c:if test="${sessionScope.user != null && (sessionScope.user.userRole == \"ADMINISTRATOR\" || sessionScope.user.userRole == \"PROVISIONING_ENG\")}">
                    <tr>
                        <div class="report_block">
                            <td>
                                <div class="report_text"> Report about most profitable routers</div>
                            </td>
                            <td>
                                <form class="form-inline inline" action="" method="post">
                                    <input type="hidden" name="reportMethod" value="Profitable">
                                    <button type="submit" class="btn btn-xs btn-default">View</button>
                                </form>
                                <form class="form-inline inline" action="reports" method="post">
                                    <input type="hidden" name="reportMethod" value="Profitable">
                                    <input type="hidden" name="extension" value="xls">
                                    <button type="submit" class="btn btn-xs btn-primary">Download</button>
                                </form>

                            </td>
                        </div>
                        <div class="clear"></div>
                    </tr>
                    <tr>
                        <div class="report_block">
                            <td>
                                <div class="report_text"> Report about utilization and capacity</div>
                            </td>
                            <td>
                                <form class="form-inline inline" action="" method="post">
                                    <input type="hidden" name="reportMethod" value="utilizationAndCapacity">
                                    <button type="submit" class="btn btn-xs btn-default">View</button>
                                </form>
                                <form class="form-inline inline" action="reports" method="post">
                                    <input type="hidden" name="reportMethod" value="utilizationAndCapacity">
                                    <input type="hidden" name="extension" value="xls">
                                    <button type="submit" class="btn btn-xs btn-primary">Download</button>
                                </form>

                            </td>
                        </div>
                        <div class="clear"></div>
                    </tr>
                    <tr>
                        <div class="report_block">
                            <td>
                                <div class="report_text"> Report about profitability</div>
                            </td>
                            <td>
                                <form class="form-inline inline" action="" method="post">
                                    <input type="hidden" name="reportMethod" value="Profitability">
                                    <button type="submit" class="btn btn-xs btn-default">View</button>
                                </form>
                                <form class="form-inline inline" action="reports" method="post">
                                    <input type="hidden" name="reportMethod" value="Profitability">
                                    <input type="hidden" name="extension" value="xls">
                                    <button type="submit" class="btn btn-xs btn-primary">Download</button>
                                </form>

                            </td>
                        </div>
                    </tr>

                    </tbody>
                </table>
        </div>

        <div class="col-xs-6 border-left">
                    <table class="table table-striped">
                        <tbody>
                            <tr><td>Start date: </td><td><input type="text" class="input-pickdate" id="startDate" name="startDate" readonly></td></tr>
                            <tr><td>End date: </td><td><input type="text" class="input-pickdate" id="endDate" name="endDate" readonly></td></tr>
                        <tr>
                            <div class="report_block">
                                <td>
                                    <div class="report_text">Report about new orders per period</div>
                                </td>
                                <td>
                                    <form class="form-inline inline" action="" method="post">
                                        <input type="hidden" name="reportMethod" value="New">
                                        <button type="submit" class="btn btn-xs btn-default">View</button>
                                    </form>
                                    <form class="form-inline inline" action="reports" method="post">
                                        <input type="hidden" name="reportMethod" value="New">
                                        <input type="hidden" name="extension" value="xls">
                                        <button type="submit" id="js-get-report-0" class="btn btn-xs btn-primary">Download</button>
                                    </form>

                                </td>
                            </div>
                            <div class="clear"></div>
                        </tr>
                        <tr>
                            <div class="report_block">
                                <td>
                                    <div class="report_text"> Report about disconnect orders per period</div>
                                </td>
                                <td>
                                    <form class="form-inline inline" action="" method="post">
                                        <input type="hidden" name="reportMethod" value="Disconnect">
                                        <button type="submit" class="btn btn-xs btn-default">View</button>
                                    </form>
                                    <form class="form-inline inline" action="reports" method="post">
                                        <input type="hidden" name="reportMethod" value="Disconnect">
                                        <input type="hidden" name="extension" value="xls">
                                        <button type="submit" id="js-get-report-1" class="btn btn-xs btn-primary">Download</button>
                                    </form>

                                </td>
                            </div>
                        </tr>

                        </c:if>
                        </tbody>
                    </table>
        </div>
        </div>
    </div>
    <jsp:include page="footer.jsp"/>
</body>
</html>
