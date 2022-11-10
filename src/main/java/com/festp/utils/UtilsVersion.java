package com.festp.utils;

import org.bukkit.Bukkit;

public class UtilsVersion
{
	private static final int VERSION = initVersion();
	public static final boolean SUPPORTS_STRIDER = UtilsVersion.GetVersion() >= 11600;
	public static final boolean SUPPORTS_CHEST_AND_MANGROVE = UtilsVersion.GetVersion() >= 11900;

	/** format is 11902 for 1.19.2, 10710 for 1.7.10*/
	private static int GetVersion()
	{
		return VERSION;
	}
	
	private static int initVersion()
	{
		String[] split = Bukkit.getBukkitVersion().split("-")[0].split("\\.");
		String majorVer = split[0]; //For 1.10 will be "1"
		String minorVer = split[1]; //For 1.10 will be "10"
		String minorVer2 = split.length > 2 ? split[2]: "0"; //For 1.10 will be "0", for 1.9.4 will be "4"
		int vMajor = Integer.parseUnsignedInt(majorVer);
		int vMinor = Integer.parseUnsignedInt(minorVer);
		int vMinor2 = Integer.parseUnsignedInt(minorVer2);
		return vMinor2 + vMinor * 100 + vMajor * 10000;
	}
}
