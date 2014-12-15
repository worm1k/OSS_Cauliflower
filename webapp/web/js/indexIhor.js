/**
 * Created by ihor on 15.12.2014.
 */

function getEmailsLike() {
    $(document).ready(function () {
            $("#emails").on('keyup', function () {
                    var emailLength = $(this).val().length;
                    var autocomplete = $("#autocomplete");
                    if (emailLength >= 2) {
                        var query = $(this).val();
                        console.log(query);
                        $.ajax({
                            type: 'POST',
                            url: 'autocomplete',
                            dataType: 'json',
                            data: {
                                query: query
                            },
                            success: function (msg) {
                                if (msg != null) {
                                    var html = "";
                                    for (var i = 0; i < msg.length; i++) {
                                        html += '<li>' + msg[i] + '</li><br>';
                                      //todo
                                    }
                                    autocomplete.html(html);
                                    autocomplete.show();
                                }
                                else {
                                    console.log('response null');
                                }
                            },
                            error: function () {
                                //your error code
                            }
                        });
                    } else {
                        autocomplete.hide();
                        autocomplete.html("");
                    }
                }
            );

        }
    );
}

