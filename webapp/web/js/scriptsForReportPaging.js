/**
 * Created by Eugene on 17.12.2014.
 */


$(document).ready(function() {
    var reportMethod = ($('#reportMethod')).val();
    var pageNumber = ($('#page')).val();
    alert(reportMethod +" " + pageNumber);
    $.get( "reportspaging?reportMethod="+reportMethod+"&page="+pageNumber, function( data ) {
        $( ".result" ).html( data );
        alert( data );
    });
});