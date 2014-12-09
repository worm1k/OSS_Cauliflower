/**
 * Created by Slavko_O on 09.12.2014.
 */

/*KaspYar*/
function ajaxGetTasks(callback){
    $.ajax({
        type: 'POST',
        url: 'gettasks',
        dataType: 'json',
        success: function(data){
            console.log(data);
        },
        error: function(data, textStatus, errorThrown){
            console.error("ERROR getting tasks", this, data, textStatus, errorThrown);
        }
    })
}

$(document).ready(function(){
    ajaxGetTasks(function(){});
});
/*END KaspYar*/