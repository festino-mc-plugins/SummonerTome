package com.festp.components.boat;

import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import com.festp.components.ITomeComponent;
import com.festp.handlers.IDataExtractor;

public class BoatDataExtractor implements IDataExtractor
{
	public ITomeComponent extract(ItemStack oldTome, Entity entity) {
		if (!(entity instanceof Boat))
			return null;
		BoatData boatData = BoatComponent.CONVERTER.fromBoat((Boat)entity); // cannot be null since the boat was summoned
	    BoatComponent comp = new BoatComponent();
	    comp.setBoatData(boatData);
	    return comp;
	}
}
