/**
 * Created by Eugene on 17.12.2014.
 */

'use strict'

angular.module('ReportView', [])
    .controller('ReportViewController', function($scope){
        $scope.page = 1;
        $scope.json = [];
        console.log('ReportViewCOntroller');

        $scope.ajaxGetReportPagination = function(data, callback){
            $.ajax({
                type: 'GET',
                url: 'amountoflines',
                data: data,
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
            });
        }

        $scope.ajaxGetReport = function(data, callback){
            console.log('ajaxGetReport');
            $.ajax({
                type: 'GET',
                url: 'reportspaging',
                data: data,
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
            });
        }

        $scope.update = function(){

        }

        $scope.ajaxGetReport({reportMethod: reportMethod, page: $scope.page}, function(json){
            $scope.ajaxGetReportPagination({reportMethod: reportMethod}, function(pagination){
                $scope.$apply(function(){
                    $scope.json = json;
                });
            })
        });
    });