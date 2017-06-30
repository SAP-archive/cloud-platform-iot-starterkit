# This script can be used to create message and device types as well
# as a device instance via the respective API
# it uses the APIv2 - which is the encouraged way to use as in Aug 2016
# it does not do any error handling - so the created types MUST NOT exist before

# all configuration settings come from config.py
try:
	import config
except ImportError:
	print("Please copy template-config.py to config.py and configure appropriately !"); exit();

# this can be used to activate debugging
# debug_communication=1
debug_communication=0

import urllib3
import json

def register_message_type(message_type_name, specifics):
	global debug_communication
	global http
	global register_message_type_url
	global message_type_id

	# use this service with basic authentication
	headers = urllib3.util.make_headers(basic_auth=config.hcp_user_credentials)
	headers['Content-Type'] = 'application/json;charset=utf-8'

	body='{"name":"' + message_type_name + '",' + specifics + '}'
	r = http.urlopen('POST', register_message_type_url, body=body, headers=headers)
	if (debug_communication == 1):
		print("request body: " + body)
		print("register_message_type():" + str(r.status))
		print(r.data)

	json_string=(r.data).decode("utf-8")
	try:
		json_string_parsed=json.loads(json_string)
		message_type_id=json_string_parsed["id"]
		# print("id:"+message_type_id)
	except KeyError:
		print("Problem extracting id from the message " + (r.data).decode("utf-8") + " received on register_message_type()")

def register_device_type_with_message_types(device_type_name, message_type_array):
	global debug_communication
	global http
	global register_device_type_url
	global device_type_id
	global device_type_token

	# use this service with basic authentication
	headers = urllib3.util.make_headers(basic_auth=config.hcp_user_credentials)
	headers['Content-Type'] = 'application/json;charset=utf-8'

	body='{"name":"' + device_type_name + '", "messageTypes":' + message_type_array + '}'
	r = http.urlopen('POST', register_device_type_url, body=body, headers=headers)
	if (debug_communication == 1):
		print("request body: " + body)
		print("register_device_type_with_message_types():" + str(r.status))
		print(r.data)

	json_string=(r.data).decode("utf-8")
	try:
		json_string_parsed=json.loads(json_string)
		device_type_id=json_string_parsed["id"]
		# print("id:"+device_type_id)
		device_type_authentication=json_string_parsed["authentication"]
		# print("authentication:"+str(device_type_authentication))
		device_type_token=device_type_authentication["token"]
		# print("token:"+device_type_token)
	except KeyError:
		print("Problem extracting id/token from the message " + (r.data).decode("utf-8") + " received on register_device_type_with_message_types()")


def register_device_instance(device_type_id, device_name, oauth_credential_for_device_type):
	global debug_communication
	global http
	global register_device_url
	global device_instance_id
	global device_instance_token

	# use this service with OAuth authentication and the credential you got from register_device_type_with_message_types
	headers = urllib3.util.make_headers(user_agent=None)

	headers['Authorization'] = 'Bearer ' + oauth_credential_for_device_type
	headers['Content-Type'] = 'application/json;charset=utf-8'
	body='{"name":"' + device_name + '", "deviceType":"' + device_type_id + '"}'
	r = http.urlopen('POST', register_device_url, body=body, headers=headers)
	if (debug_communication == 1):
		print("request body: " + body)
		print("register_device_instance():" + str(r.status))
		print(r.data)

	json_string=(r.data).decode("utf-8")
	try:
		json_string_parsed=json.loads(json_string)
		device_instance_id=json_string_parsed["id"]
		# print("id:"+device_instance_id)
		device_instance_authentication=json_string_parsed["authentication"]
		# print("iauthentication:"+str(device_instance_iauthentication))
		device_instance_token=device_instance_authentication["token"]
		# print("token:"+device_instance_token)
	except ValueError:
		print("Problem extracting id/token from the message " + (r.data).decode("utf-8") + " received on register_device_instance()")
	except KeyError:
		print("Problem extracting id/token from the message " + (r.data).decode("utf-8") + " received on register_device_instance()")


# === main starts here ===============================================
# disable InsecureRequestWarning if your are working without certificate verification
# see https://urllib3.readthedocs.org/en/latest/security.html
# be sure to use a recent enough urllib3 version if this fails
try:
	urllib3.disable_warnings()
except:
	print("urllib3.disable_warnings() failed - get a recent enough urllib3 version to avoid potential InsecureRequestWarning warnings! Can and will continue though.")

# use with or without proxy
if (config.proxy_url == ''):
	http = urllib3.PoolManager()
else:
	http = urllib3.proxy_from_url(config.proxy_url)

# see doc: https://help.hana.ondemand.com/iot/frameset.htm?3da18aa006b04d4cbeaf3be3fdd27fa6.html
register_message_type_url='https://iotrdmsiotservices-' + config.hcp_account_id + config.hcp_landscape_host + '/com.sap.iotservices.dms/v2/api/messageTypes'
register_device_type_url='https://iotrdmsiotservices-' + config.hcp_account_id + config.hcp_landscape_host + '/com.sap.iotservices.dms/v2/api/deviceTypes'
register_device_url='https://iotrdmsiotservices-' + config.hcp_account_id + config.hcp_landscape_host + '/com.sap.iotservices.dms/v2/api/devices'

# set variables
device_type_name="starterkit_type_01_v2"
device_type_id=""
device_type_token=""

message_type_id=""
message_type_name_upstream="starterkit_message_type_upstream_v2"
message_type_id_upstream=""
message_type_name_downstream="starterkit_message_type_downstream_v2"
message_type_id_downstream=""

device_instance_name="starterkit_instance_01_v2"
device_instance_id=""
device_instance_token=""

# call the services

register_message_type(message_type_name_upstream, '"direction": "fromDevice", "fields": [ { "position": 1, "name": "sensor", "type": "string" },  { "position": 2, "name": "value", "type": "string" }, { "position": 3, "name": "timestamp", "type": "long" }]')
message_type_id_upstream=message_type_id

register_message_type(message_type_name_downstream, '"direction": "toDevice", "fields": [ { "position": 1, "name": "opcode", "type": "string" },  { "position": 2, "name": "operand", "type": "string" }]')
message_type_id_downstream=message_type_id

message_type_array='[ '
message_type_array=message_type_array + '{ "id":"' + message_type_id_upstream + '", "name":"' + message_type_name_upstream + '", "direction":"fromDevice"},'
message_type_array=message_type_array + '{ "id":"' + message_type_id_downstream + '", "name":"' + message_type_name_downstream + '", "direction":"toDevice"}'
message_type_array=message_type_array + ']'
register_device_type_with_message_types(device_type_name, message_type_array)

register_device_instance(device_type_id, device_instance_name, device_type_token)

print("====================================================")
print("Registered a device " + device_instance_name +" with id " + device_instance_id + " of type " + device_type_name + " with id " + device_type_id + " and with OAuth token "+ device_instance_token + " !")
print("It uses message type " + message_type_name_upstream + " with id " + message_type_id_upstream + " for sending messages !")
print("It uses message type " + message_type_name_downstream + " with id " + message_type_id_downstream + " for receiving messages !")
print("====================================================")
