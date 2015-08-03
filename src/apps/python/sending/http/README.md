# Sending messages from the device

## Send messages with Python client using HTTP API

The IoT Starterkit provides code snippets that show the basic interactions of an IoT device with the HCP IoT Services. These primitives are either sending or
retrieving data which are shown in 2 individual snippets for usage with an HTTP transport and a combined one for usage via a bi-directional WebSocket
transport. All examples use encrypted communication (https, wss) which is a prerequisite for the interaction with HCP. The snippets are provided in Python
which is also the basis for the Raspberry Pi and Device Simulation integrated examples. Nevertheless, the respective service calls can of course also be
implemented in other programming languages.

* [insert.py](../../../../code-snippets/python/hcp-iot-services/https/insert.py): Using a POST request to send data from the device upstream

This code snippet have already been integrated in [examples](../../../../examples/python) that can directly be used.