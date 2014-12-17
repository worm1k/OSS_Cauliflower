/**
 * Created by Eugene on 17.12.2014.
 */

'use strict'

angular.module('ReportView', [])
    .controller('ReportViewController', function($scope){
        $scope.page = 1;
        $scope.json = [];

        $scope.ajaxGetReportPagination = function(data, callback){
            $.ajax({
                type: 'GET',
                url: 'amountoflines',
                data: data,
                success: function(jqXHR){
                    console.log(jqXHR);
                    if (callback && typeof(callback) === "function") {
                        callback(jqXHR);
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
                    if (callback && typeof(callback) === "function") {
                        callback(jqXHR);
                    }
                },
                error: function(jqXHR, textStatus, errorThrown){
                    console.error("ERROR getting emails", this, jqXHR, textStatus, errorThrown);
                }
            });
        }

        $scope.update = function(){

        }

        $scope.ajaxGetReportPagination({reportMethod: reportMethod}, function(length){
            console.log('LENGTH:', length);
            $scope.ajaxGetReport({reportMethod: reportMethod, page: $scope.page}, function(json){
                $scope.$apply(function(){
                    $scope.json = json;
                });
            })
        });
    });