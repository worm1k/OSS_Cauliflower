/**
 * Created by Eugene on 04.12.2014.
 */

function phoneNormalize(phone){
    return phone.replace(/[a-zA-Z-_()\s]/g, "");
}

function emailCheck(that){
    var isValid = false;
    var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;

    if(re.test($(that).find('input').val())){
        $(that).removeClass("has-error");
        isValid = true
    }else{
        $(that).addClass("has-error");
    }
    return isValid;
}

function passwordCheck(that){
    var isValid = false;
    if($(that).find('input').val().length > 5){
        $(that).removeClass("has-error");
        isValid = true;
    }else{
        $(that).addClass("has-error");
    }
    return isValid;
}

function nameCheck(that){
    var isValid = false;
    if($(that).find('input').val().length > 2){
        $(that).removeClass("has-error");
        isValid = true;
    }else{
        $(that).addClass("has-error");
    }
    return isValid;
}

function phoneCheck(that){
    var isValid = false;
    var phone = phoneNormalize($(that).find('input').val());
    if(phone.length == 13){
        $(that).removeClass("has-error");
        isValid = true;
    }else{
        $(that).addClass("has-error");
    }
    return isValid;
}

function popover(that, ms){
    $(that).find('input').popover('show');
    setTimeout(function(){
        $(that).find('input').popover('hide');
    },ms);
}

$(document).ready(function() {

    $('#auth_reg_email, #auth_log_email, #auth_block_email').find('input').inputmask('Regex', { regex: "[a-zA-Z0-9._%-]+@[a-zA-Z0-9-]+\\.[a-zA-Z]{2,4}" });
    $('#auth_reg_Phone').find('input').inputmask('+389999999999');

    $('#auth_reg_submit').click(function(e) {
        e.preventDefault();

        if(!emailCheck('#auth_reg_email')){
            popover('#auth_reg_email', 2000);
        }else if(!passwordCheck('#auth_reg_password')){
            popover('#auth_reg_password', 2000);
        }else if(!nameCheck('#auth_reg_Name')){
            popover('#auth_reg_Name', 2000);
        }else if(!nameCheck('#auth_reg_Surname')){
            popover('#auth_reg_Surname', 2000);
        }else if(!phoneCheck('#auth_reg_Phone')){
            popover('#auth_reg_Phone', 2000);
        }else{
            $('#auth_reg_Phone').find('input').val(phoneNormalize($('#auth_reg_Phone').find('input').val()));
            $('#auth_reg_form').submit();
        }
    });

    $('#auth_log_submit').click(function(e) {
        e.preventDefault();

        if(!emailCheck('#auth_log_email')){
            popover('#auth_log_email', 2000);
        }else if(!passwordCheck('#auth_log_password')){
            popover('#auth_log_password', 2000);
        }else{
            $('#auth_log_form').submit();
        }
    });

    $('#auth_block_submit').click(function(e) {
        e.preventDefault();

        if(!emailCheck('#auth_block_email')){
            popover('#auth_block_email', 2000);
        }else{
            $('#auth_block_form').submit();
        }
    });

    <!--Validating fields on auth form-->
    $('#auth_reg_email, #auth_log_email, #auth_block_email').on('keyup input', function() {
        emailCheck(this);
    });
    <!--Validating fields on auth form-->
    $('#auth_reg_password, #auth_log_password').on('keyup input', function() {
        passwordCheck(this);
    });
    <!--Validating fields on auth form-->
    $('#auth_reg_Name, #auth_reg_Surname').on('keyup input', function() {
        nameCheck(this);
    });
    <!--Validating fields on auth form-->
    $('#auth_reg_Phone').on('keyup input', function() {
        phoneCheck(this);
    });

});


$(function(){
    $("#startDate").datepicker();
    $("#endDate").datepicker();
});