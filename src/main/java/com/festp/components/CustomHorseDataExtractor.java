package com.festp.components;

import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import com.festp.handlers.IDataExtractor;

public class CustomHorseDataExtractor implements IDataExtractor
{
	public ITomeComponent extract(ItemStack oldTome, Entity entity) {
		if (!(entity instanceof AbstractHorse))
			return null;
		HorseData horseData = HorseData.fromHorse((AbstractHorse)entity);
	    CustomHorseComponent comp = new CustomHorseComponent();
	    comp.setHorseData(horseData);
	    return comp;
	}
}
