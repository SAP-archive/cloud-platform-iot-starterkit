// Imports

// Native Node.js modules
const fs = require('fs');
const path = require('path');
const storage = require('node-persist');

// Generic third-party modules
const Swagger = require('swagger-client');

// Geotab modules
const Geotab_API = require('mg-api-node');

// SAP modules
const SAP_IoT_device_REST = require('../sap-iots-device-rest');


// Constants

// Delay in ms between Geotab polling requests returning no data
const POLLING_DELAY_MS = 1000;
// The limit on the number of polling iterations. Should be set to 0 in production.
const DEBUG_ITERATIONS_LIMIT = 20;
// The maximum number of records to fetch from a Geotab database per poll request.
const MAX_RECORDS_PER_ITERATION = 100;


// Read the configuration file.
const config = readConfigFile('config.json');

// init geotab
const geotab = new Geotab_API(config.Geotab_username, config.Geotab_password, config.Geotab_database);
// TODO: use the start date only if no previous records of this type were forwarded to SAP.
const startDate = new Date((new Date()).getTime() - (7 * 24 * 60 * 60 * 1000)).toISOString();
// console.log("Start date:", startDate);

// init SAP

const SAP_Swagger_API = `https://${config.SAP_instance}.${config.SAP_landscape}/${config.SAP_instance}/iot/core/api/v1/doc/swagger/api`;

let router_device;
let SAP_DeviceManagement;
let gatewayId;
let sensorTypeId_LogRecord;
let sensorTypeId_StatusData;

const DATATYPE = {
    integer: 'integer', 
    long: 'long', 
    float: 'float', 
    double: 'double',
    boolean: 'boolean', 
    string: 'string', 
    binary: 'binary', 
    date: 'date'
}

const geotabDeviceCache = {}; // key is Geotab device ID, value is the VIN of the car
const geotabDiagnosticCache = {}; // key is Geotab Diagnostic ID, value is a dictionary of properties

main();




async function main() {
	
    SAP_DeviceManagement = await new Swagger(SAP_Swagger_API, {
        authorizations: {
            basicAuth: {
                username: config.SAP_username,
                password: config.SAP_password
            }
        }
    });
    //console.log('SAP northbound API', SAP_DeviceManagement);
    //console.log('Apis:', SAP_DeviceManagement.apis);
    //console.log('Definitions:', SAP_DeviceManagement.spec.definitions);

    sensorTypeId_LogRecord = await createType_LogRecord();
    sensorTypeId_StatusData = await createType_StatusData();

    gatewayId = await findCloudGatewayRest();
    router_device = await initRouterDevice();
    
    await storage.init();
    await Promise.all([
        copy_LogRecords(),
        copy_StatusData()
    ]);
}

/** Read the config file in JSON format synchronosly, parse the content and return the result. */
function readConfigFile(filename) {
    const abs_path = path.join(__dirname, filename);
    const json = fs.readFileSync(abs_path, {
        'encoding': 'utf8'
    });
    return JSON.parse(json);
}

async function initRouterDevice() {
    const routerDeviceId = await createDeviceUnlessExists(gatewayId, 'router', true);
    const absPathPem = path.join(__dirname, config.SAP_router_device_pem_filename);
    const absPathPass = path.join(__dirname, config.SAP_router_device_pass_filename);
    if (!(fs.existsSync(absPathPem) && fs.existsSync(absPathPass))) {
        const resp = await SAP_DeviceManagement.apis.Devices.getPEMCertificateUsingGET({
            tenantId: config.SAP_tenantID,
            deviceId: routerDeviceId
        });
        // console.debug(resp);
        if (resp.ok) {
            fs.writeFileSync(absPathPem, resp.body.pem);
            fs.writeFileSync(absPathPass, resp.body.secret);
            console.info('Saved the PEM and the passphrase for the router device.');
        }
    }

    const router_device_pem = fs.readFileSync(absPathPem, {
        'encoding': 'utf8'
    }); // contains both the certificate and the private key
    const router_device_passphrase = fs.readFileSync(absPathPass, {
        'encoding': 'utf8'
    }); // the private key is encrypted with it
    console.debug('The PEM and the passphrase for the router device read OK.');
    const gateway_hostname = `${config.SAP_instance}.${config.SAP_landscape}`;
    return new SAP_IoT_device_REST(gateway_hostname, router_device_pem, router_device_pem, router_device_passphrase);
}

