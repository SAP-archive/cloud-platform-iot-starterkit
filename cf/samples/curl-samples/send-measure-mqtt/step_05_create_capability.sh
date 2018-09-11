# step_05_create_capability.sh

. ./config.sh

export REQUEST_URL=https://${HOST}/${INSTANCE}/iot/core/api/v1/tenant/${TENANT}/capabilities

export PAYLOAD='{ "name" : "Starterkit Capability 02", "properties" : [ { "name" : "Starterkit_Property_A", "dataType" : "string" }, { "name" : "Starterkit_Property_B", "dataType" : "string" } ] }'

# echo "Request URL is: ${REQUEST_URL}" 

${CURL} ${PROXY_SETTING} --header 'Content-Type: application/json' --basic --user "${USER_PASS}" "${REQUEST_URL}" --data "${PAYLOAD}"
