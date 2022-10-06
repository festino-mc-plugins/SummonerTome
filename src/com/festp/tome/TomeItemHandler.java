package com.festp.tome;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import com.festp.CraftManager;
import com.festp.Main;

public class TomeItemHandler implements Listener
{
	public enum TomeType { MINECART, BOAT, HORSE, CUSTOM_HORSE, ALL, CUSTOM_ALL };
	private static final int REPAIR_COST = 1000;
	public static final String TOME_NBT_KEY = "summonertome";
	
	public static final String EN_LORE_MINECART = "Summons minecart";
	public static final String EN_LORE_BOAT = "Summons boat";
	public static final String EN_LORE_HORSE = "Summons horse";
	public static final String EN_LORE_CUSTOM_HORSE = "Summons custom horse";
	public static final String EN_LORE_ALL = "Summons minecart, boat or horse";
	public static final String EN_LORE_CUSTOM_ALL =  "Summons minecart, boat or custom horse";
	public static final String EN_NAME_MINECART = "Minecart tome";
	public static final String EN_NAME_BOAT = "Boat tome";
	public static final String EN_NAME_HORSE = "Horse tome";
	public static final String EN_NAME_CUSTOM_HORSE = "Advanced horse tome";
	public static final String EN_NAME_ALL = "United tome";
	public static final String EN_NAME_CUSTOM_ALL =  "Advanced united tome";
	
	public static ItemStack getTome(TomeType type) {
		if (type == null)
			return null;
    	ItemStack tome = new ItemStack(Material.ENCHANTED_BOOK);
    	Repairable rmeta = (Repairable) tome.getItemMeta();
    	rmeta.setRepairCost(REPAIR_COST);
    	tome.setItemMeta((ItemMeta) rmeta);
    	ItemMeta meta = tome.getItemMeta();
    	switch (type) {
    	case MINECART:
        	meta.setDisplayName(TomeItemHandler.EN_NAME_MINECART);
        	meta.setLore(Arrays.asList(TomeItemHandler.EN_LORE_MINECART)); break;
    	case BOAT:
        	meta.setDisplayName(TomeItemHandler.EN_NAME_BOAT);
        	meta.setLore(Arrays.asList(TomeItemHandler.EN_LORE_BOAT)); break;
    	case HORSE:
    		meta.setDisplayName(TomeItemHandler.EN_NAME_HORSE);
        	meta.setLore(Arrays.asList(TomeItemHandler.EN_LORE_HORSE)); break;
    	case CUSTOM_HORSE:
    		meta.setDisplayName(TomeItemHandler.EN_NAME_CUSTOM_HORSE);
        	meta.setLore(Arrays.asList(TomeItemHandler.EN_LORE_CUSTOM_HORSE)); break;
    	case ALL:
    		meta.setDisplayName(TomeItemHandler.EN_NAME_ALL);
        	meta.setLore(Arrays.asList(TomeItemHandler.EN_LORE_ALL)); break;
    	case CUSTOM_ALL:
    		meta.setDisplayName(TomeItemHandler.EN_NAME_CUSTOM_ALL);
        	meta.setLore(Arrays.asList(TomeItemHandler.EN_LORE_CUSTOM_ALL)); break;
    	}
    	tome.setItemMeta(meta);
    	tome = TomeFormatter.setType(tome, type);
    	return tome;
	}

