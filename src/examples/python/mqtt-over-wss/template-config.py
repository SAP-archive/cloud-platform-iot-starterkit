print("Please configure appropriately and then remove this line !"); exit();

# ===== Your specific configuration goes below / please adapt ========

# the HCP account id - trial accounts typically look like p[0-9]*trial
hcp_account_id='your_hcp_account_id'

# you only need to adapt this part of the URL if you are NOT ON TRIAL but e.g. on PROD
hcp_landscape_host='.hanatrial.ondemand.com'
# hcp_landscape_host='.hana.ondemand.com' # this is used on PROD

endpoint_certificate = "./hanatrial.ondemand.com.crt"
# you can download this certificate file with your browser from the app server in your landscape - it is used to check that the server is authentic
# we also provide the certificate that is valid in July 2016 at our github repo

# the following values need to be taken from the IoT Cockpit
device_id='the_id_of_the_device_you_created_in_the_iot_cockpit'

# the device specific OAuth token is used as MQTT password
oauth_credentials_for_device='the_oauth_token_shown_for_the_created_device'

message_type_id_From_device='the_message_type_id_From_device_you_created_in_the_iot_cockpit'

# ===== nothing to be changed / configured below this line ===========
