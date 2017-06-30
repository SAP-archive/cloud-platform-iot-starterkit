This directory has the sources for a Node.js application that prints out the table data sent on behalf of the devices via SAP Cloud Platform Internet of Things to console.

### Prerequisites

1. You have SAP Cloud Platform SDK installed https://tools.hana.ondemand.com
2. You have Node.js installed https://nodejs.org/
3. You have NodeHDB (SAP HANA Database Client for Node) installed https://github.com/SAP/node-hdb

### Opening a tunnel

* With the help of SDK tools open a tunnel to your IoT MMS schema by executing the following command:

```
	neo open-db-tunnel --account <user_id>trial --host hanatrial.ondemand.com --id <user_id>trial.iotmms.web --user <user_id>
```

![Node Consumption Example](../../../../images/xs_tunnel.jpg?raw=true "Node Consumption Example")

More you can find at https://help.hana.ondemand.com/help/frameset.htm?9e3f90f2ead74229ac5c8848ed5bf292.html

### Running Node.js script

* Adapt the script with the username and password your received (for example)

```
	var client = hdb.createClient({
		host : "localhost",
		port : 30015,
		user : "DEV_C5B3HYBVRENFOUEGJOHDLKC88",
		password : "Mb431JTeboI1l1K"
	});
```

* Adapt the script with the actual schema name and table name (for example)

```
	client.exec(
		"SELECT * FROM NEO_BBLYMLZWO9ALBF4K93YNMRYW9.T_IOT_M0T0Y0P0E1",
```

* Use Node.js to run the script by execute the following command:

```
	node C:\<user_path>\git\iot-starterkit\src\apps\nodejs\consumption\hdb\iothdb.js
```

![Node Consumption Example](../../../../images/nodehdb.jpg?raw=true "Node Consumption Example")