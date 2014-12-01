function AngularTestController($scope){
	$scope.greeting = {
		text: 'Hello'
	};
}
'use strict'

google.maps.Marker.prototype.init = function(parameters){
	typeof parameters.latLng !== "undefined" ? this.setPosition(parameters.latLng) : this.setPosition(null);
	typeof parameters.tag !== "undefined" ? this.setTag(parameters.tag) : this.setTag(null);
	typeof parameters.icon !== "undefined" ? this.setIcon(parameters.icon) : this.setIcon(null);
	return this;
}

google.maps.Marker.prototype.toGmap = function(){
	return {
		latLng: this.getPosition(),
		options: {
				icon: this.getIcon()
			},
		tag: this.getTag()
	}
}

google.maps.Marker.prototype.setTag = function(tag){ this.tag = tag; }
google.maps.Marker.prototype.getTag = function(tag){ return this.tag; }

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
			callback: callback
		}
	});
}

function rad(x){
	return x * Math.PI / 180;
}

function mapfindClosest(marker, markers){
	var lat = marker.getPosition().lat();
	var lng = marker.getPosition().lng();
	var R = 6371;
	var arrDistance = [];
	var closest = -1;

	for(var i = 0; i < markers.length; i++){
		var mlat = markers[i].getPosition().lat();
		var mlng = markers[i].getPosition().lng();
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

function isEqualProviderLocation(pl1, pl2){
	return (pl1.latLng[0] === pl2.latLng[0]) 
		&& (pl1.latLng[1] === pl2.latLng[1])
}

var gmap;
var serviceLocation = {};
var markerIcons = {
	red: new google.maps.MarkerImage("img/icons/marker_red.png"),
	green: new google.maps.MarkerImage("img/icons/marker_green.png"),
	blue: new google.maps.MarkerImage("img/icons/marker_blue.png"),
	grey: new google.maps.MarkerImage("img/icons/marker_grey.png")
}
var activeMarker = new google.maps.Marker().init({ 
	tag: "activeMarker",
	icon: markerIcons.red,
	visible: false,
	events:{
		click: function(marker, event, context){
			console.log('activeMarker click event');
		}
	}
});

console.log(activeMarker);

var arrService = [
	{ id: 1, name: 'Silver', price: 10, providerLocation: { id: 1, latLng: [50.50551183400235,30.623188018798828], tag: 'providerLocation', options: { icon: markerIcons.blue } } },
	{ id: 2, name: 'Gold', price: 25, providerLocation: { id: 1, latLng: [50.50551183400235,30.623188018798828], tag: 'providerLocation', options: { icon: markerIcons.blue } } },
	{ id: 3, name: 'Platinum', price: 45, providerLocation: { id: 1, latLng: [50.50551183400235,30.623188018798828], tag: 'providerLocation', options: { icon: markerIcons.blue } } },
	{ id: 4, name: 'Silver', price: 12, providerLocation: { id: 2, latLng: [50.5159916936212,30.607738494873047], tag: 'providerLocation', options: { icon: markerIcons.blue } } },
	{ id: 5, name: 'Gold', price: 27, providerLocation: { id: 3, latLng: [50.5159916936212,30.623188018798828], tag: 'providerLocation', options: { icon: markerIcons.blue } } },
	{ id: 6, name: 'Platinum', price: 47, providerLocation: { id: 3, latLng: [50.5159916936212,30.623188018798828], tag: 'providerLocation', options: { icon: markerIcons.blue } } },
	{ id: 7, name: 'Silver', price: 15, providerLocation: { id: 3, latLng: [50.5159916936212,30.623188018798828], tag: 'providerLocation', options: { icon: markerIcons.blue } } },
	{ id: 8, name: 'Gold', price: 30, providerLocation: { id: 4, latLng: [50.505675599698684,30.607738494873047], tag: 'providerLocation', options: { icon: markerIcons.blue } } },
	{ id: 9, name: 'Platinum', price: 50, providerLocation: { id: 2, latLng: [50.5159916936212,30.607738494873047], tag: 'providerLocation', options: { icon: markerIcons.blue } } }
];
var arrProviderLocation = [];

// temporary
var tmpProviderLocation = new ProviderLocation();
var tmpService = new Service();

tmpService.setId(arrService[0].id);
tmpService.setName(arrService[0].name);
tmpService.setPrice(arrService[0].price);
tmpService.setProviderLocationId(arrService[0].providerLocation.id);

tmpProviderLocation.setId(arrService[0].providerLocation.id);
tmpProviderLocation.setLatlng(arrService[0].providerLocation.latLng);
tmpProviderLocation.addService(tmpService);

arrProviderLocation.push(tmpProviderLocation);

for(var i = 1; i < arrService.length; i++){
	var j = 0;
	var index = 0;
	var isAdded = false;

	tmpService = new Service();
	tmpService.setId(arrService[i].id);
	tmpService.setName(arrService[i].name);
	tmpService.setPrice(arrService[i].price);
	tmpService.setProviderLocationId(arrService[i].providerLocation.id);

	while((j < arrProviderLocation.length) && (!isAdded)){
		if(arrService[i].providerLocation.id === arrProviderLocation[j].getId()){
			isAdded = true;
			index = j;
		}
		j++;
	}

	if(!isAdded){
		tmpProviderLocation = new ProviderLocation();
		tmpProviderLocation.setId(arrService[i].providerLocation.id);
		tmpProviderLocation.setLatlng(arrService[i].providerLocation.latLng);
		tmpProviderLocation.addService(tmpService);
		arrProviderLocation.push(tmpProviderLocation);

		//console.log(tmpProviderLocation.toJsonObj());
	}else{
		arrProviderLocation[index].addService(tmpService);
	}
}

for(var i = 0; i < arrProviderLocation.length; i++){
	console.log(arrProviderLocation[i].toJsonObj());
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
	},
	markers: {
		providerLocation: {
			// ToDo: revrite using Marker object
			values: [
				{ latLng: [50.50551183400235,30.623188018798828], tag: 'providerLocation', data: 'id 0', options: { icon: markerIcons.blue } },
				{ latLng: [50.5159916936212,30.607738494873047], tag: 'providerLocation', data: 'id 1', options: { icon: markerIcons.blue } },
				{ latLng: [50.5159916936212,30.623188018798828], tag: 'providerLocation', data: 'id 2', options: { icon: markerIcons.blue } },
				{ latLng: [50.505675599698684,30.607738494873047], tag: 'providerLocation', data: 'id 3', options: { icon: markerIcons.blue } }
			]
		}
	}
};

$(document).ready(function(){
	//init
	gmap = $("#js-map").gmap3({
		map: gmapOptions.map,
		marker: {
			values: gmapOptions.markers.providerLocation.values,
		}
	});

	//request geolocation
	gmap.gmap3({
		getgeoloc: {
			callback: function(latLng){
				if(latLng){
					activeMarker.setPosition(latLng);
					$(this).gmap3({
						marker: {
							latLng: activeMarker.getPosition(),
							options: {
									icon: activeMarker.getIcon()
								},
							tag: activeMarker.getTag(),
							events: {
								click: function(marker, event, context){
									console.log('active marker click event');

									var map = $(this).gmap3("get");
									var infobox = new InfoBox({
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
							}
						},
						map: {
							options: {
								center: latLng,
								zoom: 14
							}
						}
					});
					//find nearest PL and draw polyline
					mapGetMarkers(this, 'providerLocation', function(markers){
						var closest = mapfindClosest(activeMarker, markers);
						mapDrawPolyline(gmap, [
								[ activeMarker.getPosition().lat(), activeMarker.getPosition().lng() ],
								[ closest.marker.getPosition().lat(), closest.marker.getPosition().lng() ],
							], 'blue', true);
					});
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
					mapSetMarkerLocation(this, 'activeMarker', event.latLng, false, 0);
					mapGetAddressByLatLng(this, event.latLng, function(addr){
						if(addr && addr[0]){
							$("#js-address").val(addr[0].formatted_address);
						}
					});
					mapGetMarkers(this, 'activeMarker', function(marker){
						mapGetMarkers(this, 'providerLocation', function(markers){
							console.log(marker, markers);
							var closest = mapfindClosest(marker[0], markers);
							mapDrawPolyline(gmap, [
									[ marker[0].getPosition().lat(), marker[0].getPosition().lng() ],
									[ closest.marker.getPosition().lat(), closest.marker.getPosition().lng() ],
								], 'blue', true);
						});
					});
				}
			}
		}
	});

	//fix this shit
	$('#js-address').geocomplete({
		types: ['geocode']
	});


	$('#js-address').focusout(function(){
		console.log('focus out');
		var that = this;
		setTimeout(function(){
			$('#js-address').trigger('geocode');
		}, 500);
	});

})

'use strict'

angular.module('Order', [])
	.factory('address')
	.controller('OrderController', function($scope){
		$scope.address = '';
		$scope.search = function(){

		}
	});
function ProviderLocation(){
	var _id;
	var _latLng = []
	var _tag = 'providerLocation';
	var _arrService = [];

	this.setId = function(id){ _id = id; }
	this.getId = function() { return _id; }
	this.setLatlng = function(latLng){ _latLng = latLng; }
	this.getLatlng = function(){ return _latLng; }
	this.addService = function(service){ _arrService.push(service);	}
	this.setArrService = function(arrService){ _arrService = arrService; }
	this.getArrService = function(){ return _arrService; }
	this.isServiceExists = function(service){
		var isExists = false;
		var i = 0;
		while(!isExists && i < _arrService.length){
			if(_arrService[i].getId() === service.getId()){
				isExists = true;
			}
			i++;
		}
		return isExists;
	}
	this.toJsonObj = function(){
		var arrServiceJsonObj = [];

		for(var i = 0; i < _arrService.length; i++){
			arrServiceJsonObj.push(_arrService[i].toJsonObj());
		}

		return {
			id: _id,
			latLng: _latLng,
			tag: _tag,
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
	var _name;
	var _price;
	var _providerLocationId;

	this.getId = function(){ return _id; }
	this.setId = function(id){ _id = id; }
	this.getName = function(){ return _name; }
	this.setName = function(name){ _name = name; }
	this.getPrice = function(){ return _price; }
	this.setPrice = function(price){ _price = price; }
	this.setProviderLocationId = function(id){ _providerLocationId = id; }
	this.getProviderLocationId = function(){ return _providerLocationId; }
	this.toJsonObj = function(){
		return {
			id: _id,
			name: _name,
			price: _price,
			providerLocationId: _providerLocationId
		}
	}
}