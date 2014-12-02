function AngularTestController($scope){
	$scope.greeting = {
		text: 'Hello'
	};
}
'use strict'

angular.module('MapOrder', [])
	.controller('MapOrderController', function($scope){
		$scope.address;

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

function mapCenterCamera(gmap, latLng){
	gmap.gmap3({ 
		map:{
			options: { center: latLng }
		}
	})
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

	console.log('closest index: ', closest, 'closest marker: ', markers[closest], 'distance: ', arrDistance[closest]);
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

function mapSetServiceOptions(marker){
	console.log(marker);
	console.log(marker.data.toJsonObj());
	// console.log(marker.data.getLocationAddress());
	$("#js-pl-addr").html(marker.data.getLocationAddress());
}

/*******************END FUNCTIONS*******************/

var gmap;
var serviceLocation = {};
var lastNearestProviderLocation;
var markerIcons = {
	red: new google.maps.MarkerImage("/img/icons/marker_red.png"),
	green: new google.maps.MarkerImage("/img/icons/marker_green.png"),
	blue: new google.maps.MarkerImage("/img/icons/marker_blue.png"),
	grey: new google.maps.MarkerImage("/img/icons/marker_grey.png")
}

//Active Marker Initialization
var activeMarker = new MapMarker();
activeMarker.setValues({
	latLng: [10,0],
	tag: 'activeMarker',
	name: 'activeMarker',
	options: {
		icon: markerIcons.red
	}
});
activeMarker.setData({});
activeMarker.setOptions({});
activeMarker.setEvents({
	click: function(marker, event, context){
		console.log(marker, event, context);

		var map = $(this).gmap3("get");

		infobox = new InfoBox({
			content: $('#js-infobox')[0].outerHTML,
			disableAutoPan: false,
			pixelOffset: new google.maps.Size(-140, 4),
			zIndex: null,
			boxStyle: {
				width: "280px"
			},
			closeBoxMargin: "10px",
			closeBoxURL: "./img/icons/x.png",
			infoBoxClearance: new google.maps.Size(1, 1)
		});
		infobox.open(map, marker);
	}
});

var arrProviderLocationMarker = [];

// service type id:
// 1 - Silver
// 2 - Gold
// 3 - Platinum
var arrService = [
	{serviceId:1,providerLocationId:1,serviceTypeId:1,locationAddress:"locationAddress1",locationLongitude:30.623188018798828,locationLatitude:50.50551183400235,serviceTypeName:"Silver",serviceSpeed:"20",price:0},
	{serviceId:2,providerLocationId:1,serviceTypeId:2,locationAddress:"locationAddress2",locationLongitude:30.623188018798828,locationLatitude:50.50551183400235,serviceTypeName:"Gold",serviceSpeed:"40",price:0},
	{serviceId:3,providerLocationId:1,serviceTypeId:3,locationAddress:"locationAddress3",locationLongitude:30.623188018798828,locationLatitude:50.50551183400235,serviceTypeName:"Platinum",serviceSpeed:"100",price:0},
	{serviceId:4,providerLocationId:2,serviceTypeId:1,locationAddress:"locationAddress4",locationLongitude:30.607738494873047,locationLatitude:50.5159916936212,serviceTypeName:"Silver",serviceSpeed:"20",price:0},
	{serviceId:5,providerLocationId:3,serviceTypeId:2,locationAddress:"locationAddress5",locationLongitude:30.623188018798828,locationLatitude:50.5159916936212 ,serviceTypeName:"Gold",serviceSpeed:"40",price:0},
	{serviceId:6,providerLocationId:3,serviceTypeId:3,locationAddress:"locationAddress6",locationLongitude:30.623188018798828,locationLatitude:50.5159916936212,serviceTypeName:"Platinum",serviceSpeed:"100",price:0},
	{serviceId:7,providerLocationId:3,serviceTypeId:1,locationAddress:"locationAddress7",locationLongitude:30.623188018798828,locationLatitude:50.515991693621,serviceTypeName:"Silver",serviceSpeed:"20",price:0},
	{serviceId:8,providerLocationId:4,serviceTypeId:2,locationAddress:"locationAddress8",locationLongitude:30.607738494873047,locationLatitude:50.505675599698684,serviceTypeName:"Gold",serviceSpeed:"40",price:0},
	{serviceId:9,providerLocationId:2,serviceTypeId:3,locationAddress:"locationAddress9",locationLongitude:30.607738494873047,locationLatitude:50.5159916936212,serviceTypeName:"Platinum",serviceSpeed:"100",price:0}
];

var arrProviderLocation = [];
var arrProviderLocationData = [];

// temporary
var tmpProviderLocation = new ProviderLocation();
var tmpService = new Service();

//add first pl and it's service
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

arrProviderLocation.push(tmpProviderLocation);

//proceed rest of services
for(var i = 1; i < arrService.length; i++){
	var j = 0;
	var index = 0;
	var isAdded = false;

	tmpService = new Service();
	tmpService.setId(arrService[i].serviceId);
	tmpService.setServiceTypeId(arrService[i].serviceTypeId);
	tmpService.setServiceTypeName(arrService[i].serviceTypeName);
	tmpService.setProviderLocationId(arrService[i].providerLocationId);
	tmpService.setServiceSpeed(arrService[i].serviceSpeed);
	tmpService.setPrice(arrService[i].price);

	while((j < arrProviderLocation.length) && (!isAdded)){
		if(arrService[i].providerLocationId === arrProviderLocation[j].getId()){
			isAdded = true;
			index = j;
		}
		j++;
	}

	if(!isAdded){
		tmpProviderLocation = new ProviderLocation();
		tmpProviderLocation.setId(arrService[i].providerLocationId);
		tmpProviderLocation.setLat(arrService[i].locationLatitude);
		tmpProviderLocation.setLng(arrService[i].locationLongitude);
		tmpProviderLocation.setLocationAddress(arrService[i].locationAddress);
		tmpProviderLocation.addService(tmpService);

		arrProviderLocation.push(tmpProviderLocation);
	}else{
		arrProviderLocation[index].addService(tmpService);
	}
}

//Provider Location Markers Initialization
var arrProviderLocationMapMarker = [];

for(var i = 0; i < arrProviderLocation.length; i++){
	var tmpProviderLocationMapMarker = new MapMarker();
	tmpProviderLocationMapMarker.setValues({
		latLng: [arrProviderLocation[i].getLat(),arrProviderLocation[i].getLng()],
		tag: 'providerLocation',
		name: 'providerLocation',
		options: {
			icon: markerIcons.blue
		}
	});
	tmpProviderLocationMapMarker.setData(arrProviderLocation[i]);
	tmpProviderLocationMapMarker.setEvents({
		click: function(marker, event, context){
			console.log(marker, event, context);
		}
	});
	arrProviderLocationMapMarker.push(tmpProviderLocationMapMarker);

	console.log(tmpProviderLocationMapMarker.getValues());
}

var gmapOptions = {
	map: {
		options: {
			zoom: 3,
			mapTypeControl: false,
			streetViewControl: false,
			panControl: true,
			zoomControl: true,
		}
	}
};

$(document).ready(function(){
	//init
	gmap = $("#js-map").gmap3({
		map: gmapOptions.map
	});

	mapAddMarkers(gmap, arrProviderLocationMapMarker);

	//request geolocation
	gmap.gmap3({
		getgeoloc: {
			callback: function(latLng){
				if(latLng){
					console.log(latLng);

					activeMarker.getValues().latLng[0] = latLng.lat();
					activeMarker.getValues().latLng[1] = latLng.lng();

					mapAddMarker(gmap, activeMarker);

					mapZoomCamera(gmap, 14);
					mapCenterCamera(gmap, activeMarker.getValues().latLng);

					mapGetAddressByLatLng(this, latLng, function(addr){
						if(addr && addr[0]){
							$scope.$apply(function(){ $scope.address = addr[0].formatted_address; });
						}
					});

					mapGetMarkers(this, 'activeMarker', function(marker){
						mapGetMarkers(this, 'providerLocation', function(markers){
							var closest = findClosest(marker[0], markers);
							mapDrawPolyline(gmap, [
									[ marker[0].object.position.lat(), marker[0].object.position.lng() ],
									[ closest.marker.object.position.lat(), closest.marker.object.position.lng() ],
								], 'blue', true);
						});
					});
					//activeMarker.getValues().lat.setPosition(latLng);

					// $(this).gmap3({
					// 	marker: {
					// 		latLng: activeMarker.getPosition(),
					// 		options: {
					// 				icon: activeMarker.getIcon()
					// 			},
					// 		tag: activeMarker.getTag(),
					// 		data: {
					// 			id: activeMarker.getData()
					// 		},
					// 		events: {
					// 			click: function(marker, event, context){
					// 				console.log(marker, event, context);

					// 				var map = $(this).gmap3("get");

					// 				infobox = new InfoBox({
					// 					content: $('#js-infobox')[0].outerHTML,
					// 					disableAutoPan: false,
					// 					pixelOffset: new google.maps.Size(-140, 4),
					// 					zIndex: null,
					// 					boxStyle: {
					// 						width: "280px"
					// 					},
					// 					closeBoxMargin: "10px",
					// 					closeBoxURL: "./img/icons/x.png",
					// 					infoBoxClearance: new google.maps.Size(1, 1)
					// 				});
					// 				infobox.open(map, marker);
					// 			}
					// 		}
					// 	},
					// 	map: {
					// 		options: {
					// 			center: latLng,
					// 			zoom: 14
					// 		}
					// 	}
					// });
					// //find nearest PL and draw polyline
				}
			}
		}
	});

	//event handlers
	//google map 
	gmap.gmap3({
		map: {
			events: {
				click: function(map, event, context){
					var closest;
					// this is not good style
					mapGetMarkers(this, 'activeMarker', function(marker){
						console.log(marker);
						if(marker.length > 0){
							mapSetMarkerLocation(this, 'activeMarker', event.latLng, false, 0);
							mapGetAddressByLatLng(this, event.latLng, function(addr){
								if(addr && addr[0]){
									$scope.$apply(function(){ $scope.address = addr[0].formatted_address; });
								}
							});
							mapGetMarkers(this, 'providerLocation', function(markers){
								closest = findClosest(marker[0], markers);
								mapDrawPolyline(gmap, [
										[ marker[0].object.position.lat(), marker[0].object.position.lng() ],
										[ closest.marker.object.position.lat(), closest.marker.object.position.lng() ],
									], 'blue', true);

								mapSetServiceOptions(closest.marker);
							});
						}else{
							activeMarker.getValues().latLng[0] = event.latLng.lat();
							activeMarker.getValues().latLng[1] = event.latLng.lng();

							mapAddMarker(gmap, activeMarker);

							mapGetMarkers(this, 'providerLocation', function(markers){
								closest = findClosest(marker[0], markers);
								mapDrawPolyline(gmap, [
										[ marker[0].object.position.lat(), marker[0].object.position.lng() ],
										[ closest.marker.object.position.lat(), closest.marker.object.position.lng() ],
									], 'blue', true);
								mapSetServiceOptions(closest.marker);
							});
						}
					});
				}
			}
		}
	});

	// //fix this shit
	// $('#js-address').geocomplete({
	// 	types: ['geocode']
	// });


	// $('#js-address').focusout(function(){
	// 	console.log('focus out');
	// 	var that = this;
	// 	setTimeout(function(){
	// 		$('#js-address').trigger('geocode');
	// 	}, 500);
	// });

})
});

'use strict'

function MapMarker(){
	var _values = {};
	var _options = {};
	var _data = {};
	var _events = {};

	this.getValues = function(){ return _values; }
	this.setValues = function(values){ _values = values; }
	this.getOptions = function(){ return _options; }
	this.setOptions = function(options){ _options = options; }
	this.getData = function(){ return _data; }
	this.setData = function(data){ _data = data; }
	this.getEvents = function(){ return _events; }
	this.setEvents = function(events){ _events = events; }
}
'use strict'

angular.module('MapOrder', [])
	.controller('MapOrderController', function($scope){
		$scope.serviceLocationAddress;
		$scope.providerLocationAddress;
		$scope.arrService = [];
		$scope.isOpenedInfobox = false;

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

function mapCenterCamera(gmap, latLng){
	gmap.gmap3({ 
		map:{
			options: { center: latLng }
		}
	})
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

	console.log('closest index: ', closest, 'closest marker: ', markers[closest], 'distance: ', arrDistance[closest]);
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
mapSetServiceOptions = function(marker){
	console.log(marker);
	console.log(marker.data.toJsonObj());
	console.log(marker.data.getArrService());

	$scope.$apply(function(){ $scope.providerLocationAddress = marker.data.getLocationAddress() });
	$scope.$apply(function(){ $scope.arrService = marker.data.getArrService() });

}

/*******************END FUNCTIONS*******************/

var gmap;
var serviceLocation = {};
var lastNearestProviderLocation;
var markerIcons = {
	red: new google.maps.MarkerImage("/img/icons/marker_red.png"),
	green: new google.maps.MarkerImage("/img/icons/marker_green.png"),
	blue: new google.maps.MarkerImage("/img/icons/marker_blue.png"),
	grey: new google.maps.MarkerImage("/img/icons/marker_grey.png")
}

//Active Marker Initialization
var activeMarker = new MapMarker();
activeMarker.setValues({
	latLng: [10,0],
	tag: 'activeMarker',
	name: 'activeMarker',
	options: {
		icon: markerIcons.red
	}
});
activeMarker.setData({});
activeMarker.setOptions({});
activeMarker.setEvents({
	click: function(marker, event, context){
		if(!$scope.isOpenedInfobox){
			var map = $(this).gmap3("get");
			var infobox = new InfoBox({
				content: $('#js-infobox')[0].outerHTML,
				disableAutoPan: false,
				pixelOffset: new google.maps.Size(-160, 4),
				zIndex: null,
				boxStyle: {
					width: "320px"
				},
				closeBoxMargin: "10px",
				closeBoxURL: "./img/icons/x.png",
				infoBoxClearance: new google.maps.Size(1, 1)
			});

			infobox.open(map, marker);
			$scope.isOpenedInfobox = true;
			//infowindow event handler
			google.maps.event.addListener(infobox, 'closeclick', function(event){
				$scope.isOpenedInfobox = false;
			});
		}
	}
});

var arrProviderLocationMarker = [];

// service type id:
// 1 - Silver
// 2 - Gold
// 3 - Platinum
var arrService = [
	{serviceId:1,providerLocationId:1,serviceTypeId:1,locationAddress:"locationAddress1",locationLongitude:30.623188018798828,locationLatitude:50.50551183400235,serviceTypeName:"Silver",serviceSpeed:"20",price:0},
	{serviceId:2,providerLocationId:1,serviceTypeId:2,locationAddress:"locationAddress2",locationLongitude:30.623188018798828,locationLatitude:50.50551183400235,serviceTypeName:"Gold",serviceSpeed:"40",price:0},
	{serviceId:3,providerLocationId:1,serviceTypeId:3,locationAddress:"locationAddress3",locationLongitude:30.623188018798828,locationLatitude:50.50551183400235,serviceTypeName:"Platinum",serviceSpeed:"100",price:0},
	{serviceId:4,providerLocationId:2,serviceTypeId:1,locationAddress:"locationAddress4",locationLongitude:30.607738494873047,locationLatitude:50.5159916936212,serviceTypeName:"Silver",serviceSpeed:"20",price:0},
	{serviceId:5,providerLocationId:3,serviceTypeId:2,locationAddress:"locationAddress5",locationLongitude:30.623188018798828,locationLatitude:50.5159916936212 ,serviceTypeName:"Gold",serviceSpeed:"40",price:0},
	{serviceId:6,providerLocationId:3,serviceTypeId:3,locationAddress:"locationAddress6",locationLongitude:30.623188018798828,locationLatitude:50.5159916936212,serviceTypeName:"Platinum",serviceSpeed:"100",price:0},
	{serviceId:7,providerLocationId:3,serviceTypeId:1,locationAddress:"locationAddress7",locationLongitude:30.623188018798828,locationLatitude:50.515991693621,serviceTypeName:"Silver",serviceSpeed:"20",price:0},
	{serviceId:8,providerLocationId:4,serviceTypeId:2,locationAddress:"locationAddress8",locationLongitude:30.607738494873047,locationLatitude:50.505675599698684,serviceTypeName:"Gold",serviceSpeed:"40",price:0},
	{serviceId:9,providerLocationId:2,serviceTypeId:3,locationAddress:"locationAddress9",locationLongitude:30.607738494873047,locationLatitude:50.5159916936212,serviceTypeName:"Platinum",serviceSpeed:"100",price:0}
];

var arrProviderLocation = [];
var arrProviderLocationData = [];

// temporary
var tmpProviderLocation = new ProviderLocation();
var tmpService = new Service();

//add first pl and it's service
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

arrProviderLocation.push(tmpProviderLocation);

//proceed rest of services
for(var i = 1; i < arrService.length; i++){
	var j = 0;
	var index = 0;
	var isAdded = false;

	tmpService = new Service();
	tmpService.setId(arrService[i].serviceId);
	tmpService.setServiceTypeId(arrService[i].serviceTypeId);
	tmpService.setServiceTypeName(arrService[i].serviceTypeName);
	tmpService.setProviderLocationId(arrService[i].providerLocationId);
	tmpService.setServiceSpeed(arrService[i].serviceSpeed);
	tmpService.setPrice(arrService[i].price);

	while((j < arrProviderLocation.length) && (!isAdded)){
		if(arrService[i].providerLocationId === arrProviderLocation[j].getId()){
			isAdded = true;
			index = j;
		}
		j++;
	}

	if(!isAdded){
		tmpProviderLocation = new ProviderLocation();
		tmpProviderLocation.setId(arrService[i].providerLocationId);
		tmpProviderLocation.setLat(arrService[i].locationLatitude);
		tmpProviderLocation.setLng(arrService[i].locationLongitude);
		tmpProviderLocation.setLocationAddress(arrService[i].locationAddress);
		tmpProviderLocation.addService(tmpService);

		arrProviderLocation.push(tmpProviderLocation);
	}else{
		arrProviderLocation[index].addService(tmpService);
	}
}

//Provider Location Markers Initialization
var arrProviderLocationMapMarker = [];

for(var i = 0; i < arrProviderLocation.length; i++){
	var tmpProviderLocationMapMarker = new MapMarker();
	tmpProviderLocationMapMarker.setValues({
		latLng: [arrProviderLocation[i].getLat(),arrProviderLocation[i].getLng()],
		tag: 'providerLocation',
		name: 'providerLocation',
		options: {
			icon: markerIcons.blue
		}
	});
	tmpProviderLocationMapMarker.setData(arrProviderLocation[i]);
	tmpProviderLocationMapMarker.setEvents({
		click: function(marker, event, context){
			console.log(marker, event, context);
		}
	});
	arrProviderLocationMapMarker.push(tmpProviderLocationMapMarker);
}

var gmapOptions = {
	map: {
		options: {
			zoom: 3,
			mapTypeControl: false,
			streetViewControl: false,
			panControl: true,
			zoomControl: true,
		}
	}
};

$(document).ready(function(){
	//init
	gmap = $("#js-map").gmap3({
		map: gmapOptions.map
	});

	mapAddMarkers(gmap, arrProviderLocationMapMarker);

	//request geolocation
	gmap.gmap3({
		getgeoloc: {
			callback: function(latLng){
				if(latLng){
					console.log(latLng);

					activeMarker.getValues().latLng[0] = latLng.lat();
					activeMarker.getValues().latLng[1] = latLng.lng();

					mapAddMarker(gmap, activeMarker);

					mapZoomCamera(gmap, 14);
					mapCenterCamera(gmap, activeMarker.getValues().latLng);

					mapGetAddressByLatLng(this, latLng, function(addr){
						if(addr && addr[0]){
							$scope.$apply(function(){ $scope.serviceLocationAddress = addr[0].formatted_address; });
						}
					});

					mapGetMarkers(this, 'activeMarker', function(marker){
						mapGetMarkers(this, 'providerLocation', function(markers){
							var closest = findClosest(marker[0], markers);
							mapDrawPolyline(gmap, [
									[ marker[0].object.position.lat(), marker[0].object.position.lng() ],
									[ closest.marker.object.position.lat(), closest.marker.object.position.lng() ],
								], 'blue', true);

							mapSetServiceOptions(closest.marker);
						});
					});
					
				}
			}
		}
	});

	//google map 
	gmap.gmap3({
		map: {
			events: {
				click: function(map, event, context){
					var closest;
					// this is not good style
					mapGetMarkers(this, 'activeMarker', function(marker){
						console.log(marker);
						if(marker.length > 0){
							mapSetMarkerLocation(this, 'activeMarker', event.latLng, false, 0);
							mapGetAddressByLatLng(this, event.latLng, function(addr){
								if(addr && addr[0]){
									$scope.$apply(function(){ $scope.serviceLocationAddress = addr[0].formatted_address; });
								}
							});
							mapGetMarkers(this, 'providerLocation', function(markers){
								closest = findClosest(marker[0], markers);
								mapDrawPolyline(gmap, [
										[ marker[0].object.position.lat(), marker[0].object.position.lng() ],
										[ closest.marker.object.position.lat(), closest.marker.object.position.lng() ],
									], 'blue', true);

								mapSetServiceOptions(closest.marker);
							});
						}else{
							activeMarker.getValues().latLng[0] = event.latLng.lat();
							activeMarker.getValues().latLng[1] = event.latLng.lng();

							mapAddMarker(gmap, activeMarker);

							mapGetMarkers(this, 'providerLocation', function(markers){
								closest = findClosest(marker[0], markers);
								mapDrawPolyline(gmap, [
										[ marker[0].object.position.lat(), marker[0].object.position.lng() ],
										[ closest.marker.object.position.lat(), closest.marker.object.position.lng() ],
									], 'blue', true);
								mapSetServiceOptions(closest.marker);
							});
						}
					});
				}
			}
		}
	});

	// //fix this shit
	// $('#js-address').geocomplete({
	// 	types: ['geocode']
	// });


	// $('#js-address').focusout(function(){
	// 	console.log('focus out');
	// 	var that = this;
	// 	setTimeout(function(){
	// 		$('#js-address').trigger('geocode');
	// 	}, 500);
	// });

})
});
function ProviderLocation(){
	var _id;
	var _locationAddress;
	var _lat;
	var _lng;
	var _arrService = [];

	this.setId = function(id){ _id = id; }
	this.getId = function() { return _id; }

	this.getLocationAddress = function(){ return _locationAddress; }
	this.setLocationAddress = function(locationAddress){ _locationAddress = locationAddress; }

	this.setLat = function(lat){ _lat = lat; }
	this.getLat = function(){ return _lat; }

	this.setLng = function(lng){ _lng = lng; }
	this.getLng = function(){ return _lng; }

	this.addService = function(service){ _arrService.push(service);	}
	this.setArrService = function(arrService){ _arrService = arrService; }
	this.getArrService = function(){ return _arrService; }

	this.toJsonObj = function(){
		var arrServiceJsonObj = [];

		for(var i = 0; i < _arrService.length; i++){
			arrServiceJsonObj.push(_arrService[i].toJsonObj());
		}

		return {
			id: _id,
			locationAddress: _locationAddress,
			lat: _lat,
			lng: _lng,
			arrService: arrServiceJsonObj
		}
	}
};
function ResetPasswordController($scope){
	$scope.isLetterSent = false;

	$scope.sendLetter = function(){
		$scope.isLetterSent = true;
	}
}
function Service(){
	var _id;
	var _providerLocationId;
	
	var _locationAddress;
	var _lat;
	var _lng;

	var _serviceTypeId;
	var _serviceTypeName;

	var _serviceSpeed;
	var _price;

	this.getId = function(){ return _id; }
	this.setId = function(id){ _id = id; }

	this.setProviderLocationId = function(id){ _providerLocationId = id; }
	this.getProviderLocationId = function(){ return _providerLocationId; }

	this.getLocationAddress = function(){ return _locationAddress; }
	this.setLocationAddress = function(locationAddress){ _locationAddress = locationAddress; }

	this.getLat = function(){ return _lat; }
	this.setLat = function(lat){ _lat = lat; }

	this.getLng = function(){ return _lng; }
	this.setLng = function(lng){ _lng = lng; }

	this.getServiceTypeId = function(){ return _serviceTypeId; }
	this.setServiceTypeId = function(serviceTypeId){ _serviceTypeId = serviceTypeId; }

	this.getServiceTypeName = function(){ return _serviceTypeName; }
	this.setServiceTypeName = function(serviceTypeName){ _serviceTypeName = serviceTypeName; }

	this.getServiceSpeed = function(){ return _serviceSpeed; }
	this.setServiceSpeed = function(serviceSpeed){ _serviceSpeed = serviceSpeed; }

	this.getPrice = function(){ return _price; }
	this.setPrice = function(price){ _price = price; }

	this.toJsonObj = function(){
		return {
			id: _id,
			providerLocatiionId: _providerLocationId,
			locationAddress: _locationAddress,
			lat: _lat,
			lng: _lng,
			serviceTypeId: _serviceTypeId,
			serviceTypeName: _serviceTypeName,
			serviceSpeed: _serviceSpeed,
			price: _price
		}
	}
}