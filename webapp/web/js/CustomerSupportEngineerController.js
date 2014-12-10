'use strict'

angular.module('CSEDashboard', [])
    .controller('CSEDashboardController', function($scope){
        $scope.arrUser = [];

        console.log('CSE');

        function ajxGetUsers(callback){
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

        ajaxGetUsers(function(){
            $scope.$apply(function(){

            })
        })
    });