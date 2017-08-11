# step_05_create_capability.sh

. ./config.sh

export REQUEST_URL=https://${INSTANCE}/iot/core/api/v1/capabilities

export PAYLOAD='{ "name" : "Starterkit Capability 02", "properties" : [ { "name" : "Starterkit Property A", "dataType" : "string" }, { "name" : " Starterkit Property B", "dataType" : "string" } ] }'

# echo "Request URL is: ${REQUEST_URL}" 

${CURL} ${PROXY_SETTING} --header 'Content-Type: application/json' --basic --user "${USER_PASS}" "${REQUEST_URL}" --data "${PAYLOAD}"
