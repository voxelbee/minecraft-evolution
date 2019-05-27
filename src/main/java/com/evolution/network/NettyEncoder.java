package com.evolution.network;

import java.io.IOException;

import com.evolution.main.Main;
import com.evolution.network.handler.NettyManager;
import com.evomine.decode.Packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class NettyEncoder extends MessageToByteEncoder< Packet >
{
  public NettyEncoder()
  {

  }

  @Override
  protected void encode( ChannelHandlerContext p_encode_1_, Packet p_encode_2_, ByteBuf p_encode_3_ ) throws IOException, Exception
  {
    EnumConnectionState enumconnectionstate = p_encode_1_.channel().attr( NettyManager.PROTOCOL_ATTRIBUTE_KEY ).get();

    if ( enumconnectionstate == null )
    {
      throw new RuntimeException( "ConnectionProtocol unknown: " + p_encode_2_.toString() );
    }
    else
    {
      try
      {
        Main.PROTOCOL.encodeBuffer( p_encode_3_, p_encode_2_ );
      }
      catch ( Throwable throwable )
      {
        throwable.printStackTrace( System.out );
      }
    }
  }
}
