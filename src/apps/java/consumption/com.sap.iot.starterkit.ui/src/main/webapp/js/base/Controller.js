jQuery.sap.require( "sap.m.MessageToast" );

sap.ui.core.mvc.Controller.extend( "js.base.Controller", {

	getText: function( sKey, oValues ) {
		var oModel = this.getOwnerComponent().getModel( "i18n" );
		var oResourceBundle = oModel.getResourceBundle();
		return oResourceBundle.getText( sKey, oValues );
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
	}

} );