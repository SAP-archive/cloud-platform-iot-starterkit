## Client Certificate Authentication

### Prerequisites
* Beta-enabled account
* Productive landscape (does not work on TRIAL)

### 1. Device Type Registration
(The device type registration can also be done using the IoT Cockpit.)

* Before registering and authenticating devices with client certificates a corresponding device type using client certificate authentication must be registered.

```
$ curl --header 'Content-Type: application/json' --basic --user '<username>' --data '{"name": "Device Type 1","authentication": {"type": "clientCertificate"}}' https://<rdms_host>/com.sap.iotservices.dms/v2/api/deviceTypes
```

* The request body specifies the device type ```name``` and authentication ```type```:
```
{
	"name": "Device Type 1",
 	"authentication": {
		"type": "clientCertificate"
	}
}
```

* The response contains the device type ```id``` as well as the device type certificate (```p12file```) and a corresponding ```secret```:
```
{   
  "id": "273e5b736a9af59689ba",   
  "name": "Device Type 1", 
  "messageTypes": [],
  "authentication": {
	"type": "clientCertificate",   
	"p12File": "MIACAQMwgAYJKoZIhvc…",
	"secret": "kdCG2SvwjKc4ftD"
  }
}
```

 * Save the base64-encoded *.p12 file to be used as device type certificate. For using the certificate (e.g. in Postman) you need to create a *.crt and a *.key file using the following openssl commands:

```
$ openssl pkcs12 -in 273e5b736a9af59689ba.p12 -out 273e5b736a9af59689ba.crt –nokeys
$ openssl pkcs12 -in 273e5b736a9af59689ba.p12 -out 273e5b736a9af59689ba.key -nocerts
```

### 2. Device Registration

* To register a device, the previously acquired device type certificate must be attached to the HTTPS connection. It will then be used during the initial SSL handshake as client certificate.

```
$ curl --header 'Content-Type: application/json' --cert ./<device_type_certificate>.p12:<secret> --data '{"name": "Device 1","id": "Device01","deviceType": "273e5b736a9af59689ba”}' https://<rdms_cert_host>/com.sap.iotservices.dms/v2/api/devices
```

* The request body specifies the device ```name```, ```id``` and ```deviceType```:
```
{
  "name": "Device 1",
  "id": "Device01",
  "deviceType": "273e5b736a9af59689ba"
}
```

* The ```id``` must be set manually as there is currently a problem when using auto-generated ids in the certificate’s common name. They result in a longer CN than the specified upper bound of 64 (https://www.ietf.org/rfc/rfc3280.txt).

* The response contains the device type ```id``` and confirms the authentication ```type```:
```
{
  "id": "Device01",
  "deviceType": "273e5b736a9af59689ba",
  "name": "Device 1",
  "authentication": {
      "type": "clientCertificate"
  }
}
```

### 3. Certificate Signing Request (CSR)
* To authenticate the registered device, a base64-encoded certificate signing request (CSR) in the PKCS #10 format is needed. A CSR and the corresponding key can be created using the following openssl command:

```
$ openssl req -nodes -newkey rsa:2048 -keyout Device01.key -out Device01.csr
```

* Several fields that are incorporated into the CSR need to be specified. Except for the common name, all other fields must be the same as the respective properties in the acquired device type certificate. The common name is be defined as ```deviceId:<deviceId>|tenantId:<tenantId>```, where the ```<tenantId>``` must also be extracted from the device type certificate.
	
```
Generating a 2048 bit RSA private key
..................................+++
writing new private key to Device01.key'

You are about to be asked to enter information that will be incorporated
into your certificate request.
What you are about to enter is what is called a Distinguished Name or a DN.
There are quite a few fields but you can leave some blank
For some fields there will be a default value,
If you enter '.', the field will be left blank.

Country Name (2 letter code) [AU]:DE
State or Province Name (full name) [Some-State]:.
Locality Name (eg, city) []:.
Organization Name (eg, company) [Internet Widgits Pty Ltd]:SAP Trust Community
Organizational Unit Name (eg, section) []:SAP POC IOT
Common Name (e.g. server FQDN or YOUR name) []:deviceId:Device01|tenantId:fdas567d-6f4e-49a6-a2f8-7bn836g99cd0
Email Address []:.

Please enter the following 'extra' attributes
to be sent with your certificate request
A challenge password []:********        
An optional company name []:.
```

* The result is a *.key and a *.csr file to be used as device key and certificate signing request (csr) when authenticating the device.

### 4. Device Authentication

* Finally the registered device must be authenticated, i.e. to receive the device certificate. Again the  acquired device type certificate must be attached to the HTTPS connection. It will then be used during the initial SSL handshake as client certificate.

```
$ curl --header 'Content-Type: application/json' --cert ./ ./<device_type_certificate>.p12:<secret> --data '{"type": "clientCertificate","csr": "MIIC6jCCAdICAQAwgYsxCzAJBgNVB…"}' https:// ://<rdms_cert_host>/com.sap.iotservices.dms/v2/api/devices/<deviceId>/authentication
```

* The request body entails the authentication ```type``` and the previously generated base64-encoded ```csr```:
```
{
  "type": "clientCertificate",
  "csr": "MIIDAzCCAesCAQAwgb0xCzAJBgNVB…"
}
```

* The response contains the base64-encoded ```x509Certificate```:
```
{
  "id": " Device01",
  "name": "Device 1", 
  "deviceType": "273e5b736a9af59689ba",
  "authentication": {
     "type": "clientCertificate",
     "x509Certificate": "MIIDfTCCAuagAwIBAgIOE7lkN8E1BOMQAQQHPUcwDQ…"
  }
}
```

* Save the certificate e.g. in a *crt file to be used as device certificate. The file must contain a certificate header/footer. The string formatting might also need some adaptation, e.g. correct line breaks. The resulting file should look similar to this:

```
-----BEGIN CERTIFICATE-----
MIIDfTCCAuagAwIBAgIOE7lkN8E1BOMQAQQHPUcwDQa7wdN7qpbHPgtCFbukj...
-----END CERTIFICATE-----
```