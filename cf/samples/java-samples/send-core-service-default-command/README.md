# SAP Internet of Things for the Cloud Foundry Environment

A sample Java application which is capable to send Toggle Valve commands to the Device connected over MQTT. The following steps are being performed during execution:

1. Get online MQTT gateway
```
Authorization: Basic <base64-encoded credentials>
GET https://%iot.host%:443/iot/core/api/v1/gateways
```
2. Get online device having the user-specified identifier
```
Authorization: Basic <base64-encoded credentials>
GET https://%iot.host%:443/iot/core/api/v1/devices/%device.id%
```
3. Create a device if does not exists or no online device with the specified identifier was found
```
Authorization: Basic <base64-encoded credentials>
POST https://%iot.host%:443/iot/core/api/v1/devices  
{
	"gatewayId" : "%gateway.id%",
	"name" : "%device.name%"
}
```
4. Get device PEM-certificate 
```
Authorization: Basic <base64-encoded credentials>
GET https://%iot.host%:443/iot/core/api/v1/devices/%device.id%/authentication/pem
```
5. Create Java SSL context out PEM certificate
6. As a device, subscribe for incoming commands
```
SUBSCRIBE ssl://%iot.host%:8883 on topic 'commands/%device.physical.address%'  
```
7. Send random Toggle Valve commands to the the device (frequency = 1 seconds; duration = 5 seconds)
```
Authorization: Basic <base64-encoded credentials>
POST https://%iot.host%:443/iot/core/api/v1/devices/%device.id%/commands
{
	"capabilityId" : "3",
	"sensorId" : "%sensor.id%",
	"command" : {
		"val" : "%0|1%"
	}
}
```
 
>Note: A standard sample command with with the default capability ID "3" and random boolean value "0" or "1" is used in this example

## Import project
An application is provided as Maven project and could be imported to IDE with the respective plug-in or Maven command line interface.

## Run application from IDE
- Run [Main class](src/main/java/sample/Main.java) as Java application.

## Build with Maven
It is possible to build an executable JAR with Maven. Simply run `mvn clean install` from the command line.

>Note: After the very first import from GitHub, ensure to build the top-level java-samples reactor project.

## Run compiled version
- Find the compiled version under project's `target` directory
- Execute from the command line `java -jar send-core-service-default-command.jar`

>Note: In order to save efforts when typing configuration properties every time you start an application, you may place the `config.properties` file at the same level to your executable JAR. A template for such a file could be found in [resources](src/main/resources/config.properties)

![In Action](src/main/resources/send-core-service-default-command.jpg "In Action")