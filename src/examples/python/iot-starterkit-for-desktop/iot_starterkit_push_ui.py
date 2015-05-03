# all configuration settings come from config.py
try:
	import config
except ImportError:
	print("Please copy template-config.py to config.py and configure appropriately !"); exit();

# this can be used to activate debugging
# print_communication_status_codes=1
print_communication_status_codes=0

try:
    # for Python2
    from Tkinter import *
except ImportError:
    # for Python3
    from tkinter import *

import urllib3

def send_to_hcp(opcode, operand):
	global print_communication_status_codes
	global http
	global push_url
	global headers

	# print(opcode)
	# print(operand)

	body='{"method":"http", "sender":"Push test UI", "messageType":"' + str(config.message_type_id_To_device) + '", "messages":[{"opcode":"' + opcode + '", "operand":"' + operand + '"}]}'
	# print(body)
	r = http.urlopen('POST', push_url, body=body, headers=headers)
	if (print_communication_status_codes == 1):
		print("send_to_hcp():" + str(r.status))
	# print(r.data)

def handle_checkbutton():
	global cb_value
	# print("Handling checkbutton")
	# print(cb_value.get())
	send_to_hcp("led", cb_value.get())

def handle_send_button():
	global t1
	send_to_hcp("display", str(t1.get("1.0",'end-1c')))

def handle_exit_button():
	exit()

def build_and_start_ui():
	global t1
	global cb_value

	root=Tk()
	
	root.resizable(FALSE,FALSE)
	root.title("IoT Starterkit - Push service UI")
	root.geometry('+50+50')
	
	l1=Label(root, text="Data to be sent to the device", font = "TkDefaultFont 14 bold")
	l1.pack()
	
	f1=Frame(root, height=10)
	f1.pack()
	
	f2=Frame(root)
	f2.pack(fill=X)
	f2.l1=Label(f2, text="Switch LED State (on/off)")
	f2.l1.pack(side=LEFT, fill=NONE)
	
	cb_value=StringVar()
	cb_value.set("0")
	f2.cb1=Checkbutton(f2, onvalue="1", offvalue="0", command=handle_checkbutton, variable=cb_value)
	f2.cb1.pack(side=LEFT)
	
	f3=Frame(root)
	f3.pack(fill=X)
	f3.l1=Label(f3, text="Message text")
	f3.l1.pack(side=LEFT, fill=NONE)
	f3.l2=Label(f3, text="")
	f3.l2.pack(side=LEFT, fill=X)
	
	t1=Text(root, height=10, width=50, borderwidth=2, relief=SUNKEN, state=NORMAL)
	t1.pack()
	t1.insert(END, "Your text goes here")
	t1.config(relief=SUNKEN)
	
	f3=Frame(root)
	f3.pack()
	
	f3.b1=Button(f3, text="Send", command=handle_send_button)
	f3.b1.pack(side=LEFT)
	
	f3.b2=Button(f3, text="Exit", command=handle_exit_button)
	f3.b2.pack(side=LEFT)
	
	root.mainloop()

# === main starts here ===============================================
# disable InsecureRequestWarning if your are working without certificate verification
# see https://urllib3.readthedocs.org/en/latest/security.html
# be sure to use a recent enough urllib3 version if this fails
try:
	urllib3.disable_warnings()
except:
	print('urllib3.disable_warnings() failed - get a recent enough urllib3 version to avoid potential InsecureRequestWarning warnings! Can and will continue though.')

# use with or without proxy
if (config.proxy_url == ''):
	http = urllib3.PoolManager()
else:
	http = urllib3.proxy_from_url(config.proxy_url)

push_url='https://iotmms' + config.hcp_account_id + config.hcp_landscape_host + '/com.sap.iotservices.mms/v1/api/http/push/' + str(config.device_id)

# use with authentication
headers = urllib3.util.make_headers(basic_auth=config.hcp_user_credentials)
headers['Content-Type'] = 'text/plain;charset=utf-8'

build_and_start_ui()
