package com.festp.components.boat;

import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.entity.Boat;

import com.festp.utils.Utils;

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
	
	public Class<? extends Boat> getBoatClass(BoatData data) {
		return Boat.class;
	}

	public BoatData fromBoatMaterial(Material m) {
		BoatData res = new BoatData();
		if (Utils.contains(getSupportedBoats(), m))
			res.boatMaterial = m;
		return res;
	}

	public Material[] getSupportedBoats() {
		return new Material[] {
				Material.ACACIA_BOAT, Material.BIRCH_BOAT, Material.DARK_OAK_BOAT,
				Material.JUNGLE_BOAT, Material.OAK_BOAT, Material.SPRUCE_BOAT };
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
