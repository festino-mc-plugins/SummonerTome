package com.festp.components;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.festp.utils.SummonUtils;

public class StriderComponent implements ITomeComponent
{
	private static final double LAVA_SEARCHING_RADIUS = 2.5;
	public static final char CODE = 's';
	
	public char getCode() {
		return CODE;
	}
	
	public int getPriority() {
		return 10;
	}
	
	public String serialize() {
		return "";
	}
	
	public void deserialize(String data) { }

	public boolean trySwap(Entity entity) {
		return false;
	}
	
	public Location getSummonLocation(Location playerLoc) {
		return SummonUtils.tryFindForStrider(playerLoc, LAVA_SEARCHING_RADIUS);
	}

	public Entity summon(Player summoner, Location loc) {
		return SummonUtils.summonStrider(loc, summoner);
	}
}
