sap.ui.jsfragment( "js.fragment.message", {

	createContent: function( oController ) {
		if ( (typeof oController.onSelectChange) !== "function" ) {
			oController.onSelectChange = function() {
				// empty implementation
			}
		}

		var oFilter = new sap.ui.model.Filter( {
			path: "device_type",
			test: function( sValue ) {
				return true;

				if ( sValue != null ) {
					sValue = sValue.toUpperCase();
				}
				if ( "PLEASE_SELECT" == sValue ) {
					return true;
				}
				var oData = sap.ui.getCore().getModel( "pushModel" ).getData();
				var sDeviceTypeId = oData.deviceTypeId;
				if ( sDeviceTypeId != null ) {
					sDeviceTypeId = sDeviceTypeId.toUpperCase();
				}
				return (sValue === sDeviceTypeId);
			}
		} );

		var oItem = new sap.ui.core.Item( {
			key: "{message>id}",
			text: "{message>name}"
		} );

		var oSelect = new sap.m.Select( {
			tooltip: "{i18n>TOOLTIP_MESSAGES}",
			width: "150px",
			enabled: {
				path: "message>/",
				formatter: function( oValue ) {
					console.log( oValue );
					if ( oValue === null || oValue === undefined || oValue.length === 0
						|| jQuery.isEmptyObject( oValue ) ) {
						return false;
					}
					return true;
				}
			},
			change: oController.onSelectChange
		} );
		oSelect.bindAggregation( "items", {
			path: "message>/",
			template: oItem,
		/* filters: [ oFilter ] */
		} );

		return oSelect;
	}

} );