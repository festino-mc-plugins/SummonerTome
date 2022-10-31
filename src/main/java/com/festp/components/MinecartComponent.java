package com.festp.components;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.festp.utils.SummonUtils;

public class MinecartComponent implements ITomeComponent
{
	private static final double RAIL_SEARCHING_RADIUS = 1.5;
	public static final char CODE = 'm';
	
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

	public boolean canSummon(Player player) {
		return true;
	}
	
	public Location getSummonLocation(Location playerLoc) {
		Location res = SummonUtils.tryFindForMinecart(playerLoc, RAIL_SEARCHING_RADIUS);
		if (res == null)
			return null;
		return res.add(0.0, 0.01, 0.0); // workaround for rails on ice
	}

	public Entity summon(Player summoner, Location loc) {
		return SummonUtils.summonMinecart(loc, summoner);
	}
}
