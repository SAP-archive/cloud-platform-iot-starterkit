# all configuration settings come from config.py
try:
	import config
except ImportError:
	print("Please copy template-config.py to config.py and configure appropriately !"); exit();

import sys
import paho.mqtt.client as mqtt
import time
import random

# === START === values set from the config.py file ===
my_endpoint             = "iotmms" + config.hcp_account_id + config.hcp_landscape_host
my_endpoint_certificate = config.endpoint_certificate
# only needed when using Client Certificate Authentication; my_username and my_password can be skipped in this case
# my_client_certificate 	= config.client_certificate
# my_client_key 			= config.client_key
my_device_id            = config.device_id

my_client_id            = my_device_id
my_username             = my_device_id
my_password             = config.oauth_credentials_for_device

my_message_type_upstream = config.message_type_id_From_device
# === END ===== values set from the config.py file ===

my_endpoint_url_path    = "/com.sap.iotservices.mms/v1/api/ws/mqtt"

# for upstream communication
my_publish_topic      = "iot/data/" + my_device_id
# for downstream communication
my_subscription_topic = "iot/push/" + my_device_id

is_connected = False

def on_connect(mqttc, obj, flags, rc):
	print("on_connect   - rc: " + str(rc))
	global is_connected
	is_connected = True

# you can use the push API (e.g. also from the built-in sample UI) to send to the device
def on_message(mqttc, obj, msg):
	print("on_message   - " + msg.topic + " " + str(msg.qos) + " " + str(msg.payload))

def on_publish(mqttc, obj, message_id):
	print("on_publish   - message_id: " + str(message_id))

def on_subscribe(mqttc, obj, message_id, granted_qos):
	print("on_subscribe - message_id: " + str(message_id) + " / qos: " + str(granted_qos))

def on_log(mqttc, obj, level, string):
	print(string)

mqttc = mqtt.Client(client_id=my_client_id, transport='websockets')
mqttc.on_message = on_message
mqttc.on_connect = on_connect
mqttc.on_publish = on_publish
mqttc.on_subscribe = on_subscribe

mqttc.tls_set(my_endpoint_certificate)
# to use Client Certificate Authentication, also specifiy the client certificate and key; setting the username and password can be skipped in this case
# mqttc.tls_set(my_endpoint_certificate, my_client_certificate, my_client_key)
mqttc.username_pw_set(my_username, my_password)
mqttc.endpoint_url_path_set(my_endpoint_url_path)
mqttc.connect(my_endpoint, 443, 60)

# you can use the push API (e.g. also from the built-in sample UI) to send to the device
mqttc.subscribe(my_subscription_topic, 0)

mqttc.loop_start()

publish_interval=5
value=0
while 1==1:
	if is_connected == True:
		timestamp = int(time.time())

	# == START ==== fill the payload now - in this example we use the typical IoT Starterkit payload ====== 
		my_mqtt_payload='{"messageType":"' + my_message_type_upstream + '","messages":[{'
		my_mqtt_payload=my_mqtt_payload + '"sensor":"mqtt-example", '
		my_mqtt_payload=my_mqtt_payload + '"value":"' + str(value) + '", '
		my_mqtt_payload=my_mqtt_payload + '"timestamp":' + str(timestamp)
		my_mqtt_payload=my_mqtt_payload + '}]}'
	# == END ====== fill the payload now - in this example we use the typical IoT Starterkit payload ====== 

		print(my_mqtt_payload)
		mqttc.publish(my_publish_topic, my_mqtt_payload, qos=0)
		value=value+10
		if (value > 100):
			value=0
	else:
		print("still waiting for connection")
	time.sleep(publish_interval)
