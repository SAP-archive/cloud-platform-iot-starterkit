/**
 * Code sample for demonstrating Cloud-to-Cloud integration with SAP IoT Service.
 *
 * Proxy objects instantiated from this module allow for POSTing data to SAP IoT Service Cloud.
 * The data is posted on behalf of the IoT device, the credentials of which are provided while
 * creating the proxy object.
 *
 * The module uses asynchronous functions (co-routines) extensively. Several HTTP connections
 * can be established and used in parallel by a single proxy object.
 *
 * @module sap-ios-device-rest
 * @author Mikhail Bessonov <mikhail.bessonov@sap.com>
 * @license
 * SAP Sample Code License Agreement, see the LICENSE file
 */

const https = require('https');

const httpsAgentOptions = {
    keepAlive: true,
    // The maximum number of sockets per host
    maxSockets: 16,
}

const httpsAgent = new https.Agent(httpsAgentOptions);

/**
 * @typedef {Object} DeviceProxy - A proxy object for POSTing measurement data to IoT Service
 * on behalf of the IoT device.
 * @property {function} postMeasurementData - see the postMeasurementData() method
 */

/**
 * Create a proxy object communicating with the SAP IoT Service.
 *
 * The proxy object communicates to a Cloud IoT Service instance via a REST gateway on behalf of an IoT device.
 * Authentication is performed using the client certificate of the device in the PEM format.
 * Such a certificate (and the corresponding private key and its passphrase) can be obtained
 * via the SAP IoT Services Web GUI, so called Cockpit. Alternatively, it can be obtained via the
 * IoT Service REST API; a link to the Swagger/OpenAPI 2.0 description can be found in the Cockpit.
 * @param {!string} hostname - A fully qualified domain name of the instance, like uuid-1-2-3.eu10.cp.iot.sap
 * @param {!string} clientCertPEM - The client certificate of the device in the PEM format.
 *     It may contain the private key too, which will be ignored.
 * @param {!string} clientPrivateKeyPEM - The private key of the device in the PEM format.
 *     It may contain the certificate too, which will be ignored.
 * @param {string} passphrase - The secret with which the private key is symmetrically encrypted.
 * @return {DeviceProxy} A proxy device object
 */
module.exports = function (hostname, clientCertPEM, clientPrivateKeyPEM, passphrase) {
    if (typeof hostname !== 'string') {
        throw new TypeError('hostname must be a string');
    }
    if (typeof clientCertPEM !== 'string') {
        throw new TypeError('clientCertPEM must be a string');
    }
    if (typeof clientPrivateKeyPEM !== 'string') {
        throw new TypeError('clientPrivateKeyPEM must be a string');
    }
    if (typeof passphrase !== 'string') {
        throw new TypeError('passphrase must be a string');
    }

    const params = {
        'hostname': hostname,
        'clientCertPEM': clientCertPEM,
        'clientPrivateKeyPEM': clientPrivateKeyPEM,
        'passphrase': passphrase
    }

    return {
        postMeasurementData: postMeasurementData
    };

    /**
     * Post an array of measurement data to IoT Services via a REST gateway.
     * @param {!string} deviceAlternateId - the alternate ID of the device on whose behalf we post the measurement data.
     *     The device must be previously created in IoT Service via the Web GUI (Cockpit) or the REST API.
     *     The certificate provided in the PEM form to the factory function exported by the module must have a matching
     *     CN: either including the deviceAlternateId, or belonging to a router device.
     * @param {!string} sensorAlternateId - the alternate ID of the sensor, which must be previously created.
     * @param {!string} capabilityAlternateId - the alternate ID of the Capability, a record-like type supported
     *     by the sensor. The Capability must be previously created.
     * @param {!Array} measurements - the measurement data. The elements of the array must be either arrays or objects.
     *     If they are arrays, the length of each array, the order of elements and their data types must match
     *     the properties defined by the Capability. If they are objects, the keys must be strings that match
     *     the names of the properties, and the values must have the types specified for the properies
     *     defined by the Capability.
     */
    async function postMeasurementData(deviceAlternateId, sensorAlternateId, capabilityAlternateId, measurements) {
        if (!Array.isArray(measurements)) {
            measurements = [measurements];
        }
        for (const measurement of measurements) {
            if (typeof measurement !== 'object') {
                throw new TypeError('measurements must be an object or an array of objects');
            }
        }
        if (typeof deviceAlternateId !== 'string') {
            throw new TypeError('deviceAlternateId must be a string');
        }
        if (typeof sensorAlternateId !== 'string') {
            throw new TypeError('sensorAlternateId must be a string');
        }
        if (typeof capabilityAlternateId !== 'string') {
            throw new TypeError('capabilityAlternateId must be a string');
        }

        const payload = {
            'capabilityAlternateId': capabilityAlternateId,
            'sensorAlternateId': sensorAlternateId,
            'measures': measurements
        };

        const body = JSON.stringify(payload);

        let promise = new Promise(function(resolve, reject) {
            const httpsOptions = {
                hostname: params.hostname,
                port: 443,
                path: '/iot/gateway/rest/measures/' + deviceAlternateId,
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                key: params.clientPrivateKeyPEM,
                passphrase: params.passphrase,
                cert: params.clientCertPEM,
                agent: httpsAgent
            };

            const request = https.request(httpsOptions, (response) => {
                console.debug('REST gateway response code: %d', response.statusCode);
                response.setEncoding('utf8');
                let responseBody = '';
                response.on('data', (chunk) => {
                    responseBody += chunk;
                    console.debug('Got chunk of HTTP response:', chunk);
                });
                response.on('end', () => {
                    console.debug('End of HTTP response.');
                    if (response.statusCode === 200 || response.statusCode === 202) {
                        resolve(response.statusCode);
                    } else {
                        // POSTing the measurement data failed. Try to recover the error messages, and throw an exception.
                        let errorMessages = '';
                        try {
                            const resp = JSON.parse(responseBody);
                            console.debug(resp);
                            errorMessages = resp[0].messages.join('\n');
                        } catch (e) {
                            // Ignore JSON parse errors and any other problems since we are handling an error already.
                        }
                        reject(Error(`Posting measurement data for device '${deviceAlternateId}', sensor '${sensorAlternateId}', capability '${capabilityAlternateId}' failed with status code ${response.statusCode}. The SAP error messages: "${errorMessages}"`));
                    }
                });
            });
            request.on('error', (e) => {
                console.error('Error sending HTTPS request:', e.message);
                reject(Error(e.message));
            });
            request.setHeader('Content-Length', Buffer.byteLength(body));
            request.write(body);
            request.end();
        });

        return promise;
    }
}