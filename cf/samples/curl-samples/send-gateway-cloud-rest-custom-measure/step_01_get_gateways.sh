# step_01_get_gateways.sh

. ./config.sh

export REQUEST_URL=https://${INSTANCE}/iot/core/api/v1/gateways

# echo "Request URL is: ${REQUEST_URL}"

${CURL} ${PROXY_SETTING} --get --header 'Content-Type: application/json' --basic --user ${USER_PASS} ${REQUEST_URL}

# in reply
# online = Gateway Cloud instances
