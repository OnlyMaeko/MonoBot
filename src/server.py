import asyncio
from aioquic.asyncio import serve, QuicConnectionProtocol
from aioquic.quic.configuration import QuicConfiguration
from aioquic.quic.events import StreamDataReceived

class QuicBridge(QuicConnectionProtocol):
    def quic_event_received(self, event):
        if isinstance(event, StreamDataReceived):
            asyncio.create_task(self.forward_to_java(event.data))

    async def forward_to_java(self, data):
        try:
            reader, writer = await asyncio.open_connection('127.0.0.1', 9090)
            writer.write(data)
            await writer.drain()
            writer.close()
            await writer.wait_closed()
        except Exception as e:
            print(f"Bridge Error: {e}")

async def main():
    config = QuicConfiguration(is_client=False, alpn_protocols=["bridge"])
    config.load_cert_chain('cert.pem', 'key.pem')
    await serve("0.0.0.0", 4433, configuration=config, create_protocol=QuicBridge)
    await asyncio.Future()

if __name__ == "__main__":
    asyncio.run(main())