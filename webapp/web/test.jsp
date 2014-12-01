<%--
  Created by IntelliJ IDEA.
  User: Vladmyr
  Date: 01.12.14
  Time: 18:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
    <script type="text/javascript" src="vendor/jquery/dist/jquery.min.js"></script>
    <script>
        $(document).ready(function(){
            console.log('jquery');

            $.ajax({
                type: 'GET',
                url: '/webapp_war_exploded/get-service',
                contentType: 'application/json',
                dataType: 'json',
                success: function(jqXHR){
                    console.log(jqXHR);
                },
                error: function(jqXHR, textStatus, errorThrow){
                    console.error("ERROR in ajax request", this, jqXHR, textStatus, errorThrow);
                }
            })
        });
    </script>
</head>
<body>

</body>
</html>