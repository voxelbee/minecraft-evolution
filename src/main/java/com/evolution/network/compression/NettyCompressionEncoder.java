package com.evolution.network.compression;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.util.zip.Deflater;

import com.evomine.network.decode.BufferUtils;

public class NettyCompressionEncoder extends MessageToByteEncoder<ByteBuf>
{
    private final byte[] buffer = new byte[8192];
    private final Deflater deflater;
    private int threshold;

    public NettyCompressionEncoder(int thresholdIn)
    {
        this.threshold = thresholdIn;
        this.deflater = new Deflater();
    }

    protected void encode(ChannelHandlerContext p_encode_1_, ByteBuf p_encode_2_, ByteBuf p_encode_3_) throws Exception
    {
        int i = p_encode_2_.readableBytes();

        if (i < this.threshold)
        {
        	BufferUtils.writeVarIntToBuffer(p_encode_3_, 0);
        	p_encode_3_.writeBytes(p_encode_2_);
        }
        else
        {
            byte[] abyte = new byte[i];
            p_encode_2_.readBytes(abyte);
            BufferUtils.writeVarIntToBuffer(p_encode_3_, abyte.length);
            this.deflater.setInput(abyte, 0, i);
            this.deflater.finish();

            while (!this.deflater.finished())
            {
                int j = this.deflater.deflate(this.buffer);
                p_encode_3_.writeBytes(this.buffer, 0, j);
            }

            this.deflater.reset();
        }
    }

    public void setCompressionThreshold(int thresholdIn)
    {
        this.threshold = thresholdIn;
    }
}
