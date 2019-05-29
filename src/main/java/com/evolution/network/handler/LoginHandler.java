package com.evolution.network.handler;

import com.evolution.EnumLoggerType;
import com.evolution.Main;
import com.evolution.network.EnumConnectionState;
import com.evomine.decode.Packet;

public class LoginHandler implements INetHandler
{
  private final NettyManager networkManager;

  public LoginHandler( NettyManager networkManagerIn )
  {
    this.networkManager = networkManagerIn;
  }

  public void handleLoginSuccess( Packet packetIn )
  {
    this.networkManager.setConnectionState( EnumConnectionState.PLAY );
    this.networkManager.setNetHandler( new PlayHandler( this.networkManager ) );
    Main.LOGGER.log( EnumLoggerType.INFO, "Compleated login " + packetIn.params.get( "username" ) );
  }

  /**
   * Invoked when disconnecting, the parameter is a ChatComponent describing the reason for termination
   */
  @Override
  public void onDisconnect( String reason )
  {
    Main.LOGGER.log( EnumLoggerType.WARN, "Disconnected from server: " + reason );
  }

  public void handleDisconnect( Packet packetIn )
  {
    this.networkManager.closeChannel( (String) packetIn.params.get( "reason" ) );
  }

  public void handleEnableCompression( Packet packetIn )
  {
    if ( !this.networkManager.isLocalChannel() )
    {
      this.networkManager.setCompressionThreshold( (int) packetIn.params.get( "threshold" ) );
    }
  }

  @Override
  public void update()
  {

  }
}
