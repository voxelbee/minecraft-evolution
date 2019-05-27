package com.evomine.decode;

import java.util.LinkedHashMap;
import java.util.Map;

import com.evolution.network.EnumConnectionState;
import com.evolution.network.handler.ILoginHandler;
import com.evolution.network.handler.INetHandler;
import com.evolution.network.handler.IPlayHandler;

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
      ( (ILoginHandler) handler ).handleEnableCompression( this );
    }
    else if ( name.equals( "disconnect" ) )
    {
      ( (ILoginHandler) handler ).handleDisconnect( this );
    }
    else if ( name.equals( "success" ) )
    {
      ( (ILoginHandler) handler ).handleLoginSuccess( this );
    }
    else if ( name.equals( "login" ) )
    {
      ( (IPlayHandler) handler ).handleJoinGame( this );
    }
    else if ( name.equals( "keep_alive" ) )
    {
      ( (IPlayHandler) handler ).handleKeepAlive( this );
    }
  }
}
