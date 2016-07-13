sap.ui.jsview( "js.view.inbound", {

	getControllerName: function() {
		return "js.controller.inbound";
	},

	createList: function() {
		var oController = this.getController();

		this.oSwitch = new sap.m.Switch( {
			enabled: false,
			tooltip: "{i18n>TEXT_SWITCH}",
			change: function( oEvent ) {
				oController.onSwitchChange( oEvent );
			}
		} );

		this.oInput = new sap.m.Input( {
			enabled: false,
			placeholder: "{i18n>PLACEHOLDER_SEND}"
		} );
		this.oButton = new sap.m.Button( {
			enabled: false,
			text: "{i18n>LABEL_SEND}",
			tooltip: "{i18n>TEXT_SEND}",
			type: sap.m.ButtonType.Emphasized,
			press: function( oEvent ) {
				oController.onButtonPress( oEvent );
			}
		} );
		var oFlexBox = new sap.m.FlexBox( {
			direction: sap.m.FlexDirection.Row,
			displayInline: true,
			items: [ this.oInput, this.oButton ]
		} );

		return new sap.m.List( {
			inset: false,
			items: [ new sap.m.InputListItem( {
				label: "{i18n>TEXT_SWITCH}",
				content: this.oSwitch
			} ), new sap.m.InputListItem( {
				label: "{i18n>TEXT_SEND}",
				content: oFlexBox
			} ) ]
		} );
	},

	createToolbar: function() {
		var oController = this.getController();

		this.oDeviceSelect = sap.ui.jsfragment( "js.fragment.device", {
			onSelectChange: function( oEvent ) {
				oController.onDeviceSelectChange( oEvent );
			}
		} );

		this.oMessageSelect = sap.ui.jsfragment( "js.fragment.message", {
			onSelectChange: function( oEvent ) {
				oController.onMessageSelectChange( oEvent );
			}
		} );

		this.oSegmentedButton = new sap.m.SegmentedButton( {
			enabled: false,
			selectedKey: "http",
			tooltip: "{i18n>TOOLTIP_PUSH}",
			items: [ new sap.m.SegmentedButtonItem( {
				key: "http",
				text: "HTTP"
			} ), new sap.m.SegmentedButtonItem( {
				key: "ws",
				text: "WS"
			} ), new sap.m.SegmentedButtonItem( {
				key: "ws-mqtt",
				text: "MQTT"
			} ) ]
		} );

		return new sap.m.Toolbar( {
			content: [ new sap.m.Title( {
				text: "{i18n>TEXT_INBOUND_BEG}"
			} ), new sap.m.ToolbarSpacer( {
				width: "0.5em"
			} ), this.oDeviceSelect, this.oMessageSelect, new sap.m.Title( {
				text: "{i18n>TEXT_INBOUND_END}"
			} ), this.oSegmentedButton ]
		} );
	},

	createContent: function( oController ) {
		return new sap.m.Panel( {
			headerToolbar: this.createToolbar(),
			content: this.createList()
		} );
	}

} );