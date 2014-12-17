<%@ page import="com.naukma.cauliflower.info.CauliflowerInfo" %>
<%@ page import="com.naukma.cauliflower.entities.User" %>
<%@ page import="com.naukma.cauliflower.dao.UserRole" %>
<%--
  Created by IntelliJ IDEA.
  User: Vladmyr
  Date: 02.12.14
  Time: 23:51
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<footer class="footer">
    <div class="container">
        <ul class="list-inline">
            <li><a href="home.jsp">Home</a></li>
            <%
                User us = (User)request.getSession().getAttribute(CauliflowerInfo.USER_ATTRIBUTE);
                if(us==null || us.getUserRole().equals(UserRole.CUSTOMER.toString()))
                    out.print("<li><a href=\"order.jsp\">Order</a></li>");
            %>
        </ul>
    </div>
</footer>

<script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?key=AIzaSyCP497dnVu2vETnDTcY9ouPNOyCMhccFcs&libraries=places&language=en"></script>
<script type="text/javascript" src="./vendor/jquery/dist/jquery.min.js"></script>
<script type="text/javascript" src="vendor/jquery.ui/js/core.js"></script>
<script type="text/javascript" src="vendor/jquery.ui/js/widget.js"></script>
<script type="text/javascript" src="vendor/jquery.ui/js/position.js"></script>
<script type="text/javascript" src="vendor/jquery.ui/js/menu.js"></script>
<script type="text/javascript" src="vendor/jquery.ui/js/datepicker.js"></script>
<script type="text/javascript" src="vendor/jquery.ui/js/autocomplete.js"></script>
<script type="text/javascript" src="./vendor/jquery.inputmask/dist/jquery.inputmask.bundle.min.js"></script>
<script type="text/javascript" src="./vendor/gmap3/dist/gmap3.min.js"></script>
<script type="text/javascript" src="./vendor/google-infobox/google-infobox.js"></script>
<script type="text/javascript" src="./vendor/geocomplete/jquery.geocomplete.min.js"></script>
<script type="text/javascript" src="./vendor/bootstrap/dist/js/bootstrap.min.js"></script>
<script type="text/javascript" src="./vendor/bootstrap/dist/js/alert.js"></script>
<script type="text/javascript" src="./vendor/bootstrap-paginator/bootstrap-paginator.min.js"></script>
<script type="text/javascript" src="./vendor/angularjs/angular.min.js"></script>

<%--it works incorrect--%>
<%--<script src="http://code.jquery.com/jquery-1.10.2.js"></script>--%>
<%--<script src="http://code.jquery.com/ui/1.11.0/jquery-ui.js"></script>--%>
<%--<link rel="stylesheet" href="http://code.jquery.com/ui/1.11.0/themes/smoothness/jquery-ui.css">--%>

<!-- custom scripts -->
<script type="text/javascript" src="./js/MapDashboardController.js"></script>
<script type="text/javascript" src="./js/ReportsController.js"></script>
<script type="text/javascript" src="./js/InstallationEngineerDashboardController.js"></script>
<script type="text/javascript" src="./js/CustomerSupportEngineerController.js"></script>
<script type="text/javascript" src="./js/ReportViewController.js"></script>
<script type="text/javascript" src="./js/ServiceInstance.js"></script>
<script type="text/javascript" src="./js/ServiceOrder.js"></script>
<script type="text/javascript" src="./js/index.js"></script>
<script type="text/javascript" src="./js/EugeneIndex.js"></script>
<script type="text/javascript" src="./js/indexIhor.js"></script>