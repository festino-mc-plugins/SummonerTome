package com.festp.components;

import org.bukkit.Material;
import org.bukkit.entity.Boat;
import org.bukkit.entity.ChestBoat;
import org.bukkit.inventory.ItemStack;

import com.festp.inventory.InventorySerializer;
import com.festp.utils.Utils;

public class BoatData
{
	private static IBoatDataConverter CONVERTER = getConverter();
	
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
	
	private static IBoatDataConverter getConverter() {
		boolean above1_19 = Utils.GetVersion() >= 11900;
		if (above1_19)
			return new BoatDataConverter1_19();
		else
			return new BoatDataConverter1_18();
	}

	public static BoatData fromBoatMaterial(ItemStack stack) {
		BoatData res = new BoatData();
		if (charToMaterial(materialToChar(stack.getType())) != null)
			res.boatMaterial = stack.getType();
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
		boolean above1_19 = Utils.GetVersion() >= 11900;
		if (above1_19) {
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
		boolean above1_19 = Utils.GetVersion() >= 11900;
		if (above1_19) {
			if (c == 'm')
				return Material.MANGROVE_BOAT;
		}
		return null;
	}
}
