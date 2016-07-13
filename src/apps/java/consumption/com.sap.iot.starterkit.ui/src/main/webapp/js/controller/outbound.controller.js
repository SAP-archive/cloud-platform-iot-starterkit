jQuery.sap.require( "sap.ui.core.util.Export" );
jQuery.sap.require( "sap.ui.core.util.ExportTypeCSV" );

js.base.Controller.extend( "js.controller.outbound", {

	oMessageInterval: undefined,

	onInit: function() {
		this.getView().setModel( new sap.ui.model.json.JSONModel(), "message" );
		this.getView().setModel( new sap.ui.model.json.JSONModel(), "data" );

		// console.debug( "init js.controller.outbound" );
	},

	onDeviceSelectChange: function( oEvent ) {
		var that = this;

		clearInterval( that.oMessageInterval );

		var oSelectedItem = oEvent.getParameter( "selectedItem" );
		var sKey = oSelectedItem.getKey();
		if ( "placeholder" === sKey ) {

			console.log( "device placeholder selected" );

			that.getView().getModel( "message" ).setData( [] );
			that.getView().oMessageSelect.setSelectedItem( null );
			that.getView().oMessageSelect.setSelectedItemId( undefined );
			that.getView().oMessageSelect.setSelectedKey( undefined );

			that.getView().getModel( "data" ).setData( [] );
			that.getView().oExportButton.setEnabled( false );

			return;
		}

		var sDeviceType = oSelectedItem.getCustomData()[0].getValue();

		var successHandler = function( oData, textStatus, jqXHR ) {
			var oFilteredData = oData.filter( function( next ) {
				return sDeviceType === next.id;
			} );

			// console.debug( that.getBindingPathById( sDeviceType, oData ) );

			oFilteredData[0].messageTypes.unshift( {
				id: "placeholder",
				name: that.getText( "TEXT_SELECT" ),
				direction: "bidirectional"
			} );

			that.getView().oMessageSelect.bindElement( "message>/0" );
			that.getView().getModel( "message" ).setData( oFilteredData );
		};

		// console.warn( "change url to " + "data/messagetypes/".concat( sDeviceType ) );
		var sUrl = "data/messagetypes/".concat( sDeviceType );
		// var sUrl = "message.json";

		this.doGet( sUrl, successHandler );
	},

	onMessageSelectChange: function( oEvent ) {
		var that = this;

		var oSelectedItem = oEvent.getParameter( "selectedItem" );
		var sKey = oSelectedItem.getKey();

		clearInterval( that.oMessageInterval );

		if ( "placeholder" === sKey ) {

			console.log( "message placeholder selected" );

			that.getView().getModel( "data" ).setData( [] );
			that.getView().oExportButton.setEnabled( false );

			return;
		}

		var successHandler = function( oData, textStatus, jqXHR ) {
			oData = oData.reverse();

			that.getView().getModel( "data" ).setData( oData );
			that.getView().oExportButton.setEnabled( true );
		};

		var errorHandler = function( jqXHR, textStatus, errorThrown ) {
			that.getView().getModel( "data" ).setData( [] );
			that.getView().oExportButton.setEnabled( false );

			clearInterval( that.oMessageInterval );
		};

		var sDevice = this.getView().oDeviceSelect.getSelectedItem().getKey();
		var sDeviceType = this.getView().oDeviceSelect.getSelectedItem().getCustomData()[0].getValue();
		var sMessageType = sKey;

		// console.warn( "change url to " + "data/table/".concat( sDevice, "/", sDeviceType, "/", sMessageType
		// ) );
		var sUrl = "data/table/".concat( sDevice, "/", sDeviceType, "/", sMessageType );
		// var sUrl = "data.json";

		that.oMessageInterval = setInterval( function() {
			that.doGet( sUrl, successHandler, errorHandler );
		}, 1000 );
	},

	onExportButtonPress: function( oEvent ) {
		var oExport = new sap.ui.core.util.Export( {
			exportType: new sap.ui.core.util.ExportTypeCSV( {
				separatorChar: ","
			} ),
			models: this.getView().getModel( "data" ),
			rows: {
				path: "/"
			},
			columns: [ {
				name: "Device",
				template: {
					content: {
						path: "G_DEVICE"
					}
				}
			}, {
				name: "Created",
				template: {
					content: {
						path: "G_CREATED"
					}
				}
			}, {
				name: "Sensor",
				template: {
					content: {
						path: "C_SENSOR"
					}
				}
			}, {
				name: "Value",
				template: {
					content: {
						path: "C_VALUE"
					}
				}
			}, {
				name: "Timestamp",
				template: {
					content: {
						path: "C_TIMESTAMP"
					}
				}
			} ]
		} );

		oExport.saveFile().always( function() {
			this.destroy();
		} );
	},

	formatDate: function( oValue ) {
		var oDate = null;
		// can be a string primitive in JSON, but we need a number
		if ( (typeof oValue) === "string" ) {
			// backward compatibility, old type was long, new type is date
			// check if not a number
			var result = isNaN( Number( oValue ) );
			if ( result ) {
				// FF and Ie cannot create Dates using 'DD-MM-YYYY HH:MM:SS.ss' format but
				// 'DD-MM-YYYYTHH:MM:SS.ss'
				oValue = oValue.replace( " ", "T" );
				// this is a date type
				oDate = new Date( oValue );
			} else {
				// this is a long type
				oValue = parseInt( oValue );
				// ensure that UNIX timestamps are converted to milliseconds
				oDate = new Date( oValue * 1000 );
			}
		} else {
			// ensure that UNIX timestamps are converted to milliseconds
			oDate = new Date( oValue * 1000 );
		}
		return oDate.toLocaleString();
	},

	calculateChartHeight: function() {
		var pageHeaderHeight = 48;
		var consumptionPanelHeight = 48;
		var pushPanelHeight = 48;
		var listHeight = 129;
		var margins = 42;

		var staticHeight = pageHeaderHeight + consumptionPanelHeight + pushPanelHeight + listHeight + margins;
		var windowHeight = $( window ).height();

		return (windowHeight - staticHeight) + "px";
	}

} );