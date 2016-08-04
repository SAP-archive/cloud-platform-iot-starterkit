The example in this directory demonstrates the usage of MQTT over WSS for
upstream (publish) and downstream (receive messages for the "push" topic you
subscribed to) interaction of a device.

It uses the Eclipse Paho Python MQTT lib that needs a small patch in order to
allow a arbitrary (not just /mqtt) path for the WebSocket application. Please
find the instructions for installing a patched version of the library below. We
also work (pull request is issued) on getting the patch into the official
version so that the patch is no longer necessary.

```
# Instructions for installing / using the MQTT client

# Clone MQTT python client library repository
git clone https://github.com/eclipse/paho.mqtt.python

# Apply patch
cd paho.mqtt.python
git apply ../client.patch

# Install MQTT python client library
# needs root rights (use sudo on Linux / OSX)
python setup.py install

# Run MQTT sample
cd..
python mqtt-wss-sample.py
```
