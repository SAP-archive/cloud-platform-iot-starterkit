var oConnection = $.db.getConnection();

var sQuery = "SELECT * FROM NEO_<schema_id>.T_IOT_<message_type_id>";
 
var oStatement = oConnection.prepareStatement(sQuery);

var oResultSet = oStatement.executeQuery();

var sBody = "";

while (oResultSet.next()) {
    sBody += oResultSet.getNString(1) + "\t" + oResultSet.getNString(2) + "\t" + oResultSet.getNString(3) + "\t" + oResultSet.getNString(4) + "\n";
}

oResultSet.close();
oStatement.close();

$.response.status = $.net.http.OK;
$.response.setBody(sBody);