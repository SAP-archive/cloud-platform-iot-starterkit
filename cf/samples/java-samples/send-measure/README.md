# SAP Internet of Things for the Cloud Foundry Environment
A sample Java application which is capable to send Ambient measures to the Gateway Cloud either via HTTP or MQTT protocol using the device certificate based authentication and consume them later on via RESTful API. 

## Import project
This sample application is provided as Maven project and could be imported to IDE with the help of respective plug-in or Maven command line interface.

## Run application from the IDE
- Run [Main class](src/main/java/sample/Main.java) as Java application.

## Build with Maven
It is possible to build an executable JAR with Maven. Simply run `mvn clean install` from the command line.

>Note: After the very first import from GitHub, ensure to build the top-level [java-samples](../) reactor project.

### Run compiled version
- Find the compiled version under project's `target` directory
- Execute from the command line `java -jar send-measure.jar`

>Note: In order to save efforts when typing sample properties every time you launch an application, you may place the `sample.properties` file at the same level to your executable JAR. A template for such a file could be found under [resources](src/main/resources/sample.properties)

![In Action](src/main/resources/send-measure_0.jpg "In Action")
![In Action](src/main/resources/send-measure_1.jpg "In Action")

## Execution Steps
The following steps are being performed during execution:

1. Get online gateway (either REST or MQTT) based on the user input.
    ```
    Authorization: Basic <base64-encoded credentials>
    GET https://%iot.host%:443/%instance.id%/iot/core/api/v1/tenant/%tenant.id%/gateways?filter=protocolId eq '%gateway.protocol.id%' and status eq 'online' and type eq 'cloud'
    ```
2. Get online device by its identifier.
    ```
    Authorization: Basic <base64-encoded credentials>
    GET https://%iot.host%:443/%instance.id%/iot/core/api/v1/tenant/%tenant.id%/devices/%device.id%
    ```
	1. Create a new device if it does not exist or no online device with the specified identifier was found in the gateway.
	    ```
	    Authorization: Basic <base64-encoded credentials>
	    POST https://%iot.host%:443/%instance.id%/iot/core/api/v1/tenant/%tenant.id%/devices  
		{  
		   "gatewayId":"%gateway.id%",
		   "name":"SampleDevice"
		}
	    ```
3. Check if custom "Ambient" capability exists.
    ```
    Authorization: Basic <base64-encoded credentials>
    GET https://%iot.host%:443/%instance.id%/iot/core/api/v1/tenant/%tenant.id%/capabilities
    ```
	1. Create "Ambient" capability if not found.
	    ```
	    Authorization: Basic <base64-encoded credentials>
	    POST https://%iot.host%:443/%instance.id%/iot/core/api/v1/tenant/%tenant.id%/capabilities  
		{  
		   "alternateId":"ambient",
		   "name":"Ambient",
		   "properties":[  
		      {  
		         "name":"Humidity",
		         "dataType":"integer",
		         "unitOfMeasure":"%"
		      },
		      {  
		         "name":"Temperature",
		         "dataType":"float",
		         "unitOfMeasure":"°C"
		      },
		      {  
		         "name":"Light",
		         "dataType":"integer",
		         "unitOfMeasure":"Lux"
		      }
		   ]
		}
	    ```
4. Check if custom "Switch" capability exists.
    ```
    Authorization: Basic <base64-encoded credentials>
    GET https://%iot.host%:443/%instance.id%/iot/core/api/v1/tenant/%tenant.id%/capabilities
    ```
	1. Create "Switch" capability if not found.
	    ```
	    Authorization: Basic <base64-encoded credentials>
	    POST https://%iot.host%:443/%instance.id%/iot/core/api/v1/tenant/%tenant.id%/capabilities  
		{  
		   "alternateId":"switch",
		   "name":"Switch",
		   "properties":[  
		      {  
		         "name":"Text",
		         "dataType":"string"
		      },
		      {  
		         "name":"LED",
		         "dataType":"boolean"
		      }
		   ]
		}
	    ```
5. Check if custom "ControlUnit" sensor type exists.
    ```
    Authorization: Basic <base64-encoded credentials>
    GET https://%iot.host%:443/%instance.id%/iot/core/api/v1/tenant/%tenant.id%/sensorTypes
    ```
	1. Create "ControlUnit" sensor type if not found.
	    ```
	    Authorization: Basic <base64-encoded credentials>
	    POST https://%iot.host%:443/%instance.id%/iot/core/api/v1/tenant/%tenant.id%/sensorTypes  
		{  
		   "name":"ControlUnit",
		   "capabilities":[  
		      {  
		         "id":"%ambient.capability.id%",
		         "type":"measure"
		      },
		      {  
		         "id":"%switch.capability.id%",
		         "type":"command"
		      }
		   ]
		}
	    ```
6. Get device sensor which is assigned to the device.
    1. Create a new sensor and assign it to the device if no sensor is assigned to the device or a sensor has no reference to the "ControlUnit" sensor type.
	    ```
	    Authorization: Basic <base64-encoded credentials>
	    POST https://%iot.host%:443/%instance.id%/iot/core/api/v1/tenant/%tenant.id%/sensors  
		{  
		   "deviceId":"%device.id%",
		   "sensorTypeId":"%sensor.type.id%",
		   "name":"SampleSensor"
		}
	    ```
	    >Note: A new sensor will be mapped to the custom "ControlUnit" Sensor Type.
7. Get device PEM-certificate.
    ```
    Authorization: Basic <base64-encoded credentials>
    GET https://%iot.host%:443/%instance.id%/iot/core/api/v1/tenant/%tenant.id%/devices/%device.id%/authentications/clientCertificate/pem
    ```
8. Create Java SSL context based on the PEM certificate.
9. Send random Ambient measures on behalf of the device sensor.
    ```
    Authorization: <device-certificate>
    REST: POST https://%iot.host%:443/iot/gateway/rest/measures/%device.alternate.id%
    MQTT: PUBLISH ssl://%iot.host%:8883 on topic 'measures/%device.alternate.id%'  

	{  
	   "capabilityAlternateId":"%ambient.capability.alternate.id%",
	   "sensorAlternateId":"%sensor.alternate.id%",
	   "measures":[  
	      [  
	         "%random.humidity.percentage%",
	         "%random.degrees.celsius%",
	         "%random.light.illuminance%"
	      ]
	   ]
	}
    ```
    >Note: The sending rate is one measure per second. Duration is 5 seconds.

10. Consume the latest measures via the RESTful API.
    ```
    Authorization: Basic <base64-encoded credentials>
    GET https://%iot.host%:443/iot/processing/api/v1/tenant/%tenant.id%/measures/capabilities/%ambient.capability.id%?orderby=timestamp desc&filter=deviceId eq '%device.id%'&top=25
    ```

