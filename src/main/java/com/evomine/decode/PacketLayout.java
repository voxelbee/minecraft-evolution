package com.evomine.decode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;

import com.evolution.main.Main;
import com.evolution.network.EnumConnectionState;
import com.evolution.network.EnumPacketDirection;
import com.evolution.network.handler.ILoginHandler;
import com.evolution.network.handler.INetHandler;
import com.evolution.network.handler.IPlayHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import io.netty.buffer.ByteBuf;

public class PacketLayout
{
	public EnumConnectionState avalibleState;
	public int id;
	public String name;
	
	public List<String> variableNames;
	public List<String> variableDataTypes;
	
	public LinkedHashMap<String, Object> variables;
	public JsonArray root;
	
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
		packet.root = root;
		packet.variables = new LinkedHashMap<String, Object>();
		return packet;
	}
	
	public void readPacketData(ByteBuf buf) throws IOException
    {
		decodeObject(root, null, variables, buf);
		System.out.println(variables);
    }
	
	public void decodeObject(JsonElement element, String varName, LinkedHashMap<String, Object> vars, ByteBuf buf)
	{
		if(element.isJsonArray())
		{
			String objectType = element.getAsJsonArray().get(0).getAsString();
			if (objectType.equals("container"))
			{
				JsonArray container = element.getAsJsonArray().get(1).getAsJsonArray();
				for (int i = 0; i < container.size(); i++)
				{
					decodeObject(container.get(i), null, vars, buf);
				}
			}
			else if(objectType.equals("array"))
			{
				int count = BufferUtils.readVarIntFromBuffer(buf);
				
				ArrayList<LinkedHashMap<String, Object>> array = new ArrayList<LinkedHashMap<String, Object>>();
				vars.put(varName, array);
				for (int i = 0; i < count; i++)
				{
					array.add(new LinkedHashMap<String, Object>());
					decodeObject(element.getAsJsonArray().get(1).getAsJsonObject().get("type"), null, array.get(array.size() - 1), buf);
				}
			}
			else if(objectType.equals("switch"))
			{
				String comparitorVarName = element.getAsJsonArray().get(1).getAsJsonObject().get("compareTo").getAsString();
				String valueToCompare = String.valueOf(vars.get(comparitorVarName));
				JsonElement selectedObject = element.getAsJsonArray().get(1).getAsJsonObject().get("fields").getAsJsonObject().get(valueToCompare);
				if (selectedObject != null)
				{
					decodeObject(selectedObject, varName, vars, buf);
				}
			}
		}
		else if(element.isJsonObject())
		{
			JsonElement nameElement = element.getAsJsonObject().get("name");
			if (nameElement != null)
			{
				decodeObject(element.getAsJsonObject().get("type"), nameElement.getAsString(), vars, buf);
			}
			else
			{
				decodeObject(element.getAsJsonObject().get("type"), null, vars, buf);
			}
		}
		else if (element.isJsonPrimitive())
		{
			readType(element.getAsString(), varName, vars, buf);
		}
	}
	
	public void readType(String type, String name, LinkedHashMap<String, Object> vars, ByteBuf buf)
	{
		
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
		else if(name.equals("packet_login"))
		{
			((IPlayHandler)handler).handleJoinGame(this);
		}
    }
}
