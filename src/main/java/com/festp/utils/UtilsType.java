package com.festp.utils;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class UtilsType
{
	private static final Map<Material, Boolean> isTransparent = new HashMap<>(); // entity can stay in (no liquids)
	private static final Map<Material, Boolean> isSolid = new HashMap<>(); // entity can stay on (no liquids)
	
	// errors: snow? layers
	private static void checkBlock(Block b)
	{
		Material m = b.getType();
		if (isTransparent.containsKey(m))
			return;
		if (isVile(m)) {
			isTransparent.put(m, false);
			isSolid.put(m, false);
			return;
		}
		
		double eps = 0.001;
		Vector[] localStarts = new Vector[] {
				new Vector(eps, eps, eps), new Vector(1 - eps, eps, eps),
				new Vector(eps, 1 - eps, eps), new Vector(1 - eps, 1 - eps, eps),
				new Vector(0, 0.5, 0.5), new Vector(0.5, 0, 0.5), new Vector(0.5, 0.5, 0) };
		Vector[] directions = new Vector[] {
				new Vector(1, 1, 1), new Vector(-1, 1, 1),
				new Vector(1, -1, 1), new Vector(-1, -1, 1),
				new Vector(1, 0, 0), new Vector(0, 1, 0), new Vector(0, 0, 1)};
		double sqrt3 = Math.sqrt(3) - 2 * eps;
		double one = 1 - 2 * eps;
		double[] dists = new double[] { sqrt3, sqrt3, sqrt3, sqrt3, one, one, one };
		for (int i = 0; i < localStarts.length; i++)
		{
			Location start = b.getLocation().add(localStarts[i]);
			double dist = dists[i] - 2 * eps;
			RayTraceResult result = b.getWorld().rayTraceBlocks(start, directions[i], dist, FluidCollisionMode.NEVER, true);
			if (result != null) {
				isSolid.put(m, true);
				isTransparent.put(m, false);
				return;
			}
			else {
				result = b.getWorld().rayTraceBlocks(start, directions[i], dist, FluidCollisionMode.NEVER, false);
				if (result != null || UtilsType.isAir(m)) {
					isTransparent.put(m, true);
					isSolid.put(m, false);
					return;
				}
			}
		}
		// bubble column, etc
		isTransparent.put(m, false);
		isSolid.put(m, false);
	}

	/** @return true if m is not always solid or transparent (water, gates, ...) */
	private static boolean isVile(Material m) {
		return isLiquid(m) || isGate(m);
	}
	
	public static Map<Material, Boolean> getIsTransparent() {
		return isTransparent;
	}
	public static Map<Material, Boolean> getIsSolid() {
		return isSolid;
	}

	public static boolean isTransparent(Block b) {
		checkBlock(b);
		return isTransparent.get(b.getType());
	}
	public static boolean isSolid(Block b) {
		checkBlock(b);
		return isSolid.get(b.getType());
	}
	
	private static boolean isLiquid(Material m) {
		return m == Material.WATER || m == Material.LAVA;
	}
	public static boolean playerCanStayIn(Block b) { // TODO: correct slabs
		return isTransparent(b) && isTransparent(b.getRelative(0, 1, 0)) && isSolid(b.getRelative(0, -1, 0));
	}
	
	public static boolean playerCanFlyOn(Block b) {
		return isTransparent(b.getRelative(0, 1, 0)) && isTransparent(b.getRelative(0, 2, 0));
	}
	
	public static boolean isWoodenTrapdoor(Material m) {
		switch(m) {
		case ACACIA_TRAPDOOR: return true;
		case BIRCH_TRAPDOOR: return true;
		case DARK_OAK_TRAPDOOR: return true;
		case JUNGLE_TRAPDOOR: return true;
		case OAK_TRAPDOOR: return true;
		case SPRUCE_TRAPDOOR: return true;
		case CRIMSON_TRAPDOOR: return true;
		case WARPED_TRAPDOOR: return true;
		default: return false;
		}
	}
	
	public static boolean isTrapdoor(Material m) {
		if(isWoodenTrapdoor(m) || m == Material.IRON_TRAPDOOR)
			return true;
		return false;
	}
	
	public static boolean isWoodenDoor(Material m) {
		switch(m) {
		case ACACIA_DOOR: return true;
		case BIRCH_DOOR: return true;
		case DARK_OAK_DOOR: return true;
		case JUNGLE_DOOR: return true;
		case OAK_DOOR: return true;
		case SPRUCE_DOOR: return true;
		case CRIMSON_DOOR: return true;
		case WARPED_DOOR: return true;
		default: return false;
		}
	}
	
	public static boolean isDoor(Material m) {
		if(isWoodenDoor(m) || m == Material.IRON_DOOR)
			return true;
		return false;
	}
	
	public static boolean isGate(Material m) {
		switch(m) {
		case ACACIA_FENCE_GATE: return true;
		case BIRCH_FENCE_GATE: return true;
		case DARK_OAK_FENCE_GATE: return true;
		case JUNGLE_FENCE_GATE: return true;
		case OAK_FENCE_GATE: return true;
		case SPRUCE_FENCE_GATE: return true;
		case CRIMSON_FENCE_GATE: return true;
		case WARPED_FENCE_GATE: return true;
		default: return false;
		}
	}
	
	public static boolean isAir(Material m) {
		return m == Material.AIR || m == Material.CAVE_AIR || m == Material.VOID_AIR;
	}
	
	public static boolean isAir(ItemStack is) {
		return is == null || isAir(is.getType());
	}
	
	public static boolean isInteractable(Material m)
	{
		if (!m.isInteractable())
			return false;
		// tags are 1.13+, for 1.12-:
		// https://www.spigotmc.org/threads/check-if-a-block-is-interactable.535861/
		return !Tag.FENCES.isTagged(m) && !Tag.STAIRS.isTagged(m)
				&& m != Material.REDSTONE_ORE && m != Material.PUMPKIN && m != Material.MOVING_PISTON
				&& m != Material.REDSTONE_WIRE;
	}

	public static boolean isHorseArmor(Material m) {
		return m.toString().toUpperCase().contains("HORSE_ARMOR");
	}
}
