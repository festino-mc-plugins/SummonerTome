package com.festp.tome;

import com.festp.components.ITomeComponent;

public class TomeFormatter
{
	private static final String DELIMITER = "|";
	private static final String ARG_START = "~";
	private static final char ARG_START_ESCAPING = '\\';
	private static final String ARG_START_ESCAPED = ARG_START_ESCAPING + ARG_START;
	
	public static ITomeComponent[] deserialize(String data)
	{
		int count = data.indexOf(DELIMITER);
		if (count < 0)
			throw new IllegalArgumentException("no delimiter");
		
		ITomeComponent[] components = new ITomeComponent[count];
		for (int i = 0; i < count; i++)
		{
			char c = data.charAt(i);
			components[i] = TomeType.fromChar(c).getComponent();
		}
		int length = data.length();
		int start = count + DELIMITER.length();
		for (int i = 0; i < count; i++)
		{
			int argStart = start + ARG_START.length();
			if (argStart > length || !data.substring(start, argStart).equals(ARG_START))
				throw new IllegalArgumentException("no arg start");
			int nextStart = data.indexOf(ARG_START, argStart);
			while (nextStart > 0 && data.charAt(nextStart - 1) == ARG_START_ESCAPING)
				nextStart = data.indexOf(ARG_START, nextStart);
			if (nextStart < 0)
				nextStart = length;
			
			components[i].deserialize(data.substring(argStart, nextStart).replace(ARG_START_ESCAPED, ARG_START));
			start = nextStart;
		}
		
		return components;
	}
	
	public static String serialize(ITomeComponent[] components)
	{
		StringBuilder data = new StringBuilder();
		// codes first - simple debug
		for (ITomeComponent comp : components) {
			data.append(comp.getCode());
		}
		data.append(DELIMITER);
		for (ITomeComponent comp : components) {
			data.append(ARG_START);
			data.append(comp.serialize().replace(ARG_START, ARG_START_ESCAPED));
		}
		return data.toString();
	}
}
