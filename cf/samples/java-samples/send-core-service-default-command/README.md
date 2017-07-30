# SAP Internet of Things for the Cloud Foundry Environment

A sample Java application which is capable to send Toggle Valve commands to the Device connected over MQTT and listen to them at hte same time. The following steps are being performed during execution:

1. Get online MQTT gateway.
```
Authorization: Basic <base64-encoded credentials>
GET https://%iot.host%:443/iot/core/api/v1/gateways
```
2. Get online device by its identifier.
```
Authorization: Basic <base64-encoded credentials>
GET https://%iot.host%:443/iot/core/api/v1/devices/%device.id%
```
	1. Create a new device if it does not exist or no online device with the specified identifier was found in the MQTT gateway.
	```
	Authorization: Basic <base64-encoded credentials>
	POST https://%iot.host%:443/iot/core/api/v1/devices  
	{
		"gatewayId" : "%gateway.id%",
		"name" : "%random.device.name%"
	}
	```
3. Get device sensor by its identifier which is assigned to the device.
	1. Create a new sensor and assign it to the device if no sensor was found.
	
	>Note: A new sensor will be mapped to the pre-configured Sensor Type having ID "0".
	
	```
	Authorization: Basic <base64-encoded credentials>
	POST https://%iot.host%:443/iot/core/api/v1/sensors  
	{
		"deviceId" : "%device.id%",
		"sensorTypeId" : "0",
		"name" : "%random.sensor.name%"
	}
	```
4. Get device PEM-certificate.
```
Authorization: Basic <base64-encoded credentials>
GET https://%iot.host%:443/iot/core/api/v1/devices/%device.id%/authentication/pem
```
5. Create Java SSL context based on the PEM certificate.
6. As a device, subscribe for incoming commands over MQTT.
```
SUBSCRIBE ssl://%iot.host%:8883 on topic 'commands/%device.physical.address%'  
```

>Note: A subscription is going to be terminated automatically after 20 seconds.

7. Send random Toggle Valve commands to the the device.
```
Authorization: Basic <base64-encoded credentials>
POST https://%iot.host%:443/iot/core/api/v1/devices/%device.id%/commands
{
	"capabilityId" : "3",
	"sensorId" : "%sensor.id%",
	"command" : {
		"val" : "%0 | 1%"
	}
}
```
 
>Note: A pre-configured "Toggle Valve" Capability having ID "*_0_0_3" and mapped to the pre-configured Sensor Type having ID "0" will be used by this sample.
>Note: The sending rate is one command per second. Duration is 5 seconds.

## Import project
This sample application is provided as Maven project and could be imported to IDE with the help of respective plug-in or Maven command line interface.

## Run application from the IDE
- Run [Main class](src/main/java/sample/Main.java) as Java application.

## Build with Maven
It is possible to build an executable JAR with Maven. Simply run `mvn clean install` from the command line.

>Note: After the very first import from GitHub, ensure to build the top-level java-samples reactor project.

### Run compiled version
- Find the compiled version under project's `target` directory
- Execute from the command line `java -jar send-core-service-default-command.jar`

>Note: In order to save efforts when typing sample properties every time you launch an application, you may place the `sample.properties` file at the same level to your executable JAR. A template for such a file could be found under [resources](src/main/resources/sample.properties)

![In Action](src/main/resources/send-core-service-default-command.jpg "In Action")