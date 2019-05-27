package com.evomine.decode;

import com.evolution.network.EnumPacketDirection;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import io.netty.buffer.ByteBuf;

public class PacketSerializer
{
  public static void packetSerialize( final JsonElement json,
      ByteBuf buf,
      final Packet packet )
  {
    JsonArray packetArray = json.getAsJsonObject()
        .getAsJsonObject( packet.state.getAsString() )
        .getAsJsonObject( EnumPacketDirection.SERVERBOUND.getAsString() )
        .getAsJsonObject( "types" ).getAsJsonArray( "packet_" + packet.name );
    System.out.println( packetArray );
  }

  private static void objectSerialize( final JsonElement object,
      final JsonObject packetTypes,
      final ByteBuf buf ) throws JsonParseException
  {

  }
}