async function createType_LogRecord() {
    const capId = await createCapabilityUnlessExists('LogRecord', [
        { name: 'latitude',  dataType: DATATYPE.float },
        { name: 'longitude', dataType: DATATYPE.float },
        { name: 'speed',     dataType: DATATYPE.float },
        { name: 'dateTime',  dataType: DATATYPE.date }
    ]);
    return createSensorTypeUnlessExists('LogRecord', capId);
}

async function createType_StatusData() {
    const capId = await createCapabilityUnlessExists('StatusData', [
        { name: 'value',          dataType: DATATYPE.float }, // Geotab 'data'
        { name: 'name',           dataType: DATATYPE.string },  // Diagnostic
        { name: 'code',           dataType: DATATYPE.integer }, // Diagnostic
        { name: 'diagnosticType', dataType: DATATYPE.string },  // Diagnostic
        { name: 'faultResetMode', dataType: DATATYPE.string },  // Diagnostic
        { name: 'dateTime',       dataType: DATATYPE.date }
    ]);
    return createSensorTypeUnlessExists('StatusData', capId);
}


async function copy_LogRecords() {
    const dateType_LogRecord = 'LogRecord';
	let fromVersion = await loadFromVersion(dateType_LogRecord);
    
    let iterationsLeft = DEBUG_ITERATIONS_LIMIT;
    do {
        let result = await readGeotabRecords(dateType_LogRecord, fromVersion);
        //console.debug(result);
        if (Array.isArray(result.data) && result.data.length > 0) {
            fromVersion = result.toVersion;
            let statusCode = await post_LogRecords(result.data);
            console.info(`Got SAP status code: ${statusCode}\n`);
            await storeFromVersion(dateType_LogRecord, fromVersion);
        } else {
            // The fetch returned no records, wait to reduce the polling load
            await sleep(POLLING_DELAY_MS);
        }
    } while (DEBUG_ITERATIONS_LIMIT <= 0 || --iterationsLeft > 0);
}

async function copy_StatusData() {
    const dateType_StatusData = 'StatusData';
	let fromVersion = await loadFromVersion(dateType_StatusData);
    
    let iterationsLeft = DEBUG_ITERATIONS_LIMIT;
    do {
        let result = await readGeotabRecords(dateType_StatusData, fromVersion);
        //console.debug(result);
        if (Array.isArray(result.data) && result.data.length > 0) {
            fromVersion = result.toVersion;
            let statusCode = await post_StatusData(result.data);
            console.info(`Got SAP status code: ${statusCode}\n`);
            await storeFromVersion(dateType_StatusData, fromVersion);
        } else {
            // The fetch returned no records, wait to reduce the polling load
            await sleep(POLLING_DELAY_MS);
        }
    } while (DEBUG_ITERATIONS_LIMIT <= 0 || --iterationsLeft > 0);
}

async function loadFromVersion(dataType) {
	let from_version = await storage.getItem(dataType);
    return from_version;
}

async function storeFromVersion(dataType, version) {
	await storage.setItem(dataType, version);
    return version;
}

async function readGeotabRecords(typeName, fromVersion) {
    return new Promise(function (resolve, reject) {
        geotab.call('GetFeed', {
            fromVersion,
            typeName,
            resultsLimit: MAX_RECORDS_PER_ITERATION,
            search: {
                fromDate: startDate
            }
        }, function (error, result) {
            if (error) {
                console.error('Got error:', error);
                reject(Error(error));
            } else {
                console.debug(
                    `Got LogRecord, toVersion: ${result.toVersion}, number or records: ${result.data.length}`);
                // console.debug('First record:', JSON.stringify(result.data[0], null, 4));
                resolve(result);
            }
        });
    });
}

