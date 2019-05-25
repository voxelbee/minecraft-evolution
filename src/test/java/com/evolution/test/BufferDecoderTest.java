package com.evolution.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import com.evolution.main.Main;
import com.evolution.network.EnumConnectionState;
import com.evomine.decode.Protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class BufferDecoderTest
{
  private static final Protocol PROTOCOL = Main.PROTOCOL;

  @Ignore( "WIP" )
  @Test
  public void testDecodeBuffers() throws IOException, URISyntaxException
  {
    ClassLoader classloader = Thread.currentThread().getContextClassLoader();

    for ( int i = 1; i < 2; i++ )
    {
      URI path = classloader.getResource( "buffers/buffer" + i ).toURI();
      if ( i < 2 )
      {
        decode( path, EnumConnectionState.LOGIN );
      }
      else
      {
        decode( path, EnumConnectionState.PLAY );
      }
    }
  }

  @Ignore( "WIP" )
  @Test
  public void testDecodeBuffer1() throws Exception
  {
    ClassLoader classloader = Thread.currentThread().getContextClassLoader();

    URI path = classloader.getResource( "buffers/buffer_1" ).toURI();
    Map< String, Object > result = decode( path, EnumConnectionState.LOGIN );
    assertEquals( 2, result.size() );
    assertEquals( "compress", result.get( "name" ) );
    assertEquals( Collections.singletonMap( "threshold", 0 ), result.get( "params" ) );
  }

  @Ignore( "WIP" )
  @Test
  public void testDecodeBuffer2() throws Exception
  {
    ClassLoader classloader = Thread.currentThread().getContextClassLoader();

    URI path = classloader.getResource( "buffers/buffer_7" ).toURI();
    Map< String, Object > result = decode( path, EnumConnectionState.PLAY );
    assertEquals( 2, result.size() );
    assertEquals( "compress", result.get( "name" ) );
    assertEquals( Collections.singletonMap( "threshold", 0 ), result.get( "params" ) );
  }

  @Ignore( "WIP" )
  @Test
  public void testDecodeBuffer3() throws Exception
  {
    ClassLoader classloader = Thread.currentThread().getContextClassLoader();

    URI path = classloader.getResource( "buffers/buffer3" ).toURI();
    Map< String, Object > result = decode( path, EnumConnectionState.LOGIN );
    assertEquals( 2, result.size() );
    assertEquals( "compress", result.get( "name" ) );
    assertEquals( Collections.singletonMap( "threshold", 0 ), result.get( "params" ) );
  }

  private Map< String, Object > decode( URI path, EnumConnectionState state ) throws IOException
  {
    byte[] fileContent = Files.readAllBytes( Paths.get( path ) );
    ByteBuf buf = Unpooled.wrappedBuffer( fileContent );

    Map< String, Object > vars = PROTOCOL.decodeBuffer( buf, state );
    System.out.println( vars );
    assertEquals( 0, buf.readableBytes() );
    return vars;
  }
}
