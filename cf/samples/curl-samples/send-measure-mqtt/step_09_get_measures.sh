# step_09_get_measures.sh

. ./config.sh

export REQUEST_URL='https://'${HOST}'/iot/processing/api/v1/tenant/'${TENANT}'/measures/capabilities/'${MY_CAPABILITY}'?orderby=timestamp%20desc&filter=deviceId%20eq%20%27'${MY_DEVICE}'%27&top=25'

# echo "Request URL is: ${REQUEST_URL}"

${CURL} ${PROXY_SETTING} --get --header 'Content-Type: application/json' --basic --user ${USER_PASS} "${REQUEST_URL}"
