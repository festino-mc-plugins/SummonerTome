package com.festp.components.pig;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.festp.components.ITomeComponent;
import com.festp.utils.SummonUtils;
import com.festp.utils.Utils;

public class PigComponent implements ITomeComponent
{
	private static final double SEARCHING_RADIUS = 2.5;
	public static final String CODE = "pig";
	
	public String getCode() {
		return CODE;
	}
	
	public int getPriority() {
		return 8;
	}
	
	public String serialize() {
		return "";
	}
	
	public void deserialize(String data) { }

	public boolean trySwap(Entity entity) {
		return false;
	}

	public boolean canSummon(Player player) {
		return Utils.checkHotbar(player, Material.CARROT_ON_A_STICK);
	}
	
	public Location getSummonLocation(Location playerLoc) {
		return SummonUtils.tryFindForPig(playerLoc, SEARCHING_RADIUS);
	}

	public Entity summon(Player summoner, Location loc) {
		return SummonUtils.summonPig(loc, summoner);
	}
}
