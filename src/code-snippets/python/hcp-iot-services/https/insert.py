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
# http = urllib3.proxy_from_url('http://proxy_host:proxy_port')

# interaction for a specific Device instance - replace 'd000-e000-v000-i000-c000-e001' with your specific Device ID
url = 'https://iotmms_on_your_trial_system.hanatrial.ondemand.com/com.sap.iotservices.mms/v1/api/http/data/d000-e000-v000-i000-c000-e001'

headers = urllib3.util.make_headers()

# use with authentication
# please insert correct OAuth token
headers['Authorization'] = 'Bearer ' + 'your_oauth_token'
headers['Content-Type'] = 'application/json;charset=utf-8'

# send message of Message Type 'm0t0y0p0e1' and the corresponding payload layout that you defined in the IoT Services Cockpit
body='{"mode":"async", "messageType":"m0t0y0p0e1", "messages":[{"sensor":"sensor1", "value":"20", "timestamp":1468991773}]}'

# It is also possible to send multiple messages (3 in this example) in a single request that conform to the same message type.
# body='{"mode":"async", "messageType":"m0t0y0p0e1", "messages":[{"sensor":"sensor1", "value":"20", "timestamp":1468991773},{"sensor":"sensor1", "value":"21", "timestamp":1468991873},{"sensor":"sensor1", "value":"22", "timestamp":1468991973}]}'

# Because every message field in a message type definition defines its position (see message type example above) it is also possible to compress the messages array by omitting the field names.
# Please be aware is that value order is very important in this case (it should match to the message type field positions like specified during message type creation)
# body='{"mode":"async","messageType":"m0t0y0p0e1","messages":[["sensor1","20",1468991773],["sensor1","21",1468991873],["sensor1","22",1468991973]]}'

# For more information with regard to Communication Handling, please refer to online documentation at https://help.hana.ondemand.com/iot -> Message Management Service API -> Interfaces and Communication Handling

try:
	r = http.urlopen('POST', url, body=body, headers=headers)
	print(r.status)
	print(r.data)
except urllib3.exceptions.SSLError as e:
	print e
	
