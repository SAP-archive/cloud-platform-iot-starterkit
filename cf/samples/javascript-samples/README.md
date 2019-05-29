# JavaScript code samples

This folder contains simple examples that are supposed to be easily extensible. You are welcome to cherry-pick
the snippets that you find useful.

Please note that IoT Service provides links to Swagger/OpenAPI 2.0 specifications of the provided REST APIs.
You can find the links for your instance in the IoT Services Cockpit under the 'Useful links' menu item.
Modules like [Swagger Client](https://github.com/swagger-api/swagger-js) also available as 
[NPM](https://www.npmjs.com/package/swagger-client) can be used to access the APIs directly and register devices,
sensors and sensor types, read out measurement data, etc.

The examples provided so far run under recent versions of Node.js. They may run under some modern Web browsers too,
but we did not test it.

* [Proxy IoT device module](./sap-iots-device-rest) can be used to create proxy objects representing IoT devices
  and POST measurement data from them to IoT Service via a Cloud REST gateway.
* [Geotab example](./geotab) polls the [Geotab API](https://my.geotab.com/) and copies some of the read data to
  SAP IoT Service. The example uses the proxy IoT device module described above. To run the example, you need to
  have accounts both with Geotab and SAP IoT Service. However, the approach and the code snippets are generic,
  and the bulk of the code is not Geotab-specific and can be re-used in any project. This example also uses
  Swagger Client described above.

