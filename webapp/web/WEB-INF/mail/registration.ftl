<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>Email notification</title>
    <style type="text/css">
        body {
            padding-top: 0;
            padding-bottom: 0;
            padding-top: 0;
            padding-bottom: 0;
            margin: 0;
            width: 100%;
            -webkit-text-size-adjust: 100%;
            -ms-text-size-adjust: 100%;
            -webkit-font-smoothing: antialiased;
        }

        a {
            color: #382F2E;
        }

        p, h1, h2, ul, ol, li, div {
            margin: 0;
            padding: 0;
        }

        h1, h2 {
            font-weight: normal;
            background: transparent;
            border: none;
        }

        td, table {
            vertical-align: top;
        }

        td.middle {
            vertical-align: middle;
        }

        a.link1 {
            font-size: 13px;
            color: #27A1E5;
            line-height: 24px;
            text-decoration: none;
        }

        a {
            text-decoration: none;
        }

        .link2 {
            color: #ffffff;
            border-top: 10px solid #27A1E5;
            border-bottom: 10px solid #27A1E5;
            border-left: 18px solid #27A1E5;
            border-right: 18px solid #27A1E5;
            border-radius: 3px;
            -moz-border-radius: 3px;
            -webkit-border-radius: 3px;
            background: #27A1E5;
        }

        .link4 {
            color: #27A1E5;
            line-height: 24px;
        }

        h2, h1 {
            line-height: 20px;
        }

        p {
            font-size: 14px;
            line-height: 21px;
            color: #AAAAAA;
        }

        img {
            outline: none;
            text-decoration: none;
            width: auto;
            max-width: 100%;
            clear: both;
            display: block;
            float: none;
        }

        .footer {
            display: block;
            margin-top: 25px;
            padding: 10px 50px;
            font: 1em Helvetica;
            line-height: 18px;
            text-decoration: none;
        }
    </style>

</head>
<body bgcolor="#d1d3d4"
      style="padding-top: 0; padding-bottom: 0; padding-top: 0; padding-bottom: 0; background-repeat: repeat; width: 100% !important; -webkit-text-size-adjust: 100%;  -webkit-font-smoothing: antialiased;">
