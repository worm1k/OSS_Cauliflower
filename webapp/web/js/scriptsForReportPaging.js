/**
 * Created by Eugene on 17.12.2014.
 */


$(document).ready(function() {
    var reportMethod = ($('#reportMethod')).val();
    var pageNumber = 0;


    $.get( "reportspaging?reportMethod="+reportMethod+"&page="+pageNumber, function( data ) {
        //data - JSON object to print
    });
});