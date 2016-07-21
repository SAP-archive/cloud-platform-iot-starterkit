sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"sap/ui/model/json/JSONModel",
	"odataconsumption/model/models"
], function(Controller, JSONModel, Models) {
	"use strict";

	return Controller.extend("odataconsumption.controller.main", {

		_oViewModel: null,
		_oDeviceModel: null,
		_oDeviceTypesModel: null,
		_oDataModel: null,

		/**
		 * This function is automatically called on startup
		 */
		onInit: function() {

			// Create models
			this._oViewModel = Models.createViewModel();
			this._oDeviceModel = Models.createDevicesModel();
			this._oDeviceTypesModel = Models.createDeviceTypesModel();
			this._oDataModel = Models.createODataModel();

			// Set models to view
			this.getView().setModel(this._oViewModel, "viewModel");
			this.getView().setModel(this._oDeviceModel, "devices");
			this.getView().setModel(this._oDeviceTypesModel, "deviceTypes");
			this.getView().setModel(this._oDataModel, "odata");

			// Create promises to receive all devices and deviceTypes from RDMS
			var oDevicePromise = Models.createDeviceModelPromise(this._oDeviceModel);
			var oDeviceTypesPromise = Models.createDeviceTypesModelPromise(this._oDeviceTypesModel);

			// Wait until all promises are resolved and set default selection
			Promise.all([oDevicePromise, oDeviceTypesPromise]).then(function(mValues) {
				// Get devices
				var mDevices = mValues[0];

				if (mDevices.length > 0) {
					// Set selection to first device
					this.changeDevice(mDevices[0].id);
					// Initialize chart
					this.setChart();
				}
			}.bind(this));
		},

		/**
		 * Eventhandler that is called when the user selects a different device
		 *
		 * @param oEvent
		 */
		onDeviceChange: function(oEvent) {
			var oSelectedItem = oEvent.getParameter("selectedItem");
			var sId = oSelectedItem.getKey();
			// Set selected device
			this.changeDevice(sId);
			// Initialize chart
			this.setChart();
		},

		/**
		 * Eventhandler that is called when the user select a different message type
		 *
		 * @param oEvent
		 */
		onMessageTypeChange: function(oEvent) {
			var oSelectedItem = oEvent.getParameter("selectedItem");
			var sId = oSelectedItem.getKey();
			// Set selected message type
			this._oViewModel.setProperty('/selectedMessageTypeId', sId);
			// Initialize chart
			this.setChart();
		},

		/**
		 * Should be called when the device ID changes
		 *
		 * @param sId the ID of a device
		 */
		changeDevice: function(sId) {
			var oDevice = this.getDeviceById(sId);
			var oDeviceType = this.getDeviceTypeById(oDevice.deviceType);

			this._oViewModel.setProperty('/messageTypes', oDeviceType.messageTypes);
			this._oViewModel.setProperty('/selectedMessageTypeId', oDeviceType.messageTypes[0].id);
			this._oViewModel.setProperty('/selectedDeviceId', oDevice.id);
		},

		/**
		 * Get the deviceType by ID from the deviceType model
		 *
		 * @param sId the id of a device
		 * @returns {DeviceType}
		 */
		getDeviceTypeById: function(sId) {
			var mDeviceTypes = this._oDeviceTypesModel.getData();
			var mFilteredDeviceTypes = mDeviceTypes.filter(function(next) {
				return sId === next.id;
			});

			return mFilteredDeviceTypes[0];
		},

		/**
		 * Get the device by ID from the device model
		 *
		 * @param sId the ID of a device
		 * @returns {Device}
		 */
		getDeviceById: function(sId) {
			var mDevices = this._oDeviceModel.getData();
			var mFilteredDevices = mDevices.filter(function(next) {
				return sId === next.id;
			});

			return mFilteredDevices[0];
		},

		/**
		 * Set the chart to the current selected device and message type
		 */
		setChart: function() {
			var sDeviceId = this._oViewModel.getProperty('/selectedDeviceId');
			var sMessageTypeId = this._oViewModel.getProperty('/selectedMessageTypeId').toUpperCase();

			this.oDataset.bindAggregation("data", {
				path: "odata>/T_IOT_" + sMessageTypeId,
				filters: [
					new sap.ui.model.Filter("G_DEVICE", sap.ui.model.FilterOperator.EQ, sDeviceId)
				]
			});
		},

		/**
		 * Calculates the height of the chart
		 *
		 * @returns {string}
		 */
		calculateChartHeight: function() {
			var pageHeaderHeight = 48;
			var consumptionPanelHeight = 48;
			var pushPanelHeight = 48;
			var listHeight = 129;
			var margins = 42;

			var staticHeight = pageHeaderHeight + consumptionPanelHeight + pushPanelHeight + listHeight + margins;
			var windowHeight = $(window).height();

			return (windowHeight - staticHeight) + "px";
		},

		/**
		 * Formats the date object that is shown in chart
		 *
		 * @param oValue the value to be date formatted
		 * @returns {string} a date formatted string
		 */
		formatDate: function(oValue) {
			var oDate = null;
			// can be a string primitive in JSON, but we need a number
			if ((typeof oValue) === "string") {
				// backward compatibility, old type was long, new type is date
				// check if not a number
				var result = isNaN(Number(oValue));
				if (result) {
					// FF and Ie cannot create Dates using 'DD-MM-YYYY HH:MM:SS.ss' format but
					// 'DD-MM-YYYYTHH:MM:SS.ss'
					oValue = oValue.replace(" ", "T");
					// this is a date type
					oDate = new Date(oValue);
				} else {
					// this is a long type
					oValue = parseInt(oValue);
					// ensure that UNIX timestamps are converted to milliseconds
					oDate = new Date(oValue * 1000);
				}
			} else {
				// ensure that UNIX timestamps are converted to milliseconds
				oDate = new Date(oValue * 1000);
			}
			return oDate.toLocaleString();
		},

		/**
		 * Formats the enablement of the select fields. If no data is found the select fields are disabled.
		 *
		 * @param oValue the data that is shown in select
		 * @returns {boolean} false if select should be disabled else true
		 */
		formatSelectEnablement: function(oValue) {
			if (oValue === null || oValue === undefined || oValue.length === 0 || jQuery.isEmptyObject(oValue)) {
				return false;
			}
			return true;
		}
	});

});