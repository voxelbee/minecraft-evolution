package com.evolution.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import com.evolution.main.Main;
import com.evolution.network.EnumConnectionState;
import com.evomine.decode.Protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class BufferDecoderTest
{	
	@Test
	public void testDecodeBuffers() throws IOException, URISyntaxException
	{
		Main.dummy();
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		
		for (int i = 1; i < 2; i++)
		{
			URI path = classloader.getResource("buffers/buffer" + i).toURI();
			if (i < 2)
			{
				decode(path, EnumConnectionState.LOGIN);
			}
			else
			{
				decode(path, EnumConnectionState.PLAY);
			}
		}
	}
	
	private void decode(URI path, EnumConnectionState state) throws IOException
	{
		byte[] fileContent = Files.readAllBytes(Paths.get(path));
		ByteBuf buf = Unpooled.wrappedBuffer(fileContent);
		
		LinkedHashMap<String, Object> vars = new LinkedHashMap<String, Object>();
		Main.PROTOCOL.decodeBuffer(buf, vars, state);
	}
}
