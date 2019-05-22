package com.evolution.network;

import com.evomine.decode.BufferUtils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.MessageToByteEncoder;

@Sharable
public class NettyFrameEncoder extends MessageToByteEncoder<ByteBuf>
{
    protected void encode(ChannelHandlerContext p_encode_1_, ByteBuf p_encode_2_, ByteBuf p_encode_3_) throws Exception
    {
        int i = p_encode_2_.readableBytes();
        int j = BufferUtils.getVarIntSize(i);

        if (j > 3)
        {
            throw new IllegalArgumentException("unable to fit " + i + " into " + 3);
        }
        else
        {
        	p_encode_3_.ensureWritable(j + i);
        	BufferUtils.writeVarIntToBuffer(p_encode_3_, i);
        	p_encode_3_.writeBytes(p_encode_2_, p_encode_2_.readerIndex(), i);
        }
    }
}
