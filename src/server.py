import asyncio
from aioquic.asyncio import serve, QuicConnectionProtocol
from aioquic.quic.configuration import QuicConfiguration
from aioquic.quic.events import StreamDataReceived

# Read variables from config.properties so that nothing is hardcoded
config = {}
try:
    with open("config.properties", "r") as f:
        for line in f:
            if "=" in line and not line.startswith("#"):
                k, v = line.strip().split("=", 1)
                config[k.strip()] = v.strip()
except FileNotFoundError:
    pass

JAVA_SERVER_IP = config.get("server.host")
JAVA_SERVER_PORT = int(config.get("server.port"))
QUIC_SERVER_IP = config.get("quic.server.host")
QUIC_SERVER_PORT = int(config.get("quic.server.port"))

# Saves the write and the payload to be sent back to the server as well as connects to the QUIC bridge
# A history of the PDUs will be kept in the server window but not as a stateful memory (client as well but for the same reason it's not stateful just a result of not clearing the console), also in client.py
class QuicBridge(QuicConnectionProtocol):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.tcp_reader = None
        self.tcp_writer = None
        self.connected = False

    # Connects to java socket for the game state and messages as well as authentication
    async def connect_to_java(self):
        self.tcp_reader, self.tcp_writer = await asyncio.open_connection(JAVA_SERVER_IP, JAVA_SERVER_PORT)
        self.connected = True
        asyncio.create_task(self.read_from_java())

    # Sends the authentication code to java server
    async def read_from_java(self):
        try:
            while True:
                data = await self.tcp_reader.read(4096)
                if not data: break
                
                self._quic.send_stream_data(0, data)
                self.transmit()
        except Exception as e:
            pass

    def quic_event_received(self, event):
        if isinstance(event, StreamDataReceived):
            asyncio.create_task(self.forward_to_java(event.data))

    # Forwards client commands and writes the data to the java server to interact with Interface to bring it up in the client console (using the java sockets)
    async def forward_to_java(self, data):
        if not self.connected:
            await self.connect_to_java()
        try:
            
            self.tcp_writer.write(data)
            await self.tcp_writer.drain()
        except Exception as e:
            pass
# QUIC config that maps to bridge.py file to create the QUIC connection using asyncio and aioquic with the cert and key for the TLS 1.3, old comment, it now maps straight to client.py
# Bridge.py was just the one function so now its in here
async def main():
    quic_config = QuicConfiguration(is_client=False, alpn_protocols=["bridge"])
    quic_config.load_cert_chain('cert.pem', 'key.pem')
    await serve(QUIC_SERVER_IP, QUIC_SERVER_PORT, configuration=quic_config, create_protocol=QuicBridge)
    await asyncio.Future()

if __name__ == "__main__":
    asyncio.run(main())