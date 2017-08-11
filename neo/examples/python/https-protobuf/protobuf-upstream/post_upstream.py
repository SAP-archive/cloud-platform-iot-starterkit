# all configuration settings come from config.py
try:
	import config
except ImportError:
	print("Please copy template-config.py to config.py and configure appropriately !"); exit();

import urllib3
import certifi

# It is absolutely CRITICAL that you use certificate validation to ensure and guarantee that
# 1. you are indeed sending the message to *.hanatrial.ondemand.com and
# 2. that you avoid the possibility of TLS/SSL MITM attacks which would allow a malicious person to capture the OAuth token
# URLLIB3 DOES NOT VERIFY CERTIFICATES BY DEFAULT
# Therefore, install urllib3 and certifi and specify the PoolManager as below to enforce certificate check
# See https://urllib3.readthedocs.org/en/latest/security.html for more details

# use with or without proxy
http = urllib3.PoolManager(
	cert_reqs='CERT_REQUIRED', # Force certificate check.
	ca_certs=certifi.where(),  # Path to the Certifi bundle.
)
# http = urllib3.proxy_from_url(config.proxy_url)

url = 'https://iotmms' + config.hcp_account_id + '.hanatrial.ondemand.com/com.sap.iotservices.mms/v1/api/http/data/' + config.device_id
print(url)

headers = urllib3.util.make_headers()

# use with authentication
# please insert correct OAuth token
headers['Authorization'] = 'Bearer ' + config.oauth_credentials_for_device

# JSON version - uncomment to test
# headers['Content-Type'] = 'application/json;charset=utf-8'
# body='{"mode":"sync", "messageType":"' + config.message_type_id_From_device + '", "messages":[{"sensor":"sensor1", "value":"20", "timestamp":1468991773}]}'

# protobuf version - default here
import mms_upstream_pb2

mms_request=mms_upstream_pb2.Request()
mms_request.messageType=config.message_type_id_From_device
# mms_request.mode=mms_upstream_pb2.SYNC
mms_request.mode=mms_upstream_pb2.ASYNC

mms_message=mms_upstream_pb2.Message()
mms_message.sensor="sensor2"
mms_message.value="30"
mms_message.timestamp=1468991773

mms_request.messages.extend([mms_message])

headers['Content-Type'] = 'application/x-protobuf'
body=mms_request.SerializeToString()

try:
	r = http.urlopen('POST', url, body=body, headers=headers)
	print(r.status)
	print(r.data)
except urllib3.exceptions.SSLError as e:
	print e
	
