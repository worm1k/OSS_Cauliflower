'use strict'

angular.module('IEDashboard', [])
    .controller('IEDashboardController', function($scope){

        $scope.arrTaskFree = null;
        $scope.arrTaskSubscribed = null;
        $scope.arrTaskCompleted = null;

        function setTasksByStatus(arrTask){
            for(var i = 0; i < arrTask.length; i++){
                if(arrTask[i].taskStatus === 'FREE'){
                    $scope.$apply(function(){ $scope.arrTaskFree.push(arrTask[i]) });
                }else if(arrTask[i].taskStatus === 'PROCESSING'){
                    $scope.$apply(function(){ $scope.arrTaskSubscribed.push(arrTask[i]) });
                }else if(arrTask[i].taskStatus === 'COMPLETED'){
                    $scope.$apply(function(){ $scope.arrTaskCompleted.push(arrTask[i]) });
                }
            }
        }

        function ajaxGetTasks(callback){
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
                    console.error("ERROR getting tasks", this, data, textStatus, errorThrown);
                }
            });
        }

        ajaxGetTasks(function(jqXHR){
            $scope.arrTaskFree = [];
            $scope.arrTaskSubscribed = [];
            $scope.arrTaskCompleted = [];

            setTasksByStatus(jqXHR);
        });
    })
    .directive('showHide', function () {
        return {
            link: function (scope, element, attributes, controller) {
                scope.$watch(attributes.showHide, function (v) {
                    if (v) {
                        element.show();
                    } else {
                        element.hide();
                    }
                });
            }
        };
    });