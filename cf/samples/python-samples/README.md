We provide various examples that cover typical scenarios where IoT Service
functionality is integrated with additional data sources / sinks. Within these
that are intentionally kept simple we indicate potential EXTENSION POINTS where
the enhancement for specific usage can be done.

The IoT Service specific creation of device instances with their specifics
(capabilities with properties, sensortypes, sensors) can be done based on our
templates and is assumed as a precondition for sample usage.

* [Templates and program generators for device creation and usage](./templates-iots-cf)
* [Downstream transfer of binary data (e.g. files) and commands combined with upstream heartbeats](./binary-data-commands-and-heartbeats-via-mqtt)
* [Upstream transfer of data received via a Websocket link and keeping this link continuously alive](./ws-interaction)
* [Integration with an ingesting (upstream) HTTP client or receiving (downstream) HTTP server](./integration-with-http-client-and-server)

