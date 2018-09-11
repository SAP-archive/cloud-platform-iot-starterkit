# step_09_get_measures.sh

. ./config.sh

export REQUEST_URL=https://${HOST}/iot/processing/api/v1/tenant/${TENANT}/measures/capabilities/${MY_CAPABILITY}

# echo "Request URL is: ${REQUEST_URL}"

${CURL} ${PROXY_SETTING} --get --header 'Content-Type: application/json' --basic --user ${USER_PASS} ${REQUEST_URL}
