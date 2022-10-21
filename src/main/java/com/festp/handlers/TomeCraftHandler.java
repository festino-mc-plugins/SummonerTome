package com.festp.handlers;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.TreeSpecies;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import com.festp.CraftManager;
import com.festp.Main;
import com.festp.components.BoatComponent;
import com.festp.components.CustomHorseComponent;
import com.festp.components.HorseComponent;
import com.festp.components.HorseFormat;
import com.festp.components.ITomeComponent;
import com.festp.components.MinecartComponent;
import com.festp.tome.SummonerTome;
import com.festp.tome.TomeType;

public class TomeCraftHandler implements Listener
{
	public static void addTomeCrafts(Main plugin, CraftManager craftManager)
	{
    	NamespacedKey key_minecart = new NamespacedKey(plugin, "minecart_tome");
    	NamespacedKey key_boat = new NamespacedKey(plugin, "boat_tome");
    	NamespacedKey key_horse = new NamespacedKey(plugin, "horse_tome");
    	NamespacedKey key_custom_horse = new NamespacedKey(plugin, "make_custom_horse_tome"); // for both 'custom horse' and 'custom all'
    	NamespacedKey key_all = new NamespacedKey(plugin, "make_all_tome"); // for both 'all' and 'custom all'
		
		// minecart tome - book, 4 xp bottle and 4 minecarts
    	ItemStack minecart_book = TomeItemHandler.getNewTome(EnumSet.of(TomeType.MINECART));
    	ShapelessRecipe minecart_tome = new ShapelessRecipe(key_minecart, minecart_book);
    	minecart_tome.addIngredient(1, Material.BOOK);
    	minecart_tome.addIngredient(4, Material.EXPERIENCE_BOTTLE);
    	minecart_tome.addIngredient(4, Material.MINECART);
    	craftManager.addRecipe(key_minecart, minecart_tome);
		
		// boat tome - book, 2 xp bottle and 6 colors boats
    	// all tomes with boats can be customized by all the 6 boat types
    	ItemStack boat_book = TomeItemHandler.getNewTome(EnumSet.of(TomeType.BOAT));
    	ShapelessRecipe boat_tome = new ShapelessRecipe(key_boat, boat_book);
    	boat_tome.addIngredient(1, Material.BOOK);
    	boat_tome.addIngredient(2, Material.EXPERIENCE_BOTTLE);
    	boat_tome.addIngredient(1, Material.ACACIA_BOAT);
    	boat_tome.addIngredient(1, Material.BIRCH_BOAT);
    	boat_tome.addIngredient(1, Material.DARK_OAK_BOAT);
    	boat_tome.addIngredient(1, Material.JUNGLE_BOAT);
    	boat_tome.addIngredient(1, Material.OAK_BOAT);
    	boat_tome.addIngredient(1, Material.SPRUCE_BOAT);
    	craftManager.addRecipe(key_boat, boat_tome);
    	
		// horse tome - book, 2 xp bottles, 4 saddles, 2 (leads?)
    	// unwearable armor, untakeable saddle
    	ItemStack horse_book = TomeItemHandler.getNewTome(EnumSet.of(TomeType.HORSE));
    	ShapelessRecipe horse_tome = new ShapelessRecipe(key_horse, horse_book);
    	horse_tome.addIngredient(1, Material.BOOK);
    	horse_tome.addIngredient(2, Material.EXPERIENCE_BOTTLE);
    	horse_tome.addIngredient(2, Material.LEAD);
    	horse_tome.addIngredient(2, Material.SADDLE);
    	horse_tome.addIngredient(1, Material.APPLE);
    	horse_tome.addIngredient(1, Material.GOLDEN_APPLE);
    	craftManager.addRecipe(key_horse, horse_tome);
    	
    	RecipeChoice.ExactChoice minecart_choice = new RecipeChoice.ExactChoice(minecart_book);
    	RecipeChoice.ExactChoice boat_choice = new RecipeChoice.ExactChoice(boat_book);
    	RecipeChoice.ExactChoice horse_choice = new RecipeChoice.ExactChoice(horse_book);
    	
		// custom horse tome - horse tome, 4 xp bottles, jump potion, speed potion, instheal potion, label
    	// in craft events because of Horse tome and potions
    	ItemStack custom_horse_book = TomeItemHandler.getNewTome(EnumSet.of(TomeType.CUSTOM_HORSE));
    	ShapelessRecipe custom_horse_tome = new ShapelessRecipe(key_custom_horse, custom_horse_book);
    	custom_horse_tome.addIngredient(4, Material.EXPERIENCE_BOTTLE);
    	custom_horse_tome.addIngredient(1, Material.NAME_TAG);
    	
    	ItemStack heal_potion = new ItemStack(Material.POTION);
    	PotionMeta p_meta = (PotionMeta)heal_potion.getItemMeta();
    	p_meta.setBasePotionData(new PotionData(PotionType.INSTANT_HEAL));
    	heal_potion.setItemMeta(p_meta);
    	ItemStack speed_potion = new ItemStack(Material.POTION);
    	p_meta = (PotionMeta)speed_potion.getItemMeta();
    	p_meta.setBasePotionData(new PotionData(PotionType.SPEED));
    	speed_potion.setItemMeta(p_meta);
    	ItemStack jump_potion = new ItemStack(Material.POTION);
    	p_meta = (PotionMeta)jump_potion.getItemMeta();
    	p_meta.setBasePotionData(new PotionData(PotionType.JUMP));
    	jump_potion.setItemMeta(p_meta);
    	RecipeChoice.ExactChoice heal_choice = new RecipeChoice.ExactChoice(heal_potion);
    	RecipeChoice.ExactChoice speed_choice = new RecipeChoice.ExactChoice(speed_potion);
    	RecipeChoice.ExactChoice jump_choice = new RecipeChoice.ExactChoice(jump_potion);
    	custom_horse_tome.addIngredient(heal_choice);
    	custom_horse_tome.addIngredient(speed_choice);
    	custom_horse_tome.addIngredient(jump_choice);
    	//custom_horse_tome.addIngredient(1, Material.ENCHANTED_BOOK);
    	custom_horse_tome.addIngredient(horse_choice);
    	craftManager.addRecipe(key_custom_horse, custom_horse_tome);
    	
    	//RecipeChoice.ExactChoice custom_horse_choice = new RecipeChoice.ExactChoice(custom_horse_book);
		
		// united tome - horse, minecart, boat tomes, slime block, 5 xp bottles
    	ItemStack all_book = TomeItemHandler.getNewTome(EnumSet.of(TomeType.MINECART, TomeType.BOAT, TomeType.HORSE));
    	ShapelessRecipe all_tome = new ShapelessRecipe(key_all, all_book);
    	//all_tome.addIngredient(3, Material.ENCHANTED_BOOK);
    	all_tome.addIngredient(minecart_choice);
    	all_tome.addIngredient(boat_choice);
    	all_tome.addIngredient(horse_choice);
    	all_tome.addIngredient(5, Material.EXPERIENCE_BOTTLE);
    	all_tome.addIngredient(1, Material.SLIME_BLOCK);
    	craftManager.addRecipe(key_all, all_tome);
    	
    	// united custom tome - slime block, custom horse, minecart, boat tomes, 5 xp bottles
    	// from custom horse, boat and minecart tomes
    	//craftManager.addRecipe(key_custom_all_h, minecart_tome);
    	// from united tome
    	//craftManager.addRecipe(key_custom_all_a, minecart_tome);
	}

