# all configuration settings come from config.py
try:
	import config
except ImportError:
	print("Please copy template-config.py to config.py and configure appropriately !"); exit();

# this can be used to activate debugging
# debug_communication=1
debug_communication=0

try:
    # for Python2
    from Tkinter import *
except ImportError:
    # for Python3
    from tkinter import *

import json
import time
import urllib3

def send_to_hcp():
	global debug_communication
	global http
	global url
	global headers
	global s1

	timestamp=int(time.time())
	# print(timestamp)

	body='{"mode":"async", "messageType":"' + str(config.message_type_id_From_device) + '", "messages":[{"sensor":"slider_desktop", "value":"' + str(s1.get()) + '", "timestamp":' + str(timestamp) + '}]}'
	# print(body)
	r = http.urlopen('POST', url, body=body, headers=headers)
	if (debug_communication == 1):
		print("send_to_hcp():" + str(r.status))
		print(r.data)

def poll_from_hcp():
	global debug_communication
	global http
	global url
	global headers

	global t1
	global f4_cb1

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
					t1.config(state=NORMAL)
					t1.delete(1.0, END)
					t1.insert(END, operand)
					t1.config(state=DISABLED)
				if (opcode == "led"):
					f4_cb1.config(state=NORMAL)
					if (operand == "0"):
		                		f4_cb1.deselect()
					if (operand == "1"):
		                		f4_cb1.select()
					f4_cb1.config(state=DISABLED)
		except TypeError:
			print("Problem decoding the message " + (r.data).decode("utf-8") + " retrieved with poll_from_hcp()! Can and will continue though.")
	except ValueError:
		print("Problem decoding the message " + (r.data).decode("utf-8") + " retrieved with poll_from_hcp()! Can and will continue though.")
		
def handle_slider(event):
	global do_send
	global s1
	global cb1

	value=s1.get()
	if (value == 100):
		do_send=1
		cb1.config(state=NORMAL)
		cb1.select()
		cb1.config(state=DISABLED)
		# print("Start sending now !")
	if (value == 0):
		do_send=0
		# print("Stop sending now !")
		cb1.config(state=NORMAL)
		cb1.deselect()
		cb1.config(state=DISABLED)
	# print("slider value: ", value)

def handle_exit_button():
	exit()

def my_send_timer():
	global root
	global do_send
	# print("my_send_timer")
	if (do_send == 1):
		send_to_hcp()
	root.after(1000, my_send_timer)

def my_poll_timer():
	global root
	# print("my_poll_timer")
	poll_from_hcp()
	root.after(1000, my_poll_timer)

def build_and_start_ui_with_timers():
	global root
	global s1
	global cb1
	global f4_cb1
	global t1

	root=Tk()
	
	root.resizable(FALSE,FALSE)
	root.title("IoT Starterkit - Device Simulator")
	root.geometry('+50+50') 
	
	l1=Label(root, text="Data that the device sends", font = "TkDefaultFont 14 bold")
	l1.pack()
	
	l2=Label(root, text="Slide to 100 to start sending values once per second, slide to 0 to stop sending")
	l2.pack()
	
	s1=Scale(root, from_=0, to=100, orient=HORIZONTAL, command = handle_slider)
	s1.configure(length=500)
	s1.pack()
	
	cb1=Checkbutton(root, text="Sending now", state=DISABLED)
	cb1.pack()
	
	f1=Frame(root, height=3, width=500)
	f1.pack()
	
	f2=Frame(root, height=1, width=500, bg="black")
	f2.pack()
	
	f3=Frame(root, height=3, width=500)
	f3.pack()
	
	l3=Label(root, text="Data that the device receives", font = "TkDefaultFont 14 bold")
	l3.pack()
	
	f4=Frame(root, width=500)
	f4.pack()
	f4.l1=Label(f4, text="Remote controlled LED (on/off)")
	f4.l1.pack(side=LEFT)
	f4_cb1=Checkbutton(f4, state=DISABLED)
	f4_cb1.pack(side=LEFT)
	
	l4=Label(root, text="Messages sent to the device")
	l4.pack()
	
	t1=Text(root, height=10, width=70, borderwidth=2, relief=SUNKEN, state=DISABLED)
	# t1=Text(root, height=10, width=50, borderwidth=2)
	t1.pack()
	t1.config(state=NORMAL)
	t1.insert(END, "Nothing received yet")
	t1.config(state=DISABLED)
	
	b1=Button(root, text="Exit", command=handle_exit_button)
	b1.pack()

	my_send_timer()
	my_poll_timer()

	root.mainloop()

# === main starts here ===============================================
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

do_send=0

build_and_start_ui_with_timers()

