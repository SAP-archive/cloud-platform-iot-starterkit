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
				selectedDeviceName: "",
				selectedMessageTypeId: "",
				measures: "value",
				messageTypes: [],
				autoRefresh: false
			});
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
		 * Return a promise, which is resolved, when all models are loaded
		 */
		loadRDMSModels: function(deviceModel, deviceTypeModel, measureModel) {
			var oDevicePromise = this.createModelPromise(deviceModel, '/iotrdms/v2/api/devices');
			var oDeviceTypesPromise = this.createModelPromise(deviceTypeModel, '/iotrdms/v2/api/deviceTypes');
			var oMeasurePromise = this.createModelPromise(measureModel, '/iotrdms/v2/api/messageTypes');
			return Promise.all([oDevicePromise, oDeviceTypesPromise, oMeasurePromise]);
		},

		/**
		 * Create a promise that is resolved, when the model data are loaded.
		 *
		 * @param oModel the device model
		 * @param sUri uri path to load the model data from
		 * @returns {Promise}
		 */
		createModelPromise: function(oModel, sUri) {
			return new Promise(function(resolve, reject) {
				oModel.attachRequestCompleted(function() {
					resolve(oModel.getData());
				});
				oModel.loadData(sUri);
			});
		}
	};
});