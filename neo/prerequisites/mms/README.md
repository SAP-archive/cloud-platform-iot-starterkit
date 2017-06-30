# Getting started in the Cloud

## Deploy the Message Management Service (MMS)

>Previous Step [Create Device Information in Internet of Things Cockpit](../cockpit)

The deployment of the Message Management Service (MMS) step needs to be done from the ```Deploy Message Management Service``` tile in the Internet of Things Cockpit and deploys/starts the Message Management Service that takes care of receiving data from IoT Devices and sending to these. 

![Internet of Things Cockpit](../../../images/iot_cockpit_deploy_mms.png)

Then assign the Role IoT-MMS-User for the newly deployed ```iotmms``` Java Application (otherwise you will be denied access to the ```iotmms``` Application URL with an HTTP Status 403 Error). To do so: 
* Go to the Java Applications tab in your SAP Cloud Platform Cockpit of your account
* Choose the ```iotmms``` application
* Choose the Security tab of the Application details and then select Roles
<br />
<br />
![Role assignment for MMS](../../../images/mms_role_assignment_01.png)

* Do the assignment of the role to your user.
<br />
<br />
![Role assignment for MMS](../../../images/mms_role_assignment_02.png)

Once MMS is deployed and you have correctly done role assignment you can click on the
```iotmms``` Java application URL in your SAP Cloud Platform Cockpit and get to the MMS
Cockpit as shown below. It provides access to the MMS API as well as a "Display
stored messages" tile for the access to data received from IoT Devices.

![MMS Cockpit](../../../images/mms_cockpit.png?raw=true "MMS Cockpit")