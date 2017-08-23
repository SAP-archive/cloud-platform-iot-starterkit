# step_08_send_data.sh

. ./config.sh

export REQUEST_URL=https://${INSTANCE}/iot/gateway/rest/measures/${ALTERNATE_ID_4_DEVICE}

export PAYLOAD='{ "measureIds" : [ '${MY_CAPABILITY}' ], "values" : [ "value for A", "value for B" ], "logNodeAddr":"'${ALTERNATE_ID_4_SENSOR}'" }'

# echo "Request URL is: ${REQUEST_URL}" 

${CURL} ${PROXY_SETTING} --header 'Content-Type: application/json' --cert ./${CREDENTIALS_FILE}.p12:${CREDENTIAL_PASSWORD} "${REQUEST_URL}" --data "${PAYLOAD}"