	/** Sets horse name and lets spigot use custom potions */
	@EventHandler
	public void onPrepareCraft(PrepareItemCraftEvent event)
	{
		// TODO identify the craft(event.getRecipe() == recipe?) and original tomes, fire inner event
		SummonerTome newType = SummonerTome.getTome(event.getInventory().getResult());
		if (newType == null) return;

		ItemStack[] matrix = event.getInventory().getMatrix();
		// upgrade horse to custom horse:   horse/all tome + NAME from nametag + jump, speed and instheal potions
		if (newType.hasComponent(CustomHorseComponent.class))
		{
			String customName = null;
			boolean correct = true;
			boolean hasSpeed = false, hasJump = false, hasHeal = false;
			ItemStack oldTome = null;
			SummonerTome oldType = null;
			int bookCount = 1;
			for (int i = 0; i < matrix.length; i++)
			{
				if (!correct) break;
				if (matrix[i].getType() == Material.NAME_TAG) {
					if (matrix[i].getItemMeta().hasDisplayName()) {
						customName = matrix[i].getItemMeta().getDisplayName();
					}
				}
				else if (matrix[i].getType() == Material.POTION) {
					PotionMeta potion = (PotionMeta) matrix[i].getItemMeta();
					List<PotionEffect> effectList;
					if (potion.hasCustomEffects())
						effectList = potion.getCustomEffects();
					else
						effectList = new ArrayList<>();
					PotionData pd = potion.getBasePotionData();
					PotionEffect pet = pd.getType().getEffectType().createEffect(1, 1);
					effectList.add(pet);
					for (PotionEffect pe : effectList) {
						if (pe.getType() == PotionEffectType.SPEED)
							if (hasSpeed) {
								correct = false;
								break;
							}
							else {
								hasSpeed = true;
							}
						else if (pe.getType() == PotionEffectType.JUMP)
							if (hasJump) {
								correct = false;
								break;
							}
							else {
								hasJump = true;
							}
						else if (pe.getType() == PotionEffectType.HEAL)
							if (hasHeal) {
								correct = false;
								break;
							}
							else {
								hasHeal = true;
							}
					}
				}
				else if (matrix[i].getType() == Material.ENCHANTED_BOOK) {
					if (bookCount > 0) {
						correct = false;
						break;
					}
					bookCount++;
					oldTome = matrix[i];
					oldType = SummonerTome.getTome(matrix[i]);
					boolean hasHorse = oldType.hasComponent(HorseComponent.class);
					boolean hasCustomHorse = oldType.hasComponent(CustomHorseComponent.class);
					if (!hasHorse || hasCustomHorse) {
						correct = false;
						break;
					}
				}
			}
			
			if (correct && oldType != null && hasHeal && hasJump && hasSpeed) {
				CustomHorseComponent horseComp = new CustomHorseComponent();
				horseComp.setHorseData(HorseFormat.generate());
				oldType.replace(HorseComponent.class, horseComp);
				ItemStack tome = TomeItemHandler.applyTome(oldTome, oldType);
				if (customName != null) {
					ItemMeta meta = tome.getItemMeta();
					meta.setDisplayName(customName);
					tome.setItemMeta(meta);
				}
		    	event.getInventory().setResult(tome);
			}
			else {
				event.getInventory().setResult(null);
			}
	    	return;
		}
		
		// combine minecart + boat + (any) horse
		if (newType.hasComponent(MinecartComponent.class)
				&& newType.hasComponent(BoatComponent.class)
				&& newType.hasComponent(HorseComponent.class))
		{
			boolean correct = true;
			ITomeComponent mcTome = null, boatTome = null, horseTome = null;
			for (int i = 0; i < matrix.length; i++)
			{
				if (matrix[i].getType() == Material.ENCHANTED_BOOK)
				{
					SummonerTome oldType = SummonerTome.getTome(matrix[i]);
					if (oldType.getComponents().length != 1) {
						correct = false;
						break;
					}
					
					ITomeComponent comp = oldType.getComponents()[0];
					if (comp instanceof MinecartComponent) {
						if (mcTome != null) {
							correct = false;
							break;
						}
						mcTome = comp;
					}
					else if (comp instanceof BoatComponent) {
						if (boatTome != null) {
							correct = false;
							break;
						}
						boatTome = comp;
					}
					else if (comp instanceof HorseComponent || comp instanceof CustomHorseComponent) {
						if (horseTome != null) {
							correct = false;
							break;
						}
						horseTome = comp;
					}
					else {
						correct = false;
						break;
					}
				}
			}
			
			if (correct && mcTome != null && boatTome != null && horseTome != null) {
				SummonerTome combinedTome = new SummonerTome(new ITomeComponent[] { mcTome, boatTome, horseTome });
		    	ItemStack tome = new ItemStack(Material.ENCHANTED_BOOK);
		    	tome = TomeItemHandler.applyTome(tome, combinedTome);
		    	event.getInventory().setResult(tome);
			}
			else {
				event.getInventory().setResult(null);
			}
	    	return;
		}
	}

