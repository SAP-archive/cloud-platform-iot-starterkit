jQuery.sap.require( "sap.m.MessageToast" );

sap.ui.core.mvc.Controller.extend( "js.base.Controller", {

	getRouter: function() {
		return sap.ui.core.UIComponent.getRouterFor( this );
	},

	getText: function( sKey, oValues ) {
		var oModel = this.getOwnerComponent().getModel( "i18n" );
		var oResourceBundle = oModel.getResourceBundle();
		return oResourceBundle.getText( sKey, oValues );
	},

	doGet: function( sUrl, successHandler, errorHandler, beforeHandler, completeHandler ) {
		this.doHttp( "GET", sUrl, undefined, successHandler, errorHandler, beforeHandler, completeHandler );
	},

	doPost: function( sUrl, oData, successHandler, errorHandler, beforeHandler, completeHandler ) {
		this.doHttp( "POST", sUrl, oData, successHandler, errorHandler, beforeHandler, completeHandler );
	},

	doDelete: function( sUrl, successHandler, errorHandler, beforeHandler, completeHandler ) {
		this.doHttp( "DELETE", sUrl, undefined, successHandler, errorHandler, beforeHandler, completeHandler );
	},

	doHttp: function( sType, sUrl, oData, successHandler, errorHandler, beforeHandler, completeHandler ) {
		if ( successHandler === undefined ) {
			successHandler = function( data, textStatus, jqXHR ) {
				sap.m.MessageToast.show( data );
			}
		}

		if ( errorHandler === undefined ) {
			errorHandler = function( jqXHR, textStatus, errorThrown ) {
				// empty implementation
			}
		}

		if ( beforeHandler === undefined ) {
			beforeHandler = function( jqXHR, settings ) {
				// empty implementation
			}
		}

		if ( completeHandler === undefined ) {
			completeHandler = function( jqXHR, textStatus ) {
				// empty implementation
			}
		}

		if ( oData !== undefined ) {
			oData = JSON.stringify( oData );
		}

		var that = this;
		jQuery.ajax( {
			type: sType,
			contentType: "application/json",
			data: oData,
			url: sUrl,
			beforeSend: function( jqXHR, settings ) {
				beforeHandler.apply( this, [ jqXHR, settings ] );
			},
			error: function( jqXHR, textStatus, errorThrown ) {
				if ( jqXHR.status !== 0 ) {
					sap.m.MessageToast.show( "[".concat( jqXHR.status, "] ", jqXHR.statusText, " ", jqXHR.responseText ) );
				}
				errorHandler.apply( this, [ jqXHR, textStatus, errorThrown ] );
			},
			statusCode: {
				0: function( jqXHR, textStatus, errorThrown ) {
					sap.m.MessageToast.show( "[ERROR] Connection refused" );
				}
			},
			success: function( oData, textStatus, jqXHR ) {
				if ( oData === null || oData === undefined ) {
					sap.m.MessageToast.show( "[WARNING] Received a null or undefined response object" );
					return;
				}
				successHandler.apply( this, [ oData, textStatus, jqXHR ] );
			},
			complete: function( jqXHR, textStatus ) {
				completeHandler.apply( this, [ jqXHR, textStatus ] );
			}
		} );
	},

	formatJson: function( oData ) {
		return JSON.stringify( JSON.parse( JSON.stringify( oData ) ), null, 2 );
	}

} );