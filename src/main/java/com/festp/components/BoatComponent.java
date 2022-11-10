package com.festp.components;

import org.bukkit.Location;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import com.festp.utils.SummonUtils;

public class BoatComponent implements ITomeComponent
{
	private static final double BOAT_SEARCHING_RADIUS = 2.5;
	public static final String CODE = "boat";

	BoatData boatData = new BoatData();
	
	public String getCode() {
		return CODE;
	}
	
	public int getPriority() {
		return 10;
	}
	
	public String serialize() {
		return boatData.toString();
	}
	
	public void deserialize(String data) {
		boatData = BoatData.fromString(data);
	}

	public boolean trySwap(Entity entity)
	{
		if (!(entity instanceof Boat))
			return false;
		Boat boat = (Boat)entity;
		
		BoatData newData = BoatData.fromBoat(boat);
		boatData.applyToBoat(boat);
		boatData = newData;
		
		return true;
	}

	public boolean canSummon(Player player) {
		return true;
	}
	
	public Location getSummonLocation(Location playerLoc) {
		return SummonUtils.tryFindForBoat(playerLoc, BOAT_SEARCHING_RADIUS);
	}

	public Entity summon(Player summoner, Location loc) {
		return SummonUtils.summonBoat(loc, summoner, boatData);
	}

	public void setBoatData(BoatData data) {
		boatData = data;
	}

	public BoatData getBoatData() {
		return boatData;
	}
}