async function lookupDevice(geotabDeviceId) {
    return new Promise(function (resolve, reject) {
        geotab.call('Get', {
            typeName: 'Device',
            search: {
                id: geotabDeviceId
            }
        }, function (error, result) {
            if (error) {
                console.error('Got error:', error);
                reject(Error(error));
            } else {
                // console.debug('Got result:', result);
                if (result.length > 0) {
                    resolve(result[0]);
                } else {
                    reject(Error('Device not found!'));
                }
            }
        });
    });
}

async function lookupDiagnostic(diagnosticId) {
    return new Promise(function (resolve, reject) {
        geotab.call('Get', {
            typeName: 'Diagnostic',
            search: {
                id: diagnosticId
            }
        }, function (error, result) {
            if (error) {
                console.error('Got error:', error);
                reject(Error(error));
            } else {
                // console.debug('Got result:', result);
                if (result.length > 0) {
                    resolve(result[0]);
                } else {
                    reject(Error('Diagnostic not found!'));
                }
            }
        });
    });
}

async function post_LogRecords(records) {
    const measurements = {}; // Key: vehicle indentification number (VIN), value: an array of measurements
    for (const record of records) {
        const geotabDeviceId = record.device.id;
        let vin = geotabDeviceCache[geotabDeviceId];
        if (!vin) {
            try {
                const device = await lookupDevice(geotabDeviceId);
                // console.debug('Got device:', device);
                vin = device.vehicleIdentificationNumber;
                if (!vin) {
                    console.error(`Geotab device with ID ${geotabDeviceId} has no vehicleIdentificationNumber, skipped!`);
                   continue;
                }
                console.info(`Caching the SAP device for the vehicle with VIN='${vin}'`);
                const sapDeviceId = await createDeviceUnlessExists(gatewayId, vin);
                await createSensorUnlessExists(sapDeviceId, 'LogRecord', sensorTypeId_LogRecord);
                geotabDeviceCache[geotabDeviceId] = vin;
            } catch (err) {
                console.error(`Could not look up device with ID ${geotabDeviceId}!`);
                continue;
            }
        }
        (measurements[vin] = measurements[vin] || []).push({
            latitude: record.latitude,
            longitude: record.longitude,
            speed: record.speed,
            dateTime: record.dateTime
        });
    }

    const promises = [];
    for (const vin of Object.keys(measurements)) {
        promises.push(router_device.postMeasurementData(vin, 'LogRecord', 'LogRecord', measurements[vin]));
    }
    return Promise.all(promises);
}


