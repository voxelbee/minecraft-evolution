package com.evolution.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import com.evolution.player.Player;

public class World
{
  private Map< Vector2D, Chunk > chunks = new HashMap< Vector2D, Chunk >();
  private List< Player > localPlayers = new ArrayList< Player >();

  private Map< Integer, Entity > entities = new HashMap< Integer, Entity >();

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
}
