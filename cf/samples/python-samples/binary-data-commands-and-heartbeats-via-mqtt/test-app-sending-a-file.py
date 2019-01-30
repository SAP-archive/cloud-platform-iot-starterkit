# example that shows how to send binary data, e.g. a file testfile.bin
# via IoTS CF commands

import base64
import md5
import requests

# read in configuration values from environment variables

config_host='<hostname>'
config_instance='<instance id>'
config_tenant='<tenant id>'
config_user='<user>'
config_password='<password>'

# print('user: ' + config_user)
# print('password: ' + config_password)

config_my_device='<device id>'
config_my_capability='<capability id>'
config_my_sensor='<sensor id>'

request_url='https://' + config_host + '/' + config_instance + '/iot/core/api/v1/tenant/' + config_tenant + '/devices/' + config_my_device + '/commands'

filename='testfile.bin'
infile=open(filename, "rb")
data=infile.read()
infile.close()

md=md5.new()
md.update(data)
md5sum=str(md.hexdigest())
print("md5sum: " + md5sum)

content_base64=base64.b64encode(data)

# the payload layout corresponds to the capability with its properties that we created
payload='{ "capabilityId" : "' + config_my_capability + '", "sensorId" : "' + config_my_sensor + '", "command" : { "filename" : "' + filename + '" , "md5sum" : "' + md5sum + '" , "content" : "' + content_base64 + '" } }'

headers={'Content-Type' : 'application/json'}

print('request_url: ' + request_url)
print('payload: ' + payload)

try:
	response=requests.post(request_url, data=payload, headers=headers, auth=(config_user, config_password))
	print(response.status_code)
	print(response.text)
except:
	print("exception happened")
