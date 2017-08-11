# step_07_create_sensor.sh

. ./config.sh

export REQUEST_URL=https://${INSTANCE}/iot/core/api/v1/sensors

export DESIRED_PHYSICAL_ADDRESS_4_SENSOR=${PHYSICAL_ADDRESS_4_SENSOR}

export PAYLOAD='{ "name": "Starterkit sensor 01", "deviceId" : "'${MY_DEVICE}'", "sensorTypeId" : "'${MY_SENSORTYPE}'", "physicalAddress" : "'${DESIRED_PHYSICAL_ADDRESS_4_SENSOR}'" }'

# echo "Request URL is: ${REQUEST_URL}" 

${CURL} ${PROXY_SETTING} --header 'Content-Type: application/json' --basic --user "${USER_PASS}" "${REQUEST_URL}" --data "${PAYLOAD}"
