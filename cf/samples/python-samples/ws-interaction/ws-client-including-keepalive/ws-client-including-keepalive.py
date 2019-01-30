# this program uses Python 3.5 or above
# it receives data from a server it connects to and keeps the connection alive
# by frequently sending "ping" (on Websocket protocol / not application level) messages

import asyncio
from contextlib import suppress
import aiohttp
from time import gmtime, strftime
import sys
import requests

config_host='<hostname>'
config_alternate_id_4_device="<example_alternate_id>"

def do_upstream_request(payload):
	global config_do_upstream_request
	global config_host
	global alternate_id_4_device

	print("Doing upstream request with payload: " + payload)

	alternate_id_4_capability=config_alternate_id_4_device
	alternate_id_4_sensor=config_alternate_id_4_device
	
	request_url='https://' + config_host + '/iot/gateway/rest/measures/' + config_alternate_id_4_device
	
	headers={'Content-Type' : 'application/json'}
	
	# print(request_url)
	# print(payload)
	
	try:
		response=requests.post(request_url, data=payload, headers=headers, cert=('./credentials.crt', './credentials.key'))
		print(response.status_code)
		print(response.text)
	except:
		print("an exception occured")

ping_interval=10
_ws=''

async def do_forever():
	while True:
		# print("forever")
		time_string=strftime("%Y-%m-%d %H:%M:%S", gmtime())
		await asyncio.sleep(ping_interval)
		if (_ws != ''):
			print("ping at " + time_string)
			_ws.ping()
			sys.stdout.flush()

async def main():
	global _ws
	asyncio.ensure_future(do_forever())
	session = aiohttp.ClientSession()
	async with session.ws_connect('ws://localhost:8765/') as ws:
		_ws=ws
		async for msg in ws:
			print("received: " + str(msg.data))
			# EXTENSION POINT
			# process the received data and build a payload for upstream interaction with the IoT Service
			# for simplicity we choose the same alternateId for all its types when creating the device
			payload='{ "capabilityAlternateId" : "' + config_alternate_id_4_device + '" , "measures" : [[ "' + str(msg.data) + '" ]], "sensorAlternateId":"' + config_alternate_id_4_device + '" }'
			do_upstream_request(payload)
			
			sys.stdout.flush()
			if msg.type == aiohttp.WSMsgType.CLOSED:
				await ws.close()
				break
			elif msg.type == aiohttp.WSMsgType.ERROR:
				await ws.close()
				break
	print("broken out")
	sys.stdout.flush()

print("websocket client keeping the connection alive each " + str(ping_interval) + " seconds with a ping")
sys.stdout.flush()
loop=asyncio.get_event_loop()
loop.run_until_complete(main())

print("canceling pending tasks")
sys.stdout.flush()
pending=asyncio.Task.all_tasks()
for task in pending:
	task.cancel()
	with suppress(asyncio.CancelledError):
		loop.run_until_complete(task)
sys.stdout.flush()
