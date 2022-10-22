package com.festp.components;

import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.entity.Boat;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("deprecation")
public class BoatData
{
	TreeSpecies boatWood = TreeSpecies.GENERIC;
	boolean hasChest = false;
	ItemStack[] inventory = new ItemStack[0];

	@Override
	public String toString() {
		return String.valueOf(boatTypeToChar(boatWood));
	}
	
	public static BoatData fromString(String data)
	{
		if (data == null || data.length() == 0)
			throw new IllegalArgumentException("data must not be null or empty!");
		BoatData res = new BoatData();
		res.boatWood = charToBoatType(data.charAt(0));
		return res;
	}

	public static BoatData fromBoat(Boat boat) {
		BoatData res = new BoatData();
		res.boatWood = boat.getWoodType();
		return res;
	}

	public void applyToBoat(Boat boat) {
		boat.setWoodType(boatWood);
	}

	// TODO chested boats (ChestedBoat.class)
	// TODO mangrove (Boat.Type instead of deprecated TreeSpecies)
	private static TreeSpecies charToBoatType(char c) {
    	switch(c)
    	{
    	case 'a': return TreeSpecies.ACACIA;
    	case 'b': return TreeSpecies.BIRCH;
    	case 'd': return TreeSpecies.DARK_OAK;
    	case 'j': return TreeSpecies.JUNGLE;
    	case 'o': return TreeSpecies.GENERIC; // oak
    	case 's': return TreeSpecies.REDWOOD; // spruce
		}
		return null;
	}
	
	private static char boatTypeToChar(TreeSpecies type) {
    	switch(type)
    	{
    	case ACACIA: return 'a';
    	case BIRCH: return 'b';
    	case DARK_OAK: return 'd';
    	case JUNGLE: return 'j';
    	case GENERIC: return 'o'; // oak
    	case REDWOOD: return 's'; // spruce
		}
		return 'o';
	}

	public static BoatData fromBoatMaterial(ItemStack centralCell) {
		TreeSpecies boatType = getBoatType(centralCell);
		BoatData res = new BoatData();
		res.boatWood = boatType;
		return res;
	}
	
	private static TreeSpecies getBoatType(ItemStack boat) {
		Material woodType = boat.getType();
		if(woodType == Material.ACACIA_BOAT)
			return TreeSpecies.ACACIA;
		else if(woodType == Material.BIRCH_BOAT)
			return TreeSpecies.BIRCH;
		else if(woodType == Material.DARK_OAK_BOAT)
			return TreeSpecies.DARK_OAK;
		else if(woodType == Material.JUNGLE_BOAT)
			return TreeSpecies.JUNGLE;
		else if(woodType == Material.OAK_BOAT)
			return TreeSpecies.GENERIC;
		else if(woodType == Material.SPRUCE_BOAT)
			return TreeSpecies.REDWOOD;
		return TreeSpecies.GENERIC;
	}
}
