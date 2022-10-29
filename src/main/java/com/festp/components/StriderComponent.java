package com.festp.components;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.festp.utils.SummonUtils;
import com.festp.utils.Utils;

public class StriderComponent implements ITomeComponent
{
	private static final double LAVA_SEARCHING_RADIUS = 2.5;
	private static final double SEARCHING_RADIUS = 2.5;
	public static final char CODE = 's';
	
	public char getCode() {
		return CODE;
	}
	
	public int getPriority() {
		return 9;
	}
	
	public String serialize() {
		return "";
	}
	
	public void deserialize(String data) { }

	public boolean trySwap(Entity entity) {
		return false;
	}

	public boolean canSummon(Player player) {
		return Utils.checkHotbar(player, Material.WARPED_FUNGUS_ON_A_STICK);
	}
	
	public Location getSummonLocation(Location playerLoc) {
		Location onLava = SummonUtils.tryFindForStrider(playerLoc.clone(), LAVA_SEARCHING_RADIUS);
		if (onLava != null)
			return onLava;
		return SummonUtils.tryFindForPig(playerLoc, SEARCHING_RADIUS);
	}

	public Entity summon(Player summoner, Location loc) {
		return SummonUtils.summonStrider(loc, summoner);
	}
}
