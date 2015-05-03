import urllib3
# disable InsecureRequestWarning if your are working without certificate verification
# see https://urllib3.readthedocs.org/en/latest/security.html
# be sure to use a recent enough urllib3 version if this fails
try:
	urllib3.disable_warnings()
except:
	print('urllib3.disable_warnings() failed - get a recent enough urllib3 version to avoid potential InsecureRequestWarning warnings! Can and will continue though.')

# use with or without proxy
http = urllib3.PoolManager()
# http = urllib3.proxy_from_url('http://proxy_host:proxy_port')

# interaction for a specific Device instance - replace 1 with your specific Device ID
url = 'https://iotmms_on_your_trial_system.hanatrial.ondemand.com/com.sap.iotservices.mms/v1/api/http/data/1'

headers = urllib3.util.make_headers()

# use with authentication
# please insert correct OAuth token
headers['Authorization'] = 'Bearer ' + 'your_oauth_token'
headers['Content-Type'] = 'application/json;charset=utf-8'

# send message of Message Type 1 and the corresponding payload layout that you defined in the IoT Services Cockpit
body='{"mode":"async", "messageType":"1", "messages":[{"sensor":"sensor1", "value":"20", "timestamp":1413191650}]}'

r = http.urlopen('POST', url, body=body, headers=headers)

print(r.status)
print(r.data)
