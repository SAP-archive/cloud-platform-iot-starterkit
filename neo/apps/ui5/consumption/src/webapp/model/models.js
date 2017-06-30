sap.ui.define([
	"sap/ui/model/json/JSONModel",
	"sap/ui/model/odata/v2/ODataModel"
], function(JSONModel, ODataModel) {
	"use strict";

	return {

		/**
		 * Create a view model. This model holds all common information to keep the view running.
		 * @returns {*}
		 */
		createViewModel: function() {
			var oModel = new JSONModel({
				selectedDeviceId: "",
				selectedMessageTypeId: "",
				messageTypes: []
			});
			oModel.setDefaultBindingMode("OneWay");
			return oModel;
		},

		/**
		 * Create the device model. This model holds all device received from RDMS.
		 * @returns {*}
		 */
		createDevicesModel: function() {
			var oModel = new JSONModel({});
			oModel.setDefaultBindingMode("OneWay");
			return oModel;
		},

		/**
		 * Creates the device types model. This model holds all device types received from RDMS.
		 *
		 * @returns {sap.ui.model.json.JSONModel}
		 */
		createDeviceTypesModel: function() {
			var oModel = new JSONModel({});
			oModel.setDefaultBindingMode("OneWay");
			return oModel;
		},

		/**
		 * Creates the oData model to consume MMS
		 *
		 * @returns {sap.ui.model.odata.v2.ODataModel}
		 */
		createODataModel: function() {
			var oModel = new ODataModel("/iotmms/v1/api/http/app.svc/");
			return oModel;
		},

		/**
		 * Create a device model promise
		 *
		 * @param oModel the device model
		 * @returns {Promise}
		 */
		createDeviceModelPromise: function(oModel) {
			return new Promise(function(resolve, reject) {
				oModel.attachRequestCompleted(function() {
					resolve(oModel.getData());
				});
				oModel.loadData('/iotrdms/v2/api/devices');
			});
		},

		/**
		 * Creates a device type model promise
		 *
		 * @param oModel the device type model
		 * @returns {Promise}
		 */
		createDeviceTypesModelPromise: function(oModel) {
			return new Promise(function(resolve, reject) {
				oModel.attachRequestCompleted(function() {
					resolve(oModel.getData());
				});
				oModel.loadData('/iotrdms/v2/api/deviceTypes');
			});
		}
	};

});