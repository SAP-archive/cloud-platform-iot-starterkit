# Revision history and related advice

The SAP HANA Cloud Platform Internet of Things (IoT) Services are continously
developed further and thus a new version of the Message Management Service (MMS)
becomes available from time to time. The availability of such an update is
indicated in the respective MMS User Interface and an update is highly
recommended. Please deploy a new version of MMS from the IoT Services Cockpit
into your HCP account. Please also pay attention that the naming convention for the
IoT tables has been changed in the transition from BETA to General Availability.
IoT tables are named according to the following pattern now
```T_IOT_<MESSAGE_TYPE_ID>```. You might need to adapt the consumption part of
your IoT Business Applications.  For more information, please see the
documentation at https://help.hana.ondemand.com/iot
