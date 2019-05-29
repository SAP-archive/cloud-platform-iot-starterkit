# Copying data from a third-party Cloud service to SAP IoT Service

This folder contains an example that is supposed to be easily extensible. You are welcome to cherry-pick
the snippets that you find useful.

Please note that IoT Service provides links to Swagger/OpenAPI 2.0 specifications of the provided REST APIs.
You can find the links for your instance in the IoT Services Cockpit under the 'Useful links' menu item.
Modules like [Swagger Client](https://github.com/swagger-api/swagger-js) also available as
[NPM](https://www.npmjs.com/package/swagger-client) can be used to access the APIs directly and register devices,
sensors and sensor types, read out measurement data, etc.

## Description

The example polls the [Geotab API](https://my.geotab.com/) and copies some of the read data to
SAP IoT Service. The example uses the proxy IoT device module described above. To run the example, you need to
have accounts both with Geotab and SAP IoT Service. However, the approach and the code snippets are generic,
and the bulk of the code is not Geotab-specific and can be re-used in any project. This example also uses
Swagger Client described above.

## Requirements and installation

The example is written in ES8 and runs under recent versions of Node.js. This is server-side code, and we don't
recommend to run it in a Web browser.

The example depends on the [Proxy IoT Device module](./sap-iots-device-rest), which was not published on NPM yet.
It must be installed locally first with `npm install ../sap-iots-device-rest`.

Run `npm install` in this folder to fetch the other dependencies and install the example.

## Running the example

Run `npm start` in this folder.

## License

The code is provided under [SAP Sample Code License Agreement](./LICENSE).