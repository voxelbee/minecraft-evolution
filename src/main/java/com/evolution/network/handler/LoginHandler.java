package com.evolution.network.handler;

import com.evolution.network.EnumConnectionState;
import com.evomine.decode.Packet;

public class LoginHandler implements ILoginHandler
{
  private final NettyManager networkManager;

  public LoginHandler( NettyManager networkManagerIn )
  {
    this.networkManager = networkManagerIn;
  }

  @Override
  public void handleLoginSuccess( Packet packetIn )
  {
    this.networkManager.setConnectionState( EnumConnectionState.PLAY );
    this.networkManager.setNetHandler( new PlayHandler( this.networkManager ) );
  }

  /**
   * Invoked when disconnecting, the parameter is a ChatComponent describing the reason for termination
   */
  @Override
  public void onDisconnect( String reason )
  {
    System.out.println( "Disconnected from server: " + reason );
  }

  @Override
  public void handleDisconnect( Packet packetIn )
  {
    this.networkManager.closeChannel( (String) packetIn.params.get( "reason" ) );
  }

  @Override
  public void handleEnableCompression( Packet packetIn )
  {
    if ( !this.networkManager.isLocalChannel() )
    {
      this.networkManager.setCompressionThreshold( (int) packetIn.params.get( "threshold" ) );
    }
  }
}
