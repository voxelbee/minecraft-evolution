package com.evolution.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.IOException;

import com.evolution.network.handler.NettyManager;
import com.evomine.decode.PacketLayout;

import come.evolution.main.Main;

import com.evomine.decode.BufferUtils;

public class NettyEncoder extends MessageToByteEncoder<PacketLayout>
{
    public NettyEncoder()
    {
        
    }

    protected void encode(ChannelHandlerContext p_encode_1_, PacketLayout p_encode_2_, ByteBuf p_encode_3_) throws IOException, Exception
    {
        EnumConnectionState enumconnectionstate = (EnumConnectionState)p_encode_1_.channel().attr(NettyManager.PROTOCOL_ATTRIBUTE_KEY).get();

        if (enumconnectionstate == null)
        {
            throw new RuntimeException("ConnectionProtocol unknown: " + p_encode_2_.toString());
        }
        else
        {
            Integer integer = p_encode_2_.id;

	    	BufferUtils.writeVarIntToBuffer(p_encode_3_, integer.intValue());
	
	        try
	        {
	            p_encode_2_.writePacketData(p_encode_3_);
	        }
	        catch (Throwable throwable)
	        {
	        	throwable.printStackTrace(System.out);
	        }
        }
    }
}
