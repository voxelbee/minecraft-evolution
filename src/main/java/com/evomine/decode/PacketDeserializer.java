package com.evomine.decode;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

  public static Map< String, Object > deserializeRoot( final JsonElement json,
      final ByteBuf buf,
      final EnumConnectionState state )
  {
    JsonObject types = json.getAsJsonObject()
        .getAsJsonObject( state.getAsString() )
        .getAsJsonObject( EnumPacketDirection.CLIENTBOUND.getAsString() )
        .getAsJsonObject( "types" );
    JsonElement packets = types.get( "packet" );
    return (Map< String, Object >) packetDeserialize( packets, types, null, buf );
  }

  private static class KeyValue
  {
    final String key;
    final Object value;

    KeyValue( final String key, final Object value )
    {
      this.key = checkNotNull( key, "key" );
      this.value = checkNotNull( value, "value" );
    }
  }

  private static Object packetDeserialize( final JsonElement object,
      final JsonObject packetTypes,
      final Map< String, Object > parent,
      final ByteBuf buf ) throws JsonParseException
  {
    checkNotNull( object, "json" );
    if ( object.isJsonArray() )
    {
      checkState( object.getAsJsonArray().size() == 2, "Size of array" );
      JsonElement classObject = object.getAsJsonArray().get( 1 );
      String classType = object.getAsJsonArray().get( 0 ).getAsString();
      if ( classType.equals( "container" ) )
      {
        Map< String, Object > value = processContainer( classObject.getAsJsonArray(), packetTypes, buf );
        return value;
      }
      else if ( classType.equals( "mapper" ) )
      {
        String value = processMapper( classObject.getAsJsonObject(), buf );
        return value;
      }
      else if ( classType.equals( "switch" ) )
      {
        Object value = processSwitch( classObject.getAsJsonObject(), packetTypes, parent, buf );
        return value;
      }
      else if ( classType.equals( "array" ) )
      {
        List< Object > value = processArray( classObject.getAsJsonObject(), packetTypes, buf );
        return value;
      }
      else
      {
        throw new UnsupportedOperationException( "Unknown element " + classType );
      }
    }
    else if ( object.isJsonObject() )
    {
      String varName = object.getAsJsonObject().get( "name" ).getAsString();
      Object value = packetDeserialize( object.getAsJsonObject().get( "type" ), packetTypes, parent, buf );
      return new KeyValue( varName, value );
    }
    else
    {
      final String objectType = object.getAsString();
      if ( packetTypes.has( objectType ) )
      {
        Object value = packetDeserialize( packetTypes.get( objectType ), packetTypes, parent, buf );
        return value;
      }
      else
      {
        JsonElement key = Main.PROTOCOL.getValues().get( objectType );
        if ( key.isJsonPrimitive() )
        {
          return readNative( objectType, buf );
        }
        else
        {
          throw new UnsupportedOperationException( "Unknown type " + objectType );
        }
      }
    }
  }

  private static Object processSwitch( JsonObject json, JsonObject packetTypes, Map< String, Object > parent, ByteBuf buf )
  {
    String varNameToCompare = json.get( "compareTo" ).getAsString();
    if ( varNameToCompare.startsWith( "../" ) )
    {
      varNameToCompare = varNameToCompare.substring( 3 );
    }
    if ( !parent.containsKey( varNameToCompare ) )
    {
      throw new RuntimeException( "Failed to find key " + varNameToCompare );
    }
    String key = String.valueOf( parent.get( varNameToCompare ) );
    JsonElement element = json.get( "fields" ).getAsJsonObject().get( key );
    return packetDeserialize( element, packetTypes, null, buf );
  }

  private static Map< String, Object > processContainer( final JsonArray json, JsonObject packetTypes, final ByteBuf buf )
  {
    Map< String, Object > container = new LinkedHashMap< String, Object >();
    for ( JsonElement element : json )
    {
      KeyValue keyValue = (KeyValue) packetDeserialize( element, packetTypes, container, buf );
      container.put( keyValue.key, keyValue.value );
    }
    return container;
  }

  private static List< Object > processArray( final JsonObject json, JsonObject packetTypes, final ByteBuf buf )
  {
    List< Object > values = new ArrayList< Object >();
    String countType = json.get( "countType" ).getAsString();
    int numElements = (int) readNative( countType, buf );
    for ( int i = 0; i < numElements; i++ )
    {
      Map< String, Object > result = new LinkedHashMap< String, Object >();
      packetDeserialize( json.get( "type" ), packetTypes, result, buf );
      values.add( result );
    }
    return values;
  }

  private static String processMapper( final JsonObject json, final ByteBuf buf )
  {
    int mapping = BufferUtils.readVarIntFromBuffer( buf );
    String mappingS = "0x" + String.format( "%1$02X", mapping ).toLowerCase();
    JsonElement element = json.getAsJsonObject( "mappings" ).get( mappingS );
    if ( element != null )
    {
      return element.getAsString();
    }
    else
    {
      throw new UnsupportedOperationException( "Unknown packet type " + mappingS );
    }
  }

  private static Object readNative( final String type, final ByteBuf buf )
  {
    if ( type.equals( "varint" ) )
    {
      return BufferUtils.readVarIntFromBuffer( buf );
    }
    else if ( type.equals( "i32" ) )
    {
      return buf.readInt();
    }
    else if ( type.equals( "i64" ) )
    {
      return buf.readLong();
    }
    else if ( type.equals( "u8" ) )
    {
      return buf.readUnsignedByte();
    }
    else if ( type.equals( "i8" ) )
    {
      return buf.readByte();
    }
    else if ( type.equals( "f32" ) )
    {
      return buf.readFloat();
    }
    else if ( type.equals( "bool" ) )
    {
      return buf.readBoolean();
    }
    else if ( type.equals( "UUID" ) )
    {
      return new UUID( buf.readLong(), buf.readLong() );
    }
    else if ( type.equals( "restBuffer" ) )
    {
      int count = BufferUtils.readVarIntFromBuffer( buf );
      return buf.readBytes( count );
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
