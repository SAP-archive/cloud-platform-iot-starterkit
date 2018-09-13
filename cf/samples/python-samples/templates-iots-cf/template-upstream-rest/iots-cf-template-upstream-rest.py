# create an IoTS device of choice with just one program
# and no necessary human interaction
# write a script for credential conversion
# finally write 2 scripts to ingest using the converted credentials / retrieve 

config_host='<hostname>'
config_instance='<instance id>'
config_tenant='<tenant id>'
config_user='<user>'
config_password='<password>'
config_alternateId_4_device_base='<chosen alternateId base>'
config_alternateId_4_device_version='_01'

import sys
import requests
import json

alternateId_4_device=config_alternateId_4_device_base+config_alternateId_4_device_version
alternateId_4_capability_up01='c_up01_' + config_alternateId_4_device_base
alternateId_4_capability_up02='c_up02_' + config_alternateId_4_device_base
alternateId_4_sensortype=config_alternateId_4_device_base
alternateId_4_sensor=alternateId_4_device
certfile_name='./cert.pem'

# ======================================================================== 
# these values are filled as you go through the steps
gw_id_4_rest=''
my_device=''
my_capability_up01=''
my_capability_up02=''
my_sensortype=''
my_sensor=''
# ======================================================================== 

print('listing gateways')
request_url='https://' + config_host + '/' + config_instance + '/iot/core/api/v1/tenant/' + config_tenant + '/gateways'
headers={'Content-Type' : 'application/json'}
response=requests.get(request_url, headers=headers, auth=(config_user, config_password))
status_code=response.status_code
if (status_code == 200):
	print(response.text)
	try:
		json_payload=json.loads(response.text)
		for individual_dataset in json_payload:
			print(individual_dataset['id'] + ' - ' + individual_dataset['protocolId'])
			if ((individual_dataset['protocolId'] == 'rest') and (individual_dataset['status'] == 'online')):
				gw_id_4_rest=individual_dataset['id']
				print('Using gateway: ' + gw_id_4_rest)
	except (ValueError) as e:
                print(e)
# ===

print('creating the device')
request_url='https://' + config_host + '/' + config_instance + '/iot/core/api/v1/tenant/' + config_tenant + '/devices'
headers={'Content-Type' : 'application/json'}
payload='{ "gatewayId" : "' + gw_id_4_rest + '", "name" : "device_' + alternateId_4_device + '", "alternateId" : "' + alternateId_4_device + '" }'
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
request_url='https://' + config_host + '/' + config_instance + '/iot/core/api/v1/tenant/' + config_tenant + '/devices/' + my_device + '/authentications/clientCertificate/pem'
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
request_url='https://' + config_host + '/' + config_instance + '/iot/core/api/v1/tenant/' + config_tenant + '/capabilities'
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
request_url='https://' + config_host + '/' + config_instance + '/iot/core/api/v1/tenant/' + config_tenant + '/capabilities'
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

print('creating the sensortype')
request_url='https://' + config_host + '/' + config_instance + '/iot/core/api/v1/tenant/' + config_tenant + '/sensorTypes'
headers={'Content-Type' : 'application/json'}
payload='{ "name" : "sensortype_' + alternateId_4_sensortype + '", "capabilities" : [ { "id" : "' + my_capability_up01 + '", "type" : "measure" }, { "id" : "' + my_capability_up02 + '", "type" : "measure" } ] }'
# so far the alternateId for a sensorType needs to be a positive integer - so the code below does not work
# payload='{ "name" : "sensortype_' + alternateId_4_sensortype + '", "capabilities" : [ { "id" : "' + my_capability_up01 + '", "type" : "measure" }, { "id" : "' + my_capability_up02 + '", "type" : "measure" } ], "alternateId" : "' + alternateId_4_sensortype + '" }'
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
request_url='https://' + config_host + '/' + config_instance + '/iot/core/api/v1/tenant/' + config_tenant + '/sensors'
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

# now also write a separate script to ingest data

ingest_script=open("ingest.py", "w")

ingest_script.write("import requests\n\n")
ingest_script.write("config_host='" + config_host + "'\n")
ingest_script.write("config_alternateId_4_device='" + alternateId_4_device + "'\n")
ingest_script.write("config_alternateId_4_capability_up01='c_up01_" + config_alternateId_4_device_base + "'\n")
ingest_script.write("config_alternateId_4_capability_up02='c_up02_" + config_alternateId_4_device_base + "'\n")

ingest_script.write('''
alternateId_4_sensor=config_alternateId_4_device

request_url='https://' + config_host + '/iot/gateway/rest/measures/' + config_alternateId_4_device

# ingest for capability up01
payload='{ \"capabilityAlternateId\" : \"' + config_alternateId_4_capability_up01 + '\", \"measures\" : [[ \"value for p01_up01\", \"value for p02_up01\" ]], \"sensorAlternateId\":\"' + alternateId_4_sensor + '\" }'
headers={'Content-Type' : 'application/json'}

response=requests.post(request_url, data=payload, headers=headers, cert=('./credentials.crt', './credentials.key'))
print(response.status_code)
print(response.text)

# ingest for capability up02
payload='{ \"capabilityAlternateId\" : \"' + config_alternateId_4_capability_up02 + '\", \"measures\" : [[ \"value for p01_up02\", \"value for p02_up02\" ]], \"sensorAlternateId\":\"' + alternateId_4_sensor + '\" }'
headers={'Content-Type' : 'application/json'}

response=requests.post(request_url, data=payload, headers=headers, cert=('./credentials.crt', './credentials.key'))
print(response.status_code)
print(response.text)
''')

ingest_script.close()

# ===
# now also write a separate script to retrieve data

retrieve_script=open("retrieve.py", "w")

retrieve_script.write("import requests\n")
retrieve_script.write("import json\n\n")

retrieve_script.write("config_host='" + config_host + "'\n")
retrieve_script.write("config_tenant='" + config_tenant + "'\n")
retrieve_script.write("config_user='" + config_user + "'\n")
retrieve_script.write("config_password='" + config_password + "'\n\n")

retrieve_script.write("config_my_device='" + my_device + "'\n")
retrieve_script.write("config_my_capability_up01='" + my_capability_up01 + "'\n")

retrieve_script.write('''
request_url='https://' + config_host + '/iot/processing/api/v1/tenant/' + config_tenant + '/measures/capabilities/' + config_my_capability_up01 + '?orderby=timestamp%20desc&filter=deviceId%20eq%20%27' + config_my_device + '%27&skip=0&top=100'
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

print("=== summary ===")
print("device: " + str(my_device) + " altId: " + str(alternateId_4_device))
print("capability up01: " + str(my_capability_up01) + " altId: " + str(alternateId_4_capability_up01))
print("capability up02: " + str(my_capability_up02) + " altId: " + str(alternateId_4_capability_up02))
print("sensortype: " + str(my_sensortype))
print("sensor: " + str(my_sensor) + " altId: " + str(alternateId_4_sensor))
print("=== summary ===")

# ===
