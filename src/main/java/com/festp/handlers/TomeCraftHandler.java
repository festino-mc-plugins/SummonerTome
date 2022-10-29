package com.festp.handlers;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import com.festp.CraftManager;
import com.festp.Main;
import com.festp.components.BoatComponent;
import com.festp.components.BoatData;
import com.festp.components.CustomHorseComponent;
import com.festp.components.HorseComponent;
import com.festp.components.HorseFormat;
import com.festp.components.ITomeComponent;
import com.festp.components.MinecartComponent;
import com.festp.tome.SummonerTome;
import com.festp.tome.TomeType;
import com.festp.utils.Utils;
import com.festp.utils.UtilsRandom;

public class TomeCraftHandler implements Listener
{
	Main plugin;
	CraftManager craftManager;
	
	public TomeCraftHandler(Main plugin, CraftManager craftManager)
	{
		this.plugin = plugin;
		this.craftManager = craftManager;
	}
	
	public void addTomeCrafts()
	{
    	NamespacedKey key_minecart = new NamespacedKey(plugin, "minecart_tome");
    	NamespacedKey key_boat = new NamespacedKey(plugin, "boat_tome");
    	NamespacedKey key_horse = new NamespacedKey(plugin, "horse_tome");
		
    	ItemStack minecartBook = TomeItemHandler.getNewTome(EnumSet.of(TomeType.MINECART));
    	ShapelessRecipe minecartRecipe = new ShapelessRecipe(key_minecart, minecartBook);
    	minecartRecipe.addIngredient(3, Material.MINECART);
    	minecartRecipe.addIngredient(3, Material.EXPERIENCE_BOTTLE);
    	minecartRecipe.addIngredient(1, Material.BOOK);
    	craftManager.addRecipe(key_minecart, minecartRecipe);
		
    	// all tomes with boats can be customized by all the 6 boat types
    	ItemStack boatBook = TomeItemHandler.getNewTome(EnumSet.of(TomeType.BOAT));
    	ShapelessRecipe boatRecipe = new ShapelessRecipe(key_boat, boatBook);
    	RecipeChoice.MaterialChoice boatChoice = new RecipeChoice.MaterialChoice(BoatData.getSupportedBoats());
    	boatRecipe.addIngredient(boatChoice);
    	boatRecipe.addIngredient(boatChoice);
    	boatRecipe.addIngredient(boatChoice);
    	boatRecipe.addIngredient(3, Material.EXPERIENCE_BOTTLE);
    	boatRecipe.addIngredient(1, Material.BOOK);
    	craftManager.addRecipe(key_boat, boatRecipe);
    	
    	// unwearable armor, untakeable saddle
    	ItemStack horseBook = TomeItemHandler.getNewTome(EnumSet.of(TomeType.HORSE));
    	ShapelessRecipe horseRecipe = new ShapelessRecipe(key_horse, horseBook);
    	horseRecipe.addIngredient(1, Material.SADDLE);
    	horseRecipe.addIngredient(1, Material.LEATHER);
    	horseRecipe.addIngredient(1, Material.APPLE);
    	horseRecipe.addIngredient(3, Material.EXPERIENCE_BOTTLE);
    	horseRecipe.addIngredient(1, Material.BOOK);
    	craftManager.addRecipe(key_horse, horseRecipe);
    	
    	getCustomHorseRecipe(horseBook);
    	getUnitedRecipe(new ItemStack[] { minecartBook, boatBook });
    	// TODO create personal recipes (when player gets or loses tome)
	}
	
