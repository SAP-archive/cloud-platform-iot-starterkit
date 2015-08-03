var oConnection = $.hdb.getConnection();

var sQuery = "SELECT * FROM NEO_<schema_id>.T_IOT_<message_type_id>";

var oResultSet = oConnection.executeQuery(sQuery);

var sBody = "";

for(var i = 0; i < oResultSet.length; i++) {
    sBody += oResultSet[i]["G_DEVICE"] + "\t" + oResultSet[i]["G_CREATED"] + "\t" + oResultSet[i]["C_VALUE"] + "\t" + oResultSet[i]["C_TIMESTAMP"] + "\n";
}

oResultSet.close();
oStatement.close();

$.response.status = $.net.http.OK;
$.response.setBody(sBody);