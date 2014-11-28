'use strict'

var gmap;
var serviceLocation = {};
var markerProviderLocationOptions = {
	icon: new google.maps.MarkerImage("http://localhost:8080/img/icons/marker_red.png")
};
var markerServiceLocationOptions = {
	icon: new google.maps.MarkerImage("http://localhost:8080/img/icons/marker_green.png")
};
var gmapOptions = {
	map: {
		options: {
			mapTypeControl: false,
			streetViewControl: false,
			panControl: true,
			zoomControl: true,
			// panControlOptions: {
			// 	position: google.maps.ControlPosition.LEFT_TOP
			// }
			// ,
			// zoomControlOptions: {
			// 	position: google.maps.ControlPosition.LEFT_CENTER
			// }
		}
	},
	markers: {
		providerLocation: {
			values: [
				{latLng: [50.50551183400235,30.623188018798828], id: 0, data: 'id 0', options: markerProviderLocationOptions },
				{latLng: [50.5159916936212,30.607738494873047], id: 1, data: 'id 1', options: markerProviderLocationOptions },
				{latLng: [50.5159916936212,30.623188018798828], id: 2, data: 'id 2', options: markerProviderLocationOptions },
				{latLng: [50.505675599698684,30.607738494873047], id: 3, data: 'id 3', options: markerProviderLocationOptions }
			]
		}
	}
};

$(document).ready(function(){
	//init
	gmap = $("#js-map").gmap3({
		map: gmapOptions.map,
		marker: gmapOptions.markers.providerLocation
	});

	//request geolocation
	gmap.gmap3({
		getgeoloc: {
			callback: function(latLng){
				if(latLng){
					console.log(latLng);

					$(this).gmap3({
						marker: {
							latLng: latLng,
							options: serviceLocation
						},
						map: {
							options: {
								center: latLng,
								zoom: 14
							}
						}
					});
				}
			}
		}
	});

	//google map events
	gmap.gmap3({
		map: {
			events: {
				click: function(map, event, context){
					console.log(event.latLng);
				}
			}
		}
	});
})


function ResetPasswordController($scope){
	$scope.isLetterSent = false;

	$scope.sendLetter = function(){
		$scope.isLetterSent = true;
	}
}