	private void getCustomHorseRecipe(ItemStack horseBook)
	{
    	NamespacedKey key_customHorse = new NamespacedKey(plugin, "make_custom_horse_tome");
    	RecipeChoice.ExactChoice horseTomeChoice = new RecipeChoice.ExactChoice(horseBook);
    	
    	// in craft events because of Horse tome
    	ItemStack customHorseBook = TomeItemHandler.getNewTome(EnumSet.of(TomeType.CUSTOM_HORSE));
    	ShapelessRecipe customHorseRecipe = new ShapelessRecipe(key_customHorse, customHorseBook);
    	customHorseRecipe.addIngredient(1, Material.NAME_TAG);
    	customHorseRecipe.addIngredient(1, Material.GOLDEN_APPLE);
    	customHorseRecipe.addIngredient(1, Material.EXPERIENCE_BOTTLE);
    	customHorseRecipe.addIngredient(horseTomeChoice);
    	craftManager.addRecipe(key_customHorse, customHorseRecipe);
	}
	private void getUnitedRecipe(ItemStack[] ingredientTomes)
	{
    	NamespacedKey key_combine = new NamespacedKey(plugin, "make_combined_tome");
    	RecipeChoice.ExactChoice tome1Choice = new RecipeChoice.ExactChoice(ingredientTomes[0]);
    	RecipeChoice.ExactChoice tome2Choice = new RecipeChoice.ExactChoice(ingredientTomes[1]);
    	
    	ItemStack combinedBook = TomeItemHandler.getNewTome(EnumSet.of(TomeType.MINECART, TomeType.BOAT));
    	ShapelessRecipe combineRecipe = new ShapelessRecipe(key_combine, combinedBook);
    	combineRecipe.addIngredient(tome1Choice);
    	combineRecipe.addIngredient(1, Material.EXPERIENCE_BOTTLE);
    	combineRecipe.addIngredient(tome2Choice);
    	craftManager.addRecipe(key_combine, combineRecipe);
	}

	/** Sets horse name, manage components */
	@EventHandler
	public void onPrepareCraft(PrepareItemCraftEvent event)
	{
		// TODO identify the craft(event.getRecipe() == recipe?) and original tomes, fire inner event
		SummonerTome newType = SummonerTome.getTome(event.getInventory().getResult());
		if (newType == null) return;

		ItemStack[] matrix = event.getInventory().getMatrix();
		// upgrade horse to custom horse:   any horse tome + NAME from nametag
		if (newType.hasComponent(CustomHorseComponent.class))
		{
			String customName = null;
			boolean correct = true;
			ItemStack oldTome = null;
			SummonerTome oldType = null;
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
			
			if (correct && oldType != null) {
				CustomHorseComponent horseComp = new CustomHorseComponent();
				horseComp.setHorseData(HorseFormat.generate());
				oldType.replace(HorseComponent.class, horseComp);
				ItemStack newTome = new ItemStack(oldTome);
				newTome = TomeItemHandler.applyTome(newTome, oldType);
				if (customName != null) {
					ItemMeta meta = newTome.getItemMeta();
					meta.setDisplayName(customName);
					newTome.setItemMeta(meta);
				}
		    	event.getInventory().setResult(newTome);
			}
			else {
				event.getInventory().setResult(null);
			}
	    	return;
		}
		
		// combined tome recipe result, TODO rework (use event.getRecipe().equals(...))
		if (newType.hasComponent(MinecartComponent.class)
				&& newType.hasComponent(BoatComponent.class))
		{
			boolean correct = true;
			List<ITomeComponent> tomes = new ArrayList<>();
			for (int i = 0; i < matrix.length; i++)
			{
				if (matrix[i] == null)
					continue;
				
				if (matrix[i].getType() == Material.ENCHANTED_BOOK)
				{
					SummonerTome oldType = SummonerTome.getTome(matrix[i]);
					if (oldType.getComponents().length != 1) {
						correct = false;
						break;
					}
					
					for (ITomeComponent compNew : oldType.getComponents())
						for (ITomeComponent compExisting : tomes)
							if (!isCompatible(compExisting, compNew))
							{
								correct = false;
								break;
							}
					if (!correct)
						break;
					
					for (ITomeComponent compNew : oldType.getComponents())
						tomes.add(compNew);
				}
			}
			
			if (correct) {
				SummonerTome combinedTome = new SummonerTome(tomes.toArray(new ITomeComponent[0]));
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
	
	// TODO scalable system (may be ITomeComponent#isCompatible(ITomeComponent)) 
	public boolean isCompatible(ITomeComponent comp1, ITomeComponent comp2)
	{
		if (comp1.getClass() == comp2.getClass())
			return false;
		boolean horseComp1 = comp1 instanceof HorseComponent || comp1 instanceof CustomHorseComponent;
		boolean horseComp2 = comp2 instanceof HorseComponent || comp2 instanceof CustomHorseComponent;
		return !(horseComp1 && horseComp2);
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
