package com.festp.utils;

import java.text.DecimalFormat;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import com.festp.Logger;

public class Utils
{
	private static final String CRAFTBUKKIT_PACKAGE = "org.bukkit.craftbukkit.";
	
	private static Attribute MAX_HEALTH_ATTRIBUTE = initAttribute("MAX_HEALTH");
	private static Attribute MOVEMENT_SPEED_ATTRIBUTE = initAttribute("MOVEMENT_SPEED");
	private static Particle ENCHANT_PARTICLE = initParticle(new String[] { "ENCHANT", "ENCHANTMENT_TABLE" });
	
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
		if (!name.startsWith(CRAFTBUKKIT_PACKAGE)) {
			String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
		    name = CRAFTBUKKIT_PACKAGE + version + "." + name;
		}
		
		try {
			return Class.forName(name);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
	public static String getShortBukkitClass(Class<?> clazz) {
		String fullName = clazz.getName();
		if (!fullName.startsWith(CRAFTBUKKIT_PACKAGE)) {
			return fullName;
		}
		String name = fullName.substring(CRAFTBUKKIT_PACKAGE.length());
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

	public static boolean containsIgnoreCase(String[] args, String str) {
		for (String s : args)
			if (s.equalsIgnoreCase(str))
				return true;
		return false;
	}
	
	public static boolean checkHotbar(Player player, Material m) {
		PlayerInventory inv = player.getInventory();
		ItemStack item = inv.getItemInOffHand();
		if (item != null && item.getType() == m)
			return true;
		for (int i = 0; i < 9; i++) {
			item = inv.getItem(i);
			if (item != null && item.getType() == m)
				return true;
		}
		return false;
	}
	
	public static Attribute getMaxHealthAttribute() {
		return MAX_HEALTH_ATTRIBUTE;
	}
	
	public static Attribute getMovementSpeedAttribute() {
		return MOVEMENT_SPEED_ATTRIBUTE;
	}
	
	public static Particle getEnchantParticle() {
		return ENCHANT_PARTICLE;
	}
	
	private static Attribute initAttribute(String name) {
		if (Attribute.class.getEnumConstants() == null) {
			return Attribute.MAX_HEALTH;
		}
		// Attribute was an enum before 1.21
		for (Attribute attribute : Attribute.class.getEnumConstants()) {
			if (attribute.toString() == name)
				return attribute;
		}
		return null;
	}
	
	private static Particle initParticle(String[] names) {
		for (Particle particle : Particle.class.getEnumConstants()) {
			for (String name : names) {
				if (particle.toString() == name)
					return particle;
			}
		}
		return null;
	}
}
