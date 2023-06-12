package com.festp.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Boat;
import org.bukkit.entity.ChestBoat;

import com.festp.handlers.TomeEntityHandler;
import com.festp.utils.Utils;
import com.festp.utils.UtilsVersion;

public class BoatDataConverter1_19 implements IBoatDataConverter
{
	private static class BoatMaterial {
		Material boatMaterial;
		Material chestBoatMaterial;
		Boat.Type type;
		
		public BoatMaterial(Material boat, Material chestBoat, Boat.Type type) {
			this.boatMaterial = boat;
			this.chestBoatMaterial = chestBoat;
			this.type = type;
		}
	}
	
	private static BoatMaterial[] initBoatMaterials() {
		List<BoatMaterial> res = new ArrayList<>(Arrays.asList(
				new BoatMaterial(Material.ACACIA_BOAT, Material.ACACIA_CHEST_BOAT, Boat.Type.ACACIA),
				new BoatMaterial(Material.BIRCH_BOAT, Material.BIRCH_CHEST_BOAT, Boat.Type.BIRCH),
				new BoatMaterial(Material.DARK_OAK_BOAT, Material.DARK_OAK_CHEST_BOAT, Boat.Type.DARK_OAK),
				new BoatMaterial(Material.JUNGLE_BOAT, Material.JUNGLE_CHEST_BOAT, Boat.Type.JUNGLE),
				new BoatMaterial(Material.OAK_BOAT, Material.OAK_CHEST_BOAT, Boat.Type.OAK),
				new BoatMaterial(Material.SPRUCE_BOAT, Material.SPRUCE_CHEST_BOAT, Boat.Type.SPRUCE),
				new BoatMaterial(Material.MANGROVE_BOAT, Material.MANGROVE_CHEST_BOAT, Boat.Type.MANGROVE)
			));
		if (UtilsVersion.SUPPORTS_CHERRY_BOAT)
			res.add(new BoatMaterial(Material.CHERRY_BOAT, Material.CHERRY_CHEST_BOAT, Boat.Type.CHERRY));
		if (UtilsVersion.SUPPORTS_BAMBOO_RAFT)
			res.add(new BoatMaterial(Material.BAMBOO_RAFT, Material.BAMBOO_CHEST_RAFT, Boat.Type.BAMBOO));
		
		return res.toArray(new BoatMaterial[0]);
	}
	
	private static final BoatMaterial[] BOAT_MATERIALS = initBoatMaterials();

	public BoatData fromBoat(Boat boat)
	{
		Material boatMaterial = woodToMaterial(boat.getBoatType());
		if (boatMaterial == null)
			return null;
		
		BoatData res = new BoatData();
		res.boatMaterial = boatMaterial;
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
			Boat newBoat = loc.getWorld().spawn(loc, desiredClass);
			TomeEntityHandler.replaceEntity(boat, newBoat);
			boat = newBoat;
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
		List<Material> res = new ArrayList<>();
		for (BoatMaterial bm : BOAT_MATERIALS) {
			res.add(bm.boatMaterial);
			res.add(bm.chestBoatMaterial);
		}
		
		return res.toArray(new Material[0]);
	}
	
	private static Boat.Type materialToWood(Material m) {
		for (BoatMaterial bm : BOAT_MATERIALS) {
			if (m == bm.boatMaterial)
				return bm.type;
		}
    	return null;
	}
	
	private static Material woodToMaterial(Boat.Type type) {
		for (BoatMaterial bm : BOAT_MATERIALS) {
			if (type == bm.type)
				return bm.boatMaterial;
		}
		return null;
	}

	private static boolean getIsChested(Material m) {
		return Tag.ITEMS_CHEST_BOATS.isTagged(m);
	}
	
	private static Material getChestless(Material m) {
		for (BoatMaterial bm : BOAT_MATERIALS) {
			if (m == bm.chestBoatMaterial)
				return bm.boatMaterial;
		}
		return m;
	}
}
