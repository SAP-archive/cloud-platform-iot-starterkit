# create an IoTS device of choice with just one program
# and no necessary human interaction
# write a script for credential conversion
# finally write 2 scripts to start listening for downstream commands with an MQTT client
# and to originate a downstream command

config_instance='<IoTS CF instance>'
config_tenant='<config tenant>'
config_user='<user>'
config_password='<password>'

config_crt_4_landscape='<server certificate for the instance in your landscape>'
# e.g. './eu10cpiotsap.crt'

config_alternateId_4_device_base='<chosen alternateId base>'
config_alternateId_4_device_version='_01'

import sys
import requests
import json

alternateId_4_device=config_alternateId_4_device_base+config_alternateId_4_device_version
alternateId_4_capability_up01='c_up01_' + config_alternateId_4_device_base
alternateId_4_capability_up02='c_up02_' + config_alternateId_4_device_base
alternateId_4_capability_down01='c_down01_' + config_alternateId_4_device_base
alternateId_4_capability_down02='c_down02_' + config_alternateId_4_device_base
alternateId_4_sensortype=config_alternateId_4_device_base
alternateId_4_sensor=alternateId_4_device
certfile_name='./cert.pem'

# ======================================================================== 
# these values are filled as you go through the steps
gw_id_4_mqtt=''
my_device=''
my_capability_up01=''
my_capability_up02=''
my_capability_down01=''
my_capability_down02=''
my_sensortype=''
my_sensor=''
# ======================================================================== 

print('listing gateways')
request_url='https://' + config_instance + '/iot/core/api/v1/tenant/' + config_tenant + '/gateways'
headers={'Content-Type' : 'application/json'}
response=requests.get(request_url, headers=headers, auth=(config_user, config_password))
status_code=response.status_code
if (status_code == 200):
	print(response.text)
	try:
		json_payload=json.loads(response.text)
		for individual_dataset in json_payload:
			print(individual_dataset['id'] + ' - ' + individual_dataset['protocolId'])
			if ((individual_dataset['protocolId'] == 'mqtt') and (individual_dataset['status'] == 'online')):
				gw_id_4_mqtt=individual_dataset['id']
				print('Using gateway: ' + gw_id_4_mqtt)
	except (ValueError) as e:
                print(e)
# ===

print('creating the device')
request_url='https://' + config_instance + '/iot/core/api/v1/tenant/' + config_tenant + '/devices'
headers={'Content-Type' : 'application/json'}
payload='{ "gatewayId" : "' + gw_id_4_mqtt + '", "name" : "device_' + alternateId_4_device + '", "alternateId" : "' + alternateId_4_device + '" }'
response=requests.post(request_url, headers=headers, auth=(config_user, config_password), data=payload)
status_code=response.status_code
print(str(status_code) + " " + str(response.text))
if (status_code == 200):
	try:
		json_payload=json.loads(response.text)
		my_device=json_payload['id']
		print('Using device id: ' + my_device)
	except (ValueError) as e:
                print(e)
else:
	exit(0)
# ===

print('retrieving the certificate')
request_url='https://' + config_instance + '/iot/core/api/v1/tenant/' + config_tenant + '/devices/' + my_device + '/authentications/clientCertificate/pem'
headers={'Content-Type' : 'application/json'}
response=requests.get(request_url, headers=headers, auth=(config_user, config_password))
status_code=response.status_code
print(str(status_code) + " " + str(response.text))
if (status_code == 200):
	try:
		json_payload=json.loads(response.text)
		secret=json_payload['secret']
		pem=json_payload['pem']
		print('secret: ' + secret)
		print('pem: ' + pem)
		certfile=open("cert.pem", "w")
		certfile.write(pem)
		certfile.close()

		pem_script=open("convert_pem.sh", "w")

		pem_script.write("echo 'Please use pass phrase " + secret + " for the certificate import from " + certfile_name + " in the conversion !'\n\n")

		pem_script.write("openssl rsa -in " + certfile_name + " -out credentials.key\n")
		pem_script.write("openssl x509 -in " + certfile_name + " -out credentials.crt\n")
		pem_script.close()
	except (ValueError) as e:
                print(e)
else:
	exit(0)
# ===

