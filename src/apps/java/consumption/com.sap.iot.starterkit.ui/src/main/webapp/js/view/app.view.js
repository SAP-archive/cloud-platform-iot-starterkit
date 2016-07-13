sap.ui.jsview( "js.view.app", {

	getControllerName: function() {
		return "js.controller.app";
	},

	createContent: function( oController ) {
		this.setDisplayBlock( true );
		return new sap.m.App( 'iotstarterkitapp' );
	}

} );