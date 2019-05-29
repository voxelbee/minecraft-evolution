package com.evolution.player;

import com.evomine.decode.BufferUtils;

import io.netty.buffer.ByteBuf;

public class Chunk
{
  public boolean loaded;

  private int[][] blockpallets;

  private BitArray[] blockStorage;
  private byte bits;

  public Chunk()
  {
    this.bits = 4;
    this.blockpallets = new int[ 16 ][];
    this.blockStorage = new BitArray[ 16 ];
  }

  public void populate( ByteBuf data, int availableSections )
  {
    for ( int i = 0; i < blockStorage.length; i++ )
    {
      if ( ( availableSections & 1 << i ) == 0 )
      {
        this.blockStorage[ i ] = null;
      }
      else
      {
        readChunkSection( data, i );
      }
    }
  }

  private void readChunkSection( ByteBuf data, int sectionId )
  {
    this.bits = data.readByte(); // read one byte for how many bits represent the blocks

    // READ IN THE BLOCK PALLTE
    int arraySize = BufferUtils.readVarIntFromBuffer( data );
    this.blockpallets[ sectionId ] = new int[ arraySize ];

    for ( int j = 0; j < arraySize; ++j )
    {
      this.blockpallets[ sectionId ][ j ] = BufferUtils.readVarIntFromBuffer( data );
    }

    // Read in the blocks
    this.blockStorage[ sectionId ] = new BitArray( this.bits, 4096 );
    BufferUtils.readLongArray( this.blockStorage[ sectionId ].getBackingLongArray(), data );

    // we don't needs the lighting data
    data.readerIndex( data.readerIndex() + 4096 );
  }

  public void setBlock( int x, int y, int z, int id )
  {
    if ( y >= 0 && y >> 4 < this.blockStorage.length )
    {
      BitArray blocks = this.blockStorage[ y >> 4 ];
      int[] pallte = this.blockpallets[ y >> 4 ];

      int index = 0;
      for ( int i = 0; i < pallte.length; i++ )
      {
        if ( pallte[ i ] == id << 4 )
        {
          index = i;
          break;
        }
      }

      if ( blocks != null )
      {
        blocks.setAt( ( y & 15 ) << 8 | ( z & 15 ) << 4 | ( x & 15 ), index );
      }
    }
  }

  public int getBlock( int x, int y, int z )
  {
    if ( y >= 0 && y >> 4 < this.blockStorage.length )
    {
      BitArray blocks = this.blockStorage[ y >> 4 ];
      int[] pallte = this.blockpallets[ y >> 4 ];

      if ( blocks != null )
      {
        return pallte[ blocks.getAt( ( y & 15 ) << 8 | ( z & 15 ) << 4 | ( x & 15 ) ) ] >> 4;
      }
    }

    return 0;
  }
}
