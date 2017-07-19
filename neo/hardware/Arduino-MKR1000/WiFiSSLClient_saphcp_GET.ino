// retrieve data that has been pushed downstream to a device via Push API using SAP HANA Cloud Platform IoT Services 
// make sure you have stored the server side certificate on your Arduino MKR1000

// the program is based on the HTTPS example from the WiFi101 library - see below

/*
This example creates a client object that connects and transfers
data using always SSL.

It is compatible with the methods normally related to plain
connections, like client.connect(host, port).

Written by Arturo Guadalupi
last revision November 2015

*/

#include <SPI.h>
#include <WiFi101.h>

char ssid[] = "<your_ssid>";
char pass[] = "<your_wifi_password>";
int keyIndex = 0;            // your network key Index number (needed only for WEP)

int status = WL_IDLE_STATUS;

// SAP HCP specific configuration
const char* host = "iotmms<your_trial_user>trial.hanatrial.ondemand.com";
String device_id = "<your_device_id>";
String oauth_token="<your_oauth_token>";
// ========== end configuration ============

String url = "/com.sap.iotservices.mms/v1/api/http/data/" + device_id;
const int httpsPort = 443;

WiFiSSLClient client;

void setup() {
  //Initialize serial and wait for port to open:
  Serial.begin(9600);
  while (!Serial) {
    ; // wait for serial port to connect. Needed for native USB port only
  }

  // check for the presence of the shield:
  if (WiFi.status() == WL_NO_SHIELD) {
    Serial.println("WiFi shield not present");
    // don't continue:
    while (true);
  }

  // attempt to connect to Wifi network:
  while (status != WL_CONNECTED) {
    Serial.print("Attempting to connect to SSID: ");
    Serial.println(ssid);
    // Connect to WPA/WPA2 network. Change this line if using open or WEP network:
    status = WiFi.begin(ssid, pass);

    Serial.print(".");
    delay(1000);
  }
  Serial.println("");
  Serial.println("Connected to wifi");
  printWifiStatus();

  while (true) {
    Serial.print("connecting to ");
    Serial.println(host);
    if (!client.connect(host, httpsPort)) {
      Serial.println("connection failed");
      return;
    }
    
    Serial.print("requesting URL: ");
    Serial.println(url);
  
    // using HTTP/1.0 enforces a non-chunked response
    client.print(String("GET ") + url + " HTTP/1.0\r\n" +
                 "Host: " + host + "\r\n" +
                 "Content-Type: application/json;charset=utf-8\r\n" +
                 "Authorization: Bearer " + oauth_token + "\r\n" +
                 "\r\n");
                 
    Serial.println("request sent");
  
    Serial.println("reply was:");
    Serial.println("==========");
    while (client.connected()) {
      String line = client.readStringUntil('\n');
      Serial.println(line);
    }
    Serial.println("==========");
    Serial.println("closing connection");
    delay(5000);
  }
}

void loop() {
}

void printWifiStatus() {
  // print the SSID of the network you're attached to:
  Serial.print("SSID: ");
  Serial.println(WiFi.SSID());

  // print your WiFi shield's IP address:
  IPAddress ip = WiFi.localIP();
  Serial.print("IP Address: ");
  Serial.println(ip);

  // print the received signal strength:
  long rssi = WiFi.RSSI();
  Serial.print("signal strength (RSSI):");
  Serial.print(rssi);
  Serial.println(" dBm");
}