<table width="100%" border="0" cellspacing="0" cellpadding="0" class="tableContent bgBody" align="center"
       style='font-family:Helvetica, sans-serif;'>
    <!-- =============== START HEADER =============== -->

    <tr>
        <td align='center'>


            <table style="padding:20px 0 0 10px;" width="580" border="0" cellspacing="0" cellpadding="0" align="center">
                <tr>
                    <td style="padding: 0px 0 30px 0; left:10px;">
                        <div style=" position:absolute;display:inline-block;"><img src="images/logo.png"/></div>
                        <div align="center"
                             style="font-family: Tahoma,Arial, sans-serif; font-size: 36px; line-height: 45px;">
                            CAULIFLOWER
                        </div>
                    </td>
                </tr>


            </table>
        </td>
    </tr>
    <tr>
        <td>
            <hr style='height:5px;background:#27A1E5;border:none;'>
        </td>
    </tr>

    <!-- =============== END HEADER =============== -->
    <!-- =============== START BODY =============== -->
    <div>
        <table width="580" border="0" cellspacing="0" cellpadding="0" align="center">
            <tr>
                <td height='40'></td>
            </tr>
            <tr>
                <td width='200'>
                    <div>
                        <img src="images/maps.png" alt="logo" width='480' height='221'>
                    </div>
                </td>
                <td bgcolor="#27A1E5" width='380' valign='top'>
                    <table width="230" border="0" cellspacing="0" cellpadding="0" align="center" valign='top'>
                        <tr>
                            <td height='50'></td>
                        </tr>
                        <tr>
                            <td valign='top'>
                                <div style='font-family:Helvetica, Arial,serif;padding:0 0 25px 0;'>
                                    <h1 style='font-size:24px;font-weight:normal;color:#ffffff;line-height:19px;'>
                                        Welcome ! </h1>
                                </div>
                                <p style='font-size:13px;color:#cfeafa;line-height:19px;'>Choose your provider
                                    easily</p>
                            </td>

                        </tr>
                        <tr>
                            <td height='50'></td>
                        </tr>
                        <tr>
                            <td>
                                <div>
                                    <a href="http://194.44.143.139:10000/webappmy_war/order.jsp" target="blank"
                                       style='padding:15px;font-size:13px;text-decoration:none;background:#1C80C9;color:#E5F5FF;border-radius:6px;-moz-border-radius:6px;-webkit-border-radius:6px;'>Chose
                                        here</a>
                                </div>
                            </td>
                            </td>
                        </tr>
                        <tr>
                            <td height='15'></td>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>
    </div>
    <!-- =====  end maps image  ====== -->


    <div>
        <table width="580" border="0" cellspacing="0" cellpadding="0" align="center">
            <tr>
                <td height='40'></td>
            </tr>
            <tr>
                <td style='background:#F6F6F6; border-radius:6px;-moz-border-radius:6px;-webkit-border-radius:6px'>
                    <table width="480" border="0" cellspacing="0" cellpadding="0" align="center">
                        <tr>
                            <td height='25'></td>
                        </tr>
                        <tr>
                            <td>
                                <div class='contentEditable' style='text-align: center;'>
                                    <h2 style="font-size: 20px;">Dear ,<b>${to}</b></h2>
                                    <br>

                                    <p>Thank you for registration in CauliFlower !<br>Your password is :
                                    </p>
                                    <br><br>
                                    <a class='link2' style='color:#ffffff'>${body}</a>
                                    <br>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td height='24'></td>
                        </tr>
                    </table>
                </td>
            </tr>
            <tr>
                <td colspan="3">
                    <hr style='height:1px;background:#DDDDDD;border:none;'>
                </td>
            </tr>
        </table>
    </div>
    <!-- =====  end password   ====== -->
    <div>
        <table width="580" border="0" cellspacing="0" cellpadding="0" align="center">
            <tr>
                <td height='40' colspan="3"></td>
            </tr>
            <tr>
                <td width='150'>
                    <div class='contentEditableContainer contentImageEditable'>
                        <div class='contentEditable'>
                            <img src="images/side3.png" alt="side image" width='142' height='142'>
                        </div>
                    </div>
                </td>
                <td width='20'></td>
                <td width='410'>
                    <table width="410" cellpadding="0" cellspacing="0" align="center">
                        <tr>
                            <td height='15'></td>
                        </tr>
                        <tr>
                            <td>
                                <div class='contentEditableContainer contentTextEditable'>
                                    <div class='contentEditable' style='text-align: left;'>
                                        <h2 style='font-size:16px;'>Our Features :</h2>
                                        <br>

                                        <p>We provide user-friendly interface to select services, which are provided
                                            based on nearest provider location.</p>
                                        <br>
                                        <a href="http://194.44.143.139:10000/webappmy_war/home.jsp" target="blank"
                                           class='link4' style='color:#27A1E5;'>read more</a>
                                    </div>
                                </div>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
            <tr>
                <td height='40' colspan="3"></td>
            </tr>
            <tr>
                <td colspan="3">
                    <hr style='height:1px;background:#DDDDDD;border:none;'>
                </td>
            </tr>
        </table>
    </div>
    <!-- =====  1 feature   ====== -->
    <div>
        <table width="580" border="0" cellspacing="0" cellpadding="0" align="center">
            <tr>
                <td height='40' colspan="3"></td>
            </tr>
            <tr>
                <td width='410'>
                    <table width="410" cellpadding="0" cellspacing="0" align="center">
                        <tr>
                            <td height='15'></td>
                        </tr>
                        <tr>
                            <td>

                                <div style='text-align: left;'>
                                    <h2 style='font-size:16px;'>Our Features :</h2>
                                    <br>

                                    <p> We allow any user from internet to access its request placement
                                        functionality</p>
                                    <br>
                                    <a href="http://194.44.143.139:10000/webappmy_war/home.jsp" target="blank"
                                       class='link4' style='color:#27A1E5;'>read more</a>
                                </div>

                            </td>
                        </tr>
                    </table>
                </td>
                <td width='20'></td>
                <td width='150'>
                    <img src="images/side2.png" alt="side image" width='142' height='142'>
                </td>
            </tr>
            <tr>
                <td height='40' colspan="3"></td>
            </tr>
            <tr>
                <td colspan="3">
                    <hr style='height:1px;background:#DDDDDD;border:none;'>
                </td>
            </tr>
        </table>
    </div>
    <!-- =====  2 feature   ====== -->

    <!-- =============== END BODY =============== -->

    <!-- =============== START FOOTER =============== -->
    <div>
        <table bgcolor="#3180C4" width="580" border="0" cellspacing="0" cellpadding="0" align="center">

            <tr>
                <td width='90'></td>
                <td width='400' align='center'>


                    <a href="#" class="footer">
                        <font color="#ffffff"> Regards , </font>
                    </a>


                </td>
                <td width='90'></td>
            </tr>
            <tr>
                <td colspan="3" height='30'></td>
            </tr>
        </table>
    </div>

    <!-- =============== END FOOTER =============== -->
</table>
</body>
</html>