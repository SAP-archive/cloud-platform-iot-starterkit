The example in this directory demonstrates the usage of MQTT over WSS for
upstream (publish) and downstream (receive messages for the "push" topic you
subscribed to) interaction of a device. You can either use  OAuth or a Client
Certificate (works only on beta-enabled accounts on PROD) to authenticate the
device. To do so, please uncomment the respective version. There is also an
example on how to register and authenticate a device using [Client Certificate
Authentication](../../auth).

It uses the development version of the Eclipse Paho Python MQTT that starting
from May 2017 supports a arbitrary (not just /mqtt) path for the WebSocket
application. We therefore adapted our source code that formerly relied on an
individual patch to the library. Please find the instructions for installing
the development version of the library below. 

```
# Instructions for installing / using the MQTT client

# Clone MQTT python client library repository
git clone -b develop https://github.com/eclipse/paho.mqtt.python

# Install MQTT python client library
# needs root rights (use sudo on Linux / OSX)
python setup.py install

# Run MQTT sample
cd..
python mqtt-wss-sample.py
```
