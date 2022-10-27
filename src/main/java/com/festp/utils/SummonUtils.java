package com.festp.utils;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Strider;
import org.bukkit.inventory.ItemStack;

import com.festp.components.BoatData;
import com.festp.components.HorseFormat;

public class SummonUtils
{
	private static final String TAG_HAS_SUMMONED = "hassummoned"; 
	private static final String SBTAG_CUSTOM_HORSE = "customhorse"; 
	private static final String SBTAG_FROM_TOME = "fromtome"; 
	
	private static final Material[] BOAT_BLOCKS =
			{ Material.WATER, Material.ICE, Material.PACKED_ICE, Material.BLUE_ICE, Material.FROSTED_ICE, Material.SEA_PICKLE, Material.SEAGRASS, Material.TALL_SEAGRASS };
	private static final Material[] MINECART_BLOCKS =
			{ Material.RAIL, Material.ACTIVATOR_RAIL, Material.DETECTOR_RAIL, Material.POWERED_RAIL };
	private static final Material[] STRIDER_BLOCKS =
		{ Material.LAVA };
	
	public static Location tryFindForMinecart(Location playerLoc, double horRadius) {
		return UtilsWorld.searchBlock(playerLoc, MINECART_BLOCKS, horRadius, false);
	}
	public static Minecart summonMinecart(Location l, Player p) {
		Minecart mc = l.getWorld().spawn(l, Minecart.class);
		mc.addPassenger(p);
		return mc;
	}
	
	public static Location tryFindForStrider(Location playerLoc, double horRadius) {
		return UtilsWorld.searchBlock(playerLoc.add(0, -1, 0), STRIDER_BLOCKS, horRadius, true);
	}
	public static Strider summonStrider(Location l, Player p) {
		Strider strider = l.getWorld().spawn(l, Strider.class);
		strider.setSaddle(true);
		strider.addPassenger(p);
		return strider;
	}
	
	public static Location tryFindForBoat(Location loc, double horRadius) {
		// TODO watered bottom blocks
		// TODO smarter function for boats:
		//      fill one square grid, then check player loc
		//      and iterate squares on smaller grid(0.5 block) to find the nearest place to spawn
		loc.add(0, 0.5, 0);
		loc.setY(Math.floor(loc.getY() - 1));
		Location l_3x3 = UtilsWorld.searchArea_3x3(loc, BOAT_BLOCKS);
		Location l_2x2 = UtilsWorld.searchBlock22Platform(loc, BOAT_BLOCKS, horRadius, false);
		
		Location res = l_3x3;
		if (res == null) {
			res = l_2x2;
		} else if (l_2x2 != null) {
			if (loc.distanceSquared(l_2x2) < loc.distanceSquared(res)) {
				res = l_2x2;
			}
		}
		return res;
	}
	public static Boat summonBoat(Location l, Player p, BoatData boatData) {
		l.setPitch(p.getLocation().getPitch());
		l.setYaw(p.getLocation().getYaw());
		Boat boat = l.getWorld().spawn(l, boatData.getBoatClass());
		boatData.applyToBoat(boat); // TODO fix flickering (use consumer)
		boat.addPassenger(p);
		return boat;
	}

	public static Location tryFindForHorse(Location playerLoc) {
		Location loc = UtilsWorld.findHorseSpace(playerLoc);
		if (loc == null)
			return null;
		loc.setY(playerLoc.getY());
		return loc;
	}
	public static Horse summonHorse(Location l, Player p) {
		Horse horse = l.getWorld().spawn(l, Horse.class, (newHorse) ->
		{
			initHorse(newHorse, p);
		});
		return horse;
	}
	
	public static AbstractHorse summonCustomHorse(Location l, Player p, HorseFormat horseData)
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
				setCustomHorse(newHorse);

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


	public static boolean wasSummoned(Entity e) {
		if(e != null) {
			return e.getScoreboardTags().contains(SBTAG_FROM_TOME);
		}
		
		return false;
	}
	public static void setSummoned(Entity e) {
		if(e != null) {
			e.addScoreboardTag(SBTAG_FROM_TOME);
		}
	}
	public static boolean isCustomHorse(Entity horse) {
		if(horse != null && horse instanceof AbstractHorse) {
			return horse.getScoreboardTags().contains(SBTAG_CUSTOM_HORSE);
		}
		
		return false;
	}
	public static void setCustomHorse(Entity horse) {
		if(horse != null && horse instanceof AbstractHorse) {		
			horse.addScoreboardTag(SBTAG_CUSTOM_HORSE);
		}
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
}
