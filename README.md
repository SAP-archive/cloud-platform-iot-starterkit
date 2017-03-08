# Starter Kit for the SAP Cloud Platform Internet of Things

A Starter Kit for working with the [SAP Cloud Platform Internet of Things](http://help.hana.ondemand.com/iot)

The document and the code snippets will provide a complete end to end example of how to use the SAP Cloud Platform Internet of Things. You will learn how to enable it for your account as
well as how to send messages and how to receive push messages. Moreover, the samples for data consumption are provided. 

## Table of contents

* [Overview](#overview)
* [Getting started in the Cloud](#getting-started-in-the-cloud)
* [Sending messages from the device](#sending-messages-from-the-device)
* [Consuming the messages sent from the device](#consuming-the-messages-sent-from-the-device)
* [Pushing messages to the device](#pushing-messages-to-the-device)
* [Receiving the messages sent to the device](#receiving-the-messages-sent-to-the-device)
* [Integrated examples for IoT Devices](#integrated-examples-for-iot-devices)
* [Security Aspects](#security-aspects)
* [Revision history and related advice](#revision-history-and-related-advice)
* [What comes next](#what-comes-next)

## Overview

SAP Cloud Platform Internet of Things is designed to facilitate and support the implementation of Internet of Things applications. 
It provides interfaces for registering devices and their specific data types, sending data to a data base running in 
SAP Cloud Platform in a secure and efficient manner, storing the data in SAP Cloud Platform as well as provide easy access to the data stored.

The respective APIs are distributed across two main components: Remote Device Management Service (RDMS) and Message Management Service (MMS). 
Moreover, there is a web-based interface called Internet of Things Cockpit which provides easy access to the various functionalities.

![IoT Service Architecture](images/system_architecture.png "IoT Service Architecture")

MMS provides various APIs that can be used by devices to send data to the SAP Cloud Platform. It processes the data and persists the data 
in the attached databases. There may be other use cases, though, which require forwarding the data to other Message Brokers or Event Stream Processors.

The Internet of Things Cockpit is the main interface for users to interact with the Remote Device Management Service (RDMS). 
It can be used to register new devices, to define the schema of messages (devices types and message types) they can send and/or receive, 
as well as to establish the necessary trust relationship devices need to interact with MMS. The Internet of Things Cockpit and RDMS 
are provided on a subscription base in the Cloud.

### IoT Business Applications

IoT Business Applications can be built using either the HANA XS, the Java or HTML5 mechanisms provided by the SAP Cloud Platform infrastructure. 

## Getting started in the Cloud

1. [Get SAP Cloud Platform Developer Account](src/prerequisites/account)
2. [Enable Internet of Things](src/prerequisites/service)
3. [Create Device Information in Internet of Things Cockpit](src/prerequisites/cockpit)
4. [Deploy the Message Management Service (MMS)](src/prerequisites/mms)

## Sending messages from the device

* [Send messages with MMS built-in sample client using HTTP API](src/apps/built-in/sending/http)
* [Send messages with MMS built-in sample client using MQTT TCP/SSL API](src/apps/built-in/sending/mqtttcp)
* [Send messages with MMS built-in sample client using WebSocket API](src/apps/built-in/sending/ws)
* [Send messages with MMS built-in sample client using MQTT WebSocket API](src/apps/built-in/sending/mqttws)
* [Send messages with Python client using HTTP API](src/apps/python/sending/http)
* [Send messages with Python client using WebSocket API](src/apps/python/sending/ws)
* [Register a device and send messages using HTTP API with Certificate based authentication](src/apps/java/authentication/com.sap.iot.starterkit.cert)
* [Flexible ingest from an external MQTT broker using MQTT over WebSocket Data Service API](src/examples/java/com.sap.iot.starterkit.mqtt.ingest)

## Consuming the messages sent from the device

* [Consume the messages with MMS built-in client](src/apps/built-in/consumption)
* [Consume the messages with Web Application based on Java and UI5 using SAP Cloud Platform Persistence Service](src/apps/java/consumption)
* [Consume the messages with UI5 using the built-in MMS OData API](src/apps/ui5/consumption)
* [Consume the messages with UI5 using the built-in MMS OData API with advanced chart configuration and live data updates](src/apps/ui5/consumption-advanced)
* [Consume the messages with HANA XS using XSODATA and XSJS](src/apps/xs/consumption)
* [Consume the messages with Python using PyHDB](src/apps/python/consumption)
* [Consume the messages with Node.js using NodeHDB](src/apps/nodejs/consumption)

## Pushing messages to the device

* [Push messages with MMS built-in sample client using HTTP and WebSocket API](src/apps/built-in/pushing)
* [Push messages with Web Application based on Java and UI5 using HTTP API](src/apps/java/consumption)

## Receiving the messages sent to the device

* [Receive the messages with MMS built-in client](src/apps/built-in/receiving)
* [Receive the messages with Python client](src/apps/python/receiving)

## Integrated examples for IoT Devices

In order to get you up and running fast we provide examples that you may use both with or without specific IoT Device hardware.

### Working with device simulators

If you want to take a quick start with a device simulation that runs on your desktop you can use two Python programs with a graphical user interface. In
order to run the programs on your desktop (Windows and OSX have been tested) you need a Python installation and the urllib3 module installed.
Please refer to the [these instructions](src/examples/python/iot-starterkit-for-desktop/README.md) for guidance on how to meet these preconditions.

With the usage of
- the device simulation that you can start with ```python iot_starterkit_desktop.py``` and that produces the User Interface shown below

![Device Simulator](images/device_simulator.jpg?raw=true "Device Simulator")

- a program to send messages to the simulated device and switch its LED on or off that you start with ```python iot_starterkit_push_ui.py``` and that produces the User Interface shown below

![Push UI](images/push_ui.jpg?raw=true "Push UI")

you can already experience the functionality of an end-to-end scenario.

### Working with real IoT hardware

We provide instructions and code for usage with a Raspberry Pi and a GrovePi shield that lets you attach various input and output peripherals. In the
example we use a Slide Potentiometer for the input of values that can be changed by the user as well as an LED and an OLED graphical display for visible
output. You should be able to comfortably purchase these components and assemble them without having to solder.

![Starter Kit running](images/starterkit_running_01.jpg "Starter Kit running")

Please follow these [steps to setup and use the Raspberry Pi](./src/hardware/raspberry-pi/README.md) with the shown peripherals and the 
[example application](src/examples/python/iot-starterkit-for-pi-and-grove-peripherals/iot_starterkit_pi_and_grove_peripherals.py).

In addition to the Raspberry Pi we have already used the SAP Cloud Platform Internet of Things with a [variety of other hardware](./src/hardware).

## Security aspects

Security plays a very important role for the deployment of IoT scenarios in
productive environments. The Starter Kit examples concentrate of the aspect of
demonstrating basic IoT Services mechanisms. Inherently, specific usable
security mechanisms differ for different programming languages, frameworks or
hardware platforms. Thus, before making use of code snippets or integrated
examples in productive scenarios please pay additional attention on hardening
against potential attacks. We will continuously provide further guidance on
potential attack vectors and how to harden your solution on the dedicated
[Security aspects](./misc/security/README.md) page.

## Revision history and related advice

The SAP Cloud Platform Internet of Things is continuously developed further. Please pay attention to
the dedicated page [Revision history and related
advice](./misc/revision-history/README.md) to ensure you get specific
information about potentially necessary modifications to your older usage
examples.

## What comes next

The initial version of the IoT Starter Kit for SAP Cloud Platform Internet of Things as first published at SAPPHIRE 2015 intentionally tries to provide a simple and easy to
reproduce example. We welcome feedback on the adaptation of the HCP IoT Services and your own usage examples via the SCN page ["Try out the SAP Cloud Platform Internet of Things"](https://blogs.sap.com/2015/04/29/try-out-the-sap-hana-cloud-platform-internet-of-things-iot-services/).
