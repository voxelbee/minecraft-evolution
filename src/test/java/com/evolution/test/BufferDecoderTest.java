package com.evolution.test;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.evolution.main.Main;
import com.evolution.network.EnumConnectionState;
import com.evomine.decode.Protocol;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class BufferDecoderTest
{
  private static final Protocol PROTOCOL = Main.PROTOCOL;

  private static class ExpectedAndBuffer
  {
    final String className;
    final JsonObject expected;
    final ByteBuf buffer;
    ExpectedAndBuffer( String className, JsonObject expected, ByteBuf buffer)
    {
      this.className = checkNotNull(className);
      this.expected = checkNotNull( expected);
      this.buffer = checkNotNull(buffer);
    }
  }

  @Test
  public void testDecodeBuffer0() throws Exception
  {
    ExpectedAndBuffer values = get( "buffers/buffer_0");
    assertEquals("net.minecraft.network.status.server.SPacketServerInfo", values.className );
    Map< String, Object > vars = PROTOCOL.decodeBuffer( values.buffer, getConnectionState(values.className) );
    assertEquals( 2, vars.size() );
    assertEquals( "server_info", vars.get( "name" ));
    Map< String, Object > params = (Map< String, Object >) vars.get( "params" );
    assertEquals( 1, params.size() );
    assertEquals( "{\"description\":{\"text\":\"A Minecraft Server\"},\"players\":{\"max\":20,\"online\":0},\"version\":{\"name\":\"1.12\",\"protocol\":335}}",
        params.get( "response" ));
    assertEquals( 0, values.buffer.readableBytes() );
  }

  @Test
  public void testDecodeBuffer1() throws Exception
  {
    ExpectedAndBuffer values = get( "buffers/buffer_1");
    assertEquals("net.minecraft.network.status.server.SPacketPong", values.className );
    Map< String, Object > vars = PROTOCOL.decodeBuffer( values.buffer, getConnectionState(values.className)  );
    assertEquals( 2, vars.size() );
    assertEquals( "ping", vars.get( "name" ));
    Map< String, Object > params = (Map< String, Object >) vars.get( "params" );
    assertEquals( 1, params.size() );
    assertEquals( 343890327l, params.get( "time" ));
    assertEquals( 0, values.buffer.readableBytes() );
  }

  @Test
  public void testDecodeBuffer2() throws Exception
  {
    ExpectedAndBuffer values = get( "buffers/buffer_2");
    assertEquals("net.minecraft.network.login.server.SPacketEnableCompression", values.className );
    Map< String, Object > vars = PROTOCOL.decodeBuffer( values.buffer, getConnectionState(values.className)  );
    assertEquals( 2, vars.size() );
    assertEquals( "compress", vars.get( "name" ));
    Map< String, Object > params = (Map< String, Object >) vars.get( "params" );
    assertEquals( 1, params.size() );
    assertEquals( 256, params.get( "threshold" ));
    assertEquals( 0, values.buffer.readableBytes() );
  }

  @Test
  public void testDecodeBuffer3() throws Exception
  {
    ExpectedAndBuffer values = get( "buffers/buffer_3");
    Map< String, Object > vars = PROTOCOL.decodeBuffer( values.buffer, getConnectionState(values.className)  );
    assertEquals( 2, vars.size() );
    assertEquals( "success", vars.get( "name" ));
    Map< String, Object > params = (Map< String, Object >) vars.get( "params" );
    assertEquals( 2, params.size() );
    assertEquals( "7beaed24-0a62-3f97-b968-4d6f3b3f19c7", params.get( "uuid" ));
    assertEquals( "Player15", params.get( "username" ));
    assertEquals( 0, values.buffer.readableBytes() );
  }

  private ExpectedAndBuffer get(final String filename ) throws IOException, URISyntaxException
  {
    ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    URI path = classloader.getResource( filename ).toURI();
    byte[] fileContent = Files.readAllBytes( Paths.get( path ) );
    ByteBuf buf = Unpooled.wrappedBuffer( fileContent );

    final String expectedFilename = filename + "_decode";
    List<String> lines = Files.readAllLines( Paths.get( classloader.getResource( expectedFilename ).toURI() ), StandardCharsets.UTF_8 );
    JsonObject jsonObject = new JsonParser().parse(lines.get( 1 )).getAsJsonObject();

    return new ExpectedAndBuffer( lines.get( 0 ), jsonObject, buf);
  }

  private static EnumConnectionState getConnectionState( final String className )
  {
    if ( className.contains( "login" ))
    {
      return EnumConnectionState.LOGIN;
    }
    else if ( className.contains( "status" ))
    {
      return EnumConnectionState.STATUS;
    }
    else
    {
      throw new UnsupportedOperationException("Not found: " + className);
    }
  }
}
