package com.evomine.decode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.evolution.network.EnumConnectionState;
import com.evolution.network.handler.ILoginHandler;
import com.evolution.network.handler.INetHandler;

import io.netty.buffer.ByteBuf;

public class PacketLayout
{
	public EnumConnectionState avalibleState;
	public int id;
	public String name;
	
	public List<String> variableNames;
	public List<String> variableDataTypes;
	
	public Map<String, Object> variables;
	
	public PacketLayout(int id, String name, EnumConnectionState state, List<String> names, List<String> dataTypes)
	{
		this.id = id;
		this.avalibleState = state;
		this.variableNames = names;
		this.variableDataTypes = dataTypes;
		this.name = name;
	}
	
	public PacketLayout createReadWritePacket()
	{
		PacketLayout packet = new PacketLayout(id, name, avalibleState, variableNames, variableDataTypes);
		packet.variables = new LinkedHashMap<String, Object>();
		return packet;
	}
	
	public void readPacketData(ByteBuf buf) throws IOException
    {
		int count = 0;
		for (String type : variableDataTypes)
		{
			if (type.equals("varint"))
			{
				variables.put(variableNames.get(count), BufferUtils.readVarIntFromBuffer(buf));
			}
			else if(type.equals("string"))
			{
				variables.put(variableNames.get(count), BufferUtils.readStringFromBuffer(buf, 32767));
			}
			count++;
		}
    }
	
	public void writePacketData(ByteBuf buf) throws IOException
    {
		Iterator<String> keys = this.variables.keySet().iterator();
		for (String type : variableDataTypes)
		{
			if (type.equals("varint"))
			{
				BufferUtils.writeVarIntToBuffer(buf, (int)this.variables.get(keys.next()));
			}
			else if(type.equals("string"))
			{
				BufferUtils.writeString(buf, (String)this.variables.get(keys.next()));
			}
			else if(type.equals("u16"))
			{
				buf.writeShort((short)this.variables.get(keys.next()));
			}
		}
    }
	
	public void processPacket(INetHandler handler)
    {
		if (name.equals("packet_compress"))
		{
			((ILoginHandler)handler).handleEnableCompression(this);
		}
		else if(name.equals("packet_disconnect"))
		{
			((ILoginHandler)handler).handleDisconnect(this);
		}
		else if(name.equals("packet_success"))
		{
			((ILoginHandler)handler).handleLoginSuccess(this);
		}
    }
}
