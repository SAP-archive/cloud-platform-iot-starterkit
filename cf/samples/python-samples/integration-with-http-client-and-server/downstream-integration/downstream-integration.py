# example for receiving commands via IoT Service CF
# based on the received command data we trigger a request to a downstream HTTP server

import sys
import time
import json
import re
import requests

# as an additional / non standard module pre-condition:
# install Paho MQTT lib e.g. from https://github.com/eclipse/paho.mqtt.python
import paho.mqtt.client as mqtt

config_broker='<IoTS CF instance>'

config_credentials_key='./credentials.key'
config_credentials_crt='./credentials.crt'
config_crt_4_landscape='<landscape specific certificate>'

config_alternate_id_device='<alternate id for device>'
config_alternate_id_sensor='<alternate id for sensor>'

config_httpserver_url='localhost:8080/test_endpoint'

def do_request_2_httpserver(data):
	global config_httpserver_addr
	request_url=config_httpserver_url

	headers=''
	payload=''
	
	# EXTENSION POINT
	# choose what Content-Type and payload (depending on data received
	# from IoTS) to send, can e.g. also be XML for SOAP
	headers_example={'Content-Type':'application/json; charset=utf-8'}
	payload_example='example payload'

	headers=headers_example
	payload=payload_example
			
	# print(headers)
	# print(payload)

	try:
		response=requests.post(request_url, data=payload, headers=headers)
		print(response.status_code)
		print(response.headers)
		print(response.text)
	except:
		print("exception occured")
	sys.stdout.flush()

# === interaction with the external broker ===
broker=config_broker
broker_port=8883

def on_connect_broker(client, userdata, flags, rc):
	print("Connected to MQTT broker with result code " + str(rc))

def on_subscribe(client, obj, message_id, granted_qos):
	print("on_subscribe - message_id: " + str(message_id) + " / qos: " + str(granted_qos))

def on_message(client, obj, msg):
	print("on_message - " + msg.topic + " " + str(msg.qos) + " " + str(msg.payload))
	# print("on_message - " + msg.topic + " " + str(msg.qos))
	# parse the fields of the payload
	json_payload=json.loads(msg.payload)

	# capabilityId=json_payload['capabilityId']
	# print('capabilityId: ' + capabilityId)

	command=json_payload['command']
	print('command: ' + str(command))
	if (re.match(r'.*example_command', str(command))):
		do_request_2_httpserver(str(command))

my_device=config_alternate_id_device

client=mqtt.Client(client_id=my_device)
client.on_connect=on_connect_broker
client.on_subscribe=on_subscribe
client.on_message=on_message

client.tls_set(config_crt_4_landscape, certfile=config_credentials_crt, keyfile=config_credentials_key)

not_connected=True
while not_connected:
# {
	try:
		client.connect(broker, broker_port, 60)
		not_connected=False
	except:
		print("not connected yet")
		sys.stdout.flush()
		time.sleep(5)
# } while

my_subscription_topic='commands/' + my_device
client.subscribe(my_subscription_topic, 0)

client.loop_start()

interval=5
while True:
	time.sleep(interval)
	print("in main loop")
	sys.stdout.flush()
