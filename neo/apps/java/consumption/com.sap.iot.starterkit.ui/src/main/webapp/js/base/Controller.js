jQuery.sap.require( "sap.m.MessageToast" );

sap.ui.core.mvc.Controller.extend( "js.base.Controller", {

	getText: function( sKey, oValues ) {
		var oModel = this.getOwnerComponent().getModel( "i18n" );
		var oResourceBundle = oModel.getResourceBundle();
		return oResourceBundle.getText( sKey, oValues );
	},

	doGet: function( sUrl, successHandler, errorHandler ) {
		this.doHttp( "GET", sUrl, undefined, successHandler, errorHandler );
	},

	doPost: function( sUrl, oData, successHandler, errorHandler ) {
		this.doHttp( "POST", sUrl, oData, successHandler, errorHandler );
	},

	doHttp: function( sType, sUrl, oData, successHandler, errorHandler ) {
		if ( errorHandler === undefined ) {
			errorHandler = function( jqXHR, textStatus, errorThrown ) {
				// empty implementation
			}
		}

		if ( oData !== undefined ) {
			oData = JSON.stringify( oData );
		}

		jQuery.ajax( {
			type: sType,
			dataType: "json",
			contentType: "application/json",
			data: oData,
			url: sUrl,
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
			}
		} );
	}

} );