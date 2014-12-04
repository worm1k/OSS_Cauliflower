/**
 * Created by Eugene on 04.12.2014.
 */




$(document).ready(function() {

    var isMailValid = 0;

    $( "#auth_reg_submit" ).click(function() {
        if(isMailValid)
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

});