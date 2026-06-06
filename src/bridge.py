import asyncio
from aioquic.asyncio import QuicConnectionProtocol, serve, connect
from aioquic.quic.configuration import QuicConfiguration

# Server/Client bridge logic using native QUIC
# This acts as the pipe between QUIC to the sockets
async def bridge_udp_to_tcp(reader, writer):
    
    pass 


if __name__ == "__main__":
    print("QUIC Bridge Active")
    # TLS 1.3 handshake bridge between for the java client and server