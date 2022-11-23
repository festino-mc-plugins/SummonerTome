package com.festp.components;

import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import com.festp.handlers.IDataExtractor;

public class BoatDataExtractor implements IDataExtractor
{
	public ITomeComponent extract(ItemStack oldTome, Entity entity) {
		if (!(entity instanceof Boat))
			return null;
		BoatData boatData = BoatData.fromBoat((Boat)entity);
	    BoatComponent comp = new BoatComponent();
	    comp.setBoatData(boatData);
	    return comp;
	}
}
