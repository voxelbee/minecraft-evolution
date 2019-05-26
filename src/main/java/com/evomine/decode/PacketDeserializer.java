package com.evomine.decode;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.nio.charset.StandardCharsets;
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

  public static Map< String, Object > packetDeserialize( final JsonElement json,
      final ByteBuf buf,
      final EnumConnectionState state )
  {
    JsonObject types = json.getAsJsonObject()
        .getAsJsonObject( state.getAsString() )
        .getAsJsonObject( EnumPacketDirection.CLIENTBOUND.getAsString() )
        .getAsJsonObject( "types" );
    JsonElement packets = types.get( "packet" );
    return (Map< String, Object >) objectDeserialize( packets, types, Collections.emptyList(), buf );
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

  public static Object objectDeserialize( final JsonElement object,
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
        Object value = processSwitch( classObject.getAsJsonObject(), packetTypes, ancestors, null, buf );
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
      else if ( classType.equals( "pstring" ) )
      {
        String value = processPstring( classObject.getAsJsonObject(), buf );
        return value;
      }
      else if ( classType.equals( "nbtSwitch" ) )
      {
        Object value = processNbtSwitch( classObject.getAsJsonObject(), packetTypes, ancestors, buf );
        return value;
      }
      else if ( classType.equals( "entityMetadataLoop" ) )
      {
        Object value = processEntityMetadataLoop( classObject.getAsJsonObject(), packetTypes, ancestors, buf );
        return value;
      }
      else if ( classType.equals( "entityMetadataItem" ) )
      {
        Object value = processEntityMetadataItem( classObject.getAsJsonObject(), packetTypes, ancestors, buf );
        return value;
      }
      else
      {
        throw new UnsupportedOperationException( "Unknown class " + classType );
      }
    }
    else if ( object.isJsonObject() )
    {
      JsonObject jsonObject = object.getAsJsonObject();
      final String varName;
      if ( jsonObject.has( "name" ) )
      {
        varName = object.getAsJsonObject().get( "name" ).getAsString();
      }
      else if ( jsonObject.has( "anon" ) )
      {
        if ( !jsonObject.get( "anon" ).getAsBoolean() )
        {
          throw new IllegalArgumentException( "anon is present but is false" );
        }
        varName = "key";
      }
      else
      {
        throw new IllegalArgumentException( "Could not get name or anon for json object" );
      }
      Object value = objectDeserialize( jsonObject.get( "type" ), packetTypes, ancestors, buf );
      return new KeyValue( varName, value );
    }
    else
    {
      final String objectType = object.getAsString();
      JsonElement key = Main.PROTOCOL.getDefaultValues().get( objectType );
      if ( key != null )
      {
        if ( key.isJsonPrimitive() )
        {
          return readNative( objectType, buf );
        }
        else
        {
          return objectDeserialize( key, packetTypes, Collections.emptyList(), buf );
        }
      }
      else
      {
        if ( packetTypes.has( objectType ) )
        {
          Object value = objectDeserialize( packetTypes.get( objectType ), packetTypes, ancestors, buf );
          return value;
        }
        else
        {
          throw new UnsupportedOperationException( "Unknown type " + objectType );
        }
      }
    }
  }

  private static Object processEntityMetadataItem( JsonObject json, JsonObject packetTypes, List< Map< String, Object > > ancestors, ByteBuf buf )
  {
    JsonObject fields =
        Main.PROTOCOL.getDefaultValues().getAsJsonArray( "entityMetadataItem" ).get( 1 ).getAsJsonObject().get( "fields" ).getAsJsonObject();
    Map< String, Object > thisOne = ancestors.get( ancestors.size() - 1 );
    String type = String.valueOf( ( (Map< String, Object >) thisOne.get( "key" ) ).get( "type" ) );

    return objectDeserialize( fields.get( type ), packetTypes, ancestors, buf );
  }

  private static Object processEntityMetadataLoop( JsonObject json, JsonObject packetTypes, List< Map< String, Object > > ancestors, ByteBuf buf )
  {
    checkState( json.size() == 2, "Size of json object" );
    List< Object > objects = new ArrayList< Object >();
    byte endVal = json.get( "endVal" ).getAsByte();
    byte i;
    while ( ( i = buf.readByte() ) != endVal )
    {
      Object value = objectDeserialize( json.get( "type" ), packetTypes, ancestors, buf );
      objects.add( value );
    }
    return objects;
  }

  private static Object processNbtSwitch( JsonObject json, JsonObject packetTypes, List< Map< String, Object > > ancestors, ByteBuf buf )
  {
    JsonObject nbtSwitch = Main.PROTOCOL.getDefaultValues().getAsJsonArray( "nbtSwitch" ).get( 1 ).getAsJsonObject();
    Object valueToCompare = ancestors.get( ancestors.size() - 1 ).get( json.get( "type" ).getAsString() );
    return processSwitch( nbtSwitch, packetTypes, ancestors, valueToCompare, buf );
  }

  private static String processPstring( JsonObject json, ByteBuf buf )
  {
    int count = ( (Number) readNative( json.get( "countType" ).getAsString(), buf ) ).intValue();

    String value = buf.toString( buf.readerIndex(), count, StandardCharsets.UTF_8 );
    buf.readerIndex( buf.readerIndex() + count );
    return value;
  }

  private static byte[] processBuffer( JsonObject json, JsonObject packetTypes, List< Map< String, Object > > ancestors, ByteBuf buf )
  {
    int count = ( (Number) readNative( json.get( "countType" ).getAsString(), buf ) ).intValue();
    byte[] buffer = new byte[ count ];
    buf.readBytes( buffer );
    return buffer;
  }

  private static Object processOption( JsonElement json, JsonObject packetTypes, List< Map< String, Object > > ancestors, ByteBuf buf )
  {
    boolean present = buf.readBoolean();
    if ( present )
    {
      return objectDeserialize( json, packetTypes, ancestors, buf );
    }
    else
    {
      return "void";
    }
  }

  private static Object processSwitch( JsonObject json,
      JsonObject packetTypes,
      List< Map< String, Object > > ancestors,
      Object compareValue,
      ByteBuf buf )
  {
    if ( compareValue == null )
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
      return objectDeserialize( element, packetTypes, ancestors, buf );
    }
    else
    {
      JsonElement element = json.get( "fields" ).getAsJsonObject().get( String.valueOf( compareValue ) );
      return objectDeserialize( element, packetTypes, ancestors, buf );
    }
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
    List< Map< String, Object > > allAncestors = buildList.build();
    for ( JsonElement element : json )
    {
      KeyValue keyValue = (KeyValue) objectDeserialize( element, packetTypes, allAncestors, buf );

      container.put( keyValue.key, keyValue.value );
      if ( keyValue.value instanceof String )
      {
        if ( ( (String) keyValue.value ).equals( "end" ) )
        {
          break;
        }
      }
    }
    return container;
  }

  private static List< Object > processArray( final JsonObject json, JsonObject packetTypes, List< Map< String, Object > > ancestors,
      final ByteBuf buf )
  {
    List< Object > values = new ArrayList< Object >();
    String countType = json.get( "countType" ).getAsString();
    int numElements = ( (Number) readNative( countType, buf ) ).intValue();
    for ( int i = 0; i < numElements; i++ )
    {
      Object value = objectDeserialize( json.get( "type" ), packetTypes, ancestors, buf );
      values.add( value );
    }
    return values;
  }

  private static String processMapper( final JsonObject json, final ByteBuf buf )
  {
    String type = json.get( "type" ).getAsString();
    int mapping = ( (Number) readNative( type, buf ) ).intValue();
    String mappingS;
    if ( type.equals( "varint" ) )
    {
      mappingS = "0x" + String.format( "%1$02X", mapping ).toLowerCase();
    }
    else
    {
      mappingS = String.valueOf( mapping );
    }
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

  private static Object readCompound( final ByteBuf buf )
  {
    List< Object > compound = new ArrayList< Object >();
    JsonElement nbt = Main.PROTOCOL.getDefaultValues().get( "nbt" );
    while ( true )
    {
      Object value = objectDeserialize( nbt, null, Collections.emptyList(), buf );
      compound.add( value );
      if ( ( (String) ( (Map< String, Object >) value ).get( "type" ) ).equals( "end" ) )
      {
        break;
      }
    }
    return compound;
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
    else if ( type.equals( "i16" ) )
    {
      return buf.readShort();
    }
    else if ( type.equals( "compound" ) )
    {
      return readCompound( buf );
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
    else
    {
      throw new UnsupportedOperationException( "Unknown native type " + type );
    }
  }
}
