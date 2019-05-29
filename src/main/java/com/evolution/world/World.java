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
  private List< Player > players = new ArrayList< Player >();

  public void tick()
  {
    for ( int i = 0; i < players.size(); i++ )
    {
      players.get( i ).update();
    }
  }

  public void addPlayer( Player inPlayer )
  {
    this.players.add( inPlayer );
  }

  public int createNewPlayer()
  {
    this.players.add( new Player() );
    return this.players.size() - 1;
  }

  public Player getPlayer( int id )
  {
    return this.players.get( id );
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