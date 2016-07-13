jQuery.sap.require( 'sap.m.MessageToast' );

sap.ui.core.mvc.Controller.extend( 'js.base.Controller', {

	onInit: function() {

		// var oDevicesModel = new sap.ui.model.json.JSONModel();
		// oDevicesModel.setData( [ {
		// id: "1",
		// name: "1"
		// } ] );
		// sap.ui.getCore().setModel( oDevicesModel, "device" );

		// var oEventBus = this.getEventBus();
		//
		// oEventBus.subscribe( Channel.APP, Event.BEFORE_AJAX, AjaxHelper.onBeforeAjax, this );
		// oEventBus.subscribe( Channel.APP, Event.AFTER_AJAX, AjaxHelper.onAfterAjax, this );
		//
		// $( document ).ready( function() {
		// document.title = I18nHelper.getI18nText( 'LABEL_TITLE' );
		// } );

		var sTitle = this.getText( "TITLE" );
		$( document ).ready( function() {
			document.title = sTitle;
		} );

		var that = this;
		this.getDevices();
		setInterval( function() {
			that.getDevices();
		}, 5000 );
		console.log( "init core controller" );
	},

	getPushMessageTypes: function() {
		getMessageTypes( "registeredPushMessageTypes", "pushModel" );
	},

	getMessageTypes: function( namespace, key ) {
		sap.ui.getCore().getModel( namespace ).setData( [] );
		var oModelData = sap.ui.getCore().getModel( key ).getData();
		var sDeviceTypeId = oModelData.deviceTypeId;
		if ( sDeviceTypeId === null || sDeviceTypeId.trim() === "" || sDeviceTypeId === "please_select" ) {
			return;
		}
		var sUrl = "data/messagetypes/".concat( sDeviceTypeId );
		this.getDataForSelect( sUrl, namespace );
	},

	getDevices: function() {
		// this.getDataForSelect( "data/devices", "device" );
		this.getDataForSelect( "device.json", "device" );
	},

	getDataForSelect: function( url, namespace ) {
		var that = this;
		var successHandler = function( oData, textStatus, jqXHR ) {
			oData.unshift( {
				id: "placeholder",
				name: "Please select"
			} );
			that.getOwnerComponent().getModel( namespace ).setData( oData );
		};
		this.doGet( url, successHandler );
	},

	doGet: function( url, successHandler, errorHandler ) {
		this.doHttp( "GET", url, undefined, successHandler, errorHandler );
	},

	doPost: function( url, data, successHandler, errorHandler ) {
		this.doHttp( "POST", url, data, successHandler, errorHandler );
	},

	doHttp: function( type, url, data, successHandler, errorHandler ) {
		if ( errorHandler === undefined ) {
			errorHandler = function( jqXHR, textStatus, errorThrown ) {
				// empty implementation
			}
		}
		if ( data !== undefined ) {
			data = JSON.stringify( data );
		}
		jQuery.ajax( {
			type: type,
			dataType: "json",
			contentType: "application/json",
			data: data,
			url: url,
			error: function( jqXHR, textStatus, errorThrown ) {
				var sMessage = "".concat( jqXHR.status, " ", jqXHR.statusText, " ", jqXHR.responseText );
				sap.m.MessageToast.show( sMessage );
				errorHandler.apply( this, [ jqXHR, textStatus, errorThrown ] );
			},
			success: function( oData, textStatus, jqXHR ) {
				if ( oData === null || oData === undefined ) {
					sap.m.MessageToast.show( "WARNING. Received a null or undefined response object." );
					return;
				}
				successHandler.apply( this, [ oData, textStatus, jqXHR ] );
			}
		} );
	},

	getData: function() {
		var oConsumptionModelData = sap.ui.getCore().getModel( "consumptionModel" ).getData();

		var sDeviceId = oConsumptionModelData.deviceId;
		var sDeviceTypeId = oConsumptionModelData.deviceTypeId;
		var sMessageTypeId = oConsumptionModelData.messageTypeId;

		if ( sDeviceId === null || sDeviceTypeId === null || sMessageTypeId === null
			|| sDeviceId.trim() === "" || sDeviceTypeId.trim() === "" || sMessageTypeId.trim() === "" ) {
			return;
		}

		var sUrl = "data/table/".concat( sDeviceId, "/", sDeviceTypeId, "/", sMessageTypeId );
		var successHandler = function( oData, textStatus, jqXHR ) {
			oData = oData.slice( 0, 500 );
			oData = oData.reverse();
			sap.ui.getCore().getModel( "sensor" ).setData( oData );
		};
		doGet( sUrl, successHandler );
	},

	getText: function( sKey, oValues ) {
		var oModel = this.getOwnerComponent().getModel( "i18n" );
		var oResourceBundle = oModel.getResourceBundle();
		return oResourceBundle.getText( sKey, oValues );
	},

	getEventBus: function() {
		return this.getOwnerComponent().getEventBus();
	},

	getRouter: function() {
		return sap.ui.core.UIComponent.getRouterFor( this );
	},

	getRoute: function( sName ) {
		return this.getRouter().getRoute( sName );
	},

	back: function() {
		this.getRouter().back();
	},

	initConfigServiceModel: function( errHandler, sucHandler, befHandler, aftHandler ) {
		var that = this;
		var errorHandler = function( jqXHR, textStatus, errorThrown ) {
			jQuery.sap.log.error( 'getData()', jqXHR.status + ' ' + jqXHR.statusText, 'controller.js' );
			sap.m.MessageToast.show( I18nHelper.getI18nText( 'TEXT_CONFIG_GET_ERROR' ) );
			if ( (typeof errHandler) === 'function' ) {
				errHandler.apply( that, [ jqXHR, textStatus, errorThrown ] );
			}
		};

		var successHandler = function( oData, textStatus, jqXHR ) {
			var oJSONModel = that.getView().getModel( that.getOwnerComponent().CONFIGURATION_SERVICE_MODEL_NAME );
			if ( !oJSONModel ) {
				oJSONModel = new sap.ui.model.json.JSONModel();
				oJSONModel.setData( oData );
				that.getView().setModel( oJSONModel, that.getOwnerComponent().CONFIGURATION_SERVICE_MODEL_NAME );
			} else {
				oJSONModel.setData( oData );
			}

			if ( (typeof sucHandler) === 'function' ) {
				sucHandler.apply( that, [ oData, textStatus, jqXHR ] );
			}
		};

		AjaxHelper.doAjaxGet( this.getOwnerComponent().URL_CONFIG_SERVICE, successHandler, errorHandler, befHandler, aftHandler );
	},

	initProcessingServiceMappingsModel: function( errHandler, sucHandler, befHandler, aftHandler ) {
		var that = this;
		var errorHandler = function( jqXHR, textStatus, errorThrown ) {
			jQuery.sap.log.error( 'getData()', jqXHR.status + ' ' + jqXHR.statusText, 'controller.js' );
			sap.m.MessageToast.show( I18nHelper.getI18nText( 'TEXT_PROCESSING_SERVICE_GET_ERROR' ) );
			if ( (typeof errHandler) === 'function' ) {
				errHandler.apply( that, [ jqXHR, textStatus, errorThrown ] );
			}
		};

		var successHandler = function( oData, textStatus, jqXHR ) {
			var oJSONModel = that.getView().getModel( that.getOwnerComponent().PROCESSING_SERVICE_MAPPINGS_MODEL_NAME );
			if ( !oJSONModel ) {
				oJSONModel = new sap.ui.model.json.JSONModel();
				oJSONModel.setData( oData );
				that.getView().setModel( oJSONModel, that.getOwnerComponent().PROCESSING_SERVICE_MAPPINGS_MODEL_NAME );
			} else {
				oJSONModel.setData( oData );
			}

			if ( (typeof sucHandler) === 'function' ) {
				sucHandler.apply( that, [ oData, textStatus, jqXHR ] );
			}
		};

		AjaxHelper.doAjaxGet( this.getOwnerComponent().URL_PROCESSING_SERVICE_MAPPING, successHandler, errorHandler, befHandler, aftHandler );
	},

	initExtensionServiceModel: function( errHandler, sucHandler, befHandler, aftHandler ) {
		var that = this;
		var errorHandler = function( jqXHR, textStatus, errorThrown ) {
			jQuery.sap.log.error( 'getData()', jqXHR.status + ' ' + jqXHR.statusText, 'controller.js' );
			sap.m.MessageToast.show( I18nHelper.getI18nText( 'TEXT_EXTENSION_SERVICE_GET_ERROR' ) );
			if ( (typeof errHandler) === 'function' ) {
				errHandler.apply( that, [ jqXHR, textStatus, errorThrown ] );
			}
		};

		var successHandler = function( oData, textStatus, jqXHR ) {
			var oJSONModel = that.getView().getModel( that.getOwnerComponent().EXTENSION_SERVICE_MODEL_NAME );
			if ( !oJSONModel ) {
				oJSONModel = new sap.ui.model.json.JSONModel();
				oJSONModel.setData( oData );
				that.getView().setModel( oJSONModel, that.getOwnerComponent().EXTENSION_SERVICE_MODEL_NAME );
			} else {
				oJSONModel.setData( oData );
			}

			if ( (typeof sucHandler) === 'function' ) {
				sucHandler.apply( that, [ oData, textStatus, jqXHR ] );
			}
		};

		AjaxHelper.doAjaxGet( this.getOwnerComponent().URL_EXTENSION_SERVICE, successHandler, errorHandler, befHandler, aftHandler );
	}

} );