# step_09_get_measures.sh

. ./config.sh

export REQUEST_URL=https://${INSTANCE}/iot/core/api/v1/devices/${MY_DEVICE}/measures

# echo "Request URL is: ${REQUEST_URL}"

${CURL} ${PROXY_SETTING} --get --header 'Content-Type: application/json' --basic --user ${USER_PASS} ${REQUEST_URL}
