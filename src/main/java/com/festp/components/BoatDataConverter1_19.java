package com.festp.components;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Boat;
import org.bukkit.entity.ChestBoat;

import com.festp.handlers.TomeEntityHandler;
import com.festp.utils.Utils;

public class BoatDataConverter1_19 implements IBoatDataConverter
{
	public BoatData fromBoat(Boat boat)
	{
		BoatData res = new BoatData();
		res.boatMaterial = woodToMaterial(boat.getBoatType());
		res.hasChest = boat instanceof ChestBoat;
		if (res.hasChest) {
			res.inventory = ((ChestBoat)boat).getInventory().getContents();
		}
		return res;
	}

	public void applyToBoat(BoatData data, Boat boat)
	{
		Class<? extends Boat> desiredClass = data.getBoatClass();
		if (!desiredClass.isAssignableFrom(boat.getClass())
				|| desiredClass == Boat.class && boat instanceof ChestBoat) { // dirty code
			Location loc = boat.getLocation();
			TomeEntityHandler.removeEntity(boat);
			boat = loc.getWorld().spawn(loc, desiredClass);
			boat.teleport(loc);
		}
		boat.setBoatType(materialToWood(data.boatMaterial));
		if (data.hasChest)
			((ChestBoat)boat).getInventory().setContents(data.inventory);
	}
	
	public BoatData fromBoatMaterial(Material m) {
		BoatData res = new BoatData();
		res.hasChest = getIsChested(m);
		m = getChestless(m);
		if (Utils.contains(getSupportedBoats(), m))
			res.boatMaterial = m;
		return res;
	}

	public Material[] getSupportedBoats() {
		return new Material[] {
				Material.ACACIA_BOAT, Material.BIRCH_BOAT, Material.DARK_OAK_BOAT,
				Material.JUNGLE_BOAT, Material.OAK_BOAT, Material.SPRUCE_BOAT,
				Material.MANGROVE_BOAT,
				Material.ACACIA_CHEST_BOAT, Material.BIRCH_CHEST_BOAT, Material.DARK_OAK_CHEST_BOAT,
				Material.JUNGLE_CHEST_BOAT, Material.OAK_CHEST_BOAT, Material.SPRUCE_CHEST_BOAT,
				Material.MANGROVE_CHEST_BOAT};
	}
	
	private static Boat.Type materialToWood(Material m) {
    	switch(m)
    	{
    	case ACACIA_BOAT: return Boat.Type.ACACIA;
    	case BIRCH_BOAT: return Boat.Type.BIRCH;
    	case DARK_OAK_BOAT: return Boat.Type.DARK_OAK;
    	case JUNGLE_BOAT: return Boat.Type.JUNGLE;
    	case OAK_BOAT: return Boat.Type.OAK;
    	case SPRUCE_BOAT: return Boat.Type.SPRUCE;
    	case MANGROVE_BOAT: return Boat.Type.MANGROVE;
		default: return null;
		}
	}
	
	private static Material woodToMaterial(Boat.Type type) {
    	switch(type)
    	{
    	case ACACIA: return Material.ACACIA_BOAT;
    	case BIRCH: return Material.BIRCH_BOAT;
    	case DARK_OAK: return Material.DARK_OAK_BOAT;
    	case JUNGLE: return Material.JUNGLE_BOAT;
    	case OAK: return Material.OAK_BOAT;
    	case SPRUCE: return Material.SPRUCE_BOAT;
    	case MANGROVE: return Material.MANGROVE_BOAT;
		default: return null;
		}
	}

	private static boolean getIsChested(Material m) {
		return Tag.ITEMS_CHEST_BOATS.isTagged(m);
	}
	
	private static Material getChestless(Material m) {
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
}
