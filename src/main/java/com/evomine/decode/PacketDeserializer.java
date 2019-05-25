package com.evomine.decode;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.LinkedHashMap;
import java.util.Map;

import com.evolution.main.Main;
import com.evolution.network.EnumConnectionState;
import com.evolution.network.EnumPacketDirection;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import io.netty.buffer.ByteBuf;

public class PacketDeserializer
{

  public static void deserialize( final JsonElement json,
      final Map< String, Object > vars,
      final ByteBuf buf,
      final EnumConnectionState state ) throws JsonParseException
  {
    packetDeserialize( json.getAsJsonObject()
        .getAsJsonObject( state.getAsString() )
        .getAsJsonObject( EnumPacketDirection.CLIENTBOUND.getAsString() )
        .getAsJsonObject( "types" )
        .get( "packet" ),
        json.getAsJsonObject()
        .getAsJsonObject( state.getAsString() )
        .getAsJsonObject( EnumPacketDirection.CLIENTBOUND.getAsString() )
        .getAsJsonObject( "types" ),
        vars, null, buf );
  }

  private static void packetDeserialize( final JsonElement json,
      final JsonObject all,
      final Map< String, Object > vars,
      final String varName,
      final ByteBuf buf ) throws JsonParseException
  {
    checkNotNull( vars, "vars" );
    if ( json.isJsonArray() )
    {
      JsonArray array = json.getAsJsonArray();
      String objectType = array.get( 0 ).getAsString();
      if ( objectType.equals( "container" ) )
      {
        for ( JsonElement element : array.get( 1 ).getAsJsonArray() )
        {
          packetDeserialize( element, all, vars, varName, buf );
        }
      }
      else if ( objectType.equals( "mapper" ) )
      {
        String value = processMapper( json, buf );
        vars.put( varName, value );
      }
      else if ( objectType.equals( "switch" ) )
      {
        String varNameToCompare = json.getAsJsonArray().get( 1 ).getAsJsonObject().get( "compareTo" ).getAsString();
        String key = String.valueOf( vars.get( varNameToCompare ) );
        JsonElement element = json.getAsJsonArray().get( 1 ).getAsJsonObject().get( "fields" ).getAsJsonObject().get( key );
        packetDeserialize( element, all, vars, varName, buf );
      }
      else
      {
        throw new UnsupportedOperationException("Unknown element " + objectType);
      }
    }
    else if ( json.isJsonObject() )
    {
      String newVarName = json.getAsJsonObject().get( "name" ).getAsString();
      packetDeserialize( json.getAsJsonObject().get( "type" ), all, vars, newVarName, buf );
    }
    else
    {
      if ( all.has( json.getAsString() ) )
      {
        vars.put( varName, new LinkedHashMap< String, Object >() );
        packetDeserialize( all.get( json.getAsString() ), all, (Map< String, Object >) vars.get( varName ), null, buf );
      }
      else
      {
        String type = json.getAsString();
        JsonElement key = Main.PROTOCOL.getValues().get( type );
        if ( key.isJsonPrimitive() )
        {
          vars.put( varName, readNative( type, buf ) );
        }
        else
        {
          throw new UnsupportedOperationException( "Unknown type " + type );
        }
      }
    }
  }

  private static String processMapper( final JsonElement json, final ByteBuf buf )
  {
    int mapping = BufferUtils.readVarIntFromBuffer( buf );
    String mappingS = "0x" + String.format( "%1$02X", mapping ).toLowerCase();
    JsonElement element = json.getAsJsonArray().get( 1 ).getAsJsonObject().getAsJsonObject( "mappings" ).get(mappingS);
    if (element != null)
    {
      return element.getAsString();
    }
    else
    {
      throw new UnsupportedOperationException( "Unknown packet type " + mappingS);
    }
  }

  private static Object readNative( final String type, final ByteBuf buf )
  {
    if ( type.equals( "varint" ) )
    {
      return BufferUtils.readVarIntFromBuffer( buf );
    }
    else if( type.equals( "i32" ) )
    {
      return buf.readInt();
    }
    else if( type.equals( "i64" ) )
    {
      return buf.readLong();
    }
    else if( type.equals( "u8" ) )
    {
      return buf.readUnsignedByte();
    }
    else if( type.equals( "i8" ) )
    {
      return buf.readByte();
    }
    else if( type.equals( "f32" ) )
    {
      return buf.readFloat();
    }
    else if( type.equals( "bool" ) )
    {
      return buf.readBoolean();
    }
    else if( type.equals( "restBuffer" ) )
    {
      int count = BufferUtils.readVarIntFromBuffer( buf );
      return buf.readBytes(count);
    }
    else if ( type.equals( "string" ) )
    {
      return BufferUtils.readStringFromBuffer( buf, 10000 );
    }
    else
    {
      throw new UnsupportedOperationException( "Unknown native type " + type );
    }
  }
}
