'use strict'

angular.module('CSEDashboard', [])
    .controller('CSEDashboardController', function($scope){
        $scope.arrUser = [];

        console.log('CSE');

        function ajaxGetUsers(callback){
            $.ajax({
                type: 'POST',
                url: 'gettasks',
                dataType: 'json',
                success: function(jqXHR){
                    console.log(jqXHR);

                    if(jqXHR != null){
                        if (callback && typeof(callback) === "function") {
                            callback(jqXHR);
                        }
                    }
                },
                error: function(data, textStatus, errorThrown){
                    console.error("ERROR getting users", this, data, textStatus, errorThrown);
                }
            });
        }

        ajaxGetUsers(function(arrUser){
            $scope.$apply(function(){ $scope.arrUser = arrUser; });
        })
    });