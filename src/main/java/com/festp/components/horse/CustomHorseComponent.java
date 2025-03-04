package com.festp.components.horse;

import org.bukkit.Location;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.festp.components.ITomeComponent;
import com.festp.utils.SummonUtils;

public class CustomHorseComponent implements ITomeComponent
{
	private static final double SEARCHING_RADIUS = 1.5;
	private static final String SERIALIZATION_NO_HORSE_DATA = "null";
	public static final String CODE = "custom_horse";
	
	private HorseData horseData;
	
	public CustomHorseComponent() {
		// TODO avoid generating data in constructor (it is the only way to serialize it now)
		horseData = HorseData.generate();
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
		if (data.equals(SERIALIZATION_NO_HORSE_DATA))
			horseData = null;
		else
			horseData = HorseData.fromString(data);
	}

	public boolean trySwap(Entity entity)
	{
		if (!(entity instanceof AbstractHorse))
			return false;
		AbstractHorse horse = (AbstractHorse)entity;
		
		if (horse.getInventory().getSaddle() == null)
			return false;
		
		HorseData oldData = horseData;
		horseData = HorseData.fromHorse(horse);
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

	public HorseData getHorseData() {
		return horseData;
	}
	public void setHorseData(HorseData horseData) {
		this.horseData = horseData;
	}
}
