package com.festp.components.boat;

import org.bukkit.Location;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.festp.components.ITomeComponent;
import com.festp.utils.SummonUtils;
import com.festp.utils.UtilsVersion;

public class BoatComponent implements ITomeComponent
{
	private static final double BOAT_SEARCHING_RADIUS = 2.5;
	public static final String CODE = "boat";
	
	public static final IBoatDataConverter CONVERTER = getConverter();
	
	private static IBoatDataConverter getConverter() {
		if (UtilsVersion.SUPPORTS_PALE_OAK_BOAT)
			return new BoatDataConverter1_21_2();
		else if (UtilsVersion.SUPPORTS_CHEST_BOAT)
			return new BoatDataConverter1_19();
		else
			return new BoatDataConverter1_18();
	}

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
		
		BoatData newData = CONVERTER.fromBoat(boat);
		if (boatData == null || newData == null)
			return false;
		
		CONVERTER.applyToBoat(boatData, boat);
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
