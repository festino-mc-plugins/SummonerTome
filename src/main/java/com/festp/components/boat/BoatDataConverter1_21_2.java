package com.festp.components.boat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Boat;
import org.bukkit.entity.ChestBoat;
import org.bukkit.entity.boat.AcaciaBoat;
import org.bukkit.entity.boat.AcaciaChestBoat;
import org.bukkit.entity.boat.BambooChestRaft;
import org.bukkit.entity.boat.BambooRaft;
import org.bukkit.entity.boat.BirchBoat;
import org.bukkit.entity.boat.BirchChestBoat;
import org.bukkit.entity.boat.CherryBoat;
import org.bukkit.entity.boat.CherryChestBoat;
import org.bukkit.entity.boat.DarkOakBoat;
import org.bukkit.entity.boat.DarkOakChestBoat;
import org.bukkit.entity.boat.JungleBoat;
import org.bukkit.entity.boat.JungleChestBoat;
import org.bukkit.entity.boat.MangroveBoat;
import org.bukkit.entity.boat.MangroveChestBoat;
import org.bukkit.entity.boat.OakBoat;
import org.bukkit.entity.boat.OakChestBoat;
import org.bukkit.entity.boat.PaleOakBoat;
import org.bukkit.entity.boat.PaleOakChestBoat;
import org.bukkit.entity.boat.SpruceBoat;
import org.bukkit.entity.boat.SpruceChestBoat;

import com.festp.handlers.TomeEntityHandler;

public class BoatDataConverter1_21_2 implements IBoatDataConverter
{
	private static class BoatMaterial {
		final Material chestlessBoatMaterial;
		final boolean hasChest;
		final Material boatMaterial;
		final Class<? extends Boat> clazz;
		
		public BoatMaterial(Material chestlessBoat, Material boat, Class<? extends Boat> clazz) {
			this.chestlessBoatMaterial = chestlessBoat;
			this.boatMaterial = boat;
			this.hasChest = getIsChested(boat);
			this.clazz = clazz;
		}

		private static boolean getIsChested(Material m) {
			return Tag.ITEMS_CHEST_BOATS.isTagged(m);
		}
	}
	
	private static BoatMaterial[] initBoatMaterials() {
		List<BoatMaterial> res = new ArrayList<>(Arrays.asList(
				new BoatMaterial(Material.ACACIA_BOAT, Material.ACACIA_BOAT, AcaciaBoat.class),
				new BoatMaterial(Material.ACACIA_BOAT, Material.ACACIA_CHEST_BOAT, AcaciaChestBoat.class),
				new BoatMaterial(Material.BIRCH_BOAT, Material.BIRCH_BOAT, BirchBoat.class),
				new BoatMaterial(Material.BIRCH_BOAT, Material.BIRCH_CHEST_BOAT, BirchChestBoat.class),
				new BoatMaterial(Material.DARK_OAK_BOAT, Material.DARK_OAK_BOAT, DarkOakBoat.class),
				new BoatMaterial(Material.DARK_OAK_BOAT, Material.DARK_OAK_CHEST_BOAT, DarkOakChestBoat.class),
				new BoatMaterial(Material.JUNGLE_BOAT, Material.JUNGLE_BOAT, JungleBoat.class),
				new BoatMaterial(Material.JUNGLE_BOAT, Material.JUNGLE_CHEST_BOAT, JungleChestBoat.class),
				new BoatMaterial(Material.OAK_BOAT, Material.OAK_BOAT, OakBoat.class),
				new BoatMaterial(Material.OAK_BOAT, Material.OAK_CHEST_BOAT, OakChestBoat.class),
				new BoatMaterial(Material.SPRUCE_BOAT, Material.SPRUCE_BOAT, SpruceBoat.class),
				new BoatMaterial(Material.SPRUCE_BOAT, Material.SPRUCE_CHEST_BOAT, SpruceChestBoat.class),
				new BoatMaterial(Material.MANGROVE_BOAT, Material.MANGROVE_BOAT, MangroveBoat.class),
				new BoatMaterial(Material.MANGROVE_BOAT, Material.MANGROVE_CHEST_BOAT, MangroveChestBoat.class),
				new BoatMaterial(Material.CHERRY_BOAT, Material.CHERRY_BOAT, CherryBoat.class),
				new BoatMaterial(Material.CHERRY_BOAT, Material.CHERRY_CHEST_BOAT, CherryChestBoat.class),
				new BoatMaterial(Material.BAMBOO_RAFT, Material.BAMBOO_RAFT, BambooRaft.class),
				new BoatMaterial(Material.BAMBOO_RAFT, Material.BAMBOO_CHEST_RAFT, BambooChestRaft.class),
				new BoatMaterial(Material.PALE_OAK_BOAT, Material.PALE_OAK_BOAT, PaleOakBoat.class),
				new BoatMaterial(Material.PALE_OAK_BOAT, Material.PALE_OAK_CHEST_BOAT, PaleOakChestBoat.class)
			));
		
		return res.toArray(new BoatMaterial[0]);
	}
	
	private static final BoatMaterial[] BOAT_MATERIALS = initBoatMaterials();

	public BoatData fromBoat(Boat boat)
	{
		BoatMaterial boatMaterial = entityToBoatMaterial(boat.getClass());
		if (boatMaterial == null)
			return null;
		
		BoatData res = new BoatData();
		res.boatMaterial = boatMaterial.chestlessBoatMaterial;
		res.hasChest = boatMaterial.hasChest;
		if (res.hasChest) {
			res.inventory = ((ChestBoat)boat).getInventory().getContents();
		}
		return res;
	}

	public void applyToBoat(BoatData data, Boat boat)
	{
		Class<? extends Boat> desiredClass = getBoatClass(data);
		if (!desiredClass.isAssignableFrom(boat.getClass())) {
			Location loc = boat.getLocation();
			Boat newBoat = loc.getWorld().spawn(loc, desiredClass);
			TomeEntityHandler.replaceEntity(boat, newBoat);
			boat = newBoat;
		}
		if (data.hasChest) {
			((ChestBoat)boat).getInventory().setContents(data.inventory);
		}
	}
	
	public BoatData fromBoatMaterial(Material m) {
		BoatMaterial boatMaterial = materialToBoatMaterial(m);
		if (boatMaterial == null)
			return null;
		
		BoatData res = new BoatData();
		res.boatMaterial = boatMaterial.chestlessBoatMaterial;
		res.hasChest = boatMaterial.hasChest;
		return res;
	}

	public Material[] getSupportedBoats() {
		List<Material> res = new ArrayList<>();
		for (BoatMaterial bm : BOAT_MATERIALS) {
			res.add(bm.boatMaterial);
		}
		
		return res.toArray(new Material[0]);
	}
	
	public Class<? extends Boat> getBoatClass(BoatData data) {
		for (BoatMaterial bm : BOAT_MATERIALS) {
			if (data.boatMaterial == bm.chestlessBoatMaterial && data.hasChest == bm.hasChest)
				return bm.clazz;
		}
		return null;
	}
	
	private static BoatMaterial entityToBoatMaterial(Class<? extends Boat> clazz) {
		for (BoatMaterial bm : BOAT_MATERIALS) {
			if (bm.clazz.isAssignableFrom(clazz))
				return bm;
		}
		return null;
	}
	
	private static BoatMaterial materialToBoatMaterial(Material m) {
		for (BoatMaterial bm : BOAT_MATERIALS) {
			if (m == bm.boatMaterial)
				return bm;
		}
		return null;
	}
}
