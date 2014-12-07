'use strict'

var arrOrder =[
	{"serviceOrderId":1,"orderStatusId":3,"orderStatus":"PROCESSING","serviceInstanceId":1,"orderScenarioId":1,"orderScenario":"NEW"},
	{"serviceOrderId":2,"orderStatusId":3,"orderStatus":"PROCESSING","serviceInstanceId":2,"orderScenarioId":1,"orderScenario":"NEW"}
];
var arrInstance = [
	{"instanceId":1,"userId":21,"serviceLocation":{"serviceLocationId":1,"locationAddress":"\"Volodymyra Mayakovs'koho avenue, 36/7, Kyiv, Kyiv city, Ukraine\"","locationLongitude":30.613856299999952,"locationLatitude":50.5113353},"serviceId":4,"instanceStatusId":1,"instanceStatus":"DISCONNECTED","cableId":0,"isBlocked":true},
	{"instanceId":2,"userId":21,"serviceLocation":{"serviceLocationId":2,"locationAddress":"\"Mykoly Zakrevs'koho street, 49А, Kyiv, Kyiv city, Ukraine\"","locationLongitude":30.61818838119507,"locationLatitude":50.50856870002049},"serviceId":2,"instanceStatusId":1,"instanceStatus":"DISCONNECTED","cableId":0,"isBlocked":true}
];
var arrService = [
	{"serviceId":3,"providerLocationId":1,"serviceTypeId":3,"locationAddress":"locationAddress1","locationLongitude":30.623188018798828,"locationLatitude":50.50551183400235,"serviceTypeName":"PLATINUM","serviceSpeed":"100","price":70.0},
	{"serviceId":2,"providerLocationId":1,"serviceTypeId":2,"locationAddress":"locationAddress1","locationLongitude":30.623188018798828,"locationLatitude":50.50551183400235,"serviceTypeName":"GOLD","serviceSpeed":"40","price":45.0},
	{"serviceId":1,"providerLocationId":1,"serviceTypeId":1,"locationAddress":"locationAddress1","locationLongitude":30.623188018798828,"locationLatitude":50.50551183400235,"serviceTypeName":"SILVER","serviceSpeed":"20","price":30.0},
	{"serviceId":4,"providerLocationId":2,"serviceTypeId":3,"locationAddress":"locationAddress3","locationLongitude":30.607738494873047,"locationLatitude":50.5159916936212,"serviceTypeName":"PLATINUM","serviceSpeed":"100","price":70.0}
];

