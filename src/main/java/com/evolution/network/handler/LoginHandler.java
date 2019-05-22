package com.evolution.network.handler;

import com.evolution.network.EnumConnectionState;
import com.evomine.decode.PacketLayout;

public class LoginHandler implements ILoginHandler
{    
    private final NettyManager networkManager;

    public LoginHandler(NettyManager networkManagerIn)
    {
        this.networkManager = networkManagerIn;
    }

    public void handleLoginSuccess(PacketLayout packetIn)
    {
        this.networkManager.setConnectionState(EnumConnectionState.PLAY);
        this.networkManager.setNetHandler(new PlayHandler(this.networkManager));
    }

    /**
     * Invoked when disconnecting, the parameter is a ChatComponent describing the reason for termination
     */
    public void onDisconnect(String reason)
    {
    	System.out.println("Disconnected from server: " + reason);
    }

    public void handleDisconnect(PacketLayout packetIn)
    {
        this.networkManager.closeChannel((String)packetIn.variables.get("reason"));
    }

    public void handleEnableCompression(PacketLayout packetIn)
    {
        if (!this.networkManager.isLocalChannel())
        {
            this.networkManager.setCompressionThreshold((int)packetIn.variables.get("threshold"));
        }
    }
}
