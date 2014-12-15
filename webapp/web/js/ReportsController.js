'use strict'

angular.module('Reports', [])
    .controller('ReportsController', function($scope){
        $scope.reportFormat = 'xls';
        $scope.startDate = 0;
        $scope.endDate = 0;

        $scope.update = function(){
            console.log($scope.reportFormat);
        }



        function checkDates(arrThat){
            var isValid = true;
            var start = $(arrThat[0]).datepicker('getDate');
            var end = $(arrThat[1]).datepicker('getDate');
            var startMilis = 0;
            var endMilis = 0;

            if(start != null){
                startMilis = start.getMilliseconds();
            }else{
                isValid = false;
            }

            if(isValid && end != null){
                endMilis = end.getMilliseconds();
            }else{
                isValid = false;
            }

            if(isValid && startMilis > endMilis){
                isValid = false;
            }
            console.log(start, end);
            return isValid;
        }

        $(document).ready(function(){
            if($("#startDate").length > 0) $("#startDate").datepicker();
            if($("#endDate").length > 0) $("#endDate").datepicker();

            $('#js-get-report-0, #js-get-report-1').click(function(e){
                e.preventDefault();
                checkDates(['#startDate', '#endDate']);
            });
        });
    });