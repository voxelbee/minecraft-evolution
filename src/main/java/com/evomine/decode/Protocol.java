package com.evomine.decode;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.evolution.network.EnumConnectionState;
import com.evolution.network.EnumPacketDirection;
import com.evolution.network.handler.PacketStates;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import come.evolution.main.Main;

public class Protocol
{
	private JsonObject protocolObject;
	private List<PacketStates> allPackets = new ArrayList<PacketStates>();
	private int protocol;
	private String mcVersion;
	private static final String BASE_URL = "https://raw.githubusercontent.com/PrismarineJS/minecraft-data/master/data/"; 
	
	/**
	 * Creates a new protocol manager with the specified minecraft version
	 * @param mcVersion
	 */
	public Protocol(String inMcVersion)
	{
		this.mcVersion = inMcVersion; 
		System.out.println("Setting up the protocol for Minecraft version " + this.mcVersion);
		
		JsonObject root = loadFromUrl("dataPaths.json");
		String versionLocation = null;
		String protocolLocation = null;
		
		try
		{
			versionLocation = root.getAsJsonObject("pc").getAsJsonObject(this.mcVersion).get("version").getAsString() + "/version.json";
			protocolLocation = root.getAsJsonObject("pc").getAsJsonObject(this.mcVersion).get("protocol").getAsString() + "/protocol.json";
		}
		catch(Exception e)
		{
			System.out.println("Invalid minecraft version no protocol data found: " + this.mcVersion);
			System.exit(0);
		}
		
		this.protocol = loadFromUrl(versionLocation).get("version").getAsInt();
		this.protocolObject = loadFromUrl(protocolLocation);
		
		for (int i = 0; i < EnumConnectionState.values().length; i++)
		{
			allPackets.add(new PacketStates());
		}
		
		reloadPackets();
	}
	
	private JsonObject loadFromUrl(String surl)
	{
		try
		{
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new URL(BASE_URL + surl).openStream()));
			return Main.GSON.fromJson(bufferedReader, JsonObject.class);
		}
		catch (MalformedURLException e)
		{
			System.out.println("Incorrect url format: " + BASE_URL + surl);
			e.printStackTrace();
		}
		catch (IOException e)
		{
			System.out.println("Could not load URL: " + BASE_URL + surl);
			e.printStackTrace();
		}
		return null;
	}
	
	public void reloadPackets()
	{
		System.out.println("Reloading packets...");
		for (EnumConnectionState state : EnumConnectionState.values())
		{
			this.loadPackets(state);
		}
	}
	
	/**
	 * Loads the {@link JsonObject} into the class
	 * @param path - File location of the json
	 * @throws FileNotFoundException
	 */
	private JsonObject loadProtocol(String path) throws FileNotFoundException
	{
        BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
        return Main.GSON.fromJson(bufferedReader, JsonObject.class);
	}
	
	/**
	 * Loads in the packets for a specific state (loads server and client packets)
	 * @param state - State to load packets from
	 */
	public void loadPackets(EnumConnectionState state)
	{
		System.out.println("Loading " + state + " packets");
		
		for (EnumPacketDirection direction : EnumPacketDirection.values())
		{
			JsonObject packetNames = this.protocolObject.getAsJsonObject(state.getAsString())
		    		.getAsJsonObject(direction.getAsString())
		    		.getAsJsonObject("types")
		    		.getAsJsonArray("packet")
		    		.get(1).getAsJsonArray()
		    		.get(0).getAsJsonObject()
		    		.getAsJsonArray("type")
		    		.get(1).getAsJsonObject()
		    		.getAsJsonObject("mappings");
			
			JsonObject packetsData = this.protocolObject.getAsJsonObject(state.getAsString())
		    		.getAsJsonObject(direction.getAsString())
		    		.getAsJsonObject("types");
			
		    Set<String> keys = packetNames.keySet();
		    
    	    for (String string : keys)
    	    {
    	    	int packetId = Integer.decode(string);
    	    	String packetName = "packet_" + packetNames.get(string).getAsString();
    	    	
    	    	JsonArray data = packetsData.getAsJsonArray(packetName)
    		    		.get(1).getAsJsonArray();
    	    	
    	    	List<String> names = new ArrayList<String>();
    	    	List<String> types = new ArrayList<String>();
    	    	
    	    	for (int i = 0; i < data.size(); i++)
    	    	{
    	    		try
    	    		{
        	    		names.add(data.get(i).getAsJsonObject().get("name").getAsString());
    	    		}
    	    		catch(NullPointerException e)
    	    		{
    	    			System.out.println("Name field not found: " + data.get(i).getAsJsonObject());
    	    		}
    	    		try
    	    		{
    	    			types.add(data.get(i).getAsJsonObject().get("type").getAsString());
    	    		}
    	    		catch (IllegalStateException e)
    	    		{
    	    			System.out.println("Unkown data type: " + data.get(i).getAsJsonObject().get("type"));
    	    			types.add(null);
					}
				}
    	    	
    	    	PacketLayout layout = new PacketLayout(packetId, packetName, state, names, types);
    	    	this.allPackets.get(state.getId() + 1).packets.add(layout);
    		    
    	    	this.allPackets.get(state.getId() + 1).idToIndex.get(direction.getId()).put(packetId, this.allPackets.get(state.getId() + 1).packets.size() - 1);
    	    	this.allPackets.get(state.getId() + 1).indexToId.get(direction.getId()).put(this.allPackets.get(state.getId() + 1).packets.size() - 1, packetId);
    	    	this.allPackets.get(state.getId() + 1).nameToIndex.get(direction.getId()).put(packetName, this.allPackets.get(state.getId() + 1).packets.size() - 1);
    		}
		}
		System.out.println("Done loading " + state + " packets");
	}
	
	public PacketLayout getPacketFromId(EnumConnectionState state, int id)
	{
		return this.allPackets.get(state.getId() + 1).packets.get(this.allPackets.get(state.getId() + 1).idToIndex.get(EnumPacketDirection.CLIENTBOUND.getId()).get(id));
	}
	
	/**
	 * Loads packet from the name specified and the connection state. If the current state is not loaded will
	 * load in the inputed state
	 * @param state - The state that the packet is part of
	 * @param name - Name of the packet
	 * @return
	 */
	public PacketLayout getPacketFromName(EnumConnectionState state, String name)
	{
		try
		{
			return this.allPackets.get(state.getId() + 1).packets.get(this.allPackets.get(state.getId() + 1).nameToIndex.get(EnumPacketDirection.SERVERBOUND.getId()).get(name));
		}
		catch(IndexOutOfBoundsException e)
		{
			System.out.println("Could not find packet: " + name + " in state: " + state);
			return null;
		}
	}

	public int getProtocol()
	{
		return protocol;
	}
}
