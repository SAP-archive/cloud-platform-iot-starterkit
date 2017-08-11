# read in configuration values from environment variables

import os

config_broker=os.environ['INSTANCE']
config_credentials_key="./"+os.environ['CREDENTIALS_FILE']+".key"
config_credentials_crt="./"+os.environ['CREDENTIALS_FILE']+".crt"
config_crt_4_landscape="./"+os.environ['CRT_4_LANDSCAPE']
config_physical_address_device=os.environ['PHYSICAL_ADDRESS_4_DEVICE']
config_capability=os.environ['MY_CAPABILITY']
config_physical_address_sensor=os.environ['PHYSICAL_ADDRESS_4_SENSOR']

import sys
import paho.mqtt.client as mqtt
import time

# === interaction with the external broker ===
broker=config_broker
broker_port=8883

def on_connect_broker(client, userdata, flags, rc):
    print("Connected to MQTT broker with result code " + str(rc))

my_device=config_physical_address_device

client = mqtt.Client(client_id=my_device)
client.on_connect = on_connect_broker

client.tls_set(config_crt_4_landscape, certfile=config_credentials_crt, keyfile=config_credentials_key)

client.connect(broker, broker_port, 60)

client.loop_start()

my_publish_topic='measures/' + my_device
my_mqtt_payload='{ "measureIds" : [ ' + config_capability + ' ], "values" : [ "value for A", "value for B" ], "logNodeAddr":"' + config_physical_address_sensor + '" }'

interval=5
while 1==1:
	time.sleep(interval)
	print("trying to publish")
	result=client.publish(my_publish_topic, my_mqtt_payload, qos=0)
	print(result)