	public static void addTomeCrafts(Main plugin, CraftManager craftManager)
	{
    	NamespacedKey key_minecart = new NamespacedKey(plugin, "minecart_tome");
    	NamespacedKey key_boat = new NamespacedKey(plugin, "boat_tome");
    	NamespacedKey key_horse = new NamespacedKey(plugin, "horse_tome");
    	NamespacedKey key_custom_horse = new NamespacedKey(plugin, "custom_horse_tome"); // for both 'custom horse' and 'custom all'
    	NamespacedKey key_all = new NamespacedKey(plugin, "all_tome"); // for both 'all' and 'custom all'
    	//NamespacedKey key_custom_all_h = new NamespacedKey(plugin, "custom_all_tome_from_horse");
    	//NamespacedKey key_custom_all_a = new NamespacedKey(plugin, "custom_all_tome_from_all");
		
		// minecart tome - book, 4 xp bottle and 4 minecarts
    	ItemStack minecart_book = getTome(TomeType.MINECART);
    	ShapelessRecipe minecart_tome = new ShapelessRecipe(key_minecart, minecart_book);
    	minecart_tome.addIngredient(1, Material.BOOK);
    	minecart_tome.addIngredient(4, Material.EXPERIENCE_BOTTLE);
    	minecart_tome.addIngredient(4, Material.MINECART);
    	craftManager.addRecipe(key_minecart, minecart_tome);
		
		// boat tome - book, 2 xp bottle and 6 colors boats
    	// all tomes with boats can be customized by all the 6 boat types
    	ItemStack boat_book = getTome(TomeType.BOAT);
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
    	ItemStack horse_book = getTome(TomeType.HORSE);
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
    	ItemStack custom_horse_book = getTome(TomeType.CUSTOM_HORSE);
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
    	ItemStack all_book = getTome(TomeType.ALL);
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
	public void onPrepareCraft(PrepareItemCraftEvent event) {
		TomeType type = TomeFormatter.getTomeType(event.getInventory().getResult());
		if(type == null) return;

		// custom horse/custom all tome:   horse/all tome + NAME from nametag + jump, speed and instheal potions
		if(type == TomeType.CUSTOM_HORSE) { //custom_horse or custom_all from all
			ItemStack[] matrix = event.getInventory().getMatrix();
			
			String customName = null;
			boolean correct = true, haveSpeed = false, haveJump = false, haveHeal = false;
			TomeType newType = null;
			ItemStack oldTome = null;
			for(int i = 0; i < matrix.length; i++) {
				if(!correct) break;
				if(matrix[i].getType() == Material.NAME_TAG) {
					if(matrix[i].getItemMeta().hasDisplayName()) {
						customName = matrix[i].getItemMeta().getDisplayName();
					}
				}
				else if(matrix[i].getType() == Material.POTION) {
					PotionMeta potion = (PotionMeta) matrix[i].getItemMeta();
					List<PotionEffect> effectList;
					if(potion.hasCustomEffects())
						effectList = potion.getCustomEffects();
					else
						effectList = new ArrayList<>();
					PotionData pd = potion.getBasePotionData();
					PotionEffect pet = pd.getType().getEffectType().createEffect(1, 1);
					effectList.add(pet);
					for (PotionEffect pe : effectList) {
						if (pe.getType() == PotionEffectType.SPEED)
							if (haveSpeed) {
								correct = false;
								break;
							}
							else {
								haveSpeed = true;
							}
						else if (pe.getType() == PotionEffectType.JUMP)
							if (haveJump) {
								correct = false;
								break;
							}
							else {
								haveJump = true;
							}
						else if (pe.getType() == PotionEffectType.HEAL)
							if (haveHeal) {
								correct = false;
								break;
							}
							else {
								haveHeal = true;
							}
					}
				}
				else if (matrix[i].getType() == Material.ENCHANTED_BOOK) {
					if (newType != null) {
						correct = false;
						break;
					}
					oldTome = matrix[i];
					TomeType oldType = TomeFormatter.getTomeType(matrix[i]);
					if (oldType == TomeType.HORSE) {
						newType = TomeType.CUSTOM_HORSE;
					}
					else if (oldType == TomeType.ALL) {
						newType = TomeType.CUSTOM_ALL;
					}
					else {
						correct = false;
						break;
					}
				}
			}
			
			if(correct && newType != null && haveHeal && haveJump && haveSpeed) {
				if(newType == TomeType.CUSTOM_ALL) {
					TreeSpecies wood_type = TomeFormatter.getBoatType(oldTome);
			    	ItemStack custom_all2_book = TomeFormatter.setTome(oldTome, 'A', "o");
			    	custom_all2_book = TomeFormatter.setBoatType(custom_all2_book, wood_type);
			    	ItemMeta custom_all2_meta = custom_all2_book.getItemMeta();
			    	custom_all2_meta.setDisplayName(EN_NAME_CUSTOM_ALL);
			    	custom_all2_meta.setLore(Arrays.asList(EN_LORE_CUSTOM_ALL));
			    	custom_all2_book.setItemMeta(custom_all2_meta);
			    	event.getInventory().setResult(custom_all2_book);
				}
				if(customName != null) {
					ItemStack tome = event.getInventory().getResult();
					tome.getItemMeta().setDisplayName(customName);
					event.getInventory().setResult(tome);
				}
		    	return;
			}
			else
				event.getInventory().setResult(null);
		}
		
		// all/custom all:   all three tomes
		if (type == TomeType.ALL || type == TomeType.CUSTOM_ALL) {
			ItemStack[] matrix = event.getInventory().getMatrix();
			
			boolean correct = true, haveMc = false, haveBoat = false; int haveHorse = 0;
			TomeType newType = null;
			ItemStack old_boat = null;
			ItemStack old_custom_horse = null;
			for (int i = 0; i < matrix.length; i++) {
				if (!correct) break;
				else if (matrix[i].getType() == Material.ENCHANTED_BOOK) {
					TomeType old_type = TomeFormatter.getTomeType(matrix[i]);
					if (old_type == TomeType.MINECART) {
						if (haveMc) {
							correct = false;
							break;
						}
						haveMc = true;
					}
					else if (old_type == TomeType.BOAT) {
						if (haveBoat) {
							correct = false;
							break;
						}
						haveBoat = true;
						old_boat = matrix[i];
					}
					else if (old_type == TomeType.HORSE) {
						if (haveHorse > 0) {
							correct = false;
							break;
						}
						haveHorse = 1;
						newType = TomeType.ALL;
					}
					else if (old_type == TomeType.CUSTOM_HORSE) {
						if (haveHorse > 0) {
							correct = false;
							break;
						}
						haveHorse = 2;
						old_custom_horse = matrix[i];
						newType = TomeType.CUSTOM_ALL;
					}
					else {
						correct = false;
						break;
					}
				}
			}
			
			if (correct && newType != null && haveMc && haveBoat && haveHorse > 0) {
				if (newType == TomeType.ALL) {
					TreeSpecies woodType = TomeFormatter.getBoatType(old_boat);
			    	ItemStack all_book = event.getInventory().getResult();
			    	all_book = TomeFormatter.setBoatType(all_book, woodType);
			    	event.getInventory().setResult(all_book);
				}
				if (newType == TomeType.CUSTOM_ALL) {
					TreeSpecies woodType = TomeFormatter.getBoatType(old_boat);
			    	ItemStack custom_all_book = new ItemStack(Material.ENCHANTED_BOOK);
			    	custom_all_book = TomeFormatter.setTome(event.getInventory().getResult(), 'A', "o");
			    	custom_all_book = TomeFormatter.setBoatType(custom_all_book, woodType);
			    	custom_all_book = TomeFormatter.setHorseData(custom_all_book, TomeFormatter.getHorseData(old_custom_horse));
			    	ItemMeta custom_all_meta = custom_all_book.getItemMeta();
			    	if (old_custom_horse.getItemMeta().hasDisplayName() && !EN_NAME_CUSTOM_HORSE.contains(old_custom_horse.getItemMeta().getDisplayName()))
				    	custom_all_meta.setDisplayName(old_custom_horse.getItemMeta().getDisplayName());
			    	else
			    		custom_all_meta.setDisplayName(EN_NAME_CUSTOM_ALL);
			    	custom_all_meta.setLore(Arrays.asList(EN_LORE_CUSTOM_ALL));
			    	custom_all_book.setItemMeta(custom_all_meta);
			    	event.getInventory().setResult(custom_all_book);
				}
		    	return;
			}
			else
				event.getInventory().setResult(null);
		}
	}

	/** Sets boat type */
	@EventHandler
	public void onCraft(CraftItemEvent event) {
		ItemStack curResult = event.getInventory().getResult();
		TomeType type = TomeFormatter.getTomeType(curResult);
		if (type == null) return;
		
		if (type == TomeType.BOAT) {
			curResult = TomeFormatter.setBoat(curResult, event.getInventory().getMatrix()[4]); //central cell
	    	event.getInventory().setResult(curResult);
		}
	}
}
