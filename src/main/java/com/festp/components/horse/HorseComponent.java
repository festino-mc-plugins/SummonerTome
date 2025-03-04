package com.festp.components.horse;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.festp.components.ITomeComponent;
import com.festp.utils.SummonUtils;

public class HorseComponent implements ITomeComponent
{
	private static final double SEARCHING_RADIUS = 1.5;
	public static final String CODE = "horse";
	
	public String getCode() {
		return CODE;
	}
	
	public int getPriority() {
		return 2;
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
		return SummonUtils.tryFindForHorse(playerLoc, SEARCHING_RADIUS);
	}

	public Entity summon(Player summoner, Location loc) {
		return SummonUtils.summonHorse(loc, summoner);
	}
}
