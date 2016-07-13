jQuery.sap.require( 'js.base.Controller' );

js.base.Controller.extend( "js.controller.app", {

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

		// var sUrl = "data/devices";
		console.warn( "change url to " + "data/devices" );
		var sUrl = "device.json";

		that.doGet( sUrl, successHandler );
		setInterval( function() {
			that.doGet( sUrl, successHandler );
		}, 5000 );

		console.debug( "init js.controller.app" );
	}

} );