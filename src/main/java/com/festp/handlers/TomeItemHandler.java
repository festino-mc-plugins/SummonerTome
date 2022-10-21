package com.festp.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Repairable;

import com.festp.components.CustomHorseComponent;
import com.festp.components.HorseFormat;
import com.festp.components.ITomeComponent;
import com.festp.tome.SummonerTome;
import com.festp.tome.TomeType;
import com.festp.utils.NBTUtils;

public class TomeItemHandler
{
	private static final int REPAIR_COST = 1000;
	private static final String TOME_NBT_KEY = "summonertome";

	public static final String NAME_CUSTOM_HORSE = "Advanced %s";
	public static final String NAME_ALL_TEMPLATE = "United tome";
	public static final String NAME_MIXED_TEMPLATE = "Combined tome";
	public static final String NAME_ONE_TYPE_TEMPLATE = "%s tome";
	public static final String LORE_SEP = ", ";
	public static final String LORE_OR = " or ";
	public static final String LORE_TEMPLATE = "Summons %s";
	public static final String EN_NAME_CUSTOM_ALL =  "Advanced united tome";
	public static final String EN_LORE_CUSTOM_ALL =  "Summons minecart, boat or custom horse";

	public static ItemStack getNewTome(TomeType type)
	{
		return getNewTome(EnumSet.of(type));
	}
	public static ItemStack getNewTome(EnumSet<TomeType> typeSet)
	{
    	ItemStack tome = new ItemStack(Material.ENCHANTED_BOOK);
		TomeType[] types = typeSet.toArray(new TomeType[0]);
		ITomeComponent[] components = new ITomeComponent[types.length];
		for (int i = 0; i < types.length; i++) {
			components[i] = types[i].getComponent();
			if (components[i] instanceof CustomHorseComponent)
				((CustomHorseComponent)components[i]).setHorseData(HorseFormat.generate());
		}
    	return applyTome(tome, new SummonerTome(components));
	}
	
	public static ItemStack applyTome(ItemStack tome, SummonerTome newTome)
	{
		TomeType[] allTypes = TomeType.values();
		EnumSet<TomeType> excludingTypes = EnumSet.allOf(TomeType.class);
		String customHorseStr = "%s";
		List<TomeType> types = new ArrayList<>();
		for (int i = 0; i < allTypes.length; i++)
		{
			TomeType type = allTypes[i];
			if (!newTome.hasComponent(type.getComponentClass()))
				continue;
			
			if (type == TomeType.CUSTOM_HORSE)
				customHorseStr = NAME_CUSTOM_HORSE;
			excludingTypes.remove(type);

			types.add(type);
		}
		StringBuilder list = new StringBuilder();
		for (int i = 0; i < types.size(); i++)
		{
			if (i > 0) {
				if (i == types.size() - 1)
					list.append(LORE_OR);
				else
					list.append(LORE_SEP);
			}
			list.append(getForLore(types.get(i)));
		}
		String lore = String.format(LORE_TEMPLATE, list);
		String name;
		if (types.size() == 1) {
			String entityName = getForName(types.get(0));
			entityName = Character.toUpperCase(entityName.charAt(0)) + entityName.substring(1);
			name = String.format(NAME_ONE_TYPE_TEMPLATE, entityName);
		}
		else if (excludingTypes.size() == 1) { // error if both HORSE and CUSTOM_HORSE
			name = NAME_ALL_TEMPLATE;
		}
		else {
			name = NAME_MIXED_TEMPLATE;
		}
		name = String.format(customHorseStr, name);
		
    	Repairable rmeta = (Repairable) tome.getItemMeta();
    	rmeta.setRepairCost(REPAIR_COST);
    	rmeta.setDisplayName(name);
    	rmeta.setLore(Arrays.asList(lore));
    	tome.setItemMeta(rmeta);
    	tome = newTome.setTome(tome);
    	return tome;
	}
	
	private static String getForName(TomeType type)
	{
		if (type == TomeType.CUSTOM_HORSE)
			return getForName(TomeType.HORSE);
		return getForLore(type);
	}
	private static String getForLore(TomeType type)
	{
		// TODO configurable names
		return type.name().replace('_', ' ').toLowerCase();
	}

	public static boolean hasTag(ItemStack item) {
		return NBTUtils.hasString(item, TOME_NBT_KEY);
	}

	public static String getTag(ItemStack item) {
		return NBTUtils.getString(item, TOME_NBT_KEY);
	}

	public static ItemStack setTag(ItemStack item, String data) {
		return NBTUtils.setString(item, TOME_NBT_KEY, data);
	}
}
