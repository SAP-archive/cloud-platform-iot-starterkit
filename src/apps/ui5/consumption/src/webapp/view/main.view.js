sap.ui.jsview("odataconsumption.view.main", {

	/** Specifies the Controller belonging to this View. 
	 * In the case that it is not implemented, or that "null" is returned, this View does not have a Controller.
	 * @memberOf controller.main
	 */
	getControllerName: function() {
		return "odataconsumption.controller.main";
	},

	/** Is initially called once after the Controller has been instantiated. It is the place where the UI is constructed. 
	 * Since the Controller is given to this method, its event handlers can be attached right away.
	 * @memberOf controller.main
	 */
	createContent: function(oController) {
		var oToolbar = new sap.m.Toolbar({
			content: [
				new sap.m.Title({
					text: "{i18n>TEXT_OUTBOUND_BEG}"
				}),
				this.createDeviceSelect(oController),
				this.createMessageTypeSelect(oController),
				new sap.m.Title({
					text: "{i18n>TEXT_OUTBOUND_END}"
				})
			]
		});

		var oPanel = new sap.m.Panel({
			headerToolbar: oToolbar,
			content: this.createVizFrame()
		});

		var oPage = new sap.m.Page({
			title: "{i18n>TITLE}",
			content: [oPanel]
		});

		var app = new sap.m.App("myApp", {
			initialPage: "oPage"
		});
		app.addPage(oPage);
		return app;
	},

	createDeviceSelect: function(oController) {
		var oItem = new sap.ui.core.Item({
			key: "{devices>id}",
			text: "{devices>name}"
		});

		var oSelect = new sap.m.Select({
			tooltip: "{i18n>TOOLTIP_DEVICES}",
			width: "150px",
			enabled: {
				path: "devices>/",
				formatter: oController.formatSelectEnablement
			},
			selectedKey: "{viewModel>/selectedDeviceId}",
			change: [oController.onDeviceChange, oController]
		});

		oSelect.bindItems("devices>/", oItem);

		return oSelect;
	},

	createMessageTypeSelect: function(oController) {
		var oItem = new sap.ui.core.Item({
			key: "{viewModel>id}",
			text: "{viewModel>name}"
		});

		var oSelect = new sap.m.Select({
			tooltip: "{i18n>TOOLTIP_MESSAGE_TYPES}",
			width: "150px",
			enabled: {
				path: "viewModel>/messageTypes",
				formatter: oController.formatSelectEnablement
			},
			selectedKey: "{viewModel>/selectedMessageTypeId}",
			change: [oController.onMessageTypeChange, oController]
		});

		oSelect.bindItems("viewModel>/messageTypes", oItem);

		return oSelect;
	},

	createDimensionFeed: function() {
		return new sap.viz.ui5.controls.common.feeds.FeedItem({
			"uid": "timeAxis",
			"type": "Dimension",
			"values": ["timestamp"]
		});
	},

	createMeasureFeed: function() {
		return new sap.viz.ui5.controls.common.feeds.FeedItem({
			"uid": "primaryValues",
			"type": "Measure",
			"values": ["value"]
		});
	},

	createDataSet: function(oController) {

		var oDataset = new sap.viz.ui5.data.FlattenedDataset({
			dimensions: [{
				name: "timestamp",
				value: {
					path: "odata>C_TIMESTAMP",
					formatter: function(oValue) {
						return oController.formatDate(oValue);
					}
				},
				dataType: "date"
			}],
			measures: [{
				name: "value",
				value: "{odata>C_VALUE}"
			}]
		});

		oController.oDataset = oDataset;
		return oDataset;
	},

	createVizFrame: function() {
		var oController = this.getController();

		var oVizFrame = new sap.viz.ui5.controls.VizFrame({
			width: "100%",
			height: oController.calculateChartHeight(),
			vizType: "timeseries_line",
			vizProperties: {
				plotArea: {
					dataLabel: {
						visible: false
					}
				},
				legend: {
					visible: false
				},
				title: {
					visible: false
				},
				timeAxis: {
					title: {
						visible: true
					},
					levels: ["day", "month", "year"],
					levelConfig: {
						month: {
							formatString: "MM"
						},
						year: {
							formatString: "yyyy"
						}
					},
					interval: {
						unit: ''
					}
				}
			},
			timeAxis: {
				title: {
					visible: false
				}
			},
			yAxis: new sap.viz.ui5.types.Axis({
				scale: new sap.viz.ui5.types.Axis_scale({
					fixedRange: true,
					minValue: 0,
					maxValue: 100
				})
			}),
			dataset: this.createDataSet(oController)
		});
		oVizFrame.addFeed(this.createDimensionFeed());
		oVizFrame.addFeed(this.createMeasureFeed());

		$(window).resize(function() {
			oVizFrame.setHeight(oController.calculateChartHeight());
		});

		return oVizFrame;
	}
});