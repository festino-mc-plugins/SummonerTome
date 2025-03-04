package com.festp.components.boat;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.festp.inventory.InventorySerializer;
import com.festp.utils.UtilsVersion;

/**
 * Serializes, deserializes and stores boat data. Also applies data to boats.<br/>
 * 
 * serialized format: material char ('o' is for oak etc), chest char ('0'/'1'), inventory yaml (if has chest)<br/>
 * examples: "b0", "j1slots: 27\n" 
 */
public class BoatData
{
	private static final char UNDEFINED_MATERIAL = '?';
	
	Material boatMaterial = Material.OAK_BOAT;
	boolean hasChest = false;
	ItemStack[] inventory = new ItemStack[0];

	@Override
	public String toString() {
		char materialChar = materialToChar(boatMaterial);
		if (materialChar == UNDEFINED_MATERIAL)
			throw new IllegalStateException("corrupted boat material");

		StringBuilder res = new StringBuilder();
		res.append(materialChar);
		res.append(hasChest ? '1' : '0');
		if (hasChest)
			res.append(InventorySerializer.saveInventory(inventory));
		return res.toString();
	}
	
	public static BoatData fromString(String data)
	{
		if (data == null || data.length() == 0)
			throw new IllegalArgumentException("data must not be null or empty!");
		BoatData res = new BoatData();
		res.boatMaterial = charToMaterial(data.charAt(0));
		if (res.boatMaterial == null)
			return null;
		if (data.length() < 2)
			return null;
		res.hasChest = data.charAt(1) == '1';
		if (res.hasChest)
			res.inventory = InventorySerializer.loadInventory(data.substring(2));
		return res;
	}

	private static char materialToChar(Material material) {
		if (material == Material.ACACIA_BOAT)
			return 'a';
		if (material == Material.BIRCH_BOAT)
			return 'b';
		if (material == Material.DARK_OAK_BOAT)
			return 'd';
		if (material == Material.JUNGLE_BOAT)
			return 'j';
		if (material == Material.OAK_BOAT)
			return 'o';
		if (material == Material.SPRUCE_BOAT)
			return 's';
		if (UtilsVersion.SUPPORTS_MANGROVE_BOAT) {
			if (material == Material.MANGROVE_BOAT)
				return 'm';
		}
		if (UtilsVersion.SUPPORTS_CHERRY_BOAT) {
			if (material == Material.CHERRY_BOAT)
				return 'c';
		}
		if (UtilsVersion.SUPPORTS_BAMBOO_RAFT) {
			if (material == Material.BAMBOO_RAFT)
				return 'r';
		}
		if (UtilsVersion.SUPPORTS_PALE_OAK_BOAT) {
			if (material == Material.PALE_OAK_BOAT)
				return 'p';
		}
		return UNDEFINED_MATERIAL;
	}
	
	private static Material charToMaterial(char c) {
		if (c == 'a')
			return Material.ACACIA_BOAT;
		if (c == 'b')
			return Material.BIRCH_BOAT;
		if (c == 'd')
			return Material.DARK_OAK_BOAT;
		if (c == 'j')
			return Material.JUNGLE_BOAT;
		if (c == 'o')
			return Material.OAK_BOAT;
		if (c == 's')
			return Material.SPRUCE_BOAT;
		if (UtilsVersion.SUPPORTS_MANGROVE_BOAT) {
			if (c == 'm')
				return Material.MANGROVE_BOAT;
		}
		if (UtilsVersion.SUPPORTS_CHERRY_BOAT) {
			if (c == 'c')
				return Material.CHERRY_BOAT;
		}
		if (UtilsVersion.SUPPORTS_BAMBOO_RAFT) {
			if (c == 'r')
				return Material.BAMBOO_RAFT;
		}
		if (UtilsVersion.SUPPORTS_PALE_OAK_BOAT) {
			if (c == 'p')
				return Material.PALE_OAK_BOAT;
		}
		return null;
	}
}
