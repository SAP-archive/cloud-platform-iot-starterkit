# SAP Internet of Things for the Cloud Foundry Environment
A sample Java application which is capable to send Display Text commands to the Device connected over MQTT and listen to them at the same time. 

## Import project
This sample application is provided as Maven project and could be imported to IDE with the help of respective plug-in or Maven command line interface.

## Run application from the IDE
- Run [Main class](src/main/java/sample/Main.java) as Java application.

## Build with Maven
It is possible to build an executable JAR with Maven. Simply run `mvn clean install` from the command line.

>Note: After the very first import from GitHub, ensure to build the top-level [java-samples](../) reactor project.

### Run compiled version
- Find the compiled version under project's `target` directory
- Execute from the command line `java -jar send-core-service-custom-command.jar`

>Note: In order to save efforts when typing sample properties every time you launch an application, you may place the `sample.properties` file at the same level to your executable JAR. A template for such a file could be found under [resources](src/main/resources/sample.properties)

![In Action](src/main/resources/send-core-service-custom-command_0.jpg "In Action")
![In Action](src/main/resources/send-core-service-custom-command_1.jpg "In Action")

## Execution Steps
The following steps are being performed during execution:

1. Get online MQTT gateway.
    ```
    Authorization: Basic <base64-encoded credentials>
    GET https://%iot.host%:443/iot/core/api/v1/gateways?filter=type eq 'mqtt' and status eq 'online'&top=1
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
3. Check if custom "Display Text" capability exists.
    ```
    Authorization: Basic <base64-encoded credentials>
    GET https://%iot.host%:443/iot/core/api/v1/capabilities
    ```
	1. Create "Display Text" capability if not found.
	    ```
	    Authorization: Basic <base64-encoded credentials>
	    POST https://%iot.host%:443/iot/core/api/v1/capabilities  
	    {
		    "name" : "Display Text",
		    "properties" : [
			    {
				    "name" : "Text",
				    "dataType" : "string"
			    }
		    ]
	    }
	    ```
4. Check if custom "Display Sensors" sensor type exists.
    ```
    Authorization: Basic <base64-encoded credentials>
    GET https://%iot.host%:443/iot/core/api/v1/sensorTypes
    ```
	1. Create "Display Sensors" sensor type if not found.
	    ```
	    Authorization: Basic <base64-encoded credentials>
	    POST https://%iot.host%:443/iot/core/api/v1/sensorTypes  
	    {
		    "name" : "Display Sensors",
		    "capabilities" : [
			    {
				    "id" : "%display.text.capability.id%",
				    "type" : "command"
			    }
		    ]
	    }
	    ```
5. Get device sensor by its identifier which is assigned to the device.
	1. Create a new sensor and assign it to the device if no sensor is assigned to the device or a sensor has no reference to the default sensor type.
	    ```
	    Authorization: Basic <base64-encoded credentials>
	    POST https://%iot.host%:443/iot/core/api/v1/sensors  
	    {
		    "deviceId" : "%device.id%",
		    "sensorTypeId" : "0",
		    "name" : "%random.sensor.name%"
	    }
	    ```
	    >Note: A new sensor will be mapped to the pre-configured Sensor Type having ID "0".
6. Get device PEM-certificate.
    ```
    Authorization: Basic <base64-encoded credentials>
    GET https://%iot.host%:443/iot/core/api/v1/devices/%device.id%/authentication/pem
    ```
7. Create Java SSL context based on the PEM certificate.
8. As a device, subscribe for incoming commands over MQTT.
    ```
    SUBSCRIBE ssl://%iot.host%:8883 on topic 'commands/%device.alternate.id%'  
    ```
    >Note: A subscription is going to be terminated automatically after 20 seconds.

9. Send Display Text commands containing "Hello IoT" text to the the device.
    ```
    Authorization: Basic <base64-encoded credentials>
    POST https://%iot.host%:443/iot/core/api/v1/devices/%device.id%/commands
    {
	    "capabilityId" : "%display.text.capability.id%",
	    "sensorId" : "%sensor.id%",
    	"command" : {
		    "Text" : "Hello IoT"
	    }
    }
    ```
    >Note: The sending rate is one command per second. Duration is 5 seconds.