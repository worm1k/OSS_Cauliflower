'use strict'

angular.module('Reports', [])
    .controller('ReportsController', function($scope){
        $scope.reportFormat = 'xls';
        $scope.startTime;
        $scope.endTime;

        $scope.update = function(){
            console.log($scope.reportFormat);
        }

        $scope.checkDates = function(arrThat){
            var isValid = true;
            var start = $(arrThat[0]).find('input').datepicker('getDate');
            var end = $(arrThat[1]).find('input').datepicker('getDate');
            var startMillis = 0;
            var endMillis = 0;

            if(start != null){
                startMillis = start.getTime();
                $(arrThat[0]).removeClass("has-error");
            }else{
                isValid = false;
                $(arrThat[0]).addClass("has-error");
                popover(arrThat[0], 2000);
            }

            if(isValid){
                if(end != null){
                    endMillis = end.getTime();
                    $(arrThat[1]).removeClass("has-error");
                }else{
                    isValid = false;
                    $(arrThat[1]).addClass("has-error");
                    popover(arrThat[1], 2000);
                }
            }

            if(isValid && startMillis > endMillis){
                isValid = false;
                popover(arrThat[1], 2000);
            }

            console.log($scope.startTime, $scope.endTime);

            return isValid;
        }

        $(document).ready(function(){
            var arrSelectors = ['#startDate', '#endDate']

            if($(arrSelectors[0]).length > 0) $('#startDate').find('input').datepicker();
            if($(arrSelectors[1]).length > 0) $('#endDate').find('input').datepicker();

            $('#js-get-report-0, #js-get-report-1').click(function(e){
                e.preventDefault();

                if($scope.checkDates(arrSelectors)){
                    $(this).parent().submit();
                }
            });
        });
    });