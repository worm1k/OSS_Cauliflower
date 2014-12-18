/**
 * Created by Eugene on 17.12.2014.
 */

'use strict'

angular.module('ReportView', [])
    .controller('ReportViewController', function($scope){
        $scope.linesOnPage = 1;
        $scope.itemsLength = 0;
        $scope.page = 1;
        $scope.totalPages = 1;
        $scope.arrPage = [];
        $scope.json = [];
        $scope.isLoading = true;

        $scope.calcTotalPages = function(){
            $scope.totalPages = Math.floor($scope.itemsLength / $scope.linesOnPage);
            if($scope.itemsLength % $scope.linesOnPage != 0){
                $scope.totalPages++;
            }

            $scope.arrPage = [];
            for(var i = 0; i < $scope.totalPages; i++) $scope.arrPage.push(i+1);
        }

        $scope.ajaxGetReportPagination = function(data, callback){
            $.ajax({
                type: 'POST',
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

        $scope.update = function(page){
            $scope.$apply(function(){ $scope.page = page; });

            var data = {reportMethod: reportMethod, page: $scope.page};

            if(startDate != null && endDate != null){
                data.startDate = startDate;
                data.endDate = endDate;
            }

            $scope.ajaxGetReport(data, function(json){
                $scope.$apply(function(){ $scope.json = json.list; });
            })
        }

        $scope.init = function(){
            var data = {reportMethod: reportMethod, page: $scope.page};

            if(startDate != null && endDate != null){
                data.startDate = startDate;
                data.endDate = endDate;
            }

            $scope.ajaxGetReportPagination(data, function(length){
                console.log("length", length);
                $scope.ajaxGetReport(data, function(json){
                    $scope.$apply(function(){
                        $scope.json = json.list;
                        $scope.linesOnPage = json.linesOnPage;
                        $scope.itemsLength = length;
                        $scope.calcTotalPages();
                    });

                    console.log("json", json);

                    console.log('$scope.linesOnPage', $scope.linesOnPage);
                    console.log('$scope.itemsLength', $scope.itemsLength);
                    console.log('$scope.totalPages', $scope.totalPages);

                    $('#pagination').bootstrapPaginator({
                        bootstrapMajorVersion: 3,
                        currentPage: $scope.page,
                        totalPages: $scope.totalPages,
                        numberOfPages: 5,
                        itemTexts: function (type, page, current) {
                            switch (type) {
                                case "first":
                                    return "First";
                                case "prev":
                                    return "Previous";
                                case "next":
                                    return "Next";
                                case "last":
                                    return "Last";
                                case "page":
                                    return page;
                            }
                        },
                        itemContainerClass: function (type, page, current) {
                            return (page === current) ? "active" : "pointer-cursor";
                        },
                        onPageChanged: function(e, oldPage, newPage){
                            $scope.update(newPage);
                        }
                    });
                })
            });
        }

        $scope.init();
    });