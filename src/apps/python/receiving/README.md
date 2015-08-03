# Receiving the messages sent to the device

## Receive the messages with Python client

The IoT Starterkit provides code snippets that show the basic interactions of an IoT device with the HCP IoT Services. These primitives are either sending or
retrieving data which are shown in 2 individual snippets for usage with an HTTP transport and a combined one for usage via a bi-directional WebSocket
transport. All examples use encrypted communication (https, wss) which is a prerequisite for the interaction with HCP. The snippets are provided in Python
which is also the basis for the Raspberry Pi and Device Simulation integrated examples. Nevertheless, the respective service calls can of course also be
implemented in other programming languages.

* [retrieve.py](../../../code-snippets/python/hcp-iot-services/https/retrieve.py): Using a GET request to poll for messages pushed towards the device

This code snippet have already been integrated in [examples](./src/examples/python) that can directly be used.