print('creating the capability (up01)')
request_url='https://' + config_instance + '/iot/core/api/v1/tenant/' + config_tenant + '/capabilities'
headers={'Content-Type' : 'application/json'}
payload='{ "name" : "capability_up01_' + alternateId_4_capability_up01 + '", "properties" : [ { "name" : "p01_up01", "dataType" : "string" }, { "name" : "p02_up01", "dataType" : "string" } ], "alternateId" : "' + alternateId_4_capability_up01 + '" }'
print(payload)
response=requests.post(request_url, headers=headers, auth=(config_user, config_password), data=payload)
status_code=response.status_code
print(str(status_code) + " " + str(response.text))
if (status_code == 200):
	try:
		json_payload=json.loads(response.text)
		my_capability_up01=json_payload['id']
		print('Using (for up01) capability id: ' + my_capability_up01)
	except (ValueError) as e:
                print(e)
else:
	exit(0)
# ===

print('creating the capability (up02)')
request_url='https://' + config_instance + '/iot/core/api/v1/tenant/' + config_tenant + '/capabilities'
headers={'Content-Type' : 'application/json'}
payload='{ "name" : "capability_up02_' + alternateId_4_capability_up02 + '", "properties" : [ { "name" : "p01_up02", "dataType" : "string" }, { "name" : "p02_up02", "dataType" : "string" } ], "alternateId" : "' + alternateId_4_capability_up02 + '" }'
print(payload)
response=requests.post(request_url, headers=headers, auth=(config_user, config_password), data=payload)
status_code=response.status_code
print(str(status_code) + " " + str(response.text))
if (status_code == 200):
	try:
		json_payload=json.loads(response.text)
		my_capability_up02=json_payload['id']
		print('Using (for up02) capability id: ' + my_capability_up02)
	except (ValueError) as e:
                print(e)
else:
	exit(0)
# ===

print('creating the capability (down01)')
request_url='https://' + config_instance + '/iot/core/api/v1/tenant/' + config_tenant + '/capabilities'
headers={'Content-Type' : 'application/json'}
payload='{ "name" : "capability_down01_' + alternateId_4_capability_down01 + '", "properties" : [ { "name" : "p01_down01", "dataType" : "string" }, { "name" : "p02_down01", "dataType" : "string" } ], "alternateId" : "' + alternateId_4_capability_down01 + '" }'
print(payload)
response=requests.post(request_url, headers=headers, auth=(config_user, config_password), data=payload)
status_code=response.status_code
print(str(status_code) + " " + str(response.text))
if (status_code == 200):
	try:
		json_payload=json.loads(response.text)
		my_capability_down01=json_payload['id']
		print('Using (for down01) capability id: ' + my_capability_down01)
	except (ValueError) as e:
                print(e)
else:
	exit(0)
# ===
print('creating the capability (down02)')
request_url='https://' + config_instance + '/iot/core/api/v1/tenant/' + config_tenant + '/capabilities'
headers={'Content-Type' : 'application/json'}
payload='{ "name" : "capability_down02_' + alternateId_4_capability_down02 + '", "properties" : [ { "name" : "p01_down02", "dataType" : "string" }, { "name" : "p02_down02", "dataType" : "string" } ], "alternateId" : "' + alternateId_4_capability_down02 + '" }'
print(payload)
response=requests.post(request_url, headers=headers, auth=(config_user, config_password), data=payload)
status_code=response.status_code
print(str(status_code) + " " + str(response.text))
if (status_code == 200):
	try:
		json_payload=json.loads(response.text)
		my_capability_down02=json_payload['id']
		print('Using (for down02) capability id: ' + my_capability_down02)
	except (ValueError) as e:
                print(e)
else:
	exit(0)
# ===

