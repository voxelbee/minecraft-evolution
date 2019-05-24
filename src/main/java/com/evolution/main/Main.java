package com.evolution.main;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.util.LinkedHashMap;

import com.evolution.network.EnumConnectionState;
import com.evolution.network.EnumPacketDirection;
import com.evolution.network.handler.LoginHandler;
import com.evolution.network.handler.NettyManager;
import com.evomine.decode.BufferUtils;
import com.evomine.decode.PacketLayout;
import com.evomine.decode.Protocol;
import com.google.gson.Gson;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class Main
{
	public static final Logger LOGGER = new Logger();
	
	public static final Gson GSON = new Gson();
	public static final Protocol PROTOCOL = new Protocol("1.12");
	
	public static void main(String[] args) throws IOException
	{
		byte[] fileContent = Files.readAllBytes((new File("C:/tmp/buffers/buffer1")).toPath());
		ByteBuf buf = Unpooled.wrappedBuffer(fileContent);
		
		LinkedHashMap<String, Object> vars = new LinkedHashMap<String, Object>();
		PROTOCOL.decodeBuffer(buf, vars, EnumConnectionState.LOGIN);
		
		//String ip = "localhost";
		//int port = 25565;
		//NettyManager manager = NettyManager.createNetworkManagerAndConnect(InetAddress.getByName(ip), port, true);
		//manager.setNetHandler(new LoginHandler(manager));
		
		//PacketLayout C00Handshake = PROTOCOL.getPacketFromName(EnumConnectionState.HANDSHAKING, "packet_set_protocol", EnumPacketDirection.SERVERBOUND).createReadWritePacket();
		//C00Handshake.variables.put("protocolVersion", PROTOCOL.getProtocol());
		//C00Handshake.variables.put("serverHost", ip);
		//C00Handshake.variables.put("serverPort", (short)port);
		//C00Handshake.variables.put("nextState", EnumConnectionState.LOGIN.getId());
		//manager.sendPacket(C00Handshake);
		
		//PacketLayout CLogin = PROTOCOL.getPacketFromName(EnumConnectionState.LOGIN, "packet_login_start", EnumPacketDirection.SERVERBOUND).createReadWritePacket();
		//CLogin.variables.put("username", "jim");
		//manager.sendPacket(CLogin);
		
		//while(true)
		//{
		//	try
		//	{
		//		Thread.sleep(100);
		//	}
		//	catch (InterruptedException e)
		//	{
		//		e.printStackTrace();
		//	}
		//}
	}
	}
