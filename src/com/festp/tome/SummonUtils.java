package com.festp.tome;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.festp.utils.NBTUtils;
import com.festp.utils.UtilsWorld;

public class SummonUtils
{
	private static final String TAG_HAS_SUMMONED = "hassummoned"; 
	private static final String SBTAG_CUSTOM_HORSE = "customhorse"; 
	private static final String SBTAG_FROM_TOME = "fromtome"; 
	
	private static final Material[] BOAT_BLOCKS =
			{Material.WATER, Material.ICE, Material.PACKED_ICE, Material.BLUE_ICE, Material.FROSTED_ICE, Material.SEA_PICKLE, Material.SEAGRASS, Material.TALL_SEAGRASS};
	private static final Material[] MINECART_BLOCKS =
			{Material.RAIL, Material.ACTIVATOR_RAIL, Material.DETECTOR_RAIL, Material.POWERED_RAIL};
	
	public static Location tryFindForMinecart(Location playerLoc, double horRadius) {
		return UtilsWorld.searchBlock(playerLoc, MINECART_BLOCKS, horRadius, false);
	}
	public static Minecart summonMinecart(Location l, Player p, boolean mainHand) {
		Minecart mc = l.getWorld().spawn(l, Minecart.class);
		mc.addPassenger(p);
		setSummoned(mc);
		setHasSummoned(p, mainHand, mc);
		return mc;
	}
	
	public static Location tryFindForBoat(Location playerLoc, double horRadius) {
		// TODO watered bottom blocks
		// TODO smarter function for boats:
		//      fill one square grid, then check player loc
		//      and iterate squares on smaller grid(0.5 block) to find the nearest place to spawn
		Location loc = playerLoc.clone();
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
	public static Boat summonBoat(Location l, Player p, boolean mainHand, TreeSpecies type) {
		l.setPitch(p.getLocation().getPitch());
		l.setYaw(p.getLocation().getYaw());
		Boat boat = l.getWorld().spawn(l, Boat.class);
		boat.setWoodType(type);
		boat.addPassenger(p);
		setSummoned(boat);
		setHasSummoned(p, mainHand, boat);
		return boat;
	}

	public static Location tryFindForHorse(Location playerLoc) {
		return UtilsWorld.findHorseSpace(playerLoc);
	}
	public static Horse summonHorse(Location l, Player p, boolean mainHand) {
		Horse horse = l.getWorld().spawn(l, Horse.class, (newHorse) ->
		{
			newHorse.setTamed(true);
			newHorse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
			newHorse.setOwner(p);
			newHorse.addPassenger(p);
			setSummoned(newHorse);
			setHasSummoned(p, mainHand, newHorse);
		});
		return horse;
	}
	
	public static AbstractHorse summonCustomHorse(Location l, Player p, boolean mainHand)
	{
		HorseFormat horseData = getHorseData(p, mainHand);
		
		Class<? extends AbstractHorse> type;
		if (horseData == null) {
			type = Horse.class;
		} else {
			type = horseData.getHorseClass();
		}
		
		AbstractHorse horse = summonCustomHorse(l, type, new HorseSetter()
		{
			@Override
			public void set(AbstractHorse newHorse)
			{
				newHorse.setTamed(true);
				newHorse.getInventory().setSaddle(new ItemStack(Material.SADDLE)); // if horseData == null
				newHorse.setOwner(p);
				newHorse.addPassenger(p);
				setSummoned(newHorse);
				setCustomHorse(newHorse);
				setHasSummoned(p, mainHand, newHorse);
				
				if (horseData == null)
					setHorseData(p, mainHand, HorseFormat.fromHorse(newHorse));
				else
					horseData.applyToHorse(newHorse);

				
				String horseName = getCustomName(p, mainHand);
				if (horseName != null) {
					newHorse.setCustomName(horseName);
				}
				if (!newHorse.isAdult())
					newHorse.setAgeLock(true);
			}
		});
		
		return horse;
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
	private static void setHasSummoned(Player p, boolean mainHand, Entity summoned)
	{
		ItemStack tome = getTome(p, mainHand);
		tome = setHasSummoned(tome, summoned.getUniqueId());
		setTome(p, mainHand, tome);
	}
	private static HorseFormat getHorseData(Player p, boolean mainHand)
	{
		ItemStack tome = getTome(p, mainHand);
		return TomeFormatter.getHorseData(tome);
	}
	private static void setHorseData(Player p, boolean mainHand, HorseFormat horseData)
	{
		ItemStack tome = getTome(p, mainHand);
		tome = TomeFormatter.setHorseData(tome, horseData);
		setTome(p, mainHand, tome);
	}

	private static String getCustomName(Player p, boolean mainHand) {
		ItemStack tome = getTome(p, mainHand);
		if (!tome.getItemMeta().hasDisplayName())
			return null;
		String name = tome.getItemMeta().getDisplayName();
		boolean wasRenamed = !(TomeItemHandler.EN_NAME_CUSTOM_HORSE.contains(name) || TomeItemHandler.EN_NAME_CUSTOM_ALL.contains(name));
		if (!wasRenamed)
			return null;
		return name;
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
