package com.festp.utils;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class NBTUtils
{
	private static JavaPlugin plugin;
	
	public static void setPlugin(JavaPlugin plugin)
	{
		NBTUtils.plugin = plugin;
	}
	
	public static String getString(ItemStack stack, String key)
	{
		if (stack == null || !stack.hasItemMeta())
			return null;
		return getString(stack.getItemMeta().getPersistentDataContainer(), key);
	}
	
	public static ItemStack setString(ItemStack stack, String key, String value)
	{
		if (stack == null || !stack.hasItemMeta())
			return stack;
		ItemMeta meta = stack.getItemMeta();
		setString(meta.getPersistentDataContainer(), key, value);
		stack.setItemMeta(meta);
		return stack;
	}
	
	public static boolean hasString(ItemStack stack, String key)
	{
		if (stack == null || !stack.hasItemMeta())
			return false;
		return hasString(stack.getItemMeta().getPersistentDataContainer(), key);
	}

	public static ItemStack removeString(ItemStack stack, String key) {
		if (stack == null || !stack.hasItemMeta())
			return stack;
		ItemMeta meta = stack.getItemMeta();
		removeString(meta.getPersistentDataContainer(), key);
		stack.setItemMeta(meta);
		return stack;
	}
	
	public static String getString(Entity entity, String key)
	{
		if (entity == null)
			return null;
		return getString(entity.getPersistentDataContainer(), key);
	}
	
	public static void setString(Entity entity, String key, String value)
	{
		if (entity == null)
			return;
		setString(entity.getPersistentDataContainer(), key, value);
	}
	
	public static boolean hasString(Entity entity, String key)
	{
		if (entity == null)
			return false;
		return hasString(entity.getPersistentDataContainer(), key);
	}
	
	private static String getString(PersistentDataContainer container, String key)
	{
		NamespacedKey nameKey = new NamespacedKey(plugin, key);
		return container.get(nameKey, PersistentDataType.STRING);
	}
	
	private static void setString(PersistentDataContainer container, String key, String value)
	{
		NamespacedKey nameKey = new NamespacedKey(plugin, key);
		container.set(nameKey, PersistentDataType.STRING, value);
	}
	
	private static boolean hasString(PersistentDataContainer container, String key)
	{
		NamespacedKey nameKey = new NamespacedKey(plugin, key);
		return container.has(nameKey, PersistentDataType.STRING);
	}
	
	private static void removeString(PersistentDataContainer container, String key)
	{
		NamespacedKey nameKey = new NamespacedKey(plugin, key);
		container.remove(nameKey);
	}
}
