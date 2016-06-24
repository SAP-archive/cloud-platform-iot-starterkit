var sBody = '';

var oConnection = $.hdb.getConnection();

var sQuery = 'SELECT * FROM "SYSTEM"."T_IOT_M0T0Y0P0E1"';

var oResultSet = oConnection.executeQuery(sQuery);

for(var i = 0; i < oResultSet.length; i++) {
    sBody += oResultSet[i].G_DEVICE + ' | ' + oResultSet[i].G_CREATED + ' | ' + oResultSet[i].C_VALUE +
        ' | ' + oResultSet[i].C_TIMESTAMP + '\n';
}

oConnection.close();

$.response.status = $.net.http.OK;
$.response.setBody(sBody);