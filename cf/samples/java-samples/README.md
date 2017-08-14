# Java samples for the SAP Cloud Platform Internet of Things for the Cloud Foundry Environment

Contains various Java samples which illustrate the API usage across different use cases.

* [Send default Temperature measures either through HTTP or MQTT to the Gateway Cloud and consume them later on via the API](./send-gateway-cloud-default-measure)
* [Send custom Humidity measures either through HTTP or MQTT to the Gateway Cloud and consume them later on via the API](./send-gateway-cloud-custom-measure)
* [Send default Toggle Valve commands to the device and listen to them on the device side](./send-core-service-default-command)

>IMPORTANT: After the very first import from GitHub, ensure to run `mvn clean install` for the current reactor project in order to build [commons](./commons) - a Maven module containing generic logic which is used by the provided samples.
