package com.festp.tome;

import com.festp.components.ITomeComponent;

public class TomeSerializer
{
	private static final String CODE_DELIMITER = ", ";
	private static final String CODE_DATA_DELIMITER = "|";
	private static final String ARG_START = "\n";
	private static final char ARG_START_ESCAPING = '\\';
	private static final String ARG_START_ESCAPED = ARG_START_ESCAPING + ARG_START;
	
	private static ComponentManager componentManager;
	public static void setComponentManager(ComponentManager componentManager) {
		TomeSerializer.componentManager = componentManager;
	}
	
	public static ITomeComponent[] deserialize(String data)
	{
		int codesEnd = data.indexOf(CODE_DATA_DELIMITER);
		if (codesEnd < 0)
			throw new IllegalArgumentException("no CODE_DATA_DELIMITER");
		
		String[] codes = data.substring(0, codesEnd).split(CODE_DELIMITER);
		int count = codes.length;
		
		ITomeComponent[] components = new ITomeComponent[count];
		for (int i = 0; i < count; i++)
		{
			String code = codes[i];
			components[i] = componentManager.fromCode(code);
		}
		int length = data.length();
		int start = codesEnd + CODE_DATA_DELIMITER.length();
		for (int i = 0; i < count; i++)
		{
			int argStart = start + ARG_START.length();
			if (argStart > length || !data.substring(start, argStart).equals(ARG_START))
				throw new IllegalArgumentException("no ARG_START");
			int nextStart = data.indexOf(ARG_START, argStart);
			while (nextStart > 0 && data.charAt(nextStart - 1) == ARG_START_ESCAPING)
				nextStart = data.indexOf(ARG_START, nextStart + 1);
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
		for (int i = 0; i < components.length; i++) {
			if (i != 0)
				data.append(CODE_DELIMITER);
			ITomeComponent comp = components[i];
			data.append(comp.getCode());
		}
		data.append(CODE_DATA_DELIMITER);
		for (ITomeComponent comp : components) {
			data.append(ARG_START);
			data.append(comp.serialize().replace(ARG_START, ARG_START_ESCAPED));
		}
		return data.toString();
	}
	
	public static boolean canSerialize(String code) {
		return !code.contains(CODE_DELIMITER) && !code.contains(CODE_DATA_DELIMITER);
	}
}
