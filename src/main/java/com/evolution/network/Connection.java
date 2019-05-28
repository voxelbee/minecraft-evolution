package com.evolution.network;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.evolution.main.EnumLoggerType;
import com.evolution.main.Main;
import com.evolution.network.handler.LoginHandler;
import com.evolution.network.handler.NettyManager;
import com.evolution.network.handler.PlayHandler;
import com.evolution.player.Player;
import com.evomine.decode.Packet;

public class Connection
{
  private String ip;
  private int port;
  private String userName;

  public boolean isConnected;

  private NettyManager manager;
  private Player player;

  public Connection( String ip, int port, String userName )
  {
    this.ip = ip;
    this.port = port;
    this.userName = userName;
    this.player = new Player();
    try
    {
      this.connect();
    }
    catch ( UnknownHostException e )
    {
      Main.LOGGER.log( EnumLoggerType.ERROR, "Unknown host: " + ip + ":" + port );
    }
  }

  /**
   * Connects the player to the server with the specified ip, port and userName
   *
   * @throws UnknownHostException
   */
  public void connect() throws UnknownHostException
  {
    this.manager = NettyManager.createNetworkManagerAndConnect( InetAddress.getByName( this.ip ), this.port, true, this );
    this.manager.setNetHandler( new LoginHandler( manager ) );
    Main.LOGGER.log( EnumLoggerType.INFO, "Created network manager for user " + this.userName );

    sleep( 500 );

    Packet C00Handshake = new Packet( "set_protocol", EnumConnectionState.HANDSHAKING );
    C00Handshake.params.put( "protocolVersion", Main.PROTOCOL.getVersion() );
    C00Handshake.params.put( "serverHost", ip );
    C00Handshake.params.put( "serverPort", (short) port );
    C00Handshake.params.put( "nextState", EnumConnectionState.LOGIN.getId() );
    this.manager.sendPacket( C00Handshake );

    Main.LOGGER.log( EnumLoggerType.INFO, "Initallized handshake with server" );

    sleep( 100 );

    Packet CLogin = new Packet( "login_start", EnumConnectionState.LOGIN );
    CLogin.params.put( "username", this.userName );
    this.manager.sendPacket( CLogin );
    isConnected = true;
  }

  public void update()
  {
    this.manager.getNetHandler().update();
    if ( this.manager.getNetHandler() instanceof PlayHandler )
    {
      if ( this.player.getHealth() <= 0.0f )
      {
        ( (PlayHandler) this.manager.getNetHandler() ).respawn();
      }
    }
  }

  /**
   * Gets the player that this connection manages
   *
   * @return
   */
  public Player getPlayer()
  {
    return this.player;
  }

  private void sleep( int time )
  {
    try
    {
      Thread.sleep( time );
    }
    catch ( InterruptedException e1 )
    {
      e1.printStackTrace();
    }
  }
}