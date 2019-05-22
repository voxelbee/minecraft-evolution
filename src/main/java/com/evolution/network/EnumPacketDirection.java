package com.evolution.network;

public enum EnumPacketDirection
{
    SERVERBOUND("toServer", 0),
    CLIENTBOUND("toClient", 1);
	private String name;
	private int id;
	
	private EnumPacketDirection(String name, int id)
	{
		this.name = name;
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
