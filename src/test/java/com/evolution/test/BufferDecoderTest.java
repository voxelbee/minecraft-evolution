package com.evolution.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;

import com.evolution.main.Main;
import com.evolution.network.EnumConnectionState;
import com.evomine.decode.Protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class BufferDecoderTest
{
  private static final Protocol PROTOCOL = Main.PROTOCOL;

  @Test
  public void testDecodeBuffer0() throws Exception
  {
    ByteBuf buf = get( "buffers/buffer_0" );
    Map< String, Object > vars = PROTOCOL.decodeBuffer( buf, EnumConnectionState.STATUS );
    assertEquals( 2, vars.size() );
    assertEquals( "server_info", vars.get( "name" ) );
    Map< String, Object > params = (Map< String, Object >) vars.get( "params" );
    assertEquals( 1, params.size() );
    assertEquals(
        "{\"description\":{\"text\":\"A Minecraft Server\"},\"players\":{\"max\":20,\"online\":0},\"version\":{\"name\":\"1.12\",\"protocol\":335}}",
        params.get( "response" ) );
    assertEquals( 0, buf.readableBytes() );
  }

  @Test
  public void testDecodeBuffer1() throws Exception
  {
    ByteBuf buf = get( "buffers/buffer_1" );
    Map< String, Object > vars = PROTOCOL.decodeBuffer( buf, EnumConnectionState.STATUS );
    assertEquals( 2, vars.size() );
    assertEquals( "ping", vars.get( "name" ) );
    Map< String, Object > params = (Map< String, Object >) vars.get( "params" );
    assertEquals( 1, params.size() );
    assertEquals( 343890327l, params.get( "time" ) );
    assertEquals( 0, buf.readableBytes() );
  }

  @Test
  public void testDecodeBuffer2() throws Exception
  {
    ByteBuf buf = get( "buffers/buffer_2" );
    Map< String, Object > vars = PROTOCOL.decodeBuffer( buf, EnumConnectionState.LOGIN );
    assertEquals( 2, vars.size() );
    assertEquals( "compress", vars.get( "name" ) );
    Map< String, Object > params = (Map< String, Object >) vars.get( "params" );
    assertEquals( 1, params.size() );
    assertEquals( 256, params.get( "threshold" ) );
    assertEquals( 0, buf.readableBytes() );
  }

  @Test
  public void testDecodeBuffer3() throws Exception
  {
    ByteBuf buf = get( "buffers/buffer_3" );
    Map< String, Object > vars = PROTOCOL.decodeBuffer( buf, EnumConnectionState.LOGIN );
    assertEquals( 2, vars.size() );
    assertEquals( "success", vars.get( "name" ) );
    Map< String, Object > params = (Map< String, Object >) vars.get( "params" );
    assertEquals( 2, params.size() );
    assertEquals( "7beaed24-0a62-3f97-b968-4d6f3b3f19c7", params.get( "uuid" ) );
    assertEquals( "Player15", params.get( "username" ) );
    assertEquals( 0, buf.readableBytes() );
  }

  @Test
  public void testDecodeBuffer10() throws Exception
  {
    ByteBuf buf = get( "buffers/buffer_10" );
    Map< String, Object > vars = PROTOCOL.decodeBuffer( buf, EnumConnectionState.PLAY );
    assertEquals( 2, vars.size() );
    assertEquals( "unlock_recipes", vars.get( "name" ) );
    Map< String, Object > params = (Map< String, Object >) vars.get( "params" );
    assertEquals( 5, params.size() );
    assertEquals( 0, params.get( "action" ) );
    assertEquals( false, params.get( "craftingBookOpen" ) );
    assertEquals( false, params.get( "filteringCraftable" ) );
    assertEquals( Collections.emptyList(), params.get( "recipes1" ) );
    assertEquals( Collections.emptyList(), params.get( "recipes2" ) );
    assertEquals( 0, buf.readableBytes() );
  }

  @Test
  public void testDecodeBuffer11() throws Exception
  {
    ByteBuf buf = get( "buffers/buffer_11" );
    Map< String, Object > vars = PROTOCOL.decodeBuffer( buf, EnumConnectionState.PLAY );
    assertEquals( 2, vars.size() );
    assertEquals( "player_info", vars.get( "name" ) );
    Map< String, Object > params = (Map< String, Object >) vars.get( "params" );
    assertEquals( 2, params.size() );
    assertEquals( 0, params.get( "action" ) );
    List< Object > data = (List< Object >) params.get( "data" );
    assertEquals( 1, data.size() );
    Map< String, Object > playerInfo = (Map< String, Object >) data.get( 0 );
    assertEquals( 6, playerInfo.size() );
    assertEquals( UUID.fromString( "7beaed24-0a62-3f97-b968-4d6f3b3f19c7" ), playerInfo.get( "UUID" ) );
    assertEquals( "Player15", playerInfo.get( "name" ) );
    assertEquals( Collections.emptyList(), playerInfo.get( "properties" ) );
    assertEquals( 0, playerInfo.get( "gamemode" ) );
    assertEquals( 0, playerInfo.get( "ping" ) );
    assertEquals( "void", playerInfo.get( "displayName" ) );
    assertEquals( 0, buf.readableBytes() );
  }

  @Test
  public void testDecodeBuffer13() throws Exception
  {
    ByteBuf buf = get( "buffers/buffer_13" );
    Map< String, Object > vars = PROTOCOL.decodeBuffer( buf, EnumConnectionState.PLAY );
    assertEquals( 2, vars.size() );
    assertEquals( 0, buf.readableBytes() );
    assertEquals( "map_chunk", vars.get( "name" ) );
    Map< String, Object > params = (Map< String, Object >) vars.get( "params" );
    assertEquals( 6, params.size() );
    assertEquals( -9, params.get( "x" ) );
    assertEquals( 3, params.get( "z" ) );
    assertEquals( 63, params.get( "bitMap" ) );
    assertEquals( 37244, ( (byte[]) params.get( "chunkData" ) ).length );
    assertEquals( Collections.emptyList(), params.get( "blockEntities" ) );
  }

  @Test
  public void testDecodeNbt() throws Exception
  {
    ByteBuf buf = get( "buffers/buffer_21" );
    Map< String, Object > vars = PROTOCOL.decodeBuffer( buf, EnumConnectionState.PLAY );
    assertEquals( 2, vars.size() );
    assertEquals( 0, buf.readableBytes() );
    assertEquals( "map_chunk", vars.get( "name" ) );
    Map< String, Object > params = (Map< String, Object >) vars.get( "params" );
    assertEquals( -9, params.get( "x" ) );
    assertEquals( 11, params.get( "z" ) );
    assertEquals( 31, params.get( "bitMap" ) );
  }

  @Test
  public void testDecodeEntityMetadata() throws Exception
  {
    ByteBuf buf = get( "buffers/buffer_73" );
    Map< String, Object > vars = PROTOCOL.decodeBuffer( buf, EnumConnectionState.PLAY );
    assertEquals( 2, vars.size() );
    assertEquals( 0, buf.readableBytes() );
    assertEquals( "entity_metadata", vars.get( "name" ) );
    Map< String, Object > params = (Map< String, Object >) vars.get( "params" );
    assertEquals( 2225, params.get( "entityId" ) );
  }

  @Test
  public void testDecodeSwitchDefault() throws Exception
  {
    ByteBuf buf = get( "buffers/buffer_150" );
    Map< String, Object > vars = PROTOCOL.decodeBuffer( buf, EnumConnectionState.PLAY );
    assertEquals( 2, vars.size() );
    assertEquals( 0, buf.readableBytes() );
    assertEquals( "entity_equipment", vars.get( "name" ) );
    Map< String, Object > params = (Map< String, Object >) vars.get( "params" );
    assertEquals( 124, params.get( "entityId" ) );
  }

  @Test
  public void testDecodeVoidValue() throws Exception
  {
    ByteBuf buf = get( "buffers/buffer_603" );
    Map< String, Object > vars = PROTOCOL.decodeBuffer( buf, EnumConnectionState.PLAY );
    assertEquals( 2, vars.size() );
    assertEquals( 0, buf.readableBytes() );
    assertEquals( "window_items", vars.get( "name" ) );
    Map< String, Object > params = (Map< String, Object >) vars.get( "params" );
    assertEquals( (short) 0, params.get( "windowId" ) );
  }

  @Test
  public void testDecodeBitfield() throws Exception
  {
    ByteBuf buf = get( "buffers/buffer_599" );
    Map< String, Object > vars = PROTOCOL.decodeBuffer( buf, EnumConnectionState.PLAY );
    assertEquals( 2, vars.size() );
    assertEquals( 0, buf.readableBytes() );
    assertEquals( "spawn_position", vars.get( "name" ) );
    Map< String, Object > params = (Map< String, Object >) vars.get( "params" );
  }

  // @Test
  public void testDecodeBuffer8073() throws Exception
  {
    ByteBuf buf = get( "buffers/buffer_8073" );
    Map< String, Object > vars = PROTOCOL.decodeBuffer( buf, EnumConnectionState.PLAY );
    assertEquals( 2, vars.size() );
    assertEquals( 0, buf.readableBytes() );
    assertEquals( "spawn_position", vars.get( "name" ) );
    Map< String, Object > params = (Map< String, Object >) vars.get( "params" );
  }

  // @Test
  public void testDecodeAll() throws Exception
  {
    for ( int i = 5; i < 10000; i++ )
    {
      byte[] fileContent = Files.readAllBytes( ( new File( "C:/tmp/buffers/buffer_" + i ) ).toPath() );
      ByteBuf buf = Unpooled.wrappedBuffer( fileContent );

      try
      {
        Map< String, Object > vars = PROTOCOL.decodeBuffer( buf, EnumConnectionState.PLAY );
      }
      catch ( Exception e )
      {
        System.out.println( i + " : " + e.getMessage() );
      }
    }
  }

  private ByteBuf get( final String filename ) throws IOException, URISyntaxException
  {
    ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    URI path = classloader.getResource( filename ).toURI();
    byte[] fileContent = Files.readAllBytes( Paths.get( path ) );
    ByteBuf buf = Unpooled.wrappedBuffer( fileContent );

    // final String expectedFilename = filename + "_decode";
    // List< String > lines = Files.readAllLines( Paths.get( classloader.getResource( expectedFilename ).toURI() ),
    // StandardCharsets.UTF_8 );
    // JsonObject jsonObject = new JsonParser().parse( lines.get( 1 ) ).getAsJsonObject();

    return buf;
  }

  private static EnumConnectionState getConnectionState( final String className )
  {
    if ( className.contains( "login" ) )
    {
      return EnumConnectionState.LOGIN;
    }
    else if ( className.contains( "status" ) )
    {
      return EnumConnectionState.STATUS;
    }
    else if ( className.contains( "play" ) )
    {
      return EnumConnectionState.PLAY;
    }
    else
    {
      throw new UnsupportedOperationException( "Not found: " + className );
    }
  }
}
