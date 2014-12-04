/**
 * Created by Eugene on 04.12.2014.
 */




$(document).ready(function() {

    var isMailValid = 0;
    var isMailPassword = 0;

    $( "#auth_reg_submit" ).click(function() {
        if(isMailValid && isMailPassword)
            $( "#auth_reg_form" ).submit();
        else
            alert("Smth wrong with fields");
    });

    <!--Validating fields on auth form-->
    $('#auth_reg_email').on('input', function() {
        var input=$(this);
        var re = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/;
        var is_email=re.test(input.val());
        if(is_email){
            input.removeClass("invalid");
            isMailValid = 1;
        }
        else{
            input.addClass("invalid");
            isMailValid = 0;
        }
    });
    <!--Validating fields on auth form-->
    $('#auth_reg_password').on('input', function() {
        var input=$(this);
        var is_email=input.val().length > 6;
        if(is_email){
            input.removeClass("invalid");
            isMailPassword = 1;
        }
        else{
            input.addClass("invalid");
            isMailPassword = 0;
        }
    });

});