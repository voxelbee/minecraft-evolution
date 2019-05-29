package com.evolution.world;

import io.netty.buffer.ByteBuf;

public class Chunk
{
  public boolean loaded;
  private ChunkPart[] parts;

  public Chunk()
  {
    this.parts = new ChunkPart[ 16 ];
  }

  /**
   * Initalizes the chunk and loads in the data from the buffer.
   *
   * @param data
   * @param availableSections
   */
  public void loadFromData( ByteBuf data, int availableSections )
  {
    for ( int i = 0; i < 16; i++ )
    {
      if ( ( availableSections & 1 << i ) == 0 )
      {
        parts[ i ] = null;
      }
      else
      {
        parts[ i ] = new ChunkPart();
        parts[ i ].load( data );
      }
    }
  }

  /**
   * Sets a block within this chunk from a block id
   */
  public void setBlock( int x, int y, int z, int id )
  {
    if ( y >= 0 && y >> 4 < 16 )
    {
      ChunkPart chunkPart = this.parts[ y >> 4 ];

      if ( chunkPart != null )
      {
        chunkPart.setBlockID( x, y, z, id );
      }
      else
      {
        this.parts[ y >> 4 ] = new ChunkPart();
      }
    }
  }

  /**
   * Gets a block id from the chunk
   *
   * @return the block id
   */
  public int getBlock( int x, int y, int z )
  {
    if ( y >= 0 && y >> 4 < 16 )
    {
      ChunkPart chunkPart = this.parts[ y >> 4 ];

      if ( chunkPart != null )
      {
        return chunkPart.getBlockID( x, y, z );
      }
    }

    return 0;
  }
}
