# example for receiving requests from an HTTP client and answering it
# additionally doing an upstream request to the IoT Service CF

from BaseHTTPServer import BaseHTTPRequestHandler
import urlparse
import re
import time
from time import gmtime, strftime
import sys
import requests

config_server_port=8090

config_do_upstream_request=True
# config_do_upstream_request=False
config_host='<hostname>'
config_alternate_id_4_device='<alternate id>'

def do_upstream_request(payload):
	global config_do_upstream_request
	global config_instance
	global alternate_id_4_device

	print("Doing upstream request with payload: " + payload)

	alternate_id_4_capability=config_alternate_id_4_device
	alternate_id_4_sensor=config_alternate_id_4_device
	
	request_url='https://' + config_host + '/iot/gateway/rest/measures/' + config_alternate_id_4_device
	
	headers={'Content-Type' : 'application/json'}
	
	# print(request_url)
	# print(payload)
	
	if config_do_upstream_request:
		try:
			response=requests.post(request_url, data=payload, headers=headers, cert=('./credentials.crt', './credentials.key'))
			print(response.status_code)
			print(response.text)
		except:
			print("an exception occured")

class RequestHandler(BaseHTTPRequestHandler):
	def do_GET(self):
		parsed_path=urlparse.urlparse(self.path)
		message='integration example server\n'
		self.send_response(200)
		self.end_headers()
		self.wfile.write(message)
		return

	def do_POST(self):
		response=""
		received_at=str(time.ctime())
		timestamp=int(time.time())
		time_string=strftime("%Y-%m-%dT%H:%M:%S+00:00", gmtime(timestamp))
		print(self.headers)
		content_type=str(self.headers.getheader('content-type'))
		print("Content-Type: " + content_type)

		content_len=int(self.headers.getheader('content-length'))
		post_body=self.rfile.read(content_len)

		print("========================================================================")
		print(post_body)
		print("========================================================================")

		do_upstream_request(payload)
		# EXTENSION POINT
		response='<your specific response corrsponding to the content type you choose - can e.g. also be XML for SOAP>'
		self.send_header('Content-Type', 'application/json; charset=utf-8')
		self.send_header('Content-Length', len(response))
		self.end_headers()
		self.wfile.write(response)
		print("response: " + str(response) + " sent back to the requestor")
		sys.stdout.flush()
		return

if __name__ == '__main__':
	from BaseHTTPServer import HTTPServer
	host=''
	server=HTTPServer((host, config_server_port), RequestHandler)
	print('Starting integration example server at ' + str(time.ctime()))
	sys.stdout.flush()
	server.serve_forever()
