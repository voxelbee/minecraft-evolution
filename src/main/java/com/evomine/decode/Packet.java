package com.evomine.decode;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.evolution.network.EnumConnectionState;
import com.evolution.network.handler.INetHandler;

public class Packet
{
	public String name;
	private LinkedHashMap<String, Object> variables;
	
	public Packet(String name)
	{
		this.name = name;
		this.variables = new LinkedHashMap<String, Object>();
	}
	
	public void addVariable(String name, Object value)
	{
		if (this.variables != null)
		{
			this.variables.put(name, value);
		}
		else
		{
			this.variables = new LinkedHashMap<String, Object>();
			this.variables.put(name, value);
		}
	}
	
	public Object getVariable(String name)
	{
		Object value = this.variables.get(name);
		if (value == null)
		{
			System.out.println("Could not find variable " + name + " in packet " + this.name);
		}
		return value;
	}
	
	public void setVariables(LinkedHashMap<String, Object> invariables)
	{
		this.variables = invariables;
	}
	
	public LinkedHashMap<String, Object> getVariables()
	{
		return this.variables;
	}
	
	public void processPacket(INetHandler handler)
	{
		
	}
}
