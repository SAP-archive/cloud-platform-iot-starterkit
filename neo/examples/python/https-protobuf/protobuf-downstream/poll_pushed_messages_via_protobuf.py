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

headers = urllib3.util.make_headers()

# use with authentication
# please insert correct OAuth token
headers['Authorization'] = 'Bearer ' + config.oauth_credentials_for_device
headers['Accept'] = 'application/x-protobuf'

# you can send data that is retrieved here via the built-in PUSH API client in the MMS cockpit
r = http.urlopen('GET', url, headers=headers)

print(r.status)
print('<' + r.data + '>')

import mms_downstream_pb2

mms_content=mms_downstream_pb2.Array()
mms_content.ParseFromString(r.data)

print(mms_content)
