The example demonstrates how to connect to the IoT Services via MQTT over WebSocket. The Java sample client creates a connection to MMS, subscribes to the log topic and and publishes a message to the data topic. More information about the MQTT over WebSocket data service can be found [here](https://help.hana.ondemand.com/iot/frameset.htm?56d02092904346c1a605713021d2f875.html). The sample code uses the Paho Java MQTT libs.

Before executing the sample, you need to make sure to edit the [IoTServicesSample class](src/main/java/mqtt/client/IoTServicesSample.java) by adding your HCP account Id, the device Id, the OAuth token as well as the actual message.

After you finished the configuration you can build and run the project.

```
# Build project
mvn clean package

# Run the project
java -jar target/mqtt.client-0.0.1.jar
```
