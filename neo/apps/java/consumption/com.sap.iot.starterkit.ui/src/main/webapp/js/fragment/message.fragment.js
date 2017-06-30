sap.ui.jsfragment( "js.fragment.message", {

	createContent: function( oController ) {
		if ( (typeof oController.onSelectChange) !== "function" ) {
			oController.onSelectChange = function() {
				// empty implementation
			}
		}

		var oFilter = new sap.ui.model.Filter( {
			path: "direction",
			test: function( sValue ) {
				sDirection = oController.direction;

				if ( sDirection === null || sDirection === undefined ) {
					return true;
				}

				if ( sValue === "BIDIRECTIONAL" ) {
					return true;
				}

				return (sValue === sDirection.toUpperCase());
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
				path: "message>messageTypes",
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
		oSelect.bindAggregation( "items", {
			path: "message>messageTypes",
			template: oItem,
			filters: [ oFilter ]
		} );

		return oSelect;
	}

} );