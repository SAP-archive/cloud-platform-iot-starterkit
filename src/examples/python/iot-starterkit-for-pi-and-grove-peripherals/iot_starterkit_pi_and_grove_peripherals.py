# all configuration settings come from config.py
try:
	import config
except ImportError:
	print("Please copy template-config.py to config.py and configure appropriately !"); exit();

# debug_communication=1
debug_communication=0

import urllib3
import time
import json

import grovepi
from grove_oled import *

import sys
import signal

def send_to_hcp(http, url, headers, value):
	timestamp=int(time.time())
	# print(timestamp)

	body='{"mode":"async", "messageType":"' + str(config.message_type_id_From_device) + '", "messages":[{"sensor":"slider_device", "value":"' + str(value) + '", "timestamp":' + str(timestamp) + '}]}'
	# print(body)
	r = http.urlopen('POST', url, body=body, headers=headers)
	if (debug_communication == 1):
		print("send_to_hcp():" + str(r.status))
		print(r.data)

def poll_from_hcp(http, url, headers):
	global msg_string

	r = http.urlopen('GET', url, headers=headers)
	if (debug_communication == 1):
		print("poll_from_hcp():" + str(r.status))
		print(r.data)
	json_string='{"all_messages":'+(r.data).decode("utf-8")+'}'
	# print(json_string)

	try:
		json_string_parsed=json.loads(json_string)
		# print(json_string_parsed)
		# take care: if multiple messages arrive in 1 payload - their order is last in / first out - so we need to traverse in reverese order
		try:
			messages_reversed=reversed(json_string_parsed["all_messages"])
			for single_message in messages_reversed:
				# print(single_message)
				payload=single_message["messages"][0]
				opcode=payload["opcode"]
				operand=payload["operand"]
				# print(opcode)
				# print(operand)
				# now do things depending on the opcode
				if (opcode == "display"):
					# print(operand)
					# we write to the display at one centralized point only
					msg_string=operand
				if (opcode == "led"):
					if (operand == "0"):
						# print("LED off")
						switch_led(0)
					if (operand == "1"):
						# print("LED on")
						switch_led(1)
		except TypeError:
			print("Problem decoding the message " + (r.data).decode("utf-8") + " retrieved with poll_from_hcp()! Can and will continue though.")
	except ValueError:
		print("Problem decoding the message " + (r.data).decode("utf-8") + " retrieved with poll_from_hcp()! Can and will continue though.")
		
def read_slider_value(slider, old_value):
	try:
		# try to normalize to 0 - 100
		slider_value = (int)(grovepi.analogRead(slider) / 10.23)
		# print("slider value: ", slider_value)
	except IOError:
		print("IOError communicating with HW - can and will continue though.")
		slider_value=old_value
	return(slider_value)

def switch_led(arg):
	global led
	try:
		# LED is against Vcc - so HIGH switches it off / LOW on
		if (arg == 0):
			grovepi.digitalWrite(led, 1)
		if (arg == 1):
			grovepi.digitalWrite(led, 0)
	except IOError:
		print("IOError communicating with HW - can and will continue though.")

def redraw_oled(value, do_send, msg_string):
	# we do this in a central place to avoid concurrent access to the OLED
	oled_setTextXY(1,0)
	oled_putString("            ")
	oled_setTextXY(1,0)
	oled_putString("Value:"+str(value))
	oled_setTextXY(2,0)
	oled_putString("            ")
	if (do_send == 0):
		oled_setTextXY(2,0)
		oled_putString("Not sending!")
	if (do_send == 1):
		oled_setTextXY(2,0)
		oled_putString("Sending!")

	oled_setTextXY(4,0)
	oled_putString("            ")
	oled_setTextXY(4,0)
	# truncate to max 12 characters
	oled_putString(msg_string[:12])

def signal_handler(signal, frame):
	global slider_value

	print("You pressed Ctrl+C!")

	# try to finish with a consistent state
	switch_led(0)
	do_send=0
	msg_string="Stopped!"
	redraw_oled(slider_value, do_send, msg_string)

	sys.exit(0)

# === main starts here ===============================================

signal.signal(signal.SIGINT, signal_handler)

# disable InsecureRequestWarning if your are working without certificate verification
# see https://urllib3.readthedocs.org/en/latest/security.html
# be sure to use a recent enough urllib3 version if this fails
try:
	urllib3.disable_warnings()
except:
	print("urllib3.disable_warnings() failed - get a recent enough urllib3 version to avoid potential InsecureRequestWarning warnings! Can and will continue though.")

# use with or without proxy
if (config.proxy_url == ''):
	http = urllib3.PoolManager()
else:
	http = urllib3.proxy_from_url(config.proxy_url)

url='https://iotmms' + config.hcp_account_id + config.hcp_landscape_host + '/com.sap.iotservices.mms/v1/api/http/data/' + str(config.device_id)

headers = urllib3.util.make_headers(user_agent=None)

# use with authentication
headers['Authorization'] = 'Bearer ' + config.oauth_credentials_for_device
headers['Content-Type'] = 'application/json;charset=utf-8'

# initialize HW
# Connect the Grove Slide Potentiometer to analog port A0
slider=0   # pin 1 (yellow wire)
grovepi.pinMode(slider, "INPUT")

# Connect the Grove LED to digital port D4
led=4
grovepi.pinMode(led, "OUTPUT")
switch_led(0)

# Connect the OLED to an I2C connector
oled_init()
oled_clearDisplay()
oled_setNormalDisplay()
oled_setVerticalMode()

do_send=0
slider_value=0
msg_string=""

print("Entering main loop now")
print("Slide to 100 to start sending / to 0 to stop sending! Terminate with Ctrl+C!")
while 1:
	slider_value=read_slider_value(slider, slider_value)
	if (slider_value == 100):
		do_send=1
	if (slider_value == 0):
		do_send=0
	if (do_send == 1):
		send_to_hcp(http, url, headers, slider_value)
	poll_from_hcp(http, url, headers)

	redraw_oled(slider_value, do_send, msg_string)

	time.sleep(1)

