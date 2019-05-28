package com.evolution.main;

import java.io.IOException;

import com.evolution.network.Connection;
import com.evomine.decode.Protocol;
import com.google.gson.Gson;

public class Main
{
  public static final Logger LOGGER = new Logger();

  public static final Gson GSON = new Gson();
  public static final Protocol PROTOCOL = new Protocol( "1.12" );

  public static void main( String[] args ) throws IOException
  {
    String ip = "localhost";
    int port = 25565;

    Connection[] con = new Connection[ 1 ];

    for ( int i = 0; i < con.length; i++ )
    {
      con[ i ] = new Connection( ip, port, "Player" + i );
      con[ i ].connect();
    }

    while ( con[ 0 ].isConnected )
    {
      for ( int i = 0; i < con.length; i++ )
      {
        con[ i ].update();
      }

      try
      {
        Thread.sleep( 100 );
      }
      catch ( InterruptedException e )
      {
        e.printStackTrace();
      }
    }
  }
}
