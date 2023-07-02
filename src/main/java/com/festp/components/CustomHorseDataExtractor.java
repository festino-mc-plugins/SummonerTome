package com.festp.components;

import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import com.festp.handlers.IDataExtractor;
import com.festp.tome.SummonerTome;

public class CustomHorseDataExtractor implements IDataExtractor
{
	public ITomeComponent extract(ItemStack oldTome, Entity entity) {
		if (!(entity instanceof AbstractHorse))
			return null;
		CustomHorseComponent oldComp = SummonerTome.getTome(oldTome).getComponent(CustomHorseComponent.class);
		HorseData horseData = oldComp.getHorseData();
		HorseData newData = HorseData.fromHorse((AbstractHorse)entity);
		horseData.inventory = newData.inventory;
		
		CustomHorseComponent comp = new CustomHorseComponent();
	    comp.setHorseData(horseData);
	    return comp;
	}
}
