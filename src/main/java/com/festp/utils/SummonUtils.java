package com.festp.utils;

import java.util.UUID;
import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Strider;
import org.bukkit.inventory.ItemStack;

import com.festp.components.boat.BoatData;
import com.festp.components.horse.HorseData;

public class SummonUtils
{
	private static final String TAG_HAS_SUMMONED = "hassummoned";
	private static final String TAG_CODE = "tomecode";
	
	private static final Material[] BOAT_BLOCKS =
			{ Material.WATER, Material.ICE, Material.PACKED_ICE, Material.BLUE_ICE, Material.FROSTED_ICE,
				Material.BUBBLE_COLUMN, Material.KELP, Material.KELP_PLANT, Material.SEAGRASS, Material.TALL_SEAGRASS };
	private static final Material[] MINECART_BLOCKS =
			{ Material.RAIL, Material.ACTIVATOR_RAIL, Material.DETECTOR_RAIL, Material.POWERED_RAIL };
	private static final Material[] STRIDER_BLOCKS =
		{ Material.LAVA };
	
	private static final Predicate<Block> PREDICATE_MINECART = new Predicate<Block>() {
		@Override
		public boolean test(Block b) {
			return Utils.contains(MINECART_BLOCKS, b.getType());
		}
	};
	private static final Predicate<Block> PREDICATE_STRIDER = new Predicate<Block>() {
		@Override
		public boolean test(Block b) {
			return Utils.contains(STRIDER_BLOCKS, b.getType()) && UtilsType.playerCanFlyOn(b);
		}
	};
	private static final Predicate<Block> PREDICATE_PIG = new Predicate<Block>() {
		@Override
		public boolean test(Block b) {
			return UtilsType.playerCanStayIn(b);
		}
	};
	private static final Predicate<Block> PREDICATE_BOAT = new Predicate<Block>() {
		@Override
		public boolean test(Block b) {
			if (Utils.contains(BOAT_BLOCKS, b.getType()))
				return true;
			BlockData data = b.getBlockData();
			return data instanceof Waterlogged && ((Waterlogged)data).isWaterlogged();
		}
	};
	private static final Predicate<Block> PREDICATE_HORSE = new Predicate<Block>() {
		@Override
		public boolean test(Block b) {
			return UtilsType.isSolid(b);
			//return UtilsType.playerCanStayIn(b.getRelative(0, 1, 0));
		}
	};
	
	public static Location tryFindForMinecart(Location playerLoc, double horRadius) {
		return UtilsWorld.searchBlock(playerLoc, PREDICATE_MINECART, horRadius);
	}
	public static Minecart summonMinecart(Location l, Player p) {
		Minecart mc = l.getWorld().spawn(l, Minecart.class);
		mc.addPassenger(p);
		mc.setVelocity(p.getVelocity());
		return mc;
	}
	
	public static Location tryFindForStrider(Location playerLoc, double horRadius) {
		Location loc = UtilsWorld.searchBlock(playerLoc.add(0, -1, 0), PREDICATE_STRIDER, horRadius);
		if (loc == null)
			loc = UtilsWorld.searchBlock(playerLoc.add(0, -1, 0), PREDICATE_STRIDER, horRadius);
		if (loc != null)
			loc.add(0, 1, 0);
		return loc;
	}
	public static Strider summonStrider(Location l, Player p) {
		l.setDirection(p.getLocation().getDirection());
		Strider strider = l.getWorld().spawn(l, Strider.class);
		strider.setSaddle(true);
		strider.addPassenger(p);
		strider.setVelocity(p.getVelocity());
		return strider;
	}
	
	public static Location tryFindForPig(Location playerLoc, double horRadius) {
		return UtilsWorld.searchBlock(playerLoc, PREDICATE_PIG, horRadius);
	}
	public static Pig summonPig(Location l, Player p) {
		l.setDirection(p.getLocation().getDirection());
		Pig pig = l.getWorld().spawn(l, Pig.class);
		pig.setSaddle(true);
		pig.addPassenger(p);
		pig.setVelocity(p.getVelocity());
		return pig;
	}
	
	private static Location getNearest(Location loc, Location l1, Location l2) {
		if (l1 == null) {
			return l2;
		} else if (l2 != null) {
			if (loc.distanceSquared(l2) < loc.distanceSquared(l1)) {
				return l2;
			}
		}
		return l1;
	}
	
	private static Location findNearest_2x2_3x3(Location loc, double horRadius, Predicate<Block> predicate, boolean softMode) {
		setGroundY(loc);
		Location l_3x3 = UtilsWorld.searchArea_NxN(loc, 3, horRadius, predicate, softMode);
		Location l_2x2 = UtilsWorld.searchArea_NxN(loc, 2, horRadius, predicate, softMode);
		
		Location res = getNearest(loc, l_3x3, l_2x2);
		// TODO set original Y if possible (use Block#getCollisionShape())
		if (res != null)
			res.setY(loc.getY() + 1);
		return res;
	}
	
