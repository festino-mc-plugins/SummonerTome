package com.festp.crafting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Repairable;

import com.festp.components.ITomeComponent;
import com.festp.tome.ComponentManager;
import com.festp.tome.SummonerTome;
import com.festp.utils.NBTUtils;

public class TomeItemBuilder
{
	private static final int REPAIR_COST = 1000;
	private static final String TOME_NBT_KEY = "summonertome";

	// TODO localization
	public static final String NAME_ALL_TEMPLATE = "United tome";
	public static final String NAME_MIXED_TEMPLATE = "Combined tome";
	public static final String NAME_ONE_TYPE_TEMPLATE = "%s tome";
	public static final String LORE_SEP = ", ";
	public static final String LORE_OR = " or ";
	public static final String LORE_TEMPLATE = "Summons %s";
	
	private static ComponentManager componentManager;
	public static void setComponentManager(ComponentManager componentManager) {
		TomeItemBuilder.componentManager = componentManager;
	}
	
	public static ItemStack getNewTome(ITomeComponent component)
	{
    	return getNewTome(new ITomeComponent[] { component });
	}
	public static ItemStack getNewTome(ITomeComponent[] components)
	{
    	ItemStack tome = new ItemStack(Material.ENCHANTED_BOOK);
    	// recreate all components (just in case there is random)
		for (int i = 0; i < components.length; i++) {
			components[i] = componentManager.fromCode(components[i].getCode());
		}
    	return applyTome(tome, new SummonerTome(components));
	}
	
	public static ItemStack applyTome(ItemStack tome, SummonerTome newTome)
	{
		String[] allComponents = componentManager.getLoadedComponents();
		String format = "%s";
		List<String> codes = new ArrayList<>();
		for (int i = 0; i < allComponents.length; i++)
		{
			String code = allComponents[i];
			if (!newTome.hasComponent(code))
				continue;
			
			String componentFormat = componentManager.getLangInfo(code).tomeNameFormat;
			if (componentFormat != null)
				format = String.format(componentFormat, format);

			codes.add(code);
		}
		boolean isAnyAllTome = codes.size() == componentManager.getAll().length;
		StringBuilder list = new StringBuilder();
		for (int i = 0; i < codes.size(); i++)
		{
			if (i > 0) {
				if (i == codes.size() - 1)
					list.append(LORE_OR);
				else
					list.append(LORE_SEP);
			}
			String loreName = componentManager.getLangInfo(codes.get(i)).lorePetName;
			list.append(loreName);
		}
		String lore = String.format(LORE_TEMPLATE, list);
		
		String name;
		boolean decorateName = true;
		if (codes.size() == 1) {
			name = componentManager.getLangInfo(codes.get(0)).soloTomeName;
			if (name != null) {
				decorateName = false;
			} else {
				String loreName = componentManager.getLangInfo(codes.get(0)).lorePetName;
				String entityName = String.format(NAME_ONE_TYPE_TEMPLATE, capitalize(loreName.toLowerCase()));
				entityName = Character.toUpperCase(entityName.charAt(0)) + entityName.substring(1);
				name = String.format(NAME_ONE_TYPE_TEMPLATE, entityName);
			}
		} else if (isAnyAllTome) {
			name = NAME_ALL_TEMPLATE;
		} else {
			name = NAME_MIXED_TEMPLATE;
		}
		if (decorateName) {
			name = String.format(format, name);
		}
		
    	Repairable rmeta = (Repairable) tome.getItemMeta();
    	rmeta.setRepairCost(REPAIR_COST);
    	rmeta.setDisplayName(name);
    	rmeta.setLore(Arrays.asList(lore));
    	tome.setItemMeta(rmeta);
    	tome = newTome.setTome(tome);
    	return tome;
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
	
	private static String capitalize(String str) {
		if (str.length() == 0)
			return str;
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
}
