package com.evolution.main;

import java.io.IOException;
import java.net.InetAddress;

import com.evolution.network.EnumConnectionState;
import com.evolution.network.handler.LoginHandler;
import com.evolution.network.handler.NettyManager;
import com.evomine.decode.Packet;
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
    NettyManager manager = NettyManager.createNetworkManagerAndConnect( InetAddress.getByName( ip ), port, true );
    manager.setNetHandler( new LoginHandler( manager ) );

    Packet C00Handshake = new Packet( "set_protocol", EnumConnectionState.HANDSHAKING );
    C00Handshake.params.put( "protocolVersion", PROTOCOL.getProtocol() );
    C00Handshake.params.put( "serverHost", ip );
    C00Handshake.params.put( "serverPort", (short) port );
    C00Handshake.params.put( "nextState", EnumConnectionState.LOGIN.getId() );
    manager.sendPacket( C00Handshake );

    Packet CLogin = new Packet( "login_start", EnumConnectionState.LOGIN );
    CLogin.params.put( "username", "jim" );
    manager.sendPacket( CLogin );

    while ( true )
    {
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
