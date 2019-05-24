package com.evomine.decode;

import java.util.LinkedHashMap;

import com.evolution.network.EnumConnectionState;
import com.evolution.network.EnumPacketDirection;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import io.netty.buffer.ByteBuf;

public class PacketDeserializer
{
    public static void deserialize(JsonElement json,
    		LinkedHashMap<String, Object> vars,
    		ByteBuf buf,
    		EnumConnectionState state) throws JsonParseException
	{
    	packetDesrialize(json.getAsJsonObject().getAsJsonObject(state.getAsString()).getAsJsonObject(EnumPacketDirection.CLIENTBOUND.getAsString()), vars, buf);
    }
    
    private static void packetDesrialize(JsonElement json,
    		LinkedHashMap<String, Object> vars,
    		ByteBuf buf) throws JsonParseException
    {
    	if (json.getAsJsonObject().has("types"))
    	{
			
		}
    	System.out.println(json);
    }
}
