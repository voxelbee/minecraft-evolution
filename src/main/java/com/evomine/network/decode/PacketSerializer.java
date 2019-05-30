package com.evomine.network.decode;

import static com.google.common.base.Preconditions.checkNotNull;

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

    JsonObject idMap = json.getAsJsonObject()
        .getAsJsonObject( packet.state.getAsString() )
        .getAsJsonObject( EnumPacketDirection.SERVERBOUND.getAsString() )
        .getAsJsonObject( "types" )
        .getAsJsonArray( "packet" ).get( 1 ).getAsJsonArray().get( 0 ).getAsJsonObject()
        .getAsJsonArray( "type" ).get( 1 ).getAsJsonObject()
        .getAsJsonObject( "mappings" );

    int id = 0;
    for ( String key : idMap.keySet() )
    {
      if ( idMap.get( key ).getAsString().equals( packet.name ) )
      {
        id = Integer.decode( key );
      }
    }
    BufferUtils.writeVarIntToBuffer( buf, id );
    objectSerialize( packetArray, packet, buf );
  }

  private static void objectSerialize( final JsonElement object, final Packet packet, ByteBuf buf ) throws JsonParseException
  {
    if ( object.isJsonArray() )
    {
      JsonElement classObject = object.getAsJsonArray().get( 1 );
      String classType = object.getAsJsonArray().get( 0 ).getAsString();
      if ( classType.equals( "container" ) )
      {
        processContainer( classObject.getAsJsonArray(), packet, buf );
      }
      else
      {
        throw new UnsupportedOperationException( "PacketSerializer: Unknown class " + classType );
      }
    }
    else
    {
      throw new UnsupportedOperationException( "PacketSerializer: No other types implemented " + object );
    }
  }

  private static void processContainer( final JsonArray object, final Packet packet, ByteBuf buf )
  {
    for ( int i = 0; i < object.size(); i++ )
    {
      String dataType = object.get( i ).getAsJsonObject().get( "type" ).getAsString();
      String varName = object.get( i ).getAsJsonObject().get( "name" ).getAsString();
      Object value = packet.params.get( varName );
      checkNotNull( value, "params" );
      writeNative( dataType, value, buf );
    }
  }

  private static void writeNative( final String type, final Object value, ByteBuf buf )
  {
    if ( type.equals( "varint" ) )
    {
      BufferUtils.writeVarIntToBuffer( buf, (int) value );
    }
    else if ( type.equals( "string" ) )
    {
      BufferUtils.writeString( buf, (String) value );
    }
    else if ( type.equals( "i16" ) )
    {
      buf.writeShort( (short) value );
    }
    else if ( type.equals( "u16" ) )
    {
      buf.writeShort( (short) value );
    }
    else if ( type.equals( "f64" ) )
    {
      buf.writeDouble( (double) value );
    }
    else if ( type.equals( "bool" ) )
    {
      buf.writeBoolean( (boolean) value );
    }
    else if ( type.equals( "i8" ) )
    {
      buf.writeByte( (byte) value );
    }
    else if ( type.equals( "u8" ) )
    {
      buf.writeByte( (byte) value );
    }
    else if ( type.equals( "f32" ) )
    {
      buf.writeFloat( (float) value );
    }
    else if ( type.equals( "restBuffer" ) )
    {
      BufferUtils.writeVarIntToBuffer( buf, ( (byte[]) value ).length );
      buf.writeBytes( (byte[]) value );
    }
    else
    {
      throw new UnsupportedOperationException( "PacketSerializer: Unknown native type " + type );
    }
  }
}
