package com.evolution;

import java.io.IOException;

import com.evolution.network.Connection;
import com.evolution.world.World;
import com.evomine.network.decode.Protocol;
import com.google.gson.Gson;

public class Main
{
  public static final Logger LOGGER = new Logger();

  public static final Gson GSON = new Gson();
  public static final Protocol PROTOCOL = new Protocol( "1.12" );
  public static final World WORLD = new World();

  public static int ticksPerSecond = 20;

  public static void main( String[] args ) throws IOException
  {
    String ip = "localhost";
    int port = 25565;

    Connection[] con = new Connection[ 1 ];

    for ( int i = 0; i < con.length; i++ )
    {
      con[ i ] = new Connection( ip, port, "" + i );
    }

    long aim = 1000 / ticksPerSecond;
    while ( con[ 0 ].isConnected )
    {
      long start = System.currentTimeMillis();

      WORLD.tick();

      for ( int i = 0; i < con.length; i++ )
      {
        con[ i ].update();
      }

      long end = System.currentTimeMillis();
      long timeSleep = aim - ( start - end );
      if ( timeSleep < 0 )
      {
        LOGGER.log( EnumLoggerType.WARN, "Running behind..." );
      }
      else
      {
        sleep( timeSleep );
      }
    }
  }

  public static void sleep( long time )
  {
    try
    {
      Thread.sleep( time );
    }
    catch ( InterruptedException e )
    {
      e.printStackTrace();
    }
  }
}
