/**
 * Created by Eugene on 04.12.2014.
 */


var isMailValid = 0;
var isMailPassword = 0;
var isMailName = 0;
var isMailPhone = 0;
var isMailSurname = 0;

function phoneNormalize(phone){
    return phone.replace(/[a-zA-Z-_()\s]/g, "");
}

$(document).ready(function() {

    $('#auth_reg_email').find('input').inputmask({ mask: "*{1,20}[.*{1,20}][.*{1,20}][.*{1,20}]@*{1,20}[.*{2,6}][.*{1,2}]", greedy: false });
    $('#auth_reg_Phone').find('input').inputmask('+38 (999) 999 99 99');

    $('#auth_reg_submit').click(function(e) {
        e.preventDefault();
        if(isMailValid && isMailPassword && isMailName && isMailPhone && isMailSurname){
            console.log(phoneNormalize($('#auth_reg_Phone').find('input').val()));
            //$( "#auth_reg_form" ).submit();
        }
    });

    <!--Validating fields on auth form-->
    $('#auth_reg_email, #auth_log_email').on('keyup', function() {
        var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;

        if(re.test($(this).find('input').val())){
            $(this).removeClass("has-error");
            isMailValid = 1;
        }else{
            $(this).addClass("has-error");
            isMailValid = 0;
        }
    });
    <!--Validating fields on auth form-->
    $('#auth_reg_password, #auth_log_password').on('keyup', function() {
        if($(this).find('input').val().length > 5){
            $(this).removeClass("has-error");
            isMailPassword = 1;
        }
        else{
            $(this).addClass("has-error");
            isMailPassword = 0;
        }
    });
    <!--Validating fields on auth form-->
    $('#auth_reg_Name, #auth_reg_Surname').on('keyup', function() {
        if($(this).find('input').val().length > 2){
            $(this).removeClass("has-error");
            isMailName = 1;
        }
        else{
            $(this).addClass("has-error");
            isMailName = 0;
        }
    });
    <!--Validating fields on auth form-->
    $('#auth_reg_Phone').on('keyup', function() {
        var input=$(this).find('input');
        var is_email = (input.val().length == 10 && $.isNumeric(input.val()));
        if(is_email){
            input.removeClass("has-error");
            isMailPhone = 1;
        }
        else{
            input.addClass("has-error");
            isMailPhone = 0;
        }
    });
});