# step_04_get_certificate.sh
# save request answer to file and convert then
# - replace \n with CRLF
# - convert to individual .key and .crt file without password

. ./config.sh

export REQUEST_URL=https://${INSTANCE}/iot/core/api/v1/devices/${MY_DEVICE}/authentication/pem

# echo "Request URL is: ${REQUEST_URL}"

${CURL} ${PROXY_SETTING} --get --header 'Content-Type: application/json' --basic --user ${USER_PASS} ${REQUEST_URL} > ${CERT_FILE}.raw

sed 's/^.*"secret":"//' ${CERT_FILE}.raw > pw.raw
sed 's/","pem":".*$//' pw.raw > pw.txt

sed 's/^.*pem":"//' ${CERT_FILE}.raw > ${CERT_FILE}.raw.1
sed 's/"}]}$//' ${CERT_FILE}.raw.1 > ${CERT_FILE}.raw.2
sed 's/\\n/\n/g' ${CERT_FILE}.raw.2 > ${CERT_FILE}.txt

rm -f ${CERT_FILE}.raw.*
rm -f pw.raw

export IMPORT_PASSWORD=`cat pw.txt`

echo "Please use pass phrase ${IMPORT_PASSWORD} for the certificate import from ${CERT_FILE}.txt in the conversion !"

openssl rsa -in ${CERT_FILE}.txt -out ${CREDENTIALS_FILE}.key
openssl x509 -in ${CERT_FILE}.txt -out ${CREDENTIALS_FILE}.crt
