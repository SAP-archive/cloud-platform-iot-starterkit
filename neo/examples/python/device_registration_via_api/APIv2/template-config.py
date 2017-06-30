print("Please configure appropriately and then remove this line !"); exit();

# ===== Your specific configuration goes below / please adapt ========

# the HCP account id - trial accounts typically look like p[0-9]*trial
hcp_account_id='your_hcp_account_id'

# you only need to adapt this part of the URL if you are NOT ON TRIAL but e.g. on PROD
hcp_landscape_host='.hanatrial.ondemand.com'
# hcp_landscape_host='.hana.ondemand.com' # this is used on PROD

# these credentials are used from applications with the RDMS API
hcp_user_credentials='account_username:account_password'

# optional network proxy, set if to be used, otherwise set to ''
proxy_url=''
# proxy_url='http://proxy_host:proxy_port'

# ===== nothing to be changed / configured below this line ===========
