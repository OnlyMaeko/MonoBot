import asyncio
from aioquic.asyncio import serve, QuicConnectionProtocol
from aioquic.quic.configuration import QuicConfiguration
from aioquic.quic.events import StreamDataReceived

class QuicBridge(QuicConnectionProtocol):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.tcp_reader = None
        self.tcp_writer = None
        self.connected = False

    async def connect_to_java(self):
        # Connect to Java Server and keep the connection ALIVE
        self.tcp_reader, self.tcp_writer = await asyncio.open_connection('127.0.0.1', 9090)
        self.connected = True
        # Start a background loop to constantly read Java's responses and send them back
        asyncio.create_task(self.read_from_java())

    async def read_from_java(self):
        try:
            while True:
                data = await self.tcp_reader.read(4096)
                if not data: break
                # Send the ACK/AUTH responses back to the Client over QUIC
                self._quic.send_stream_data(0, data)
                self.transmit()
        except Exception as e:
            pass

    def quic_event_received(self, event):
        if isinstance(event, StreamDataReceived):
            asyncio.create_task(self.forward_to_java(event.data))

    async def forward_to_java(self, data):
        if not self.connected:
            await self.connect_to_java()
        try:
            # Send the Client's commands to the Java Server
            self.tcp_writer.write(data)
            await self.tcp_writer.drain()
        except Exception as e:
            pass

async def main():
    config = QuicConfiguration(is_client=False, alpn_protocols=["bridge"])
    config.load_cert_chain('cert.pem', 'key.pem')
    await serve("0.0.0.0", 4433, configuration=config, create_protocol=QuicBridge)
    await asyncio.Future()

if __name__ == "__main__":
    asyncio.run(main())