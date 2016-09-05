sap.ui.jsview( "js.view.main", {

	getControllerName: function() {
		return "js.controller.main";
	},

	createContent: function( oController ) {
		this.setDisplayBlock( true );

		var oOutboundView = sap.ui.jsview( "js.view.outbound" );
		var oInboundView = sap.ui.jsview( "js.view.inbound" );

		return new sap.m.Page( {
			title: "{i18n>TITLE}",
			content: [ oOutboundView, oInboundView ]
		} );
	}

} );