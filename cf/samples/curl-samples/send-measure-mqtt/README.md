This example shows a sequence of steps to describe and instantiate a device and
then send data via MQTT publish requests. The MQTT code is done in Python and
uses the [Paho MQTT lib](https://github.com/eclipse/paho.mqtt.python). All
configuration is in the file [config.sh](./config.sh).

steps:
* step_01_get_gateways.sh
* step_02_create_device.sh
* step_03_get_my_device.sh
* step_04_get_certificate.sh
* step_05_create_capability.sh
* step_06_create_sensortype.sh
* step_07_create_sensor.sh
* step_08_send_data.sh - calls mqtt-ingest.py
* step_09_get_measures.sh
