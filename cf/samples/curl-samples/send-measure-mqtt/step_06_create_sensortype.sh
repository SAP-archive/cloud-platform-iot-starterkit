# step_06_create_sensortype.sh

. ./config.sh

export REQUEST_URL=https://${INSTANCE}/iot/core/api/v1/sensorTypes

export PAYLOAD='{ "name" : "Starterkit Sensor Type 02", "capabilities" : [ { "id" : "'${MY_CAPABILITY}'", "type" : "measure" } ] }'

# echo "Request URL is: ${REQUEST_URL}" 

${CURL} ${PROXY_SETTING} --header 'Content-Type: application/json' --basic --user "${USER_PASS}" "${REQUEST_URL}" --data "${PAYLOAD}"
