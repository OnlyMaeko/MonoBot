import asyncio
from aioquic.asyncio import connect, QuicConnectionProtocol
from aioquic.quic.configuration import QuicConfiguration
from aioquic.quic.events import StreamDataReceived

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

async def run_client():
    config = QuicConfiguration(is_client=True, alpn_protocols=["bridge"])
    config.verify_mode = False 
    async with connect("127.0.0.1", 4433, configuration=config, create_protocol=ClientBridge) as quic:
        server = await asyncio.start_server(lambda r, w: handle_tcp_client(r, w, quic), '127.0.0.1', 8080)
        await server.serve_forever()

if __name__ == "__main__":
    asyncio.run(run_client())