angular.module('MapDashboard', [])
	.controller('MapDashboardController', function($scope){
		$scope.gmap = $("#js-map").gmap3();
		$scope.arrServiceInstance = [];
		$scope.arrProviderLocation = [];
		$scope.arrService = arrService;

		$scope.serviceInstance;
		$scope.service;
		$scope.arrAvailableServices = [];

		$scope.arrServiceInstanceMapMarker = [];
		$scope.arrProviderLocationMapMarker = [];

		var markerIcons = {
			red: new google.maps.MarkerImage("img/icons/marker_red.png"),
			green: new google.maps.MarkerImage("img/icons/marker_green.png"),
			blue: new google.maps.MarkerImage("img/icons/marker_blue.png"),
			grey: new google.maps.MarkerImage("img/icons/marker_grey.png")
		}

		$scope.update = function(){
			updateGeneralInfo();
		}

		function mapGetLatLngByAddress(gmap, address, callback){
			gmap.gmap3({
				getlatlng: {
					address: address,
					callback: callback
				}
			})
		}

		function mapSetMarkerLocation(gmap, tag, latLng, centerCamera, zoom){
			gmap.gmap3({
				get: {
					tag: tag,
					all: true,
					callback: function(marker){
						if(marker.length != 0){
							marker[0].setPosition(latLng);
						}
					}
				}
			});

			if(centerCamera){
				gmap.gmap3({
					map: {
						options: {
							center: latLng 
						}
					}
				});
			}

			if(typeof zoom !== "undefined"){
				if(zoom > 0 && zoom < 22)
					gmap.gmap3({
						map: {
							options: {
								zoom: zoom
							}
						}
					});
			}
		}

		function mapAddMarker(gmap, mapMarker){
			gmap.gmap3({
				marker: {
					values: [mapMarker.getValues()],
					data: mapMarker.getData(),
					events: mapMarker.getEvents()
				}
			})
		}

		function mapAddMarkers(gmap, arrMapMarker){
			for(var i = 0; i < arrMapMarker.length; i++){
				mapAddMarker(gmap, arrMapMarker[i]);
			}
		}

		function mapZoomCamera(gmap, zoom){
			gmap.gmap3({
				map:{
					options: { zoom: zoom }
				} 
				
			})
		}

		function mapCenterCamera(gmap, latLng, isSmooth){
			if(isSmooth){
				gmap.gmap3("get").panTo(new google.maps.LatLng(latLng[0], latLng[1]));
			}else{
				gmap.gmap3({ 
					map:{
						options: { center: latLng }
					}
				})
			}
		}

		function mapGetAddressByLatLng(gmap, latLng, callback){
			gmap.gmap3({
				getaddress: {
					latLng: latLng,
					callback: callback
				}
			});
		}

		function mapGetMarkers(gmap, tag, callback){
			gmap.gmap3({
				get: {
					tag: tag,
					all: true,
					full: true,
					callback: callback
				}
			});
		}

		function rad(x){
			return x * Math.PI / 180;
		}

		function findClosest(marker, markers){
			var lat = marker.object.position.lat();
			var lng = marker.object.position.lng();
			var R = 6371;
			var arrDistance = [];
			var closest = -1;

			for(var i = 0; i < markers.length; i++){
				var mlat = markers[i].object.position.lat();
				var mlng = markers[i].object.position.lng();
				var dLat = rad(mlat - lat);
				var dLng = rad(mlng - lng);
				var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(rad(lat)) * Math.cos(rad(mlat)) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
				var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
				var d = R * c;

				arrDistance[i] = d;

				if(closest == -1 || d < arrDistance[closest]){
					closest = i;
				}
			}

			return {
				marker: markers[closest],
				distance: arrDistance[closest],
				index: closest
			}
		}

		function mapClearByTag(gmap, tag){
			gmap.gmap3({
				clear: { 
					tag: tag 
				}
			});
		}

		function updateGeneralInfo(){
			var i = 0;
			var isFound = false;

			$scope.arrAvailableServices = [];

			while(i < $scope.arrService.length && !isFound){
				if($scope.serviceInstance.id == $scope.arrService[i].serviceId){
                    $scope.$apply(function(){ $scope.service = $scope.arrService[i]; });
					isFound = true;
				}
				i++;
			}

			for(var i = 0; i < $scope.arrService.length; i++){
				if($scope.arrService[i].providerLocationId == $scope.service.providerLocationId){
                    $scope.$apply(function(){ $scope.arrAvailableServices.push($scope.arrService[i]); });
				}
			}

			mapCenterCamera($scope.gmap, [$scope.serviceInstance.serviceLocation.locationLatitude,$scope.serviceInstance.serviceLocation.locationLongitude], true);
			mapZoomCamera($scope.gmap, 14);

			mapGetMarkers($scope.gmap, 'serviceLocation', function(markers){
				for(var i = 0; i < markers.length; i++){
					//set bounce animation
					if($scope.serviceInstance.id == markers[i].data.getId()){
						markers[i].object.setAnimation(google.maps.Animation.BOUNCE);
					}else{
						markers[i].object.setAnimation(null);
					}
				}
			});

			// for(var i = 0; i < 
			// 	if($scope.service.providerLocationId){

			// 	}
		}

		function mapConnectServiceAndProviderLocations(){
			for(var i = 0; i < $scope.arrServiceInstance.length; i++){
				var path = [];
				path.push([$scope.arrServiceInstance[i].serviceLocation.locationLatitude,$scope.arrServiceInstance[i].serviceLocation.locationLongitude]);
				path.push([$scope.arrServiceInstance[i].providerLocation.lat,$scope.arrServiceInstance[i].providerLocation.lng]);
				mapDrawPolyline($scope.gmap, path, 'blue', false);
			}
		}

		function mapDrawPolyline(gmap, path, style, doClear){
			var styleOptions = {
					blue: '#00a5e0'
				}

			if(doClear) mapClearByTag(gmap, 'polyline');
			
			gmap.gmap3({
				polyline: {
					tag: 'polyline',
					options: {
						strokeColor: styleOptions[style],
						strokeOpacity: 1.0,
						strokeWeight: 3,
						path: path
					}
				}
			})
		}

		var gmapOptions = {
			map: {
				options: {
					zoom: 3,
					mapTypeControl: false,
					streetViewControl: false,
					panControl: true,
					zoomControl: true
				}
			}
		};

        function ajaxGetDashboardData(callback){
            $.ajax({
                type: 'GET',
                url: 'dashboard',
                dataType: 'json',
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

        /*******************END FUNCTIONS*******************/

        ajaxGetDashboardData(function(serverData){
            var arrService = serverData.service == null? [] : serverData.service;
            var arrInstance = serverData.instances == null? [] : serverData.instances;
            var arrOrder = serverData.orders == null? [] : serverData.orders;

            console.log('arrService', arrService);

        //proceed instances and orders
		for(var i = 0; i < arrInstance.length; i++){
			var arrOrderIndexToDelete = [];
			var tmpServiceInstance = new ServiceInstance();
			var tmpServiceLocationMapMarker = new MapMarker();

			tmpServiceInstance.setId(arrInstance[i].instanceId);
			tmpServiceInstance.setUserId(arrInstance[i].userId);
			tmpServiceInstance.setServiceLocation(arrInstance[i].serviceLocation);
			tmpServiceInstance.setServiceId(arrInstance[i].serviceId);
			tmpServiceInstance.setInstanceStatusId(arrInstance[i].instanceStatusId);
			tmpServiceInstance.setInstanceStatus(arrInstance[i].instanceStatus)
			tmpServiceInstance.setCableId(arrInstance[i].cableId);
			tmpServiceInstance.setIsBlocked(arrInstance[i].isBlocked);

			//service lcation map markers
			tmpServiceLocationMapMarker.setValues({
				latLng: [tmpServiceInstance.getServiceLocation().locationLatitude,tmpServiceInstance.getServiceLocation().locationLongitude],
				tag: 'serviceLocation',
				name: 'serviceLocation',
				options: {
					icon: markerIcons.green,
					animation: null
				}
			});
			tmpServiceLocationMapMarker.setData(tmpServiceInstance);
			tmpServiceLocationMapMarker.setOptions({});
			tmpServiceLocationMapMarker.setEvents({});

			$scope.arrServiceInstanceMapMarker.push(tmpServiceLocationMapMarker);

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
		var tmpLocationAddressMapMarker = new MapMarker();

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

		//provider location map markers
		tmpLocationAddressMapMarker.setValues({
			latLng: [tmpProviderLocation.getLat(),tmpProviderLocation.getLng()],
			tag: 'providerLocation',
			name: 'providerLocation',
			options: {
				icon: markerIcons.blue
			}
		});
		tmpLocationAddressMapMarker.setData(tmpProviderLocation);
		tmpLocationAddressMapMarker.setOptions({});
		tmpLocationAddressMapMarker.setEvents({});

		$scope.arrProviderLocationMapMarker.push(tmpLocationAddressMapMarker);
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
				tmpLocationAddressMapMarker = new MapMarker();

				tmpProviderLocation.setId(arrService[i].providerLocationId);
				tmpProviderLocation.setLat(arrService[i].locationLatitude);
				tmpProviderLocation.setLng(arrService[i].locationLongitude);
				tmpProviderLocation.setLocationAddress(arrService[i].locationAddress);
				tmpProviderLocation.addService(tmpService);
				
				//provider location map markers
				tmpLocationAddressMapMarker.setValues({
					latLng: [tmpProviderLocation.getLat(),tmpProviderLocation.getLng()],
					tag: 'providerLocation',
					name: 'providerLocation',
					options: {
						icon: markerIcons.blue
					}
				});
				tmpLocationAddressMapMarker.setData(tmpProviderLocation);
				tmpLocationAddressMapMarker.setOptions({});
				tmpLocationAddressMapMarker.setEvents({});

				$scope.arrProviderLocationMapMarker.push(tmpLocationAddressMapMarker);
				$scope.arrProviderLocation.push(tmpProviderLocation);
                index = $scope.arrProviderLocation.length - 1;
			}else{
				$scope.arrProviderLocation[index].addService(tmpService);
			}

			// set provider location for service instance
//			while(k < $scope.arrServiceInstance.length && !isAddedProviderLocation){
//                console.log('i: ' + i + ',k: ' + k);
//				if($scope.arrServiceInstance[k].serviceId == tmpService.getId()){
//					$scope.arrServiceInstance[k].providerLocation = $scope.arrProviderLocation[index].toJsonObj();
//					isAddedProviderLocation = true;
//				}
//				k++;
//			}

            for(var k = 0; k < $scope.arrServiceInstance.length; k++){
                if($scope.arrServiceInstance[k].serviceId == tmpService.getId()){
					$scope.arrServiceInstance[k].providerLocation = $scope.arrProviderLocation[index].toJsonObj();
					isAddedProviderLocation = true;
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

		$scope.$apply(function(){ $scope.serviceInstance = $scope.arrServiceInstance[0]; })

		updateGeneralInfo();

		$(document).ready(function(){
			//init
			$scope.gmap.gmap3({
				map: gmapOptions.map
			});

			mapAddMarkers($scope.gmap, $scope.arrServiceInstanceMapMarker);
			mapAddMarkers($scope.gmap, $scope.arrProviderLocationMapMarker);
			mapConnectServiceAndProviderLocations();
            updateGeneralInfo();
		})

        });
	});