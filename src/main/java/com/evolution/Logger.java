package com.evolution;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger
{
	private static final Date DATE = new Date();
	private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("[dd-MM-yyyy HH:mm:ss]");  
	
	public void log(EnumLoggerType type, String message)
	{
		String finalMessage = FORMATTER.format(DATE) + " [" + type.toString() + "] " + message;
		System.out.println(finalMessage);  
	}
}
