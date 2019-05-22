package com.evolution.network.handler;

import com.evomine.decode.PacketLayout;

public interface ILoginHandler extends INetHandler
{
    void handleLoginSuccess(PacketLayout packetIn);

    void handleDisconnect(PacketLayout packetIn);

    void handleEnableCompression(PacketLayout packetIn);
}