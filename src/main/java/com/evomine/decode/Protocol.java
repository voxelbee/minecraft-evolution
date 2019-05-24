package com.evomine.decode;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;

import com.evolution.main.EnumLoggerType;
import com.evolution.main.Main;
import com.evolution.network.EnumConnectionState;
import com.google.gson.JsonObject;

import io.netty.buffer.ByteBuf;

public class Protocol
{
	private JsonObject protocol;
	private int protocolVersion;
	private String mcVersion;
	
	/**
	 * Creates a new protocol manager with the specified minecraft version
	 * @param mcVersion
	 * @throws FileNotFoundException 
	 */
	public Protocol(String inMcVersion)
	{
		this.mcVersion = inMcVersion; 
		Main.LOGGER.log(EnumLoggerType.INFO, "Setting up the protocol for Minecraft version " + this.mcVersion);
		
		try
		{
			JsonObject root = loadJson("protocol/dataPaths.json");
			String versionLocation = root.getAsJsonObject("pc").getAsJsonObject(this.mcVersion).get("version").getAsString() + "/version.json";
			String protocolLocation = root.getAsJsonObject("pc").getAsJsonObject(this.mcVersion).get("protocol").getAsString() + "/protocol.json";
			
			this.protocolVersion = loadJson("protocol/" + versionLocation).get("version").getAsInt();
			this.protocol = loadJson("protocol/" + protocolLocation);
		}
		catch (Exception e1)
		{
			Main.LOGGER.log(EnumLoggerType.ERROR, "Invalid minecraft version no protocol data found: " + this.mcVersion);
			e1.printStackTrace();
			System.exit(0);
		}
		
		Main.LOGGER.log(EnumLoggerType.INFO, "Prococol version loaded: " + this.protocolVersion);
		
		JsonObject nbts = loadNbt();
		JsonObject types = protocol.get("types").getAsJsonObject();
		
		for (String key : nbts.keySet())
		{
			types.add(key, nbts.get(key));
		}
		types.addProperty("optionalNbt", "nbt");
		Main.LOGGER.log(EnumLoggerType.INFO, "Loaded all data types for protocol");
	}
	
	private JsonObject loadNbt()
	{
		try
		{
			return loadJson("protocol/nbt.json");
		}
		catch (Exception e1)
		{
			Main.LOGGER.log(EnumLoggerType.ERROR, "Could not load nbt protocol data: " + this.mcVersion);
			e1.printStackTrace();
			System.exit(0);
		}
		return null;
	}
	
	/**
	 * Loads the {@link JsonObject} into the class
	 * @param path - File location of the json
	 * @throws FileNotFoundException
	 */
	private JsonObject loadJson(String path) throws FileNotFoundException
	{
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream is = classloader.getResourceAsStream(path);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        return Main.GSON.fromJson(bufferedReader, JsonObject.class);
	}

	public int getVersion()
	{
		return this.protocolVersion;
	}
	
	public JsonObject getProtocol()
	{
		return this.protocol;
	}
	
	public void decodeBuffer(ByteBuf buf, Map<String, Object> vars, EnumConnectionState state)
	{
		PacketDeserializer.deserialize(protocol, vars, buf, state);
	}
}
