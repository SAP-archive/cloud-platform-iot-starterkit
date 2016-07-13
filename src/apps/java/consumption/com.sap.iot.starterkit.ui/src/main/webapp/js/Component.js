sap.ui.core.UIComponent.extend( 'iotstarterkit.Component', {

	metadata: {
		routing: {
			config: {
				viewType: "JS",
				viewPath: "js.view",
				controlId: "iotstarterkitapp",
				async: true
			},
			routes: [ {
				pattern: "",
				name: "main",
				target: [ "main" ]
			} ],
			targets: {
				main: {
					viewName: "main",
					viewId: "main",
					viewLevel: 1,
					controlAggregation: "pages"
				}
			}
		}
	},

	init: function() {
		sap.ui.core.UIComponent.prototype.init.apply( this, arguments );
		this.initResourceModel();
		this.getRouter().initialize();
	},

	initResourceModel: function() {
		var sUrl = "locale/i18n.properties";
		var oResourceModel = new sap.ui.model.resource.ResourceModel( {
			bundleUrl: sUrl
		} );
		this.setModel( oResourceModel, "i18n" );
	},

	createContent: function() {
		return sap.ui.view( {
			viewName: "js.view.app",
			type: "JS",
			viewData: {
				component: this
			}
		} );
	}

} );