print('creating the sensortype')
request_url='https://' + config_instance + '/iot/core/api/v1/tenant/' + config_tenant + '/sensorTypes'
headers={'Content-Type' : 'application/json'}
payload='{ "name" : "sensortype_' + alternateId_4_sensortype + '", "capabilities" : [ { "id" : "' + my_capability_up01 + '", "type" : "measure" }, { "id" : "' + my_capability_up02 + '", "type" : "measure" }, { "id" : "' + my_capability_down01 + '", "type" : "command" }, { "id" : "' + my_capability_down02 + '", "type" : "command" } ] }'
# so far the alternateId for a sensorType needs to be a positive integer - so the code below does not work
# payload='{ "name" : "sensortype_' + alternateId_4_sensortype + '", "capabilities" : [ { "id" : "' + my_capability_up01 + '", "type" : "measure" }, { "id" : "' + my_capability_up02 + '", "type" : "measure" }, { "id" : "' + my_capability_down01 + '", "type" : "command" }, { "id" : "' + my_capability_down02 + '", "type" : "command" } ], "alternateId" : "' + alternateId_4_sensortype + '" }'
# print(payload)
response=requests.post(request_url, headers=headers, auth=(config_user, config_password), data=payload)
status_code=response.status_code
print(str(status_code) + " " + str(response.text))
if (status_code == 200):
	try:
		json_payload=json.loads(response.text)
		my_sensortype=json_payload['id']
		print('Using sensortype id: ' + my_sensortype)
	except (ValueError) as e:
                print(e)
else:
	exit(0)
# ===

print('creating the sensor')
request_url='https://' + config_instance + '/iot/core/api/v1/tenant/' + config_tenant + '/sensors'
headers={'Content-Type' : 'application/json'}
payload='{ "name": "sensor_' + alternateId_4_sensor + '", "deviceId" : "' + my_device + '", "sensorTypeId" : "' + my_sensortype + '", "alternateId" : "' + alternateId_4_sensor + '" }'
response=requests.post(request_url, headers=headers, auth=(config_user, config_password), data=payload)
status_code=response.status_code
print(str(status_code) + " " + str(response.text))
if (status_code == 200):
	try:
		json_payload=json.loads(response.text)
		my_sensor=json_payload['id']
		print('Using sensor id: ' + my_sensor)
	except (ValueError) as e:
                print(e)
else:
	exit(0)
# ===

# now also write a separate script to receive commands as an MQTT client

mqtt_client_script=open("mqtt-client.py", "w")

mqtt_client_script.write("config_broker='" + config_instance + "'\n")
mqtt_client_script.write("config_alternate_id_device='" + alternateId_4_device + "'\n")
mqtt_client_script.write("config_alternate_id_capability_up01='" + alternateId_4_capability_up01 + "'\n")
mqtt_client_script.write("config_alternate_id_capability_up02='" + alternateId_4_capability_up02 + "'\n")
mqtt_client_script.write("config_alternate_id_sensor='" + alternateId_4_sensor + "'\n")

mqtt_client_script.write('''
import sys
import time

# as an additional / non standard module pre-condition: 
# install Paho MQTT lib e.g. from https://github.com/eclipse/paho.mqtt.python
import paho.mqtt.client as mqtt

# ========================================================================
def on_connect_broker(client, userdata, flags, rc):
	print('Connected to MQTT broker with result code: ' + str(rc))
	sys.stdout.flush()

def on_subscribe(client, obj, message_id, granted_qos):
	print('on_subscribe - message_id: ' + str(message_id) + ' / qos: ' + str(granted_qos))
	sys.stdout.flush()

def on_message(client, obj, msg):
	# print('on_message - ' + msg.topic + ' ' + str(msg.qos))
	print('on_message - ' + msg.topic + ' ' + str(msg.qos) + ' ' + str(msg.payload))
	sys.stdout.flush()
# ========================================================================

# === main starts here ===================================================

config_credentials_key='./credentials.key'
config_credentials_crt='./credentials.crt'

broker=config_broker
broker_port=8883

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

print("connected to broker now")
sys.stdout.flush()

my_publish_topic='measures/' + my_device
my_subscription_topic='commands/' + my_device
client.subscribe(my_subscription_topic, 0)

client.loop_start()

sleep_time=10
time.sleep(sleep_time)

while True:
	print('in main loop')
	sys.stdout.flush()

	time.sleep(sleep_time)
	payload='{ \"capabilityAlternateId\" : \"' + config_alternate_id_capability_up01 + '\", \"measures\" : [[ \"value for p01_up01\", \"value for p02_up01\" ]], \"sensorAlternateId\":\"' + config_alternate_id_sensor + '\" }'
	result=client.publish(my_publish_topic, payload, qos=0)
	print("published for capability up01 with result: " + str(result))
	sys.stdout.flush()

	time.sleep(sleep_time)
	payload='{ \"capabilityAlternateId\" : \"' + config_alternate_id_capability_up02 + '\", \"measures\" : [[ \"value for p01_up02\", \"value for p02_up02\" ]], \"sensorAlternateId\":\"' + config_alternate_id_sensor + '\" }'
	result=client.publish(my_publish_topic, payload, qos=0)
	print("published for capability up02 with result: " + str(result))
	sys.stdout.flush()
''')
mqtt_client_script.close()

