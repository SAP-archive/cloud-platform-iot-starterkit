# Intel Edison + SAP HCP IoT Services

## Prerequisites

Follow the instructions [here](https://github.com/SAP/iot-starterkit/tree/master/src/prerequisites/account) to set up your SAP HCP account, enable IoT Services and set up the message types, device type & device needed for this tutorial. Make sure to note down the following while following the instructions:

- Device ID
- Outbound Message ID
- Inbound Message ID
- OAuth Access Token

## Setting Up the Edison

Follow the steps below after following [Intel's instructions](https://software.intel.com/en-us/iot/library/edison-getting-started) on setting up the board to work with Intel XDK.

### Hardware

**Hardware Required:**

> If you do not have a particular sensor, please omit the relevant JS function from `src/main.js` when uploading the project onto your board.

- Intel Edison Compute Module
- Arduino Expansion Board
- Grove Base Shield
- Grove Cable (x4)
- Grove Temperature Sensor
- Grove Light Sensor
- Grove Rotary Angle Sensor
- Grove LED Socket
- LED

**Setup:**

1. Connect the Grove Base Shield to the Arduino Expansion Board.
2. Connect the LED Socket to `D2` on the Base Shield and plug an LED into the socket.
3. Connect the Temperature Sensor to `A0` on the Base Shield.
4. Connect the Light Sensor to `A1` on the Base Shield.
5. Connect the Rotary Angle Sensor to `A2` on the Base Shield.

## Running the Project on Edison

- Start a new blank project on Intel XDK.
- Copy over the code from `src/main.js` & `src/package.json` to `main.js` & `package.json` respectively in your new XDK project.
- In `main.js`, set up the variables from Line 13 - Line 26 according to your own HCP account. You can get these values from your IoT Services Cockpit.

```js
/********************************************************************* 
                  Set Below Variables Accordingly
*********************************************************************/
// IoT Server Host (Message Management Service URL)
var hostIoT = 'iotmms<...>.hanatrial.ondemand.com';
// Path to Websocket Endpoint
var pathIoT = '/com.sap.iotservices.mms/v1/api/ws/data/';
// OAuth Token
var authStrIoT = 'Bearer <Your Token>';
// Device ID
var deviceID = '<Device ID>';
// Message IDs
var outMessageID = '<Outbound Message ID>';
var inMessageID = '<Inbound Message ID>';
```

- Upload the project onto your Edison & run it. If set up correctly, you should see output like this:

```
Websocket Connected.
Data #1 Sent
Data #2 Sent
Data #3 Sent
...
```

### Accessing the Data

1. Open up Message Management Service (MMS).
2. Click the "Display stored messages" tile.
3. The table with the data from the Edison will be named `T_IOT_<Outbound Message ID>`.

### Turning the LED On/Off

1. Open up Message Management Service (MMS).
2. Click the "Push messages to device" tile.
3. Enter your Device ID (from IoT Services Cockpit).
4. Select "ws" as the Method from the dropdown.
5. The message should be: `{"messageType":"<Inbound Message ID>","messages":[{"opcode":"LED Switch","operand":"<1 or 0>"}]}`.
6. Push the message. The LED connected to the Edison should switch on/off according to the operand value.
