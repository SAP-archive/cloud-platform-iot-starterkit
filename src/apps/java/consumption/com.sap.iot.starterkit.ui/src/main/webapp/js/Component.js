sap.ui.define( [ "sap/ui/core/UIComponent" ], function( UIComponent ) {
	"use strict";

	return UIComponent.extend( "iotstarterkit.Component", {

		metadata: {
			routing: {
				config: {
					viewType: "JS",
					viewPath: "js.view",
					controlId: "iotstarterkitapp",/*
															 * bypassed: { target: [ "list", "404" ] },
															 */
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
			UIComponent.prototype.init.apply( this, arguments );
			this.initResourceModel();
			// this.initIconModel();
			// this.initSettingsModel();
			// this.initDataModel();

			var oDevicesModel = new sap.ui.model.json.JSONModel();
			// oDevicesModel.setData( [ {
			// id: "1",
			// name: "1"
			// }, {
			// id: "2",
			// name: "2"
			// } ] );
			this.setModel( oDevicesModel, "device" );

			this.getRouter().initialize();
		},

		initDataModel: function() {
			var sUrl = "model/data.json";
			var oParameters = null;
			var bAsync = false;
			var oJSONModel = new sap.ui.model.json.JSONModel();
			oJSONModel.loadData( sUrl, oParameters, bAsync );
			this.setModel( oJSONModel );
		},

		initResourceModel: function() {
			var sUrl = "locale/i18n.properties";
			var oResourceModel = new sap.ui.model.resource.ResourceModel( {
				bundleUrl: sUrl
			} );
			this.setModel( oResourceModel, "i18n" );
		},

		initIconModel: function() {
			var sUrl = "model/icon.json";
			var oParameters = null;
			var bAsync = false;
			var oJSONModel = new sap.ui.model.json.JSONModel();
			oJSONModel.loadData( sUrl, oParameters, bAsync );
			this.setModel( oJSONModel, "icon" );
		},

		initSettingsModel: function() {
			var oJSONModel = new js.core.Settings();
			this.setModel( oJSONModel, "settings" );
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

} );
