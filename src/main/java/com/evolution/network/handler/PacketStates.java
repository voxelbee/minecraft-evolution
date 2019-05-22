package com.evolution.network.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.evomine.decode.PacketLayout;

public class PacketStates
{
	public List<PacketLayout> packets = new ArrayList<PacketLayout>();
	public List<HashMap<Integer, Integer>> idToIndex = new ArrayList<HashMap<Integer, Integer>>();
	public List<HashMap<Integer, Integer>> indexToId = new ArrayList<HashMap<Integer, Integer>>();
	public List<HashMap<String, Integer>> nameToIndex = new ArrayList<HashMap<String, Integer>>();
	
	/**
	 * Sets up the id mapper initalizing values
	 */
	public PacketStates()
	{
		for (int i = 0; i < 2; i++)
		{
			idToIndex.add(new HashMap<Integer, Integer>());
			indexToId.add(new HashMap<Integer, Integer>());
			nameToIndex.add(new HashMap<String, Integer>());
		}
	}
}
