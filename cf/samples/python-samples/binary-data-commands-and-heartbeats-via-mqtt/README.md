This sample shows the downstream transfer of binary data (e.g. files) and
commands combined with upstream heartbeats. We indicate potential EXTENSION
POINTS where the further enhancement for specific usage can be done.

The IoT Service specific creation of device instances with their specifics
(capabilities with properties, sensortypes, sensors) can be done with the basic
samples we provide for this purpose and is assumed as a precondition for sample
usage.

* [device side part](./binary-data-commands-and-heartbeats-via-mqtt.py)
* [test application sending a file via IoT Service CF commands](./test-app-sending-a-file.py)
