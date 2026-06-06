import asyncio
from aioquic.asyncio import connect, QuicConnectionProtocol
from aioquic.quic.configuration import QuicConfiguration
from aioquic.quic.events import StreamDataReceived

class ClientBridge(QuicConnectionProtocol):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.tcp_writer = None

    def quic_event_received(self, event):
        if isinstance(event, StreamDataReceived):
            if self.tcp_writer:
                # When data comes back from the Server over QUIC, write it to the Java Client
                self.tcp_writer.write(event.data)
                asyncio.create_task(self.tcp_writer.drain())

async def handle_tcp_client(reader, writer, quic_protocol):
    quic_protocol.tcp_writer = writer # Save the writer so we can send data back
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