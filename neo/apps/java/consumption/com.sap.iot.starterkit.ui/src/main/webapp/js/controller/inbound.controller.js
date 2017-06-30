js.base.Controller.extend( "js.controller.inbound", {

	onInit: function() {
		this.getView().setModel( new sap.ui.model.json.JSONModel(), "message" );
	},

	onDeviceSelectChange: function( oEvent ) {
		var that = this;

		var oSelectedItem = oEvent.getParameter( "selectedItem" );
		var sKey = oSelectedItem.getKey();
		if ( "placeholder" === sKey ) {

			that.getView().getModel( "message" ).setData( [] );
			that.getView().oMessageSelect.setSelectedItem( null );
			that.getView().oMessageSelect.setSelectedItemId( undefined );
			that.getView().oMessageSelect.setSelectedKey( undefined );

			that.getView().oSegmentedButton.setEnabled( false );
			that.getView().oSwitch.setEnabled( false );
			that.getView().oInput.setEnabled( false );
			that.getView().oButton.setEnabled( false );

			that.getView().oInput.setValue( "" );
			that.getView().oSwitch.setState( false );
			that.getView().oSegmentedButton.setSelectedKey( "http" );

			return;
		}

		var sDeviceType = oSelectedItem.getCustomData()[0].getValue();

		var successHandler = function( oData, textStatus, jqXHR ) {
			var oFilteredData = oData.filter( function( next ) {
				return sDeviceType === next.id;
			} );

			oFilteredData[0].messageTypes.unshift( {
				id: "placeholder",
				name: that.getText( "TEXT_SELECT" ),
				direction: "bidirectional"
			} );

			that.getView().oMessageSelect.bindElement( "message>/0" );
			that.getView().getModel( "message" ).setData( oFilteredData );
		};

		var sUrl = "rdms/v2/api/deviceTypes";

		this.doGet( sUrl, successHandler );
	},

	onMessageSelectChange: function( oEvent ) {
		var that = this;

		var oSelectedItem = oEvent.getParameter( "selectedItem" );
		var sKey = oSelectedItem.getKey();
		if ( "placeholder" === sKey ) {

			that.getView().oSegmentedButton.setEnabled( false );
			that.getView().oSwitch.setEnabled( false );
			that.getView().oInput.setEnabled( false );
			that.getView().oButton.setEnabled( false );

			that.getView().oInput.setValue( "" );
			that.getView().oSwitch.setState( false );
			that.getView().oSegmentedButton.setSelectedKey( "http" );

			return;
		}

		that.getView().oSegmentedButton.setEnabled( true );
		that.getView().oSwitch.setEnabled( true );
		that.getView().oInput.setEnabled( true );
		that.getView().oButton.setEnabled( true );
	},

	onSwitchChange: function( oEvent ) {
		var bState = oEvent.getParameter( "state" );
		var sOperand = bState == true ? "1" : "0";

		this.doPush( "led", sOperand );
	},

	onButtonPress: function( oEvent ) {
		var sOperand = this.getView().oInput.getValue();

		this.doPush( "display", sOperand );
	},

	doPush: function( sOpcode, sOperand ) {
		var oData = {
			sender: "My IoT application",
			messageType: this.getView().oMessageSelect.getSelectedItem().getKey(),
			method: this.getView().oSegmentedButton.getSelectedKey(),
			messages: [ {
				opcode: sOpcode,
				operand: sOperand
			} ]
		};

		var successHandler = function( oData, textStatus, jqXHR ) {
			sap.m.MessageToast.show( oData.msg );
		};

		var sDevice = this.getView().oDeviceSelect.getSelectedItem().getKey();
		var sUrl = "mms/v1/api/http/push/".concat( sDevice );

		this.doPost( sUrl, oData, successHandler );
	}

} );