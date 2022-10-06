package com.festp.tome;


import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.inventory.ItemStack;

import com.festp.tome.TomeItemHandler.TomeType;
import com.festp.utils.NBTUtils;

public class TomeFormatter {
	
	public static ItemStack setType(ItemStack tome, TomeType type)
	{
    	switch (type) {
    	case MINECART:
    		tome = TomeFormatter.setTome(tome, 'm', " "); break;
    	case BOAT:
    		tome = TomeFormatter.setTome(tome, 'b', "o"); break; // "o" means "oak"
    	case HORSE:
    		tome = TomeFormatter.setTome(tome, 'h', " "); break;
    	case CUSTOM_HORSE:
    		tome = TomeFormatter.setTome(tome, 'H', " "); break;
    	case ALL:
    		tome = TomeFormatter.setTome(tome, 'a', " "); break;
    	case CUSTOM_ALL:
    		tome = TomeFormatter.setTome(tome, 'A', "o"); break;
    	}
    	return tome;
	}

	public static ItemStack setBoatType(ItemStack tome, TreeSpecies type)
	{
	    if (isTome(tome)) {
    		char[] info =  NBTUtils.getString(tome, TomeItemHandler.TOME_NBT_KEY).toCharArray();
        	switch(type)
        	{
        	case ACACIA: info[1] = 'a'; break;
        	case BIRCH: info[1] = 'b'; break;
        	case DARK_OAK: info[1] = 'd'; break;
        	case JUNGLE: info[1] = 'j'; break;
        	case GENERIC: info[1] = 'o'; break;
        	case REDWOOD: info[1] = 's'; break;
			}
        	tome = NBTUtils.setString(tome, TomeItemHandler.TOME_NBT_KEY, new String(info));
        }
		return tome;
	}
	
	public static TreeSpecies getBoatType(ItemStack tome) {
	    if (isTome(tome)) {
        	String info = NBTUtils.getString(tome, TomeItemHandler.TOME_NBT_KEY);
        	switch(info.charAt(1))
        	{
        	case 'a': return TreeSpecies.ACACIA;
        	case 'b': return TreeSpecies.BIRCH;
        	case 'd': return TreeSpecies.DARK_OAK;
        	case 'j': return TreeSpecies.JUNGLE;
        	case 'o': return TreeSpecies.GENERIC; //oak
        	case 's': return TreeSpecies.REDWOOD; //spruce
			}
        }
		return TreeSpecies.GENERIC;
	}
	
	
	
	public static HorseFormat getHorseData(ItemStack tome) {
	    if (isTome(tome)) {
	    	String info = NBTUtils.getString(tome, TomeItemHandler.TOME_NBT_KEY);
	    	if (info.length() > 2) {
	    		return HorseFormat.fromString(info.substring(2));
	    	}
	    }
    	return null;
	}

	public static ItemStack setHorseData(ItemStack tome, HorseFormat data) {
        String newData = NBTUtils.getString(tome, TomeItemHandler.TOME_NBT_KEY).substring(0, 2) + data.toString();
		return NBTUtils.setString(tome, TomeItemHandler.TOME_NBT_KEY, newData);
	}
	
	public static ItemStack setBoat(ItemStack tome, ItemStack boat) {
		Material woodType = boat.getType();
		if(woodType == Material.ACACIA_BOAT)
			return setTome(tome, 'b', "a");
		else if(woodType == Material.BIRCH_BOAT)
			return setTome(tome, 'b', "b");
		else if(woodType == Material.DARK_OAK_BOAT)
			return setTome(tome, 'b', "d");
		else if(woodType == Material.JUNGLE_BOAT)
			return setTome(tome, 'b', "j");
		else if(woodType == Material.SPRUCE_BOAT)
			return setTome(tome, 'b', "s");
		return tome;
	}
	public static TomeType getTomeType(ItemStack item) {
        if (isTome(item)) {
        	String info = NBTUtils.getString(item, TomeItemHandler.TOME_NBT_KEY);
        	if(info.startsWith("m")) {
				return TomeType.MINECART;
			}
			else if(info.startsWith("b")) {
				return TomeType.BOAT;
			}
			else if(info.startsWith("h")) {
				return TomeType.HORSE;
			}
			else if(info.startsWith("H")) {
				return TomeType.CUSTOM_HORSE;
			}
			else if(info.startsWith("a")) {
				return TomeType.ALL;
			}
			else if(info.startsWith("A")) {
				return TomeType.CUSTOM_ALL;
			}
        }
		return null;
	}
	public static ItemStack setTome(ItemStack item, char data, String metadata) {
		return NBTUtils.setString(item, TomeItemHandler.TOME_NBT_KEY, data + metadata);
	}
	
	public static boolean isTome(ItemStack item) {
		return NBTUtils.hasString(item, TomeItemHandler.TOME_NBT_KEY);
	}
}
