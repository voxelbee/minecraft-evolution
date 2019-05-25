package com.evomine.decode;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.evolution.main.Main;
import com.evolution.network.EnumConnectionState;
import com.evolution.network.EnumPacketDirection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
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
    return (Map< String, Object >) packetDeserialize( packets, types, Collections.emptyList(), buf );
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
      final List< Map< String, Object > > ancestors,
      final ByteBuf buf ) throws JsonParseException
  {
    checkNotNull( object, "json" );
    checkNotNull( ancestors, "ancestors" );
    if ( object.isJsonArray() )
    {
      checkState( object.getAsJsonArray().size() == 2, "Size of array" );
      JsonElement classObject = object.getAsJsonArray().get( 1 );
      String classType = object.getAsJsonArray().get( 0 ).getAsString();
      if ( classType.equals( "container" ) )
      {
        Map< String, Object > value = processContainer( classObject.getAsJsonArray(), packetTypes, ancestors, buf );
        return value;
      }
      else if ( classType.equals( "mapper" ) )
      {
        String value = processMapper( classObject.getAsJsonObject(), buf );
        return value;
      }
      else if ( classType.equals( "switch" ) )
      {
        Object value = processSwitch( classObject.getAsJsonObject(), packetTypes, ancestors, buf );
        return value;
      }
      else if ( classType.equals( "array" ) )
      {
        List< Object > value = processArray( classObject.getAsJsonObject(), packetTypes, ancestors, buf );
        return value;
      }
      else if ( classType.equals( "option" ) )
      {
        Object value = processOption( classObject, packetTypes, ancestors, buf );
        return value;
      }
      else if ( classType.equals( "buffer" ) )
      {
        byte[] value = processBuffer( classObject.getAsJsonObject(), packetTypes, ancestors, buf );
        return value;
      }
      else
      {
        throw new UnsupportedOperationException( "Unknown class " + classType );
      }
    }
    else if ( object.isJsonObject() )
    {
      String varName = object.getAsJsonObject().get( "name" ).getAsString();
      Object value = packetDeserialize( object.getAsJsonObject().get( "type" ), packetTypes, ancestors, buf );
      return new KeyValue( varName, value );
    }
    else
    {
      final String objectType = object.getAsString();
      if ( packetTypes.has( objectType ) )
      {
        Object value = packetDeserialize( packetTypes.get( objectType ), packetTypes, ancestors, buf );
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

  private static byte[] processBuffer( JsonObject json, JsonObject packetTypes, List< Map< String, Object > > ancestors, ByteBuf buf )
  {
    int count = (int) readNative( json.get( "countType" ).getAsString(), buf );
    byte[] buffer = new byte[ count ];
    buf.readBytes( buffer );
    return buffer;
  }

  private static Object processOption( JsonElement json, JsonObject packetTypes, List< Map< String, Object > > ancestors, ByteBuf buf )
  {
    boolean present = buf.readBoolean();
    if ( present )
    {
      return packetDeserialize( json, packetTypes, ancestors, buf );
    }
    else
    {
      return "void";
    }
  }

  private static Object processSwitch( JsonObject json, JsonObject packetTypes, List< Map< String, Object > > ancestors, ByteBuf buf )
  {
    String varNameToCompare = json.get( "compareTo" ).getAsString();
    final Map< String, Object > thisOne;
    if ( varNameToCompare.startsWith( "../" ) )
    {
      varNameToCompare = varNameToCompare.substring( 3 );
      thisOne = ancestors.get( ancestors.size() - 2 );
    }
    else
    {
      thisOne = ancestors.get( ancestors.size() - 1 );
    }
    String key = String.valueOf( thisOne.get( varNameToCompare ) );
    JsonElement element = json.get( "fields" ).getAsJsonObject().get( key );
    return packetDeserialize( element, packetTypes, ancestors, buf );
  }

  private static Map< String, Object > processContainer( final JsonArray json, JsonObject packetTypes,
      List< Map< String, Object > > ancestors,
      final ByteBuf buf )
  {
    Map< String, Object > container = new LinkedHashMap< String, Object >();
    Builder< Map< String, Object > > buildList = ImmutableList.builder();
    for ( Map< String, Object > item : ancestors )
    {
      buildList.add( item );
    }
    buildList.add( container );
    for ( JsonElement element : json )
    {
      KeyValue keyValue = (KeyValue) packetDeserialize( element, packetTypes, buildList.build(), buf );
      container.put( keyValue.key, keyValue.value );
    }
    return container;
  }

  private static List< Object > processArray( final JsonObject json, JsonObject packetTypes, List< Map< String, Object > > ancestors,
      final ByteBuf buf )
  {
    List< Object > values = new ArrayList< Object >();
    String countType = json.get( "countType" ).getAsString();
    int numElements = (int) readNative( countType, buf );
    for ( int i = 0; i < numElements; i++ )
    {
      Object value = packetDeserialize( json.get( "type" ), packetTypes, ancestors, buf );
      values.add( value );
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
