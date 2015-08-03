This directory has the sources for a Python application that prints out the table data sent on behalf of the devices via HCP IoT Services to console.

### Prerequisites

1. You have HCP SDK installed https://tools.hana.ondemand.com
2. You have Python installed https://www.python.org (use version 2.7 to make PyHDB work)
3. You have PyHDB (SAP HANA Database Client for Python) installed https://github.com/SAP/PyHDB

### Opening a tunnel

* With the help of SDK tools open a tunnel to your IoT MMS schema by executing the following command:

```
	neo open-db-tunnel --account <user_id>trial --host hanatrial.ondemand.com --id <user_id>trial.iotmms.web --user <user_id>
```

![Python Consumption Example](../../../../images/xs_tunnel.jpg?raw=true "Python Consumption Example")

More you can find at https://help.hana.ondemand.com/help/frameset.htm?9e3f90f2ead74229ac5c8848ed5bf292.html

### Running Python script

* Adapt the script with the username and password your received (for example)

```
	connection = pyhdb.connect(
    	host="localhost",
    	port=30015,
    	user="DEV_C5B3HYBVRENFOUEGJOHDLKC88",
    	password="Mb431JTeboI1l1K"
	)
```

* Adapt the script with the actual schema name and table name (for example)

```
	cursor.execute("SELECT * FROM NEO_BBLYMLZWO9ALBF4K93YNMRYW9.T_IOT_M0T0Y0P0E1")
```

* Use Python (2.7) to run the script by execute the following command:

```
	python C:\<user_path>\git\iot-starterkit\src\apps\python\consumption\pyhdb\iotpyhdb.py
```

![Python Consumption Example](../../../../images/pyhdb.jpg?raw=true "Python Consumption Example")