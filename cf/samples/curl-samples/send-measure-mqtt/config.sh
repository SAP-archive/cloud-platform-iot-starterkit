export CURL=/usr/bin/curl

export INSTANCE='<your instance>.cp.iot.sap'
export CRT_4_LANDSCAPE='<crt for the landsacpe you are in> e.g. eu10cpiotsap.crt'

export USER='root'
export PASSWORD='<your password>'
export USER_PASS="${USER}:${PASSWORD}"

# export PROXY_SETTING="--proxy http://<proxy>:<port>"
export PROXY_SETTING="--noproxy \"*\""

export ALTERNATE_ID_4_DEVICE="SK-MQTT-0123456789"
export ALTERNATE_ID_4_SENSOR="SK-MQTT-0123456789"

export CERT_FILE="cert"
export CREDENTIALS_FILE="credentials"
export CREDENTIAL_PASSWORD="1234"

# ======================================================================== 
# these settings have to be filled in as you go through the steps
# so please replace the NN with the respective value that is shown in the output

# from step_01
export GW_ID_4_MQTT="NN"

# from step_02
export MY_DEVICE="NN"

# from step_05
export MY_CAPABILITY="NN"

# from step_06
export MY_SENSORTYPE="NN"

# from step_07
export MY_SENSOR="NN"
