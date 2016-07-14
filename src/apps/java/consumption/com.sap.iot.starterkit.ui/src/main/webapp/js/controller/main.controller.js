jQuery.sap.require( "js.base.Controller" );

js.base.Controller.extend( "js.controller.main", {

	oDeviceInterval: undefined,

	onInit: function() {
		var that = this;

		this.getView().setModel( new sap.ui.model.json.JSONModel(), "device" );

		$( document ).ready( function() {
			document.title = that.getText( "TITLE" );
		} );

		var successHandler = function( oData, textStatus, jqXHR ) {
			oData.unshift( {
				id: "placeholder",
				name: that.getText( "TEXT_SELECT" )
			} );

			that.getView().getModel( "device" ).setData( oData );
		};

		var errorHandler = function( jqXHR, textStatus, errorThrown ) {
			clearInterval( that.oDeviceInterval );
		};

		var sUrl = "rdms/v2/api/devices";

		that.doGet( sUrl, successHandler );
		that.oDeviceInterval = setInterval( function() {
			that.doGet( sUrl, successHandler, errorHandler );
		}, 5000 );
	}

} );