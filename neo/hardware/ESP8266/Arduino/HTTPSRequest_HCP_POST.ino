// post data from a device upstream to SAP HANA Cloud Platform IoT Services

// the program is based on the HTTPS example for ESP8266 - see below
/*
 *  HTTP over TLS (HTTPS) example sketch
 *
 *  This example demonstrates how to use
 *  WiFiClientSecure class to access HTTPS API.
 *  We fetch and display the status of
 *  esp8266/Arduino project continous integration
 *  build.
 *
 *  Created by Ivan Grokhotkov, 2015.
 *  This example is in public domain.
 */

#include <ESP8266WiFi.h>
#include <WiFiClientSecure.h>

// ========== start configuration ==========
// WiFi configuration
const char* ssid = "<your_ssid>";
const char* password = "<your_wifi_password>";

// SAP HCP specific configuration
const char* host = "iotmms<your_trial_user>trial.hanatrial.ondemand.com";
String device_id = "<your_device_id>";
String message_type_id = "<your_message_type_id>";
String oauth_token="<your_oauth_token>";

// ========== end configuration ============

String url = "/com.sap.iotservices.mms/v1/api/http/data/" + device_id;

// just an example payload corresponding to the other Starterkit examples
String post_payload = "{\"mode\":\"async\", \"messageType\":\"" + message_type_id + "\", \"messages\":[{\"sensor\":\"sensor1\", \"value\":\"20\", \"timestamp\":1413191650}]}";

const int httpsPort = 443;

void setup() {
  Serial.begin(115200);
  Serial.println();
  Serial.print("connecting to ");
  Serial.println(ssid);
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());

  // Use WiFiClientSecure class to create TLS connection
  WiFiClientSecure client;
  Serial.print("connecting to ");
  Serial.println(host);
  if (!client.connect(host, httpsPort)) {
    Serial.println("connection failed");
    return;
  }
  
  Serial.print("requesting URL: ");
  Serial.println(url);

  // Serial.println(post_payload);
  
  // using HTTP/1.0 enforces a non-chunked response
  client.print(String("POST ") + url + " HTTP/1.0\r\n" +
               "Host: " + host + "\r\n" +
               "Content-Type: application/json;charset=utf-8\r\n" +
               "Authorization: Bearer " + oauth_token + "\r\n" +
               "Content-Length: " + post_payload.length() + "\r\n\r\n" +
               post_payload + "\r\n\r\n");
               
  Serial.println("request sent");

  Serial.println("reply was:");
  Serial.println("==========");
  while (client.connected()) {
    String line = client.readStringUntil('\n');
    Serial.println(line);
  }
  Serial.println("==========");
  Serial.println("closing connection");
}

void loop() {
}
