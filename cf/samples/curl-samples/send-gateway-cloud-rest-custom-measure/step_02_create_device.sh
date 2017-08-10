# step_02_create_device.sh

. ./config.sh

export REQUEST_URL=https://${INSTANCE}/iot/core/api/v1/devices

export MY_DESIRED_PHYSICAL_ADDRESS_4_DEVICE=${PHYSICAL_ADDRESS_4_DEVICE}

export DESIRED_NAME_4_DEVICE="STARTERKIT_DEVICE_REST_01"

export PAYLOAD='{ "gatewayId" : "'${GW_ID_4_REST}'", "name" : "'${DESIRED_NAME_4_DEVICE}'", "physicalAddress" : "'${MY_DESIRED_PHYSICAL_ADDRESS_4_DEVICE}'" }'

# echo "Request URL is: ${REQUEST_URL}"

${CURL} ${PROXY_SETTING} --header 'Content-Type: application/json' --basic --user "${USER_PASS}" "${REQUEST_URL}" --data "${PAYLOAD}"

# in reply - information about the created device
