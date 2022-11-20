package com.festp.crafting;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import com.festp.Main;
import com.festp.Permissions;
import com.festp.components.BoatComponent;
import com.festp.components.BoatData;
import com.festp.components.CustomHorseComponent;
import com.festp.components.HorseComponent;
import com.festp.components.ITomeComponent;
import com.festp.components.MinecartComponent;
import com.festp.components.PigComponent;
import com.festp.components.StriderComponent;
import com.festp.config.Config;
import com.festp.tome.ComponentManager;
import com.festp.tome.SummonerTome;
import com.festp.utils.Utils;
import com.festp.utils.UtilsRandom;

public class TomeCraftHandler implements Listener
{
	private Main plugin;
	private CraftManager craftManager;
	private ComponentManager componentManager;
	
	private Recipe boatRecipe;
	private Recipe customHorseRecipe;
	private Recipe combineRecipe;
	private Config config;
	
	public TomeCraftHandler(Main plugin, Config config, CraftManager craftManager, ComponentManager componentManager)
	{
		this.plugin = plugin;
		this.config = config;
		this.craftManager = craftManager;
		this.componentManager = componentManager;
	}
	
	public void addTomeCrafts()
	{
    	NamespacedKey key_minecart = new NamespacedKey(plugin, "minecart_tome");
    	NamespacedKey key_boat = new NamespacedKey(plugin, "boat_tome");
    	NamespacedKey key_strider = new NamespacedKey(plugin, "strider_tome");
    	NamespacedKey key_pig = new NamespacedKey(plugin, "pig_tome");
    	NamespacedKey key_horse = new NamespacedKey(plugin, "horse_tome");
		
    	ItemStack minecartBook = TomeItemBuilder.getNewTome(componentManager.fromCode(MinecartComponent.CODE));
    	ShapelessRecipe minecartRecipe = new ShapelessRecipe(key_minecart, minecartBook);
    	minecartRecipe.addIngredient(3, Material.MINECART);
    	minecartRecipe.addIngredient(3, Material.EXPERIENCE_BOTTLE);
    	minecartRecipe.addIngredient(1, Material.BOOK);
    	craftManager.addRecipe(key_minecart, minecartRecipe);
		
    	ItemStack boatBook = TomeItemBuilder.getNewTome(componentManager.fromCode(BoatComponent.CODE));
    	ShapelessRecipe boatRecipe = new ShapelessRecipe(key_boat, boatBook);
    	RecipeChoice.MaterialChoice boatChoice = new RecipeChoice.MaterialChoice(BoatData.getSupportedBoats());
    	boatRecipe.addIngredient(boatChoice);
    	boatRecipe.addIngredient(boatChoice);
    	boatRecipe.addIngredient(boatChoice);
    	boatRecipe.addIngredient(3, Material.EXPERIENCE_BOTTLE);
    	boatRecipe.addIngredient(1, Material.BOOK);
    	craftManager.addRecipe(key_boat, boatRecipe);
    	this.boatRecipe = boatRecipe;
    	
    	ItemStack striderBook = TomeItemBuilder.getNewTome(componentManager.fromCode(StriderComponent.CODE));
    	ShapelessRecipe striderRecipe = new ShapelessRecipe(key_strider, striderBook);
    	striderRecipe.addIngredient(1, Material.SADDLE);
    	striderRecipe.addIngredient(1, Material.LAVA_BUCKET);
    	striderRecipe.addIngredient(1, Material.WARPED_FUNGUS);
    	striderRecipe.addIngredient(3, Material.EXPERIENCE_BOTTLE);
    	striderRecipe.addIngredient(1, Material.BOOK);
    	craftManager.addRecipe(key_strider, striderRecipe);
    	
    	ItemStack pigBook = TomeItemBuilder.getNewTome(componentManager.fromCode(PigComponent.CODE));
    	ShapelessRecipe pigRecipe = new ShapelessRecipe(key_pig, pigBook);
    	pigRecipe.addIngredient(1, Material.SADDLE);
    	pigRecipe.addIngredient(1, Material.PORKCHOP);
    	pigRecipe.addIngredient(1, Material.CARROT);
    	pigRecipe.addIngredient(3, Material.EXPERIENCE_BOTTLE);
    	pigRecipe.addIngredient(1, Material.BOOK);
    	craftManager.addRecipe(key_pig, pigRecipe);
    	
    	// unwearable armor, untakeable saddle
    	ItemStack horseBook = TomeItemBuilder.getNewTome(componentManager.fromCode(HorseComponent.CODE));
    	ShapelessRecipe horseRecipe = new ShapelessRecipe(key_horse, horseBook);
    	horseRecipe.addIngredient(1, Material.SADDLE);
    	horseRecipe.addIngredient(1, Material.LEATHER);
    	horseRecipe.addIngredient(1, Material.APPLE);
    	horseRecipe.addIngredient(3, Material.EXPERIENCE_BOTTLE);
    	horseRecipe.addIngredient(1, Material.BOOK);
    	craftManager.addRecipe(key_horse, horseRecipe);
    	
    	getCustomHorseRecipe(horseBook);
    	getUnitedRecipe(new ItemStack[] { minecartBook, boatBook });
    	// TODO create/remove personal recipes (when player gets or loses tome)
	}
	
