# Data models for the SAP Cloud Platform Internet of Things Edge Platform protocols

To set up the Internet of Things Edge Platform and publish data, you need to provide the data model required by the protocol. You can do so manually or by performing the following steps:

1. Adjust the [protocol-config.sh](protocol-config.sh) file with your instance information, credentials, and the protocol type.
2. Save the file.
3. Execute the shell script [protocol-model.sh](protocol-model.sh) using the Bash Unix shell.

Bash is the default shell on macOS and available for installation on Windows, for example, via Git for Windows.

>Note: The created capabilities are not unique across protocols. If you already created a protocol-specific data model, you must remove the created sensorTypes and capabilities before executing the script. This can best be done in the Internet of Things Service Cockpit.
