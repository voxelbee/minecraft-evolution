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
import com.evomine.decode.Packet;
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
    Packet packet = PROTOCOL.decodeBuffer( buf, EnumConnectionState.STATUS );
    Map< String, Object > vars = packet.params;
    assertEquals( "server_info", packet.name );
    assertEquals(
        "{\"description\":{\"text\":\"A Minecraft Server\"},\"players\":{\"max\":20,\"online\":0},\"version\":{\"name\":\"1.12\",\"protocol\":335}}",
        vars.get( "response" ) );
    assertEquals( 0, buf.readableBytes() );
  }

  @Test
  public void testDecodeBuffer1() throws Exception
  {
    ByteBuf buf = get( "buffers/buffer_1" );
    Packet packet = PROTOCOL.decodeBuffer( buf, EnumConnectionState.STATUS );
    Map< String, Object > vars = packet.params;
    assertEquals( "ping", packet.name );
    assertEquals( 343890327l, vars.get( "time" ) );
    assertEquals( 0, buf.readableBytes() );
  }

  @Test
  public void testDecodeBuffer2() throws Exception
  {
    ByteBuf buf = get( "buffers/buffer_2" );
    Packet packet = PROTOCOL.decodeBuffer( buf, EnumConnectionState.LOGIN );
    Map< String, Object > vars = packet.params;
    assertEquals( "compress", packet.name );
    assertEquals( 1, vars.size() );
    assertEquals( 256, vars.get( "threshold" ) );
    assertEquals( 0, buf.readableBytes() );
  }

  @Test
  public void testDecodeBuffer3() throws Exception
  {
    ByteBuf buf = get( "buffers/buffer_3" );
    Packet packet = PROTOCOL.decodeBuffer( buf, EnumConnectionState.LOGIN );
    Map< String, Object > vars = packet.params;
    assertEquals( "success", packet.name );
    assertEquals( 2, vars.size() );
    assertEquals( "7beaed24-0a62-3f97-b968-4d6f3b3f19c7", vars.get( "uuid" ) );
    assertEquals( "Player15", vars.get( "username" ) );
    assertEquals( 0, buf.readableBytes() );
  }

  @Test
  public void testDecodeBuffer10() throws Exception
  {
    ByteBuf buf = get( "buffers/buffer_10" );
    Packet packet = PROTOCOL.decodeBuffer( buf, EnumConnectionState.PLAY );
    Map< String, Object > vars = packet.params;
    assertEquals( "unlock_recipes", packet.name );
    assertEquals( 5, vars.size() );
    assertEquals( 0, vars.get( "action" ) );
    assertEquals( false, vars.get( "craftingBookOpen" ) );
    assertEquals( false, vars.get( "filteringCraftable" ) );
    assertEquals( Collections.emptyList(), vars.get( "recipes1" ) );
    assertEquals( Collections.emptyList(), vars.get( "recipes2" ) );
    assertEquals( 0, buf.readableBytes() );
  }

  /**
   * Testing for ../ in switch
   *
   * @throws Exception
   */
  @Test
  public void testDecodeBackwardsSwitchRef() throws Exception
  {
    ByteBuf buf = get( "buffers/buffer_11" );
    Packet packet = PROTOCOL.decodeBuffer( buf, EnumConnectionState.PLAY );
    Map< String, Object > vars = packet.params;
    assertEquals( "player_info", packet.name );
    assertEquals( 2, vars.size() );
    assertEquals( 0, vars.get( "action" ) );
    List< Object > data = (List< Object >) vars.get( "data" );
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
    Packet packet = PROTOCOL.decodeBuffer( buf, EnumConnectionState.PLAY );
    Map< String, Object > vars = packet.params;
    assertEquals( 0, buf.readableBytes() );
    assertEquals( "map_chunk", packet.name );
    assertEquals( 6, vars.size() );
    assertEquals( -9, vars.get( "x" ) );
    assertEquals( 3, vars.get( "z" ) );
    assertEquals( 63, vars.get( "bitMap" ) );
    assertEquals( 37244, ( (byte[]) vars.get( "chunkData" ) ).length );
    assertEquals( Collections.emptyList(), vars.get( "blockEntities" ) );
  }

  @Test
  public void testDecodeNbt() throws Exception
  {
    ByteBuf buf = get( "buffers/buffer_21" );
    Packet packet = PROTOCOL.decodeBuffer( buf, EnumConnectionState.PLAY );
    Map< String, Object > vars = packet.params;
    assertEquals( 0, buf.readableBytes() );
    assertEquals( "map_chunk", packet.name );
    assertEquals( -9, vars.get( "x" ) );
    assertEquals( 11, vars.get( "z" ) );
    assertEquals( 31, vars.get( "bitMap" ) );
  }

  @Test
  public void testDecodeEntityMetadata() throws Exception
  {
    ByteBuf buf = get( "buffers/buffer_73" );
    Packet packet = PROTOCOL.decodeBuffer( buf, EnumConnectionState.PLAY );
    Map< String, Object > vars = packet.params;
    assertEquals( 0, buf.readableBytes() );
    assertEquals( "entity_metadata", packet.name );
    assertEquals( 2225, vars.get( "entityId" ) );
  }

  @Test
  public void testDecodeSwitchDefault() throws Exception
  {
    ByteBuf buf = get( "buffers/buffer_150" );
    Packet packet = PROTOCOL.decodeBuffer( buf, EnumConnectionState.PLAY );
    Map< String, Object > vars = packet.params;
    assertEquals( 0, buf.readableBytes() );
    assertEquals( "entity_equipment", packet.name );
    assertEquals( 124, vars.get( "entityId" ) );
  }

  @Test
  public void testDecodeVoidValue() throws Exception
  {
    ByteBuf buf = get( "buffers/buffer_603" );
    Packet packet = PROTOCOL.decodeBuffer( buf, EnumConnectionState.PLAY );
    Map< String, Object > vars = packet.params;
    assertEquals( 0, buf.readableBytes() );
    assertEquals( "window_items", packet.name );
    assertEquals( (short) 0, vars.get( "windowId" ) );
  }

  @Test
  public void testDecodeBitfield() throws Exception
  {
    ByteBuf buf = get( "buffers/buffer_599" );
    Packet packet = PROTOCOL.decodeBuffer( buf, EnumConnectionState.PLAY );
    Map< String, Object > vars = packet.params;
    assertEquals( 0, buf.readableBytes() );
    assertEquals( "spawn_position", packet.name );
    Map< String, Object > location = (Map< String, Object >) vars.get( "location" );
    assertEquals( 3, location.size() );
    assertEquals( -12l, location.get( "x" ) );
    assertEquals( 64l, location.get( "y" ) );
    assertEquals( 180l, location.get( "z" ) );
  }

  @Test
  public void testDecodeBuffer8073() throws Exception
  {
    ByteBuf buf = get( "buffers/buffer_8073" );
    Packet packet = PROTOCOL.decodeBuffer( buf, EnumConnectionState.PLAY );
    Map< String, Object > vars = packet.params;
    assertEquals( 0, buf.readableBytes() );
    assertEquals( "advancements", packet.name );
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
        Packet packet = PROTOCOL.decodeBuffer( buf, EnumConnectionState.PLAY );
        Map< String, Object > vars = packet.params;
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
