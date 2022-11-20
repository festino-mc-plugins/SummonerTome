package com.festp.tome;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.festp.components.ITomeComponent;

public class DisabledComponent implements ITomeComponent
{
	final String code;
	String data;
	
	public DisabledComponent(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
	public int getPriority() {
		return -1;
	}
	public String serialize() {
		return data;
	}
	public void deserialize(String data) {
		this.data = data;
	}
	public boolean trySwap(Entity entity) {
		return false;
	}
	public boolean canSummon(Player player) {
		return false;
	}
	public Location getSummonLocation(Location playerLoc) {
		return null;
	}
	public Entity summon(Player summoner, Location loc) {
		return null;
	}

}
