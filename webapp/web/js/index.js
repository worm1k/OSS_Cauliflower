'use strict'

$(document).ready(function(){
    $('#js-logout-lnk').click(function(e){
        e.preventDefault();
        $('#js-logout-form').submit();
    });
});

function User(){
    var _id;
    var _email;
    var _fName;
    var _lName;
    var _phone;
    var _role;
    var _roleId;

    this.getId = function(){ return _id; }
    this.setId = function(id){ _id = id; }

    this.getEmail = function(){ return _email; }
    this.setEmail = function(email){ _email = email; }

    this.getFName = function(){ return _fName; }
    this.setFName = function(fName){ _fName = fName; }

    this.getLName = function(){ return _lName; }
    this.setLName = function(lName){ _lName = lName; }

    this.getPhone = function(){ return _phone; }
    this.setPhome = function(phone){ _phone = phone; }

    this.getRole = function(){ return _role; }
    this.setRole = function(role){ _role = role; }

    this.getRoleId = function(){ return _roleId; }
    this.setRoleId = function(roleId){ _roleId = roleId; }
}

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

angular.module('NgApp', [])
    .controller('MapOrderController', function($scope){
        $scope.serviceLocationAddress;
        $scope.providerLocationAddress;
        $scope.arrService = [];
        $scope.serviceId = 0;
        $scope.isOpenedInfobox = false;
        $scope.gmap = $("#js-map").gmap3();

        $scope.mapSetActiveMarkerLocationByAddress = function(){
            console.log($scope.gmap);
            console.log('ng-click fn');

            console.log($scope.serviceLocationAddress);

            mapGetLatLngByAddress($scope.gmap,$scope.serviceLocationAddress, function(result){
                if(result){
                    console.log(result && result[0]);
                    console.log(result[0].formatted_address);
                    // $scope.$apply(function(){ $scope.serviceLocationAddress = result[0].formatted_address; });
                    // mapSetMarkerLocation($scope.gmap, 'activeMarker', new google.maps.LatLng(result[0].geometry.location.lat(),result[0].geometry.location.lng()), false, 0);
                }
            });
        }

        var infobox = new InfoBox();

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
                console.log(latLng);
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

        function mapOpenInfobox(gmap, marker){
            if(!$scope.isOpenedInfobox){
                var map = gmap.gmap3("get");
                infobox = new InfoBox({
                    content: $('#js-infobox')[0].outerHTML,
                    disableAutoPan: true,
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
                mapCenterCamera(gmap, [marker.position.lat(),marker.position.lng()], true);
                $scope.isOpenedInfobox = true;

                //infowindow event handler
                google.maps.event.addListener(infobox, 'closeclick', function(event){
                    $scope.isOpenedInfobox = false;
                });

                google.maps.event.addListener(infobox, 'domready', function() {
                    $('#js-proceed-to-order').click(function(){
                        var serviceId = $('input[name="serviceId"]:checked').val();

                        $('#js-order-form input[name="serviceLocationAddress"]').val($scope.serviceLocationAddress);
                        $('#js-order-form input[name="serviceLocationLongtitude"]').val(marker.position.lng());
                        $('#js-order-form input[name="serviceLocationLatitude"]').val(marker.position.lat());
                        $('#js-order-form input[name="serviceId"]').val(serviceId);

                        $("#js-order-form").submit();
                    })
                });
            }
        }

        function mapCloseInfobox(gmap){
            if($scope.isOpenedInfobox){
                infobox.close();
                $scope.isOpenedInfobox = false;
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
        function mapSetServiceOptions (marker){
            console.log(marker);
            console.log(marker.data.toJsonObj());
            console.log(marker.data.getArrService());

            $scope.$apply(function(){ $scope.providerLocationAddress = marker.data.getLocationAddress() });
            $scope.$apply(function(){ $scope.arrService = marker.data.getArrService() });

        }

        function mapClickEvent(map, event, context){
            var closest;

            mapCloseInfobox(this);

            // this is not good style
            mapGetMarkers(this, 'activeMarker', function(marker){
                if(marker.length > 0){
                    mapSetMarkerLocation(this, 'activeMarker', event.latLng, false, 0);
                    mapGetAddressByLatLng(this, event.latLng, function(addr){
                        if(addr && addr[0]){
                            $scope.$apply(function(){ $scope.serviceLocationAddress = addr[0].formatted_address; });
                        }
                    });
                    mapGetMarkers(this, 'providerLocation', function(markers){
                        closest = findClosest(marker[0], markers);
                        mapDrawPolyline($scope.gmap, [
                            [ marker[0].object.position.lat(), marker[0].object.position.lng() ],
                            [ closest.marker.object.position.lat(), closest.marker.object.position.lng() ],
                        ], 'blue', true);

                        mapSetServiceOptions(closest.marker);
                        mapOpenInfobox(this, marker[0].object);
                    });
                }else{
                    activeMarker.getValues().latLng[0] = event.latLng.lat();
                    activeMarker.getValues().latLng[1] = event.latLng.lng();

                    mapAddMarker($scope.gmap, activeMarker);

                    mapGetMarkers(this, 'activeMarker', function(marker){
                        mapGetMarkers(this, 'providerLocation', function(markers){
                            closest = findClosest(marker[0], markers);
                            mapDrawPolyline($scope.gmap, [
                                [ marker[0].object.position.lat(), marker[0].object.position.lng() ],
                                [ closest.marker.object.position.lat(), closest.marker.object.position.lng() ],
                            ], 'blue', true);
                            mapSetServiceOptions(closest.marker);
                            mapOpenInfobox(this, marker[0].object);
                        });
                    });
                }
            });
        }

        function ajaxGetServices(callback){
            $.ajax({
                type: 'GET',
                url: 'services',
                dataType: 'json',
                success: function(jqXHR){
                    console.log(jqXHR);
                    if(jqXHR && jqXHR[0]){
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

        var serviceLocation = {};
        var lastNearestProviderLocation;
        var markerIcons = {
            red: new google.maps.MarkerImage("img/icons/marker_red.png"),
            green: new google.maps.MarkerImage("img/icons/marker_green.png"),
            blue: new google.maps.MarkerImage("img/icons/marker_blue.png"),
            grey: new google.maps.MarkerImage("img/icons/marker_grey.png")
        }

        //Active Marker Initialization
        var activeMarker = new MapMarker();
        activeMarker.setValues({
            latLng: [0,0],
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
                console.log("marker");
                console.log(marker);

                mapOpenInfobox($scope.gmap, marker);
            }
        });

        var arrProviderLocationMarker = [];
        var arrProviderLocation = [];
        var arrProviderLocationData = [];

        ajaxGetServices(function(arrService){
            //temporary
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
                        zoomControl: true
                    }
                }
            };

            $(document).ready(function(){
                //init
                $scope.gmap.gmap3({
                    map: gmapOptions.map
                });

                mapAddMarkers($scope.gmap, arrProviderLocationMapMarker);

                //request geolocation
                $scope.gmap.gmap3({
                    getgeoloc: {
                        callback: function(latLng){
                            if(latLng){
                                console.log(latLng);

                                activeMarker.getValues().latLng[0] = latLng.lat();
                                activeMarker.getValues().latLng[1] = latLng.lng();

                                mapAddMarker($scope.gmap, activeMarker);

                                mapZoomCamera($scope.gmap, 14);
                                mapCenterCamera($scope.gmap, activeMarker.getValues().latLng, true);

                                mapGetAddressByLatLng(this, latLng, function(addr){
                                    if(addr && addr[0]){
                                        $scope.$apply(function(){ $scope.serviceLocationAddress = addr[0].formatted_address; });
                                    }
                                });

                                mapGetMarkers(this, 'activeMarker', function(marker){
                                    mapGetMarkers(this, 'providerLocation', function(markers){
                                        var closest = findClosest(marker[0], markers);

                                        mapDrawPolyline($scope.gmap, [
                                            [ marker[0].object.position.lat(), marker[0].object.position.lng() ],
                                            [ closest.marker.object.position.lat(), closest.marker.object.position.lng() ],
                                        ], 'blue', true);

                                        mapSetServiceOptions(closest.marker);

                                        mapOpenInfobox(this, marker[0].object);
                                    });
                                });

                            }
                        }
                    }
                });

                //google map
                $scope.gmap.gmap3({
                    map: {
                        events: {
                            click: function(map, event, context){
                                var closest;

                                mapCloseInfobox(this);

                                // this is not good style
                                mapGetMarkers(this, 'activeMarker', function(marker){
                                    console.log(marker);
                                    console.log(marker.length > 0);
                                    if(marker.length > 0){
                                        mapSetMarkerLocation(this, 'activeMarker', event.latLng, false, 0);
                                        mapGetAddressByLatLng(this, event.latLng, function(addr){
                                            if(addr && addr[0]){
                                                $scope.$apply(function(){ $scope.serviceLocationAddress = addr[0].formatted_address; });
                                            }
                                        });
                                        mapGetMarkers(this, 'providerLocation', function(markers){
                                            closest = findClosest(marker[0], markers);
                                            mapDrawPolyline($scope.gmap, [
                                                [ marker[0].object.position.lat(), marker[0].object.position.lng() ],
                                                [ closest.marker.object.position.lat(), closest.marker.object.position.lng() ]
                                            ], 'blue', true);

                                            mapSetServiceOptions(closest.marker);
                                            mapOpenInfobox(this, marker[0].object);
                                        });
                                    }else{
                                        activeMarker.getValues().latLng[0] = event.latLng.lat();
                                        activeMarker.getValues().latLng[1] = event.latLng.lng();

                                        mapAddMarker($scope.gmap, activeMarker);

                                        mapGetMarkers(this, 'activeMarker', function(marker){
                                            mapGetMarkers(this, 'providerLocation', function(markers){
                                                closest = findClosest(marker[0], markers);
                                                mapDrawPolyline($scope.gmap, [
                                                    [ marker[0].object.position.lat(), marker[0].object.position.lng() ],
                                                    [ closest.marker.object.position.lat(), closest.marker.object.position.lng() ],
                                                ], 'blue', true);
                                                mapSetServiceOptions(closest.marker);
                                                mapOpenInfobox(this, marker[0].object);
                                            });
                                        });
                                    }
                                });
                            }
                        }
                    }
                });

                // //fix this shit
                // $('#js-address').geocomplete()
                // .bind('geocode:result', function(event, result){
                // 	console.log(result);
                // 	console.log(result.geometry.location.lat(), result.geometry.location.lng());
                // });


                // $('#js-address').focusout(function(){
                // 	console.log('focus out');
                // 	var that = this;
                // 	setTimeout(function(){
                // 		$('#js-address').trigger('geocode');
                // 	}, 500);
                // });

            });
        });
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