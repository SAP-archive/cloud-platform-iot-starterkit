This directory has the sources for a XSODATA and XSJS services that expose the IOT data out the data storage.

### Message Storage

Messages can be send to the HCP IoT Services using the Message Management Service (MMS) component deployed in the consumer account. By default MMS stores incoming messages into a relational database. 
The respective message tables are automatically created by MMS based on the message type definitions. More details can be found in the IoT Services documentation.
By default, MMS uses the automatically created database (schema) for storing data and the default data source binding for accessing this schema. 
The name of the underlying database schema is displayed in the "Databases & Schemas" section of the HCP Cockpit. The default data source bindings for this schema are also displayed on this page. 
In the example below, MMS is bound to a shared HANA instance.

![](../../../../images/xs/0001.png)

The default schema binding can be changed to point to a dedicated HANA instance or any other database available in HCP. HANA MDC will be used in this example.

### Creating and configuring HANA MDC instance

Click on New, select HANA MDC Database, give it a name i.e. "iotmmsxs" and specify a password for your Database user. Please, note that your Database user is "SYSTEM" be default.

![](../../../../images/xs/0002.png)

Wait until the Database is created and started

![](../../../../images/xs/0003.png)

Navigate to Overview, re-check the Database state and then click on SAP HANA Cockpit at the bottom

![](../../../../images/xs/0004.png)

Once prompted, specify your Database user "SYSTEM" and password you have given above when creating MDC

![](../../../../images/xs/0005.png)

Click OK to be assigned with the necessary Administration roles

![](../../../../images/xs/0006.png)

Continue after successful message popup is shown

![](../../../../images/xs/0007.png)

This will navigate you to SAP HANA Cockpit

![](../../../../images/xs/0008.png)

Select "Manage Roles and Users" tile, choose your user "SYSTEM" and assign it with the roles required for the Web-based Tools, like stated in the [documentation](https://help.hana.ondemand.com/help/frameset.htm?d7c4ca5dac4f4dbbb47901eebe9ea0d1.html) 

![](../../../../images/xs/0009.png)

### Changing the database binding of MMS

Navigate to Java Applications, select "iotmms" and stop it

![](../../../../images/xs/0010.png)

Select "Data Source Bindings" and delete the default binding for HANA system

![](../../../../images/xs/0011.png)

Create a new default binding to the HANA MDC "iotmmsxs", specify your Database user "SYSTEM" and password as Custom Logon, save your changes

![](../../../../images/xs/0012.png)

Start your "iotmms" Java application

![](../../../../images/xs/0013.png) 

Launch "iotmms", navigate to HTTP Sample Client and send some data on behalf of the embedded device (or use a real one). With that a T_IOT* table is created in the Database under your user schema (it has a "SYSTEM" name same to your user).

![](../../../../images/xs/0014.png) 

In order to check Database content, navigate to HANA MDC Overview and click on SAP HANA Web-based Development Workbench at the bottom

![](../../../../images/xs/0004.png) 

Select "Catalog" tile

![](../../../../images/xs/0015.png) 

Find your Database user schema "SYSTEM", expand its Tables and show the content of the T_IOT table

 ![](../../../../images/xs/0016.png) 

### HANA XS Development

Now we are ready for XS development. Only the browser based tools will be used for that.

Select "Editor" tile

![](../../../../images/xs/0015.png) 

Create a new package under Content

![](../../../../images/xs/0017.png) 

Give it a name i.e. "iotmmsxs" and press Create

![](../../../../images/xs/0018.png) 

[Drag and drop or create the missing files](iotmmsxs) in the following order:

- .xsapp
- .xsprivileges
- .xsaccess
- iotservice.xsodata

Do not forget to activate them.

The content of your package should look as follows

![](../../../../images/xs/0019.png) 

If you try to run your XSODATA service immediately by clicking on green run button on top, this will result in 403 Forbidden error, because Application privileges are missing for your user.

![](../../../../images/xs/0020.png) 

Select "Security" tile

![](../../../../images/xs/0015.png) 

Choose your user "SYSTEM" and navigate to "Application Privileges" tab

![](../../../../images/xs/0021.png) 

Select "iotmmsxs::Basic" privilege form the list and confirm your changes

![](../../../../images/xs/0022.png) 

Now when launching XSODATA service, it will return you the Service Root

![](../../../../images/xs/0023.png) 

Adding /$metadata at the end of the URL will give you the Metadata

![](../../../../images/xs/0024.png)

And Entity Set is then accessible when adding a concrete one at the end of the URL

![](../../../../images/xs/0025.png)
 
You may also consume the data with XSJS after activating and launching the [iotservices.xsjs](iotmmsxs) script

![](../../../../images/xs/0026.png)