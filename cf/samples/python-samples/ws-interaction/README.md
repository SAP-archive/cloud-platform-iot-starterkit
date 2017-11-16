This sample shows the upstream transfer of data received via a Websocket link
and keeping this link continously alive. We indicate potential EXTENSION
POINTS where the further enhancement for specific usage can be done.

The IoT Service specific creation of device instances with their specifics
(capabilities with properties, sensortypes, sensors) can be done with the basic
samples we provide for this purpose and is assumed as a precondition for sample
usage.

* [server part for testing](./ws-server/ws-server.py)
* [client part with IoTS upstream interaction](./ws-client-including-keepalive/ws-client-including-keepalive.py)
