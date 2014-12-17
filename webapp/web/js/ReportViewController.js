/**
 * Created by Eugene on 17.12.2014.
 */

'use strict'

angular.module('ReportView', [])
    .controller('ReportViewController', function($scope){
        $scope.json = [];
        console.log('ReportViewCOntroller');

        $scope.ajaxGetReport = function(method, page, callback){
            console.log('ajaxGetReport');
            $.ajax({
                type: 'GET',
                url: 'reportspaging',
                data: {
                    page: page,
                    reportMethod: method
                },
                success: function(jqXHR){
                    console.log(jqXHR);
                    if(jqXHR && jqXHR[0]){
                        if (callback && typeof(callback) === "function") {
                            callback(jqXHR);
                        }
                    }
                },
                error: function(jqXHR, textStatus, errorThrown){
                    console.error("ERROR getting emails", this, jqXHR, textStatus, errorThrown);
                }
            })
        }

        $scope.ajaxGetReport(reportMethod, 1, function(data){
            $scope.$apply(function(){ $scope.json = data; });
        });
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

