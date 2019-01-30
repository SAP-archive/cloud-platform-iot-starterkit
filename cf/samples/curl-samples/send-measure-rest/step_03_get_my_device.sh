# step_03_get_my_device.sh

. ./config.sh

# last part of path = requested device - get this from step_02

export REQUEST_URL=https://${HOST}/${INSTANCE}/iot/core/api/v1/tenant/${TENANT}/devices/${MY_DEVICE}

# or list all
# export REQUEST_URL=https://${INSTANCE}/iot/core/api/v1/devices

# echo "Request URL is: ${REQUEST_URL}"

${CURL} ${PROXY_SETTING} --get --header 'Content-Type: application/json' --basic --user ${USER_PASS} ${REQUEST_URL}