	/** Sets boat type */
	@EventHandler
	public void onCraft(CraftItemEvent event)
	{
		ItemStack curResult = event.getInventory().getResult();
		SummonerTome tome = SummonerTome.getTome(curResult);
		if (tome == null)
			return;
		if (tome.getComponents().length != 1)
			return;
		
		BoatComponent boatComp = tome.getComponent(BoatComponent.class);
		if (boatComp != null) {
			ItemStack centralCell = event.getInventory().getMatrix()[4];
			TreeSpecies boatType = getBoatType(centralCell);
			boatComp.setBoat(boatType);
			curResult = tome.setTome(curResult);
	    	event.getInventory().setResult(curResult);
		}
	}
	
	private static TreeSpecies getBoatType(ItemStack boat) {
		Material woodType = boat.getType();
		if(woodType == Material.ACACIA_BOAT)
			return TreeSpecies.ACACIA;
		else if(woodType == Material.BIRCH_BOAT)
			return TreeSpecies.BIRCH;
		else if(woodType == Material.DARK_OAK_BOAT)
			return TreeSpecies.DARK_OAK;
		else if(woodType == Material.JUNGLE_BOAT)
			return TreeSpecies.JUNGLE;
		else if(woodType == Material.OAK_BOAT)
			return TreeSpecies.GENERIC;
		else if(woodType == Material.SPRUCE_BOAT)
			return TreeSpecies.REDWOOD;
		return TreeSpecies.GENERIC;
	}
}
