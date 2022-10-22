package com.festp.components;

import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.entity.Boat;

@SuppressWarnings("deprecation")
public class BoatDataConverter1_18 implements IBoatDataConverter
{
	public BoatData fromBoat(Boat boat) {
		BoatData res = new BoatData();
		res.boatMaterial = woodToMaterial(boat.getWoodType());
		return res;
	}

	public void applyToBoat(BoatData data, Boat boat) {
		boat.setWoodType(materialToWood(data.boatMaterial));
	}
	
	private static TreeSpecies materialToWood(Material m) {
    	switch(m)
    	{
    	case ACACIA_BOAT: return TreeSpecies.ACACIA;
    	case BIRCH_BOAT: return TreeSpecies.BIRCH;
    	case DARK_OAK_BOAT: return TreeSpecies.DARK_OAK;
    	case JUNGLE_BOAT: return TreeSpecies.JUNGLE;
    	case OAK_BOAT: return TreeSpecies.GENERIC;
    	case SPRUCE_BOAT: return TreeSpecies.REDWOOD;
		default: return null;
		}
	}
	
	private static Material woodToMaterial(TreeSpecies type) {
    	switch(type)
    	{
    	case ACACIA: return Material.ACACIA_BOAT;
    	case BIRCH: return Material.BIRCH_BOAT;
    	case DARK_OAK: return Material.DARK_OAK_BOAT;
    	case JUNGLE: return Material.JUNGLE_BOAT;
    	case GENERIC: return Material.OAK_BOAT;
    	case REDWOOD: return Material.SPRUCE_BOAT;
		}
		return null;
	}
}
