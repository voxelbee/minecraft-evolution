package com.evolution.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import com.evolution.entity.Entity;
import com.evolution.entity.Player;
import com.evolution.util.math.AxisAlignedBB;
import com.evolution.util.math.BlockPos;
import com.google.common.collect.Lists;

public class World
{
  private Map< Vector2D, Chunk > chunks = new HashMap< Vector2D, Chunk >();
  private List< Player > localPlayers = new ArrayList< Player >();

  private Map< Integer, Entity > entities = new HashMap< Integer, Entity >();

  public static final AxisAlignedBB FULL_BLOCK_AABB = new AxisAlignedBB( 0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D );
  @Nullable
  public static final AxisAlignedBB NULL_AABB = null;

  public void tick()
  {
    for ( int i = 0; i < localPlayers.size(); i++ )
    {
      localPlayers.get( i ).update();
    }
  }

  /**
   * Returns first instance of entity with specified type
   *
   * @param type
   * @return
   */
  public Entity getEntity( int type )
  {
    for ( Integer key : entities.keySet() )
    {
      Entity current = entities.get( key );
      if ( current.getType() == 91 )
      {
        return current;
      }
    }
    return null;
  }

  public void addEntity( int id, Entity inEntity )
  {
    this.entities.put( id, inEntity );
  }

  public void addPlayer( Player inPlayer )
  {
    this.localPlayers.add( inPlayer );
  }

  public int createNewPlayer()
  {
    this.localPlayers.add( new Player() );
    return this.localPlayers.size() - 1;
  }

  public Player getPlayer( int id )
  {
    return this.localPlayers.get( id );
  }

  public Chunk getChunk( int x, int z )
  {
    return this.chunks.get( new Vector2D( x, z ) );
  }

  public boolean hasChunk( int x, int z )
  {
    return this.chunks.containsKey( new Vector2D( x, z ) );
  }

  public void setChunk( int x, int z, Chunk value )
  {
    this.chunks.put( new Vector2D( x, z ), value );
  }

  public int getBlock( double x, double y, double z )
  {
    int chunkX = (int) Math.floor( x / 16 );
    int chunkZ = (int) Math.floor( z / 16 );

    Chunk chunk = this.getChunk( chunkX, chunkZ );
    if ( chunk != null )
    {
      if ( chunk.loaded )
      {
        return chunk.getBlock( (int) Math.floor( x ), (int) Math.floor( y ), (int) Math.floor( z ) );
      }
    }

    return 0;
  }

  public int getBlock( BlockPos pos )
  {
    int chunkX = (int) Math.floor( pos.getX() / 16 );
    int chunkZ = (int) Math.floor( pos.getZ() / 16 );

    Chunk chunk = this.getChunk( chunkX, chunkZ );
    if ( chunk != null )
    {
      if ( chunk.loaded )
      {
        return chunk.getBlock( pos.getX(), pos.getY(), pos.getZ() );
      }
    }

    return 0;
  }

  public void setBlock( double x, double y, double z, int id )
  {
    int chunkX = (int) Math.floor( x / 16 );
    int chunkZ = (int) Math.floor( z / 16 );

    Chunk chunk = this.getChunk( chunkX, chunkZ );
    if ( chunk != null )
    {
      if ( chunk.loaded )
      {
        chunk.setBlock( (int) Math.floor( x ), (int) Math.floor( y ), (int) Math.floor( z ), id );
      }
    }
  }

  public boolean isBlockLoaded( BlockPos pos )
  {
    return this.hasChunk( (int) Math.floor( ( pos.getX() / 16.0D ) ), (int) Math.floor( ( pos.getZ() / 16.0D ) ) );
  }

  private boolean getBlockCollisionBoxes( @Nullable Entity entityIn, AxisAlignedBB entityBoxIn, boolean p_191504_3_,
      @Nullable List< AxisAlignedBB > outputList )
  {
    int i = (int) Math.floor( entityBoxIn.minX ) - 1;
    int j = (int) Math.ceil( entityBoxIn.maxX ) + 1;
    int k = (int) Math.floor( entityBoxIn.minY ) - 1;
    int l = (int) Math.ceil( entityBoxIn.maxY ) + 1;
    int i1 = (int) Math.floor( entityBoxIn.minZ ) - 1;
    int j1 = (int) Math.ceil( entityBoxIn.maxZ ) + 1;
    BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();

    try
    {
      for ( int k1 = i; k1 < j; ++k1 )
      {
        for ( int l1 = i1; l1 < j1; ++l1 )
        {
          boolean flag2 = k1 == i || k1 == j - 1;
          boolean flag3 = l1 == i1 || l1 == j1 - 1;

          if ( ( !flag2 || !flag3 ) && this.isBlockLoaded( blockpos$pooledmutableblockpos.setPos( k1, 64, l1 ) ) )
          {
            for ( int i2 = k; i2 < l; ++i2 )
            {
              if ( !flag2 && !flag3 || i2 != l - 1 )
              {
                if ( p_191504_3_ )
                {
                  if ( k1 < -30000000 || k1 >= 30000000 || l1 < -30000000 || l1 >= 30000000 )
                  {
                    boolean lvt_21_2_ = true;
                    return lvt_21_2_;
                  }
                }

                blockpos$pooledmutableblockpos.setPos( k1, i2, l1 );
                int iblockstate1 = this.getBlock( blockpos$pooledmutableblockpos );

                if ( iblockstate1 != 0 )
                {
                  AxisAlignedBB axisalignedbb = FULL_BLOCK_AABB.offset( blockpos$pooledmutableblockpos );

                  if ( entityBoxIn.intersectsWith( axisalignedbb ) )
                  {
                    outputList.add( axisalignedbb );
                  }
                }

                if ( p_191504_3_ && !outputList.isEmpty() )
                {
                  boolean flag5 = true;
                  return flag5;
                }
              }
            }
          }
        }
      }
    }
    finally
    {
      blockpos$pooledmutableblockpos.release();
    }

    return !outputList.isEmpty();
  }

  public List< AxisAlignedBB > getCollisionBoxes( @Nullable Entity entityIn, AxisAlignedBB aabb )
  {
    List< AxisAlignedBB > list = Lists.< AxisAlignedBB > newArrayList();
    this.getBlockCollisionBoxes( entityIn, aabb, false, list );

    return list;
  }
}
