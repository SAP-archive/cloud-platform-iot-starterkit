This directory has the sources for a XS and OData services that expose the IOT data out the data storage.

### Message Storage

Messages can be send to the HCP IoT Services using the Message Management Service (MMS) component deployed in the consumer account. By default MMS stores incoming messages into a relational database. 
The respective message tables are automatically created by MMS based on the device type and message type definitions. More details can be found in the IoT Services documentation.
By default, MMS uses the automatically created database (schema) for storing data and the default data source binding for accessing this schema. 
The name of the underlying database schema is displayed in the "Databases & Schemas" section of the HCP Cockpit. The default data source bindings for this schema are also displayed on this page. 
In the example below, MMS is bound to a shared HANA instance.

![Message Storage](../../../../images/xs_odata_01.jpg?raw=true "Message Storage")

The default schema binding can be changed to point to a dedicated HANA instance or any other database available in HCP.

### Creating a HANA XS instance

For the purpose of HANA XS development, a HANA XS database instance will be provided on HCP account-level in HCP. The instance can be created using the HCP cockpit.

![Creating a HANA XS instance](../../../../images/xs_odata_02.jpg?raw=true "Creating a HANA XS instance")

In the example below a HANA XS instance called “iotmmsxs” is created.

![Creating a HANA XS instance](../../../../images/xs_odata_03.jpg?raw=true "Creating a HANA XS instance")

The HANA XS instance will also show up as a new schema in the “Databases and Schemas” section.

![Creating a HANA XS instance](../../../../images/xs_odata_04.jpg?raw=true "Creating a HANA XS instance")

### Changing the database binding of MMS

In order to switch the MMS database binding from HANA to HANA XS, select the HANA schema and remove the “iotmms” binding. After that, select the new “iotmmsxs” schema and 
add a new <DEFAULT> binding to “iotmms”.

![Changing the database binding of MMS](../../../../images/xs_odata_05.jpg?raw=true "Changing the database binding of MMS")

After doing that MMS needs to be restarted. As soon as MMS will receive data from devices it will automatically create all tables in the new HANA XS instance. 
Hence, all the data can be accessed via HANA XS application running in the same HANA instance.

### HANA XS Development

Now we are ready for XS development. Only the browser based tools will be used for that. Click on Development Tools link available for your XS instance.

![HANA XS Development](../../../../images/xs_odata_06.jpg?raw=true "HANA XS Development")

This will open an Editor tool for you showing your account specific package.

![HANA XS Development](../../../../images/xs_odata_07.jpg?raw=true "HANA XS Development")

Right away open another web based tool which we will use in the next steps. Click on a black arrow right to green plus and select ‘Catalog’

![HANA XS Development](../../../../images/xs_odata_08.jpg?raw=true "HANA XS Development")

This will open a Catalog tool for you showing your DB schemas with their content. 

![HANA XS Development](../../../../images/xs_odata_09.jpg?raw=true "HANA XS Development")

Pay your attention that NEO_% schema is a real name of your (iotmmsxs) schema displayed in your HCP Cockpit. All T_IOT_% tables are landed there.

![HANA XS Development](../../../../images/xs_odata_10.jpg?raw=true "HANA XS Development")

Return back to Editor, right click on (iotmmsxs) and select Create Application.

![HANA XS Development](../../../../images/xs_odata_11.jpg?raw=true "HANA XS Development")

Select ‘Create in selected package’ option (this is not mandatory) and choose ‘Blank Application’ template. This will create a blank XS application for you. 

![HANA XS Development](../../../../images/xs_odata_12.jpg?raw=true "HANA XS Development")

[Drag and drop or create the missing files](iotmmsxs). The content of your package should look as follows:

![HANA XS Development](../../../../images/xs_odata_13.jpg?raw=true "HANA XS Development")

Adapt all necessary files and activate them.

### Grant Roles

Return back to Catalog tool and select a SQL button on top.

![Grant Roles](../../../../images/xs_odata_14.jpg?raw=true "Grant Roles")

Insert the [following SQL script](sql/grant_role.sql) there, adapt it and press green execute button. This should result in success execution.

### Start XS application

Return back to HCP Cockpit and navigate to HANA XS Application. 

![Start XS application](../../../../images/xs_odata_15.jpg?raw=true "Start XS application")

Click on application URL. This will open a default index.html file located in your XS application.

Add /iotservice.xsodata at the end of that URL. This will point you to OData service.
https://&lt;system_id>hanaxs.hanatrial.ondemand.com/&lt;user_id&gt;trial/iotmmsxs/iotservice.xsodata

The next link shows OData service metadata
https://&lt;system_id&gt;hanaxs.hanatrial.ondemand.com/&lt;user_id&gt;trial/iotmmsxs/iotservice.xsodata/$metadata 

The next link shows entity content in XML format
https://&lt;system_id&gt;hanaxs.hanatrial.ondemand.com/&lt;user_id&gt;trial/iotmmsxs/iotservice.xsodata/T_IOT_&lt;MESSAGE_TYPE_ID&gt;

The next link shows entity content in JSON format
https://&lt;system_id&gt;hanaxs.hanatrial.ondemand.com/&lt;user_id&gt;trial/iotmmsxs/iotservice.xsodata/T_IOT_&lt;MESSAGE_TYPE_ID&gt;?$format=json

XSJS Service based on old ```$.db``` interface
https://&lt;system_id&gt;hanaxs.hanatrial.ondemand.com/&lt;user_id&gt;trial/iotmmsxs/iotserviceolddbapi.xsjs

XSJS Service based on new ```$.hdb``` interface (available starting from HANA XS SP09)
https://&lt;system_id&gt;hanaxs.hanatrial.ondemand.com/&lt;user_id&gt;trial/iotmmsxs/iotservicenewhdbapi.xsjs