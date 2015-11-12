# Getting started in the Cloud

## Deploy the Message Management Service (MMS)

>Previous Step [Create Device Information in Internet of Things Services Cockpit](../cockpit)

The deployment of the Message Management Service (MMS) step needs to be done from the ```Deploy Message Management Service``` tile in the IoT Services Cockpit and deploys/starts the Message Management Service that takes care of receiving data from IoT Devices and sending to these. 

![IoT Services Cockpit](../../../images/iot_cockpit_deploy_mms.png)

Then assign the Role IoT-MMS-User for the newly deployed ```iotmms``` Java Application (otherwise you will be denied access to the ```iotmms``` Application URL with an HTTP Status 403 Error). To do so: 
* Go to the Java Applications tab in your SAP HANA Cloud Platform cockpit of your account
* Choose the ```iotmms``` application
* Choose the Roles tab of the Application details
<br />
<br />
![Role assignment for MMS](../../../images/mms_role_assignment_01.png)

* Do the assignment of the role to your user.
<br />
<br />
![Role assignment for MMS](../../../images/mms_role_assignment_02.png)

Once MMS is deployed and you have correctly done role assignment you can click on the
```iotmms``` Java application URL in your HCP Cockpit and get to the MMS
Cockpit as shown below. It provides access to the MMS API as well as a "Display
stored messages" tile for the access to data received from IoT Devices.

![MMS Cockpit](../../../images/mms_cockpit.png?raw=true "MMS Cockpit")

>Next Step [Authentication Configuration (MMS Push API)](../authentication)