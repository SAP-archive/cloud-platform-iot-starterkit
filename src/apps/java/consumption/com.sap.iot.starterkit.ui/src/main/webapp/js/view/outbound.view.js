sap.ui.jsview( "js.view.outbound", {

	getControllerName: function() {
		return "js.controller.outbound";
	},

	createDimensionFeed: function() {
		return new sap.viz.ui5.controls.common.feeds.FeedItem( {
			"uid": "axisLabels",
			"type": "Dimension",
			"values": [ "timestamp" ]
		} );
	},

	createMeasureFeed: function() {
		return new sap.viz.ui5.controls.common.feeds.FeedItem( {
			"uid": "primaryValues",
			"type": "Measure",
			"values": [ "value" ]
		} );
	},

	createDataSet: function() {
		var oController = this.getController();

		return new sap.viz.ui5.data.FlattenedDataset( {
			dimensions: [ {
				name: "timestamp",
				value: {
					path: "data>C_TIMESTAMP",
					formatter: function( oValue ) {
						return oController.formatDate( oValue );
					}
				}
			} ],
			measures: [ {
				name: "value",
				value: "{data>C_VALUE}"
			} ],
			data: {
				path: "data>/"
			}
		} );
	},

	createVizFrame: function() {
		var oController = this.getController();

		var oVizFrame = new sap.viz.ui5.controls.VizFrame( {
			width: "100%",
			height: oController.calculateChartHeight(),
			vizType: "line",
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
				categoryAxis: {
					title: {
						visible: true
					}
				}
			},
			xAxis: {
				title: {
					visible: false
				}
			},
			yAxis: new sap.viz.ui5.types.Axis( {
				scale: new sap.viz.ui5.types.Axis_scale( {
					fixedRange: true,
					minValue: 0,
					maxValue: 100
				} )
			} ),
			dataset: this.createDataSet()
		} );
		oVizFrame.addFeed( this.createDimensionFeed() );
		oVizFrame.addFeed( this.createMeasureFeed() );

		$( window ).resize( function() {
			oVizFrame.setHeight( oController.calculateChartHeight() );
		} );

		return oVizFrame;
	},

	createToolbar: function() {
		var oController = this.getController();

		this.oDeviceSelect = sap.ui.jsfragment( "js.fragment.device", {
			onSelectChange: function( oEvent ) {
				oController.onDeviceSelectChange( oEvent );
			}
		} );

		this.oMessageSelect = sap.ui.jsfragment( "js.fragment.message", {
			onSelectChange: function( oEvent ) {
				oController.onMessageSelectChange( oEvent );
			},
			direction: "fromDevice"
		} );

		this.oExportButton = new sap.m.Button( {
			enabled: false,
			visible: false,
			tooltip: "{i18n>TOOLTIP_EXPORT}",
			icon: "sap-icon://excel-attachment",
			press: function( oEvent ) {
				oController.onExportButtonPress( oEvent );
			}
		} );

		return new sap.m.Toolbar( {
			content: [ new sap.m.Title( {
				text: "{i18n>TEXT_OUTBOUND_BEG}"
			} ), this.oDeviceSelect, this.oMessageSelect, new sap.m.Title( {
				text: "{i18n>TEXT_OUTBOUND_END}"
			} ), new sap.m.ToolbarSpacer(), this.oExportButton ]
		} );
	},

	createContent: function( oController ) {
		return new sap.m.Panel( {
			headerToolbar: this.createToolbar(),
			content: this.createVizFrame()
		} );
	}

} );