	private void getCustomHorseRecipe(ItemStack horseBook)
	{
    	NamespacedKey key_customHorse = new NamespacedKey(plugin, "make_custom_horse_tome");
    	RecipeChoice.ExactChoice horseTomeChoice = new RecipeChoice.ExactChoice(horseBook);
    	
    	// in craft events because of Horse tome
    	ItemStack customHorseBook = TomeItemBuilder.getNewTome(componentManager.fromCode(CustomHorseComponent.CODE));
    	ShapelessRecipe customHorseRecipe = new ShapelessRecipe(key_customHorse, customHorseBook);
    	customHorseRecipe.addIngredient(1, Material.NAME_TAG);
    	customHorseRecipe.addIngredient(1, Material.GOLDEN_APPLE);
    	customHorseRecipe.addIngredient(1, Material.EXPERIENCE_BOTTLE);
    	customHorseRecipe.addIngredient(horseTomeChoice);
    	craftManager.addRecipe(key_customHorse, customHorseRecipe);
    	this.customHorseRecipe = Bukkit.getRecipe(key_customHorse);
	}
	private void getUnitedRecipe(ItemStack[] ingredientTomes)
	{
    	NamespacedKey key_combine = new NamespacedKey(plugin, "make_combined_tome");
    	RecipeChoice.ExactChoice tome1Choice = new RecipeChoice.ExactChoice(ingredientTomes[0]);
    	RecipeChoice.ExactChoice tome2Choice = new RecipeChoice.ExactChoice(ingredientTomes[1]);
    	
    	ItemStack combinedBook = TomeItemBuilder.getNewTome(new ITomeComponent[] {
    			componentManager.fromCode(MinecartComponent.CODE), componentManager.fromCode(BoatComponent.CODE) });
    	ShapelessRecipe combineRecipe = new ShapelessRecipe(key_combine, combinedBook);
    	combineRecipe.addIngredient(tome1Choice);
    	combineRecipe.addIngredient(1, Material.EXPERIENCE_BOTTLE);
    	combineRecipe.addIngredient(tome2Choice);
    	craftManager.addRecipe(key_combine, combineRecipe);
    	this.combineRecipe = Bukkit.getRecipe(key_combine);
	}

