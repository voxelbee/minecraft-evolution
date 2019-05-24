package com.evolution.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.IOException;
import java.util.List;

import com.evolution.main.Main;
import com.evolution.network.handler.NettyManager;
import com.evomine.decode.PacketLayout;
import com.evomine.decode.BufferUtils;

public class NettyDecoder extends ByteToMessageDecoder
{
    public NettyDecoder()
    {
    	
    }

    protected void decode(ChannelHandlerContext p_decode_1_, ByteBuf p_decode_2_, List<Object> p_decode_3_) throws IOException, InstantiationException, IllegalAccessException, Exception
    {
        if (p_decode_2_.readableBytes() != 0)
        {
            EnumConnectionState state = ((EnumConnectionState)p_decode_1_.channel().attr(NettyManager.PROTOCOL_ATTRIBUTE_KEY).get());
        	
        	int i = BufferUtils.readVarIntFromBuffer(p_decode_2_);
            //PacketLayout packet = Main.PROTOCOL.getPacketFromId(state, i).createReadWritePacket();
            //packet.readPacketData(p_decode_2_);
            //System.out.println(i + "  " + packet.name);

            if (p_decode_2_.readableBytes() > 0)
            {
            //    throw new IOException("Packet " + state.getId() + "/" + i + " (" + packet.name + ") was larger than I expected, found " + p_decode_2_.readableBytes() + " bytes extra whilst reading packet " + i);
            }
            else
            {
            //    p_decode_3_.add(packet);
            }
        }
    }
}
