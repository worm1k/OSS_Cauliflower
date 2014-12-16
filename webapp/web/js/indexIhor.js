/**
 * Created by ihor on 15.12.2014.
 */

$(document).ready(function(){
    $('#js-email-to-block').on('keyup', function(){
        var that = this;

        if($(that).val().length > 1){
            var query = $(this).val();
            $.ajax({
                type: 'POST',
                url: 'autocomplete',
                dataType: 'json',
                data: {
                    query: query
                },
                success: function (email) {
                    if (email != null && typeof email != 'undefined') {
                        if(email.length > 0){
                            $(that).autocomplete({
                                minLengthType: 2,
                                autoFocus: true,
                                source: email,
                                close: function(){
                                    emailCheck('#auth_block_email');
                                }
                            });
                        }
                    }
                },
                error: function(jqXHR, textStatus, errorThrown){
                    console.error("ERROR getting emails", this, jqXHR, textStatus, errorThrown);
                }
            });
        }
    });
});

