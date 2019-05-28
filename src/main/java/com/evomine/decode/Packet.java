package com.evomine.decode;

import java.util.LinkedHashMap;
import java.util.Map;

import com.evolution.network.EnumConnectionState;
import com.evolution.network.handler.INetHandler;
import com.evolution.network.handler.LoginHandler;
import com.evolution.network.handler.PlayHandler;

public class Packet
{
  public String name;
  public EnumConnectionState state;
  public Map< String, Object > params;

  public Packet( Map< String, Object > vars )
  {
    this.params = (Map< String, Object >) vars.get( "params" );
    this.name = (String) vars.get( "name" );
  }

  public Packet( String name, EnumConnectionState state )
  {
    this.params = new LinkedHashMap< String, Object >();
    this.name = name;
    this.state = state;
  }

  public void processPacket( INetHandler handler )
  {
    if ( name.equals( "compress" ) )
    {
      ( (LoginHandler) handler ).handleEnableCompression( this );
    }
    else if ( name.equals( "disconnect" ) )
    {
      ( (LoginHandler) handler ).handleDisconnect( this );
    }
    else if ( name.equals( "success" ) )
    {
      ( (LoginHandler) handler ).handleLoginSuccess( this );
    }
    else if ( name.equals( "login" ) )
    {
      ( (PlayHandler) handler ).handleJoinGame( this );
    }
    else if ( name.equals( "keep_alive" ) )
    {
      ( (PlayHandler) handler ).handleKeepAlive( this );
    }
    else if ( name.equals( "position" ) )
    {
      ( (PlayHandler) handler ).handlePosition( this );
    }
    else if ( name.equals( "update_health" ) )
    {
      ( (PlayHandler) handler ).handleUpdateHealth( this );
    }
    else if ( name.equals( "packet_abilities" ) )
    {
      ( (PlayHandler) handler ).handlePlayerAbilities( this );
    }
  }
}
