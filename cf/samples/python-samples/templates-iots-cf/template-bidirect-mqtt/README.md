We provide examples that cover the full process of creating and using devices.
These program also generate code for the repective usage scenarios.

Usage:
* modify according to your IoTS CF instance and their credentials
* adapt desired alternateID for device, capabilities and sensor
* adapt properties in capabilities and resulting payloads
* run: python2.7 iots-cf-template-bidirect-mqtt.py (generates more code)
* run: sh ./convert_pem.sh to convert credentials
* ingest with: python2.7 mqtt-client.py
* retrieve ingested values with: python2.7 retrieve.py
* send commands to mqtt-client.py with: python2.7 originate-commands.py
