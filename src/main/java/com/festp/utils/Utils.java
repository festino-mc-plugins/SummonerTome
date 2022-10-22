package com.festp.utils;

import java.text.DecimalFormat;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import com.festp.Logger;

public class Utils
{
	private static final String BUKKIT_PACKAGE = "org.bukkit.craftbukkit.";
	public static int VERSION = initVersion();

	/** format is 11902 for 1.19.2, 10710 for 1.7.10*/
	public static int GetVersion()
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
	
	public static void printError(String msg) {
		Logger.severe(msg);
	}

	public static void printStackTracePeak(Exception e, int n) {
		String error = "";
		StackTraceElement[] elems = e.getStackTrace();
		for (int i = 0; i < elems.length && i < n; i++) {
			error += elems[i].toString() + "\n";
		}
		printError(error);
	}
	
	public static String toString(Vector v) {
		if (v == null)
			return "(null)";
		DecimalFormat dec = new DecimalFormat("#0.00");
		return ("("+dec.format(v.getX())+"; "
				  +dec.format(v.getY())+"; "
				  +dec.format(v.getZ())+")")
				.replace(',', '.');
	}
	public static String toString(Location l) {
		if (l == null) return toString((Vector)null);
		return toString(new Vector(l.getX(), l.getY(), l.getZ()));
	}
	public static String toString(Block b) {
		if (b == null) return toString((Location)null);
		return toString(b.getLocation());
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack getHead(String headName, String texture_url) {
		ItemStack stack = new ItemStack(Material.PLAYER_HEAD);
		stack = Bukkit.getUnsafe().modifyItemStack(stack,
				"{SkullOwner:{Id:" + UUID.randomUUID().toString() + ",Properties:{textures:[{Value:\"" + texture_url + "\"}]}}}");
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(headName);
		stack.setItemMeta(meta);
		return stack;
	}
	
	/** format: "entity.CraftHorse" or "org.bukkit.craftbukkit.v1_18_R1.entity.CraftHorse" */
	public static Class<?> getBukkitClass(String name) {
		if (!name.startsWith(BUKKIT_PACKAGE)) {
			String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
		    name = BUKKIT_PACKAGE + version + "." + name;
		}
		
		try {
			return Class.forName(name);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
	public static String getShortBukkitClass(Class<?> clazz) {
		String fullName = clazz.getName();
		if (!fullName.startsWith(BUKKIT_PACKAGE)) {
			return fullName;
		}
		String name = fullName.substring(BUKKIT_PACKAGE.length());
		return name.substring(name.indexOf(".") + 1);
	}
	
	public static boolean containsAllOf(String str, String... args) {
		for(String s : args)
			if(!str.contains(s))
				return false;
		return true;
	}
	
	public static boolean contains(Object[] list, Object find) {
		for (Object m : list)
			if (m == find)
				return true;
		return false;
	}
}
