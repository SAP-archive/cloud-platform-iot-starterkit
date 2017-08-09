In addition to JSON encoded payloads it is also possible to use Google Protocol
Buffers for upstream or downstream messages transported via https. Please have
a look at the information at
[https://help.sap.com/viewer/7436c3125dd5491f939689f18954b1e9/Cloud/en-US/8e1c277be0cd4854943a15f86188aaec.html](https://help.sap.com/viewer/7436c3125dd5491f939689f18954b1e9/Cloud/en-US/8e1c277be0cd4854943a15f86188aaec.html)
for a description of the mechanism, the available types and their mappings as
well as an example .proto file.

In order to use the examples you need to have the respective tooling (protocol
compiler as well as language bindings) for protobuf in Version 3 installed -
see
[https://github.com/google/protobuf/releases](https://github.com/google/protobuf/releases)

* [Upstream example]./protobuf-upstream()
* [Downstream example]./protobuf-downstream()


