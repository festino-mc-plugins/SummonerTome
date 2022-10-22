package com.festp.components;

import org.bukkit.entity.Boat;

public interface IBoatDataConverter
{
	public BoatData fromBoat(Boat boat);
	public void applyToBoat(BoatData data, Boat boat);
}