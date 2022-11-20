package com.festp.components;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Player;
import org.bukkit.inventory.AbstractHorseInventory;
import org.bukkit.inventory.ItemStack;

import com.festp.handlers.TomeEntityHandler;
import com.festp.inventory.InventorySerializer;
import com.festp.utils.SummonUtils;
import com.festp.utils.Utils;
import com.festp.utils.UtilsRandom;
import com.festp.utils.SummonUtils.HorseSetter;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class HorseData {
	double maxHealth;
	double speed;
	double jumpStrength;
	Class<? extends AbstractHorse> type;
	ItemStack[] inventory;
	boolean isAdult;
	
	// HORSE
	Color horseColor;
	Style horseStyle;
	
	// CHESTED
	boolean chestedIsCarrying;
	
	private HorseData() {}
	
	@Override
	public String toString()
	{
		JsonObject json = new JsonObject();
		json.addProperty("type", Utils.getShortBukkitClass(type));
		json.addProperty("max_health", maxHealth);
		json.addProperty("movement_speed", speed);
		json.addProperty("jump_strength", jumpStrength);
		json.addProperty("is_adult", isAdult);
		json.addProperty("inventory", InventorySerializer.saveInventory(inventory));
		if (Horse.class.isAssignableFrom(type)) {
			json.addProperty("color", horseColor.name());
			json.addProperty("style", horseStyle.name());
		} else if (ChestedHorse.class.isAssignableFrom(type)) {
			json.addProperty("is_chested", chestedIsCarrying);
		}
		return json.toString();
	}
	
	@SuppressWarnings("unchecked")
	public static HorseData fromString(String s)
	{
		if (s == null || s.equals("")) {
			return null;
		}
		
		JsonObject json;
		try {
			Object parsed = JsonParser.parseString(s);
			json = (JsonObject) parsed;
		} catch (JsonParseException e) {
			System.out.print("[] SummonerTome JSON parse error: " + s);
			e.printStackTrace();
			return null;
		}
		
		HorseData res = new HorseData();
		Class<?> resClass = Utils.getBukkitClass(json.get("type").getAsString());
		if (resClass == null) {
			System.out.print("[] SummonerTome horse class parse error: " + s);
			return null;
		}
		res.type = (Class<? extends AbstractHorse>) resClass;
		res.maxHealth = json.get("max_health").getAsDouble();
		res.speed = json.get("movement_speed").getAsDouble();
		res.jumpStrength = json.get("jump_strength").getAsDouble();
		res.isAdult = json.get("is_adult").getAsBoolean();
		
		/*List<Map<String, Object>> inv = (List<Map<String, Object>>) json.get("inventory");
		res.inventory = new ItemStack[inv.size()];
		int i = 0;
		for (Map<String, Object> item : inv) {
			if (item != null)
				res.inventory[i] = ItemStack.deserialize(item);
			i++;
		}*/
		String inv = json.get("inventory").getAsString();
		res.inventory = InventorySerializer.loadInventory(inv);

		if (Horse.class.isAssignableFrom(res.type)) {
			res.horseColor = Color.valueOf(json.get("color").getAsString());
			res.horseStyle = Style.valueOf(json.get("style").getAsString());
		} else if (ChestedHorse.class.isAssignableFrom(res.type)) {
			res.chestedIsCarrying = json.get("is_chested").getAsBoolean();
		}

		return res;
	}
	
	public Class<? extends AbstractHorse> getHorseClass() {
		return type;
	}

	public void applyToHorse(AbstractHorse horse)
	{
		HorseSetter setter = new SummonUtils.HorseSetter() {
			@Override
			public void set(AbstractHorse newHorse) {
				newHorse.setTamed(true);
				newHorse.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth);
				newHorse.setHealth(maxHealth);
				newHorse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(speed);
				newHorse.setJumpStrength(jumpStrength);
				if (isAdult) 	newHorse.setAdult();
				else 			newHorse.setBaby();
				
				if (newHorse instanceof Horse) {
					Horse h = (Horse) newHorse;
					h.setColor(horseColor);
					h.setStyle(horseStyle);
				} else if (newHorse instanceof ChestedHorse) { // before inv filling
					ChestedHorse ch = (ChestedHorse) newHorse;
					ch.setCarryingChest(chestedIsCarrying);
				}
				
				AbstractHorseInventory hinv = newHorse.getInventory();
				int i = 0;
				for (ItemStack is : inventory) {
					hinv.setItem(i, is);
					i++;
				}
			}
		};
		if (!type.isAssignableFrom(horse.getClass())) {
			Location loc = horse.getLocation();
			AbstractHorse newHorse = SummonUtils.summonCustomHorse(loc, type, setter);
			TomeEntityHandler.replaceEntity(horse, newHorse);
			horse = newHorse;
		} else {
			setter.set(horse);
		}
	}

	public static HorseData fromHorse(AbstractHorse horse)
	{
		HorseData res = new HorseData();
		res.type = horse.getClass();
		res.maxHealth = horse.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
		res.speed = horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue();
		res.jumpStrength = horse.getAttribute(Attribute.HORSE_JUMP_STRENGTH).getBaseValue();
		res.isAdult = horse.isAdult();
		res.inventory = horse.getInventory().getContents();

		if (horse instanceof Horse) {
			Horse h = (Horse) horse;
			res.horseColor = h.getColor();
			res.horseStyle = h.getStyle();
		} else if (horse instanceof ChestedHorse) {
			ChestedHorse ch = (ChestedHorse) horse;
			res.chestedIsCarrying = ch.isCarryingChest();
		}

		return res;
	}

	public static HorseData generate() {
		return generate(Horse.class);
	}
	public static HorseData generate(Class<? extends AbstractHorse> type)
	{
		HorseData res = generateBySpawn(type);
		if (res == null)
		{
			res = new HorseData();
			res.type = type;
			res.maxHealth = UtilsRandom.getInt(15, 30);
			res.speed = UtilsRandom.getDouble(0.1125, 0.3375);
			res.jumpStrength = UtilsRandom.getDouble(0.4, 1.0);
			res.inventory = new ItemStack[1];
			res.isAdult = true;
			
			if (type.isAssignableFrom(Horse.class)) {
				res.horseColor = UtilsRandom.get(Horse.Color.values());
				res.horseStyle = UtilsRandom.get(Horse.Style.values());
			}
			
			if (type.isAssignableFrom(ChestedHorse.class)) {
				res.chestedIsCarrying = false;
			}
		}
		res.inventory[0] = new ItemStack(Material.SADDLE);
		return res;
	}
	private static HorseData generateBySpawn(Class<? extends AbstractHorse> type)
	{
		Location tempLocation = null;
		World world = Bukkit.getWorlds().get(0);
		if (world != null) {
			if (world.getKeepSpawnInMemory()) {
				tempLocation = world.getSpawnLocation();
			}
		}
		if (tempLocation == null) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				tempLocation = p.getLocation();
				break;
			}
		}
		
		if (tempLocation == null)
			return null;
		tempLocation = tempLocation.add(0, 512, 0);
		
		AbstractHorse horse = world.spawn(tempLocation, type);
		if (horse == null)
			return null;
		HorseData res = HorseData.fromHorse(horse);
		horse.remove();
		return res;
	}
}
