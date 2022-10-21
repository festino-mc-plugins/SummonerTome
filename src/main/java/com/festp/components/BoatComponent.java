package com.festp.components;

import org.bukkit.Location;
import org.bukkit.TreeSpecies;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import com.festp.utils.SummonUtils;

public class BoatComponent implements ITomeComponent
{
	private static final double BOAT_SEARCHING_RADIUS = 2.5;
	public static final char CODE = 'b';

	TreeSpecies boatWood = TreeSpecies.GENERIC;
	
	public char getCode() {
		return CODE;
	}
	
	public int getPriority() {
		return 10;
	}
	
	public String serialize() {
		return String.valueOf(boatTypeToChar(boatWood));
	}
	
	public void deserialize(String data) {
		if (data == null || data.length() == 0)
			throw new IllegalArgumentException("data must not be null or empty!");
    	boatWood = charToBoatType(data.charAt(0));
	}

	public boolean trySwap(Entity entity)
	{
		if (!(entity instanceof Boat))
			return false;
		Boat boat = (Boat)entity;
		
		if (boatWood == boat.getWoodType())
			return false;
		TreeSpecies prevWood = boatWood;
		
		boatWood = boat.getWoodType();
		boat.setWoodType(prevWood);
		return true;
	}

	public Location getSummonLocation(Location playerLoc) {
		return SummonUtils.tryFindForBoat(playerLoc, BOAT_SEARCHING_RADIUS);
	}

	@Override
	public Entity summon(Player summoner, Location loc) {
		return SummonUtils.summonBoat(loc, summoner, boatWood);
	}

	public void setBoat(TreeSpecies boatType) {
		boatWood = boatType;
	}

	public TreeSpecies getBoat() {
		return boatWood;
	}
	
	private static TreeSpecies charToBoatType(char c) {
    	switch(c)
    	{
    	case 'a': return TreeSpecies.ACACIA;
    	case 'b': return TreeSpecies.BIRCH;
    	case 'd': return TreeSpecies.DARK_OAK;
    	case 'j': return TreeSpecies.JUNGLE;
    	case 'o': return TreeSpecies.GENERIC; // oak
    	case 's': return TreeSpecies.REDWOOD; // spruce
		}
		return null;
	}
	
	private static char boatTypeToChar(TreeSpecies type) {
    	switch(type)
    	{
    	case ACACIA: return 'a';
    	case BIRCH: return 'b';
    	case DARK_OAK: return 'd';
    	case JUNGLE: return 'j';
    	case GENERIC: return 'o'; // oak
    	case REDWOOD: return 's'; // spruce
		}
		return 'o';
	}
}
