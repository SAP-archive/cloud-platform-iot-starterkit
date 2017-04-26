This directory contains the sources for a Java application that uses Certificate based authentication to interact with the SAP Cloud Platform Internet of Things. 
It consists of several files: 
  - Main.java for the main program logic
  - config.properties to specify necessary parameters
  - KeyStoreClient.java bundles Java keystore operations
  - HttpClient.java bundles HTTPS operations
  - several other files used mainly for serialization purposes
 
 It executes the following step-by-step procedure:
 1. Reads in the device type certificate from the provided folder and store it in the Java keystore to be used as client certificate for the HTTPS-connection
 2. If no device id is given, it calls the Remote Device Management Service (RDMS) and registers the device, otherwise the step is skipped 
 3. It checks its keystore if it already contains a device certificate for the considered device id
 4. If there is no device certificate, the following substeps are executed or otherwise skipped
 	4.1. A RSA key pair is generated
 	4.2  A Certificate Signing Request (CSR) is created where most of the necessary values are taken from the device type certificate.
 		 An option exist to use just one common name, the default are two.
 	4.3 A call to RDMS is triggered with the CSR that gives back the device certificate.
 	4.4 The device certificate is stored in the Java keystore.
 	4.5 A non-default option exists to store the device certificate and its confidential key as PEM-files. IMPORTANT: Please remove these security-relevant files after usage.
 5. It calls the Message Management Service (MMS) using the device certificate as client certificate and sends some sample data.   


Usage in an IDE:
- Import as a Maven project to your IDE
- Adapt the configuration file [configuration](src/main/resources/config.properties) according to in-line descriptions of the properties
- Run [main](src/main/java/com/sap/iot/starterkit/cert/Main.java) as Java application

>Note: A standard sample message with "sensor", "value", "timestamp" fileds is used in this example<br>
>Note: JDK 8 is required to compile this example
