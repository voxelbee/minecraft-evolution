package com.evolution.network.handler;

import com.evomine.decode.Packet;

public interface ILoginHandler extends INetHandler
{
  void handleLoginSuccess( Packet packetIn );

  void handleDisconnect( Packet packetIn );

  void handleEnableCompression( Packet packetIn );
}
