This directory has the sources for a Java application that uses the HCP IoT
Services to interact with an IoT Device. It renders the received data that is
stored in the database (that the MMS part of the IoT Services writes to) in an
xy plot and lets you send data to the device via the API that the MMS part
exposes for this purpose.

![UI5 Consumption Example](../../../../images/consumption_ui5_01.jpg?raw=true "UI5 Consumption Example")

In order to compile and deploy our example application please get the sources to your system and go to the com.sap.iot.starterkit.ui directory.

### Compilation and deployment
* Build the project with Maven using ```mvn clean install``` - it will produce the .war file to be used in the target directory
* Deploy the .war file using the ```HCP Cockpit > Java Application > Deploy Application``` but don't start it yet

![UI .war file deployment](../../../../images/ui_war_file_deployment.png?raw=true "UI .war file deployment")

* Select the application and go to ```Destinations > Import from file``` (use the provided file [iotmms](./com.sap.iot.starterkit.ui/destinations/iotmms) in the ```com.sap.iot.starterkit.ui/destinations``` folder) to correctly route requests from your User Interface to the ```iotmms``` application providing the MMS Services

![Destination configuration](../../../../images/destination_configuration_01.png?raw=true "Destination configuration")

* Within this process adapt URL, User and Password fields with the information for your account

![Destination configuration](../../../../images/destination_configuration_02.png?raw=true "Destination configuration")

* Select the application and go to ```Destinations > Import from file``` (use the provided file [iotrdms](./com.sap.iot.starterkit.ui/destinations/iotrdms) in the ```com.sap.iot.starterkit.ui/destinations``` folder) to correctly route requests from your User Interface to the ```iotrdms``` application providing the RDMS Services

![Destination configuration](../../../../images/destination_configuration_03.png?raw=true "Destination configuration")

* Create a new Data Source Binding to ensure that both the iotmms as well as your UI application use the same database schema

![Data Source binding](../../../../images/data_source_binding_01.png?raw=true "Data Source binding")

![Data Source binding](../../../../images/data_source_binding_02.png?raw=true "Data Source binding")

Finally start your UI application by clicking at the URL for the running application and use it.

### Further References
For general information about the development of Java Applications on the HANA Cloud Platform please also refer to
* SAP HANA Cloud Platform > Java: Getting Started > Installing Java Tools for Eclipse and SDK > Installing the SDK https://help.hana.ondemand.com/help/frameset.htm?7613843c711e1014839a8273b0e91070.html 
* SAP HANA Cloud Platform > Java: Getting Started > Samples https://help.hana.ondemand.com/help/frameset.htm?937ce0d172bb101490cf767db0e91070.html
* SAP HANA Cloud Platform > Java: Getting Started > Samples > Building Samples with Maven https://help.hana.ondemand.com/help/frameset.htm?841e3eaf32fa4bc3becc6ccd50758278.html
* SAP HANA Cloud Platform > Java: Getting Started > Samples > Building Samples with Maven > Building Samples from the Command Line https://help.hana.ondemand.com/help/frameset.htm?ad423da413994430bfd9564633f7bc52.html
* SCN - Building Java Web Applications with Maven http://scn.sap.com/community/developer-center/cloud-platform/blog/2014/05/27/building-java-applications-with-maven
