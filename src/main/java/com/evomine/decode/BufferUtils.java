package com.evomine.decode;

import java.nio.charset.StandardCharsets;

import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;

public class BufferUtils
{
  /**
   * Reads a compressed int from the buffer. To do so it maximally reads 5 byte-sized chunks whose most significant
   * bit dictates whether another byte should be read.
   */
  public static int readVarIntFromBuffer( ByteBuf buffer )
  {
    int i = 0;
    int j = 0;

    while ( true )
    {
      byte b0 = buffer.readByte();
      i |= ( b0 & 127 ) << j++ * 7;

      if ( j > 5 )
      {
        throw new RuntimeException( "VarInt too big" );
      }

      if ( ( b0 & 128 ) != 128 )
      {
        break;
      }
    }

    return i;
  }

  /**
   * Calculates the number of bytes required to fit the supplied int (0-5) if it were to be read/written using
   * readVarIntFromBuffer or writeVarIntToBuffer
   */
  public static int getVarIntSize( int input )
  {
    for ( int i = 1; i < 5; ++i )
    {
      if ( ( input & -1 << i * 7 ) == 0 )
      {
        return i;
      }
    }

    return 5;
  }

  /**
   * Writes a compressed int to the buffer. The smallest number of bytes to fit the passed int will be written. Of
   * each such byte only 7 bits will be used to describe the actual value since its most significant bit dictates
   * whether the next byte is part of that same int. Micro-optimization for int values that are expected to have
   * values below 128.
   */
  public static void writeVarIntToBuffer( ByteBuf buffer, int input )
  {
    while ( ( input & -128 ) != 0 )
    {
      buffer.writeByte( input & 127 | 128 );
      input >>>= 7;
    }

    buffer.writeByte( input );
  }

  public static void writeString( ByteBuf buffer, String string )
  {
    byte[] abyte = string.getBytes( StandardCharsets.UTF_8 );

    if ( abyte.length > 32767 )
    {
      throw new EncoderException( "String too big (was " + abyte.length + " bytes encoded, max " + 32767 + ")" );
    }
    else
    {
      BufferUtils.writeVarIntToBuffer( buffer, abyte.length );
      buffer.writeBytes( abyte );
    }
  }

  /**
   * Reads a string from this buffer. Expected parameter is maximum allowed string length. Will throw IOException if
   * string length exceeds this value!
   */
  public static String readStringFromBuffer( ByteBuf buffer, int maxLength )
  {
    int i = BufferUtils.readVarIntFromBuffer( buffer );

    if ( i > maxLength * 4 )
    {
      throw new DecoderException( "The received encoded string buffer length is longer than maximum allowed (" + i + " > " + maxLength * 4 + ")" );
    }
    else if ( i < 0 )
    {
      throw new DecoderException( "The received encoded string buffer length is less than zero! Weird string!" );
    }
    else
    {
      String s = buffer.toString( buffer.readerIndex(), i, StandardCharsets.UTF_8 );
      buffer.readerIndex( buffer.readerIndex() + i );

      if ( s.length() > maxLength )
      {
        throw new DecoderException( "The received string length is longer than maximum allowed (" + i + " > " + maxLength + ")" );
      }
      else
      {
        return s;
      }
    }
  }

  /**
   * Reads a length-prefixed array of longs from the buffer.
   */
  public static long[] readLongArray( @Nullable long[] array, ByteBuf buf )
  {
    return readLongArray( array, buf.readableBytes() / 8, buf );
  }

  public static long[] readLongArray( @Nullable long[] array, int size, ByteBuf buf )
  {
    int i = readVarIntFromBuffer( buf );

    if ( array == null || array.length != i )
    {
      if ( i > size )
      {
        throw new DecoderException( "LongArray with size " + i + " is bigger than allowed " + size );
      }

      array = new long[ i ];
    }

    for ( int j = 0; j < array.length; ++j )
    {
      array[ j ] = buf.readLong();
    }

    return array;
  }
}
