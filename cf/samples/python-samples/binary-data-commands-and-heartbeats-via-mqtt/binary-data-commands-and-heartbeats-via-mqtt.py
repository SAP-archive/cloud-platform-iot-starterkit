# example for receiving binary content and commands via the downstream channel
# and sending data (e.g. a regular heartbeat) via the upstream channel

import sys
import json
import base64
import re
import time
from time import gmtime, strftime

# as an additional / non standard module pre-condition: 
# install Paho MQTT lib e.g. from https://github.com/eclipse/paho.mqtt.python
import paho.mqtt.client as mqtt

# ========================================================================
def on_connect_broker(client, userdata, flags, rc):
	print('Connected to MQTT broker with result code: ' + str(rc))

def on_subscribe(client, obj, message_id, granted_qos):
	print('on_subscribe - message_id: ' + str(message_id) + ' / qos: ' + str(granted_qos))

def on_message(client, obj, msg):
	# the msg, e.g. base64 encoded file content is often considerably large
	print('on_message - ' + msg.topic + ' ' + str(msg.qos))
	# print('on_message - ' + msg.topic + ' ' + str(msg.qos) + ' ' + str(msg.payload))
	# parse the fields of the payload
	json_payload=json.loads(msg.payload)

	# capabilityId=json_payload['capabilityId']
	# print('capabilityId: ' + capabilityId)

	# downstream command handling
	if (re.match(r'.*"command":{"command":"', str(msg.payload))):
		print('dealing with a control command')
		command=json_payload['command']
		# print('command: ' + str(command))

		control_command=str(command['command'])
		control_arguments=str(command['arguments'])
		print(control_command)
		print(control_arguments)

		# EXTENSION POINT
		# place additional activities, e.g. how to execute a specific command with its arguments here
		if (control_command == 'my_specific_command'):
			True

	# file(name) handling
	if (re.match(r'.*"command":{"filename":"', str(msg.payload))):
		print('dealing with a file(name)')
		command=json_payload['command']
		# print('command: ' + str(command))

		filename=str(command['filename'])
		md5sum=str(command['md5sum'])
		content=str(command['content'])

		print('filename: ' + filename)
		print('md5sum:   ' + md5sum)
		# the base64 encoded file content is often considerably large
		# print('content:  ' + content)

		data=base64.b64decode(content)
	
		file_2_write='./received.' + filename
		outfile=open(file_2_write, 'wb')
		outfile.write(data)
		outfile.close()

		# EXTENSION POINT
		# place additional activities, e.g. what to do with the received file here
		True
# ========================================================================

# === main starts here ===================================================

# read in configuration values from environment variables
config_broker='<hostname>'

config_credentials_key='./credentials.key'
config_credentials_crt='./credentials.crt'
config_crt_4_landscape='<landscape specific certificate>'

config_alternate_id_device='<alternate id for device>'
config_alternate_id_capability='<alternate id for capability>'
config_alternate_id_sensor='<alternate id for sensor>'

broker=config_broker
broker_port=8883

my_device=config_alternate_id_device
client=mqtt.Client(client_id=my_device)
client.on_connect=on_connect_broker
client.on_subscribe=on_subscribe
client.on_message=on_message

client.tls_set(config_crt_4_landscape, certfile=config_credentials_crt, keyfile=config_credentials_key)

client.connect(broker, broker_port, 60)

my_publish_topic='measures/' + my_device
my_subscription_topic='commands/' + my_device
client.subscribe(my_subscription_topic, 0)

client.loop_start()

heartbeat_interval=5*6
while True:
	# send_heartbeat=True
	send_heartbeat=False

	if send_heartbeat:
		time_string='GMT: ' + strftime('%Y-%m-%d %H:%M:%S', gmtime())
		my_mqtt_payload='{ "capabilityAlternateId" : "' + config_alternate_id_capability + '_upstream" , "measures" : [[ "type_alive", "' + time_string + '" ]], "sensorAlternateId":"' + config_alternate_id_sensor + '_upstream" }'

		print(my_mqtt_payload)
		result=client.publish(my_publish_topic, my_mqtt_payload, qos=0)
		print(result)

	time.sleep(heartbeat_interval)
	# print('in main loop')
	sys.stdout.flush()

