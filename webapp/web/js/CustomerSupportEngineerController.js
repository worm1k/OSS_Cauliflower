'use strict'

angular.module('CSEDashboard', [])
    .controller('CSEDashboardController', function($scope){
        $scope.arrUser = [];
        $scope.customerUserId = 0;

        $scope.getCustomerUserInfo = function(id){
            console.log(id);
        }

        console.log('CSE');

        function ajaxGetUsers(callback){
            $.ajax({
                type: 'GET',
                url: 'getallcustomers',
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
    })
    .controller('CSEDashboardUserController',function($scope){
        $scope.customerUserId = customerUserId;
        $scope.customerUser = null;

        $scope.arrServiceInstance = [];
        $scope.arrProviderLocation = [];
        $scope.arrService = [];

        $scope.serviceInstance;
        $scope.service;

        $scope.isControllerInit = false;

        $scope.update = function(){
            updateGeneralInfo(false);
        }

        function updateGeneralInfo(isInit){
            if($scope.arrServiceInstance.length > 0){
                console.log('serviceInstance', $scope.serviceInstance);
                console.log('providerLocation arrServices', $scope.serviceInstance.providerLocation.arrService);

                var i = 0;
                var isFound = false;

                //find service instance service
                while(i < $scope.serviceInstance.providerLocation.arrService.length && !isFound){
                    if($scope.serviceInstance.serviceId == $scope.serviceInstance.providerLocation.arrService[i].id){
                        if(isInit){
                            $scope.$apply(function(){
                                $scope.service = $scope.serviceInstance.providerLocation.arrService[i];
                            });
                        }else{
                            $scope.service = $scope.serviceInstance.providerLocation.arrService[i];
                        }
                        isFound = true;
                    }
                    i++;
                }
            }else{
                console.log('empty');
            }
        }

        function passwordCheck(that){
            var isValid = false;
            if($(that).find('input').val().length > 5){
                $(that).removeClass("has-error");
                isValid = true;
            }else{
                $(that).addClass("has-error");
            }
            return isValid;
        }

        function popover(that, ms){
            $(that).find('input').popover('show');
            $(that).find('input').focus();
            setTimeout(function(){
                $(that).find('input').popover('hide');
            },ms);
        }

        function ajaxGetDashboardData(userId, callback){
            $.ajax({
                type: 'GET',
                url: 'dashboard',
                dataType: 'json',
                data: {
                    userId: userId
                },
                success: function(jqXHR){
                    console.log('ajaxGetDashboardData', jqXHR);
                    if(jqXHR != null){
                        if (callback && typeof(callback) === "function") {
                            callback(jqXHR);
                        }
                    }
                },
                error: function(jqXHR, textStatus, errorThrown){
                    console.error("ERROR getting services", this, jqXHR, textStatus, errorThrown);
                }
            })
        }

        ajaxGetDashboardData($scope.customerUserId, function(serverData){
            var arrService = serverData.service == null? [] : serverData.service;
            var arrInstance = serverData.instances == null? [] : serverData.instances;
            var arrOrder = serverData.orders == null? [] : serverData.orders;

            $scope.customerUser = serverData.user;

            //proceed instances and orders
            for(var i = 0; i < arrInstance.length; i++){
                var arrOrderIndexToDelete = [];
                var tmpServiceInstance = new ServiceInstance();

                tmpServiceInstance.setId(arrInstance[i].instanceId);
                tmpServiceInstance.setUserId(arrInstance[i].userId);
                tmpServiceInstance.setServiceLocation(arrInstance[i].serviceLocation);
                tmpServiceInstance.setServiceId(arrInstance[i].serviceId);
                tmpServiceInstance.setInstanceStatusId(arrInstance[i].instanceStatusId);
                tmpServiceInstance.setInstanceStatus(arrInstance[i].instanceStatus)
                tmpServiceInstance.setCableId(arrInstance[i].cableId);
                tmpServiceInstance.setIsBlocked(arrInstance[i].isBlocked);

                for(var j = 0; j < arrOrder.length; j++){
                    if(arrOrder[j].serviceInstanceId == tmpServiceInstance.getId()){
                        var tmpServiceOrder = new ServiceOrder();
                        tmpServiceOrder.setId(arrOrder[j].serviceOrderId);
                        tmpServiceOrder.setOrderStatusId(arrOrder[j].orderStatusId);
                        tmpServiceOrder.setOrderStatus(arrOrder[j].orderStatus);
                        tmpServiceOrder.setServiceInstanceId(arrOrder[j].serviceInstanceId);
                        tmpServiceOrder.setOrderScenarioId(arrOrder[j].orderScenarionId);
                        tmpServiceOrder.setOrderScenario(arrOrder[j].orderScenario);
                        tmpServiceOrder.setUserId(arrOrder[j].userId);
                        tmpServiceOrder.setData(null);

                        tmpServiceInstance.addServiceOrder(tmpServiceOrder);
                        arrOrderIndexToDelete.push(j);
                    }
                }

                $scope.arrServiceInstance.push(tmpServiceInstance.toJsonObj());

                //delete saved service orders
                for(var j = 0; j < arrOrderIndexToDelete.length; j++){
                    arrOrder.splice(arrOrderIndexToDelete[j], 1);
                }
            }

            //proceed provider locations
            var tmpProviderLocation = new ProviderLocation();
            var tmpService = new Service();

            tmpService.setId(arrService[0].serviceId);
            tmpService.setServiceTypeId(arrService[0].serviceTypeId);
            tmpService.setServiceTypeName(arrService[0].serviceTypeName);
            tmpService.setProviderLocationId(arrService[0].providerLocationId);
            tmpService.setServiceSpeed(arrService[0].serviceSpeed);
            tmpService.setPrice(arrService[0].price);

            tmpProviderLocation.setId(arrService[0].providerLocationId);
            tmpProviderLocation.setLat(arrService[0].locationLatitude);
            tmpProviderLocation.setLng(arrService[0].locationLongitude);
            tmpProviderLocation.setLocationAddress(arrService[0].locationAddress);
            tmpProviderLocation.addService(tmpService);

            $scope.arrProviderLocation.push(tmpProviderLocation);

            //proceed rest of services
            for(var i = 1; i < arrService.length; i++){
                var j = 0;
                var k = 0;
                var index = 0;
                var isAdded = false;
                var isAddedProviderLocation = false;

                while((j < $scope.arrProviderLocation.length) && (!isAdded)){
                    if(arrService[i].providerLocationId === $scope.arrProviderLocation[j].getId()){
                        isAdded = true;
                        index = j;
                    }
                    j++;
                }

                tmpService = new Service();
                tmpService.setId(arrService[i].serviceId);
                tmpService.setServiceTypeId(arrService[i].serviceTypeId);
                tmpService.setServiceTypeName(arrService[i].serviceTypeName);
                tmpService.setProviderLocationId(arrService[i].providerLocationId);
                tmpService.setServiceSpeed(arrService[i].serviceSpeed);
                tmpService.setPrice(arrService[i].price);

                if(!isAdded){
                    tmpProviderLocation = new ProviderLocation();

                    tmpProviderLocation.setId(arrService[i].providerLocationId);
                    tmpProviderLocation.setLat(arrService[i].locationLatitude);
                    tmpProviderLocation.setLng(arrService[i].locationLongitude);
                    tmpProviderLocation.setLocationAddress(arrService[i].locationAddress);
                    tmpProviderLocation.addService(tmpService);

                    $scope.arrProviderLocation.push(tmpProviderLocation);
                    index = $scope.arrProviderLocation.length - 1;
                }else{
                    $scope.arrProviderLocation[index].addService(tmpService);
                }

                for(var k = 0; k < $scope.arrServiceInstance.length; k++){
                    var l = 0;
                    isAddedProviderLocation = false;
                    while(l < $scope.arrProviderLocation[index].getArrService().length && !isAddedProviderLocation){
                        if($scope.arrServiceInstance[k].serviceId == $scope.arrProviderLocation[index].getArrService()[l].getId()){
                            $scope.arrServiceInstance[k].providerLocation = $scope.arrProviderLocation[index].toJsonObj();
                            isAddedProviderLocation = true;
                        }
                        l++;
                    }
                }
            }

            console.log('******* $scope.arrServiceInstance *******');
            for(var i = 0; i < $scope.arrServiceInstance.length; i++){
                console.log($scope.arrServiceInstance[i]);
            }

            console.log('******* $scope.arrProviderLocation *******');
            for(var i = 0; i < $scope.arrProviderLocation.length; i++){
                console.log($scope.arrProviderLocation[i].toJsonObj());
            }

            $scope.$apply(function(){ $scope.serviceInstance = $scope.arrServiceInstance[0]; });
            updateGeneralInfo(true);

            $(document).ready(function(){
                $('#js-act-change-pass').click(function(e){
                    e.preventDefault();
                    console.log($('#js-input-password').length);

                    if(!passwordCheck('#js-change-pass')){
                        popover('#js-change-pass', 2000);
                    }else{
                        $('#js-form-change-pass').submit();
                    }
                });
            })
        });
    });