# ===
# now also write a separate script to retrieve data

retrieve_script=open("retrieve.py", "w")

retrieve_script.write("import requests\n")
retrieve_script.write("import json\n\n")

retrieve_script.write("config_instance='" + config_instance + "'\n")
command_script.write("config_tenant='" + config_tenant + "'\n")
retrieve_script.write("config_user='" + config_user + "'\n")
retrieve_script.write("config_password='" + config_password + "'\n\n")

retrieve_script.write("config_my_device='" + my_device + "'\n")
retrieve_script.write("config_my_capability_up01='" + my_capability_up01 + "'\n")

retrieve_script.write('''
request_url='https://' + config_instance + '/iot/processing/api/v1/tenant/' + config_tenant + '/measures/capabilities/' + config_my_capability_up01 + '?orderby=timestamp%20desc&filter=deviceId%20eq%20%27' + config_my_device + '%27&skip=0&top=100'
headers={'Content-Type' : 'application/json'}
response=requests.get(request_url, headers=headers, auth=(config_user, config_password))
status_code=response.status_code
if (status_code == 200):
	print(response.text)
	try:
		json_payload=json.loads(response.text)
		for individual_measure in json_payload:
			print('value: ' + str(individual_measure['measure']))
	except (ValueError) as e:
                print(e)
''')

retrieve_script.close()

# ===
# now also write a separate script to originate a command

command_script=open("originate-commands.py", "w")

command_script.write("import requests\n\n")
command_script.write("config_instance='" + config_instance + "'\n")
command_script.write("config_tenant='" + config_tenant + "'\n")
command_script.write("config_user='" + config_user + "'\n")
command_script.write("config_password='" + config_password + "'\n\n")

command_script.write("config_my_device='" + my_device + "'\n")
command_script.write("config_my_capability_down01='" + my_capability_down01 + "'\n")
command_script.write("config_my_capability_down02='" + my_capability_down02 + "'\n")
command_script.write("config_my_sensor='" + my_sensor + "'\n")

command_script.write("request_url='https://' + config_instance + '/iot/core/api/v1/tenant/' + config_tenant + '/devices/' + config_my_device + '/commands'\n")

command_script.write('''
payload='{ "capabilityId" : "' + config_my_capability_down01 + '", "sensorId" : "' + config_my_sensor + '", "command" : { "p01_down01" : "value for p01_down01" , "p02_down01" : "value for p02_down01" } }'

headers={'Content-Type' : 'application/json'}

print('request_url: ' + request_url)
print('payload: ' + payload)

response=requests.post(request_url, data=payload, headers=headers, auth=(config_user, config_password))
print(response.status_code)
print(response.text)

payload='{ "capabilityId" : "' + config_my_capability_down02 + '", "sensorId" : "' + config_my_sensor + '", "command" : { "p01_down02" : "value for p01_down02" , "p02_down02" : "value for p02_down02" } }'

headers={'Content-Type' : 'application/json'}

print('request_url: ' + request_url)
print('payload: ' + payload)

response=requests.post(request_url, data=payload, headers=headers, auth=(config_user, config_password))
print(response.status_code)
print(response.text)
''')

command_script.close()

print("=== summary ===")
print("device: " + str(my_device) + " altId: " + str(alternateId_4_device))
print("capability up01: " + str(my_capability_up01) + " altId: " + str(alternateId_4_capability_up01))
print("capability up02: " + str(my_capability_up02) + " altId: " + str(alternateId_4_capability_up02))
print("capability down01: " + str(my_capability_down01) + " altId: " + str(alternateId_4_capability_down01))
print("capability down02: " + str(my_capability_down02) + " altId: " + str(alternateId_4_capability_down02))
print("sensortype: " + str(my_sensortype))
print("sensor: " + str(my_sensor) + " altId: " + str(alternateId_4_sensor))
print("=== summary ===")

# ===