	/** Sets horse name, manage components */
	@EventHandler
	public void onPrepareCraft(PrepareItemCraftEvent event)
	{
		SummonerTome newType = SummonerTome.getTome(event.getInventory().getResult());
		if (newType == null) return;

		ItemStack res = getExactCraftResult(event.getRecipe(), event.getInventory());

		if (newType.getComponents().length == 1) {
			ITomeComponent component = newType.getComponents()[0];
			if (!componentManager.isCraftable(component.getCode()))
				res = null;
		}
		event.getInventory().setResult(res);
	}
	private ItemStack getExactCraftResult(Recipe recipe, CraftingInventory inventory)
	{
		ItemStack[] matrix = inventory.getMatrix();
		// upgrade horse to custom horse:   any horse tome + NAME from nametag
		// set the actual result of the custom horse tome recipe
		if (isEqual(recipe, customHorseRecipe))
		{
			if (!hasPermission(inventory.getViewers(), Permissions.CRAFT)) {
				return null;
			}
			
			String customName = null;
			boolean correct = true;
			ItemStack oldItem = null;
			SummonerTome oldTome = null;
			int bookCount = 0;
			for (int i = 0; i < matrix.length; i++)
			{
				if (!correct)
					break;
				if (matrix[i] == null)
					continue;
				
				if (matrix[i].getType() == Material.NAME_TAG) {
					if (matrix[i].getItemMeta().hasDisplayName()) {
						customName = matrix[i].getItemMeta().getDisplayName();
					}
				}
				else if (matrix[i].getType() == Material.ENCHANTED_BOOK) {
					if (bookCount > 0) {
						correct = false;
						break;
					}
					bookCount++;
					oldItem = matrix[i];
					oldTome = SummonerTome.getTome(matrix[i]);
					boolean hasHorse = oldTome.hasComponent(HorseComponent.class);
					boolean hasCustomHorse = oldTome.hasComponent(CustomHorseComponent.class);
					if (!hasHorse || hasCustomHorse) {
						correct = false;
						break;
					}
				}
			}
			
			if (correct && oldTome != null) {
				// TODO identify original tomes, fire inner event
				CustomHorseComponent horseComp = new CustomHorseComponent();
				oldTome.replace(HorseComponent.class, horseComp);
				ItemStack newItem = new ItemStack(oldItem);
				newItem = TomeItemBuilder.applyTome(newItem, oldTome);
				if (customName != null) {
					ItemMeta meta = newItem.getItemMeta();
					meta.setDisplayName(customName);
					newItem.setItemMeta(meta);
				}
				return newItem;
			}
			else {
				return null;
			}
		}
		
		// set the actual result of the combined tome recipe
		if (isEqual(recipe, combineRecipe))
		{
			if (!hasPermission(inventory.getViewers(), Permissions.CRAFT)) {
				return null;
			}
			
			boolean correct = true;
			List<ITomeComponent> resComponents = new ArrayList<>();
			for (int i = 0; i < matrix.length; i++)
			{
				if (matrix[i] == null)
					continue;
				
				if (matrix[i].getType() == Material.ENCHANTED_BOOK)
				{
					SummonerTome oldTome = SummonerTome.getTome(matrix[i]);
					if (oldTome == null || oldTome.getComponents().length < 1) {
						correct = false;
						break;
					}
					
					for (ITomeComponent compNew : oldTome.getComponents())
						for (ITomeComponent compExisting : resComponents)
							if (!isCompatible(compExisting, compNew))
							{
								correct = false;
								break;
							}
					if (!correct)
						break;
					
					for (ITomeComponent compNew : oldTome.getComponents())
						resComponents.add(compNew);
				}
			}
			
			correct = correct && isAllowedComponentNumber(resComponents.size());
			if (correct) {
				SummonerTome combinedTome = new SummonerTome(resComponents.toArray(new ITomeComponent[0]));
		    	ItemStack tomeItem = new ItemStack(Material.ENCHANTED_BOOK);
		    	tomeItem = TomeItemBuilder.applyTome(tomeItem, combinedTome);
		    	return tomeItem;
			}
			else {
		    	return null;
			}
		}
    	return inventory.getResult();
	}
	
	private boolean isAllowedComponentNumber(int number) {
		int maxNumber = config.get(Config.Key.MAX_COMPONENTS, 0);
		if (maxNumber == 0)
			return true;
		return number <= maxNumber;
	}

	private boolean hasPermission(List<HumanEntity> viewers, String permission) {
		if (viewers == null || permission == null)
			return false;
		for (HumanEntity viewer : viewers)
			if (!viewer.hasPermission(permission))
				return false;
		return true;
	}

	public boolean isCompatible(ITomeComponent comp1, ITomeComponent comp2)
	{
		return componentManager.isCompatible(comp1, comp2);
	}
	
	// TODO new comparing method; Bukkit creates a new recipe every time; no .getKey()?
	/** Compares ONLY the results */
	private static boolean isEqual(Recipe recipe1, Recipe recipe2) {
		//System.out.println(recipe1 + " " + recipe2 + " " + recipe1.equals(recipe2) + " " + (recipe1 == recipe2));
		return recipe1.getResult().equals(recipe2.getResult());
	}

	/** Sets boat type */
	@EventHandler
	public void onCraft(CraftItemEvent event)
	{
		if (!event.getRecipe().equals(boatRecipe))
			return;
		
		ItemStack curResult = event.getInventory().getResult();
		SummonerTome tome = SummonerTome.getTome(curResult);
		if (tome == null)
			return;
		if (tome.getComponents().length != 1)
			return;
		
		BoatComponent boatComp = tome.getComponent(BoatComponent.class);
		if (boatComp != null) {
			int count = 0;
			Material boatMaterial = Material.OAK_BOAT;
			// choose any of the boats with equal probability
			for (ItemStack item : event.getInventory().getMatrix()) {
				if (item == null)
					continue;
				
				if (Utils.contains(BoatData.getSupportedBoats(), item.getType())) {
					count++;
					if (UtilsRandom.getDouble() < 1.0 / count)
						boatMaterial = item.getType();
				}
			}
			boatComp.setBoatData(BoatData.fromBoatMaterial(boatMaterial));
			curResult = tome.setTome(curResult);
	    	event.getInventory().setResult(curResult);
		}
	}
}
