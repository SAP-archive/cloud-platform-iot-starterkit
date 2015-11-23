# This script shows the usage of binary message types
# It demonstrates the low level interaction (also suitable for uControlers) to do a https POST in order to send data from the device to the Cloud

# all configuration settings come from config.py
try:
	import config
except ImportError:
	print("Please copy template-config.py to config.py and configure appropriately !"); exit();

import sys
import socket
import ssl
import base64

input="test content" # could be binary, e.g. an image or arbitrary file content
input_base64=(base64.b64encode(input.encode('utf-8'))).decode('utf-8')

post_payload='{"mode":"async", "messageType":"' 
post_payload=post_payload + config.message_type_upstream
post_payload=post_payload + '", "messages":[{"' + config.fieldname_upstream + '":"'
post_payload=post_payload + input_base64
post_payload=post_payload + '"}]}'
# print(post_payload)

s=socket.socket(socket.AF_INET, socket.SOCK_STREAM)

c=ssl.wrap_socket(s)
c.connect(socket.getaddrinfo(config.host, 443)[0][4])

request='POST /com.sap.iotservices.mms/v1/api/http/data/' + config.device_id + ' HTTP/1.1\r\n'
request=request + 'Host: ' + config.host + '\r\n'
request=request + 'Content-Type: application/json;charset=utf-8\r\n'
request=request + 'Authorization: Bearer ' + config.oauth_token + '\r\n'
request=request + 'Content-Length: ' + str(len(post_payload)) + '\r\n\r\n'
request=request + post_payload + '\r\n\r\n'

# print(request)

if (sys.version_info.major == 3):
	c.write(bytes(request, 'ascii'))
else:
	c.write(request)

result=c.read(4096)
print(result)

c.close()
