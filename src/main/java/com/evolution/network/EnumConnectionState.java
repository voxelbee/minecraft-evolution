package com.evolution.network;

public enum EnumConnectionState
{
	HANDSHAKING("handshaking", -1),
    PLAY("play", 0),
    STATUS("status", 1),
    LOGIN("login", 2);
	
	private String name;
	private int id;
	 
	EnumConnectionState(String envName, int id)
	{
        this.name = envName;
        this.id = id;
    }
 
    public String getAsString()
    {
        return name;
    }
    
    public int getId()
    {
    	return id;
    }
}
