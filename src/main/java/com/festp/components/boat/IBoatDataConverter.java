package com.festp.components.boat;

import org.bukkit.Material;
import org.bukkit.entity.Boat;

public interface IBoatDataConverter
{
	public BoatData fromBoat(Boat boat);
	public void applyToBoat(BoatData data, Boat boat);
	public BoatData fromBoatMaterial(Material m);
	public Material[] getSupportedBoats();
}
