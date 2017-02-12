sap.ui.jsview( "js.view.main", {

	getControllerName: function() {
		return "js.controller.main";
	},

	createBar: function() {
		var oController = this.getController();

		var oSampleButton = new sap.m.Button( {
			icon: "sap-icon://example",
			tooltip: "{i18n>TOOLTIP_SAMPLE}",
			press: function( oEvent ) {
				oController.onSampleButtonPress( oEvent );
			}
		} );

		var oTitle = new sap.m.Title( {
			text: "{i18n>TITLE}"
		} );

		return new sap.m.Bar( {
			design: sap.m.BarDesign.Header,
			contentMiddle: oTitle,
			contentRight: oSampleButton
		} );
	},

	createTextArea: function() {
		var oController = this.getController();

		this.oTextArea = new sap.m.TextArea( {
			width: "100%",
			height: "100%",
			placeholder: "{i18n>LABEL_NODATA}"
		} );
		this.oTextArea.addStyleClass( "customTextArea" );
		return this.oTextArea;
	},

	createToolbar: function() {
		var oController = this.getController();

		var oUpdateButton = new sap.m.Button( {
			text: "{i18n>LABEL_UPDATE}",
			type: sap.m.ButtonType.Accept,
			tooltip: "{i18n>TOOLTIP_UPDATE}",
			press: function( oEvent ) {
				oController.onUpdateButtonPress( oEvent );
			}
		} );

		var oDeleteButton = new sap.m.Button( {
			text: "{i18n>LABEL_DELETE}",
			type: sap.m.ButtonType.Reject,
			tooltip: "{i18n>TOOLTIP_DELETE}",
			press: function( oEvent ) {
				oController.onDeleteButtonPress( oEvent );
			}
		} );

		return new sap.m.Toolbar( {
			content: [ new sap.m.ToolbarSpacer(), oUpdateButton, oDeleteButton ]
		} );
	},

	createContent: function( oController ) {
		this.setDisplayBlock( true );
		return new sap.m.Page( {
			enableScrolling: false,
			customHeader: this.createBar(),
			content: this.createTextArea(),
			footer: this.createToolbar()
		} );
	}

} );