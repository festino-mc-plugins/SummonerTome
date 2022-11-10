package com.festp.components;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Boat;
import org.bukkit.entity.ChestBoat;
import org.bukkit.inventory.ItemStack;

import com.festp.inventory.InventorySerializer;
import com.festp.utils.UtilsVersion;

public class BoatData
{
	private static final IBoatDataConverter CONVERTER = getConverter();
	
	private static IBoatDataConverter getConverter() {
		if (UtilsVersion.SUPPORTS_CHEST_AND_MANGROVE)
			return new BoatDataConverter1_19();
		else
			return new BoatDataConverter1_18();
	}
	
	public static Material[] getSupportedBoats() {
		return CONVERTER.getSupportedBoats();
	}
	
	Material boatMaterial = Material.OAK_BOAT;
	boolean hasChest = false;
	ItemStack[] inventory = new ItemStack[0];

	@Override
	public String toString() {
		StringBuilder res = new StringBuilder();
		res.append(materialToChar(boatMaterial));
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

	public static BoatData fromBoat(Boat boat) {
		return CONVERTER.fromBoat(boat);
	}

	public void applyToBoat(Boat boat) {
		CONVERTER.applyToBoat(this, boat);
	}
	
	public Class<? extends Boat> getBoatClass() {
		return hasChest ? ChestBoat.class : Boat.class;
	}

	// TODO refactor
	public static BoatData fromBoatMaterial(Material m) {
		BoatData res = new BoatData();
		res.hasChest = getIsChested(m);
		m = getChestless(m);
		if (charToMaterial(materialToChar(m)) != null)
			res.boatMaterial = m;
		return res;
	}

	private static boolean getIsChested(Material m) {
		if (!UtilsVersion.SUPPORTS_CHEST_AND_MANGROVE)
			return false;
		return Tag.ITEMS_CHEST_BOATS.isTagged(m);
	}
	
	private static Material getChestless(Material m) {
		if (!UtilsVersion.SUPPORTS_CHEST_AND_MANGROVE)
			return m;
		
		if (m == Material.ACACIA_CHEST_BOAT)
			return Material.ACACIA_BOAT;
		if (m == Material.BIRCH_CHEST_BOAT)
			return Material.BIRCH_BOAT;
		if (m == Material.DARK_OAK_CHEST_BOAT)
			return Material.DARK_OAK_BOAT;
		if (m == Material.JUNGLE_CHEST_BOAT)
			return Material.JUNGLE_BOAT;
		if (m == Material.OAK_CHEST_BOAT)
			return Material.OAK_BOAT;
		if (m == Material.SPRUCE_CHEST_BOAT)
			return Material.SPRUCE_BOAT;
		if (m == Material.MANGROVE_CHEST_BOAT)
			return Material.MANGROVE_BOAT;
		return m;
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
		if (UtilsVersion.SUPPORTS_CHEST_AND_MANGROVE) {
			if (material == Material.MANGROVE_BOAT)
				return 'm';
		}
		return '?';
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
		if (UtilsVersion.SUPPORTS_CHEST_AND_MANGROVE) {
			if (c == 'm')
				return Material.MANGROVE_BOAT;
		}
		return null;
	}
}
