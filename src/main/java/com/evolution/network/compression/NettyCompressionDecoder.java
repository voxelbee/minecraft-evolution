package com.evolution.network.compression;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import com.evomine.decode.BufferUtils;

public class NettyCompressionDecoder extends ByteToMessageDecoder
{
    private final Inflater inflater;
    private int threshold;

    public NettyCompressionDecoder(int thresholdIn)
    {
        this.threshold = thresholdIn;
        this.inflater = new Inflater();
    }

    protected void decode(ChannelHandlerContext p_decode_1_, ByteBuf p_decode_2_, List<Object> p_decode_3_) throws DataFormatException, Exception
    {
        if (p_decode_2_.readableBytes() != 0)
        {
            int i = BufferUtils.readVarIntFromBuffer(p_decode_2_);

            if (i == 0)
            {
                p_decode_3_.add(p_decode_2_.readBytes(p_decode_2_.readableBytes()));
            }
            else
            {
                if (i < this.threshold)
                {
                    throw new DecoderException("Badly compressed packet - size of " + i + " is below server threshold of " + this.threshold);
                }

                if (i > 2097152)
                {
                    throw new DecoderException("Badly compressed packet - size of " + i + " is larger than protocol maximum of " + 2097152);
                }

                byte[] abyte = new byte[p_decode_2_.readableBytes()];
                p_decode_2_.readBytes(abyte);
                this.inflater.setInput(abyte);
                byte[] abyte1 = new byte[i];
                this.inflater.inflate(abyte1);
                p_decode_3_.add(Unpooled.wrappedBuffer(abyte1));
                this.inflater.reset();
            }
        }
    }

    public void setCompressionThreshold(int thresholdIn)
    {
        this.threshold = thresholdIn;
    }
}
