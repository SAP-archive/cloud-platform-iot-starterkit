# Java samples for the SAP Cloud Platform Internet of Things for the Cloud Foundry Environment

Contains various Java samples which illustrate the API usage across different use cases.

### Prerequisites

* Maven
* JDK 1.8 or higher

>IMPORTANT: After the very first import from GitHub, ensure to run `mvn clean install` for the current reactor project in order to build [commons](./commons) - a Maven module containing generic logic which is used by the provided samples.

## Upstream. Sending measures from the device.

* [Model "Ambient" measure and send its values either through HTTP or MQTT to the Gateway as well as consume them later on via the API](./send-measure)

## Downstream. Sending commands to the device.

* [Model "Switch" command and send it to the device as well as listen to incoming commands on the device side](./send-command)