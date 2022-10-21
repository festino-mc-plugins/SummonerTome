package com.festp.components;

import java.util.Comparator;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface ITomeComponent
{
	public char getCode();
	public int getPriority();
	public String serialize();
	public void deserialize(String data);
	public boolean trySwap(Entity entity);
	public Location getSummonLocation(Location playerLoc);
	public Entity summon(Player summoner, Location loc);
	
	/** Sorts components in descending order of priority */
	public static class ComponentComparator implements Comparator<ITomeComponent> {
		@Override
		public int compare(ITomeComponent a, ITomeComponent b) {
			return Integer.compare(b.getPriority(), a.getPriority());
		}
	}
}
