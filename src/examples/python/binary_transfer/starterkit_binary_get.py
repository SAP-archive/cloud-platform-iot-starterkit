# This script shows the usage of binary message types
# It demonstrates the low level interaction (also suitable for uControlers) to do a https GET in order to receive data sent via the Push API
# response parsing and handling of larger responses are left to the user

# all configuration settings come from config.py
try:
	import config
except ImportError:
	print("Please copy template-config.py to config.py and configure appropriately !"); exit();

import sys
import socket
import ssl

s=socket.socket(socket.AF_INET, socket.SOCK_STREAM)

c=ssl.wrap_socket(s)
c.connect(socket.getaddrinfo(config.host, 443)[0][4])

request='GET /com.sap.iotservices.mms/v1/api/http/data/' + config.device_id + ' HTTP/1.1\r\n'
request=request + 'Host: ' + config.host + '\r\n'
request=request + 'Content-Type: application/json;charset=utf-8\r\n'
request=request + 'Authorization: Bearer ' + config.oauth_token + '\r\n\r\n'

# print("==")
# print(request)
# print("==")

if (sys.version_info.major == 3):
        c.write(bytes(request, 'ascii'))
else:
        c.write(request)

result=c.read(4096)
print(result)

c.close()
