function ServiceInstance(){
	var _id;
	var _userId;
	var _serviceLocation;
	var _serviceId;
	var _instanceStatusId;
	var _instanceStatus;
	var _cableId;
	var _isBlocked;
	var _arrServiceOrder = [];
	var _providerLocation;

	this.getId = function(){ return _id; }
	this.setId = function(id){ _id = id; }

	this.getUserId = function(){ return _userId; }
	this.setUserId = function(userId){ _userId = userId; }

	this.getServiceLocation = function(){ return _serviceLocation; }
	this.setServiceLocation = function(serviceLocation){ _serviceLocation = serviceLocation; }

	this.getServiceId = function(){ return _serviceId; }
	this.setServiceId = function(serviceId){ _serviceId = serviceId; }

	this.getInstanceStatusId = function(){ return _instanceStatusId; }
	this.setInstanceStatusId = function(instanceStatusId){ _instanceStatusId = instanceStatusId; }

	this.getInstanceStatus = function(){ return _instanceStatus; }
	this.setInstanceStatus = function(instanceStatus){ _instanceStatus = instanceStatus; }

	this.getCableId = function(){ return _cableId; }
	this.setCableId = function(cableId){ _cableId = cableId; }

	this.getIsBlocked = function(){ return _isBlocked; }
	this.setIsBlocked = function(isBlocked){ _isBlocked = isBlocked; }

	this.getArrServiceOrder = function(){ return _arrServiceOrder; }
	this.setArrServiceOrder = function(arrServiceOrder){ _arrServiceOrder = arrServiceOrder; }
	this.addServiceOrder = function(serviceOrder){ _arrServiceOrder.push(serviceOrder); }

	this.getProviderLocation = function(){ return _providerLocation; }
	this.setProviderLocation = function(providerLocation){ _providerLocation = providerLocation; }

	this.toJsonObj = function(){
		var arrServiceOrder = [];

		for(var i = 0; i < _arrServiceOrder.length; i++){
			arrServiceOrder.push(_arrServiceOrder[i].toJsonObj());
		}

		return {
			id: _id,
			userId: _userId,
			serviceLocation: _serviceLocation,
			serviceId: _serviceId,
			instanceStatusId: _instanceStatusId,
			instanceStatus: _instanceStatus,
			cableId: _cableId,
			isBlocked: _isBlocked,
			arrServiceOrder: arrServiceOrder,
			providerLocation: _providerLocation 
		}
	}
}