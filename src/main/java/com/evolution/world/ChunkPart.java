package com.evolution.world;

import java.util.ArrayList;
import java.util.List;

import com.evomine.decode.BufferUtils;

import io.netty.buffer.ByteBuf;

// 16x16x16 set of blocks
public class ChunkPart
{
  // Blocks used
  private List< Integer > palette;

  // Blocks data
  private BitArray blocks;

  // Number of bits to represent blocks
  private int bits;

  /**
   * loads the chunk part from the ByteBuf data
   *
   * @param data
   */
  public void load( ByteBuf data )
  {
    this.bits = data.readByte();

    // READ IN THE BLOCK PALLTE
    int arraySize = BufferUtils.readVarIntFromBuffer( data );
    palette = new ArrayList< Integer >();

    for ( int j = 0; j < arraySize; ++j )
    {
      palette.add( BufferUtils.readVarIntFromBuffer( data ) );
    }

    // Read in the blocks
    blocks = new BitArray( this.bits, 4096 );
    BufferUtils.readLongArray( blocks.getBackingLongArray(), data );

    // we don't needs the lighting data
    data.readerIndex( data.readerIndex() + 4096 );
  }

  public void initalize()
  {
    palette = new ArrayList< Integer >();
    this.bits = 4;
  }

  /**
   * Sets the id of a block changing chunk id's if needed
   */
  public void setBlockID( int x, int y, int z, int id )
  {
    int index = palette.indexOf( id << 4 );
    if ( index == -1 )
    {
      palette.add( id << 4 );
      index = palette.size() - 1;

      if ( palette.size() > ( 1 << this.bits ) )
      {
        blocksToNewBitSize( palette.size() );
      }
    }
    blocks.setAt( ( y & 15 ) << 8 | ( z & 15 ) << 4 | ( x & 15 ), index );
  }

  /**
   * Copies the blocks into a new data structure with the
   * new bit size per element
   *
   * @param newBits
   */
  private void blocksToNewBitSize( int newBits )
  {
    BitArray newBlocks = new BitArray( newBits, 4096 );
    for ( int i = 0; i < 4096; i++ )
    {
      newBlocks.setAt( i, this.blocks.getAt( i ) );
    }
    this.blocks = newBlocks;
  }

  /**
   * Returns the id of a block at in pos
   *
   * @return
   */
  public int getBlockID( int x, int y, int z )
  {
    return palette.get( blocks.getAt( ( y & 15 ) << 8 | ( z & 15 ) << 4 | ( x & 15 ) ) ) >> 4;
  }
}
