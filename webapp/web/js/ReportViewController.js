/**
 * Created by Eugene on 17.12.2014.
 */

'use strict'

angular.module('ReportView', [])
    .controller('ReportViewController', function($scope){
        console.log('ReportViewCOntroller');

        $scope.ajaxGetReport = function(method, page){
            console.log('ajaxGetReport');
            $.ajax({
                type: 'GET',
                url: 'reportspaging',
                data: {
                    reportMethod: reportMethod
                },
                success: function(jqXHR){
                    console.log(jqXHR);
                },
                error: function(jqXHR, textStatus, errorThrown){
                    console.error("ERROR getting emails", this, jqXHR, textStatus, errorThrown);
                }
            })
        }

        $scope.ajaxGetReport(0, 0);
    });

//$(document).ready(function() {
//    var reportMethod = ($('#reportMethod')).val();
//    var pageNumber = 0;
//
//
//    $.get( "reportspaging?reportMethod="+reportMethod+"&page="+pageNumber, function( data ) {
//        //data - JSON object to print
//    });
//});