async function post_StatusData(records) {
    const measurements = {}; // Key: vehicle indentification number (VIN), value: an array of measurements
    for (const record of records) {
        const geotabDeviceId = record.device.id;
        let vin = geotabDeviceCache[geotabDeviceId];
        if (!vin) {
            try {
                const device = await lookupDevice(geotabDeviceId);
                // console.debug('Got device:', device);
                vin = device.vehicleIdentificationNumber;
                if (!vin) {
                    console.error(`Geotab device with ID ${geotabDeviceId} has no vehicleIdentificationNumber, skipped!`);
                    continue;
                }
                console.info(`Caching the SAP device for the vehicle with VIN='${vin}'`);
                const sapDeviceId = await createDeviceUnlessExists(gatewayId, vin);
                await createSensorUnlessExists(sapDeviceId, 'StatusData', sensorTypeId_StatusData);
                geotabDeviceCache[geotabDeviceId] = vin;
            } catch (err) {
                console.error(`Could not look up device with ID ${geotabDeviceId}!`);
                continue;
            }
        }
        
        const diagnosticId = record.diagnostic.id;
        let diagnostic = geotabDiagnosticCache[diagnosticId];
        if (!diagnostic) {
            try {
                diagnostic = await lookupDiagnostic(diagnosticId);
                // console.debug('Got diagnostic:', diagnosticRecord);
                console.info(`Caching the SAP Diagnostic with ID='${diagnosticId}'`);
                geotabDiagnosticCache[diagnosticId] = diagnostic;
            } catch (err) {
                console.error(`Could not look up Diagnostic with ID ${diagnosticId}!`);
                continue;
            }
        }

        let value = parseFloat(record.data);
        if (isNaN(value)) {
            console.error('Numeric value expected. Skipping!');
            continue;
        }
        (measurements[vin] = measurements[vin] || []).push({
            value,
            name: diagnostic.name,
            code: diagnostic.code,
            diagnosticType: diagnostic.diagnosticType,
            faultResetMode: diagnostic.faultResetMode,
            dateTime: record.dateTime
        });
    }

    const promises = [];
    for (const vin of Object.keys(measurements)) {
        promises.push(router_device.postMeasurementData(vin, 'StatusData', 'StatusData', measurements[vin]));
    }
    return Promise.all(promises);
}


async function createCapabilityUnlessExists(alternateId, properties) {
    const existingCaps = await SAP_DeviceManagement.apis.Capabilities.getCapabilitiesUsingGET(
        {
            tenantId: config.SAP_tenantID,
            filter: `alternateId eq '${alternateId}'`
        }
    );
    //console.debug('existing:', existingCaps);
    
    if (!(existingCaps.ok && Array.isArray(existingCaps.body))) {
        throw new Error('Lookup of existing capabilities failed!');
    }
    
    if (existingCaps.body.length > 0) {
        console.debug(`Capability with alternateId '${alternateId}' found, ID='${existingCaps.body[0].id}'`);
        return existingCaps.body[0].id;
    }
        
    // The lookup was OK, but the capability with this alternate ID was not found.
    console.info(`Creating a new capability '${alternateId}'`);
    const res = await SAP_DeviceManagement.apis.Capabilities.createCapabilityUsingPOST(
        {
            tenantId: config.SAP_tenantID,
            request: {
                alternateId,
                properties,
                name: alternateId 
            }
        }
    );
    // console.info(`... status code: ${res.status}, capability ID: ${res.body.id}`);
    
    if (!res.ok) {
        throw new Error('Capability creation failed!');
    }
    
    console.info(`Capability '${alternateId}' created with ID='${res.body.id}'.`);
    return res.body.id;
}

async function createSensorTypeUnlessExists(typeName, capabilityId, isCommand=false) {
    const existingTypes = await SAP_DeviceManagement.apis.SensorTypes.getSensorTypesUsingGET(
        {
            tenantId: config.SAP_tenantID,
            filter: `name eq '${typeName}'`
        }
    );
    //console.debug('existing:', existingTypes);
    
    if (!(existingTypes.ok && Array.isArray(existingTypes.body))) {
        throw new Error('Lookup of existing sensor types failed!');
    }

    // Names are not guaranteed to be unique.
    for (const type of existingTypes.body) {
        for (const cap of type.capabilities) {
            // TODO: check for command
            if (cap.id === capabilityId) {
                console.debug(`Sensor type with name '${typeName}' found, ID='${type.id}'`);
                return type.id;
            }
        }
    }
        
    // The lookup was OK, but the sensor type with this name was not found.
    console.info(`Creating a new sensor type '${typeName}'`);
    const res = await SAP_DeviceManagement.apis.SensorTypes.createSensorTypeUsingPOST(
        {
            tenantId: config.SAP_tenantID,
            request: {
                name: typeName,
                capabilities: [{id: capabilityId, type: isCommand ? 'command' : 'measure'}]
            }
        }
    );
    // console.info(`... status code: ${res.status}, sensor type ID: ${res.body.id}`);
    
    if (!res.ok) {
        throw new Error('Sensor type creation failed!');
    }
    
    console.info(`Sensor type '${typeName}' created with ID '${res.body.id}'.`);
    return res.body.id;
}

