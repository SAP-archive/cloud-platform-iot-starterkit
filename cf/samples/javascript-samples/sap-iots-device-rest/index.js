const https = require('https');

const httpsAgentOptions = {
    keepAlive: true,
    maxSockets: 16, // the maximum number of sockets per host
}

const httpsAgent = new https.Agent(httpsAgentOptions);

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
                        let errorMessages = '';
                        try {
                            const resp = JSON.parse(responseBody);
                            console.debug(resp);
                            errorMessages = resp[0].messages.join('\n');
                        } catch (e) {
                            // ignore JSON parse errors
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