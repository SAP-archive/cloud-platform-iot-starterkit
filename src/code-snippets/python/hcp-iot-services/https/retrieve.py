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

# interaction for a specific Device instance - replace 1 with your specific Device ID
url = 'https://iotmms_on_your_trial_system.hanatrial.ondemand.com/com.sap.iotservices.mms/v1/api/http/data/1'

headers = urllib3.util.make_headers()

# use with authentication
# please insert correct OAuth token
headers['Authorization'] = 'Bearer ' + 'your_oauth_token'

r = http.urlopen('GET', url, headers=headers)

print(r.status)
print(r.data)
