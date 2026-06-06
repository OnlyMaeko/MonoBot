import asyncio
from aioquic.asyncio import connect, QuicConnectionProtocol
from aioquic.quic.configuration import QuicConfiguration
from aioquic.quic.events import StreamDataReceived

# Read variables from config.properties so that nothing is hardcoded same as in server.py but ofc takes in client config settings
config = {}
try:
    with open("config.properties", "r") as f:
        for line in f:
            if "=" in line and not line.startswith("#"):
                k, v = line.strip().split("=", 1)
                config[k.strip()] = v.strip()
except FileNotFoundError:
    pass

QUIC_SERVER_IP = config.get("quic.client.host")
QUIC_SERVER_PORT = int(config.get("quic.server.port"))
JAVA_CLIENT_IP = config.get("client.host")
JAVA_CLIENT_PORT = int(config.get("client.port"))


# initializes the QUIC bridge and writes datastream to the socket
class ClientBridge(QuicConnectionProtocol):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.tcp_writer = None

# Writes datastream to the java client
    def quic_event_received(self, event):
        if isinstance(event, StreamDataReceived):
            if self.tcp_writer:
                self.tcp_writer.write(event.data)
                asyncio.create_task(self.tcp_writer.drain())

# Saves the write and the payload to be sent back to the client
# A history of the PDUs will be kept in the server window but not as a stateful memory (client as well but for the same reason it's not stateful just a result of not clearing the console) 
async def handle_tcp_client(reader, writer, quic_protocol):
    quic_protocol.tcp_writer = writer 
    stream_id = quic_protocol._quic.get_next_available_stream_id()
    while True:
        try:
            data = await reader.read(4096)
            if not data: 
                await asyncio.sleep(0.1)
                continue
            quic_protocol._quic.send_stream_data(stream_id, data)
            quic_protocol.transmit()
        except Exception:
            break
# Replace bridge method to here to write client messages to send to server instead of over bridge.py and runs the client server to connect to the server.py with the ports from the config file
async def run_client():
    quic_config = QuicConfiguration(is_client=True, alpn_protocols=["bridge"])
    quic_config.verify_mode = False 
    async with connect(QUIC_SERVER_IP, QUIC_SERVER_PORT, configuration=quic_config, create_protocol=ClientBridge) as quic:
        server = await asyncio.start_server(lambda r, w: handle_tcp_client(r, w, quic), JAVA_CLIENT_IP, JAVA_CLIENT_PORT)
        await server.serve_forever()

if __name__ == "__main__":
    asyncio.run(run_client())