import asyncio
from aioquic.asyncio import connect, QuicConnectionProtocol
from aioquic.quic.configuration import QuicConfiguration

class ClientBridge(QuicConnectionProtocol):
    pass

async def handle_tcp_client(reader, writer, quic_protocol):
    stream_id = quic_protocol._quic.get_next_available_stream_id()
    while True:
        data = await reader.read(4096)
        if not data: break
        quic_protocol._quic.send_stream_data(stream_id, data)
        quic_protocol.transmit()

async def run_client():
    config = QuicConfiguration(is_client=True, alpn_protocols=["bridge"])
    config.verify_mode = False 
    async with connect("127.0.0.1", 4433, configuration=config, create_protocol=ClientBridge) as quic:
        server = await asyncio.start_server(lambda r, w: handle_tcp_client(r, w, quic), '127.0.0.1', 8080)
        await server.serve_forever()

if __name__ == "__main__":
    asyncio.run(run_client())