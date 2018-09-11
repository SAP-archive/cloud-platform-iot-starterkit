# step_08_send_data.sh

. ./config.sh

export REQUEST_URL=https://${HOST}/iot/gateway/rest/measures/${ALTERNATE_ID_4_DEVICE}

export PAYLOAD='{ "capabilityAlternateId" : '${MY_CAPABILITY}', "measures" : [ "value for A", "value for B" ], "sensorAlternateId":"'${ALTERNATE_ID_4_SENSOR}'" }'

# echo "Request URL is: ${REQUEST_URL}" 

${CURL} ${PROXY_SETTING} --header 'Content-Type: application/json' --cert ./${CREDENTIALS_FILE}.p12:${CREDENTIAL_PASSWORD} "${REQUEST_URL}" --data "${PAYLOAD}"
