sap.ui.core.UIComponent.extend( "mqttingest.Component", {

	metadata: {
		manifest: "json"
	},

	init: function() {
		sap.ui.core.UIComponent.prototype.init.apply( this, arguments );
		this.getRouter().initialize();
	}

} );