	public static Location tryFindForBoat(Location loc, double horRadius) {
		Location l1 = findNearest_2x2_3x3(loc.clone(), horRadius, PREDICATE_BOAT, false);
		Location l2 = findNearest_2x2_3x3(loc.add(0, +1, 0), horRadius, PREDICATE_BOAT, false);
		return getNearest(loc, l1, l2);
	}
	public static Boat summonBoat(Location l, Player p, BoatData boatData) {
		l.setDirection(p.getLocation().getDirection());
		Boat boat = l.getWorld().spawn(l, boatData.getBoatClass());
		boatData.applyToBoat(boat); // TODO fix flickering (use consumer)
		boat.addPassenger(p);
		boat.setVelocity(p.getVelocity());
		return boat;
	}

	public static Location tryFindForHorse(Location loc, double horRadius) {
		return findNearest_2x2_3x3(loc, horRadius, PREDICATE_HORSE, true);
	}
	public static Horse summonHorse(Location l, Player p) {
		l.setDirection(p.getLocation().getDirection());
		Horse horse = l.getWorld().spawn(l, Horse.class, (newHorse) ->
		{
			initHorse(newHorse, p);
		});
		horse.setVelocity(p.getVelocity());
		return horse;
	}
	
	public static AbstractHorse summonCustomHorse(Location l, Player p, HorseData horseData)
	{
		Class<? extends AbstractHorse> type;
		if (horseData == null) {
			//type = Horse.class;
			return null;
		} else {
			type = horseData.getHorseClass();
		}
		
		AbstractHorse horse = summonCustomHorse(l, type, new HorseSetter()
		{
			@Override
			public void set(AbstractHorse newHorse)
			{
				horseData.applyToHorse(newHorse);
				initHorse(newHorse, p);
				newHorse.setTamed(true);
				newHorse.setOwner(p);
				newHorse.addPassenger(p);

				if (!newHorse.isAdult())
					newHorse.setAgeLock(true);
			}
		});
		
		return horse;
	}
	
	private static void initHorse(AbstractHorse horse, Player owner)
	{
		horse.setTamed(true);
		if (horse.getInventory().getSaddle() == null)
			horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
		horse.setOwner(owner);
		horse.addPassenger(owner);
	}
	
	public static AbstractHorse summonCustomHorse(Location l, Class<? extends AbstractHorse> type, HorseSetter setter) {
		return l.getWorld().spawn(l, type, (newHorse) -> setter.set(newHorse));
	}
	
	public static interface HorseSetter {
		public void set(AbstractHorse newHorse);
	}


	public static boolean wasSummoned(Entity entity) {
		return NBTUtils.hasString(entity, TAG_CODE);
	}
	public static String getCode(Entity entity) {
		return NBTUtils.getString(entity, TAG_CODE);
	}
	public static void setCode(Entity entity, String code) {
		NBTUtils.setString(entity, TAG_CODE, code);
	}
	
	public static boolean hasSummoned(ItemStack tome) {
		return getHasSummoned(tome) != null;
	}
	public static Entity getHasSummoned(ItemStack tome) {
        if (NBTUtils.hasString(tome, TAG_HAS_SUMMONED)) {
        	UUID entityUuid = UUID.fromString(NBTUtils.getString(tome, TAG_HAS_SUMMONED));
        	return Bukkit.getEntity(entityUuid);
        }
		return null;
	}
	
	private static ItemStack setHasSummoned(ItemStack tome, UUID entityUuid) {
        if (entityUuid == null)
        	tome = NBTUtils.removeString(tome, TAG_HAS_SUMMONED);
        else
        	tome = NBTUtils.setString(tome, TAG_HAS_SUMMONED, entityUuid.toString());
        return tome;
	}
	public static void setHasSummoned(Player p, boolean mainHand, Entity summoned)
	{
		ItemStack tome = getTome(p, mainHand);
		tome = setHasSummoned(tome, summoned.getUniqueId());
		setTome(p, mainHand, tome);
	}
	
	private static ItemStack getTome(Player p, boolean mainHand)
	{
		ItemStack tome;
		if (mainHand)
			tome = p.getInventory().getItemInMainHand();
		else
			tome = p.getInventory().getItemInOffHand();
		return tome;
	}
	private static void setTome(Player p, boolean mainHand, ItemStack tome)
	{
		if (mainHand)
			p.getInventory().setItemInMainHand(tome);
		else
			p.getInventory().setItemInOffHand(tome);
	}
	
	// TODO check actual height using loc.getBlock().getCollisionShape()
	// for blocks player intersect using player.getWidth()
	private static void setGroundY(Location loc) {
		
		loc.setY(Math.floor(loc.getY() - 0.5));
		//loc.setY(Math.floor(loc.getY() - (0.0625 - 0.0001))); // 0.0625 is carpet height
	}
}
