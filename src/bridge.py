import asyncio
from aioquic.asyncio import QuicConnectionProtocol, serve, connect
from aioquic.quic.configuration import QuicConfiguration

# Server/Client bridge logic using native QUIC
# This is the pipe between QUIC to the sockets with TLS in the server.py file, this is leftover from an earlier iteration and neither complete or used in current version, please disregard
async def bridge_udp_to_tcp(reader, writer):
    
    pass 


if __name__ == "__main__":
    print("QUIC Bridge Active")
    