package com.evomine.decode;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import com.evomine.network.decode.PacketDeserializer;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class BitFieldTest
{
  @Test
  public void testBitfieldLongSigned()
  {
    final String jsonLine =
        "[{\"name\":\"z\",\"size\":26,\"signed\":true},"
            + "{\"name\":\"y\",\"size\":12,\"signed\":true},"
            + "{\"name\":\"x\",\"size\":26,\"signed\":true}]";
    final JsonArray bitfield = new JsonParser().parse( jsonLine ).getAsJsonArray();
    ByteBuf buf = Unpooled.copiedBuffer( new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 } );
    Object result = PacketDeserializer.processBitfield( bitfield, buf );
    Map< String, Long > expected = new LinkedHashMap< String, Long >();
    expected.put( "x", 17172232l );
    expected.put( "y", 257l );
    expected.put( "z", 264204l );
    assertEquals( expected, result );
  }

  @Test
  public void testBitfieldIntUnsigned1()
  {
    final String jsonLine =
        "[{\"name\":\"d\",\"size\":3,\"signed\":false},"
            + "{\"name\":\"c\",\"size\":26,\"signed\":true},\n"
            + "{\"name\":\"b\",\"size\":2,\"signed\":false},\n"
            + "{\"name\":\"a\",\"size\":1,\"signed\":false}]";
    ByteBuffer bbuf = ByteBuffer.allocate( 4 );
    final int bits = Integer.parseInt( "01101000100010001000100100000111", 2 );
    bbuf.putInt( bits );
    bbuf.clear();

    final JsonArray bitfield = new JsonParser().parse( jsonLine ).getAsJsonArray();
    ByteBuf buf = Unpooled.copiedBuffer( bbuf );
    Object result = PacketDeserializer.processBitfield( bitfield, buf );
    Map< String, Long > expected = new LinkedHashMap< String, Long >();
    expected.put( "a", 1l );
    expected.put( "b", 3l );
    expected.put( "c", 17895712l );
    expected.put( "d", 3l );
    assertEquals( expected, result );
  }

  @Test
  public void testBitfieldIntUnsigned2()
  {
    final String jsonLine =
        "[{\"name\":\"b\",\"size\":31,\"signed\":false},"
            + "{\"name\":\"a\",\"size\":1,\"signed\":false}]";
    ByteBuffer bbuf = ByteBuffer.allocate( 4 );
    final int bits = Integer.parseInt( "011", 2 );
    bbuf.putInt( bits );
    bbuf.clear();

    final JsonArray bitfield = new JsonParser().parse( jsonLine ).getAsJsonArray();
    ByteBuf buf = Unpooled.copiedBuffer( bbuf );
    Object result = PacketDeserializer.processBitfield( bitfield, buf );
    Map< String, Long > expected = new LinkedHashMap< String, Long >();
    expected.put( "a", 1l );
    expected.put( "b", 1l );
    assertEquals( expected, result );
  }

  @Test
  public void testBitfieldIntSigned2()
  {
    final String jsonLine =
        "[{\"name\":\"b\",\"size\":30,\"signed\":false},"
            + "{\"name\":\"a\",\"size\":2,\"signed\":true}]";
    final JsonArray bitfield = new JsonParser().parse( jsonLine ).getAsJsonArray();
    ByteBuffer bbuf = ByteBuffer.allocate( 4 );

    {
      final int bits = Integer.parseInt( "111", 2 );
      bbuf.putInt( bits );
      bbuf.clear();
      ByteBuf buf = Unpooled.copiedBuffer( bbuf );
      Object result = PacketDeserializer.processBitfield( bitfield, buf );
      Map< String, Long > expected = new LinkedHashMap< String, Long >();
      expected.put( "a", -1l );
      expected.put( "b", 1l );
      assertEquals( expected, result );
    }

    {
      bbuf.clear();
      final int bits = Integer.parseInt( "100", 2 );
      bbuf.putInt( bits );
      bbuf.clear();
      ByteBuf buf = Unpooled.copiedBuffer( bbuf );
      Object result = PacketDeserializer.processBitfield( bitfield, buf );
      Map< String, Long > expected = new LinkedHashMap< String, Long >();
      expected.put( "a", 0l );
      expected.put( "b", 1l );
      assertEquals( expected, result );
    }

    {
      bbuf.clear();
      final int bits = Integer.parseInt( "101", 2 );
      bbuf.putInt( bits );
      bbuf.clear();
      ByteBuf buf = Unpooled.copiedBuffer( bbuf );
      Object result = PacketDeserializer.processBitfield( bitfield, buf );
      Map< String, Long > expected = new LinkedHashMap< String, Long >();
      expected.put( "a", 1l );
      expected.put( "b", 1l );
      assertEquals( expected, result );
    }
  }

}
