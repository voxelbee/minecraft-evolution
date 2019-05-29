package com.evolution.network;

import java.io.IOException;
import java.util.List;

import com.evolution.main.Main;
import com.evolution.network.handler.NettyManager;
import com.evomine.decode.Packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class NettyDecoder extends ByteToMessageDecoder
{
  public NettyDecoder()
  {

  }

  @Override
  protected void decode( ChannelHandlerContext p_decode_1_, ByteBuf p_decode_2_, List< Object > p_decode_3_ )
      throws IOException, InstantiationException, IllegalAccessException, Exception
  {
    if ( p_decode_2_.readableBytes() != 0 )
    {
      EnumConnectionState state = ( p_decode_1_.channel().attr( NettyManager.PROTOCOL_ATTRIBUTE_KEY ).get() );

      Packet packet = null;
      try
      {
        packet = Main.PROTOCOL.decodeBuffer( p_decode_2_, state );
        packet.state = state;

        if ( p_decode_2_.readableBytes() > 0 )
        {
          throw new IOException( "Packet " + state + " (" + packet.name + ") was larger than I expected, found "
              + p_decode_2_.readableBytes() + " bytes extra whilst reading packet" );
        }
        else
        {
          p_decode_3_.add( packet );
        }
      }
      catch ( Exception e )
      {
        e.printStackTrace();
      }
    }
  }
}
