var hdb = require("hdb");

var client = hdb.createClient({
	host : "localhost",
	port : 30015,
	user : "<user>",
	password : "<password>"
});

client.on("error", function(error) {
	console.error("Network connection error", error);
});

client.connect(function(error) {
	if (error) {
		return console.error("Connect error:", error);
	}
	client.exec(
		"SELECT * FROM NEO_<schema_id>.T_IOT_<message_type_id>",
		function(error, rows) {
			client.end();
			if (error) {
				return console.error("Execute error:", error);
			}
			console.log(rows);
		});
});