# MQTT Ingest Example
A sample Java web application which subscribes to arbitrary MQTT broker, translates the  received messages into MMS valid format and publish them to MMS with MQTT over WS Data Service.

## Import project
An application is provided as Maven project and could be imported to IDE with the respective plug-in or Maven command line interface.

## Compile with Maven
Build a WAR archive with Maven. Run `mvn clean install` either from the command line or from IDE

## POST ../mqtt-ingest/do
Make a POST request in order to configure the clients, connect them and subscribe
```
POST ../mqtt-ingest/do
{
	"subscriber" : {
		"serverUri" : "tcp://iot.org:1883",
		"topic" : "temperature"
	},
	"publisher" : {
		"serverUri" : "wss://iotmmsp0000trial.hanatrial.ondemand.com/com.sap.iotservices.mms/v1/api/ws/mqtt",
		"clientId" : "d000-e000-v000-i000-c000-e001",
		"topic" : "iot/data/d000-e000-v000-i000-c000-e001",
		"authorization" : {
			"username" : "d000-e000-v000-i000-c000-e001,
			"password" : "abc0a92514e4808894d392758a889zxc"
		}
	},
	"mapping" : {
		"messageTypeId" : "qwe5148f132eb9cfa5rty",
		"input" : {
			"type" : "double"
		},
		"output" : {
			"type" : "json",
			"references" : [
				{ "name" : "value", "type" : "double" }
			]
		}
	}
}
```
## PUT ../mqtt-ingest/do
Make a PUT request in order to re-configure the clients, re-connect them and re-subscribe
```
PUT ../mqtt-ingest/do
{
	"subscriber" : {
		"serverUri" : "tcp://iot.org:1883",
		"topic" : "temperature"
	},
	"publisher" : {
		"serverUri" : "wss://iotmmsp0000trial.hanatrial.ondemand.com/com.sap.iotservices.mms/v1/api/ws/mqtt",
		"clientId" : "d000-e000-v000-i000-c000-e001",
		"topic" : "iot/data/d000-e000-v000-i000-c000-e001",
		"authorization" : {
			"username" : "d000-e000-v000-i000-c000-e001,
			"password" : "abc0a92514e4808894d392758a889zxc"
		}
	},
	"mapping" : {
		"messageTypeId" : "qwe5148f132eb9cfa5rty",
		"input" : {
			"type" : "double"
		},
		"output" : {
			"type" : "json",
			"references" : [
				{ "name" : "value", "type" : "double" }
			]
		}
	}
}
```
## GET ../mqtt-ingest/do
Make a GET in order to request to see the current configuration
```
GET ../mqtt-ingest/do
```
## DELETE ../mqtt-ingest/do
Make a DELETE in order to request disconnect the clients and remove configuration
```
DELETE ../mqtt-ingest/do
```

TODOs:
- [x] Support JSON as mapping input type
- [ ] Optimize JSON serialization/deserialization
- [ ] Introduce JSON schema validation
- [ ] Improve error handling
- [ ] Support client certificate based authorization