package com.festp.components;

import org.bukkit.Location;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.festp.utils.SummonUtils;

public class CustomHorseComponent implements ITomeComponent
{
	private static final double SEARCHING_RADIUS = 1.5;
	private static final String SERIALIZATION_NO_HORSE_DATA = "null";
	public static final String CODE = "custom_horse";
	
	private HorseFormat horseData;
	
	public CustomHorseComponent() {
		horseData = HorseFormat.generate();
	}
	
	public String getCode() {
		return CODE;
	}
	
	public int getPriority() {
		return 5;
	}

	public String serialize() {
		if (horseData == null)
			return SERIALIZATION_NO_HORSE_DATA;
		return horseData.toString();
	}
	
	public void deserialize(String data) {
		if (data == SERIALIZATION_NO_HORSE_DATA)
			horseData = null;
		else
			horseData = HorseFormat.fromString(data);
	}

	public boolean trySwap(Entity entity)
	{
		if (!(entity instanceof AbstractHorse))
			return false;
		AbstractHorse horse = (AbstractHorse)entity;
		
		if (horse.getInventory().getSaddle() == null)
			return false;
		
		HorseFormat oldData = horseData;
		horseData = HorseFormat.fromHorse(horse);
		if (oldData == null)
			horse.remove();
		else
			oldData.applyToHorse(horse);
		return true;
	}

	public boolean canSummon(Player player) {
		return true;
	}
	
	public Location getSummonLocation(Location playerLoc) {
		return SummonUtils.tryFindForHorse(playerLoc, SEARCHING_RADIUS);
	}

	public Entity summon(Player summoner, Location loc) {
		return SummonUtils.summonCustomHorse(loc, summoner, horseData);
	}

	public HorseFormat getHorseData() {
		return horseData;
	}
	public void setHorseData(HorseFormat horseData) {
		this.horseData = horseData;
	}
}
