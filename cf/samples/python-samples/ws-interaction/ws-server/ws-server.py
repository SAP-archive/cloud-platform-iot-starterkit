# this program uses Python 3.5 or above
# it frequently sends application level data to a connected client

import asyncio
import websockets
import time
import sys

send_interval=30
# send_interval=60*60*2

async def my_service(websocket, path):
	while True:
		time_str=time.ctime(time.time())
		await websocket.send(time_str)
		print("sent: " + time_str)
		sys.stdout.flush()
		time.sleep(send_interval)

print("websocket server sending data each " + str(send_interval) + " seconds")

service=websockets.serve(my_service, 'localhost', 8765)

asyncio.get_event_loop().run_until_complete(service)
asyncio.get_event_loop().run_forever()
