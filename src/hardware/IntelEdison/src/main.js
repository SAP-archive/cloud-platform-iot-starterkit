/*jslint node:true, vars:true, bitwise:true, unparam:true*/
/*jshint unused:true*/

/********************************************************************* 
                       Require Node Modules
*********************************************************************/
var groveSensor = require('jsupm_grove');
var mraa = require('mraa');
var WebSocket = require('ws');
var request = require('request');


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


/********************************************************************* 
                        Init Sensors & Pins
*********************************************************************/
var tempSensor = new groveSensor.GroveTemp(0);      // Temp Sensor at A0
var lightSensor = new groveSensor.GroveLight(1);    // Light Sensor at A1
var rotarySensor = new groveSensor.GroveRotary(2);  // Rotary Angle Sensor at A2

var LEDPin = new mraa.Gpio(2);  // LED plugged into LED Socket at D2
LEDPin.dir(mraa.DIR_OUT);


/********************************************************************* 
                          Init Websocket
*********************************************************************/
var options = {
    headers: {
        Authorization: authStrIoT
    }
};

var ws = new WebSocket('wss://' + hostIoT + pathIoT + deviceID, options);

ws.on('open', function () {
    console.log("Websocket Connected");

    // On WebSocket connection, start sending data every two seconds.
    startTempWatch();
    startLightWatch();
    startRotaryWatch();
});
ws.on('close', function () {
    console.log("Websocket Disconnected");
});
ws.on('error', function (error) {
    console.log("ERROR: " + error);
});
ws.on('message', setLED); // On receiving a message, switch on/off the LED.


/********************************************************************* 
                         Main Functions
*********************************************************************/
var dataNum = 0; // Data Counter

function setLED(data) {
    data = JSON.parse(data);

    // Verify the inbound message ID & opcode --> Switch on/off LED according
    // to the operand.
    if (data.messageType == inMessageID) {
        if (data.messages[0].opcode === 'LED Switch') {
            if (data.messages[0].operand == 1) {
                LEDPin.write(1);
                console.log("LED Switched On");
            } else if (data.messages[0].operand == 0) {
                LEDPin.write(0);
                console.log("LED Switched Off");
            }
        }
    }
}

function startTempWatch() {
    setInterval(function () {
        var a = tempSensor.raw_value();
        var resistance = (1023 - a) * 10000 / a;
        var celsius_temperature = 1 / (Math.log(resistance / 10000) / 3975 + 1 / 298.15) - 273.15;

        updateIoT('TempSensor', celsius_temperature);
    }, 2000);
}

function startLightWatch() {
    setInterval(function () {
        updateIoT('LightSensor', lightSensor.value());
    }, 2000);
}

function startRotaryWatch() {
    setInterval(function () {
        updateIoT('RotarySensor', rotarySensor.abs_deg());
    }, 2000);
}

function updateIoT(sensor, value) {
    dataNum++;

    // Generate timestamp.
    var date = new Date();
    var time = Math.round(date.getTime() / 1000);

    // JSON data format for MMS messages.
    var jsonData = {
        mode: 'async',
        messageType: outMessageID,
        messages: [{
            timestamp: time,
            sensor: sensor,
            value: value
        }]
    };
    var strData = JSON.stringify(jsonData);

    // Send message to MMS through the WebSocket.
    ws.send(strData, console.log("Data #" + dataNum + " Sent"));
}
