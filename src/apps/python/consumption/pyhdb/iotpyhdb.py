import pyhdb

connection = pyhdb.connect(
	host="localhost",
	port=30015,
	user="<user>",
	password="<password>"
)

cursor = connection.cursor()

cursor.execute("SELECT * FROM NEO_<schema_id>.T_IOT_<message_type_id>")

results = cursor.fetchall()

for result in results:
	print result

connection.close()