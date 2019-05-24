package com.evomine.decode;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class PacketDeserializer implements JsonDeserializer<Packet>
{
	@Override
    public Packet deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
        Packet packet = new Packet("name");
        return packet;
    }
}