async function findCloudGatewayRest() {
    const gateways = await SAP_DeviceManagement.apis.Gateways.getGatewaysUsingGET(                {
            tenantId: config.SAP_tenantID,
            filter: "alternateId eq 'GATEWAY_CLOUD_REST'"
        }
    );
    if (!(gateways.ok && Array.isArray(gateways.body) && gateways.body.length > 0)) {
        throw new Error('Lookup of Cloud REST gateway failed!');
    }
    console.info(`Cloud REST gateway ID is '${gateways.body[0].id}'`);
    return gateways.body[0].id;
}

async function createDeviceUnlessExists(gatewayId, alternateId, isRouter=false) {
    const existingDevices = await SAP_DeviceManagement.apis.Devices.getDevicesUsingGET(
        {
            tenantId: config.SAP_tenantID,
            filter: `(alternateId eq '${alternateId}') and (gatewayId eq '${gatewayId}')`
        }
    );
    //console.debug('existing:', existingDevices);
    
    if (!(existingDevices.ok && Array.isArray(existingDevices.body))) {
        throw new Error('Lookup of existing devices failed!');
    }
    
    if (existingDevices.body.length > 0) {
        console.debug(`Device with alternateId '${alternateId}' found, ID='${existingDevices.body[0].id}'`);
        //console.debug(existingDevices.body[0].authentications);
        return existingDevices.body[0].id;
    }
        
    // The lookup was OK, but the capability with this alternate ID was not found.
    console.info(`Creating a new device '${alternateId}'`);
    const request = {
        gatewayId,
        alternateId,                
        name: alternateId
    }
    if (isRouter) {
        request.authorizations = [{type: 'router'}];
    }
    const res = await SAP_DeviceManagement.apis.Devices.createDeviceUsingPOST(
        {
            request,
            tenantId: config.SAP_tenantID
        }
    );
    // console.info(`... status code: ${res.status}, device ID: ${res.body.id}`);
    
    if (!res.ok) {
        throw new Error('Device creation failed!');
    }
    
    console.info(`Device '${alternateId}' created with ID='${res.body.id}'.`);
    return res.body.id;
}

async function createSensorUnlessExists(deviceId, alternateId, sensorTypeId) {
    const existingSensors = await SAP_DeviceManagement.apis.Sensors.getSensorsUsingGET(
        {
            tenantId: config.SAP_tenantID,
            filter: `(deviceId eq '${deviceId}') and (alternateId eq '${alternateId}')`
        }
    );
    //console.debug('existing:', existingSensors);
    
    if (!(existingSensors.ok && Array.isArray(existingSensors.body))) {
        throw new Error('Lookup of existing sensors failed!');
    }
    
    if (existingSensors.body.length > 0) {
        console.debug(`Sensor with alternateId '${alternateId}' found in device '${deviceId}', ID='${existingSensors.body[0].id}'`);
        return existingSensors.body[0].id;
    }
        
    // The lookup was OK, but the capability with this alternate ID was not found.
    console.info(`Creating a new sensor '${alternateId}'`);
    const res = await SAP_DeviceManagement.apis.Sensors.createSensorUsingPOST(
        {
            tenantId: config.SAP_tenantID,
            request: {
                deviceId,
                alternateId,
                sensorTypeId,
                name: alternateId 
            }
        }
    );
    // console.info(`... status code: ${res.status}, sensor ID: ${res.body.id}`);
    
    if (!res.ok) {
        throw new Error('Sensor creation failed!');
    }
    
    console.info(`Sensor '${alternateId}' for device '${deviceId}' created with ID='${res.body.id}'.`);
    return res.body.id;
}

/** Asyc sleep for a single co-routine. */
function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

