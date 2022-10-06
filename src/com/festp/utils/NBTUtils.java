package com.festp.utils;

import org.bukkit.NamespacedKey;
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
		NamespacedKey nameKey = new NamespacedKey(plugin, key);
		PersistentDataContainer container = stack.getItemMeta().getPersistentDataContainer();
		return container.get(nameKey, PersistentDataType.STRING);
	}
	
	public static ItemStack setString(ItemStack stack, String key, String value)
	{
		if (stack == null || !stack.hasItemMeta())
			return stack;
		NamespacedKey nameKey = new NamespacedKey(plugin, key);
		ItemMeta meta = stack.getItemMeta();
		meta.getPersistentDataContainer().set(nameKey, PersistentDataType.STRING, value);
		stack.setItemMeta(meta);
		return stack;
	}
	
	public static boolean hasString(ItemStack stack, String key)
	{
		if (stack == null || !stack.hasItemMeta())
			return false;
		NamespacedKey nameKey = new NamespacedKey(plugin, key);
		PersistentDataContainer container = stack.getItemMeta().getPersistentDataContainer();
		return container.has(nameKey, PersistentDataType.STRING);
	}

	public static ItemStack removeString(ItemStack stack, String key) {
		if (stack == null || !stack.hasItemMeta())
			return stack;
		NamespacedKey nameKey = new NamespacedKey(plugin, key);
		ItemMeta meta = stack.getItemMeta();
		meta.getPersistentDataContainer().remove(nameKey);
		stack.setItemMeta(meta);
		return stack;
	}
}
