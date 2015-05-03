print("Please configure appropriately and then remove this line !"); exit();

# ===== Your specific configuration goes below / please adapt ========

# the HCP account id - trial accounts typically look like p[0-9]*trial
hcp_account_id='your_hcp_account_id'

# you only need to adapt this part of the URL if you are NOT ON TRIAL but e.g. on PROD
hcp_landscape_host='.hanatrial.ondemand.com'
# hcp_landscape_host='.hana.ondemand.com' # this is used on PROD

# these credentials are used from applications with the "push messages to devices" API
hcp_user_credentials='account_username:account_password'

# optional network proxy, set if to be used, otherwise set to ''
proxy_url=''
# proxy_url='http://proxy_host:proxy_port'

# the following values need to be taken from the IoT Cockpit
device_id='the_id_of_the_device_you_created_in_the_iot_cockpit'
oauth_credentials_for_device='the_oauth_token_shown_for_the_created_device'

message_type_id_From_device='the_message_type_id_From_device_you_created_in_the_iot_cockpit'
message_type_id_To_device='the_message_type_id_To_device_you_created_in_the_iot_cockpit'

# ===== nothing to be changed / configured below this line ===========
