# This script is based on AutobahnPython/examples/twisted/websocket/echo_tls/client.py from the https://github.com/tavendo/AutobahnPython repository

###############################################################################
#
# The MIT License (MIT)
#
# Copyright (c) Tavendo GmbH
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
# THE SOFTWARE.
#
###############################################################################

import sys
from optparse import OptionParser

from twisted.python import log
from twisted.internet import reactor, ssl

from autobahn.twisted.websocket import WebSocketClientFactory, \
    WebSocketClientProtocol, \
    connectWS

from base64 import b64encode

class IoTServicesClientProtocol(WebSocketClientProtocol):

    def sendToHCP(self):
# send message of Message Type 1 and the corresponding payload layout that you defined in the IoT Services Cockpit
        self.sendMessage('{"mode":"async", "messageType":"1", "messages":[{"sensor":"sensor1", "value":"20", "timestamp":1413191650}]}'.encode('utf8'))

    def onOpen(self):
        self.sendToHCP()

    def onMessage(self, payload, isBinary):
        if not isBinary:
            print("Text message received: {}".format(payload.decode('utf8')))

if __name__ == '__main__':

    log.startLogging(sys.stdout)

    parser = OptionParser()

# interaction for a specific Device instance - replace 1 with your specific Device ID
    parser.add_option("-u", "--url", dest="url", help="The WebSocket URL", default="wss://iotmms_on_your_trialinstance.hanatrial.ondemand.com/com.sap.iotservices.mms/v1/api/ws/data/1")
    (options, args) = parser.parse_args()

    # create a WS server factory with our protocol
    ##
    factory = WebSocketClientFactory(options.url, debug=False)
    headers={'Authorization': 'Bearer ' + 'your_oauth_token'}
    # print(headers)
    factory = WebSocketClientFactory(options.url, headers=headers, debug=False)
    factory.protocol = IoTServicesClientProtocol

    # SSL client context: default
    ##
    if factory.isSecure:
        contextFactory = ssl.ClientContextFactory()
    else:
        contextFactory = None

    connectWS(factory, contextFactory)
    reactor.run()
