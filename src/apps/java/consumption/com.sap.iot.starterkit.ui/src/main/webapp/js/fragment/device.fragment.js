sap.ui.jsfragment( "js.fragment.device", {

	createContent: function( oController ) {
		if ( (typeof oController.onSelectChange) !== "function" ) {
			oController.onSelectChange = function() {
				// empty implementation
			}
		}

		var oItem = new sap.ui.core.Item( {
			key: "{device>id}",
			text: "{device>name}"
		} );
		oItem.addCustomData( new sap.ui.core.CustomData( {
			key: "type",
			value: "{device>device_type}"
		} ) );

		var oSelect = new sap.m.Select( {
			tooltip: "{i18n>TOOLTIP_DEVICES}",
			width: "150px",
			enabled: {
				path: "device>/",
				formatter: function( oValue ) {
					if ( oValue === null || oValue === undefined || oValue.length === 0
						|| jQuery.isEmptyObject( oValue ) ) {
						return false;
					}
					return true;
				}
			},
			change: oController.onSelectChange
		} );
		oSelect.bindItems( "device>/", oItem );

		return oSelect;
	}

} );