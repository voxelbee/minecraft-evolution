package com.evolution.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.evolution.Main;
import com.evolution.network.EnumConnectionState;
import com.evomine.network.decode.Packet;
import com.evomine.network.decode.Protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class BufferEncoderTest
{
  private static final Protocol PROTOCOL = Main.PROTOCOL;

  @Test
  public void testEncodePositionPacket() throws Exception
  {
    Packet inPacket = new Packet( "position", EnumConnectionState.PLAY );
    inPacket.params.put( "x", (double) 10 );
    inPacket.params.put( "y", (double) 10 );
    inPacket.params.put( "z", (double) 10 );
    inPacket.params.put( "onGround", true );

    ByteBuf buf = Unpooled.directBuffer();
    PROTOCOL.encodeBuffer( buf, inPacket );

    Packet outPacket = PROTOCOL.decodeBufferTest( buf, EnumConnectionState.PLAY );
    assertEquals( 0, buf.readableBytes() );
    assertEquals( inPacket.params.get( "x" ), outPacket.params.get( "x" ) );
    assertEquals( inPacket.params.get( "y" ), outPacket.params.get( "y" ) );
    assertEquals( inPacket.params.get( "z" ), outPacket.params.get( "z" ) );
    assertEquals( inPacket.params.get( "onGround" ), outPacket.params.get( "onGround" ) );
  }
}
