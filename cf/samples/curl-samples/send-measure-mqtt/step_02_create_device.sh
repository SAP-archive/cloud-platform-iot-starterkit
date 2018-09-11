# step_02_create_device.sh

. ./config.sh

export REQUEST_URL=https://${HOST}/${INSTANCE}/iot/core/api/v1/tenant/${TENANT}/devices

export MY_DESIRED_ALTERNATE_ID_4_DEVICE=${ALTERNATE_ID_4_DEVICE}

export DESIRED_NAME_4_DEVICE="STARTERKIT_DEVICE_MQTT_01"

export PAYLOAD='{ "gatewayId" : "'${GW_ID_4_MQTT}'", "name" : "'${DESIRED_NAME_4_DEVICE}'", "alternateId" : "'${MY_DESIRED_ALTERNATE_ID_4_DEVICE}'" }'

# echo "Request URL is: ${REQUEST_URL}"

${CURL} ${PROXY_SETTING} --header 'Content-Type: application/json' --basic --user "${USER_PASS}" "${REQUEST_URL}" --data "${PAYLOAD}"

# in reply - information about the created device
