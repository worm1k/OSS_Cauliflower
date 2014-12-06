<%@ page import="com.naukma.cauliflower.entities.Task" %>
<%@ page import="java.util.List" %>
<%@ page import="com.naukma.cauliflower.info.CauliflowerInfo" %>
<%--
  Created by IntelliJ IDEA.
  User: Артем
  Date: 06.12.2014
  Time: 22:01
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
  /*System.out.println("dashboard");
  List<Task> tasks=(List<Task>)request.getSession().getAttribute(CauliflowerInfo.TASKS_PARAM);
  //System.out.println(tasks);
//  if(tasks==null) request.getRequestDispatcher("gettasks").forward(request, response);
  request.getRequestDispatcher("/gettasks").forward(request, response);
  //*/
%>
<!DOCTYPE html>
<html lang="en" ng-app="NgApp">
<head>
  <title></title>
  <jsp:include page="head.jsp"/>
</head>
<body>
<jsp:include page="header.jsp"/>

<div class="container">
  <h1 class="txt-center txt-bold">CauliFlower OSS</h1>
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

  <div class="col-xs-6">
    <h2 class="txt-center">New tasks</h2>
    <form class="form-proc_tasks" action="" method="post" id="form-ent_tasks" autocomplete="off">
      <table class="table table-condensed">
        <tr>
          <td>
            <input type="checkbox" name="myTextEditBox" value="checked1" />
          </td>
          <td>
            First task
          </td>
        </tr>
        <tr>
          <td>
            <input type="checkbox" name="myTextEditBox" value="checked2" />
          </td>
          <td>
            First task
          </td>
        </tr>
        <tr>
          <td>
            <input type="checkbox" name="myTextEditBox" value="checked3" />
          </td>
          <td>
            First task
          </td>
        </tr>
        <tr>
          <td>
            <input type="checkbox" name="myTextEditBox" value="checked4" />
          </td>
          <td>
            First task
          </td>
        </tr>
        <tr>
          <td>
            <input type="checkbox" name="myTextEditBox" value="checked5" />
          </td>
          <td>
            First task
          </td>
        </tr>
        <tr>
          <td>
            <input type="checkbox" name="myTextEditBox" value="checked6" />
          </td>
          <td>
            First task
          </td>
        </tr>
      </table>
      <button class="btn-signin btn btn-lg btn-primary btn-block" id="ent_to_proc_submit" type="submit">Processing</button>
    </form>
  </div>
  <div class="col-xs-6 border-left">
    <h2 class="txt-center">Processing tasks</h2>
    <form class="form-proc_tasks" action="" method="post" id="form-proc_tasks" autocomplete="off">
      <table class="table table-condensed">
        <tr>
          <td>
            <input type="checkbox" name="myTextEditBox" value="checked1" />
          </td>
          <td>
            First task
          </td>
        </tr>
        <tr>
          <td>
            <input type="checkbox" name="myTextEditBox" value="checked2" />
          </td>
          <td>
            First task
          </td>
        </tr>
        <tr>
          <td>
            <input type="checkbox" name="myTextEditBox" value="checked3" />
          </td>
          <td>
            First task
          </td>
        </tr>
        <tr>
          <td>
            <input type="checkbox" name="myTextEditBox" value="checked4" />
          </td>
          <td>
            First task
          </td>
        </tr>
        <tr>
          <td>
            <input type="checkbox" name="myTextEditBox" value="checked5" />
          </td>
          <td>
            First task
          </td>
        </tr>
        <tr>
          <td>
            <input type="checkbox" name="myTextEditBox" value="checked6" />
          </td>
          <td>
            First task
          </td>
        </tr>
        <tr>
          <td>
            <input type="checkbox" name="myTextEditBox" value="checked6" />
          </td>
          <td>
            First task
          </td>
        </tr>
        <tr>
          <td>
            <input type="checkbox" name="myTextEditBox" value="checked6" />
          </td>
          <td>
            First task
          </td>
        </tr>
        <tr>
          <td>
            <input type="checkbox" name="myTextEditBox" value="checked6" />
          </td>
          <td>
            First task
          </td>
        </tr>
      </table>
      <button class="btn-signin btn btn-lg btn-primary btn-block" id="proc_to_comp_submit" type="submit">Complete</button>
      <button class="btn-signin btn btn-lg btn-primary btn-block" id="proc_to_ent_submit" type="submit">Entering</button>
    </form>
  </div>
  <div>
    <b>
      <button class="btn-signin btn btn-lg btn-primary btn-block" id="get_order" type="submit">Order</button>
    </b>
  </div>
</div>

<jsp:include page="footer.jsp"/>
</body>
</html>
