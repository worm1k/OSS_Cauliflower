<%--
  Created by IntelliJ IDEA.
  User: Артем
  Date: 02.12.2014
  Time: 18:09
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
      <div class="header_logo_name">Cauliflower</div>
</div></a>
<div class="header_login"><a href=""> Administrator </a></div>
</div>
<div class="clear"></div>

<div class="new_eng_cont">

  <form class="form-horizontal" action="register" method="post">
    <fieldset>

      <!-- Form Name -->
      <legend class="eng_legend">New Engineer</legend>
      </br>
      </br>

      <!-- Select Basic -->
      <div class="form-group">
        <label class="col-md-4 control-label" for="eng_role">Role</label>
        <div class="col-md-6">
          <select id="eng_role" name="userRoleId" class="form-control">
            <option value="5">Customer Support Engineer</option>
            <option value="3">Provisioning Engineer</option>
            <option value="4">Installation Engineer</option>
          </select>
        </div>
      </div>
      </br>

      <!-- Text input-->
      <div class="form-group">
        <label class="col-md-4 control-label" for="eng_first_name">First name</label>
        <div class="col-md-6">
          <input id="eng_first_name" name="name" type="text" placeholder="" class="form-control input-md" required="">

        </div>
      </div>
      </br>

      <!-- Text input-->
      <div class="form-group">
        <label class="col-md-4 control-label" for="eng_last_name">Last name</label>
        <div class="col-md-6">
          <input id="eng_last_name" name="surname" type="text" placeholder="" class="form-control input-md" required="">

        </div>
      </div>
      </br>

      <!-- Text input-->
      <div class="form-group">
        <label class="col-md-4 control-label" for="eng_email">Email</label>
        <div class="col-md-6">
          <input id="eng_email" name="email" type="email" placeholder="" class="form-control input-md" required="">

        </div>
      </div>
      </br>

      <!-- Text input-->
      <div class="form-group">
        <label class="col-md-4 control-label" for="eng_password">Password</label>
        <div class="col-md-6">
          <input id="eng_password" name="password" type="password" placeholder="" class="form-control input-md" required="">

        </div>
      </div>
      </br>

      <!-- Prepended text-->
      <div class="form-group">
        <label class="col-md-4 control-label" for="eng_phone">Phone number</label>
        <div class="col-md-6">
          <div class="input-group">
            <span class="input-group-addon">+380</span>
            <input id="eng_phone" name="phone" class="form-control" placeholder="" type="text" required="">
          </div>

        </div>
      </div>
      </br>
      </br>

      <div class="create_eng_button">
        <button type=submit class="btn btn-success">Create new Engineer</button>
      </div>

    </fieldset>
  </form>




</div>

</div>
</body>
</html>
