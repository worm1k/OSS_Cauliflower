function ServiceOrder(){
	var _id;
	var _orderStatusId;
	var _orderStatus;
	var _serviceInstanceId;
	var _orderScenarioId;
	var _orderScenario;
	var _userId;
	var _data;

	this.getId = function(){ return _id; }
	this.setId = function(id){ _id = id; }

	this.getOrderStatusId = function(){ return _orderStatusId; }
	this.setOrderStatusId = function(orderStatusId){ _orderStatusId = orderStatusId; }

	this.getOrderStatus = function(){ return _orderStatus; }
	this.setOrderStatus = function(orderStatus){ _orderStatus = orderStatus; }

	this.getServiceInstanceId = function(){ return _serviceInstanceId; }
	this.setServiceInstanceId = function(serviceInstanceId){ _serviceInstanceId = serviceInstanceId; }

	// this.getServiceInstance = function(){ return _serviceInstance; }
	// this.setServiceInstance = function(serviceInstance){ _serviceInstance = serviceInstance; }

	this.getOrderScenarioId = function(){ return _orderScenarioId; }
	this.setOrderScenarioId = function(orderScenarioId){ _orderScenarioId = orderScenarioId; }

	this.getOrderScenario = function(){ return _orderScenario; }
	this.setOrderScenario = function(orderScenario){ _orderScenario = orderScenario; }

	this.getUserId = function(){ return _userId; }
	this.setUserId = function(userId){ _userId = userId; }

	this.getData = function(){ return _data; }
	this.setData = function(data){ _data = data; }

	this.toJsonObj = function(){
		return {
			id: _id,
			orderStatusId: _orderStatusId,
			orderStatus: _orderStatus,
			serviceInstanceId: _serviceInstanceId,
			orderScenarioId: _orderScenarioId,
			orderScenario: _orderScenario,
			userId: _userId,
			data: _data,
		}
	}
}