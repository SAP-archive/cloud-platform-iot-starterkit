js.base.Controller.extend( "js.controller.main", {

	onInit: function() {
		var sTitle = this.getText( "TITLE" );
		$( document ).ready( function() {
			document.title = sTitle;
		} );

		var that = this;
		var successHandler = function( oData, textStatus, jqXHR ) {
			try {
				oData = that.formatJson( oData );
			} catch ( oError ) {
				// do nothing
			}
			that.getView().oTextArea.setValue( oData );
		};
		var errorHandler = function( jqXHR, textStatus, errorThrown ) {
			var sValue = "[".concat( jqXHR.status, "] ", jqXHR.statusText, ". ", jqXHR.responseText );
			that.getView().oTextArea.setValue( sValue );
		};

		this.doGet( "do", successHandler, errorHandler );
	},

	onSampleButtonPress: function( oEvent ) {
		var that = this;
		jQuery.getJSON( "sample/configuration.json", function( oData ) {
			var sFormattedJson = that.formatJson( oData );
			that.getView().oTextArea.setValue( sFormattedJson );
		} );
	},

	onUpdateButtonPress: function( oEvent ) {
		var json = this.getView().oTextArea.getValue();
		try {
			json = JSON.parse( json );
		} catch ( oError ) {
			// do nothing
		}
		var that = this;
		var successHandler = function( oData, textStatus, jqXHR ) {
			var sFormattedJson = that.formatJson( oData );
			that.getView().oTextArea.setValue( sFormattedJson );
			sap.m.MessageToast.show( "[SUCCESS] Configuration updated" );
		};
		var errorHandler = function( jqXHR, textStatus, errorThrown ) {
			var sValue = "[".concat( jqXHR.status, "] ", jqXHR.statusText, ". ", jqXHR.responseText );
			that.getView().oTextArea.setValue( sValue );
		};
		this.doPost( "do", json, successHandler, errorHandler );
	},

	onDeleteButtonPress: function( oEvent ) {
		var that = this;
		var successHandler = function( oData, textStatus, jqXHR ) {
			that.getView().oTextArea.setValue( oData );
		};
		var errorHandler = function( jqXHR, textStatus, errorThrown ) {
			var sValue = "[".concat( jqXHR.status, "] ", jqXHR.statusText, ". ", jqXHR.responseText );
			that.getView().oTextArea.setValue( sValue );
		};

		this.doDelete( "do", successHandler, errorHandler );
	}

} );