# iOS Sample for the HANA Cloud Platform IoT Services

The IoT Starterkit supports any iOS device (Apple iPhone / iPad). In the example we use a native mobile application to register a device via the [Remote Device Management API](https://help.hana.ondemand.com/iot/frameset.htm?2e2fe26905c247668f1e61360846ce53.html) and send data to the [Message Management Service API](https://help.hana.ondemand.com/iot/frameset.htm?8e1c277be0cd4854943a15f86188aaec.html) with HTTP Post requests.

Prerequsites:

* Apple hardware (MacBook, iMac, etc.) to develop native iOS applications
* Latest version of [XCode](https://developer.apple.com/xcode/) installed

Please follow these steps to get the example up and running:

* Create Device Model
    * Create a Device Type and Message Type according as described here [Create Device Information](https://github.com/SAP/iot-starterkit/tree/master/src/prerequisites/cockpit)
    * Copy *Device Type ID*, *Device Registration Token* and *Message Type ID*
    * Copy *Account ID* and *Data Center* (e.g., hanatrial or hana.eu1) which can be retrieved from the IoT Cockpit URL

* Deploy Application
    * Download the XCode project *iot-starter-kit* from github
    * Open the XCode project *iot-starterkit-ios.xcodeproj*
    * Select the target device (iOS Simulator or native device)
    * Press *Build*
    * The app starts in the iOS Simulator or on your device

* Register Device
    * Enter your *Account ID*, *Data Center*
    * Enter *Device Type ID* and *Device Registration Token*
    * Press *Register Device*
    * In case of successfull registration the field *Device ID* will be filled and the local variable *deviceToken* will be filled (you can see in the console the according loggin information=

* Send Message (device registration must be successfully executed)
    * Enter *Message Type ID*
    * Press *Send Message*
    * A message will be send with a random value (1 - 100) as value