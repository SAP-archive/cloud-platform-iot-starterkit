sap.ui.jsview( "js.view.app", {

	createContent: function( oController ) {
		this.setDisplayBlock( true );
		return new sap.m.App( "iotstarterkitapp" );
	}

} );