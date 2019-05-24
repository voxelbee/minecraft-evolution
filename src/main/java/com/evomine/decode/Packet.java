package com.evomine.decode;

import java.util.LinkedHashMap;
import com.evolution.network.handler.INetHandler;

public class Packet
{
	public String name;
	public LinkedHashMap<String, Object> variables;
	
	public Packet()
	{
		this.variables = new LinkedHashMap<String, Object>();
	}
	
	public void processPacket(INetHandler handler)
	{
		
	}
}
